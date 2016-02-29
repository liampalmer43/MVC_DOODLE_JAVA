import java.util.Observable;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;    
import java.io.*;

public class Model extends Observable { 
    // the data in the model
    private Color current_color = Color.black;
    private int current_width = 4;
    private int stage = 0;
    private JFrame frame;
    private JPanel parent;
    private JScrollPane fixed_view;
    private JPanel full_view;
    private Animation animation;
    private static boolean full = true;
    private JFileChooser chooser;

    // Whether or not the animation is currently playing;
    private boolean playing = false;
    private static int WIDTH = 600;
    private static int HEIGHT = 350;

    ArrayList<Stroke> strokes = new ArrayList<Stroke>();
    
    public static class Stroke implements Serializable {
        public ArrayList<Pair> points;
        public Color color;
        public int width;
        public boolean complete;
        Stroke(ArrayList<Pair> new_points, Color new_color, int new_width) {
            points = new_points;
            color = new_color;
            width = new_width;
            complete = false;
        }
    }

    public static class Pair implements Serializable {
        private float x;
        private float y;
        private long time;
        Pair(float xc, float yc, long t) {
            x = xc;
            y = yc;
            time = t;
        }
        public float getX(int scale_width) {
            if (!full) {
                return x;
            }
            return x * (float)scale_width / WIDTH;
        }
        public float getY(int scale_height) {
            if (!full) {
                return y;
            }
            return y * (float)scale_height / HEIGHT;
        }
        public float X() {
            return x;
        }
        public float Y() {
            return y;
        }
        public long getTime() {
            return time;
        }
    }    
    
    Model() {
        chooser = new JFileChooser();
        FileNameExtensionFilter filter_text = new FileNameExtensionFilter("*.txt", "txt");
        chooser.addChoosableFileFilter(filter_text);
        FileNameExtensionFilter filter_binary = new FileNameExtensionFilter("*.bin", "bin");
        chooser.addChoosableFileFilter(filter_binary);

        setChanged();
    }

    public void addPoint(int x, int y, long time, int width, int height) {
        if (playing || strokes.size() == 0) {
            return;
        }
        strokes.get(strokes.size() - 1).points.add(new Pair(x * (float)WIDTH / width, y * (float)HEIGHT / height, time));
        
        setChanged();
        notifyObservers();
    }

    public void addStroke(int x, int y, long time, int width, int height) {
        if (playing) {
            return;
        }
        cleanPoints();

        strokes.add(new Stroke(new ArrayList<Pair>(), current_color, current_width));
        strokes.get(strokes.size() - 1).points.add(new Pair(x * (float)WIDTH / width, y * (float)HEIGHT / height, time));
        
        stage = (strokes.size() - 1) * 100;
        setChanged();
        notifyObservers();
    }

    public void finishStroke() {
        if (playing) {
            return;
        }
        strokes.get(strokes.size() - 1).complete = true;
        stage = strokes.size() * 100;
        setChanged();
        notifyObservers();
    }

    public void cleanPoints() {
        for (int i = 0; i < strokes.size(); ++i) {
            int start_stage = i * 100;
            int end_stage = start_stage + 100;
            if (stage < end_stage) {
                for (int j = strokes.size() - 1; j >= i; --j) {
                    strokes.remove(j);
                }  
                return;
            }
        }
    }

    public void setStage(int new_stage) {
        if (stage == new_stage) {
            return;
        }
        stage = new_stage;
        setChanged();
        notifyObservers();
    }

    public void play() {
        // If the animation is already playing, avoid starting another timer.
        if (playing) {
            return;
        }
        playing = true;
        animation.freeze();
        stage = 0;
        setChanged();
        notifyObservers();
        real_play();
    }
    
