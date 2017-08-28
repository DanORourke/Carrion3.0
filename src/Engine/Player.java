package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import Engine.Piece.Town;
import GUI.Coords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    boolean canGeneralAdd(General g) {
        return g.canAdd() && !connectedFullTowns(g).isEmpty();
    }

    private ArrayList<Piece> connectedFullTowns(General g){
        ArrayList<Piece> connectedPieces = new ArrayList<>();
        for (Coords c : pieces.keySet()) {
            Piece p = pieces.get(c);

            if (((p.getType() == 6) && ((Town) p).hasTroop()) ||
                    ((p.getType() == 7) && (((Capitol) p).getTroops() > 0))) {
                List<Piece> frontier = new ArrayList<>();
                List<Piece> visited = new ArrayList<>();
                visited.add(p);
                frontier.add(p);

                while (!frontier.isEmpty()) {
                    Piece current = frontier.get(frontier.size()-1);
                    for (Coords k : pieces.keySet()){
                        Piece bigBag = pieces.get(k);
                        //System.out.println("new big bag");
                        if ((!visited.contains(bigBag))&&(areNeighbors(bigBag, current)) ) {
                            frontier.add(bigBag);
                        }
                    }
                    visited.add(current);
                    frontier.remove(current);
                }

                if (visited.contains(g)){
                    connectedPieces.add(p);
                }
            }
        }
        return connectedPieces;
    }

    private boolean areNeighbors(Piece p1, Piece p2){
        Coords c1 = p1.getCoords();
        Coords c2 = p2.getCoords();
        int q1 = c1.getQ();
        int r1 = c1.getR();
        int s1 = c1.getS();
        int q2 = c2.getQ();
        int r2 = c2.getR();
        int s2 = c2.getS();
        return (((q1 == q2) && ((r1 + 1) == r2) && ((s1 - 1) == s2)) ||
                ((q1 == q2) && ((r1 - 1) == r2) && ((s1 + 1) == s2)) ||
                (((q1 + 1) == q2) && (r1 == r2) && ((s1 - 1) == s2)) ||
                (((q1 - 1) == q2) && (r1 == r2) && ((s1 + 1) == s2)) ||
                (((q1 + 1) == q2) && ((r1 - 1) == r2) && (s1 == s2)) ||
                (((q1 - 1) == q2) && ((r1 + 1) == r2) && (s1 == s2)));
    }

    Piece getConnectedTroopGiver(General g){
        return connectedFullTowns(g).get(0);
    }

    boolean canGeneralSubtract(General g) {
        return g.canSubtract() && !connectedTowns(g).isEmpty();
    }

    private ArrayList<Piece> connectedTowns(General g){
        ArrayList<Piece> connectedPieces = new ArrayList<>();
        for (Coords c : pieces.keySet()) {
            Piece p = pieces.get(c);

            if ((p.getType() == 6)  ||
                    (p.getType() == 7)) {
                List<Piece> frontier = new ArrayList<>();
                List<Piece> visited = new ArrayList<>();
                visited.add(p);
                frontier.add(p);

                while (!frontier.isEmpty()) {
                    Piece current = frontier.get(frontier.size()-1);
                    for (Coords k : pieces.keySet()){
                        Piece bigBag = pieces.get(k);
                        //System.out.println("new big bag");
                        if ((!visited.contains(bigBag))&&(areNeighbors(bigBag, current)) ) {
                            frontier.add(bigBag);
                        }
                    }
                    visited.add(current);
                    frontier.remove(current);
                }

                if (visited.contains(g)){
                    connectedPieces.add(p);
                }
            }
        }
        return connectedPieces;
    }
//
//    Piece getConnectedTroopReceiver(General g){
//        return connectedTowns(g).get(0);
//    }

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
