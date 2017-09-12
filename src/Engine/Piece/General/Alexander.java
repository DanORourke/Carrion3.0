package Engine.Piece.General;

import Engine.Alliance;
import Engine.Piece.Piece;
import GUI.Coords;

import java.util.ArrayList;

public class Alexander extends General{

    Alexander(){
        super("Alexander of Macedon");
    }

    private Alexander(Coords coords, int type, Alliance alliance, String name, int troops,
                        int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed, boolean lines,
                        Coords launchPoint, boolean dropAfterWin,
                        Coords iAmAssisting, ArrayList<Coords> assistingMe){
        super(coords, type, alliance, name, troops, movementPoints, hasChief, wantsChief, exposed, lines, launchPoint,
                dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public Piece copy(){
        return new Alexander(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewNew(Coords c, int type, Alliance alliance){
        return new Alexander(c, type, alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewMoved(Coords c, int n){
        return new Alexander(c, getType(), getAlliance(), getName(), troops, movementPoints - n,
                hasChief, wantsChief, exposed, lines, launchPoint, dropAfterWin,
                null, new ArrayList<>());
    }

    @Override
    public General createNewStuck(){
        return new Alexander(getCoords(), getType(), getAlliance(), name,
                troops, 0, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewTroop(int addedTroops){
        return new Alexander(getCoords(), getType(), getAlliance(), getName(), troops + addedTroops,
                movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewWantsChief(boolean wantsChief){
        return new Alexander(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewHasChief(boolean hasChief){
        return new Alexander(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewExposed(boolean exposed){
        return new Alexander(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewLines(){
        return new Alexander(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General resetGeneralMove(){
        return new Alexander(getCoords(), getType(), getAlliance(), name, troops, calcMovementPoints(),
                hasChief, wantsChief, exposed, false, null,
                false, null, new ArrayList<>());
    }

    @Override
    public General resetGeneralAllocate(){
        return new Alexander(getCoords(), getType(), getAlliance(), name,
                calcConTroops(), movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    private int calcConTroops(){
        if (isExposed() && !hasChief){
            if (troops > 1){
                return troops - 1;
            }
        }
        return troops;
    }

    @Override
    public General createNewFighting(Coords launchPoint, boolean dropAfterWin){
        //keep iamassisting to tell if general is distracted
        return new Alexander(getCoords(), getType(), getAlliance(), name, troops, 0, hasChief, wantsChief,
                exposed,true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewAssisting(Coords iAmAssisting){
        return new Alexander(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints - 1, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewAssisted(Coords assistingGeneral){
        assistingMe.add(assistingGeneral);
        return new Alexander(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewClearAssisting(){
        return new Alexander(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, null, assistingMe);
    }

    @Override
    public General createNewRemoveAssistingMe(Coords assistingGeneral){
        assistingMe.remove(assistingGeneral);
        return new Alexander(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public General createNewTraitor(Alliance alliance){
        return new Alexander(getCoords(), getType(), alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    @Override
    public String getDescription(){
        return "Pro:  Alexander's troops fight like they are one third larger than they are.\n\n" +
                "Con:  Alexander will lose a troop at the beginning of every turn.\n" +
                "If the Chief of Staff is stationed with Alexander when he loses a battle, " +
                "Alexander will kill the Chief of Staff.";
    }

    @Override
    public int getStandardAttackBonus(General other){
        if (other != null && other.getName().equals("Oda Nobunaga") && other.isExposed() && !other.hasChief()){
            return (int)Math.ceil(((double)troops * 5) / 3);
        }else if (other != null && other.getName().equals("Leonidas") && other.isExposed() && other.getTroops() < troops){
            return (int)Math.ceil(((double)other.getTroops() * 4) / 3);
        }
        return (int)Math.ceil(((double)troops * 4) / 3);
    }
}
