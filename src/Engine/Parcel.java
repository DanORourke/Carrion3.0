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
        for (int t : ps.keySet()){
            addPiece(t, ps.get(t).copy());
        }
    }

    Parcel(Parcel oldParcel){
        this.territory = oldParcel.getTerritory();
        HashMap<Integer, Piece> oPieces = oldParcel.getPieces();
        for (int t : oPieces.keySet()){
            addPiece(t, oPieces.get(t).copy());
        }
    }

    Parcel(Alliance home){
        this.territory = home;
    }

    Parcel(Piece p){
        this.territory = Alliance.UNOCCUPIED;
        if (p.isGeneral()){
            addPiece(1, p);
        }else{
            addPiece(p.getType(), p);
        }
    }

    private void addPiece(int key, Piece p){
        pieces.put(key, p);
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
                    if (p.isCapitol()){
                        Capitol c = (Capitol) p;
                        if (c.hasChief()){
                            hold[j] = 8;
                            j++;
                            hold[j] = c.getAlliance().getDataCode();
                            j++;
                        }
                    }else if (p.isGeneral()){
                        General g = (General)p;
                        if (g.hasChief()){
                            hold[j] = 8;
                            j++;
                            hold[j] = g.getAlliance().getDataCode();
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

    public General getAllianceGeneral(Alliance a){
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


    String getOldAllocateString(Alliance turnTeam, Alliance userTeam, HashMap<Alliance, Player> players){
        if (turnTeam.equals(userTeam)){
            return getActiveAllocateString(userTeam, players);
        }
        String s = "";
        Player player = players.get(turnTeam);
        if (hasSupplyLine()){
            s = getSupply().getAlliance().toString() + " Supply Line.\n\n";
        }else if (hasTown()){
            s = getTown().getAllocateString(turnTeam);
        }else if (hasCapitol()){
            s = getCapitol().getAllocateString(turnTeam);
        }
        if (hasSingleGeneral()){
            General g = getFirstGeneral();
            if (g.getAlliance().equals(userTeam)){
                s+= g.getAllocateStringOnlyUser();
            }else if (g.getAlliance().equals(turnTeam)){
                s+= g.getAllocateStringOnlyTurn(player);
            }else{
                s+= g.getAllocateStringNeither();
            }
        }
        return s;
    }

    String getActiveAllocateString(Alliance userTeam, HashMap<Alliance, Player> players){
        String s = "";
        Player player = players.get(userTeam);
        if (hasSupplyLine()){
            s = getSupply().getAlliance().toString() + " Supply Line.\n\n";
        }else if (hasTown()){
            s = getTown().getAllocateString(userTeam);
        }else if (hasCapitol()){
            s = getCapitol().getAllocateString(userTeam);
        }
        if (hasSingleGeneral()){
            General g = getFirstGeneral();
            if (g.getAlliance().equals(userTeam)){
                s+= g.getAllocateStringBoth(player);
            }else{
                s+= g.getAllocateStringNeither();
            }
        }
        return s;
    }

    String getOldMoveString(Alliance turnTeam, Alliance userTeam, Board board){
        if (isBattle()){
            return getOldBattleString(board);
        }
        String s = "";
        if (hasSupplyLine()){
            s = getSupply().getAlliance().toString() + " Supply Line.\n\n";
        }else if (hasTown()){
            s = getTown().getMoveString();
        }else if (hasCapitol()){
            s = getCapitol().getMoveString();
        }
        if (hasSingleGeneral()){
            General g = getFirstGeneral();
            if (g.getAlliance().equals(userTeam)){
                s+= g.getMoveStringOnlyUser(board);
            }else if (g.getAlliance().equals(turnTeam)){
                s+= g.getMoveStringOnlyTurn(board);
            }else{
                s+= g.getMoveStringNeither(board);
            }
        }
        return s;
    }

    String getActiveMoveString(Alliance userTeam, Board board){
        if (isBattle()){
            return getActiveBattleString(userTeam, board);
        }
        String s = "";
        if (hasSupplyLine()){
            s = getSupply().getAlliance().toString() + " Supply Line.\n\n";
        }else if (hasTown()){
            s = getTown().getMoveString();
        }else if (hasCapitol()){
            s = getCapitol().getMoveString();
        }
        if (hasSingleGeneral()){
            General g = getFirstGeneral();
            if (g.getAlliance().equals(userTeam)){
                s+= g.getMoveStringBoth(board);
            }else{
                s+= g.getMoveStringNeither(board);
            }
        }
        return s;
    }

    private String getOldBattleString(Board board){
        //typeCode, 1 = field, 2 = defended town, 3 = defended cap, 4 = town, 5 = cap
        if (isFieldBattle()){

        }else if (isDefendedTownBattle()){

        }else if (isDefendedCapitolBattle()){

        }else if (isTownBattle()){
            General ag = getFirstGeneral();
            Town dt = getTown();
            String s = dt.getBattleString(board, ag);
            s += ag.getOldBattleString(board, dt, 4, true);
            return s;
        }else if (isCapitolBattle()){

        }
        return "OldBattle";
    }

    private String getActiveBattleString(Alliance userTeam, Board board){
        if (isFieldBattle()){

        }else if (isDefendedTownBattle()){

        }else if (isDefendedCapitolBattle()){

        }else if (isTownBattle()){
            General ag = getFirstGeneral();
            Town dt = getTown();
            String s = dt.getBattleString(board, ag);
            s += ag.getActiveBattleString(board, dt, 4, true);
            return s;
        }else if (isCapitolBattle()){

        }
        return "ActiveBattle";
    }
}
