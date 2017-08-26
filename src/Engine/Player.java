package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import GUI.Coords;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    private final Alliance alliance;
    private final HashMap<Coords, Piece> pieces = new HashMap<>();

    Player(Alliance alliance){
        this.alliance = alliance;
    }

//    private void initializePieces(){
//        //create slot for every kind of piece
//        pieces.put(0, new HashMap<>());
//        pieces.put(1, new HashMap<>());
//        pieces.put(2, new HashMap<>());
//        pieces.put(3, new HashMap<>());
//        pieces.put(4, new HashMap<>());
//        pieces.put(5, new HashMap<>());
//        pieces.put(6, new HashMap<>());
//        pieces.put(7, new HashMap<>());
//        pieces.put(8, new HashMap<>());
//    }

    void addPiece(Piece p){
        Coords c = p.getCoords();
        pieces.put(c, p);
    }

    void removePiece(Piece p){
        Coords c = p.getCoords();
        pieces.remove(c);
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
        removePiece(g);
        addPiece(addedG);
    }

    boolean willMoveChief(){
        Piece[] movePieces = moveChief();
        if (movePieces.length != 2){
            System.out.println("length != 2");
            return false;
        }
        Piece hasChief = movePieces[0];
        Piece wantsChief = movePieces[1];
        return areConnected(hasChief, wantsChief);

    }

    Piece[] moveChief(){
        Piece wantsChief = null;
        Piece hasChief = null;
        for (Coords c : pieces.keySet()){
            Piece p = pieces.get(c);
            if ((p.getType() > 0 && p.getType() < 6)){
                General g = (General)p;
                if (g.wantsChief()){
                    wantsChief = g;
                }else if (g.hasChief()){
                    hasChief = g;
                }
            }else if (p.getType() == 7){
                Capitol cap = (Capitol)p;
                if (cap.wantsChief()){
                    wantsChief = cap;
                }else if (cap.hasChief()){
                    hasChief = cap;
                }
            }
        }
        //System.out.println((wantsChief != null) + " " + (hasChief != null));
        if (wantsChief != null && hasChief != null){
            return new Piece[]{hasChief, wantsChief};
        }else{
            return new Piece[0];
        }
    }

    ArrayList<Piece> getChiefWanters(){
        ArrayList<Piece> wanters = new ArrayList<>();
        for (Coords c : pieces.keySet()){
            Piece p = pieces.get(c);
            if ((p.getType() > 0 && p.getType() < 6)){
                General g = (General)p;
                if (g.wantsChief()){
                    wanters.add(g);
                }
            }else if (p.getType() == 7){
                Capitol cap = (Capitol)p;
                if (cap.wantsChief()){
                    wanters.add(cap);
                }
            }
        }
        return wanters;
    }

    private boolean areConnected(Piece a, Piece b){
        //TODO this
        return true;
    }
}
