package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

class Talker{
    private String username;
    private final DB db;
    private final Socket socket;
    private final PrintWriter out;
    private final Ears ears;
    private final HashMap<String, Talker> talkers;
    private String ping;
    private Timer timer;


    Talker(DB db, Socket socket, HashMap<String, Talker> talkers){
        this.db = db;
        this.socket = socket;
        this.talkers = talkers;
        PrintWriter out1;
        Ears ears1;
        try {
            out1 = new PrintWriter(socket.getOutputStream(), true);
            ears1 = new Ears(socket, this);
        } catch (IOException e) {
            e.printStackTrace();
            out1 = null;
            ears1 = null;
        }
        out = out1;
        ears = ears1;
        Thread thread = new Thread(ears);
        thread.start();
        sendPing();
    }

    private void sendPing(){
        send("ping");
        ping = "ping";
        scheduleTestConnection();
    }

    private void scheduleTestConnection(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                testConnection();
            }
        }, 60*1000);
    }

    private void testConnection(){
        if (!socket.isClosed()){
            if (ping.equals("ping")){
                close();
            }else if (ping.equals("pong")){
                sendPing();
            }
        }
    }

    void close(){
        timer.cancel();
        talkers.remove(username);
        try {
            if (ears != null){
                ears.setStop();
            }
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
            System.out.println("socket closed: "+ socket.isClosed());
        } catch (IOException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println(talkers);
    }

    void send(String message){
        System.out.println("sending: " + message);
        out.println(message);
    }

    void receive(String message){
        ArrayList<String> ask = new ArrayList<>(Arrays.asList(message.split(";")));

        if (ask.size() < 1){
            return;
        }
        String request = ask.get(0);
        ask.remove(0);
        if (request.equals("ping")){
            send("pong");
        }else if (request.equals("pong")){
            ping = "pong";
        }else if (request.equals("sendChat")){
            chat(message.substring(9));
        }else if (request.equals("close")){
            close();
        }else if (request.equals("newUser")){
            createNewUser(ask);
        }else if (request.equals("signIn")){
            signIn(ask);
        }else if (request.equals("status")){
            getStatus();
        }else if (request.equals("newGame")){
            newGame(ask);
        }else if (request.equals("exitGame")){
            exitGame(ask);
        }else if (request.equals("submitOrders")){
            submitOrders(ask);
        }
    }

    private void chat(String message){
        for (String friend: talkers.keySet()){
            talkers.get(friend).send("newChat;" + username + ": " + message);
        }
    }

    private void createNewUser(ArrayList<String> info){
        if (info.size()== 2 && db.createNewUser(info.get(0), info.get(1))){
            this.username = info.get(0);
            talkers.put(username, this);
            send("signIn;Empty");
        }else{
            send("signIn;Invalid");
        }
    }

    private void signIn(ArrayList<String> info){
        if (info.size()== 2 && db.validUser(info.get(0), info.get(1))){
            this.username = info.get(0);
            talkers.put(username, this);
            send("signIn;" + db.getStatus(username));
        }else{
            send("signIn;Invalid");
        }
    }

    void getStatus(){
        send("status;" + db.getStatus(username));
    }

    private void newGame(ArrayList<String> ask){
        if (ask.size() == 1 && isInteger(ask.get(0))){
            ArrayList<String> results = db.newGame(username, Integer.parseInt(ask.get(0)));
            for (String other: results){
                if (talkers.containsKey(other)){
                    talkers.get(other).getStatus();
                }
            }
            getStatus();
        }else {
            send("Invalid");
        }
    }

    private void exitGame( ArrayList<String> ask){
        if (ask.size() == 1 && isInteger(ask.get(0)) && db.exitGame(username, Integer.parseInt(ask.get(0)))){
            getStatus();
        }else {
            send("Invalid");
        }
    }

    private void submitOrders(ArrayList<String> ask){
        if (ask.size() == 2 && isInteger(ask.get(0))){
            int id = Integer.parseInt(ask.get(0));
            ArrayList<String> others = db.submitOrders(username, id, ask.get(1));
            if (!others.isEmpty()){
                String encoded = others.get(0);
                String theirEncoded = others.get(1);
                send("gameUpdate;" + id + ";"+ encoded);
                getStatus();
                int i = 2;
                while (i < others.size()){
                    if (talkers.containsKey(others.get(i)) && !others.get(i).equals(username)){
                        talkers.get(others.get(i)).getStatus();
                        talkers.get(others.get(i)).send("gameUpdate;" + id + ";"+ theirEncoded);
                    }
                    i++;
                }
            }else {
                send("gameUpdate;" + id + ";Invalid");
            }
        }
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
}
