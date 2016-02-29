import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.*;
import java.awt.event.*;    

public class Main{

    public static void main(String[] args){ 
        JFrame frame = new JFrame("Doodle Wizard");
        frame.setMinimumSize(new Dimension(400, 350));
        
        // create Model and initialize it
        Model model = new Model();
        model.setFrame(frame);
        
        Palette palette = new Palette(model);
        model.addObserver(palette);
        LineWidth linewidth = new LineWidth(model);
        model.addObserver(linewidth);       
        DrawingBoard board = new DrawingBoard(model);
        model.addObserver(board);
        DrawingBoard fixed_board = new DrawingBoard(model);
        model.addObserver(fixed_board);
        fixed_board.setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        fixed_board.setMaximumSize(new Dimension(model.getWidth(), model.getHeight()));
        Animation animation = new Animation(model);
        model.addObserver(animation);

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.add(new JPanel());
        p2.add(fixed_board);
        p2.add(new JPanel());
        p1.add(new JPanel());
        p1.add(p2);
        p1.add(new JPanel());

        JScrollPane fixed_view = new JScrollPane(p1);
        fixed_view.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel full_view = new JPanel(new GridLayout(1,1));
        full_view.add(board);
        full_view.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Menu menu = new Menu(model);
        model.addObserver(menu);
        frame.setJMenuBar(menu.getMenuBar());
 
        // let all the views know that they're connected to the model
        model.notifyObservers();
        
        // create the window
        JPanel p = new JPanel(new BorderLayout());
        frame.getContentPane().add(p);
        
        JPanel left_side = new JPanel(new GridLayout(2, 1, 5, 5));
        left_side.setPreferredSize(new Dimension(100, 500));
        left_side.add(palette);
        left_side.add(linewidth);
        animation.setPreferredSize(new Dimension(800, 50));
        //palette.setMaximumSize(new Dimension(100, 600));
        p.add(full_view, BorderLayout.CENTER);        
        p.add(left_side, BorderLayout.LINE_START);
        p.add(animation, BorderLayout.PAGE_END);
        
        model.setParent(p);
        model.setFixedView(fixed_view);
        model.setFullView(full_view);
        model.setAnimation(animation);

        frame.setPreferredSize(new Dimension(800,500));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    } 
}

