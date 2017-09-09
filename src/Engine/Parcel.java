package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import Engine.Piece.Supply;
import Engine.Piece.Town;
import GUI.Coords;
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

    String getOldMoveString(Alliance turnTeam, Alliance userTeam, HashMap<Alliance, Player> players){
        String s = "";
        return s;
    }

    String getActiveMoveString(Alliance turnTeam, Alliance userTeam, HashMap<Alliance, Player> players){
        String s = "";
        return s;
    }

//        if (turnStage == 0){
//        if (activeParcel.hasTown() && activeParcel.getTown().getAlliance().equals(userTeam)){
//            s = userTeam.toString() + " town has troop to give: " + activeParcel.getTown().hasTroop() + "\n";
//        }
//        if (activeParcel.hasCapitol() && activeParcel.getCapitol().getAlliance().equals(userTeam)){
//            s = userTeam.toString() + " capitol has " + activeParcel.getCapitol().getTroops() + " troops to give.\n";
//        }
//        if (activeParcel.hasSingleGeneral() && activeParcel.getFirstGeneral().getAlliance().equals(userTeam)){
//            s += userTeam.toString() + " General " + activeParcel.getFirstGeneral().getType() + " has:\n" +
//                    activeParcel.getFirstGeneral().getTroops() + " troops under his command.\n" +
//                    players.get(userTeam).getUnassisgnedTroops(activeParcel.getFirstGeneral()) +
//                    " unassigned troops he is connected to.";
//        }
//        if (activeParcel.hasSingleGeneral() && !activeParcel.getFirstGeneral().getAlliance().equals(userTeam)){
//            s = activeParcel.getFirstGeneral().getAlliance().toString() + " General " +
//                    activeParcel.getFirstGeneral().getType() + " has:\n" +
//                    activeParcel.getFirstGeneral().getTroops() + " troops under his command.";
//        }
//    }else{
//        if (activeParcel.isTownBattle()){
//            General g = activeParcel.getFirstGeneral();
//            Town t = activeParcel.getTown();
//            s = g.getAlliance().toString() + " General " + g.getType() + " attacking " +
//                    t.getAlliance().toString() + " town with " +
//                    g.getTroops() + " troops under his command.\n";
//            if (!g.getAssistingMe().isEmpty()){
//                for (Coords cords : g.getAssistingMe()){
//                    General assisting = board.get(cords).getAllianceGeneral(g.getAlliance());
//                    s += "Receiving assistance from  " + assisting.getAlliance().toString() +
//                            " General " + assisting.getType() + "\n";
//                }
//            }
//        }
//        else if(activeParcel.hasSingleGeneral() && activeParcel.getFirstGeneral().getAlliance().equals(userTeam)){
//            General g = activeParcel.getFirstGeneral();
//            s = userTeam.toString() + " General " + g.getType() + " has:\n" +
//                    g.getTroops() + " troops under his command.\n" + g.getMovementPoints() + " movement points.\n";
//            if (g.getiAmAssisting() != null){
//                General assisted = board.get(g.getiAmAssisting()).getAllianceGeneral(g.getAlliance());
//                s+= "Orders to assist " + userTeam.toString() + " General " + assisted.getType() + "\n";
//            }
//            if (!g.getAssistingMe().isEmpty()){
//                for (Coords cords : g.getAssistingMe()){
//                    General assisting = board.get(cords).getAllianceGeneral(g.getAlliance());
//                    s += "Orders to receive assistance from  " + userTeam.toString() +
//                            " General " + assisting.getType() + "\n";
//                }
//            }
//            if (g.hasChief()){
//                s += "The Chief of Staff.\n";
//            }
//            if (g.wantsChief()){
//                s += "Orders to receive the Chief of Staff.\n";
//            }
//        }
//        else if (activeParcel.hasSingleGeneral() && !activeParcel.getFirstGeneral().getAlliance().equals(userTeam)){
//            General g = activeParcel.getFirstGeneral();
//            s = g.getAlliance().toString() + " General " + g.getType() + " has:\n" +
//                    g.getTroops() + " troops under his command.";
//        }
//    }
}
