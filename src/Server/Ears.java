package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

class Ears implements Runnable{
    private final BufferedReader in;
    private final Client client;
    private final Talker talker;
    private final boolean server;
    private boolean stop = false;

    Ears(Socket socket, Client client){
        BufferedReader in1;
        try {
            in1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            in1 = null;
        }
        this.in = in1;
        this.client = client;
        this.talker = null;
        this.server = false;
    }

    Ears(Socket socket, Talker talker){
        BufferedReader in1;
        try {
            in1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            in1 = null;
        }
        this.in = in1;
        this.client = null;
        this.talker = talker;
        this.server = true;
    }

    @Override
    public void run() {
        listen();
    }

    private void listen(){
        if (server){
            while(!stop && talker!= null){
                try {
                    String message = in.readLine();
                    if (message != null){
                        System.out.println("hear message: " + message);
                        talker.receive(message);
                    }
                } catch (SocketException e){
                    System.out.println("reset");
                    talker.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    talker.close();
                }
            }
        }else{
            while(!stop && client != null){
                try {
                    String message = in.readLine();
                    if (message != null){
                        System.out.println("hear message: " + message);
                        client.receive(message);
                    }
                } catch (SocketException e){
                    System.out.println("reset");
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    client.close();
                }
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setStop(){
        this.stop = true;
    }
}
