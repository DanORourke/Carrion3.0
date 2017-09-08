package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import Engine.Piece.Town;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final Alliance alliance;
    private final ArrayList<Piece> pieces = new ArrayList<>();

    Player(Alliance alliance){
        this.alliance = alliance;
    }

    void addPiece(Piece p){
        pieces.add(p);
    }

    void removePiece(Piece p){
        pieces.remove(p);
    }

    ArrayList<Piece> getPieces(){
        return pieces;
    }


    boolean canGeneralAdd(General g) {
        return g.canAdd() && !connectedFullTowns(g).isEmpty();
    }

    public int getUnassisgnedTroops(General g){
        ArrayList<Piece> connectedTowns = connectedFullTowns(g);
        int total = 0;
        for (Piece p : connectedTowns) {
            if ((p.getType() == 6) && ((Town) p).hasTroop()) {
                total++;
            }else if ((p.getType() == 7) && ((Capitol) p).getTroops() > 0){
                total+= ((Capitol) p).getTroops();
            }
        }
        return total;
    }

    private ArrayList<Piece> connectedFullTowns(General g){
        ArrayList<Piece> connectedPieces = new ArrayList<>();
        for (Piece p : pieces) {
            if ((((p.getType() == 6) && ((Town) p).hasTroop()) ||
                    ((p.getType() == 7) && (((Capitol) p).getTroops() > 0))) && areConnected(p, g)) {
                connectedPieces.add(p);
            }
        }
        return connectedPieces;
    }

    private boolean areNeighborsOrRoommates(Piece p1, Piece p2){
        return p1.getCoords().isNextTo(p2.getCoords()) || p1.getCoords().equals(p2.getCoords());
    }

    Piece getConnectedTroopGiver(General g){
        return connectedFullTowns(g).get(0);
    }

    boolean canGeneralSubtract(General g) {
        return g.canSubtract() && !connectedTowns(g).isEmpty();
    }

    private ArrayList<Piece> connectedTowns(General g){
        ArrayList<Piece> connectedPieces = new ArrayList<>();
        for (Piece p : pieces) {
            if (((p.getType() == 6)  ||
                    (p.getType() == 7)) && areConnected(p, g) ) {
                connectedPieces.add(p);
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
        for (Piece p : pieces){
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
        for (Piece p : pieces){
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
        List<Piece> frontier = new ArrayList<>();
        List<Piece> visited = new ArrayList<>();
        visited.add(b);
        frontier.add(b);

        while (!frontier.isEmpty()) {
            Piece current = frontier.get(frontier.size()-1);
            for (Piece bigBag : pieces){
                //System.out.println("new big bag");
                if ((!visited.contains(bigBag))&&(areNeighborsOrRoommates(bigBag, current)) ) {
                    frontier.add(bigBag);
                }
            }
            visited.add(current);
            frontier.remove(current);
        }

        return visited.contains(a);
    }

    void resetPlayerPiecesAllocate(Board board){
        for (Piece p : pieces){
            if(p.isTown()){
                board.resetTownAllocate((Town)p);
            }else if (p.isCapitol()){
                board.resetCapAllocate((Capitol)p);
            }
        }
    }

    void resetPlayerPiecesMove(Board board){
        for (Piece p : pieces){
            if (p.isGeneral()){
                board.resetGeneralMove((General)p);
            }
        }
    }

    public Alliance getAlliance() {
        return alliance;
    }

    boolean noGenerals(){
        int gens = 0;
        for (Piece p : pieces){
            if (p.isGeneral()){
                gens ++;
            }
        }
        return gens == 0;
    }
}
