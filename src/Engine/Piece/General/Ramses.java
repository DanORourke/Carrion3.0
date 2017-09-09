package Engine.Piece.General;

import Engine.Alliance;
import Engine.Board;
import Engine.Piece.Piece;
import GUI.Coords;

import java.util.ArrayList;

public class Ramses extends General{

    Ramses(){
        super("Ramses II");
    }

    private Ramses(Coords coords, int type, Alliance alliance, String name, int troops,
                    int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed, boolean lines,
                    Coords launchPoint, boolean dropAfterWin,
                    Coords iAmAssisting, ArrayList<Coords> assistingMe){
        super(coords, type, alliance, name, troops, movementPoints, hasChief, wantsChief, exposed, lines, launchPoint,
                dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public Piece copy(){
        return new Ramses(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewNew(Coords c, int type, Alliance alliance){
        return new Ramses(c, type, alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewMoved(Coords c, int n){
        return new Ramses(c, getType(), getAlliance(), getName(), troops, movementPoints - n,
                hasChief, wantsChief, exposed, lines, launchPoint, dropAfterWin,
                null, new ArrayList<>());
    }

    @Override
    public General createNewStuck(){
        return new Ramses(getCoords(), getType(), getAlliance(), name,
                troops, 0, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewTroop(int addedTroops){
        return new Ramses(getCoords(), getType(), getAlliance(), getName(), troops + addedTroops,
                movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewWantsChief(boolean wantsChief){
        return new Ramses(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewHasChief(boolean hasChief){
        return new Ramses(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewExposed(boolean exposed){
        return new Ramses(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewLines(){
        return new Ramses(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General resetGeneralMove(){
        return new Ramses(getCoords(), getType(), getAlliance(), name, troops, calcMovementPoints(),
                hasChief, wantsChief, exposed, false, null,
                false, null, new ArrayList<>());
    }

    @Override
    public General createNewFighting(Coords launchPoint, boolean dropAfterWin){
        //keep iamassisting to tell if general is distracted
        return new Ramses(getCoords(), getType(), getAlliance(), name, troops, 0, hasChief, wantsChief,
                exposed,true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewAssisting(Coords iAmAssisting){
        return new Ramses(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints - 1, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewAssisted(Coords assistingGeneral){
        assistingMe.add(assistingGeneral);
        return new Ramses(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewClearAssisting(){
        return new Ramses(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, null, assistingMe);
    }

    @Override
    public General createNewRemoveAssistingMe(Coords assistingGeneral){
        assistingMe.remove(assistingGeneral);
        return new Ramses(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewTraitor(Alliance alliance){
        return new Ramses(getCoords(), getType(), alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public String getDescription(){
        return "Pro:  Ramses can move 1 more space than a normal general.\n\n" +
                "Con:  Ramses inflicts a third fewer casualties than a normal general.";
    }

    @Override
    public int calcMovementPoints(){
        if (exposed){
            //faster
            if (getTroops() < 6){
                return 6;
            }else if (getTroops() < 11){
                return 5;
            }else if (getTroops() < 16){
                return 4;
            }else{
                return 3;
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

    @Override
    public int getStandardCasualties(Board board, General g){
        //kills at two thirds normal, doesn't apply when assisting
        if (hasChief){
            int casualties = (int)Math.ceil((double)troops / 2);
            casualties += addAssistingCasualties(board);
            return casualties;
        }else {
            int casualties = (int)Math.ceil((double)troops / 3);
            casualties += addAssistingCasualties(board);
            return casualties;
        }
    }
}
