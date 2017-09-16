package GUI;

import Server.Client;

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
        frame.setResizable(false);
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
        JPanel online = createOnlinePanel();
        tabbed.addTab("Online", online);
        JPanel offline = createOfflinePanel();
        tabbed.addTab("Offline", offline);
        tabbed.setOpaque(true);
        tabbed.setBackground(Colors.BACKGROUND);
        tabbed.setForeground(Colors.RED);
        return tabbed;
    }

    private JPanel createOfflinePanel(){
        JPanel offline = new JPanel(new GridBagLayout());
        offline.setBackground(Colors.BACKGROUND);

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

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weighty = 0.2;
        //c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 5, 10);
        offline.add(sign, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        offline.add(neighbors, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        offline.add(angle, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        offline.add(two, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.5;
        c.weighty = 0.2;
        //c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 10, 10, 5);
        offline.add(play, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 10);
        offline.add(three, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 10);
        offline.add(four, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 10);
        offline.add(five, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 10, 10);
        offline.add(six, c);

        return offline;
    }

    private JPanel createOnlinePanel(){
        JPanel online = new JPanel(new GridBagLayout());
        online.setBackground(Colors.BACKGROUND);

        JLabel nameLabel = new JLabel("Username");
        nameLabel.setBackground(Colors.BACKGROUND);
        nameLabel.setForeground(Colors.YELLOW);

        JTextField name = new JTextField();

        JLabel passLabel = new JLabel("Password");
        passLabel.setBackground(Colors.BACKGROUND);
        passLabel.setForeground(Colors.YELLOW);

        JPasswordField pass = new JPasswordField();

        JLabel repeatLabel = new JLabel("Password");
        repeatLabel.setBackground(Colors.BACKGROUND);
        repeatLabel.setForeground(Colors.YELLOW);

        JPasswordField repeatPass = new JPasswordField();

        JLabel info = new JLabel("<html>Enter Ipv4 and Port of Server. " +
                "Official server is initially entered.</html>");
        info.setBackground(Colors.BACKGROUND);
        info.setForeground(Colors.YELLOW);

        JLabel ipLabel = new JLabel("Ipv4");
        JTextField ip = new JTextField("127.0.0.1");
        ipLabel.setBackground(Colors.BACKGROUND);
        ipLabel.setForeground(Colors.YELLOW);

        JLabel portLabel = new JLabel("Port");
        JTextField port = new JTextField("54445");
        portLabel.setBackground(Colors.BACKGROUND);
        portLabel.setForeground(Colors.YELLOW);

        JLabel flag = new JLabel();
        flag.setBackground(Colors.BACKGROUND);
        flag.setForeground(Colors.RED);

        JButton signIn = createSignIn(name, pass, ip, port, flag);
        JButton newUser = createNewUser(name, pass, repeatPass, ip, port, flag);

        GridBagConstraints c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.25;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 5, 5);
        online.add(nameLabel, c);

        c  = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.75;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 5, 5, 10);
        online.add(name, c);

        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.25;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        online.add(passLabel, c);

        c  = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.75;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 10);
        online.add(pass, c);

        c  = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.75;
        c.weighty = 0.125;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 10);
        online.add(signIn, c);

        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.25;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        online.add(repeatLabel, c);

        c  = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 0.75;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 10);
        online.add(repeatPass, c);

        c  = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 0.75;
        c.weighty = 0.125;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 10);
        online.add(newUser, c);

        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        online.add(info, c);

        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0.25;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 5);
        online.add(ipLabel, c);

        c  = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 6;
        c.weightx = 0.75;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 10);
        online.add(ip, c);

        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 7;
        c.weightx = 0.25;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 10, 5);
        online.add(portLabel, c);

        c  = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 7;
        c.weightx = 0.75;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 10, 10);
        online.add(port, c);

        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 2;
        c.weighty = 0.125;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 10, 10);
        online.add(flag, c);

        return online;
    }

    private JButton createSignIn(JTextField name, JPasswordField pass, JTextField ip, JTextField port, JLabel flag){
        JButton signIn = new JButton("Sign In");
        signIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String status = new Client(name.getText(), new String(pass.getPassword()),
                        ip.getText(), port.getText()).signIn();
                if (status.equals("INVALID")){
                    flag.setText("INVALID");
                }
                else {
                    new Lobby(name.getText(), new String(pass.getPassword()), ip.getText(), port.getText(), status);
                }
                name.setText("");
                pass.setText("");
            }
        });
        return signIn;
    }

    private JButton createNewUser(JTextField name, JPasswordField pass, JPasswordField repeatPass,
                                  JTextField ip, JTextField port, JLabel flag){
        JButton newUser = new JButton("New User");
        newUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String status = new Client(name.getText(), new String(pass.getPassword()),
                        ip.getText(), port.getText()).newUser(new String(repeatPass.getPassword()));
                if (status.equals("INVALID")){
                    flag.setText("INVALID");
                }
                else {
                    new Lobby(name.getText(), new String(pass.getPassword()), ip.getText(), port.getText(), status);
                }
                name.setText("");
                pass.setText("");
                repeatPass.setText("");
            }
        });
        return newUser;
    }
}
