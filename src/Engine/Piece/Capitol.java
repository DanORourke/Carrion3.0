package Engine.Piece;

import Engine.Alliance;
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

    public Capitol resetCapitol(){
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
}
