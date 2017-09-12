package Engine.Piece.General;

import Engine.Alliance;
import Engine.Piece.Piece;
import GUI.Coords;

import java.util.ArrayList;

public class Charlemagne extends General{

    Charlemagne(){
        super("Charlemagne");
    }

    private Charlemagne(Coords coords, int type, Alliance alliance, String name, int troops,
                   int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed, boolean lines,
                   Coords launchPoint, boolean dropAfterWin,
                   Coords iAmAssisting, ArrayList<Coords> assistingMe){
        super(coords, type, alliance, name, troops, movementPoints, hasChief, wantsChief, exposed, lines, launchPoint,
                dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public Piece copy(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewNew(Coords c, int type, Alliance alliance){
        return new Charlemagne(c, type, alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewMoved(Coords c, int n){
        return new Charlemagne(c, getType(), getAlliance(), getName(), troops, movementPoints - n,
                hasChief, wantsChief, exposed, lines, launchPoint, dropAfterWin,
                null, new ArrayList<>());
    }

    @Override
    public General createNewStuck(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, 0, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewTroop(int addedTroops){
        return new Charlemagne(getCoords(), getType(), getAlliance(), getName(), troops + addedTroops,
                movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewWantsChief(boolean wantsChief){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewHasChief(boolean hasChief){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewExposed(boolean exposed){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewLines(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General resetGeneralMove(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, calcMovementPoints(),
                hasChief, wantsChief, exposed, false, null,
                false, null, new ArrayList<>());
    }

    @Override
    public General resetGeneralAllocate(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewFighting(Coords launchPoint, boolean dropAfterWin){
        //keep iamassisting to tell if general is distracted
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, 0, hasChief, wantsChief,
                exposed,true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewAssisting(Coords iAmAssisting){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints - 1, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewAssisted(Coords assistingGeneral){
        assistingMe.add(assistingGeneral);
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewClearAssisting(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, null, assistingMe);
    }

    @Override
    public General createNewRemoveAssistingMe(Coords assistingGeneral){
        assistingMe.remove(assistingGeneral);
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewTraitor(Alliance alliance){
        return new Charlemagne(getCoords(), getType(), alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public String getDescription(){
        return "Pro:  Charlemagne suffers a third fewer casualties than a normal general.\n\n" +
                "Con:  Charlemagne can move one fewer space than a normal general.";
    }
}
