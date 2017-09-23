package Server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import Engine.Engine;

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
            String sql = "SELECT * FROM USER WHERE USERNAME = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.isBeforeFirst()){
                while (rs.next()){
                    user.addAll(Arrays.asList(rs.getString("USERNAME"),
                            rs.getString("SALT"), rs.getString("HASH")));
                }
            }
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(user);
        return user;
    }

    synchronized boolean createNewUser(String username, String password) {
        ArrayList<String> user = getUserInfo(username);
        if (user == null || !user.isEmpty() || username.equals("") ||
                username.equals("newUser") || password.equals("") || username.length() > 9) {
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

            String sql = "INSERT INTO USER (USERNAME,SALT,HASH) " +
                    "VALUES (?, '" + salt + "', '" + hash + "');";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            pstmt.close();
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
            String sql ="SELECT * FROM GAME WHERE PLAYER1 = ? OR PLAYER2 = ? OR PLAYER3 = ? " +
                                "OR PLAYER4 = ? OR PLAYER5 = ? OR PLAYER6 = ? ORDER BY ID DESC LIMIT 10;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            pstmt.setString(3, username);
            pstmt.setString(4, username);
            pstmt.setString(5, username);
            pstmt.setString(6, username);

            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()){
                pstmt.close();
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
                        status += ";" + players.get(color);
                    }
                    status += ";" + board;
                }
            }
            pstmt.close();
            rs.close();
            return status;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Empty";
    }

    synchronized ArrayList<String> newGame(String username, int gameType){
        if (gameType == 0){
            return newNeighbors(username);
        }else if (gameType == 1) {
            return newAngle(username);
        } else if (gameType == 2){
            return new2(username);
        }else if (gameType == 3){
            return new3(username);
        }else if (gameType == 4){
            return new4(username);
        }else if (gameType == 5){
            return new5(username);
        }else{
            //gameType = 6
            return new6(username);
        }
    }

    private synchronized HashMap <String, String> getNewGame(int type){
        HashMap <String, String> game = new HashMap<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM GAME WHERE TYPE = " + type + " AND STATUS = 0;");
            if (rs.isBeforeFirst()){
                game.put("ID", rs.getString("ID"));
                String player1 = rs.getString("PLAYER1");
                if (player1 != null){
                    game.put("PLAYER1", player1);
                }
                String player2 = rs.getString("PLAYER2");
                if (player2 != null){
                    game.put("PLAYER2", player2);
                }
                String player3 = rs.getString("PLAYER3");
                if (player3 != null){
                    game.put("PLAYER3", player3);
                }
                String player4 = rs.getString("PLAYER4");
                if (player4 != null){
                    game.put("PLAYER4", player4);
                }
                String player5 = rs.getString("PLAYER5");
                if (player5 != null){
                    game.put("PLAYER5", player5);
                }
                String player6 = rs.getString("PLAYER6");
                if (player6 != null){
                    game.put("PLAYER6", player6);
                }
            }
            stmt.close();
            rs.close();
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return game;
    }

    private synchronized void addPlayer(String username, int gameId, String place){
        try {
            String sql = "UPDATE GAME SET " + place + " = ? WHERE ID = ?";

            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setInt(2, gameId);
            pstmt.executeUpdate();
            pstmt.close();
            c.commit();
            System.out.println("addPlayer");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized void startGame(String username, int type, String place){
        try {
            String sql = "INSERT INTO GAME (TYPE, STATUS, " + place + ") VALUES (?, 0, ?);";

            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, type);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            pstmt.close();
            c.commit();
            System.out.println("newGame started " + username + " " + type + " " + place);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized void beginGame(int id, int type){
        String encoded = "21," + String.valueOf(type);
        ArrayList<String> info = new Engine(encoded, 0).getInfo();
        try {
            Statement stmt = c.createStatement();
            String sql = "UPDATE GAME SET STATUS = " + Integer.parseInt(info.get(0)) + ", " +
                    "BOARD = '" + info.get(1) + "' WHERE ID = " + id + ";";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized ArrayList<String> newNeighbors(String username){
        ArrayList<String> players = new ArrayList<>();
        int type = 0;
        try {
            HashMap <String, String> newGame = getNewGame(type);

            if (newGame.isEmpty()){
                startGame(username, type, "PLAYER1");
            }

            int id = Integer.parseInt(newGame.get("ID"));
            String player1 = newGame.get("PLAYER1");
            String player2 = newGame.get("PLAYER2");


            boolean entered = false;

            if (player1 == null && (player2 == null || !player2.equals(username))){
                addPlayer(username, id, "PLAYER1");
                entered = true;
                player1 = username;

            }else if (player2 == null && (!player1.equals(username))){
                addPlayer(username, id, "PLAYER2");
                entered = true;
                player2 = username;
            }

            if (entered && player1 != null && player2 != null){
                beginGame(id, type);
                players.add(player1);
                players.add(player2);
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return players;
    }

    private synchronized ArrayList<String> newAngle(String username){
        ArrayList<String> players = new ArrayList<>();
        int type = 1;

        try {
            HashMap <String, String> newGame = getNewGame(type);

            if (newGame.isEmpty()){
                startGame(username, type, "PLAYER1");
            }

            int id = Integer.parseInt(newGame.get("ID"));
            String player1 = newGame.get("PLAYER1");
            String player3 = newGame.get("PLAYER3");


            boolean entered = false;

            if (player1 == null && (player3 == null || !player3.equals(username))){
                addPlayer(username, id, "PLAYER1");
                entered = true;
                player1 = username;
            }else if (player3 == null && (!player1.equals(username))){
                addPlayer(username, id, "PLAYER3");
                entered = true;
                player3 = username;
            }

            if (entered && player1 != null && player3 != null){
                beginGame(id, type);
                players.add(player1);
                players.add(player3);
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return players;
    }

    private synchronized ArrayList<String> new2(String username){
        ArrayList<String> players = new ArrayList<>();
        int type = 2;
        try {
            HashMap <String, String> newGame = getNewGame(type);

            if (newGame.isEmpty()){
                startGame(username, type, "PLAYER1");
            }

            int id = Integer.parseInt(newGame.get("ID"));
            String player1 = newGame.get("PLAYER1");
            String player4 = newGame.get("PLAYER4");


            boolean entered = false;

            if (player1 == null && (player4 == null || !player4.equals(username))){
                addPlayer(username, id, "PLAYER1");
                entered = true;
                player1 = username;
            }else if (player4 == null && (!player1.equals(username))){
                addPlayer(username, id, "PLAYER4");
                entered = true;
                player4 = username;
            }

            if (entered && player1 != null && player4 != null){
                beginGame(id, type);
                players.add(player1);
                players.add(player4);
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return players;
    }

    private synchronized ArrayList<String> new3(String username){
        ArrayList<String> players = new ArrayList<>();
        int type = 3;
        try {
            HashMap <String, String> newGame = getNewGame(type);

            if (newGame.isEmpty()){
                startGame(username, type, "PLAYER1");
            }

            int id = Integer.parseInt(newGame.get("ID"));
            String player1 = newGame.get("PLAYER1");
            String player3 = newGame.get("PLAYER3");
            String player5 = newGame.get("PLAYER5");


            boolean entered = false;

            if (player1 == null && (player3 == null || !player3.equals(username)) &&
                    (player5 == null || !player5.equals(username))){
                addPlayer(username, id, "PLAYER1");
                entered = true;
                player1 = username;
            }else if (player3 == null && (player1 == null ||!player1.equals(username)) &&
                    (player5 == null || !player5.equals(username))){
                addPlayer(username, id, "PLAYER3");
                entered = true;
                player3 = username;
                System.out.println("newGame entered");
            }else if (player5 == null && (player1 == null ||!player1.equals(username)) &&
                    (player3 == null || !player3.equals(username))){
                addPlayer(username, id, "PLAYER5");
                entered = true;
                player5 = username;
            }

            if (entered && player1 != null && player3 != null && player5 != null){
                beginGame(id, type);
                players.add(player1);
                players.add(player3);
                players.add(player5);
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return players;
    }

    private synchronized ArrayList<String> new4(String username){
        ArrayList<String> players = new ArrayList<>();
        int type = 4;
        try {
            HashMap <String, String> newGame = getNewGame(type);

            if (newGame.isEmpty()){
                startGame(username, type, "PLAYER2");

            }

            int id = Integer.parseInt(newGame.get("ID"));
            String player2 = newGame.get("PLAYER2");
            String player3 = newGame.get("PLAYER3");
            String player5 = newGame.get("PLAYER5");
            String player6 = newGame.get("PLAYER6");


            boolean entered = false;

            if (player2 == null && (player3 == null || !player3.equals(username)) &&
                    (player5 == null || !player5.equals(username)) && (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER2");
                entered = true;
                player2 = username;
            }else if (player3 == null && (player2 == null ||!player2.equals(username)) &&
                    (player5 == null || !player5.equals(username)) && (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER3");
                entered = true;
                player3 = username;
            }else if (player5 == null && (player2 == null ||!player2.equals(username)) &&
                    (player3 == null || !player3.equals(username)) && (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER5");
                entered = true;
                player5 = username;
            }else if (player6 == null && (player2 == null ||!player2.equals(username)) &&
                    (player3 == null || !player3.equals(username)) && (player5 == null || !player5.equals(username))){
                addPlayer(username, id, "PLAYER6");
                entered = true;
                player6 = username;
            }

            if (entered && player2 != null && player3 != null && player5 != null && player6 != null){
                beginGame(id, type);
                players.add(player2);
                players.add(player3);
                players.add(player5);
                players.add(player6);
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return players;
    }

    private synchronized ArrayList<String> new5(String username){
        ArrayList<String> players = new ArrayList<>();
        int type = 5;
        try {
            HashMap <String, String> newGame = getNewGame(type);

            if (newGame.isEmpty()){
                startGame(username, type, "PLAYER1");

            }

            int id = Integer.parseInt(newGame.get("ID"));
            String player1 = newGame.get("PLAYER1");
            String player2 = newGame.get("PLAYER2");
            String player3 = newGame.get("PLAYER3");
            String player5 = newGame.get("PLAYER5");
            String player6 = newGame.get("PLAYER6");


            boolean entered = false;

            if (player1 == null && (player2 == null || !player2.equals(username)) &&
                    (player3 == null || !player3.equals(username)) && (player5 == null || !player5.equals(username)) &&
                    (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER1");
                entered = true;
                player1 = username;
            }else if (player2 == null && (player1 == null ||!player1.equals(username)) &&
                    (player3 == null || !player3.equals(username)) && (player5 == null || !player5.equals(username)) &&
                    (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER2");
                entered = true;
                player2 = username;
            }else if (player3 == null && (player1 == null ||!player1.equals(username)) &&
                    (player2 == null || !player2.equals(username)) && (player5 == null || !player5.equals(username)) &&
                    (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER3");
                entered = true;
                player3 = username;
            }else if (player5 == null && (player1 == null ||!player1.equals(username)) &&
                    (player2 == null || !player2.equals(username)) && (player3 == null || !player3.equals(username)) &&
                    (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER5");
                entered = true;
                player5 = username;
            }else if (player6 == null && (player1 == null ||!player1.equals(username)) &&
                    (player2 == null || !player2.equals(username)) && (player3 == null || !player3.equals(username)) &&
                    (player5 == null || !player5.equals(username))){
                addPlayer(username, id, "PLAYER6");
                entered = true;
                player6 = username;
            }

            if (entered && player1 != null && player2 != null && player3 != null &&
                    player5 != null && player6 != null){
                beginGame(id, type);
                players.add(player1);
                players.add(player2);
                players.add(player3);
                players.add(player5);
                players.add(player6);
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return players;
    }

    private synchronized ArrayList<String> new6(String username){
        ArrayList<String> players = new ArrayList<>();
        int type = 6;
        try {
            HashMap <String, String> newGame = getNewGame(type);

            if (newGame.isEmpty()){
                startGame(username, type, "PLAYER1");
            }

            int id = Integer.parseInt(newGame.get("ID"));
            String player1 = newGame.get("PLAYER1");
            String player2 = newGame.get("PLAYER2");
            String player3 = newGame.get("PLAYER3");
            String player4 = newGame.get("PLAYER4");
            String player5 = newGame.get("PLAYER5");
            String player6 = newGame.get("PLAYER6");

            boolean entered = false;

            if (player1 == null && (player2 == null || !player2.equals(username)) &&
                    (player3 == null || !player3.equals(username)) && (player4 == null || !player4.equals(username)) &&
                    (player5 == null || !player5.equals(username)) && (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER1");
                entered = true;
                player1 = username;
            }else if (player2 == null && (player1 == null || !player1.equals(username)) &&
                    (player3 == null || !player3.equals(username)) && (player4 == null || !player4.equals(username)) &&
                    (player5 == null || !player5.equals(username)) && (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER2");
                entered = true;
                player2 = username;
            }else if (player3 == null && (player1 == null || !player1.equals(username)) &&
                    (player2 == null || !player2.equals(username)) && (player4 == null || !player4.equals(username)) &&
                    (player5 == null || !player5.equals(username)) && (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER3");
                entered = true;
                player3 = username;
            }else if (player4 == null && (player1 == null || !player1.equals(username)) &&
                    (player2 == null || !player2.equals(username)) && (player3 == null || !player3.equals(username)) &&
                    (player5 == null || !player5.equals(username)) && (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER4");
                entered = true;
                player4 = username;
            }else if (player5 == null && (player1 == null || !player1.equals(username)) &&
                    (player2 == null || !player2.equals(username)) && (player3 == null || !player3.equals(username)) &&
                    (player4 == null || !player4.equals(username)) && (player6 == null || !player6.equals(username))){
                addPlayer(username, id, "PLAYER5");
                entered = true;
                player5 = username;
            }else if (player6 == null && (player1 == null || !player1.equals(username)) &&
                    (player2 == null || !player2.equals(username)) && (player3 == null || !player3.equals(username)) &&
                    (player4 == null || !player4.equals(username)) && (player5 == null || !player5.equals(username))){
                addPlayer(username, id, "PLAYER6");
                entered = true;
                player6 = username;
            }

            if (entered && player1 != null && player2 != null && player3 != null &&
                    player4 != null && player5 != null && player6 != null){
                beginGame(id, type);
                players.add(player1);
                players.add(player2);
                players.add(player3);
                players.add(player4);
                players.add(player5);
                players.add(player6);
            }
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return players;
    }

    synchronized boolean exitGame(String username, int id){
        try {
            String sql = "UPDATE GAME SET " +
                    "PLAYER1 = " +
                    "CASE " +
                    "When PLAYER1 = ? THEN NULL ELSE PLAYER1 END, " +
                    "PLAYER2 = " +
                    "CASE " +
                    "When PLAYER2 = ? THEN NULL ELSE PLAYER2 END, " +
                    "PLAYER3 = " +
                    "CASE " +
                    "When PLAYER3 = ? THEN NULL ELSE PLAYER3 END, " +
                    "PLAYER4 = " +
                    "CASE " +
                    "When PLAYER4 = ? THEN NULL ELSE PLAYER4 END, " +
                    "PLAYER5 = " +
                    "CASE " +
                    "When PLAYER5 = ? THEN NULL ELSE PLAYER5 END, " +
                    "PLAYER6 = " +
                    "CASE " +
                    "When PLAYER6 = ? THEN NULL ELSE PLAYER6 END " +
                    "WHERE ID = ? AND STATUS = 0";

            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            pstmt.setString(3, username);
            pstmt.setString(4, username);
            pstmt.setString(5, username);
            pstmt.setString(6, username);
            pstmt.setInt(7, id);

            pstmt.executeUpdate();
            pstmt.close();
            c.commit();
            System.out.println("exitGame " + id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    synchronized ArrayList<String> submitOrders(String username, int id, String orders){
        ArrayList<String> results = new ArrayList<>();
        HashMap <String, String> game = getGame(id);
        //check if its my turn
        if (!myTurn(username, game)){
            return results;
        }
        String reallyOld = game.get("BOARD");
        Engine engine = new Engine(game.get("BOARD"), Integer.parseInt(game.get("STATUS")));
        engine.addEncodedTurn(orders);
        String oldEncoded = engine.getLatestEncoded();
        engine.nextPhase(true);
        String newEncoded = engine.getLatestEncoded();
        int playerTurn = engine.getPlayerTurn();
        int oldLength = oldEncoded.length();
        int reallyOldLength = reallyOld.length();
        updateGame(id, playerTurn, newEncoded);
        results.add(newEncoded.substring(oldLength + 1));
        results.add(newEncoded.substring(reallyOldLength + 1));
        if (game.containsKey("PLAYER1")){
            results.add(game.get("PLAYER1"));
        }if (game.containsKey("PLAYER2")){
            results.add(game.get("PLAYER2"));
        }if (game.containsKey("PLAYER3")){
            results.add(game.get("PLAYER3"));
        }if (game.containsKey("PLAYER4")){
            results.add(game.get("PLAYER4"));
        }if (game.containsKey("PLAYER5")){
            results.add(game.get("PLAYER5"));
        }if (game.containsKey("PLAYER6")){
            results.add(game.get("PLAYER6"));
        }
        return results;
    }

    private synchronized void updateGame(int id, int playerTurn, String newEncoded){
        try {
            Statement stmt = c.createStatement();
            String sql = "UPDATE GAME SET STATUS = " + playerTurn + ", " +
                    "BOARD = '" + newEncoded + "' WHERE ID = " + id + ";";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean myTurn(String username, HashMap <String, String> game){
        int turn = Integer.parseInt(game.get("STATUS"));
        if (turn == 1){
            return username.equals(game.get("PLAYER1"));
        }else if (turn == 2){
            return username.equals(game.get("PLAYER2"));
        }else if (turn == 3){
            return username.equals(game.get("PLAYER3"));
        }else if (turn == 4){
            return username.equals(game.get("PLAYER4"));
        }else if (turn == 5){
            return username.equals(game.get("PLAYER5"));
        }else {
            return turn == 6 && username.equals(game.get("PLAYER6"));
        }
    }

    private synchronized HashMap <String, String> getGame(int id){
        HashMap <String, String> game = new HashMap<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM GAME WHERE ID = " + id + ";");
            if (rs.isBeforeFirst()){
                game.put("ID", rs.getString("ID"));
                game.put("TYPE", rs.getString("TYPE"));
                game.put("STATUS", rs.getString("STATUS"));
                String player1 = rs.getString("PLAYER1");
                if (player1 != null){
                    game.put("PLAYER1", player1);
                }
                String player2 = rs.getString("PLAYER2");
                if (player2 != null){
                    game.put("PLAYER2", player2);
                }
                String player3 = rs.getString("PLAYER3");
                if (player3 != null){
                    game.put("PLAYER3", player3);
                }
                String player4 = rs.getString("PLAYER4");
                if (player4 != null){
                    game.put("PLAYER4", player4);
                }
                String player5 = rs.getString("PLAYER5");
                if (player5 != null){
                    game.put("PLAYER5", player5);
                }
                String player6 = rs.getString("PLAYER6");
                if (player6 != null){
                    game.put("PLAYER6", player6);
                }
                game.put("BOARD", rs.getString("BOARD"));
            }
            stmt.close();
            rs.close();
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return game;
    }
}
