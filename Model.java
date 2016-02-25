import java.util.Observable;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Model extends Observable { 
    // the data in the model, just a counter
    private int counter;    
    private Color current_color = Color.black;
    private int current_width = 4;
    private JFrame frame;



    ArrayList< ArrayList<Pair> > points = new ArrayList< ArrayList<Pair> >();
    ArrayList<Color> colors = new ArrayList<Color>();
    ArrayList<Integer> widths = new ArrayList<Integer>();

    public static class Pair {
        public int x;
        public int y;
        Pair(int xc, int yc) {
            x = xc;
            y = yc;
        }
    }    
    
    Model() {
        setChanged();
    }

    public void addPoint(int x, int y) {
        points.get(points.size() - 1).add(new Pair(x, y));
        setChanged();
        notifyObservers();
    }

    public void addStroke(int x, int y) {
        points.add(new ArrayList<Pair>());
        points.get(points.size() - 1).add(new Pair(x, y));
        colors.add(current_color);
        widths.add(current_width);
        setChanged();
        notifyObservers();
    }

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
    
    public void incrementCounter() {
        if (counter < 5) {
            counter++;
            System.out.println("Model: increment counter to " + counter);
            setChanged();
            notifyObservers();
        }
    }   
}

