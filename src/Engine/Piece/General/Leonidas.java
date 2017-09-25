package Engine.Piece.General;

import Engine.Alliance;
import Engine.Piece.Piece;
import GUI.Coords;

import java.util.ArrayList;

public class Leonidas extends General{

    Leonidas(){
        super("Leonidas");
    }

    private Leonidas(Coords coords, int type, Alliance alliance, String name, int troops,
                        int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed, boolean lines,
                        Coords launchPoint, boolean dropAfterWin,
                        Coords iAmAssisting, ArrayList<Coords> assistingMe, Alliance abandoned){
        super(coords, type, alliance, name, troops, movementPoints, hasChief, wantsChief, exposed, lines, launchPoint,
                dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public Piece copy(){
        return new Leonidas(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewNew(Coords c, int type, Alliance alliance){
        return new Leonidas(c, type, alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewMoved(Coords c, int n){
        return new Leonidas(c, getType(), getAlliance(), getName(), troops, movementPoints - n,
                hasChief, wantsChief, exposed, lines, launchPoint, dropAfterWin,
                null, new ArrayList<>(), abandoned);
    }

    @Override
    public General createNewStuck(){
        return new Leonidas(getCoords(), getType(), getAlliance(), name,
                troops, 0, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewTroop(int addedTroops){
        return new Leonidas(getCoords(), getType(), getAlliance(), getName(), troops + addedTroops,
                movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewWantsChief(boolean wantsChief){
        return new Leonidas(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewHasChief(boolean hasChief){
        return new Leonidas(getCoords(), getType(), getAlliance(), name, calcConTroops(exposed, hasChief), movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewExposed(boolean exposed){
        return new Leonidas(getCoords(), getType(), getAlliance(), name, calcConTroops(exposed, hasChief), movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);

    }

    private int calcConTroops(boolean exposed, boolean hasChief){
        if (troops > 10 && exposed && !hasChief){
            return 10;
        }
        return troops;
    }

    @Override
    public General createNewLines(){
        return new Leonidas(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, true, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General resetGeneralMove(){
        return new Leonidas(getCoords(), getType(), getAlliance(), name, troops, calcMovementPoints(),
                hasChief, wantsChief, exposed, false, null,
                false, null, new ArrayList<>(), abandoned);
    }

    @Override
    public General resetGeneralAllocate(){
        return new Leonidas(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewFighting(Coords launchPoint, boolean dropAfterWin){
        //keep iamassisting to tell if general is distracted
        return new Leonidas(getCoords(), getType(), getAlliance(), name, troops, 0, hasChief, wantsChief,
                exposed,true, launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewAssisting(Coords iAmAssisting){
        return new Leonidas(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints - 1, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewAssisted(Coords assistingGeneral){
        assistingMe.add(assistingGeneral);
        return new Leonidas(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewClearAssisting(){
        return new Leonidas(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, null, assistingMe, abandoned);
    }

    @Override
    public General createNewRemoveAssistingMe(Coords assistingGeneral){
        assistingMe.remove(assistingGeneral);
        return new Leonidas(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public General createNewTraitor(Alliance alliance){
        return new Leonidas(getCoords(), getType(), alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, Alliance.UNOCCUPIED);
    }

    @Override
    public General createNewAbandoned(Alliance abandoned){
        return new Leonidas(getCoords(), getType(), Alliance.UNOCCUPIED, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe, abandoned);
    }

    @Override
    public String getDescription(){
        return "Pro:  If Leonidas is defending outnumbered, " +
                "main enemy generals will fight as though they only have as many troops as Leonidas, but kill like normal.\n\n" +
                "Con:  Leonidas can command a maximum of 10 troops.  If he has more than 10 troops when he is exposed without the Chief of Staff or the Chief of Staff changes posts, Leonidas will instantly lose command of all troops in excess of 10.";
    }

    @Override
    public boolean canAdd(){
        if (!isExposed() || hasChief){
            return troops < 20;
        }
        return troops < 10;
    }
}
