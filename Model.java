import java.util.Observable;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;    
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class Model extends Observable { 
    // the data in the model
    private Color current_color = Color.black;
    private int current_width = 4;
    private int stage = 0;
    private JFrame frame;
    private JPanel parent;
    private JScrollPane fixed_view;
    private JPanel full_view;
    private static boolean full = true;
    private JFileChooser chooser;

    // Whether or not the animation is currently playing;
    private boolean playing = false;
    private static int WIDTH = 600;
    private static int HEIGHT = 350;

    ArrayList<Stroke> strokes = new ArrayList<Stroke>();
    
    public static class Stroke {
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

    public static class Pair {
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
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Doodle Images", "doodle");
        chooser.setFileFilter(filter);

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
        stage = 0;
        setChanged();
        notifyObservers();
        final Timer timer = new Timer(12, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (stage >= strokes.size() * 100) {
                    ((Timer)evt.getSource()).stop();
                    playing = false;
                }
                stage++;
                setChanged();
                notifyObservers();
            }
        });
        timer.start();
    }

    public void reverse() {
        // If the animation is already playing, avoid starting another timer.
        if (playing) {
            return;
        }
        playing = true;
        stage = strokes.size() * 100;
        setChanged();
        notifyObservers();
        final Timer timer = new Timer(12, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (stage <= 0) {
                    ((Timer)evt.getSource()).stop();
                    playing = false;
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
        int result = chooser.showOpenDialog(frame);
        if (result == chooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            String fileName = chooser.getSelectedFile().getName();
            String line = null;
            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                ArrayList<Stroke> stroke_import = new ArrayList<Stroke>();
                try {
                    line = bufferedReader.readLine();
                    int size = Int(line);
                    for (int i = 0; i < size; ++i) {
                        line = bufferedReader.readLine();
                        int width = Int(line);
                        line = bufferedReader.readLine();
                        int blue = Int(line);
                        line = bufferedReader.readLine();
                        int red = Int(line);
                        line = bufferedReader.readLine();
                        int green = Int(line);
                
                        Stroke stroke = new Stroke(new ArrayList<Pair>(), new Color(red, green, blue), width);   
                        stroke.complete = true;

                        line = bufferedReader.readLine();
                        int pointListSize = Int(line);
System.out.println("DDDDDDDDDDDDDDDDDDDDD: " + pointListSize);
                        for(int j = 0; j < pointListSize; ++j) {
                            float x = Float(bufferedReader.readLine());
                            float y = Float(bufferedReader.readLine());
                            long time = Long.parseLong(bufferedReader.readLine());
                            stroke.points.add(new Pair(x, y, time));
                        }
System.out.println("KKKKKKKKKKKKKKKKKKKKK: " + stroke.points.size());
                        stroke_import.add(stroke);
                    }
                } finally {
                    bufferedReader.close();
                }
                strokes = stroke_import;
System.out.println(strokes.size());
                stage = strokes.size() * 100;
System.out.println(stage);
                setChanged();
                notifyObservers();
            } 
            catch(FileNotFoundException ex) {
                System.out.println("Unable to open file '" + fileName + "'");       
                System.out.println(ex);         
            } catch(IOException ex) {
                System.out.println("Error reading file '" + fileName + "'");                  
            }
        }
    }

    public void saveFile() {
        int result = chooser.showSaveDialog(frame);
        if (chooser.getSelectedFile() == null) {
            return;
        }
        String path = chooser.getSelectedFile().getAbsolutePath() + ".doodle";
        String fileName = chooser.getSelectedFile().getName() + ".doodle";
            
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
            System.out.println("Error writing to file '" + fileName + "'");
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
        frame.dispose();
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
}

