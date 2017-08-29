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
    private final boolean lines;

    public General(Coords coords, int type, Alliance alliance, int name){
        super(coords, type, alliance);
        this.name = name;
        this.troops = 1;
        this.movementPoints = 5;
        this.hasChief = false;
        this.wantsChief = false;
        this.exposed = false;
        this.lines = false;
    }

    public General(Coords coords, int type, Alliance alliance, int name, int troops,
                   int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed, boolean lines){
        super(coords, type, alliance);
        this.name = name;
        this.troops = troops;
        this.movementPoints = movementPoints;
        this.hasChief = hasChief;
        this.wantsChief = wantsChief;
        this.exposed = exposed;
        this.lines = lines;
    }

    @Override
    public Piece copy(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines);
    }

    public General createNewMoved(Coords c, int n){
        return new General(c, getType(), getAlliance(), getName(),
                troops, movementPoints - n, hasChief, wantsChief, exposed, lines);
    }

    public General createNewTroop(int addedTroops){
        return new General(getCoords(), getType(), getAlliance(), getName(),
                troops + addedTroops, movementPoints, hasChief, wantsChief, exposed, lines);
    }

    public General createNewWantsChief(boolean wantsChief){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines);
    }

    public General createNewHasChief(boolean hasChief){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines);
    }

    public General createNewExposed(boolean exposed){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines);
    }

    public General createNewLines(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, true);
    }

    public General resetGeneralMove(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, calcMovementPoints(), hasChief, wantsChief, exposed, false);
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
        if (lines){
            return troops > 1;
        }else{
            return movementPoints > 0 && troops > 1;
        }
    }

    public boolean canMove(boolean cut){
        if (cut){
            if (lines){
                return movementPoints > 0;
            }else{
                return movementPoints > 1;
            }
        }else{
            return movementPoints > 0;
        }
    }

    public boolean canMoveAndDrop() {
        if (lines){
            return troops > 1;
        }else {
            return movementPoints > 1 && troops > 1;
        }
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

    private int calcMovementPoints(){
        if (troops < 6){
            return 5;
        }else if (troops < 11){
            return 4;
        }else if (troops < 16){
            return 3;
        }else{
            return 2;
        }
    }

    public boolean getLines(){
        return lines;
    }
}
