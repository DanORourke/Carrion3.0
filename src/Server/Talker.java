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
            System.out.println(input);
            ArrayList<String> ask = new ArrayList<>(Arrays.asList(input.split(",")));
            processAsk(ask);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processAsk(ArrayList<String> ask){
        if (ask.get(0).equals("newUser")){
            ask.remove(0);
            createNewUser(ask);
        }else{
            String username = ask.get(0);
            String password = ask.get(1);
            if (db.validUser(username, password)){
                ask.remove(0);
                ask.remove(0);
                String request = ask.get(0);
                if (request.equals("status")){
                    send("Empty");
                }else if (request.equals("newGame")){

                }else if (request.equals("submitOrders")){

                }else if (request.equals("updateGame")){

                }
            }else{
                send("Invalid");
            }
        }
    }

    private void send(String message){
        out.println(message);
    }

    private void createNewUser(ArrayList<String> info){
        if (info.size()== 2 && db.createNewUser(info.get(0), info.get(1))){
            send("Valid");
        }else{
            send("Invalid");
        }
    }
}
