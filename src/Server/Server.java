package Server;

import Engine.Engine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
        // timer to clear out players who wait to long to move
        scheduleClearSquatters();
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



    private void scheduleClearSquatters(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                clearSquatters();
            }
        }, 1000, 24*60*60*1000);

    }

    private void clearSquatters(){
        int waitTime = 7*24*60*60;
        //find all the squatters
        HashMap<Integer, HashMap<String, String>> squatters = db.getSquatters(waitTime);
        //make squatters abscond
        for (Integer id : squatters.keySet()){
            HashMap<String, String> game = squatters.get(id);
            Engine engine = new Engine(game.get("BOARD"), Integer.parseInt(game.get("STATUS")));
            engine.addEncodedTurn("next,abscond,next");
            String newEncoded = engine.getLatestEncoded();
            int playerTurn = engine.getPlayerTurn();
            db.updateSquatGame(id, playerTurn, newEncoded, waitTime);
            //tell anyone playing that game
            for (String key : game.keySet()){
                if(!key.equals("BOARD") && !key.equals("STATUS")){
                    if (talkers.containsKey(key)){
                        //maybe shouldn't send update to largest because player could update in between two db calls
                        talkers.get(key).getStatus();
                        talkers.get(key).send("gameUpdate;" + id + ";next,abscond,next");
                    }
                }
            }
        }
    }
}
