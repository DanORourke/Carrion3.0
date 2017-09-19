package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

class Talker implements Runnable{
    private final DB db;
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    Talker(DB db, Socket socket){
        this.db = db;
        this.socket = socket;
    }

    public void run(){
        try {
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        listen();
    }

    private void listen(){
        try {
            String input = in.readLine();
            System.out.println("listening: " + input);
            ArrayList<String> ask = new ArrayList<>(Arrays.asList(input.split(";")));
            processAsk(ask);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String message){
        System.out.println("sending: " + message);
        out.println(message);
    }

    private void processAsk(ArrayList<String> ask){
        if (ask.size() > 0 && ask.get(0).equals("newUser")){
            ask.remove(0);
            createNewUser(ask);
        }else if (ask.size() > 2){
            String username = ask.get(0);
            String password = ask.get(1);
            if (db.validUser(username, password)){
                ask.remove(0);
                ask.remove(0);
                String request = ask.get(0);
                ask.remove(0);
                if (request.equals("status")){
                    getStatus(username);
                }else if (request.equals("newGame")){
                    newGame(username, ask);
                }else if (request.equals("exitGame")){
                    exitGame(username, ask);
                }else if (request.equals("submitOrders")){
                    submitOrders(username, ask);
                }
            }else{
                send("Invalid");
            }
        }
    }

    private void createNewUser(ArrayList<String> info){
        if (info.size()== 2 && db.createNewUser(info.get(0), info.get(1))){
            send("Empty");
        }else{
            send("Invalid");
        }
    }

    private void getStatus(String username){
        send(db.getStatus(username));
        //send("1;2;6;1;q;w;e;r;t;y;21,6;1;2;6;1;q;w;e;r;t;y;21,6;1;2;6;1;q;w;e;r;t;y;21,6;1;2;6;1;q;w;e;r;t;y;21,6");
    }

    private void newGame(String username, ArrayList<String> ask){
        if (ask.size() == 1 && isInteger(ask.get(0)) && db.newGame(username, Integer.parseInt(ask.get(0)))){
            getStatus(username);
        }else {
            send("Invalid");
        }
    }

    private void exitGame(String username, ArrayList<String> ask){
        if (ask.size() == 1 && isInteger(ask.get(0)) && db.exitGame(username, Integer.parseInt(ask.get(0)))){
            getStatus(username);
        }else {
            send("Invalid");
        }
    }

    private void submitOrders(String username, ArrayList<String> ask){
        if (ask.size() == 2 && isInteger(ask.get(0))){
            send(db.submitOrders(username, Integer.parseInt(ask.get(0)), ask.get(1)));
        }else {
            send("Invalid");
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
