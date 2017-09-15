package Server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
        createMapTable();
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
                    "PLAYER1ID INT, " +
                    "PLAYER2ID INT, " +
                    "PLAYER3ID INT, " +
                    "PLAYER4ID INT, " +
                    "PLAYER5ID INT, " +
                    "PLAYER6ID INT, " +
                    "BOARD TEXT NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            System.out.println("GAME Table created successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    private void createMapTable(){
        try {
            Statement stmt = c.createStatement();
            String sql = "CREATE TABLE MAP " +
                    "(PLAYERID INTEGER NOT NULL, " +
                    "GAMEID INT NOT NULL, " +
                    "PLAYERCOLOR INT NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            System.out.println("MAP Table created successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

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

        if (user == null || !user.isEmpty() || username.equals("") || password.equals("")) {
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
}
