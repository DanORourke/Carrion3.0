package GUI;
import Engine.Engine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Largest {
    private final Engine engine;
    private final DrawingPanel drawingPanel;
    private final JFrame frame;

    public Largest(String encodedBoard){
        //TODO test encoded before giving to engine
        this.engine = new Engine(encodedBoard);
        this.drawingPanel = new DrawingPanel(engine);
        this.frame = new JFrame("Carrion");
        setFrame();
        prepareFrame();
    }

    private void setFrame(){
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setResizable(true);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
    }

    private void prepareFrame(){
        JScrollPane scroll = new JScrollPane(drawingPanel);
        scroll.setWheelScrollingEnabled(false);
        scroll.setMinimumSize(new Dimension(0, 0));
        JTabbedPane tabbed = createTabbed();
        tabbed.setMinimumSize(new Dimension(0,0));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, tabbed);
        split.setOneTouchExpandable(true);
        split.setDividerLocation(600);
        frame.add(split);
        frame.revalidate();
        scroll.getViewport().setViewPosition(new Point(drawingPanel.getPreferredSize().width/2 -
                scroll.getViewport().getViewRect().width/2,
                drawingPanel.getPreferredSize().height/2 - scroll.getViewport().getViewRect().height/2));
    }

    private JTabbedPane createTabbed(){
        JTabbedPane tabbed = new JTabbedPane();
        JPanel mainPanel = createMainPanel();
        tabbed.addTab("Main", mainPanel);
        return tabbed;
    }

    private JPanel createMainPanel(){
        JPanel mainPanel = new JPanel(new GridBagLayout());

        JTextArea hoverArea = createHoverArea();
        JScrollPane hovScroll = new JScrollPane(hoverArea);
        GridBagConstraints c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(hovScroll, c);

        JTextArea clickArea = createClickArea();
        JScrollPane clickScroll = new JScrollPane(clickArea);
        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(clickScroll, c);

        JButton back = createBackButton();
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(back, c);

        JButton forward = createForwardButton();
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(forward, c);

        return mainPanel;
    }

    private JTextArea createHoverArea(){
        JTextArea hoverArea = new JTextArea("hoverArea");
        hoverArea.setEditable(false);
        hoverArea.setLineWrap(true);
        drawingPanel.setHoverArea(hoverArea);
        return hoverArea;
    }

    private JTextArea createClickArea(){
        JTextArea clickArea = new JTextArea("clickArea");
        clickArea.setEditable(false);
        clickArea.setLineWrap(true);
        drawingPanel.setClickArea(clickArea);
        return clickArea;
    }

    private JButton createBackButton(){
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.back();
                drawingPanel.updateMap();
            }
        });
        return back;
    }

    private JButton createForwardButton(){
        JButton forward = new JButton("Forward");
        forward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.forward();
                drawingPanel.updateMap();
            }
        });
        return forward;
    }
}
