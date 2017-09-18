package Server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import Engine.Engine;
import GUI.Largest;

class DB {
    private Connection c;

    DB(){
        this.c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Carrion.db");
            c.setAutoCommit(false);
            DatabaseMetaData dm = c.getMetaData();

            System.out.println("Driver name: " + dm.getDriverName());
            System.out.println("Driver version: " + dm.getDriverVersion());
            System.out.println("Product name: " + dm.getDatabaseProductName());
            System.out.println("Product version: " + dm.getDatabaseProductVersion());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Opened database successfully");

        //set up db if first time opened
        initializeDb();
    }

    private void initializeDb(){
        //set up db if first time opened, boolean firstTime affects network procedure
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT name FROM sqlite_master " +
                    "WHERE type='table' ORDER BY name;" );
            if (rs.isBeforeFirst() ) {
                rs.close();
                stmt.close();
                System.out.println("Has data");
            }
            else {
                rs.close();
                stmt.close();
                System.out.println("No data");
                // set up db
                createTables();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createTables(){
        createUserTable();
        createBattleTable();
        //createMapTable();
    }

    private void createUserTable(){
        try {
            Statement stmt = c.createStatement();
            String sql = "CREATE TABLE USER " +
                    "(ID INTEGER PRIMARY KEY, " +
                    "USERNAME TEXT NOT NULL, " +
                    "SALT TEXT NOT NULL, " +
                    "HASH TEXT NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            System.out.println("USER Table created successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    private void createBattleTable(){
        try {
            Statement stmt = c.createStatement();
            String sql = "CREATE TABLE GAME " +
                    "(ID INTEGER PRIMARY KEY, " +
                    "TYPE INT NOT NULL, " +
                    //status, 1-6 = player turn, 0 = waiting for players, 7 = over
                    "STATUS INT NOT NULL, " +
                    "PLAYER1 TEXT, " +
                    "PLAYER2 TEXT, " +
                    "PLAYER3 TEXT, " +
                    "PLAYER4 TEXT, " +
                    "PLAYER5 TEXT, " +
                    "PLAYER6 TEXT, " +
                    "BOARD TEXT)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            System.out.println("GAME Table created successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
//
//    private void createMapTable(){
//        try {
//            Statement stmt = c.createStatement();
//            String sql = "CREATE TABLE MAP " +
//                    "(PLAYERID INTEGER NOT NULL, " +
//                    "GAMEID INT NOT NULL, " +
//                    "PLAYERCOLOR INT NOT NULL)";
//            stmt.executeUpdate(sql);
//            stmt.close();
//            c.commit();
//            System.out.println("MAP Table created successfully");
//        } catch ( Exception e ) {
//            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//        }
//    }

    private synchronized ArrayList<String> getUserInfo(String username){
        ArrayList<String> user = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM USER WHERE USERNAME = '" + username + "';" );
            if (rs.isBeforeFirst()){
                while (rs.next()){
                    user.addAll(Arrays.asList(rs.getString("USERNAME"),
                            rs.getString("SALT"), rs.getString("HASH")));
                }
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    synchronized boolean createNewUser(String username, String password) {
        ArrayList<String> user = getUserInfo(username);

        if (user == null || !user.isEmpty() || username.equals("") ||
                username.equals("newUser") || password.equals("")) {
            return false;
        }else{
            ArrayList<String> newUser = createSaltnHash(password);
            addUser(username, newUser.get(0), newUser.get(1));
            return true;
        }
    }

    private synchronized ArrayList<String> createSaltnHash(String password){
        ArrayList<String> saltnHash = new ArrayList<>();
        Random rand = new Random();
        String salt = String.valueOf(rand.nextInt(10));
        int i;
        for (i = 0; i < 33; i++){
            salt = salt + String.valueOf(rand.nextInt(10));
        }
        saltnHash.add(salt);
        String saltedPassword = password + salt;
        String hash = createHash(saltedPassword);
        saltnHash.add(hash);
        return saltnHash;
    }

    private String createHash(String text){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(text.getBytes());

        byte byteData[] = md.digest();
        return convertByteToHex(byteData);
    }

    private String convertByteToHex(byte[] byteData){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    private synchronized void addUser(String username, String salt, String hash){
        try {
            Statement stmt = c.createStatement();

            String sql = "INSERT INTO USER (USERNAME,SALT,HASH) " +
                    "VALUES ('" + username + "', '" + salt + "', '" + hash + "');";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            System.out.println("User added");
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    synchronized boolean validUser(String username, String password){
        ArrayList<String> user = getUserInfo(username);

        if (user == null || user.isEmpty()) {
            return false;
        }else{
            String recordName = user.get(0);
            String recordSalt = user.get(1);
            String recordHash = user.get(2);
            String hashedPass = createHash(password + recordSalt);
            return (username.equals(recordName) && recordHash.equals(hashedPass));
        }
    }

    synchronized String getStatus(String username){
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM GAME WHERE PLAYER1 = '" + username + "' " +
                                "OR PLAYER2 = '" + username + "' OR PLAYER3 = '" + username + "' " +
                                "OR PLAYER4 = '" + username + "' OR PLAYER5 = '" + username + "' " +
                                "OR PLAYER6 = '" + username + "' ORDER BY ID ASC LIMIT 10;" );
            if (!rs.isBeforeFirst()){
                stmt.close();
                rs.close();
                return "Empty";
            }
            String status = "";
            boolean first = true;
            while (rs.next()){
                int id = rs.getInt("ID");
                int type = rs.getInt("TYPE");
                int gameStatus = rs.getInt("STATUS");
                HashMap<Integer, String> players = new HashMap<>();
                String player1 = rs.getString("PLAYER1");
                if (player1 != null){
                    players.put(1, player1);
                }
                String player2 = rs.getString("PLAYER2");
                if (player2 != null){
                    players.put(2, player2);
                }
                String player3 = rs.getString("PLAYER3");
                if (player3 != null){
                    players.put(3, player3);
                }
                String player4 = rs.getString("PLAYER4");
                if (player4 != null){
                    players.put(4, player4);
                }
                String player5 = rs.getString("PLAYER5");
                if (player5 != null){
                    players.put(5, player5);
                }
                String player6 = rs.getString("PLAYER6");
                if (player6 != null){
                    players.put(6, player6);
                }

                String board = rs.getString("BOARD");

                if (first){
                    status = String.valueOf(id);
                    first = false;
                }else {
                    status += ";" + String.valueOf(id);
                }
                status += ";" + String.valueOf(type);
                status += ";" + String.valueOf(gameStatus);
                if (gameStatus != 0){
                    for (Integer color : players.keySet()){
                        if (players.get(color).equals(username)){
                            status += ";" + String.valueOf(color);
                        }
                    }
                    for (Integer color : players.keySet()){
                        if (!players.get(color).equals(username)){
                            status += ";" + players.get(color);
                        }
                    }
                    status += ";" + board;
                }

            }
            stmt.close();
            rs.close();
            return status;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Empty";
    }

    synchronized boolean newGame(String username, int gameType){
        if (gameType == 0){
            return newNeighbors(username);
        }
        return false;
//        else if (gameType == 1){
//            return newAngle(username);
//        }else if (gameType == 2){
//            return new2(username);
//        }else if (gameType == 3){
//            return new3(username);
//        }else if (gameType == 4){
//            return new4(username);
//        }else if (gameType == 5){
//            return new5(username);
//        }else{
//            //gameType = 6
//            return new6(username);
//        }
    }

    private synchronized boolean newNeighbors(String username){
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM GAME WHERE TYPE = 0 AND STATUS = 0;");
            if (!rs.isBeforeFirst()){
                stmt = c.createStatement();
                String sql = "INSERT INTO GAME (TYPE, STATUS, PLAYER1) VALUES (0, 0, '" + username + "');";
                stmt.executeUpdate(sql);
                stmt.close();
                c.commit();
                rs.close();
                System.out.println("newGame started");
                return true;
            }

            int id = rs.getInt("ID");
            String player1 = rs.getString("PLAYER1");
            String player2 = rs.getString("PLAYER2");
            stmt.close();
            rs.close();

            boolean entered = false;

            if (player1 == null && (player2 == null || !player2.equals(username))){
                stmt = c.createStatement();
                String sql = "UPDATE GAME SET PLAYER1 = '" + username + "' WHERE ID = " + id + ";";
                stmt.executeUpdate(sql);
                stmt.close();
                c.commit();
                entered = true;
                player1 = username;
                System.out.println("newGame entered");
            }else if (player2 == null && (!player1.equals(username))){
                stmt = c.createStatement();
                String sql = "UPDATE GAME SET PLAYER2 = '" + username + "' WHERE ID = " + id + ";";
                stmt.executeUpdate(sql);
                stmt.close();
                c.commit();
                entered = true;
                player2 = username;
                System.out.println("newGame entered");
            }

            if (entered && player1 != null && player2 != null){
                ArrayList<String> info = new Engine("21,0").getInfo();
                System.out.println(info);
                stmt = c.createStatement();
                String sql = "UPDATE GAME SET STATUS = " + Integer.parseInt(info.get(0)) + ", " +
                                "BOARD = '" + info.get(1) + "' WHERE ID = " + id + ";";
                stmt.executeUpdate(sql);
                stmt.close();
                c.commit();
                System.out.println("newGame entered");
            }
            if (entered){
                return true;
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return false;
    }
}
