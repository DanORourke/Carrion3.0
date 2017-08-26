package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import GUI.GameData;

import java.util.HashMap;

public class Parcel {
    private final Alliance territory;
    private final HashMap<Integer, Piece> pieces = new HashMap<>();

    Parcel(){
        this.territory = Alliance.UNOCCUPIED;
    }

    Parcel(Alliance home, HashMap<Integer, Piece> ps){
        this.territory = home;
        for (int p : ps.keySet()){
            addPiece(ps.get(p));
        }
    }

    Parcel(Parcel oldParcel){
        this.territory = oldParcel.getTerritory();
        HashMap<Integer, Piece> oPieces = oldParcel.getPieces();
        for (int t : oPieces.keySet()){
            addPiece(oPieces.get(t).copy());
        }
    }

    Parcel(Alliance home, Piece[] ps){
        this.territory = home;
        for (Piece p : ps){
            addPiece(p);
        }
    }

    Parcel(Piece[] ps){
        this.territory = Alliance.UNOCCUPIED;
        for (Piece p : ps){
            addPiece(p);
        }
    }

    Parcel(Alliance home){
        this.territory = home;
    }

    public Parcel(Alliance home, Piece p){
        this.territory = home;
        addPiece(p);

    }

    Parcel(Piece p){
        this.territory = Alliance.UNOCCUPIED;
        addPiece(p);
    }

    private void addPiece(Piece p){
        if (p.isGeneral()){
            if (!pieces.containsKey(1)){
                pieces.put(1, p);
            }else {
                pieces.put(2, p);
            }
        }else{
            pieces.put(p.getType(), p);
        }
    }

    boolean isEmpty() {
        return getNumberPieces() == 0;
    }

    private int getNumberPieces(){
        return pieces.size();
    }

    GameData getGD(){
        int[] hold = new int[16];
        int j = 0;
        if (territory != Alliance.UNOCCUPIED){
            hold[0] = 10;
            j++;
        }
        if (isFieldBattle()){
            hold[j] = 9;
            j++;
            hold[j] = getFirstGeneral().getAlliance().getDataCode();
            j++;
            hold[j] = getSecondGeneral().getAlliance().getDataCode();
            j++;
        }else{
            for (int t : pieces.keySet()){
                Piece p = pieces.get(t);
                hold[j] = p.getType();
                j++;
                hold[j] = p.getAlliance().getDataCode();
                j++;
                if (p.isGeneral()){
                    General g = (General)p;
                    if (g.hasChief()){
                        hold[j] = 8;
                        j++;
                        hold[j] = g.getAlliance().getDataCode();
                        j++;
                    }
                }else if (p.isCapitol()){
                    Capitol c = (Capitol) p;
                    if (c.hasChief()){
                        hold[j] = 8;
                        j++;
                        hold[j] = c.getAlliance().getDataCode();
                        j++;
                    }
                }
            }
        }
        int[] data = new int[j];
        System.arraycopy(hold, 0, data, 0, j);
        return new GameData(data);
    }

    private boolean isFieldBattle(){
        //return true if parcel contains more than one general;
        return (pieces.containsKey(1) && pieces.containsKey(2));
    }

    HashMap<Integer, Piece> getPieces() {
        return pieces;
    }

    Alliance getTerritory() {
        return territory;
    }

    boolean hasGeneral(){
        return (pieces.containsKey(1) || pieces.containsKey(2));
    }

    boolean hasSingleGeneral(){
        return (pieces.containsKey(1) && !pieces.containsKey(2));
    }

    General getFirstGeneral(){
        //return first general
        if (pieces.containsKey(1)){
            return (General) pieces.get(1);
        }
        //catch exception??
        return null;
    }

    General getSecondGeneral(){
        //return second general
        if (pieces.containsKey(2)){
            return (General) pieces.get(2);
        }
        //catch exception??
        return null;
    }

    Capitol getCapitol(){
        //return second capitol
        if (pieces.containsKey(7)){
            return (Capitol) pieces.get(7);
        }
        //catch exception??
        return null;
    }

    boolean hasSupplyLine() {
        return pieces.containsKey(0);
    }
}
