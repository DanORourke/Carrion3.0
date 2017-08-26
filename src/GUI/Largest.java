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
    private final JTextArea hoverArea;
    private JPanel topPanel;

    public Largest(String encodedBoard){
        //TODO test encoded before giving to engine
        this.engine = new Engine(encodedBoard);
        this.drawingPanel = new DrawingPanel(engine);
        this.frame = new JFrame("Carrion");
        this.hoverArea = createHoverArea();
        setFrame();
        prepareFrame();
    }

    private void setFrame(){
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
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
        split.setResizeWeight(0.66);
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
        tabbed.setOpaque(true);
        tabbed.setBackground(Colors.BACKGROUND);
        return tabbed;
    }

    private JPanel createMainPanel(){
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(true);
        mainPanel.setBackground(Colors.BACKGROUND);

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

        topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(true);
        topPanel.setBackground(Colors.BACKGROUND);
        updateTopPanel();
        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(topPanel, c);

        JPanel bfButtonPanel = new JPanel();
        bfButtonPanel.setOpaque(true);
        bfButtonPanel.setBackground(Colors.BACKGROUND);
        bfButtonPanel.setLayout(new BoxLayout(bfButtonPanel, BoxLayout.LINE_AXIS));
        JButton back = createBackButton();
        bfButtonPanel.add(back);
        bfButtonPanel.add(Box.createHorizontalGlue());
        JButton forward = createForwardButton();
        bfButtonPanel.add(forward);
        JButton current = createCurrentButton();
        bfButtonPanel.add(Box.createHorizontalGlue());
        bfButtonPanel.add(current);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(bfButtonPanel, c);

        return mainPanel;
    }

    private void updateTopPanel(){
        topPanel.removeAll();

        int[] info = engine.getStateInfo();
        int playerTurn = info[0];
        int turnStage = info[1];
        int canAct = info[2];

        JLabel turnLabel = createTurnLabel(playerTurn, turnStage);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        topPanel.add(turnLabel, c);

        if (canAct == 0){
            frame.revalidate();
            frame.repaint();
            return;
        }

        JPanel actionButtonPanel = new JPanel();
        actionButtonPanel.setLayout(new BoxLayout(actionButtonPanel, BoxLayout.LINE_AXIS));
        actionButtonPanel.setOpaque(true);
        actionButtonPanel.setBackground(Colors.BACKGROUND);

        JButton next = createNextButton();
        actionButtonPanel.add(next);

        if (turnStage == 0){
            JButton expose = createExposeButton();
            actionButtonPanel.add(Box.createHorizontalGlue());
            actionButtonPanel.add(expose);
        }else if (turnStage == 1){
            JButton assist = createAssistButton();
            actionButtonPanel.add(Box.createHorizontalGlue());
            actionButtonPanel.add(assist);

            JButton chief = createChiefButton();
            actionButtonPanel.add(Box.createHorizontalGlue());
            actionButtonPanel.add(chief);
        }

        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.insets = new Insets(10, 0, 0, 0);
        topPanel.add(actionButtonPanel, c);

        frame.revalidate();
        frame.repaint();
    }

    private JButton createNextButton(){
        JButton next = new JButton("Next");
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.nextPhase();
                drawingPanel.updateMap();
                updateTopPanel();
            }
        });
        return next;
    }


    private JButton createExposeButton(){
        return new JButton("Expose General");
    }

    private JButton createAssistButton(){
        return new JButton("Assist General");
    }

    private JButton createChiefButton(){
        JButton chief =  new JButton("Move Chief");
        chief.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.settingChief();
                hoverArea.setText("Left click on the general you are ordering the chief of staff to station with next, " +
                        "or right click on the capitol you are ordering the chief of staff to station with next.  " +
                        "The chief will only move if he is connected to his next station at the end of the turn.");
            }
        });
        return chief;
    }

    private JLabel createTurnLabel(int playerTurn, int turnStage){
        JLabel turnLabel = new JLabel();
        turnLabel.setFont(new Font("Serif", Font.BOLD, 23));
        turnLabel.setOpaque(true);
        turnLabel.setBackground(Colors.BACKGROUND);
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        String player = getPlayerColor(playerTurn, turnLabel);
        String stage = getTurnOrders(turnStage);
        turnLabel.setText(player + " Player: " + stage);
        return turnLabel;
    }

    private String getTurnOrders(int turnOrders){
        if (turnOrders == 0){
            return "Allocate";
        }else if (turnOrders == 1){
            return "Move";
        }else{
            return "?";
        }
    }

    private String getPlayerColor(int playerTurn, JLabel turnLabel){
        if (playerTurn == 0){
            return "";
        }else if (playerTurn == 6){
            turnLabel.setForeground(Colors.PURPLE);
            return "Purple";
        }else if (playerTurn == 4){
            turnLabel.setForeground(Colors.GREEN);
            return "Green";
        }else if (playerTurn == 2){
            turnLabel.setForeground(Colors.ORANGE);
            return "Orange";
        }else if (playerTurn == 3){
            turnLabel.setForeground(Colors.YELLOW);
            return "Yellow";
        }else if (playerTurn == 1){
            turnLabel.setForeground(Colors.RED);
            return "Red";
        }else {
            turnLabel.setForeground(Colors.BLUE);
            return "Blue";
        }
    }

    private JTextArea createHoverArea(){
        JTextArea hoverArea = new JTextArea("hoverArea");
        hoverArea.setEditable(false);
        hoverArea.setLineWrap(true);
        drawingPanel.setHoverArea(hoverArea);
        return hoverArea;
    }

    private JButton createBackButton(){
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.back();
                drawingPanel.updateMap();
                updateTopPanel();
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
                updateTopPanel();
            }
        });
        return forward;
    }

    private JButton createCurrentButton(){
        JButton current = new JButton("Current");
        current.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.current();
                drawingPanel.updateMap();
                updateTopPanel();
            }
        });
        return current;
    }
}
