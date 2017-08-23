package Engine;

import Engine.Piece.General.General;
import Engine.Piece.Piece;
import Engine.Piece.Supply;
import GUI.Coords;
import GUI.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    //private final int gameType;
    //private final int mapRadius;
    private HashMap<Coords, Parcel> board;
    private HashMap<Coords, GameData> changeData = new HashMap<>();

    Board(int gameType, int mapRadius){
        //this.gameType = gameType;
        //this.mapRadius = mapRadius;
        createBoard(mapRadius);
        populateBoard(gameType);
    }

    Board(Board board1){
        this.board = new HashMap<>();
        HashMap<Coords, Parcel> oldBoard = board1.getTotalBoard();
        for (Coords c : oldBoard.keySet()){
            Coords c2 = new Coords(c);
            Parcel p2 = new Parcel(oldBoard.get(c));
            board.put(c2, p2);
        }
    }

    private void createBoard(int mapRadius){
        board = new HashMap<>();
        board.put(new Coords(0, 0, 0), new Parcel());
        for (int i = 1; i < mapRadius; i++) {
            board.put(new Coords(i, -i, 0), new Parcel());
            board.put(new Coords(-i, i, 0), new Parcel());
            board.put(new Coords(i, 0, -i), new Parcel());
            board.put(new Coords(-i, 0, i), new Parcel());
            board.put(new Coords(0, i, -i), new Parcel());
            board.put(new Coords(0, -i, i), new Parcel());
            for (int k = 1; k < i; k++) {
                board.put(new Coords(-i, k, (i - k)), new Parcel());
                board.put(new Coords(i, -k, -(i - k)), new Parcel());
                board.put(new Coords(k, -i, (i - k)), new Parcel());
                board.put(new Coords(-k, i, -(i - k)), new Parcel());
                board.put(new Coords(-k, -(i - k), i), new Parcel());
                board.put(new Coords(k, (i - k), -i), new Parcel());
            }
        }
        System.out.println(board.size() + " " + mapRadius);
    }

    HashMap<Coords, Parcel> getTotalBoard(){
        return board;
    }

    private void populateBoard(int gameType){
        setUnoccupiedTowns();
        ArrayList<Alliance>  players = new ArrayList<>();
        if (gameType == 0){
            players.add(Alliance.RED);
            players.add(Alliance.ORANGE);
        }else if (gameType == 1){
            players.add(Alliance.RED);
            players.add(Alliance.YELLOW);
        }else if (gameType == 2){
            players.add(Alliance.RED);
            players.add(Alliance.GREEN);
        }else if (gameType == 3){
            players.add(Alliance.RED);
            players.add(Alliance.YELLOW);
            players.add(Alliance.BLUE);
        }else if (gameType == 4){
            players.add(Alliance.YELLOW);
            players.add(Alliance.ORANGE);
            players.add(Alliance.BLUE);
            players.add(Alliance.PURPLE);
        }else if (gameType == 5){
            players.add(Alliance.RED);
            players.add(Alliance.YELLOW);
            players.add(Alliance.ORANGE);
            players.add(Alliance.BLUE);
            players.add(Alliance.PURPLE);
        }else if (gameType == 6){
            players.add(Alliance.RED);
            players.add(Alliance.YELLOW);
            players.add(Alliance.GREEN);
            players.add(Alliance.ORANGE);
            players.add(Alliance.BLUE);
            players.add(Alliance.PURPLE);
        }
        for (Alliance a : players){
            setPlayer(a);
        }
    }

    private void updateMap(HashMap<Coords, Parcel> parcels){
        for (Coords c: parcels.keySet()){
            board.put(c, parcels.get(c));
        }
    }

    void clearChangeData(){
        changeData.clear();
    }

    HashMap<Coords, GameData> getChangeData(){
        return changeData;
    }

    private void setUnoccupiedTowns(){
        HashMap<Coords, Parcel> parcels = Alliance.UNOCCUPIED.getInitialSetup();
        updateMap(parcels);
    }

    private void setPlayer(Alliance a){
        HashMap<Coords, Parcel> parcels = a.getInitialSetup();
        for (Coords c : board.keySet()){
            if (a.inMyTerritory(c)){
                if (parcels.containsKey(c)){
                    Parcel p = parcels.get(c);
                    HashMap<Integer, Piece> pieces = p.getPieces();
                    parcels.put(c, new Parcel(a, pieces));
                }else{
                    parcels.put(c, new Parcel(a));
                }
            }
        }
        updateMap(parcels);
    }

    Parcel get(Coords c){
        return board.get(c);
    }

    void moveGeneral(General g, Coords next){
        removePiece(g);
        General gen = g.createNewMoved(next, 1);
        addPiece(gen);
    }

    private void removePiece(Piece p){
        Coords init = p.getCoords();
        Parcel op = board.get(init);
        HashMap<Integer, Piece> oPieces = op.getPieces();
        HashMap<Integer, Piece> nPieces = new HashMap<>();
        for (int t : oPieces.keySet()){
            Piece oPiece = oPieces.get(t);
            if (!p.equals(oPiece)){
                nPieces.put(t, oPiece.copy());
            }else {
                System.out.println("remove at " + oPieces.get(t).getCoords().toString());
            }
        }
        Parcel np =  new Parcel(op.getTerritory(), nPieces);
        board.put(init,np);
        changeData.put(init, np.getGD());
    }

    private void addPiece(Piece p){
        Coords init = p.getCoords();
        Parcel op = board.get(init);
        HashMap<Integer, Piece> oPieces = op.getPieces();
        HashMap<Integer, Piece> nPieces = new HashMap<>();
        for (int t : oPieces.keySet()){
            nPieces.put(t, oPieces.get(t).copy());
        }
        if (p.isGeneral()){
            if (!op.hasGeneral()){
                nPieces.put(1, p.copy());
            }else {
                nPieces.put(2, p.copy());
            }
        }else{
            nPieces.put(p.getType(), p.copy());
        }
        Parcel np =  new Parcel(op.getTerritory(), nPieces);
        board.put(init,np);
        changeData.put(init, np.getGD());
    }

    void dropSupply(General g){
        //addTroops, but really the remove inside it, needs to be first for some reason, equals not working
        Coords c = g.getCoords();
        Supply supply  = new Supply(c, g.getAlliance());
        addPiece(supply);
        addTroops(g, -1);
    }

    void addTroops(General g, int n){
        removePiece(g);
        General gen = g.createNewTroop(n);
        addPiece(gen);
    }
}
