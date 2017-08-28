package Engine.Piece;

import Engine.Alliance;
import GUI.Coords;

public class Town extends Piece {
    private final boolean haveTroop;

    public Town(Coords coords,Alliance alliance){
        super(coords, 6, alliance);
        this.haveTroop = true;
    }

    public Town(Coords coords,Alliance alliance, boolean haveTroop){
        super(coords, 6, alliance);
        this.haveTroop = haveTroop;
    }

    @Override
    public Piece copy(){
        return new Town(getCoords(), getAlliance(), haveTroop);
    }

    public Town createNewTroop(boolean haveTroop){
        return new Town(getCoords(), getAlliance(), haveTroop);
    }

    public Town resetTown(){
        return new Town(getCoords(), getAlliance(), true);
    }

    public boolean hasTroop(){
        return haveTroop;
    }
}
