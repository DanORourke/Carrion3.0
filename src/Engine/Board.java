package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import Engine.Piece.Supply;
import Engine.Piece.Town;
import GUI.Coords;
import GUI.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Board {
    //private final int gameType;
    //private final int mapRadius;
    private HashMap<Coords, Parcel> board;
    private HashMap<Coords, GameData> changeData = new HashMap<>();

    Board(int gameType, int mapRadius, ArrayList<General> generals){
        //this.gameType = gameType;
        //this.mapRadius = mapRadius;
        createBoard(mapRadius);
        populateBoard(gameType, generals);
    }

    Board(Board board1){
        this.board = new HashMap<>();
        HashMap<Coords, Parcel> oldBoard = board1.getTotalBoard();
        for (Coords c : oldBoard.keySet()){
            Coords c2 = new Coords(c);
            Parcel p2 = new Parcel(oldBoard.get(c));
            board.put(c2, p2);
        }
    }

    private void createBoard(int mapRadius){
        board = new HashMap<>();
        board.put(new Coords(0, 0, 0), new Parcel());
        for (int i = 1; i < mapRadius; i++) {
            board.put(new Coords(i, -i, 0), new Parcel());
            board.put(new Coords(-i, i, 0), new Parcel());
            board.put(new Coords(i, 0, -i), new Parcel());
            board.put(new Coords(-i, 0, i), new Parcel());
            board.put(new Coords(0, i, -i), new Parcel());
            board.put(new Coords(0, -i, i), new Parcel());
            for (int k = 1; k < i; k++) {
                board.put(new Coords(-i, k, (i - k)), new Parcel());
                board.put(new Coords(i, -k, -(i - k)), new Parcel());
                board.put(new Coords(k, -i, (i - k)), new Parcel());
                board.put(new Coords(-k, i, -(i - k)), new Parcel());
                board.put(new Coords(-k, -(i - k), i), new Parcel());
                board.put(new Coords(k, (i - k), -i), new Parcel());
            }
        }
        System.out.println(board.size() + " " + mapRadius);
    }

    HashMap<Coords, Parcel> getTotalBoard(){
        return board;
    }

    private void populateBoard(int gameType, ArrayList<General> generals){
        setUnoccupiedTowns();
        ArrayList<Alliance>  players = new ArrayList<>();
        if (gameType == 0){
            players.add(Alliance.RED);
            players.add(Alliance.ORANGE);
        }else if (gameType == 1){
            players.add(Alliance.RED);
            players.add(Alliance.YELLOW);
        }else if (gameType == 2){
            players.add(Alliance.RED);
            players.add(Alliance.GREEN);
        }else if (gameType == 3){
            players.add(Alliance.RED);
            players.add(Alliance.YELLOW);
            players.add(Alliance.BLUE);
        }else if (gameType == 4){
            players.add(Alliance.YELLOW);
            players.add(Alliance.ORANGE);
            players.add(Alliance.BLUE);
            players.add(Alliance.PURPLE);
        }else if (gameType == 5){
            players.add(Alliance.RED);
            players.add(Alliance.YELLOW);
            players.add(Alliance.ORANGE);
            players.add(Alliance.BLUE);
            players.add(Alliance.PURPLE);
        }else if (gameType == 6){
            players.add(Alliance.RED);
            players.add(Alliance.YELLOW);
            players.add(Alliance.GREEN);
            players.add(Alliance.ORANGE);
            players.add(Alliance.BLUE);
            players.add(Alliance.PURPLE);
        }
        int i = 0;
        for (Alliance a : players){
            setPlayer(a, generals.subList((i*5), ((i*5)+5)));
            i++;
        }
    }

    private void updateMap(HashMap<Coords, Parcel> parcels){
        for (Coords c: parcels.keySet()){
            board.put(c, parcels.get(c));
        }
    }

    void clearChangeData(){
        changeData.clear();
    }

    HashMap<Coords, GameData> getChangeData(){
        return changeData;
    }

    private void setUnoccupiedTowns(){
        HashMap<Coords, Parcel> parcels = Alliance.UNOCCUPIED.getInitialSetup(null);
        updateMap(parcels);
    }

    private void setPlayer(Alliance a, List<General> generals){
        HashMap<Coords, Parcel> parcels = a.getInitialSetup(generals);
        for (Coords c : board.keySet()){
            if (a.inMyTerritory(c)){
                if (parcels.containsKey(c)){
                    Parcel p = parcels.get(c);
                    HashMap<Integer, Piece> pieces = p.getPieces();
                    parcels.put(c, new Parcel(a, pieces));
                }else{
                    parcels.put(c, new Parcel(a));
                }
            }
        }
        updateMap(parcels);
    }

    public Parcel get(Coords c){
        return board.get(c);
    }

    void moveGeneral(General g, Coords next){
        removePiece(g);
        General gen = g.createNewMoved(next, 1);
        addPiece(gen);
        removeHelpFromGeneralIAmAssisting(g);
        removeAssistingFromGeneralsAssistingMe(g);
    }

    void stopAssisting(General g){
        General gen = g.createNewClearAssisting();
        removePiece(g);
        addPiece(gen);
    }

    private void removeHelpFromGeneralIAmAssisting(General g){
        if (g.getiAmAssisting() != null){
            General assisted = board.get(g.getiAmAssisting()).getAllianceGeneral(g.getAlliance());
            General nAssisted = assisted.createNewRemoveAssistingMe(g.getCoords());
            removePiece(assisted);
            addPiece(nAssisted);
        }
    }
    private void removeAssistingFromGeneralsAssistingMe(General g){
        for (Coords c : g.getAssistingMe()){
            General assisting = getAssistingGeneral(c);
            General nAssisting = assisting.createNewClearAssisting();
            removePiece(assisting);
            addPiece(nAssisting);
        }
    }

    private void removePiece(Piece p){
        Coords c = p.getCoords();
        Parcel op = board.get(c);
        HashMap<Integer, Piece> oPieces = op.getPieces();
        HashMap<Integer, Piece> nPieces = new HashMap<>();
        for (int t : oPieces.keySet()){
            Piece oPiece = oPieces.get(t);
            if (!p.equals(oPiece)){
                nPieces.put(t, oPiece.copy());
            }
        }
        Parcel np =  new Parcel(op.getTerritory(), nPieces);
        HashMap<Integer, Piece> wht = np.getPieces();
        board.put(c,np);
        changeData.put(c, np.getGD());
    }

    private void addPiece(Piece p){
        Coords c = p.getCoords();
        Parcel op = board.get(c);
        HashMap<Integer, Piece> oPieces = op.getPieces();
        HashMap<Integer, Piece> nPieces = new HashMap<>();
        for (int t : oPieces.keySet()){
            nPieces.put(t, oPieces.get(t).copy());
        }
        if (p.isGeneral()){
            if (!op.hasGeneral()){
                nPieces.put(1, p.copy());
            }else if(op.hasSingleGeneral()) {
                nPieces.put(2, p.copy());
            }
            //why dont i need to worry about this?????
            //i do, but only when exposing generals before battles????
            //should be fixed
            else if (op.hasGeneral()){
                //if have a general in the second position
                //did it this way so new generals go where they came from
                nPieces.put(1, p.copy());
            }
        }else{
            nPieces.put(p.getType(), p.copy());
        }
        Parcel np =  new Parcel(op.getTerritory(), nPieces);
        board.put(c,np);
        changeData.put(c, np.getGD());
    }

    void dropAttackerDown(General g){
        removePiece(g);
        addPiece(g.copy());
    }

    void occupyTown(General g){
        Coords c = g.getCoords();
        Town t = get(c).getTown();
        removePiece(t);
        Town nt  = new Town(c, g.getAlliance());
        addPiece(nt);
        General gen = g.createNewTroop(-1);
        General ngen = gen.createNewStuck();
        removePiece(g);
        addPiece(ngen);

//        if (!g.getLines()){
//            General ng = g.createNewLines();
//            General nng = ng.createNewMoved(c, 1);
//            removePiece(g);
//            addPiece(nng);
//        }
    }

    void razeTown(General g){
        System.out.println("razeTown called");
        Coords c = g.getCoords();
        Town t = get(c).getTown();
        removePiece(t);
        Town nt  = new Town(c, Alliance.UNOCCUPIED);
        addPiece(nt);
    }

    void injureGeneral(General g, int amount){
        General nG = g.createNewTroop(amount * -1);
        removePiece(g);
        addPiece(nG);
    }

    void killGeneral(General g){
        removePiece(g);
    }

    void dropSupply(General g){
        Coords c = g.getCoords();
        Supply supply  = new Supply(c, g.getAlliance());
        addPiece(supply);
        addTroops(g, -1);
        if (!g.getLines()){
            General ng = g.createNewLines();
            General nng = ng.createNewMoved(c, 1);
            removePiece(g);
            addPiece(nng);
            addTroops(nng, -1);
        }
    }

    void cutSupply(General g){
        Coords c = g.getCoords();
        Supply s = board.get(c).getSupply();
        removePiece(s);
        if (!g.getLines()){
            General ng = g.createNewLines();
            General nng = ng.createNewMoved(c, 1);
            removePiece(g);
            addPiece(nng);
        }
    }

    void addTroops(General g, int n){
        removePiece(g);
        General gen = g.createNewTroop(n);
        addPiece(gen);
    }

    void setFightingGeneral(General g, Coords launchPoint, boolean dropAfterWin){
        Parcel par = board.get(g.getCoords());
        if (par.isGeneralBattle()){
            General oldDefender = par.getFirstGeneral();
            //tell general i am assisting that i am no longer assisting him
            removeHelpFromGeneralIAmAssisting(oldDefender);
            General nD = oldDefender.createNewFighting(launchPoint, false);
            removePiece(oldDefender);
            addPiece(nD);
        }
        General ng = g.createNewFighting(launchPoint, dropAfterWin);
        removeHelpFromGeneralIAmAssisting(g);
        removePiece(g);
        addPiece(ng);
    }

    void subtractTroopFromTown(Piece p){
        if (p.getType() == 6){
            Town t = ((Town)p).createNewTroop(false);
            //System.out.println("remove troop from town " + ((Town) p).hasTroop() + " " + t.hasTroop());
            removePiece(p);
            //System.out.println(board.get(p.getCoords()).getTown().hasTroop());
            addPiece(t);
            //System.out.println(board.get(p.getCoords()).getTown().hasTroop());
        }else{
            //System.out.println("remove troop from cap");
            Capitol cap = ((Capitol)p).createNewTroops(-1);
            removePiece(p);
            addPiece(cap);
        }
    }

    void setChiefOrders(General g, boolean wantsChief){
        General ng = g.createNewWantsChief(wantsChief);
        removePiece(g);
        addPiece(ng);
    }

    void setChiefOrders(Capitol cap, boolean wantsChief){
        Capitol ncap = cap.createNewWantsChief(wantsChief);
        removePiece(cap);
        addPiece(ncap);
    }

    void removeChief(Piece p){
        if (p.getType() > 0 && p.getType() < 6){
            setChief((General)p, false);
        }else if (p.getType() == 7){
            setChief((Capitol) p, false);
        }
    }

    void addChief(Piece p){
        if (p.getType() > 0 && p.getType() < 6){
            setChief((General)p, true);
        }else if (p.getType() == 7){
            setChief((Capitol) p, true);
        }
    }

    private void setChief(General g, boolean hasChief){
        General ng = g.createNewHasChief(hasChief);
        removePiece(g);
        addPiece(ng);
    }

    private void setChief(Capitol cap, boolean hasChief){
        Capitol ncap = cap.createNewHasChief(hasChief);
        removePiece(cap);
        addPiece(ncap);
    }

    void setExposedGeneral(General g){
        General ng = g.createNewExposed(true);
        removePiece(g);
        addPiece(ng);
    }

    void resetGeneralMove(General g){
        General ng = g.resetGeneralMove();
        removePiece(g);
        addPiece(ng);
    }

    void resetTownAllocate(Town t) {
        Town nt = t.resetTownAllocate();
        removePiece(t);
        addPiece(nt);
    }

    void resetCapAllocate(Capitol cap) {
        Capitol nCap = cap.resetCapitolAllocate();
        removePiece(cap);
        addPiece(nCap);
    }

    ArrayList<General> getBattleExpose(){
        ArrayList<General> generals = new ArrayList<>();
        for (Coords c : board.keySet()){
            Parcel p = board.get(c);
            if (p.isBattle()){
                if (p.isGeneralBattle()){
                    General d = p.getFirstGeneral();
                    General a = p.getSecondGeneral();
                        if (!d.isExposed()){
                            generals.add(d);
                        }
                        if (!a.isExposed()){
                            generals.add(a);
                        }
                }else {
                    General a = p.getFirstGeneral();
                    if (!a.isExposed()){
                        generals.add(a);
                    }
                }
            }
        }
        return  generals;
    }

    ArrayList<Coords> getBattleCoords(){
        ArrayList<Coords> bCoords = new ArrayList<>();
        for (Coords c : board.keySet()){
            Parcel p = board.get(c);
            if (p.isCapitolBattle() || p.isDefendedCapitolBattle()){
                bCoords.add(c);
            }
        }
        if (bCoords.isEmpty()){
            for (Coords c : board.keySet()){
                Parcel p = board.get(c);
                if (p.isBattle()){
                    bCoords.add(c);
                }
            }
        }
        return bCoords;
    }

    public General getAssistingGeneral(Coords c){
        Parcel p = board.get(c);
        //attacking gen cant be assisting
        return p.getFirstGeneral();
    }

    void setAssist(General assistingG, General assistedG){
        General nAssisting = assistingG.createNewAssisting(assistedG.getCoords());
        removePiece(assistingG);
        addPiece(nAssisting);
        General nAssisted = assistedG.createNewAssisted(assistingG.getCoords());
        removePiece(assistedG);
        addPiece(nAssisted);
    }

    Coords calcRetreat(Coords battle, Coords launch, Alliance retreatingAlliance, boolean attackerWon){
        System.out.println("launch " + launch.toString() + "battle " + battle.toString());
        if (attackerWon){
            ArrayList<Coords> options = new ArrayList<>();
            for (Coords c : board.keySet()){
                if(c.isNextTo(battle) && !(c.isNextTo(launch) || c.equals(launch))){
                    options.add(c);
                }
            }
            return pickARetreat(options, retreatingAlliance);
        }else{
            ArrayList<Coords> options = new ArrayList<>();
            options.add(launch);
            for (Coords c : board.keySet()){
                if(c.isNextTo(battle) && c.isNextTo(launch)){
                    options.add(c);
                }
            }
            return pickARetreat(options, retreatingAlliance);
        }
    }

    private Coords pickARetreat(ArrayList<Coords> options, Alliance retreatingAlliance){
        ArrayList<Coords> noGood = new ArrayList<>();
        for (Coords c : options){
            Parcel p = board.get(c);
            if (p.hasGeneral() || (p.hasTown() && !p.getTown().getAlliance().equals(retreatingAlliance)) ||
                    (p.hasCapitol() && !p.getCapitol().getAlliance().equals(retreatingAlliance)) ||
                    (p.hasSupplyLine() && !p.getSupply().getAlliance().equals(retreatingAlliance))){
                noGood.add(c);
            }
        }
        options.removeAll(noGood);
        if (options.isEmpty()){
            return null;
        }
        else{
            Random rand = new Random();
            return options.get(rand.nextInt(options.size()));
        }
    }

    void killPlayer(Player dead, Alliance killer){
        ArrayList<General> traitors = new ArrayList<>();
        for (Piece p : dead.getPieces()){
            if (p.isSupply()){
                removePiece(p);
            }else if (p.isGeneral()){
                Parcel parc = board.get(p.getCoords());
                if (parc.hasSingleGeneral()){
                    traitors.add((General)p);
                }
                removePiece(p);
            }else{
                //is town or capitol
                Town ghost = new Town(p.getCoords(), Alliance.UNOCCUPIED);
                removePiece(p);
                addPiece(ghost);
            }
        }
//        if (!traitors.isEmpty()){
//            General gen = traitors.get(new Random().nextInt(traitors.size())).createNewTraitor(killer);
//            addPiece(gen);
//        }
        for (General g : traitors){
            //turn all non fighting generals
            General gen = g.createNewTraitor(killer);
            addPiece(gen);
        }
    }
}
