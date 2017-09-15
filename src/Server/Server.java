package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final DB db;
    private ServerSocket listener;

    public Server(int port){
        this.db = new DB();
        setListener(port);
        listen();
    }

    private void setListener(int port){
        try{
            listener = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void listen(){
        System.out.println("listening");
        while(true){
            try {
                Socket socket = listener.accept();
                System.out.println("New Listener");
                Talker talker = new Talker(db, socket);
                Thread thread = new Thread(talker);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
