package Engine.Piece;

import Engine.Alliance;
import GUI.Coords;

public class Town extends Piece {

    public Town(Coords coords,Alliance alliance){
        super(coords, 6, alliance);
    }

    @Override
    public Piece copy(){
        return new Town(getCoords(), getAlliance());
    }
}
