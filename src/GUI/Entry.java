package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Entry {
    private JFrame frame;

    public Entry(){
        this.frame = new JFrame("Carrion");
        setFrame();
        prepareFrame();
    }

    private void setFrame(){
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setResizable(true);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
    }

    private void prepareFrame(){
        JTabbedPane tabbed = createTabbed();
        frame.add(tabbed);
        frame.revalidate();
    }

    private JTabbedPane createTabbed(){
        JTabbedPane tabbed = new JTabbedPane();
        JPanel offline = createOfflinePanel();
        tabbed.addTab("Offline", offline);
        tabbed.setOpaque(true);
        tabbed.setBackground(Colors.BACKGROUND);
        return tabbed;
    }

    private JPanel createOfflinePanel(){
        JPanel offline = new JPanel(new GridLayout(0, 1));
        offline.setOpaque(true);
        offline.setBackground(Colors.BACKGROUND);
        ArrayList<JRadioButton> buttons = new ArrayList<>();

        ButtonGroup group = new ButtonGroup();

        JRadioButton neighbors = new JRadioButton("2 Player Neighbors");
        neighbors.setBackground(Colors.BACKGROUND);
        neighbors.setForeground(Colors.YELLOW);
        offline.add(neighbors);
        group.add(neighbors);
        buttons.add(neighbors);

        JRadioButton angle = new JRadioButton("2 Player Angle");
        angle.setBackground(Colors.BACKGROUND);
        angle.setForeground(Colors.YELLOW);
        offline.add(angle);
        group.add(angle);
        buttons.add(angle);

        JRadioButton two = new JRadioButton("2 Player");
        two.setBackground(Colors.BACKGROUND);
        two.setForeground(Colors.YELLOW);
        offline.add(two);
        group.add(two);
        buttons.add(two);

        JRadioButton three = new JRadioButton("3 Player");
        three.setSelected(true);
        three.setBackground(Colors.BACKGROUND);
        three.setForeground(Colors.YELLOW);
        offline.add(three);
        group.add(three);
        buttons.add(three);

        JRadioButton four = new JRadioButton("4 Player");
        four.setBackground(Colors.BACKGROUND);
        four.setForeground(Colors.YELLOW);
        offline.add(four);
        group.add(four);
        buttons.add(four);

        JRadioButton five = new JRadioButton("5 Player");
        five.setBackground(Colors.BACKGROUND);
        five.setForeground(Colors.YELLOW);
        offline.add(five);
        group.add(five);
        buttons.add(five);

        JRadioButton six = new JRadioButton("6 Player");
        six.setBackground(Colors.BACKGROUND);
        six.setForeground(Colors.YELLOW);
        offline.add(six);
        group.add(six);
        buttons.add(six);

        JButton play = new JButton("Play");
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton type = new JRadioButton();
                for (JRadioButton t : buttons){
                    if (t.isSelected()){
                        type = t;
                    }
                }

                if (type == neighbors){
                    new Largest("21,0");
                }else if(type == angle) {
                    new Largest("21,1");
                }else if(type == two) {
                    new Largest("21,2");
                }else if(type == three) {
                    new Largest("21,3");
                }else if(type == four) {
                    new Largest("21,4");
                }else if(type == five) {
                    new Largest("21,5");
                }else if(type == six) {
                    new Largest("21,6");
                }
            }
        });
        offline.add(play);
        return offline;
    }
}
