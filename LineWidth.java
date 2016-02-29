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

class LineWidth extends JPanel implements Observer {

    // the view's main user interface
    ArrayList<JPanel> panels = new ArrayList<JPanel>();
    ArrayList<Integer> widths = new ArrayList<Integer>();

    // the model that this view is showing
    private Model model;
    
    LineWidth(Model model_) {
        // create the view UI
        widths.add(2);
        widths.add(4);
        widths.add(6);
        widths.add(8);
        widths.add(10);
        widths.add(12);
        widths.add(14);
        widths.add(16);    
        widths.add(18);
        widths.add(20); 

        for (int i = 0; i < widths.size(); ++i) {
            int width = widths.get(i);
            JPanel new_panel = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.black);
                    g2.setStroke(new BasicStroke(width));
                    g2.draw(new Line2D.Float(0, getHeight()/2, getWidth(), getHeight()/2));
                }
            };
            new_panel.setBackground(Color.white);
            new_panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    model.setWidth(width);
                }
            });
            panels.add(new_panel);
        }

        this.setLayout(new GridLayout(5, 2, 2, 2));
        for (int i = 0; i < panels.size(); ++i) {
            this.add(panels.get(i));
        }
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        // set the model 
        model = model_;
    } 

    // Observer interface 
    @Override
    public void update(Observable arg0, Object arg1) {
        // Might be used so this component can watch the model.
    }
} 
