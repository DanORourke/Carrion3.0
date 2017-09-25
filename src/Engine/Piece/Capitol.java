package Engine.Piece;

import Engine.Alliance;
import Engine.Board;
import Engine.Piece.General.General;
import GUI.Coords;

public class Capitol extends Piece{
    private final boolean hasChief;
    private final boolean wantsChief;
    private final int troops;
    private final Alliance abandoned;

    public Capitol(Coords coords, Alliance alliance){
        super(coords, 7, alliance);
        this.hasChief = true;
        this.wantsChief = false;
        this.troops = 2;
        this.abandoned = Alliance.UNOCCUPIED;
    }

    public Capitol(Coords coords, Alliance alliance, boolean chief, boolean wantsChief, int troops, Alliance abandoned){
        super(coords, 7, alliance);
        this.hasChief = chief;
        this.wantsChief = wantsChief;
        this.troops = troops;
        this.abandoned = abandoned;
    }

    @Override
    public Piece copy(){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief, troops, abandoned);
    }

    public Capitol createNewWantsChief(boolean wantsChief){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief, troops, abandoned);
    }

    public Capitol createNewHasChief(boolean hasChief){
        return new Capitol(getCoords(), getAlliance(), hasChief, false, troops, abandoned);
    }

    public Capitol createNewTroops(int troopChange){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief, troops + troopChange, abandoned);
    }

    public Capitol resetCapitolAllocate(){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief, 2, abandoned);
    }

    public Capitol createNewAbandoned(Alliance abandoned){
        return new Capitol(getCoords(), Alliance.UNOCCUPIED, hasChief, wantsChief, troops, abandoned);
    }

    public Alliance getAbandoned(){
        return abandoned;
    }

    public boolean hasChief() {
        return hasChief;
    }

    public boolean wantsChief(){
        return wantsChief;
    }

    public int getTroops(){
        return troops;
    }

    public int getDefendBonus(General g) {
        if (hasChief()){
            return 4;
        }
        return 3;
    }

    public int getCasualties(General g){
        if (hasChief()){
            return 2;
        }
        return 1;
    }

    public String getAllocateString(Alliance turnTeam){
        String s = "";
        if (!getAlliance().equals(turnTeam)){
            s = getAlliance().toString() + " Capitol";
            if (hasChief()){
                s+= " has the Chief of Staff.\n\n";
            }else {
                s+= ".\n\n";
            }
        }else if (getAlliance().equals(turnTeam)){
            if (getTroops() == 2){
                s = getAlliance().toString() + " Capitol has " + 2 +" unassigned troops";
            }else if (getTroops() == 1){
                s = getAlliance().toString() + " Capitol has " + 1 +" unassigned troop";
            }else {
                s = getAlliance().toString() + " Capitol has " + 0 +" unassigned troops";
            }
            if (hasChief()){
                s+= " and the Chief of Staff.\n\n";
            }else {
                s+= ".\n\n";
            }
        }
        return s;
    }

    public String getMoveString(){
        String s = getAlliance().toString() + " Capitol";
        if (hasChief()){
            s+= " has the Chief of Staff.\n\n";
        }else {
            s+= ".\n\n";
        }

        return s;
    }

    public String getBattleString(General g){
        String s = "Defending " + getAlliance().toString() + " Capitol";
        if (hasChief()){
            s+= " has the Chief of Staff.\n";
        }else {
            s+= ".\n+";
        }
        s+= getDefendBonus(g) + " total fight bonus.\n+" +
                + getCasualties(g) + " total inflicted casualties.\n\n";
        return s;
    }
}
