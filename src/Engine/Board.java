package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import Engine.Piece.Supply;
import Engine.Piece.Town;
import GUI.Coords;
import GUI.GameData;

import java.util.ArrayList;
import java.util.HashMap;

class Board {
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

    void occupyTown(General g){
        Coords c = g.getCoords();
        Town t = get(c).getTown();
        removePiece(t);
        Town nt  = new Town(c, g.getAlliance());
        addPiece(nt);
        addTroops(g, -1);
        if (!g.getLines()){
            General ng = g.createNewLines();
            General nng = ng.createNewMoved(c, 1);
            removePiece(g);
            addPiece(nng);
        }
    }

    void dropSupply(General g){
        Coords c = g.getCoords();
        Supply supply  = new Supply(c, g.getAlliance());
        addPiece(supply);
        addTroops(g, -1);
        if (!g.getLines()){
            General ng = g.createNewLines();
            General nng = ng.createNewMoved(c, 1);
            removePiece(g);
            addPiece(nng);
        }
    }

    void cutSupply(General g){
        Coords c = g.getCoords();
        Supply s = board.get(c).getSupply();
        removePiece(s);
        if (!g.getLines()){
            General ng = g.createNewLines();
            General nng = ng.createNewMoved(c, 1);
            removePiece(g);
            addPiece(nng);
        }
    }

    void addTroops(General g, int n){
        removePiece(g);
        General gen = g.createNewTroop(n);
        addPiece(gen);
    }

    void setFightingGeneral(General g, Coords launchPoint, boolean dropAfterWin){
        General ng = g.createNewFighting(launchPoint, dropAfterWin);
        removePiece(g);
        addPiece(ng);
    }

    void subtractTroopFromTown(Piece p){
        if (p.getType() == 6){
            Town t = ((Town)p).createNewTroop(false);
            System.out.println("remove troop from town " + ((Town) p).hasTroop() + " " + t.hasTroop());
            removePiece(p);
            //System.out.println(board.get(p.getCoords()).getTown().hasTroop());
            addPiece(t);
            System.out.println(board.get(p.getCoords()).getTown().hasTroop());
        }else{
            System.out.println("remove troop from cap");
            Capitol cap = ((Capitol)p).createNewTroops(-1);
            removePiece(p);
            addPiece(cap);
        }
    }

    void setChiefOrders(General g, boolean wantsChief){
        General ng = g.createNewWantsChief(wantsChief);
        removePiece(g);
        addPiece(ng);
    }

    void setChiefOrders(Capitol cap, boolean wantsChief){
        Capitol ncap = cap.createNewWantsChief(wantsChief);
        removePiece(cap);
        addPiece(ncap);
    }

    void removeChief(Piece p){
        if (p.getType() > 0 && p.getType() < 6){
            setChief((General)p, false);
        }else if (p.getType() == 7){
            setChief((Capitol) p, false);
        }
    }

    void addChief(Piece p){
        if (p.getType() > 0 && p.getType() < 6){
            setChief((General)p, true);
        }else if (p.getType() == 7){
            setChief((Capitol) p, true);
        }
    }

    private void setChief(General g, boolean hasChief){
        General ng = g.createNewHasChief(hasChief);
        removePiece(g);
        addPiece(ng);
    }

    private void setChief(Capitol cap, boolean hasChief){
        Capitol ncap = cap.createNewHasChief(hasChief);
        removePiece(cap);
        addPiece(ncap);
    }

    void setExposedGeneral(General g){
        General ng = g.createNewExposed(true);
        removePiece(g);
        addPiece(ng);
    }

    void resetGeneralMove(General g){
        General ng = g.resetGeneralMove();
        removePiece(g);
        addPiece(ng);
    }

    void resetTownAllocate(Town t) {
        Town nt = t.resetTownAllocate();
        removePiece(t);
        addPiece(nt);
    }

    void resetCapAllocate(Capitol cap) {
        Capitol nCap = cap.resetCapitolAllocate();
        removePiece(cap);
        addPiece(nCap);
    }
}
