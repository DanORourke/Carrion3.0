package GUI;

import Server.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

class Lobby {
    private final String username;
    private final String password;
    private final String ip;
    private final String port;
    private String status;
    private JFrame frame;
    private JPanel activeGames;

    Lobby(String username, String password, String ip, String port, String status){
        this.username = username;
        //not ideal
        this.password = password;
        this.ip = ip;
        this.port = port;
        this.status = status;
        this.frame = new JFrame("Carrion");
        setFrame();
        prepareFrame();
    }

    private void setFrame(){
        frame.setSize(700, 400);
        frame.setResizable(false);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
    }

    private void prepareFrame(){
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Colors.BACKGROUND);
        JPanel title = createTitlePanel();
        createActiveGames();
        JScrollPane scroll = new JScrollPane(activeGames);
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
                status = new Client(username, password, ip, port).signIn();
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
                    createActiveGames();
                    frame.revalidate();
                    frame.repaint();
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

    private JPanel createActiveGames(){
        activeGames = new JPanel(new GridLayout(0, 1));
        activeGames.setBackground(Colors.BACKGROUND);
        if (status.equals("Empty")){
            JLabel sign = new JLabel(username + " is a coward");
            sign.setBackground(Colors.BACKGROUND);
            sign.setForeground(Colors.YELLOW);
            activeGames.add(sign);
            return activeGames;
        }
        JLabel sign = new JLabel("Battles:");
        sign.setBackground(Colors.BACKGROUND);
        sign.setForeground(Colors.YELLOW);
        activeGames.add(sign);

        ArrayList<String> info =  new ArrayList<>(Arrays.asList(status.split(",")));
        int i = 0;
        while (i > info.size()){
            int gameId = Integer.parseInt(info.get(i));
            String gameStatus = info.get(i+1);
            int gameType = Integer.parseInt(info.get(i+2));
            int myColor = Integer.parseInt(info.get(i+3));
            int activeColor = Integer.parseInt(info.get(i+4));
            i+=5;
            int numberOfPlayers = convertTypeToNumber(gameType);
            int total = i + numberOfPlayers;
            ArrayList<String> players = new ArrayList<>();
            while (i < total){
                players.add(info.get(i));
                i++;
            }
            String encodedBoard = info.get(i);
            i++;
            JPanel panel = createSingleGamePanel(gameId, gameStatus, gameType, myColor,
                    activeColor, players, encodedBoard);
            activeGames.add(panel);
        }
        return activeGames;
    }

    private JPanel createSingleGamePanel(int gameId, String gameStatus, int gameType, int myColor,
                                         int activeColor, ArrayList<String> players, String encodedBoard){

        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(Colors.BACKGROUND);
        return gamePanel;
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

        JRadioButton neighbors = new JRadioButton("2 Player Neighbors");
        neighbors.setBackground(Colors.BACKGROUND);
        neighbors.setForeground(Colors.YELLOW);
        Font littleFont = new Font("Serif", Font.BOLD, 17);
        neighbors.setFont(littleFont);
        group.add(neighbors);
        buttons.add(neighbors);

        JRadioButton angle = new JRadioButton("2 Player Angle");
        angle.setBackground(Colors.BACKGROUND);
        angle.setForeground(Colors.YELLOW);
        angle.setFont(littleFont);
        group.add(angle);
        buttons.add(angle);

        JRadioButton two = new JRadioButton("2 Player");
        two.setBackground(Colors.BACKGROUND);
        two.setForeground(Colors.YELLOW);
        two.setFont(littleFont);
        group.add(two);
        buttons.add(two);

        JRadioButton three = new JRadioButton("3 Player");
        three.setSelected(true);
        three.setBackground(Colors.BACKGROUND);
        three.setForeground(Colors.YELLOW);
        three.setFont(littleFont);
        group.add(three);
        buttons.add(three);

        JRadioButton four = new JRadioButton("4 Player");
        four.setBackground(Colors.BACKGROUND);
        four.setForeground(Colors.YELLOW);
        four.setFont(littleFont);
        group.add(four);
        buttons.add(four);

        JRadioButton five = new JRadioButton("5 Player");
        five.setBackground(Colors.BACKGROUND);
        five.setForeground(Colors.YELLOW);
        five.setFont(littleFont);
        group.add(five);
        buttons.add(five);

        JRadioButton six = new JRadioButton("6 Player");
        six.setBackground(Colors.BACKGROUND);
        six.setForeground(Colors.YELLOW);
        six.setFont(littleFont);
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
}
