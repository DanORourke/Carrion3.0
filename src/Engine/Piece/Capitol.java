package Engine.Piece;

import Engine.Alliance;
import GUI.Coords;

public class Capitol extends Piece{
    private final boolean hasChief;

    public Capitol(Coords coords, Alliance alliance){
        super(coords, 7, alliance);
        hasChief = true;
    }

    public Capitol(Coords coords, Alliance alliance, boolean chief){
        super(coords, 7, alliance);
        hasChief = chief;
    }

    @Override
    public Piece copy(){
        return new Capitol(getCoords(), getAlliance(), hasChief);
    }

    public boolean hasChief() {
        return hasChief;
    }
}
