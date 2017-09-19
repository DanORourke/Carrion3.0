package GUI;

import Server.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class Lobby {
    private final HashMap<String, String> networkInfo;
    private final String username;
    private String status;
    private JFrame frame;
    private JPanel activeGames;

    Lobby(HashMap<String, String> networkInfo, String status){
        this.networkInfo = networkInfo;
        this.username = networkInfo.get("username");
        this.status = status;
        this.frame = new JFrame("Carrion");
        setFrame();
        prepareFrame();
    }

    private void setFrame(){
        frame.setSize(900, 600);
        frame.setResizable(true);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
    }

    private void prepareFrame(){
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Colors.BACKGROUND);
        JPanel title = createTitlePanel();
        createActiveGames();
        JScrollPane scroll = new JScrollPane(activeGames);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        JPanel newGame = createNewGamePanel();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        main.add(title, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.8;
        c.fill = GridBagConstraints.BOTH;
        main.add(scroll, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.8;
        c.fill = GridBagConstraints.VERTICAL;
        main.add(newGame, c);

        frame.add(main);
        frame.revalidate();
    }

    private JPanel createTitlePanel(){
        JPanel title = new JPanel(new GridBagLayout());
        title.setBackground(Colors.BACKGROUND);
        JLabel welcome = new JLabel("Welcome to Carrion " + username);
        welcome.setBackground(Colors.BACKGROUND);
        welcome.setForeground(Colors.YELLOW);
        Font font = new Font("Serif", Font.BOLD, 35);
        welcome.setFont(font);

        JLabel buttonWork = new JLabel();
        buttonWork.setBackground(Colors.BACKGROUND);

        JButton refresh = new JButton("Refresh Page");
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status = new Client(networkInfo).signIn();
                update();
                if (status.equals("Invalid")){
                    buttonWork.setForeground(Colors.RED);
                    buttonWork.setText("Invalid");
                    Timer timer = new Timer(5000, new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonWork.setText("");
                        }
                    });
                    timer.start();
                }else {
                    buttonWork.setForeground(Colors.YELLOW);
                    buttonWork.setText("Valid");
                    Timer timer = new Timer(5000, new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonWork.setText("");
                        }
                    });
                    timer.start();
                }
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 10, 10, 10);
        title.add(welcome, c);

        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 0.25;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 10, 10, 10);
        title.add(buttonWork, c);

        c.gridx = 2;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 0.25;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 10, 10, 10);
        title.add(refresh, c);

        return title;
    }

    private void createActiveGames(){
        activeGames = new JPanel(new GridLayout(0, 1, 0, 50));
        activeGames.setBackground(Colors.BACKGROUND);
        updateActiveGames();
    }

    private void update(){
        if (!status.equals("Invalid")){
            updateActiveGames();
            frame.revalidate();
            frame.repaint();
        }
    }

    private void updateActiveGames(){
        activeGames.removeAll();
        if (status.equals("Empty")){
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
        signPanel.setBackground(Colors.BACKGROUND);
        JLabel sign = new JLabel(" Battles");
        sign.setBackground(Colors.BACKGROUND);
        sign.setForeground(Colors.YELLOW);
        sign.setFont(new Font("Serif", Font.BOLD, 23));
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        signPanel.add(sign, c);
        activeGames.add(signPanel);

        ArrayList<String> info =  new ArrayList<>(Arrays.asList(status.split(";")));
        int i = 0;
        while (i < info.size()){
            int gameId = Integer.parseInt(info.get(i));
            int gameType = Integer.parseInt(info.get(i+1));
            int gameStatus = Integer.parseInt(info.get(i+2));
            i+=3;
            int numberOfPlayers = convertTypeToNumber(gameType);
            int total = i + numberOfPlayers;
            ArrayList<String> players = new ArrayList<>();
            String encodedBoard = "NA";
            int myColor = -1;
            if (gameStatus != 0){
                myColor = Integer.parseInt(info.get(i));
                i++;
                while (i < total + 1){
                    players.add(info.get(i));
                    i++;
                }
                encodedBoard = info.get(i);
                i++;
            }

            JPanel panel = createSingleGamePanel(gameId, gameStatus, gameType, myColor, players, encodedBoard);
            activeGames.add(panel);
        }
    }

    private JPanel createSingleGamePanel(int gameId, int gameStatus, int gameType, int myColor,
                                         ArrayList<String> players, String encodedBoard){

        JPanel gamePanel = new JPanel(new GridLayout(1, 0, 0, 0));
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
        gamePanel.add(gameInfo);

        JPanel colorInfo = new JPanel(new GridLayout(0, 1));
        colorPanel(colorInfo);
        JLabel myLabel = new JLabel("My Color: " + convertNumberToColor(myColor));
        colorLabel(myLabel);
        colorInfo.add(myLabel);
        JLabel activeLabel = new JLabel("Active Color: " + convertActiveGameStatus(gameStatus));
        colorLabel(activeLabel);
        colorInfo.add(activeLabel);
        gamePanel.add(colorInfo);

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
        gamePanel.add(playerInfo);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        colorPanel(buttonPanel);
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
                    status = new Client(networkInfo).exitGame(gameId);
                    update();
                }else {
                    HashMap<Integer, String> playerNames = convertPlayerNames(gameType, players);
                    new Largest(encodedBoard, myColor, playerNames, networkInfo, gameId);
                }
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(play, c);
        gamePanel.add(buttonPanel);

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
            playerNames.put(3, players.get(2));
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
            return "History Lesson";
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
                status = new Client(networkInfo).newGame(gameType);
                update();
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
}
