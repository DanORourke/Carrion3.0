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
                   Coords iAmAssisting, ArrayList<Coords> assistingMe, Alliance abandoned){
        super(coords, type, alliance, name, troops, movementPoints, hasChief, wantsChief, exposed, lines, launchPoint,
                dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public Piece copy(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewNew(Coords c, int type, Alliance alliance){
        return new Charlemagne(c, type, alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewMoved(Coords c, int n){
        return new Charlemagne(c, getType(), getAlliance(), getName(), troops, movementPoints - n,
                hasChief, wantsChief, exposed, lines, launchPoint, dropAfterWin,
                null, new ArrayList<>(), abandoned);
    }

    @Override
    public General createNewStuck(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, 0, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewTroop(int addedTroops){
        return new Charlemagne(getCoords(), getType(), getAlliance(), getName(), troops + addedTroops,
                movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewWantsChief(boolean wantsChief){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewHasChief(boolean hasChief){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewExposed(boolean exposed){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewLines(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, true, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General resetGeneralMove(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, calcMovementPoints(),
                hasChief, wantsChief, exposed, calcLines(), null,
                false, null, new ArrayList<>(), abandoned);
    }

    private boolean calcLines(){
        System.out.println("calcLines called");
        return (movementPoints > 15 && isExposed() && !hasChief);
    }

    @Override
    public General resetGeneralAllocate(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewFighting(Coords launchPoint, boolean dropAfterWin){
        //keep iamassisting to tell if general is distracted
        return new Charlemagne(getCoords(), getType(), getAlliance(), name, troops, 0, hasChief, wantsChief,
                exposed,true, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewAssisting(Coords iAmAssisting){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints - 1, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewAssisted(Coords assistingGeneral){
        assistingMe.add(assistingGeneral);
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewClearAssisting(){
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, null, assistingMe, abandoned);
    }

    @Override
    public General createNewRemoveAssistingMe(Coords assistingGeneral){
        assistingMe.remove(assistingGeneral);
        return new Charlemagne(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewTraitor(Alliance alliance){
        return new Charlemagne(getCoords(), getType(), alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, Alliance.UNOCCUPIED);
    }

    @Override
    public General createNewAbandoned(Alliance abandoned){
        return new Charlemagne(getCoords(), getType(), Alliance.UNOCCUPIED, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public String getDescription(){
        return "Pro:  Charlemagne suffers a third fewer casualties than a normal general.\n\n" +
                "Con:  Charlemagne can move one fewer space than a normal general.";
    }

    @Override
    public int calcMovementPoints(){
        if (isExposed() && !hasChief){
            if (getTroops() < 6){
                return 4;
            }else if (getTroops() < 11){
                return 3;
            }else if (getTroops() < 16){
                return 2;
            }else{
                return 1;
            }
        }else{
            if (getTroops() < 6){
                return 5;
            }else if (getTroops() < 11){
                return 4;
            }else if (getTroops() < 16){
                return 3;
            }else{
                return 2;
            }
        }
    }
}
