package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Client {
    private final String name;
    private final String pass;
    private final String ip;
    private final String port;
    private Socket socket;

    public Client(HashMap<String, String> networkInfo){
        this.name = networkInfo.get("username");
        this.pass = networkInfo.get("password");
        this.ip = networkInfo.get("ip");
        this.port = networkInfo.get("port");
    }

    private void tellSocket(String message){
        socket = new Socket();
        int timeout = 5000;
        try {
            System.out.println(ip + " " + port + " tellSocket: " + message);
            socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), timeout);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String response(){
        try {
            //my server will be super slow, might need to be longer
            socket.setSoTimeout(10000);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String message = in.readLine();
            System.out.println("response: " + message);
            socket.close();
            return message;
        } catch(SocketTimeoutException e){
            System.out.println("socket timed out");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Invalid";
    }

    public String signIn(){
        String status = "Invalid";
        if (!validFormat()){
            System.out.println("bad format " + ip + " " + port);
            return status;
        }
        tellSocket(name + ";" + pass + ";status");
        return response();
    }

    public String newUser(String repeatPass){
        String newUser = "Invalid";
        if (!validFormat() || !repeatPass.equals(pass)){
            System.out.println("bad format " + ip + " " + port + " " + pass + " " + repeatPass);
            return newUser;
        }
        tellSocket("newUser;" + name + ";" + pass);
        return response();
    }

    private boolean validFormat(){
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

    public String newGame(int gameType){
        String message = name + ";" + pass + ";newGame;" + gameType;
        tellSocket(message);
        return response();
    }

    public String exitGame(int id){
        String message = name + ";" + pass + ";exitGame;" + id;
        tellSocket(message);
        return response();
    }

    public String submitOrders(int id, String orders){
        String message = name + ";" + pass + ";submitOrders;" + id + ";" + orders;
        tellSocket(message);
        return response();
    }
}
