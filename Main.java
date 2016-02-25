// HelloMVC: a simple MVC example
// the model is just a counter 
// inspired by code by Joseph Mack, http://www.austintek.com/mvc/

/**
 *  Two views with integrated controllers.  Uses java.util.Observ{er, able} instead
 *  of custom IView.
 */

import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.*;
import java.awt.event.*;    

public class Main{

    public static void main(String[] args){ 
        JFrame frame = new JFrame("HelloMVC4");
        
        // create Model and initialize it
        Model model = new Model();
        model.setFrame(frame);
        
        // create View, tell it about model (and controller)
        View view = new View(model);
        // tell Model about View. 
        model.addObserver(view);
        
        // create second view ...
        View2 view2 = new View2(model);
        model.addObserver(view2);
        
        // create palette view ...
        Palette palette = new Palette(model);
        model.addObserver(palette);
        LineWidth linewidth = new LineWidth(model);
        model.addObserver(linewidth);       
        DrawingBoard board = new DrawingBoard(model);
        model.addObserver(board);
 
        // let all the views know that they're connected to the model
        model.notifyObservers();
        
        // create the window
        JPanel p = new JPanel(new BorderLayout());
        frame.getContentPane().add(p);
        
        p.add(view, BorderLayout.LINE_END);

        JPanel left_side = new JPanel(new GridLayout(2, 1, 5, 5));
        left_side.setPreferredSize(new Dimension(100, 500));
        left_side.add(palette);
        left_side.add(linewidth);
        //palette.setMaximumSize(new Dimension(100, 600));
        p.add(board, BorderLayout.CENTER);        
        p.add(left_side, BorderLayout.LINE_START);
        
        frame.setPreferredSize(new Dimension(800,500));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    } 
}

