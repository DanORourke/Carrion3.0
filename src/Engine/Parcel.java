package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import Engine.Piece.Supply;
import Engine.Piece.Town;
import GUI.GameData;

import java.util.HashMap;

public class Parcel {
    private final Alliance territory;
    private final HashMap<Integer, Piece> pieces = new HashMap<>();
    //type; 0 = supply, 1 = first gen, 2 = second gen, 6 = town,
    // 7 = capitol

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

    Parcel(Alliance home){
        this.territory = home;
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
        }else if (isDefendedTownBattle()){
            Town t = getTown();
            hold[j] = t.getType();
            j++;
            hold[j] = t.getAlliance().getDataCode();
            j++;
            hold[j] = 9;
            j++;
            hold[j] = getFirstGeneral().getAlliance().getDataCode();
            j++;
            hold[j] = getSecondGeneral().getAlliance().getDataCode();
            j++;
        }else if (isDefendedCapitolBattle()){
            Capitol cap = getCapitol();
            hold[j] = cap.getType();
            j++;
            hold[j] = cap.getAlliance().getDataCode();
            j++;
            hold[j] = 9;
            j++;
            hold[j] = getFirstGeneral().getAlliance().getDataCode();
            j++;
            hold[j] = getSecondGeneral().getAlliance().getDataCode();
            j++;
        }else{
            //make towns first
            for (int t : pieces.keySet()){
                Piece p = pieces.get(t);
                if (p.isTown()){
                    hold[j] = p.getType();
                    j++;
                    hold[j] = p.getAlliance().getDataCode();
                    j++;
                }
            }
            for (int t : pieces.keySet()){
                Piece p = pieces.get(t);
                if (!p.isTown()){
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
        }
        int[] data = new int[j];
        System.arraycopy(hold, 0, data, 0, j);
        return new GameData(data);
    }

    boolean isBattle(){
        return ((pieces.containsKey(1) && pieces.containsKey(2)) ||
                ((pieces.containsKey(1) && pieces.containsKey(6)) &&
                        (!getFirstGeneral().getAlliance().equals(getTown().getAlliance()) &&
                        !getTown().getAlliance().equals(Alliance.UNOCCUPIED))) ||
                (pieces.containsKey(1) && pieces.containsKey(7) &&
                        (!getFirstGeneral().getAlliance().equals(getCapitol().getAlliance()) &&
                                !getCapitol().getAlliance().equals(Alliance.UNOCCUPIED))));
    }

    boolean isGeneralBattle(){
        return (pieces.containsKey(1) && pieces.containsKey(2));
    }

    boolean isFieldBattle(){
        //return true if parcel contains more than one general and no town or capitol;
        return (pieces.containsKey(1) && pieces.containsKey(2) && !pieces.containsKey(6) && !pieces.containsKey(7));
    }

    boolean isDefendedTownBattle(){
        return (pieces.containsKey(1) && pieces.containsKey(2) && pieces.containsKey(6));
    }

    boolean isDefendedCapitolBattle(){
        return (pieces.containsKey(1) && pieces.containsKey(2) && pieces.containsKey(7));
    }

    boolean isTownBattle(){
        return (pieces.containsKey(1) && !pieces.containsKey(2) && pieces.containsKey(6) &&
        !pieces.get(6).getAlliance().equals(pieces.get(1).getAlliance()) &&
                !pieces.get(6).getAlliance().equals(Alliance.UNOCCUPIED));
    }

    boolean isCapitolBattle(){
        return (pieces.containsKey(1) && !pieces.containsKey(2) && pieces.containsKey(7) &&
                !pieces.get(7).getAlliance().equals(pieces.get(1).getAlliance()) &&
                !pieces.get(7).getAlliance().equals(Alliance.UNOCCUPIED));
    }

    HashMap<Integer, Piece> getPieces() {
        return pieces;
    }

    public Alliance getTerritory() {
        return territory;
    }

    boolean hasGeneral(){
        return (pieces.containsKey(1) || pieces.containsKey(2));
    }

    boolean hasSingleGeneral(){
        return (pieces.containsKey(1) && !pieces.containsKey(2));
    }

    boolean hasTown(){
        return pieces.containsKey(6);
    }

    boolean hasCapitol(){
        return pieces.containsKey(7);
    }

    General getAllianceGeneral(Alliance a){
        General g1 = getFirstGeneral();
        if (g1 != null && g1.getAlliance().equals(a)){
            return g1;
        }
        General g2 = getSecondGeneral();
        if (g2 != null && g2.getAlliance().equals(a)){
            return g2;
        }
        return null;
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

    Town getTown(){
        if (pieces.containsKey(6)){
            return (Town) pieces.get(6);
        }
        return null;
    }

    Supply getSupply(){
        if (pieces.containsKey(0)){
            return (Supply) pieces.get(0);
        }
        return null;
    }

    boolean hasSupplyLine() {
        return pieces.containsKey(0);
    }

    boolean hasOnlySupply(){
        return pieces.keySet().size() == 1 && pieces.containsKey(0);
    }

    General getAttacker(){
        if (pieces.containsKey(2)){
            return (General)pieces.get(2);
        }else{
            return (General)pieces.get(1);
        }
    }

    Piece getDefender(){
        if (pieces.containsKey(2)){
            return pieces.get(1);
        }else if (pieces.containsKey(6)){
            return pieces.get(6);
        }else{
            return pieces.containsKey(7) ? pieces.get(7): new Piece();
        }
    }
}
