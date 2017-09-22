package Server;

import GUI.Entry;
import GUI.Lobby;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

public class Client {
    private final String username;
    private final Entry entry;
    private final Socket socket;
    private final PrintWriter out;
    private final Ears ears;
    private Lobby lobby;
    private String ping;

    public Client(HashMap<String, String> networkInfo, Entry entry, boolean newUser){
        if (networkInfo.keySet().contains("username")){
            username = networkInfo.get("username");
        }else{
            username = null;
        }
        this.entry = entry;
        this.socket = new Socket();
        int timeout = 5000;
        PrintWriter out1;
        Ears ears1;
        boolean connected = false;
        if (validFormat(networkInfo, newUser)){
            try {
                socket.connect(new InetSocketAddress(networkInfo.get("ip"),
                        Integer.parseInt(networkInfo.get("port"))), timeout);
                out1 = new PrintWriter(socket.getOutputStream(), true);
                ears1 = new Ears(socket, this);
                Thread thread = new Thread(ears1);
                thread.start();
                connected = true;
            } catch (IOException e) {
                e.printStackTrace();
                out1 = null;
                ears1 = null;
            }
        }else{
            out1 = null;
            ears1 = null;
        }
        out = out1;
        ears = ears1;
        if (connected){
            signIn(networkInfo, newUser);
            sendPing();
        }else{
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    entry.clientRejects();
                }
            });
        }
    }

    private void sendPing(){
        send("ping");
        ping = "ping";
        scheduleTestConnection();
    }

    private void scheduleTestConnection(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                testConnection();
            }
        }, 60*1000);
    }

    private void testConnection(){
        if (ping.equals("ping")){
            close();
        }else if (ping.equals("pong")){
            sendPing();
        }
    }

    public void setLobby(Lobby lobby){
        this.lobby = lobby;
    }

    public void close(){
        //tell gui
        System.out.println("close client");
        try {
            if (ears != null){
                ears.setStop();
            }
            if (!socket.isClosed()){
                send("close");
                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            }
            System.out.println("socket closed: "+ socket.isClosed());
        } catch (IOException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void sendClose(){
        if (socket.isConnected()){
            send("close");
        }
    }

    private boolean validFormat(HashMap<String,String> networkInfo, boolean newUser){
        String ip = networkInfo.get("ip");
        String port = networkInfo.get("port");
        String pass = networkInfo.get("password");
        String repeat = networkInfo.get("repeat");

        System.out.println((username == null) + " " + (pass == null) + username + " : " + pass);


        if ((username == null || username.equals("")) || (pass == null || pass.equals("")) ||
                (newUser && (repeat == null || repeat.equals("")) && !(pass.equals(repeat))) ||
                (ip == null || ip.equals("")) || (port == null || port.equals(""))){
            return false;
        }

        ArrayList<String> ipList= new ArrayList<>(Arrays.asList(ip.split("\\.")));
        if (ipList.size() != 4){
            System.out.println(ipList.size() + " bad length");
            return false;
        }
        for (String part : ipList){
            if (!(isInteger(part) && (Integer.parseInt(part) >= 0 && Integer.parseInt(part) <= 255))){
                System.out.println(part + " not right sized number");
                return false;
            }
        }
        return isInteger(port);
    }

    private boolean isInteger(String s) {
        int radix = 10;
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    private void send(String message){
        out.println(message);
    }

    private void signIn(HashMap<String, String> networkInfo, boolean newUser){
        if (newUser){
            send("newUser;" + networkInfo.get("username") +  ";" + networkInfo.get("password"));
        }else {

            send("signIn;" + networkInfo.get("username") +  ";" + networkInfo.get("password"));
        }
    }

    void receive(String message){
        ArrayList<String> info = new ArrayList<>(Arrays.asList(message.split(";")));
        if (info.size() < 1){
            return;
        }
        String label = info.get(0);
        info.remove(0);
        if (label.equals("ping")){
            send("pong");
        }else if (label.equals("pong")){
            ping = "pong";
        }else if (label.equals("close")){
            close();
        }else if (label.equals("newChat")){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    lobby.newChat(message.substring(8));
                }
            });
        }else if (label.equals("signIn")){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    entry.signIn(info);
                }
            });
        }else if (label.equals("status")){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    lobby.updateStatus(info);
                }
            });
        }else if (label.equals("gameUpdate")){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    lobby.updateGame(info);
                }
            });
        }
    }

    public void newGame(int gameType){
        if (socket.isConnected()){
            send("newGame;" + gameType);
        }else{
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    lobby.notConnected();
                }
            });
        }
    }

    public void exitGame(int id){
        if (socket.isConnected()){
            send("exitGame;" + id);
        }else{
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    lobby.notConnected();
                }
            });
        }
    }

    public void submitOrders(int id, String orders){
        if (socket.isConnected()){
            send("submitOrders;" + id + ";" + orders);
        }else{
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    lobby.notConnected();
                }
            });
        }
    }

    public void sendChat(String message){
        if (socket.isConnected()){
            send("sendChat;" + message);
        }
    }

    public String getUsername(){
        return username;
    }
}
