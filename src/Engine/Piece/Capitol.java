package Engine.Piece;

import Engine.Alliance;
import GUI.Coords;

public class Capitol extends Piece{
    private final boolean hasChief;
    private final boolean wantsChief;

    public Capitol(Coords coords, Alliance alliance){
        super(coords, 7, alliance);
        this.hasChief = true;
        this.wantsChief = false;
    }

    public Capitol(Coords coords, Alliance alliance, boolean chief, boolean wantsChief){
        super(coords, 7, alliance);
        this.hasChief = chief;
        this.wantsChief = wantsChief;
    }

    @Override
    public Piece copy(){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief);
    }

    public Capitol createNewWantsChief(boolean wantsChief){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief);
    }

    public Capitol createNewHasChief(boolean hasChief){
        return new Capitol(getCoords(), getAlliance(), hasChief, wantsChief);
    }

    public boolean hasChief() {
        return hasChief;
    }

    public boolean wantsChief(){
        return wantsChief;
    }
}
