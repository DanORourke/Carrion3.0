package Engine.Piece.General;

import Engine.Alliance;
import Engine.Piece.Piece;
import GUI.Coords;

public class General extends Piece {
    private final int name;
    private final int troops;
    private final int movementPoints;
    private final boolean hasChief;
    private final boolean wantsChief;
    private final boolean exposed;

    public General(Coords coords, int type, Alliance alliance, int name){
        super(coords, type, alliance);
        this.name = name;
        this.troops = 1;
        this.movementPoints = 5;
        this.hasChief = false;
        this.wantsChief = false;
        this.exposed = false;
    }

    public General(Coords coords, int type, Alliance alliance, int name,
                   int troops, int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed){
        super(coords, type, alliance);
        this.name = name;
        this.troops = troops;
        this.movementPoints = movementPoints;
        this.hasChief = hasChief;
        this.wantsChief = wantsChief;
        this.exposed = exposed;
    }

    @Override
    public Piece copy(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed);
    }

    public General createNewMoved(Coords c, int n){
        return new General(c, getType(), getAlliance(), getName(),
                troops, movementPoints - n, hasChief, wantsChief, exposed);
    }

    public General createNewTroop(int addedTroops){
        return new General(getCoords(), getType(), getAlliance(), getName(),
                troops + addedTroops, movementPoints, hasChief, wantsChief, exposed);
    }

    public General createNewWantsChief(boolean wantsChief){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed);
    }

    public General createNewHasChief(boolean hasChief){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed);
    }

    public General createNewExposed(boolean exposed){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed);
    }

    public General resetGeneralMove(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, 5, hasChief, wantsChief, exposed);
    }

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

    public boolean wantsChief(){
        return wantsChief;
    }

    public boolean getExposed(){
        return exposed;
    }

    public boolean canAdd(){
        return troops < 20;
    }

    public boolean canSubtract(){
        return troops > 1;
    }
}
