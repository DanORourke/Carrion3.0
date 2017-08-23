package Engine.Piece;

import Engine.Alliance;
import GUI.Coords;

public class Supply extends Piece{

    public Supply(Coords coords, Alliance alliance){
        super(coords, 0, alliance);
    }

    public Supply createNew(Coords coords){
        return new Supply(coords, getAlliance());
    }

    @Override
    public Piece copy(){
        return new Supply(getCoords(), getAlliance());
    }
}
