import java.util.Observable;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;    

public class Model extends Observable { 
    // the data in the model
    private Color current_color = Color.black;
    private int current_width = 4;
    private int stage = 0;
    private JFrame frame;

    ArrayList<Stroke> strokes = new ArrayList<Stroke>();
/*
    ArrayList< ArrayList<Pair> > points = new ArrayList< ArrayList<Pair> >();
    ArrayList<Color> colors = new ArrayList<Color>();
    ArrayList<Integer> widths = new ArrayList<Integer>();
*/
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
        public int x;
        public int y;
        public long time;
        Pair(int xc, int yc, long t) {
            x = xc;
            y = yc;
            time = t;
        }
    }    
    
    Model() {
        setChanged();
    }

    public void addPoint(int x, int y, long time) {
        if (strokes.size() == 0) {
            return;
        }
        strokes.get(strokes.size() - 1).points.add(new Pair(x, y, time));
/*
        points.get(points.size() - 1).add(new Pair(x, y, time));
*/
        setChanged();
        notifyObservers();
    }

    public void addStroke(int x, int y, long time) {
        cleanPoints();

        strokes.add(new Stroke(new ArrayList<Pair>(), current_color, current_width));
        strokes.get(strokes.size() - 1).points.add(new Pair(x, y, time));
        
/*
        points.add(new ArrayList<Pair>());
        points.get(points.size() - 1).add(new Pair(x, y, time));
System.out.println("ADDING STROKE " + current_color);
        colors.add(current_color);
        widths.add(current_width);
        assert colors.size() == points.size();
*/      
        stage = (strokes.size() - 1) * 100;
        setChanged();
        notifyObservers();
    }

    public void finishStroke() {
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

/*
System.out.println("CLEANING");
        for (int i = 0; i < points.size(); ++i) {
System.out.println("INSIDE LOOP");
            int start_stage = i * 100;
            int end_stage = start_stage + 100;
            if (stage <= start_stage) {
                for (int j = points.size() - 1; j >= i; --j) {
                    points.remove(j);
                    widths.remove(j);
                    colors.remove(j);
                }  
                return;
            }
System.out.println("CHECK HERE");
            ArrayList<Pair> ps = points.get(i);
            if (stage <= end_stage) {
                // start_stage < stage <= end_stage
                long start = ps.get(0).time;
                long end = ps.get(ps.size() - 1).time;
System.out.println("STAGE " + stage);
                long limit;
                if (stage == start_stage) {
                    limit = 0;
                }
                else if (stage == end_stage) {
                    limit = end - start;
                }
                else {
                    limit = (end - start) * (stage % 100) / 100;
                }
                limit += start;
System.out.println("START " + start);
System.out.println("END " + end);
System.out.println("LIMIT " + limit);
                for (int j = 0; j < ps.size(); ++j) {
                    if (ps.get(j).time > limit) {
System.out.println("HERE IS THE VALUE OF INTEREST: " + j + " Out of " + ps.size());
                        for (int k = ps.size() - 1; k >= j; --k) {
                            ps.remove(k);
                        }
System.out.println(ps.size());
                        for (int k = points.size() - 1; k > i; --k) {
                            points.remove(k);
                            widths.remove(k);
                            colors.remove(k);
                        }
                        return;
                    }
                }
                
            }
        }
*/
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
        stage = 0;
        setChanged();
        notifyObservers();
        final Timer timer = new Timer(12, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (stage >= strokes.size() * 100) {
                    ((Timer)evt.getSource()).stop();
                }
                stage++;
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
/*
    public ArrayList< ArrayList<Pair> > getPoints() {
        return points;
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public ArrayList<Integer> getWidths() {
        return widths;
    }
    
    public int getCounterValue() {
        return counter;
    }
*/
    public void setColor(Color color) {
        current_color = color;
    }

    public void setWidth(int width) {
        current_width = width;
    }

    public void setFrame(JFrame new_frame) {
        frame = new_frame;
    }

    public Color getColor() {
        return current_color;
    }

    public JFrame getFrame() {
        return frame;
    }
 /*   
    public void incrementCounter() {
        if (counter < 5) {
            counter++;
            setChanged();
            notifyObservers();
        }
    }
*/   
}

