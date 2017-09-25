package Engine.Piece.General;

import Engine.Alliance;
import Engine.Piece.Piece;
import GUI.Coords;

import java.util.ArrayList;

public class Frederick extends General {

    Frederick(){
        super("Frederick of Prussia");
    }

    private Frederick(Coords coords, int type, Alliance alliance, String name, int troops,
                        int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed, boolean lines,
                        Coords launchPoint, boolean dropAfterWin,
                        Coords iAmAssisting, ArrayList<Coords> assistingMe, Alliance abandoned){
        super(coords, type, alliance, name, troops, movementPoints, hasChief, wantsChief, exposed, lines, launchPoint,
                dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public Piece copy(){
        return new Frederick(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewNew(Coords c, int type, Alliance alliance){
        return new Frederick(c, type, alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewMoved(Coords c, int n){
        return new Frederick(c, getType(), getAlliance(), getName(), troops, movementPoints - n,
                hasChief, wantsChief, exposed, lines, launchPoint, dropAfterWin,
                null, new ArrayList<>(), abandoned);
    }

    @Override
    public General createNewStuck(){
        return new Frederick(getCoords(), getType(), getAlliance(), name,
                troops, 0, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewTroop(int addedTroops){
        return new Frederick(getCoords(), getType(), getAlliance(), getName(), troops + addedTroops,
                movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewWantsChief(boolean wantsChief){
        return new Frederick(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewHasChief(boolean hasChief){
        return new Frederick(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewExposed(boolean exposed){
        return new Frederick(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewLines(){
        return new Frederick(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, true, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General resetGeneralMove(){
        return new Frederick(getCoords(), getType(), getAlliance(), name, troops, calcMovementPoints(),
                hasChief, wantsChief, exposed, false, null,
                false, null, new ArrayList<>(), abandoned);
    }

    @Override
    public General resetGeneralAllocate(){
        return new Frederick(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewFighting(Coords launchPoint, boolean dropAfterWin){
        //keep iamassisting to tell if general is distracted
        return new Frederick(getCoords(), getType(), getAlliance(), name, troops, 0, hasChief, wantsChief,
                exposed,true, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewAssisting(Coords iAmAssisting){
        return new Frederick(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints - 1, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewAssisted(Coords assistingGeneral){
        assistingMe.add(assistingGeneral);
        return new Frederick(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewClearAssisting(){
        return new Frederick(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, null, assistingMe, abandoned);
    }

    @Override
    public General createNewRemoveAssistingMe(Coords assistingGeneral){
        assistingMe.remove(assistingGeneral);
        return new Frederick(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewTraitor(Alliance alliance){
        return new Frederick(getCoords(), getType(), alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, Alliance.UNOCCUPIED);
    }

    @Override
    public General createNewAbandoned(Alliance abandoned){
        return new Frederick(getCoords(), getType(), Alliance.UNOCCUPIED, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public String getDescription(){
        return "Pro:  No opponent general can receive assistance when fighting Frederick.\n\n" +
                "Con:  Frederick will not accept assistance.";
    }
}

