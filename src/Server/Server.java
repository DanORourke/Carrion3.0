package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private final DB db;
    private final ServerSocket server;
    private final HashMap<String, Talker> talkers = new HashMap<>();

    public Server(int port){
        this.db = new DB();
        ServerSocket s;
        try{
            s = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
            s = null;
            System.exit(0);
        }
        server = s;
        listen();
    }

    private void listen(){
        System.out.println("listening");
        while(true){
            try {
                Socket socket = server.accept();
                System.out.println("New Listener");
                new Talker(db, socket, talkers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
