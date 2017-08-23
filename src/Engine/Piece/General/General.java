package Engine.Piece.General;

import Engine.Alliance;
import Engine.Piece.Piece;
import GUI.Coords;

public class General extends Piece {
    private final int name;
    private final int troops;
    private final int movementPoints;
    private final boolean hasChief;

    public General(Coords coords, int type, Alliance alliance, int name){
        super(coords, type, alliance);
        this.name = name;
        this.troops = 1;
        this.movementPoints = 5;
        this.hasChief = false;
    }

    public General(Coords coords, int type, Alliance alliance, int name,
                   int troops, int movementPoints, boolean hasChief){
        super(coords, type, alliance);
        this.name = name;
        this.troops = troops;
        this.movementPoints = movementPoints;
        this.hasChief = hasChief;
    }

    @Override
    public Piece copy(){
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief);
    }

    public General createNewMoved(Coords c, int n){
        return new General(c, getType(), getAlliance(), getName(), troops, movementPoints - n, hasChief);
    }

    public General createNewTroop(int addedTroops){
        return new General(getCoords(), getType(), getAlliance(), getName(),
                troops + addedTroops, movementPoints, hasChief);
    }

//    @Override
//    public int hashCode(){
//        return super.hashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj){
//        return super.equals(obj);
//    }

    public int getName() {
        return name;
    }

    public int getTroops(){
        return troops;
    }

    public int getMovementPoints(){
        return movementPoints;
    }

    public boolean canDrop() {
        return true;
    }

    public boolean canMoveAndDrop() {
        return true;
    }

    public boolean hasChief() {
        return hasChief;
    }
}
