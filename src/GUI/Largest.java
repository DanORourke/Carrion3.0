package GUI;
import Engine.Engine;
import Engine.Alliance;
import Server.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class Largest extends JFrame{
    private final Engine engine;
    private final DrawingPanel drawingPanel;
    private final JTextArea hoverArea;
    private final boolean online;
    private final int userTeam;
    private final HashMap<Integer, String> playerNames;
    private final Client client;
    private final int id;
    private JPanel topPanel;

    Largest(String encodedBoard, int userTeam,
                   HashMap<Integer, String> playerNames, Client client, int id){
        super("Game ID: " + id);
        this.online = true;
        this.userTeam = userTeam;
        this.playerNames = playerNames;
        Alliance.playerNames.putAll(playerNames);
        this.client = client;
        this.id = id;
        this.engine = new Engine(encodedBoard, userTeam);
        this.drawingPanel = new DrawingPanel(engine);
        this.hoverArea = createHoverArea();
        setFrame();
        prepareFrame();
    }

    Largest(String encodedBoard){
        super("Offline");
        this.online = false;
        this.userTeam = 0;
        this.playerNames = null;
        this.client = null;
        this.id = 0;
        this.engine = new Engine(encodedBoard, 0);
        this.drawingPanel = new DrawingPanel(engine);
        this.hoverArea = createHoverArea();
        setFrame();
        prepareFrame();
    }

    private void setFrame(){
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1200, 600);
        setResizable(true);
        setLocationRelativeTo( null );
        setVisible(true);
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                getContentPane().removeAll();
            }
        };
        addWindowListener(exitListener);
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
        split.setDividerLocation(800);
        add(split);
        revalidate();
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

        hoverArea.setLineWrap(true);
        hoverArea.setWrapStyleWord(true);
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

        if (turnStage == 0){
            hoverArea.setText(getAllocateText());
        }else if (turnStage == 1){
            hoverArea.setText(getMoveText());
        }else if (turnStage == -1){
            hoverArea.setText(getWaitingText());
        }

        if (canAct == 0){
            revalidate();
            repaint();
            return;
        }

        JPanel actionButtonPanel = new JPanel();
        actionButtonPanel.setLayout(new BoxLayout(actionButtonPanel, BoxLayout.LINE_AXIS));
        actionButtonPanel.setOpaque(true);
        actionButtonPanel.setBackground(Colors.BACKGROUND);

        JButton next = createNextButton(turnStage);
        actionButtonPanel.add(next);

        if (turnStage == 0){
            JButton expose = createExposeButton();
            actionButtonPanel.add(Box.createHorizontalGlue());
            actionButtonPanel.add(expose);

            JButton abscond = createAbscondButton();
            actionButtonPanel.add(Box.createHorizontalGlue());
            actionButtonPanel.add(abscond);

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

        revalidate();
        repaint();
    }

    private JButton createNextButton(int turnStage){
        JButton next = new JButton("Next");
        if (turnStage == 1){
            next.setText("Submit Orders");
        }
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userTeam != 0 && turnStage == 1){
                    String encodedTurn = engine.getEncodedTurn();
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    client.submitOrders(id, encodedTurn);
                }else {
                    engine.nextPhase(false);
                    drawingPanel.updateMap();
                    updateTopPanel();
                }
            }
        });
        return next;
    }

    void notConnected(){
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        hoverArea.setText("Not Connected to Server");
    }

    int getId(){
        return id;
    }

    void updateEngine(String turn){
        if (turn.equals("Invalid")){
            hoverArea.setText("Invalid");
        }else{
            engine.addEncodedTurn(turn);
            drawingPanel.updateMap();
            updateTopPanel();
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private JButton createExposeButton(){
        JButton expose = new JButton("Expose General");
        expose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.setExposingGeneral();
                hoverArea.setText("Click on the general whose identity you are exposing to enemy intelligence.");
            }
        });
        return expose;
    }

    private JButton createAbscondButton(){
        JButton abscond = new JButton("Abscond");
        abscond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.abscond(false);
                hoverArea.setText("You have run away with what little treasure you could carry.");
                drawingPanel.updateMap();
                updateTopPanel();
            }
        });
        return abscond;
    }

    private JButton createAssistButton(){
        JButton assist = new JButton("Assist General");
        assist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.assist();
                hoverArea.setText("Left click on the general you are ordering to assist, " +
                        "then left click on the general you are ordering be assisted.\n\n" +
                        "A general cannot assist another if he is more than one space away or " +
                        "engaged in battle.\n\nIf a general is attacked when he is assisting another, he will suffer " +
                        "a penalty due to the distraction.");
            }
        });
        return assist;
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
        turnLabel.setText(player + stage);
        return turnLabel;
    }

    private String getTurnOrders(int turnOrders){
        if (turnOrders == 0){
            return "Allocate";
        }else if (turnOrders == 1){
            return "Move";
        }else if (turnOrders == -1){
            return "Ready?";
        }else{
            return "?";
        }
    }

    private String getPlayerColor(int playerTurn, JLabel turnLabel){
        if (playerTurn == 0){
            return "";
        }else if (playerTurn == 6){
            turnLabel.setForeground(Colors.PURPLE);
            if (online){
                return playerNames.get(6) + ": ";
            }else{
                return "Purple Player: ";
            }
        }else if (playerTurn == 4){
            turnLabel.setForeground(Colors.GREEN);
            if (online){
                return playerNames.get(4) + ": ";
            }else{
                return "Green Player: ";
            }
        }else if (playerTurn == 2){
            turnLabel.setForeground(Colors.ORANGE);
            if (online){
                return playerNames.get(2) + ": ";
            }else{
                return "Orange Player: ";
            }
        }else if (playerTurn == 3){
            turnLabel.setForeground(Colors.YELLOW);
            if (online){
                return playerNames.get(3) + ": ";
            }else{
                return "Yellow Player: ";
            }
        }else if (playerTurn == 1){
            turnLabel.setForeground(Colors.RED);
            if (online){
                return playerNames.get(1) + ": ";
            }else{
                return "Red Player: ";
            }
        }else {
            turnLabel.setForeground(Colors.BLUE);
            if (online){
                return playerNames.get(5) + ": ";
            }else{
                return "Blue Player: ";
            }
        }
    }

    private JTextArea createHoverArea(){
        JTextArea hoverArea = new JTextArea("hoverArea");
        hoverArea.setEditable(false);
        hoverArea.setLineWrap(true);
        hoverArea.setFont(new Font("Times New Roman", Font.PLAIN, 16));
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

    private String getMoveText() {
        return "Left click on a general to give him an order. " +
                "Left click on a neighboring tile to order the general to move to that tile.\n\n" +
                "Right click on a general to order him to drop a supply line or occupy a town in the tile he is in. " +
                "Right click on a neighboring tile to order a general to move and drop or move and occupy.";
    }

    private String getAllocateText() {
        return "Left click on a general to order a troop to his command.\n\n" +
                "Right click on a general to order him to release a troop. " +
                "That troop will only leave if it is connected to a town or capitol, " +
                "and will not be available for another general to use.";
    }

    private String getWaitingText() {
        return "Click Next when ready to play.";
    }
}
