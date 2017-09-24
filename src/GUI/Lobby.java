package GUI;

import Server.Client;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Lobby extends JFrame{
    private Client client;
    private final String username;
    private ArrayList<String> status;
    private JPanel activeGames;
    private Largest largest;
    private JLabel flag;
    private JTextArea chatArea;

    public Lobby(Client client, ArrayList<String> status){
        super(client.getUsername());
        this.client = client;
        client.setLobby(this);
        this.username = client.getUsername();
        this.status = status;
        this.largest = null;
        setFrame();
        prepareFrame();
    }

    private void setFrame(){
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setResizable(true);
        setLocationRelativeTo( null );
        setVisible(true);
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                getContentPane().removeAll();
                disposeLargest();
                client.sendClose();
                client.close();
            }
        };
        addWindowListener(exitListener);
    }

    private void prepareFrame(){
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Colors.BACKGROUND);
        JPanel title = createTitlePanel();
        createActiveGames();
        JScrollPane scroll = new JScrollPane(activeGames);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        JTabbedPane tabbed = createTabbed();
        tabbed.setMinimumSize(new Dimension(0,0));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, tabbed);
        split.setBackground(Colors.BACKGROUND);
        split.setOneTouchExpandable(true);
        split.setResizeWeight(0.66);
        split.setDividerLocation(600);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.BOTH;
        main.add(title, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        main.add(split, c);

        add(main);
        revalidate();
    }

    private JTabbedPane createTabbed(){
        JTabbedPane tabbed = new JTabbedPane();
        tabbed.setForeground(Colors.RED);
        tabbed.setBackground(Colors.BACKGROUND);
        JPanel newGame = createNewGamePanel();
        tabbed.add("New", newGame);
        JPanel chat = createChatPanel();
        tabbed.add("Chat", chat);
        return tabbed;
    }

    private JPanel createChatPanel(){
        JPanel chat = new JPanel(new GridBagLayout());
        chat.setBackground(Colors.BACKGROUND);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);

        JTextArea type = new JTextArea();
        type.setWrapStyleWord(true);
        type.setLineWrap(true);

        JButton sendChat = new JButton("Send");
        sendChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = type.getText();
                if (message != null && client != null){
                    client.sendChat(message);
                }
                type.setText("");
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.75;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 5, 10);
        chat.add(chatArea, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0.20;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 10);
        chat.add(type, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 0.05;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 10, 10, 10);
        chat.add(sendChat, c);


        return chat;
    }

    public void newChat(String message){
        chatArea.append(message + "\n");
    }

    private JPanel createTitlePanel(){
        JPanel title = new JPanel(new GridBagLayout());
        title.setBackground(Colors.BACKGROUND);
        JLabel welcome = new JLabel("Welcome to Carrion " + username);
        welcome.setBackground(Colors.BACKGROUND);
        welcome.setForeground(Colors.YELLOW);
        Font font = new Font("Serif", Font.BOLD, 35);
        welcome.setFont(font);

        flag = new JLabel();
        flag.setBackground(Colors.BACKGROUND);
        flag.setForeground(Colors.RED);
        flag.setFont(font);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        title.add(welcome, c);

        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 10, 10, 10);
        title.add(flag, c);

        return title;
    }

    private void createActiveGames(){
//        activeGames = new JPanel(new GridLayout(0, 1, 0, 10));
//        activeGames.setBackground(Colors.BACKGROUND);
//        updateActiveGames();
        activeGames = new JPanel();
        activeGames.setLayout(new BoxLayout(activeGames, BoxLayout.PAGE_AXIS));
        activeGames.setOpaque(true);
        activeGames.setBackground(Colors.BACKGROUND);
        updateActiveGames();
    }

    private void update(){
        if (!status.get(0).equals("Invalid")){
            updateActiveGames();
            revalidate();
            repaint();
        }else {
            flag.setText("Invalid");
            Timer timer = new Timer(5000, new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            flag.setText("");
                        }
                    });
                }
            });
            timer.start();
        }
    }

    private void updateActiveGames(){
        activeGames.removeAll();
        if (status.get(0).equals("Empty")){
            JPanel panel= new JPanel(new GridBagLayout());
            panel.setBackground(Colors.BACKGROUND);
            JLabel sign = new JLabel(username + " is a coward");
            sign.setBackground(Colors.BACKGROUND);
            sign.setForeground(Colors.YELLOW);
            sign.setFont(new Font("Serif", Font.BOLD, 23));
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.CENTER;
            panel.add(sign, c);
            activeGames.add(panel);
            return;
        }
        JPanel signPanel= new JPanel(new GridBagLayout());
        signPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        signPanel.setBackground(Colors.BACKGROUND);
        JLabel sign = new JLabel(" Battles");
        sign.setBackground(Colors.BACKGROUND);
        sign.setForeground(Colors.YELLOW);
        sign.setFont(new Font("Serif", Font.BOLD, 23));
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        signPanel.add(sign, c);
        signPanel.setPreferredSize(new Dimension(25, 75));
        signPanel.setMinimumSize(new Dimension(25, 75));
        activeGames.add(signPanel);

        int i = 0;
        while (i < status.size()){
            int gameId = Integer.parseInt(status.get(i));
            int gameType = Integer.parseInt(status.get(i+1));
            int gameStatus = Integer.parseInt(status.get(i+2));
            i+=3;
            int numberOfPlayers = convertTypeToNumber(gameType);
            int total = i + numberOfPlayers;
            ArrayList<String> players = new ArrayList<>();
            String encodedBoard = "NA";
            int myColor = -1;
            if (gameStatus != 0){
                myColor = Integer.parseInt(status.get(i));
                i++;
                while (i < total + 1){
                    players.add(status.get(i));
                    i++;
                }
                encodedBoard = status.get(i);
                i++;
            }

            JPanel panel = createSingleGamePanel(gameId, gameStatus, gameType, myColor, players, encodedBoard);
            panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
            activeGames.add(panel);
        }
        activeGames.add(Box.createVerticalGlue());
        activeGames.add(new JLabel());
    }

    private JPanel createSingleGamePanel(int gameId, int gameStatus, int gameType, int myColor,
                                         ArrayList<String> players, String encodedBoard){

        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                                BorderFactory.createLoweredBevelBorder()));
        gamePanel.setBackground(Colors.BACKGROUND);

        JPanel gameInfo = new JPanel(new GridLayout(0, 1));
        colorPanel(gameInfo);
        JLabel id = new JLabel("Game ID: " + gameId);
        colorLabel(id);
        gameInfo.add(id);
        JLabel gStatus = new JLabel("Game Status: " + convertGameStatus(gameStatus));
        colorLabel(gStatus);
        gameInfo.add(gStatus);
        JLabel gType = new JLabel("Game Type: " + convertNumberToType(gameType));
        colorLabel(gType);
        gameInfo.add(gType);

        JPanel colorInfo = new JPanel(new GridLayout(0, 1));
        colorPanel(colorInfo);
        JLabel myLabel = new JLabel("My Color: " + convertNumberToColor(myColor));
        colorLabel(myLabel);
        colorInfo.add(myLabel);
        JLabel activeLabel = new JLabel("Active Color: " + convertActiveGameStatus(gameStatus));
        colorLabel(activeLabel);
        colorInfo.add(activeLabel);

        JPanel playerInfo = new JPanel(new GridLayout(0, 1));
        colorPanel(playerInfo);
        JLabel playerLabel = new JLabel("Other Players: ");
        colorLabel(playerLabel);
        playerInfo.add(playerLabel);
        for (String playerName : players){
            if (!playerName.equals(username)){
                JLabel nameLabel = new JLabel(playerName);
                colorLabel(nameLabel);
                playerInfo.add(nameLabel);
            }
        }


        JButton play = new JButton("Open");
        if (gameStatus == 0){
            play.setText("Exit");
        }else if (gameStatus == 7){
            play.setText("Study");
        }
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStatus == 0){
                    //exit game
                    client.exitGame(gameId);
                }else {
                    HashMap<Integer, String> playerNames = convertPlayerNames(gameType, players);
                    disposeLargest();
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            largest = new Largest(encodedBoard, myColor, playerNames, client, gameId);
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    });
                }
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.30;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        gamePanel.add(gameInfo, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.30;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        gamePanel.add(colorInfo, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.30;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        gamePanel.add(playerInfo, c);

        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 0.30;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 10, 10, 10);
        gamePanel.add(play, c);

        return gamePanel;
    }

    private HashMap<Integer, String> convertPlayerNames(int gameType, ArrayList<String> players){
        HashMap<Integer, String> playerNames = new HashMap<>();
        if (gameType == 0){
            playerNames.put(1, players.get(0));
            playerNames.put(2, players.get(1));
        }else if (gameType == 1){
            playerNames.put(1, players.get(0));
            playerNames.put(3, players.get(1));
        }else if (gameType == 2){
            playerNames.put(1, players.get(0));
            playerNames.put(4, players.get(1));
        }else if (gameType == 3){
            playerNames.put(1, players.get(0));
            playerNames.put(3, players.get(1));
            playerNames.put(5, players.get(2));
        }else if (gameType == 4){
            playerNames.put(2, players.get(0));
            playerNames.put(3, players.get(1));
            playerNames.put(4, players.get(2));
            playerNames.put(5, players.get(3));
        }else if (gameType == 5){
            playerNames.put(1, players.get(0));
            playerNames.put(2, players.get(1));
            playerNames.put(3, players.get(2));
            playerNames.put(5, players.get(3));
            playerNames.put(6, players.get(4));
        }else if (gameType == 6){
            playerNames.put(1, players.get(0));
            playerNames.put(2, players.get(1));
            playerNames.put(3, players.get(2));
            playerNames.put(4, players.get(3));
            playerNames.put(5, players.get(4));
            playerNames.put(6, players.get(5));
        }
        return playerNames;
    }

    private String convertActiveGameStatus(int gameStatus){
        if (gameStatus > 0 && gameStatus < 7){
            return convertNumberToColor(gameStatus);
        }else {
            return "NA";
        }
    }

    private String convertGameStatus(int gameStatus){
        if (gameStatus == 0){
            return "Impending";
        }else if (gameStatus == 7){
            return "History";
        }else{
            return "Active";
        }
    }

    private String convertNumberToColor(int number){
        if (number == -1){
            return "NA";
        }else if (number == 1){
            return "Red";
        }else if (number == 2){
            return "Orange";
        }else if (number == 3){
            return "Yellow";
        }else if (number == 4){
            return "Green";
        }else if (number == 5){
            return "Blue";
        }else{
            //number == 6
            return "Purple";
        }
    }

    private void colorPanel(JPanel panel){
        panel.setBackground(Colors.BACKGROUND);
    }

    private void colorLabel(JLabel label){
        label.setBackground(Colors.BACKGROUND);
        label.setForeground(Colors.YELLOW);
        //FONT??
    }

    private String convertNumberToType(int number){
        if (number == 0){
            return "Neighbors";
        }else if(number == 1){
            return "Angle";
        }else{
            return number + " Player";
        }
    }

    private int convertTypeToNumber(int gameType){
        if (gameType < 3){
            return 2;
        }else{
            return gameType;
        }
    }

    private JPanel createNewGamePanel(){
        JPanel newGame = new JPanel(new GridBagLayout());
        newGame.setBackground(Colors.BACKGROUND);

        JLabel sign = new JLabel("New Game");
        sign.setBackground(Colors.BACKGROUND);
        sign.setForeground(Colors.YELLOW);
        Font bigFont = new Font("Serif", Font.BOLD, 23);
        sign.setFont(bigFont);

        ArrayList<JRadioButton> buttons = new ArrayList<>();
        ButtonGroup group = new ButtonGroup();

        JRadioButton neighbors = new JRadioButton("Neighbors");
        shapeRadio(neighbors);
        group.add(neighbors);
        buttons.add(neighbors);

        JRadioButton angle = new JRadioButton("Angle");
        shapeRadio(angle);
        group.add(angle);
        buttons.add(angle);

        JRadioButton two = new JRadioButton("2 Player");
        shapeRadio(two);
        group.add(two);
        buttons.add(two);

        JRadioButton three = new JRadioButton("3 Player");
        three.setSelected(true);
        shapeRadio(three);
        group.add(three);
        buttons.add(three);

        JRadioButton four = new JRadioButton("4 Player");
        shapeRadio(four);
        group.add(four);
        buttons.add(four);

        JRadioButton five = new JRadioButton("5 Player");
        shapeRadio(five);
        group.add(five);
        buttons.add(five);

        JRadioButton six = new JRadioButton("6 Player");
        shapeRadio(six);
        group.add(six);
        buttons.add(six);

        JButton play = new JButton("Enter Fray");
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton type = new JRadioButton();
                for (JRadioButton t : buttons){
                    if (t.isSelected()){
                        type = t;
                    }
                }
                int gameType;
                if (type == neighbors){
                    gameType = 0;
                }else if(type == angle) {
                    gameType = 1;
                }else if(type == two) {
                    gameType = 2;
                }else if(type == three) {
                    gameType = 3;
                }else if(type == four) {
                    gameType = 4;
                }else if(type == five) {
                    gameType = 5;
                }else {
                    //type == 6
                    gameType = 6;
                }
                client.newGame(gameType);
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weighty = 0.2;
        //c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 5, 10);
        newGame.add(sign, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        newGame.add(neighbors, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        newGame.add(angle, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        newGame.add(two, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.5;
        c.weighty = 0.2;
        //c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 10, 10, 5);
        newGame.add(play, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 10);
        newGame.add(three, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 10);
        newGame.add(four, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 10);
        newGame.add(five, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 10, 10);
        newGame.add(six, c);

        return newGame;
    }

    private void shapeRadio(JRadioButton btn){
        btn.setBackground(Colors.BACKGROUND);
        btn.setForeground(Colors.YELLOW);
        btn.setFocusPainted(false);
        Font littleFont = new Font("Serif", Font.BOLD, 17);
        btn.setFont(littleFont);
    }

    void disposeLargest(){
        if (largest != null){
            largest.dispose();
        }
    }

    public void notConnected(){
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        flag.setText("Not Connected to Server");
        Timer timer = new Timer(5000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        flag.setText("");
                    }
                });
            }
        });
        timer.start();
        if (largest != null){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    largest.notConnected();
                }
            });
        }
    }

    public void updateStatus(ArrayList<String> status){
        this.status = status;
        update();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    public void updateGame(ArrayList<String> info){
        System.out.println(info);
        if (largest != null){
            System.out.println(largest.getId());
        }
        if (largest != null && largest.getId() == Integer.parseInt(info.get(0))){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    largest.updateEngine(info.get(1));
                }
            });
        }
    }
}
