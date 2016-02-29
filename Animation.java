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
    JButton start = new JButton("Start");
    JButton end = new JButton("End");

    // the model that this view is showing
    private Model model;    

    Animation(Model model_) {
        // create the view UI
        this.setLayout(new BorderLayout());
        play.setPreferredSize(new Dimension(70, 30));
        reverse.setPreferredSize(new Dimension(70, 30));
        start.setPreferredSize(new Dimension(70, 30));
        end.setPreferredSize(new Dimension(70, 30));
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.play();
            }
        }); 
        reverse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.reverse();
            }
        }); 
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.start();
            }
        }); 
        end.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) { 
                model.end();
            }
        });
        slider.setMajorTickSpacing(100);
        slider.setMinorTickSpacing(10);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) { 
                model.setStage(slider.getValue());
            }
        });
        this.add(slider, BorderLayout.CENTER);
        JPanel button_group = new JPanel(new GridLayout(1, 2, 5, 5));
        button_group.setPreferredSize(new Dimension(140, 30));
        button_group.add(start);
        button_group.add(end);
        JPanel button_group_2 = new JPanel(new GridLayout(1, 2, 5, 5));
        button_group_2.setPreferredSize(new Dimension(140, 30));
        button_group_2.add(play);
        button_group_2.add(reverse);
        this.add(button_group_2, BorderLayout.LINE_START);
        this.add(button_group, BorderLayout.LINE_END);

        model = model_;
    } 

    // Observer interface 
    @Override
    public void update(Observable arg0, Object arg1) {
        // Must get the stage value before calling setMaximum.
        // Otherwise, setMaximum will change the slider and make its change handler call,
        // updating model.getStage() inappropriately.
        int number_of_strokes = model.getCompleteStrokes();
        int stage = model.getStage();
        slider.setMaximum(number_of_strokes * 100);
        slider.setValue(stage);
        
        Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
        for (int i = 0; i <= number_of_strokes * 100; i += 100) {
            labels.put(new Integer(i), new JLabel(Integer.toString(i / 100)));
        }
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
    }
} 
