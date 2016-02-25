import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.*;
import java.awt.event.*;    
import java.util.*;
import java.util.Observable;
import java.util.Observer;
import java.awt.geom.*;

class DrawingBoard extends JPanel implements Observer {

    // the model that this view is showing
    private Model model;
    
    DrawingBoard(Model model_) {
        // create the view UI
        this.setBackground(Color.white);
        MouseAdapter mouse_adapter = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                model.addPoint(e.getX(), e.getY(), System.currentTimeMillis());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                model.addStroke(e.getX(), e.getY(), System.currentTimeMillis());
            }
        };
        this.addMouseListener(mouse_adapter);
        this.addMouseMotionListener(mouse_adapter);
        
        // set the model 
        model = model_;
    }
        
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                            RenderingHints.VALUE_ANTIALIAS_ON);
        ArrayList< ArrayList<Model.Pair> > points = model.getPoints();
        ArrayList<Color> colors = model.getColors();
        ArrayList<Integer> widths = model.getWidths();

        
        for (int i = 0; i < points.size(); ++i) {
            int start_stage = i * 100;
            int end_stage = start_stage + 100;
            if (model.getStage() <= start_stage) {
                return;
            }
            g2.setColor(colors.get(i));
            g2.setStroke(new BasicStroke(widths.get(i), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            ArrayList<Model.Pair> ps = points.get(i);
            if (model.getStage() >= end_stage) {
                for (int j = 0; j < ps.size(); ++j) {
                    Model.Pair previous = j - 1 >= 0 ? ps.get(j-1) : ps.get(j);
                    Model.Pair current = ps.get(j);
                    g2.draw(new Line2D.Float(previous.x, previous.y, current.x, current.y));
                }
            }
            else {
                // start_stage < model.getStage() <= end_stage
                long start = ps.get(0).time;
                long end = ps.get(ps.size() - 1).time;
                long limit = (end - start) * (model.getStage() % 100) / 100;
                limit += start;
                for (int j = 0; j < ps.size(); ++j) {
                    if (ps.get(j).time <= limit) {
                        Model.Pair previous = j - 1 >= 0 ? ps.get(j-1) : ps.get(j);
                        Model.Pair current = ps.get(j);
                        g2.draw(new Line2D.Float(previous.x, previous.y, current.x, current.y));
                    }
                    else {
                        return;
                    }
                }
                
            }
        }
    }

    // Observer interface 
    @Override
    public void update(Observable arg0, Object arg1) {
        repaint();
    }
} 
