import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;    
import java.util.Observable;
import java.util.Observer;

class Menu extends JPanel implements Observer {

    // the view's main user interface
    private JMenuBar bar;
    private JMenu file;
    private JMenu view; 
     
    // the model that this view is showing
    private Model model;
    
    Menu(Model model_) {
        // create the view UI
        bar = new JMenuBar();
        file = new JMenu("File");
        view = new JMenu("View");
        JMenuItem new_doodle = new JMenuItem("New");
        new_doodle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        }); 
        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.selectFile();
            }
        }); 
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.saveFile();
            }
        }); 
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.exit();
            }
        }); 
        file.add(new_doodle);
        file.add(open);
        file.add(save);
        file.add(exit);

        ButtonGroup view_group = new ButtonGroup();
        JRadioButtonMenuItem full_size = new JRadioButtonMenuItem("Full Size");
        full_size.setSelected(true);
        full_size.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.setFull();
            }
        });
        JRadioButtonMenuItem fit = new JRadioButtonMenuItem("Fit");
        fit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.setFixed();
            }
        });
        view_group.add(full_size);
        view_group.add(fit);
        view.add(full_size);
        view.add(fit);

        bar.add(file);
        bar.add(view);
        this.add(bar);

        // set the model 
        model = model_;
    } 

    public JMenuBar getMenuBar() {
        return bar;
    }

    // Observer interface 
    @Override
    public void update(Observable arg0, Object arg1) {
    }
} 
