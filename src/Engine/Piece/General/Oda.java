package Engine.Piece.General;


import Engine.Alliance;
import Engine.Board;
import Engine.Piece.Piece;
import GUI.Coords;

import java.util.ArrayList;

public class Oda extends General{

    Oda(){
        super("Oda Nobunaga");
    }

    private Oda(Coords coords, int type, Alliance alliance, String name, int troops,
                        int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed, boolean lines,
                        Coords launchPoint, boolean dropAfterWin,
                        Coords iAmAssisting, ArrayList<Coords> assistingMe){
        super(coords, type, alliance, name, troops, movementPoints, hasChief, wantsChief, exposed, lines, launchPoint,
                dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public Piece copy(){
        return new Oda(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewNew(Coords c, int type, Alliance alliance){
        return new Oda(c, type, alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewMoved(Coords c, int n){
        return new Oda(c, getType(), getAlliance(), getName(), troops, movementPoints - n,
                hasChief, wantsChief, exposed, lines, launchPoint, dropAfterWin,
                null, new ArrayList<>());
    }

    @Override
    public General createNewStuck(){
        return new Oda(getCoords(), getType(), getAlliance(), name,
                troops, 0, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewTroop(int addedTroops){
        return new Oda(getCoords(), getType(), getAlliance(), getName(), troops + addedTroops,
                movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewWantsChief(boolean wantsChief){
        return new Oda(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewHasChief(boolean hasChief){
        return new Oda(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewExposed(boolean exposed){
        return new Oda(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewLines(){
        return new Oda(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General resetGeneralMove(){
        return new Oda(getCoords(), getType(), getAlliance(), name, troops, calcMovementPoints(),
                hasChief, wantsChief, exposed, false, null,
                false, null, new ArrayList<>());
    }

    @Override
    public General resetGeneralAllocate(){
        return new Oda(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewFighting(Coords launchPoint, boolean dropAfterWin){
        //keep iamassisting to tell if general is distracted
        return new Oda(getCoords(), getType(), getAlliance(), name, troops, 0, hasChief, wantsChief,
                exposed,true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewAssisting(Coords iAmAssisting){
        return new Oda(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints - 1, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewAssisted(Coords assistingGeneral){
        assistingMe.add(assistingGeneral);
        return new Oda(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewClearAssisting(){
        return new Oda(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, null, assistingMe);
    }

    @Override
    public General createNewRemoveAssistingMe(Coords assistingGeneral){
        assistingMe.remove(assistingGeneral);
        return new Oda(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewTraitor(Alliance alliance){
        return new Oda(getCoords(), getType(), alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public String getDescription(){
        return "Pro:  Oda's troops kill like they are one third larger than they are.\n\n" +
                "Con:  Enemy troops fight like they are one third larger than they are.";
    }

    @Override
    public int getStandardCasualties(Board board, General g){
        if (g.getName().equals("Charlemagne") && g.isExposed()){
            return (int)Math.ceil((double)troops / 2);
        }
        return (int)Math.ceil(((double)troops * 2) / 3);
    }
}
