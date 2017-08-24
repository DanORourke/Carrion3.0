package GUI;
import Engine.Engine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Largest {
    private int radius = 25;
    private String encodedBoard = "21,3,next,12,-8,-4,11,-8,-3,0,11,-8,-3,10,-8,-2,0,next,next";//

    public Largest(){
        //test encoded before giving to engine
        Engine engine = new Engine(encodedBoard);
        DrawingPanel drawingPanel = new DrawingPanel(radius, engine);
        JScrollPane scroll = new JScrollPane(drawingPanel);
        scroll.setWheelScrollingEnabled(false);
        JFrame frame = new JFrame("Carrion");
        JPanel bigPanel = new JPanel(new BorderLayout());
        bigPanel.add(scroll, BorderLayout.CENTER);


        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.back();
                drawingPanel.updateMap();
            }
        });

        JButton forward = new JButton("Forward");
        forward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.forward();
                drawingPanel.updateMap();
            }
        });


        bigPanel.add(back, BorderLayout.BEFORE_FIRST_LINE);
        bigPanel.add(forward, BorderLayout.AFTER_LAST_LINE);
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.add(bigPanel);
        frame.setSize(700, 600);
        frame.setResizable(true);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
        scroll.getViewport().setViewPosition(new Point(drawingPanel.getPreferredSize().width/2 -
                scroll.getViewport().getViewRect().width/2,
                drawingPanel.getPreferredSize().height/2 + (radius * 3 * 13)/2 -
                scroll.getViewport().getViewRect().height/2));
    }
}
