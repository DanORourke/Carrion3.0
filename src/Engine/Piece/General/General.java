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
        return new General(getCoords(), getType(), getAlliance(), name, troops, movementPoints, hasChief, false,
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

    public General resetGeneralAllocate(){
        return new General(getCoords(), getType(), getAlliance(), name,
                troops, movementPoints, hasChief, wantsChief, exposed, lines,
                launchPoint, dropAfterWin, iAmAssisting, assistingMe);
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
        }else if (!p.getTerritory().equals(getAlliance()) && !p.getTerritory().equals(Alliance.UNOCCUPIED)){
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
            return ((int)Math.ceil((double)troops / 4));
        }
        return 0;
    }

    public int getStandardAttackBonus(General other){
        if (other != null && other.getName().equals("Oda Nobunaga") && other.isExposed() && !other.hasChief()){
            return (int)Math.ceil(((double)troops * 4) / 3);
        }else if (other != null && other.getName().equals("Leonidas") && other.isExposed() && other.getTroops() < troops){
            return other.getTroops();
        }
        return troops;
    }

    public int getAttackBonus(Board board, General defender){
        int bonus = getStandardAttackBonus(defender);
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        return bonus;
    }

    public int getDefendBonus(Board board, General attacker){
        int bonus = getStandardAttackBonus(attacker);
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        bonus -= addDistractedBonus();
        return bonus;
    }

    public int getAttackTownBonus(Board board){
        int bonus = getStandardAttackBonus(null);
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        return bonus;
    }

    public int getAttackDefendedTownBonus(Board board, General defender){
        int bonus = getStandardAttackBonus(defender);
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        return bonus;
    }

    public int getDefendTownBonus(Board board, General attacker, Town t){
        int bonus = getStandardAttackBonus(attacker);
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        bonus -= addDistractedBonus();
        if (getAlliance().equals(t.getAlliance())){
            bonus += t.getDefendedBonus(attacker, this);
        }
        return bonus;
    }

    public int getAttackCapitolBonus(Board board){
        return getAttackTownBonus(board);
    }

    public int getAttackDefendedCapitolBonus(Board board, General defender, Capitol cap){
        int bonus = getStandardAttackBonus(defender);
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        return bonus;
    }

    public int getDefendCapitolBonus(Board board, General attacker, Capitol cap){
        int bonus = getStandardAttackBonus(attacker);
        bonus += addAssistingAttackBonus(board);
        bonus += addTerritoryBonus(board);
        bonus -= addDistractedBonus();
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
        if (g.getName().equals("Charlemagne") && g.isExposed()){
            return (int)Math.ceil((double)troops / 3);
        }else if (g.getName().equals("Leonidas") && g.isExposed() && g.getTroops() < troops){
            return (int)Math.ceil((double)g.getTroops() / 2);
        }
        return (int)Math.ceil((double)troops / 2);
    }

    public int getAttackCasualties(Board board, General defender){
        return getStandardCasualties(board, defender) + addAssistingCasualties(board);
    }

    public int getDefendCasualties(Board board, General attacker){
        return getStandardCasualties(board, attacker) + addAssistingCasualties(board);
    }

    public int getAttackDefendedTownCasualties(Board board, General defender, Town t){
        return getStandardCasualties(board, defender) + addAssistingCasualties(board);
    }

    public int getDefendTownCasualties(Board board, General attacker, Town t){
        int casualties = getStandardCasualties(board, attacker) + addAssistingCasualties(board);
        casualties += t.getCasualties(attacker);
        return casualties;
    }

    public int getDefendCapitalCasualties(Board board, General attacker, Capitol cap){
        int casualties = getStandardCasualties(board, attacker) + addAssistingCasualties(board);
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
        Collections.addAll(allPossibleG, "Ramses II", "Charlemagne", "Leonidas",
                "Oda Nobunaga", "Alexander of Macedon");
        return allPossibleG;
    }

    private static General makeFromName(String name){
        if (name.equals("Ramses II")){
            return new Ramses();
        }else if (name.equals("Charlemagne")){
            return new Charlemagne();
        }else if (name.equals("Leonidas")){
            return new Leonidas();
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
                s+= "Orders to assist " + assisted.getAlliance().toString() + " General " + assisted.getName() + ".\n";
            }
            if (!assistingMe.isEmpty()){
                for (Coords cords : assistingMe){
                    General assisting = board.get(cords).getAllianceGeneral(getAlliance());
                    s += "Orders to receive assistance from  " + assisting.getAlliance().toString() +
                            " General " + assisting.getName() + ".\n";
                }
            }
        }
        return s;
    }

    public String getAllBattleString(General other, Board board, boolean attacker){
        String s = getStringFirstLine(true);
        for (Coords c : assistingMe){
            General ag = board.getAssistingGeneral(c);
            s+= "+" + ag.getAssistBonus(this) + " assist bonus from " + ag.getName() + "\n";
        }
        s+= "+" + addTerritoryBonus(board) + " territory bonus.\n";
        if (attacker){
            s+= "+" + getAttackBonus(board, other) + " total attack bonus.\n";
        }else{
            s+= "-" + addDistractedBonus() + " distracted penalty.\n";
            s+= "+" + getDefendBonus(board, other) + " total attack bonus.\n";
        }
        for (Coords c : assistingMe){
            General ag = board.getAssistingGeneral(c);
            s+= "+" + ag.getAssistCasualties(this) + " inflicted casualties from " + ag.getName() + "\n";
        }
        if (attacker){
            s+= "+" + getAttackCasualties(board, other) + " total inflicted casualties.\n";
        }else{
            s+= "+" + getDefendCasualties(board, other) + " total inflicted casualties.\n";
        }
        return s;
    }

    public String getOldBattleString(Board board, Piece p, int typeCode, boolean attacker){
        //typeCode, 1 = field, 2 = defended town, 3 = defended cap, 4 = town, 5 = cap
        if (typeCode == 1){
            General other = (General)p;
            String s = getAllBattleString(other, board, attacker);
            if (dropAfterWin){
                s+= "Orders to drop a supply line after securing victory.\n";
            }
            return s;
        }else if (typeCode == 2){
            General other = (General)p;
            Town t = board.get(getCoords()).getTown();
            String s = getStringFirstLine(true);
            for (Coords c : assistingMe){
                General ag = board.getAssistingGeneral(c);
                s+= "+" + ag.getAssistBonus(this) + " assist bonus from " + ag.getName() + "\n";
            }
            s+= "+" + addTerritoryBonus(board) + " territory bonus.\n";
            if (attacker){
                s+= "+" + getAttackDefendedTownBonus(board, other) + " total attack bonus.\n";
            }else{
                s+= "-" + addDistractedBonus() + " distracted penalty.\n";
                s+= "+" + t.getDefendedBonus(other, this) + " town fight bonus.\n";
                s+= "+" + getDefendTownBonus(board, other, t) + " total attack bonus.\n";
            }
            for (Coords c : assistingMe){
                General ag = board.getAssistingGeneral(c);
                s+= "+" + ag.getAssistCasualties(this) + " inflicted casualties from " + ag.getName() + "\n";
            }
            if (attacker){
                s+= "+" + getAttackDefendedTownCasualties(board, other, t) + " total inflicted casualties.\n";
                if (dropAfterWin){
                    s+= "Orders to occupy the town after securing victory.\n";
                }else {
                    s+= "Orders to raze the town after securing victory.\n";
                }
            }else{
                s+= "+" + t.getCasualties(other) + " inflicted casualties from the town.\n";
                s+= "+" + getDefendTownCasualties(board, other, t) + " total inflicted casualties.\n";
            }
            return s;
        }else if (typeCode == 3){
            General other = (General)p;
            Capitol cap = board.get(getCoords()).getCapitol();
            String s = getStringFirstLine(true);
            for (Coords c : assistingMe){
                General ag = board.getAssistingGeneral(c);
                s+= "+" + ag.getAssistBonus(this) + " assist bonus from " + ag.getName() + "\n";
            }
            s+= "+" + addTerritoryBonus(board) + " territory bonus.\n";
            if (attacker){
                s+= "+" + getAttackCapitolBonus(board) + " total attack bonus.\n";
            }else{
                s+= "-" + addDistractedBonus() + " distracted penalty.\n";
                s+= "+" + cap.getDefendBonus(other) + " capitol fight bonus.\n";
                s+= "+" + getDefendCapitolBonus(board, other, cap) + " total attack bonus.\n";
            }
            for (Coords c : assistingMe){
                General ag = board.getAssistingGeneral(c);
                s+= "+" + ag.getAssistCasualties(this) + " inflicted casualties from " + ag.getName() + "\n";
            }
            if (attacker){
                if (dropAfterWin){
                    s+= "Orders to occupy the town after securing victory.\n";
                }else {
                    s+= "Orders to raze the town after securing victory.\n";
                }
            }else{
                s+= "+" + cap.getCasualties(other) + " inflicted casualties from the town.\n";
                s+= "+" + getDefendCapitalCasualties(board, other, cap) + " total inflicted casualties.\n";
            }
            return s;
        }else if (typeCode == 4){
            //not always user, but will be exposed if not yet
            String s = getStringFirstLine(true);
            for (Coords c : assistingMe){
                General ag = board.getAssistingGeneral(c);
                s+= "+" + ag.getAssistBonus(this) + " assist bonus from " + ag.getName() + "\n";
            }
            s+= "+" + addTerritoryBonus(board) + " territory bonus.\n";
            s+= "+" + getAttackTownBonus(board) + " total attack bonus.\n";

            if (dropAfterWin){
                s+= "Orders to occupy the town after securing victory.\n";
            }else {
                s+= "Orders to raze the town after securing victory.\n";
            }
            return s;

        }else if (typeCode == 5){
            String s = getStringFirstLine(true);
            for (Coords c : assistingMe){
                General ag = board.getAssistingGeneral(c);
                s+= "+" + ag.getAssistBonus(this) + " assist bonus from " + ag.getName() + "\n";
            }
            s+= "+" + addTerritoryBonus(board) + " territory bonus.\n";
            s+= "+" + getAttackCapitolBonus(board) + " total attack bonus.\n";

            if (dropAfterWin){
                s+= "Orders to occupy the town after securing victory.\n";
            }else {
                s+= "Orders to raze the town after securing victory.\n";
            }
            return s;
        }
        return "OldBattle";
    }

    public String getActiveBattleString(Board board, Piece p, int typeCode, boolean attacker){
        //typeCode, 1 = field, 2 = defended town, 3 = defended cap, 4 = town, 5 = cap
        if (typeCode == 1){
            General other = (General)p;
            //attacker must be user team
            if (attacker){
                String s = getAllBattleString(other, board, attacker);
                if (dropAfterWin){
                    s+= "Orders to drop a supply line after securing victory.\n";
                }
                return s;
            }else {
                return getStringFirstLine(false);
            }

        }else if (typeCode == 2){
            General other = (General)p;
            Town t = board.get(getCoords()).getTown();
            if (attacker){
                String s = getStringFirstLine(true);
                for (Coords c : assistingMe){
                    General ag = board.getAssistingGeneral(c);
                    s+= "+" + ag.getAssistBonus(this) + " assist bonus from " + ag.getName() + "\n";
                }
                s+= "+" + addTerritoryBonus(board) + " territory bonus.\n";
                    s+= "+" + getAttackDefendedTownBonus(board, other) + " total attack bonus.\n";
                for (Coords c : assistingMe){
                    General ag = board.getAssistingGeneral(c);
                    s+= "+" + ag.getAssistCasualties(this) + " inflicted casualties from " + ag.getName() + "\n";
                }
                s+= "+" + getAttackDefendedTownCasualties(board, other, t) + " total inflicted casualties.\n";
                if (dropAfterWin){
                    s+= "Orders to occupy the town after securing victory.\n";
                }else {
                    s+= "Orders to raze the town after securing victory.\n";
                }
                return s;
            }else {
                String s = t.getBattleString(board, other);
                s += "Defending " + getStringFirstLine(false);
                return s;
            }
        }else if (typeCode == 3){
            General other = (General)p;
            Capitol cap = board.get(getCoords()).getCapitol();
            if (attacker){
                String s = getStringFirstLine(true);
                for (Coords c : assistingMe){
                    General ag = board.getAssistingGeneral(c);
                    s+= "+" + ag.getAssistBonus(this) + " assist bonus from " + ag.getName() + "\n";
                }
                s+= "+" + addTerritoryBonus(board) + " territory bonus.\n";
                s+= "+" + getAttackCapitolBonus(board) + " total attack bonus.\n";

                for (Coords c : assistingMe){
                    General ag = board.getAssistingGeneral(c);
                    s+= "+" + ag.getAssistCasualties(this) + " inflicted casualties from " + ag.getName() + "\n";
                }
                if (dropAfterWin){
                    s+= "Orders to occupy the town after securing victory.\n";
                }else {
                    s+= "Orders to raze the town after securing victory.\n";
                }

                return s;
            }else {
                String s = cap.getBattleString(other);
                s +=  "Defending " + getStringFirstLine(false);
                return s;
            }
        }else if (typeCode == 4){
            Town t = (Town)p;
            String s = getStringFirstLine(true);
            for (Coords c : assistingMe){
                General ag = board.getAssistingGeneral(c);
                s+= "+" + ag.getAssistBonus(this) + " assist bonus from " + ag.getName() + "\n";
            }
            s+= "+" + addTerritoryBonus(board) + " territory bonus.\n";
            s+= "+" + getAttackTownBonus(board) + " total fight bonus.\n";

            if (dropAfterWin){
                s+= "Orders to occupy the town after securing victory.\n";
            }else {
                s+= "Orders to raze the town after securing victory.\n";
            }
            return s;

        }else if (typeCode == 5){
            String s = getStringFirstLine(true);
            for (Coords c : assistingMe){
                General ag = board.getAssistingGeneral(c);
                s+= "+" + ag.getAssistBonus(this) + " assist bonus from " + ag.getName() + "\n";
            }
            s+= "+" + addTerritoryBonus(board) + " territory bonus.\n";
            s+= "+" + getAttackCapitolBonus(board) + " total attack bonus.\n";

            if (dropAfterWin){
                s+= "Orders to occupy the town after securing victory.\n";
            }else {
                s+= "Orders to raze the town after securing victory.\n";
            }
            return s;        }
        return "ActiveBattle";
    }
}
