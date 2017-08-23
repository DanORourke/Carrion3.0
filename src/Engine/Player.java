package Engine;

import Engine.Piece.General.General;
import Engine.Piece.Piece;
import GUI.Coords;
import java.util.HashMap;

public class Player {
    private final Alliance alliance;
    private final HashMap<Integer, HashMap<Coords, Piece>> pieces = new HashMap<>();

    Player(Alliance alliance){
        this.alliance = alliance;
        initializePieces();
    }

    private void initializePieces(){
        //create slot for every kind of piece
        pieces.put(0, new HashMap<>());
        pieces.put(1, new HashMap<>());
        pieces.put(2, new HashMap<>());
        pieces.put(3, new HashMap<>());
        pieces.put(4, new HashMap<>());
        pieces.put(5, new HashMap<>());
        pieces.put(6, new HashMap<>());
        pieces.put(7, new HashMap<>());
        pieces.put(8, new HashMap<>());
    }

    void addPiece(Piece p){
        int type = p.getType();
        Coords c = p.getCoords();
        pieces.get(type).put(c, p);
    }

    void removePiece(Piece p){
        int type = p.getType();
        Coords c = p.getCoords();
        pieces.get(type).remove(c);
    }

    boolean canGeneralAdd(Coords c) {
        //TODO
        return true;
    }

    boolean canGeneralSubtract(Coords c) {
        //TODO
        return true;
    }

    void addTroops(General g, int n) {
        General addedG = g.createNewTroop(n);
        addPiece(addedG);
    }
}
