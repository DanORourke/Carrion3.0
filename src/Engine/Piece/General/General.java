package Engine.Piece.General;

import Engine.Alliance;
import Engine.Parcel;
import Engine.Piece.Capitol;
import Engine.Piece.Piece;
import Engine.Board;
import Engine.Piece.Town;
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
        this.launchPoint = null;
        this.dropAfterWin = false;
        this.iAmAssisting = null;

    }

    private General(Coords coords, int type, Alliance alliance, int name, int troops,
                   int movementPoints, boolean hasChief, boolean wantsChief, boolean exposed, boolean lines,
                   Coords launchPoint, boolean dropAfterWin,
                    Coords iAmAssisting, ArrayList<Coords> assistingMe){
        super(coords, type, alliance);
        this.name = name;
        this.troops = troops;
        this.movementPoints = movementPoints;
        this.hasChief = hasChief;
        this.wantsChief = wantsChief;
        this.exposed = exposed;
        this.lines = lines;
        this.launchPoint = launchPoint;
        this.dropAfterWin = dropAfterWin;
        this.iAmAssisting = iAmAssisting;
        this.assistingMe.addAll(assistingMe);
    }

    @Override
    public Piece copy(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewMoved(Coords c, int n){
        return new General(c, getType(), getAlliance(), getName(), troops, movementPoints - n,
                hasChief, wantsChief, exposed, lines, launchPoint, dropAfterWin,
                null, new ArrayList<>());
    }

    public General createNewStuck(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, 0, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewTroop(int addedTroops){
        return new General(getCoords(), getType(), getAlliance(), getName(), troops + addedTroops,
                movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewWantsChief(boolean wantsChief){
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewHasChief(boolean hasChief){
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewExposed(boolean exposed){
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, lines, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewLines(){
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, wantsChief,
                exposed, true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General resetGeneralMove(){
        return new General(getCoords(), getType(), getAlliance(), name, troops, calcMovementPoints(),
                hasChief, wantsChief, exposed, false, null,
                false, null, new ArrayList<>());
    }

    public General createNewFighting(Coords launchPoint, boolean dropAfterWin){
        //keep iamassisting to tell if general is distracted
        return new General(getCoords(), getType(), getAlliance(), name, troops, 0, hasChief, wantsChief,
                exposed,true, launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewAssisting(Coords iAmAssisting){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints - 1, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewAssisted(Coords assistingGeneral){
        assistingMe.add(assistingGeneral);
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewClearAssisting(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, null, assistingMe);
    }

    public General createNewRemoveAssistingMe(Coords assistingGeneral){
        assistingMe.remove(assistingGeneral);
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
    }

    public General createNewTraitor(Alliance alliance){
        return new General(getCoords(), getType(), alliance, name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
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
            return movementPoints > 0 && troops > 1;
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

    private int addTerritoryBonus(Board board){
        int bonus = 0;
        Parcel p = board.get(getCoords());
        if (p.getTerritory().equals(getAlliance())){
            bonus++;
        }else if (!p.getTerritory().equals(getAlliance()) && p.getTerritory().equals(Alliance.UNOCCUPIED)){
            bonus --;
        }
        return bonus;
    }

    private int addAssistingAttackBonus(Board board){
        int bonus = 0;
        for (Coords c : assistingMe){
            General ag = board.getAssistingGeneral(c, getAlliance());
            bonus += ag.getAssistBonus(this);
        }
        return bonus;
    }

    private int addDistractedBonus(){
        if (iAmAssisting != null){
            return ((int)Math.ceil((double)troops / 4) * -1);
        }
        return 0;
    }

    public int getAttackBonus(Board board, General defender){
        int bonus = troops;
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        return bonus;
    }

    public int getDefendBonus(Board board, General attacker){
        int bonus = troops;
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        bonus += addDistractedBonus();
        return bonus;
    }

    public int getAttackTownBonus(Board board){
        int bonus = troops;
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        return bonus;
    }

    public int getAttackDefendedTownBonus(Board board, General defender, Town t){
        int bonus = troops;
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        return bonus;
    }

    public int getDefendTownBonus(Board board, General attacker, Town t){
        int bonus = troops;
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        bonus += addDistractedBonus();
        if (getAlliance().equals(t.getAlliance())){
            bonus += t.getDefendBonus(attacker);
        }
        return bonus;
    }

    public int getAttackCapitolBonus(Board board, Capitol cap){
        return getAttackTownBonus(board);
    }

    public int getAttackDefendedCapitolBonus(Board board, General defender, Capitol cap){
        int bonus = troops;
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        return bonus;
    }

    public int getDefendCapitolBonus(Board board, General attacker, Capitol cap){
        int bonus = troops;
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        bonus += addDistractedBonus();
        if (getAlliance().equals(cap.getAlliance())){
            bonus += cap.getDefendBonus(attacker);
        }
        return bonus;
    }

    public int getAssistBonus(General assisting){
        return (int)Math.ceil((double)troops / 2);
    }

    public boolean canAssist(Coords c){
        return getCoords().isNextTo(c) && movementPoints > 0;
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

    public boolean canLoseToTown(int casualties){
        return troops > casualties;
    }

    public boolean canLoseToCap(int casualties){
        return troops > casualties;
    }

    public int getAttackCasualties(Board board, General defender){
        int casualties = (int)Math.ceil((double)troops / 2);
        casualties += addAssistingCasualties(board);
        return casualties;
    }

    public int getDefendCasualties(Board board, General attacker){
        int casualties = (int)Math.ceil((double)troops / 2);
        casualties += addAssistingCasualties(board);
        return casualties;
    }

    public int getAttackDefendedTownCasualties(Board board, General defender, Town t){
        int casualties = (int)Math.ceil((double)troops / 2);
        casualties += addAssistingCasualties(board);
        return casualties;
    }

    public int getDefendTownCasualties(Board board, General attacker, Town t){
        int casualties = (int)Math.ceil((double)troops / 2);
        casualties += addAssistingCasualties(board);
        casualties += t.getCasualties(attacker);
        return casualties;
    }

    public int getDefendCapitalCasualties(Board board, General attacker, Capitol cap){
        int casualties = (int)Math.ceil((double)troops / 2);
        casualties += addAssistingCasualties(board);
        casualties += cap.getCasualties(attacker);
        return casualties;
    }

    private int addAssistingCasualties(Board board){
        int casualties = 0;
        for (Coords c : assistingMe){
            General ag = board.getAssistingGeneral(c, getAlliance());
            casualties += ag.getAssistCasualties(this);
        }
        return casualties;
    }

    private int getAssistCasualties(General assisted){
        return (int)Math.ceil((double)troops / 4);
    }

    public boolean canSufferCasualties(int casualties){
        return troops > casualties;
    }
}
