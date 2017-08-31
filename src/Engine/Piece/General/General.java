package Engine.Piece.General;

import Engine.Alliance;
import Engine.Piece.Piece;
import Engine.Board;
import GUI.Coords;

import java.util.ArrayList;

public class General extends Piece {
    private final int name;
    private final int troops;
    private final int movementPoints;
    private final boolean hasChief;
    private final boolean wantsChief;
    private final boolean exposed;
    private final boolean lines;
    private final boolean isFighting;
    private final boolean isAttacking;
    private final Coords launchPoint;
    private final boolean dropAfterWin;
    private final Coords iAmAssisting;
    private final ArrayList<Coords> assistingMe = new ArrayList<>();

    public General(Coords coords, int type, Alliance alliance, int name){
        super(coords, type, alliance);
        this.name = name;
        this.troops = 1;
        this.movementPoints = 5;
        this.hasChief = false;
        this.wantsChief = false;
        this.exposed = false;
        this.lines = false;
        this.isFighting = false;
        this.isAttacking = false;
        this.launchPoint = null;
        this.dropAfterWin = false;
        this.iAmAssisting = null;

    }

    private General(Coords coords, int type, Alliance alliance, int name, int troops,
                   int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed, boolean lines,
                   boolean isFighting, boolean isAttacking, Coords launchPoint, boolean dropAfterWin,
                    Coords iAmAssisting, ArrayList<Coords> assistingMe){
        super(coords, type, alliance);
        this.name = name;
        this.troops = troops;
        this.movementPoints = movementPoints;
        this.hasChief = hasChief;
        this.wantsChief = wantsChief;
        this.exposed = exposed;
        this.lines = lines;
        this.isFighting = isFighting;
        this.isAttacking = isAttacking;
        this.launchPoint = launchPoint;
        this.dropAfterWin = dropAfterWin;
        this.iAmAssisting = iAmAssisting;
        this.assistingMe.addAll(assistingMe);
    }

    @Override
    public Piece copy(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                isFighting, isAttacking, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewMoved(Coords c, int n){
        return new General(c, getType(), getAlliance(), getName(), troops, movementPoints - n,
                hasChief, wantsChief, exposed, lines, isFighting, isAttacking, launchPoint, dropAfterWin,
                null, new ArrayList<>());
    }

    public General createNewTroop(int addedTroops){
        return new General(getCoords(), getType(), getAlliance(), getName(), troops + addedTroops,
                movementPoints, hasChief, wantsChief, exposed, lines, isFighting, isAttacking,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewWantsChief(boolean wantsChief){
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, isFighting, isAttacking,launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewHasChief(boolean hasChief){
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, isFighting, isAttacking,launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewExposed(boolean exposed){
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, isFighting, isAttacking,launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewLines(){
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, true, isFighting, isAttacking,launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General resetGeneralMove(){
        return new General(getCoords(), getType(), getAlliance(), name, troops, calcMovementPoints(),
                hasChief, wantsChief, exposed, false, false, false,null,
                false, null, new ArrayList<>());
    }

    public General createNewFighting(boolean isFighting, boolean isAttacking, Coords launchPoint, boolean dropAfterWin){
        return new General(getCoords(), getType(), getAlliance(), name, troops, 0, hasChief, wantsChief,
                exposed,true, isFighting, isAttacking, launchPoint, dropAfterWin, null, assistingMe);
    }

    public General createNewAssisting(Coords iAmAssisting){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                isFighting, isAttacking, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewAssisted(Coords assistingGeneral){
        assistingMe.add(assistingGeneral);
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                isFighting, isAttacking, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewClearAssisting(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                isFighting, isAttacking, launchPoint, dropAfterWin, null, assistingMe);
    }

    public General createNewRemoveAssistingMe(Coords assistingGeneral){
        assistingMe.remove(assistingGeneral);
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                isFighting, isAttacking, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
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

    public int getAttackTownBonus(Board board){
        int bonus = 0;
        for (Coords c : assistingMe){
            General ag = board.getAssistingGeneral(c, getAlliance());
            bonus += ag.getAssistBonus();
        }
        return bonus + troops;
    }

    public int getAssistBonus(){
        return troops/2;
    }

    public boolean canAssist(Coords c){
        return getCoords().isNextTo(c);
    }

    public boolean canBeAssistedBy(Coords c){
        return getCoords().isNextTo(c);
    }

    public Coords getiAmAssisting(){
        return iAmAssisting;
    }

    public ArrayList<Coords> getAssistingMe(){
        return assistingMe;
    }

    public Coords getLaunchPoint(){
        return launchPoint;
    }

    public boolean getDropAfterWin(){
        return dropAfterWin;
    }

    public boolean canLoseToTown(){
        return troops > 1;
    }
}
