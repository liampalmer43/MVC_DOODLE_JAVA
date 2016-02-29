import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.*;
import java.awt.event.*;    
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Observable;
import java.util.Observer;
import java.io.File;
import java.io.IOException;

class Palette extends JPanel implements Observer {

    // the view's main user interface
    private JButton button;
    private ArrayList<JPanel> panels = new ArrayList<JPanel>();
    private ArrayList<Color> colors = new ArrayList<Color>();
    private JColorChooser options = new JColorChooser();

    // the model that this view is showing
    private Model model;
    
    Palette(Model model_) {
        // create the view UI
        colors.add(Color.black);
        colors.add(Color.blue);
        colors.add(Color.orange);
        colors.add(Color.magenta);
        colors.add(Color.cyan);
        colors.add(Color.pink);
        colors.add(Color.red);
        colors.add(Color.green);
        colors.add(Color.yellow);

        for (int i = 0; i < colors.size(); ++i) {
            JPanel new_panel = new JPanel();
            Color color = colors.get(i);
            new_panel.setBackground(color);
            new_panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    model.setColor(color);
                    System.out.println(color);
                }
            });
            panels.add(new_panel);
        }
        try {
            BufferedImage icon = ImageIO.read(new File("Color_Icon.png"));
            Image scaled_icon = icon.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            button = new JButton(new ImageIcon(scaled_icon));
        } catch (IOException e) {
            button = new JButton("Color");
        }

        this.setLayout(new GridLayout(5, 2, 2, 2));
        for (int i = 0; i < panels.size(); ++i) {
            this.add(panels.get(i));
        }
        this.add(button);
        
        // set the model 
        model = model_;
        
        // setup the event to go to the "controller"
        // (this anonymous class is essentially the controller)
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ActionListener action_listener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        model.setColor(options.getColor());
                        System.out.println(options.getColor());
                    }
                };
                options.setColor(model.getColor());
                JDialog dialog = JColorChooser.createDialog(model.getFrame(), "Custom Color Picker", false, options, action_listener, null);
                dialog.setVisible(true);
            }
        }); 
    } 

    // Observer interface 
    @Override
    public void update(Observable arg0, Object arg1) {
        // Might be used if we want this componenet to watch the view.
    }
} 
