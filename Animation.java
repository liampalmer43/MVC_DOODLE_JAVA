import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

class Animation extends JPanel implements Observer {

    // the view's main user interface
    ArrayList<JPanel> panels = new ArrayList<JPanel>();
    ArrayList<Integer> widths = new ArrayList<Integer>();

    JSlider slider = new JSlider(0, 0, 0);
    JButton play = new JButton("Play");
    JButton reverse = new JButton("Reverse");

    // the model that this view is showing
    private Model model;    

    Animation(Model model_) {
        // create the view UI
        this.setLayout(new BorderLayout());
        play.setPreferredSize(new Dimension(70, 30));
        reverse.setPreferredSize(new Dimension(70, 30));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) { 
                model.setStage(slider.getValue());
            }
        });
        slider.setMajorTickSpacing(100);
        slider.setMinorTickSpacing(10);
        this.add(play, BorderLayout.LINE_START);
        this.add(slider, BorderLayout.CENTER);
        this.add(reverse, BorderLayout.LINE_END);

        model = model_;
    } 

    // Observer interface 
    @Override
    public void update(Observable arg0, Object arg1) {
//        if (model.getPoints().size() * 100 == slider.getMaximum()) {
//            return;
//        }
        // Must get the stage value before calling setMaximum.
        // Otherwise, setMaximum will change the slider and make its change handler call,
        // updating model.getStage() inappropriately.
        int stage = model.getStage();
        slider.setMaximum(model.getPoints().size() * 100);
        slider.setValue(stage);
        
        Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
        for (int i = 0; i <= model.getPoints().size() * 100; i += 100) {
            labels.put(new Integer(i), new JLabel(Integer.toString(i / 100)));
        }
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
    }
} 