    public void real_play() {
        if (stage >= strokes.size() * 100) {
            playing = false;
            setChanged();
            notifyObservers();
            return;
        }
        int index = stage / 100;
        long start_time = strokes.get(index).points.get(0).time;
        long end_time = strokes.get(index).points.get(strokes.get(index).points.size() - 1).time;
        long duration = end_time - start_time;
        int interval = (int)duration / 100;
        final Timer timer = new Timer(interval, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (stage >= (index + 1) * 100) {
                    ((Timer)evt.getSource()).stop();
                    real_play();
                    return;
                }
                stage++;
                setChanged();
                notifyObservers();
            }
        });
        timer.start();
    }

    public void reverse() {
        if (playing) {
            return;
        }
        playing = true;
        animation.freeze();
        stage = 0;
        setChanged();
        notifyObservers();
        real_reverse();
        // If the animation is already playing, avoid starting another timer.
        if (playing) {
            return;
        }
        playing = true;
        animation.freeze();
        stage = strokes.size() * 100;
        setChanged();
        notifyObservers();
        real_reverse();
    }

    public void real_reverse() {
        if (stage <= 0) {
            playing = false;
            setChanged();
            notifyObservers();
            return;
        }
        int index = (stage / 100) - 1;
        long start_time = strokes.get(index).points.get(0).time;
        long end_time = strokes.get(index).points.get(strokes.get(index).points.size() - 1).time;
        long duration = end_time - start_time;
        int interval = (int)duration / 100;
        final Timer timer = new Timer(interval, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (stage <= index * 100) {
                    ((Timer)evt.getSource()).stop();
                    real_reverse();
                    return;
                }
                stage--;
                setChanged();
                notifyObservers();
            }
        });
        timer.start();
    }


    public void start() {
        stage = 0;
        setChanged();
        notifyObservers();
    }

    public void end() {
        stage = strokes.size() * 100;
        setChanged();
        notifyObservers();
    }

    public void newDoodle() {
        Object[] options = {"Yes", "No"};
        int options_index = JOptionPane.showOptionDialog(frame, "Proceed without saving?", "Warning: About to lose data!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
        if (options_index == 0) {
            strokes.clear();
            stage = 0;
            setChanged();
            notifyObservers();
        }
    }

    public float Float(String s) {
        if (s.startsWith("-")) { 
            s = s.substring(1);
            return -Float.valueOf(s);
        }
        return Float.valueOf(s);
    }

    public int Int(String s) {
        if (s.startsWith("-")) { 
            s = s.substring(1);
            return -Integer.parseInt(s);
        }
        return Integer.parseInt(s);
    }

    public void selectFile() {
        Object[] options = {"Yes", "No"};
        int options_index = JOptionPane.showOptionDialog(frame, "Proceed without saving?", "Warning: About to lose data!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
        if (options_index == 1) {
            return;
        }
        int result = chooser.showOpenDialog(frame);
        if (result == chooser.APPROVE_OPTION) {
            String extension = "";
            String path = chooser.getSelectedFile().getAbsolutePath();
            String fileName = chooser.getSelectedFile().getName();
            if (path.endsWith(".txt")) {
                extension = ".txt";
            }
            else if (path.endsWith(".bin")) {
                extension = ".bin";
            }
            else {
                return;
            }
            
            if (extension.equals(".txt")) {
                try {
                    FileReader fileReader = new FileReader(path);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    ArrayList<Stroke> stroke_import = new ArrayList<Stroke>();
                    try {
                        int size = Int(bufferedReader.readLine());
                        for (int i = 0; i < size; ++i) {
                            int width = Int(bufferedReader.readLine());
                            int blue = Int(bufferedReader.readLine());
                            int red = Int(bufferedReader.readLine());
                            int green = Int(bufferedReader.readLine());
                    
                            Stroke stroke = new Stroke(new ArrayList<Pair>(), new Color(red, green, blue), width);   
                            stroke.complete = true;

                            int number_of_points = Int(bufferedReader.readLine());
                            for(int j = 0; j < number_of_points; ++j) {
                                float x = Float(bufferedReader.readLine());
                                float y = Float(bufferedReader.readLine());
                                long time = Long.parseLong(bufferedReader.readLine());
                                stroke.points.add(new Pair(x, y, time));
                            }
                            stroke_import.add(stroke);
                        }
                    } finally {
                        bufferedReader.close();
                    }
                    strokes = stroke_import;
                    stage = strokes.size() * 100;
                    setChanged();
                    notifyObservers();
                } 
                catch(FileNotFoundException ex) {
                    System.out.println(ex);         
                } catch(IOException ex) {
                    System.out.println(ex);
                }
            }
            else if (extension.equals(".bin")) {
               try {
                    InputStream file = new FileInputStream(path);
                    InputStream buffer = new BufferedInputStream(file);
                    ObjectInput in = new ObjectInputStream(buffer);
                    ArrayList<Stroke> input_strokes = new ArrayList<Stroke>();

                    try {
                        Object first = in.readObject();
                        strokes = (ArrayList<Stroke>)first;
                        stage = strokes.size() * 100;
                        setChanged();
                        notifyObservers();
                    } catch(ClassNotFoundException ex) {
                        System.out.println(ex);
                    } finally {
                        in.close();
                    }
                } catch (IOException ex) {
                    System.out.println(ex);
                } 
            }
        }
    }

    public void saveFile() {
        int result = chooser.showSaveDialog(frame);
        String extension;
        if (result == chooser.APPROVE_OPTION) {
            String desc = chooser.getFileFilter().getDescription();
            if (desc.equals("*.txt")) {
                extension = ".txt";
            }
            else if (desc.equals("*.bin")) {
                extension = ".bin";
            }
            else {
                extension = ".txt";
            }
        }
        else {
            return;
        }

        String path = chooser.getSelectedFile().getAbsolutePath() + extension;
        String fileName = chooser.getSelectedFile().getName() + extension;
        
        if (extension.equals(".txt")) {    
            try {
                FileWriter fileWriter = new FileWriter(path);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                try {
                    bufferedWriter.write(String.valueOf(strokes.size()));
                    bufferedWriter.newLine();
                    for (int i = 0; i < strokes.size(); ++i) {
                        Stroke stroke = strokes.get(i);
                        
                        bufferedWriter.write(String.valueOf(stroke.width));
                        bufferedWriter.newLine();

                        bufferedWriter.write(String.valueOf(stroke.color.getBlue()));
                        bufferedWriter.newLine();
                        bufferedWriter.write(String.valueOf(stroke.color.getRed()));
                        bufferedWriter.newLine();
                        bufferedWriter.write(String.valueOf(stroke.color.getGreen()));
                        bufferedWriter.newLine();

                        ArrayList<Pair> points = stroke.points;
                        bufferedWriter.write(String.valueOf(points.size()));
                        bufferedWriter.newLine();
                        for(int j = 0; j < points.size(); ++j) {
                            bufferedWriter.write(String.valueOf(points.get(j).X()));
                            bufferedWriter.newLine();
                            bufferedWriter.write(String.valueOf(points.get(j).Y()));
                            bufferedWriter.newLine();
                            bufferedWriter.write(String.valueOf(points.get(j).getTime()));
                            bufferedWriter.newLine();
                        }
                    }
                } finally { 
                    bufferedWriter.close();
                }                   

            }
            catch(IOException ex) {
                System.out.println(ex);
            }
        }
        else if (extension == ".bin") {
            try {
                OutputStream file = new FileOutputStream(path);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput out = new ObjectOutputStream(buffer);
                try {
                    out.writeObject(strokes);
                } finally {
                    out.close();
                }
            }
            catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }   
    
    public void setFull() {
        if (full) {
            return;
        }
        full = true;
        parent.remove(fixed_view);
        parent.add(full_view, BorderLayout.CENTER);
        parent.validate();
        parent.repaint();
    }
    
    public void setFixed() {
        if (!full) {
            return;
        }
        full = false;
        parent.remove(full_view);
        parent.add(fixed_view, BorderLayout.CENTER);
        parent.validate();
        parent.repaint();
    }

    public void exit() {
        Object[] options = {"Yes", "No"};
        int options_index = JOptionPane.showOptionDialog(frame, "Proceed without saving?", "Warning: About to lose data!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
        if (options_index == 0) {
            frame.dispose();
        }
    }

    public int getStage() {
        return stage;
    }

    public ArrayList<Stroke> getStrokes() {
        return strokes;
    }

    public int getCompleteStrokes() {
        int count = 0;
        for (int i = 0; i < strokes.size(); ++i) {
            if (strokes.get(i).complete) {
                count++;
            }
        }
        return count;
    }
    
    public void setColor(Color color) {
        current_color = color;
    }

    public void setWidth(int width) {
        current_width = width;
    }

    public void setFrame(JFrame new_frame) {
        frame = new_frame;
    }

    public void setParent(JPanel new_parent) {
        parent = new_parent;
    }

    public void setFixedView(JScrollPane new_fixed_view) {
        fixed_view = new_fixed_view;
    }

    public void setFullView(JPanel new_full_view) {
        full_view = new_full_view;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getWidth() {
        return WIDTH;
    }

    public Color getColor() {
        return current_color;
    }

    public JFrame getFrame() {
        return frame;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setAnimation(Animation a) {
        animation = a;
    }
}

