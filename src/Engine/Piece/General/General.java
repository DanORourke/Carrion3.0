package Engine.Piece.General;

import Engine.Alliance;
import Engine.Parcel;
import Engine.Piece.Capitol;
import Engine.Piece.Piece;
import Engine.Board;
import Engine.Piece.Town;
import Engine.Player;
import GUI.Coords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class General extends Piece {
    final String name;
    final int troops;
    final int movementPoints;
    final boolean hasChief;
    final boolean wantsChief;
    final boolean exposed;
    final boolean lines;
    final Coords launchPoint;
    final boolean dropAfterWin;
    final Coords iAmAssisting;
    final ArrayList<Coords> assistingMe = new ArrayList<>();

    public General(String name){
        super(new Coords(0, 0, 0), 1, Alliance.UNOCCUPIED);
        this.name = name;
        this.troops = 1;
        this.movementPoints = calcMovementPoints();
        this.hasChief = false;
        this.wantsChief = false;
        this.exposed = false;
        this.lines = false;
        this.launchPoint = null;
        this.dropAfterWin = false;
        this.iAmAssisting = null;

    }

    public General(Coords coords, int type, Alliance alliance, String name, int troops,
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

    public General createNewNew(Coords c, int type, Alliance alliance){
        return new General(c, type, alliance, name,
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

    public String getName() {
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

    public boolean isExposed(){
        return exposed;
    }

    public boolean canAdd(){
        return troops < 20;
    }

    public boolean canSubtract(){
        return troops > 1;
    }

    public int calcMovementPoints(){
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
            General ag = board.getAssistingGeneral(c);
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

    public int getStandardCasualties(Board board, General g){
        int casualties = (int)Math.ceil((double)troops / 2);
        casualties += addAssistingCasualties(board);
        return casualties;
    }

    public int getAttackCasualties(Board board, General defender){
        return getStandardCasualties(board, defender);
    }

    public int getDefendCasualties(Board board, General attacker){
        return getStandardCasualties(board, attacker);
    }

    public int getAttackDefendedTownCasualties(Board board, General defender, Town t){
        return getStandardCasualties(board, defender);
    }

    public int getDefendTownCasualties(Board board, General attacker, Town t){
        int casualties = getStandardCasualties(board, attacker);
        casualties += t.getCasualties(attacker);
        return casualties;
    }

    public int getDefendCapitalCasualties(Board board, General attacker, Capitol cap){
        int casualties = getStandardCasualties(board, attacker);
        casualties += cap.getCasualties(attacker);
        return casualties;
    }

    public int addAssistingCasualties(Board board){
        int casualties = 0;
        for (Coords c : assistingMe){
            General ag = board.getAssistingGeneral(c);
            casualties += ag.getAssistCasualties(this);
        }
        return casualties;
    }

    public int getAssistCasualties(General assisted){
        return (int)Math.ceil((double)troops / 4);
    }

    public boolean canSufferCasualties(int casualties){
        return troops > casualties;
    }

    public static ArrayList<General> createNewGenerals(int gameType){
        ArrayList<General> generals = new ArrayList<>();
        ArrayList<String> allPossibleG = createAllPossibleG();
        if (gameType < 3){
            gameType = 10;
        }else{
            gameType = gameType * 5;
        }
        while (generals.size() < gameType){
            if (allPossibleG.isEmpty()){
                allPossibleG = createAllPossibleG();
            }
            String name = allPossibleG.get(new Random().nextInt(allPossibleG.size()));
            allPossibleG.remove(name);
            generals.add(makeFromName(name));
        }
        return generals;
    }

    public static ArrayList<General> createGenerals(ArrayList<String> moves){
        ArrayList<General> generals = new ArrayList<>();
        int gameType = Integer.valueOf(moves.get(1));
        if (gameType < 3){
            gameType = 10;
        }else{
            gameType = gameType * 5;
        }
        int i = 2;
        while (i < 2 + gameType){
            String name = moves.get(i);
            i++;
            generals.add(makeFromName(name));
        }
        return generals;
    }

    private static ArrayList<String> createAllPossibleG(){
        ArrayList<String> allPossibleG = new ArrayList<>();
        Collections.addAll(allPossibleG, "Ramses II", "Charlemagne", "Frederick of Prussia",
                "Oda Nobunaga", "Alexander of Macedon");
        return allPossibleG;
    }

    private static General makeFromName(String name){
        if (name.equals("Ramses II")){
            return new Ramses();
        }else if (name.equals("Charlemagne")){
            return new Charlemagne();
        }else if (name.equals("Frederick of Prussia")){
            return new Frederick();
        }else if (name.equals("Oda Nobunaga")){
            return new Oda();
        }else if (name.equals("Alexander of Macedon")){
            return new Alexander();
        }
        return new General("General");
    }

    public String getDescription(){
        return "Boring old plain general here.  If you are reading this something has gone wrong. " +
                "Although he is the super";
    }

    private String getStringFirstLine(boolean user){
        String s = getAlliance().toString() + " General ";
        if (exposed || user){
            s+= name + " has:\n";
        }else {
            s+= getType() + " has:\n";
        }
        if (exposed && user){
            s+= "Been exposed.\n";
        }
        if (troops == 1){
            s+= troops + " troop under his command.\n";
        }else {
            s+= troops + " troops under his command.\n";
        }
        if (hasChief){
            s+= "The assistance of the Chief of Staff.\n";
        }
        return s;
    }

    private String getStringLastLine(boolean user){
        String s = "\n";
        if (exposed || user){
            s+= getDescription();
            return s;
        }else {
            return s;
        }
    }

    public String getAllocateStringOnlyUser() {
        String s = getStringFirstLine(true);
        s+= getStringLastLine(true);
        return s;
    }

    public String getAllocateStringOnlyTurn(Player player) {
        String s = getStringFirstLine(false);
        s+= player.getUnassisgnedTroops(this) + " unassigned troops he is connected to.\n";
        s+= getStringLastLine(false);
        return s;
    }

    public String getAllocateStringBoth(Player player) {
        String s = getStringFirstLine(true);
        s+= player.getUnassisgnedTroops(this) + " unassigned troops he is connected to.\n";
        s+= getStringLastLine(true);
        return s;
    }

    public String getAllocateStringNeither() {
        String s = getStringFirstLine(false);
        s+= getStringLastLine(false);
        return s;
    }



    public String getMoveStringOnlyUser(Board board) {
        String s = getStringFirstLine(true);
        s+= getMoveMiddleString(true, false, board);
        s+= getStringLastLine(true);
        return s;
    }

    public String getMoveStringOnlyTurn(Board board) {
        String s = getStringFirstLine(false);
        s+= getMoveMiddleString(false, true, board);
        s+= getStringLastLine(false);
        return s;
    }

    public String getMoveStringBoth(Board board) {
        String s = getStringFirstLine(true);
        s+= getMoveMiddleString(true, true, board);
        s+= getStringLastLine(true);
        return s;
    }

    public String getMoveStringNeither(Board board) {
        String s = getStringFirstLine(false);
        s+= getMoveMiddleString(false, false, board);
        s+= getStringLastLine(false);
        return s;
    }

    private String getMoveMiddleString(boolean user, boolean turn, Board board){
        String s = "";
        if (turn){
            if (movementPoints == 1){
                s += movementPoints + " movement point.\n";
            }else{
                s += movementPoints + " movement points.\n";
            }
        }
        if (user){
            if (wantsChief){
                s+= "Orders to receive the Chief of Staff.\n";
            }
            if (iAmAssisting != null){
                General assisted = board.get(iAmAssisting).getAllianceGeneral(getAlliance());
                s+= "Orders to assist " + assisted.getAlliance().toString() + " General " + assisted.getName() + "\n";
            }
            if (!assistingMe.isEmpty()){
                for (Coords cords : assistingMe){
                    General assisting = board.get(cords).getAllianceGeneral(getAlliance());
                    s += "Orders to receive assistance from  " + assisting.getAlliance().toString() +
                            " General " + assisting.getName() + "\n";
                }
            }
        }
        return s;
    }
}
