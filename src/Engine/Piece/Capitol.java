package Engine.Piece;

import Engine.Alliance;
import Engine.Piece.General.General;
import GUI.Coords;

public class Capitol extends Piece{
    private final boolean hasChief;
    private final boolean wantsChief;
    private final int troops;

    public Capitol(Coords coords, Alliance alliance){
        super(coords, 7, alliance);
        this.hasChief = true;
        this.wantsChief = false;
        this.troops = 2;
    }

    public Capitol(Coords coords, Alliance alliance, boolean chief, boolean wantsChief, int troops){
        super(coords, 7, alliance);
        this.hasChief = chief;
        this.wantsChief = wantsChief;
        this.troops = troops;
    }

    @Override
    public Piece copy(){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief, troops);
    }

    public Capitol createNewWantsChief(boolean wantsChief){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief, troops);
    }

    public Capitol createNewHasChief(boolean hasChief){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief, troops);
    }

    public Capitol createNewTroops(int troopChange){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief, troops + troopChange);
    }

    public Capitol resetCapitolAllocate(){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief, 2);
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
        return 2;
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
}
