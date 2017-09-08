package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import Engine.Piece.Town;
import GUI.Coords;
import GUI.GameData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Engine {
    private final ArrayList<GameState> history = new ArrayList<>();
    private int histIndex = -1;
    private final int mapRadius;
    private final int gameType;
    private General activeGeneral = null;
    private Coords activeCoords = null;
    private Coords nextActiveCoords = null;
    private HashMap<Alliance, Player> players = new HashMap<>();
    private Board board;
    private int playerTurn;
    private int turnStage;//0=allocate, 1=move, 2=special if needed
    private boolean rememberClick = false;
    private int indexOfNoChange = -1;
    private boolean assisting = false;
    private Coords assistingCoords = null;
    private boolean assisted = false;
    private boolean settingChief = false;
    private boolean exposingGeneral = false;

    public Engine(String encodedBoard){
        ArrayList<String> moves = new ArrayList<>(Arrays.asList(encodedBoard.split(",")));
        mapRadius = Integer.valueOf(moves.get(0));
        gameType = Integer.valueOf(moves.get(1));
        initializeEngine(moves);
    }


    private void initializeEngine(ArrayList<String> moves){
        initializeTurn();
        board = new Board(gameType, mapRadius);
        addStartHistory();
        int i = 2;
        while(i < moves.size()){
            String moveType = moves.get(i);
            if (moveType.equals("next")){
                nextPhase();
                i++;
            }else if (turnStage == 0){
                if (moveType.equals("expose")){
                    exposingGeneral = true;
                    int q = Integer.valueOf(moves.get(i+1));
                    int r = Integer.valueOf(moves.get(i+2));
                    int s = Integer.valueOf(moves.get(i+3));
                    //left click doesn't matter
                    click(new Coords(q, r, s), true);
                    i+=4;
                }else{
                    int q = Integer.valueOf(moves.get(i));
                    int r = Integer.valueOf(moves.get(i+1));
                    int s = Integer.valueOf(moves.get(i+2));
                    int leftClick = Integer.valueOf(moves.get(i+3));
                    click(new Coords(q, r, s), leftClick == 1);
                    i+=4;
                }
            }else{
                //turnstage == 1
                //q, r, s (of active coords)  ,q, r, s (of clicked coords) , boolean leftclick(0=false, 1=true)
                if (moveType.equals("chief")){
                    int q = Integer.valueOf(moves.get(i+1));
                    int r = Integer.valueOf(moves.get(i+2));
                    int s = Integer.valueOf(moves.get(i+3));
                    int leftClick = Integer.valueOf(moves.get(i+4));
                    settingChief = true;
                    setChiefOrders(new Coords(q, r, s), leftClick == 1);
                    i+= 5;
                }else if (moveType.equals("assist")){
                    int q = Integer.valueOf(moves.get(i+1));
                    int r = Integer.valueOf(moves.get(i+2));
                    int s = Integer.valueOf(moves.get(i+3));
                    assistingCoords = new Coords(q, r, s);
                    int qa = Integer.valueOf(moves.get(i+4));
                    int ra = Integer.valueOf(moves.get(i+5));
                    int sa = Integer.valueOf(moves.get(i+6));
                    setAssisted(new Coords(qa, ra, sa));
                    i+= 7;
                }else if (moveType.equals("battle")){
                    int q = Integer.valueOf(moves.get(i+1));
                    int r = Integer.valueOf(moves.get(i+2));
                    int s = Integer.valueOf(moves.get(i+3));
                    Coords c = new Coords(q, r, s);
                    int wonEnc = Integer.valueOf(moves.get(i+4));
                    Coords launch;
                    if (moves.get(i+5).equals("null")){
                        launch = null;
                        i+=6;
                    }else{
                        int ql = Integer.valueOf(moves.get(i+5));
                        int rl = Integer.valueOf(moves.get(i+6));
                        int sl = Integer.valueOf(moves.get(i+7));
                        launch = new Coords(ql, rl, sl);
                        i+=8;
                    }
                    afterBattle(c, wonEnc, launch);
                }else{
                    int qc = Integer.valueOf(moves.get(i));
                    int rc = Integer.valueOf(moves.get(i+1));
                    int sc = Integer.valueOf(moves.get(i+2));
                    activeCoords = new Coords(qc, rc, sc);
                    int q = Integer.valueOf(moves.get(i+3));
                    int r = Integer.valueOf(moves.get(i+4));
                    int s = Integer.valueOf(moves.get(i+5));
                    int leftClick = Integer.valueOf(moves.get(i+6));
                    click(new Coords(q, r, s), leftClick == 1);
                    i+=7;
                }
            }
        }
        System.out.println("history size: " + history.size());
    }

    private void addStartHistory(){
        String startEncoded = String.valueOf(mapRadius) + "," + String.valueOf(gameType);
        history.add(new GameState(board, null, playerTurn, turnStage, startEncoded));
        histIndex++;
    }

    private void addClickToHistory(Coords c, boolean leftClick){
        String encoded = createClickEncoded(c, leftClick);
        System.out.println(encoded);
        history.add(new GameState(board, activeCoords, playerTurn, turnStage, encoded));
        histIndex ++;
    }

    private String createClickEncoded(Coords c, boolean leftClick){
        //could remove s coord and calculate it up top instead, q + r + s = 0, but might keep it to use as verification
        System.out.println("histIndex: " + histIndex);
        String oldEncoded = history.get(histIndex).getEncodedBoard();
        String left;
        if (leftClick){
            left = "1";
        }else{
            left = "0";
        }
        if (turnStage == 0){
            return oldEncoded + "," + c.toString() + "," + left;

        }else {
            return oldEncoded + "," + activeCoords.toString() + "," + c.toString() + "," + left;
        }
    }

    private void clearFutureHistory(){
        if (history.size()-1 > histIndex){
            history.subList(histIndex + 1, history.size()).clear();
        }
    }

    public void nextPhase(){
        histIndex = history.size() - 1;
        setState();
        rotatePhase();
        String oldEncoded = history.get(history.size() - 1).getEncodedBoard();
        String encoded = nextEncoded(oldEncoded);
        history.add(new GameState(new Board(board), null,  playerTurn, turnStage, encoded));
        histIndex = history.size() - 1;
    }

    private String nextEncoded(String oldEncoded){
        //thinking of condensing the allocate clicks
        if (turnStage == 0){
            return oldEncoded + ",next";

        }else {
            return oldEncoded + ",next";
        }
    }

    private void rotatePhase(){
        if (turnStage == 0){
            turnStage = 1;
            Player active = getActivePlayer();
            if (active != null){
                active.resetPlayerPiecesMove(board);
            }
        }else if (turnStage == 1){
            rotatePlayer();
            turnStage = 0;
        }

        settingChief = false;
        exposingGeneral = false;
        assisting = false;
        assisted = false;
    }

    private void rotatePlayer(){
        condenseAllocateHistory();
        histIndex = history.size() - 1;
        //do stuff if battles need to happen
        playoutBattles();
        //move the chief if someone else wants him and he is connected
        moveChief();
        boolean looking = true;
        while(looking){
            playerTurn++;
            if (playerTurn > 6){
                playerTurn = 1;
            }
            //must be a better way to do this
            for (Alliance a : players.keySet()){
                if (a.getDataCode() == playerTurn){
                    players.get(a).resetPlayerPiecesAllocate(board);
                    looking = false;
                }
            }
        }
        indexOfNoChange = histIndex;
    }

    private void playoutBattles(){
        ArrayList<Coords> bCoords = board.getBattleCoords();
        if (!bCoords.isEmpty()){
            battle(bCoords.get(0));
        }
    }

    private void addToHistory(String newOrders){
        String oldEncoded = history.get(history.size() - 1).getEncodedBoard();
        history.add(new GameState(new Board(board), null,  playerTurn, turnStage, oldEncoded + newOrders));
        histIndex = history.size() - 1;
        setState();
    }

    private void battle(Coords c){
        // get attacker and defender
        Parcel parcel = board.get(c);
        General ga = parcel.getAttacker();
        Piece p = parcel.getDefender();

        if (parcel.isFieldBattle()){
            General gd = (General)p;
            int aBonus = ga.getAttackBonus(board);
            int dBonus = gd.getDefendBonus(board);
            Random rand = new Random();
            int attack = aBonus + rand.nextInt(20) + 1;
            int defence = dBonus + rand.nextInt(20) + 1;
            if (attack > defence) {
                Coords retreat = board.calcRetreat(c, gd.getLaunchPoint(), gd.getAlliance(), true);
                afterBattle(c, 1, retreat);
            }else{
                Coords retreat = board.calcRetreat(c, ga.getLaunchPoint(), ga.getAlliance(), false);
                afterBattle(c, 0, retreat);
            }

        }else if (parcel.isDefendedTownBattle()){
            Town t = parcel.getTown();
            General gd = (General)p;
            int aBonus = ga.getAttackDefendedTownBonus(board, t);
            int dBonus = gd.getDefendTownBonus(board, t);
            Random rand = new Random();
            int attack = aBonus + rand.nextInt(20) + 1;
            int defence = dBonus + rand.nextInt(20) + 1;
            if (attack > defence) {
                Coords retreat = board.calcRetreat(c, gd.getLaunchPoint(), gd.getAlliance(), true);
                afterBattle(c, 1, retreat);
            }else{
                Coords retreat = board.calcRetreat(c, ga.getLaunchPoint(), ga.getAlliance(), false);
                afterBattle(c, 0, retreat);
            }

        }else if (parcel.isDefendedCapitolBattle()){
            Capitol cap = parcel.getCapitol();
            General gd = (General)p;
            int aBonus = ga.getAttackDefendedCapitolBonus(board, cap);
            int dBonus = gd.getDefendCapitolBonus(board, cap);
            Random rand = new Random();
            int attack = aBonus + rand.nextInt(20) + 1;
            int defence = dBonus + rand.nextInt(20) + 1;
            if (attack > defence) {
                afterBattle(c, 1, null);
            }else{
                Coords retreat = board.calcRetreat(c, ga.getLaunchPoint(), ga.getAlliance(), false);
                afterBattle(c, 0, retreat);
            }
        }else if (parcel.isTownBattle()){
            int aBonus = ga.getAttackTownBonus(board);
            Town t = (Town) p;
            int tBonus = t.getAttackBonus();
            Random rand = new Random();
            int attack = aBonus + rand.nextInt(20) + 1;
            int defence = tBonus + rand.nextInt(20) + 1;
            if (attack > defence){
                afterBattle(c, 1, null);
            }else{
                //defence wins
                Coords retreat = board.calcRetreat(c, ga.getLaunchPoint(), ga.getAlliance(), false);
                afterBattle(c, 0, retreat);
            }

        }else if (parcel.isCapitolBattle()){
            int aBonus = ga.getAttackCapitolBonus(board);
            Capitol cap = (Capitol) p;
            int capBonus = cap.getAttackBonus();
            Random rand = new Random();
            int attack = aBonus + rand.nextInt(20) + 1;
            int defence = capBonus + rand.nextInt(20) + 1;
            if (attack > defence){
                afterBattle(c, 1, null);
            }else{
                //defence wins
                Coords retreat = board.calcRetreat(c, ga.getLaunchPoint(), ga.getAlliance(), false);
                afterBattle(c, 0, retreat);
            }
        }
    }

    private void afterBattle(Coords battleField, int attackerWon, Coords retreat){
        //attackerWon == 1 equals true, 0 equals false
        Parcel parcel = board.get(battleField);
        General ga = parcel.getAttacker();
        Piece p = parcel.getDefender();

        if (parcel.isFieldBattle()){
            General gd = (General) p;
            if (attackerWon == 1){
                int casualties = ga.getCasualties(board);
                if (gd.canSufferCasualties(casualties) && retreat != null){
                    board.injureGeneral(gd, casualties);
                    gd = board.get(battleField).getAllianceGeneral(gd.getAlliance());
                    board.moveGeneral(gd, retreat);
                }else {
                    board.killGeneral(gd);
                    fillPlayers();
                    Player killed = players.get(gd.getAlliance());
                    if (killed.noGenerals()){
                        System.out.println("kill player " + gd.getAlliance().toString());
                        board.killPlayer(killed, ga.getAlliance());
                    }
                }
                if (ga.getDropAfterWin()){
                    board.dropSupply(ga);
                }
            }else{
                int casualties = gd.getCasualties(board);
                board.stopAssisting(gd);
                if (ga.canSufferCasualties(casualties) && retreat != null){
                    board.injureGeneral(ga, casualties);
                    ga = board.get(battleField).getAllianceGeneral(ga.getAlliance());
                    board.moveGeneral(ga, retreat);
                }else {
                    board.killGeneral(ga);
                    fillPlayers();
                    Player killed = players.get(ga.getAlliance());
                    if (killed.noGenerals()){
                        System.out.println("kill player " + ga.getAlliance().toString());
                        board.killPlayer(killed, gd.getAlliance());
                    }
                }
            }
        }else if (parcel.isDefendedTownBattle()){
            Town t = parcel.getTown();
            General gd = (General)p;
            if (attackerWon == 1){
                board.razeTown(ga);
                ga = board.get(battleField).getAllianceGeneral(ga.getAlliance());
                int casualties = ga.getAttackDefendedTownCasualties(board, t);
                if (gd.canSufferCasualties(casualties) && retreat != null){
                    board.injureGeneral(gd, casualties);
                    gd = board.get(battleField).getAllianceGeneral(gd.getAlliance());
                    board.moveGeneral(gd, retreat);
                }else {
                    board.killGeneral(gd);
                    fillPlayers();
                    Player killed = players.get(gd.getAlliance());
                    if (killed.noGenerals()){
                        System.out.println("kill player " + gd.getAlliance().toString());
                        board.killPlayer(killed, ga.getAlliance());
                    }
                }
                if (ga.getDropAfterWin()){
                    board.occupyTown(ga);
                }

            }else{
                int casualties = gd.getDefendTownCasualties(board, t);
                board.stopAssisting(gd);
                if (ga.canSufferCasualties(casualties) && retreat != null){
                    board.injureGeneral(ga, casualties);
                    ga = board.get(battleField).getAllianceGeneral(ga.getAlliance());
                    board.moveGeneral(ga, retreat);
                }else {
                    board.killGeneral(ga);
                    fillPlayers();
                    Player killed = players.get(ga.getAlliance());
                    if (killed.noGenerals()){
                        System.out.println("kill player " + ga.getAlliance().toString());
                        board.killPlayer(killed, gd.getAlliance());
                    }
                }
            }

        }else if (parcel.isDefendedCapitolBattle()){
            Capitol cap = parcel.getCapitol();
            General gd = (General)p;
            if (attackerWon == 1){
                fillPlayers();
                Player killed = players.get(gd.getAlliance());
                System.out.println("kill player " + gd.getAlliance().toString());
                board.killPlayer(killed, ga.getAlliance());
                ga = board.get(battleField).getAllianceGeneral(ga.getAlliance());
                if (ga.getDropAfterWin()){
                    board.occupyTown(ga);
                }

            }else{
                int casualties = gd.getDefendCapitalCasualties(board, cap);
                board.stopAssisting(gd);
                if (ga.canSufferCasualties(casualties) && retreat != null){
                    board.injureGeneral(ga, casualties);
                    ga = board.get(battleField).getAllianceGeneral(ga.getAlliance());
                    board.moveGeneral(ga, retreat);
                }else {
                    board.killGeneral(ga);
                    fillPlayers();
                    Player killed = players.get(ga.getAlliance());
                    if (killed.noGenerals()){
                        System.out.println("kill player " + ga.getAlliance().toString());
                        board.killPlayer(killed, gd.getAlliance());
                    }
                }
            }

        }else if (parcel.isTownBattle()){
            if (attackerWon == 1){
                board.razeTown(ga);
                ga = board.get(battleField).getFirstGeneral();
                if (ga.getDropAfterWin()){
                    board.occupyTown(ga);
                }
            }else{
                Town t = (Town)p;
                if (ga.canLoseToTown(t.getCasualties()) && retreat != null){
                    board.injureGeneral(ga, t.getCasualties());
                    ga = board.get(battleField).getFirstGeneral();
                    board.moveGeneral(ga, retreat);
                }else{
                    board.killGeneral(ga);
                    fillPlayers();
                    Player killed = players.get(ga.getAlliance());
                    if (killed.noGenerals()){
                        System.out.println("kill player " + ga.getAlliance().toString());
                        board.killPlayer(killed, t.getAlliance());
                    }
                }
            }

        }else if (parcel.isCapitolBattle()){
            Capitol cap = (Capitol) p;
            if (attackerWon == 1){
                fillPlayers();
                Player killed = players.get(cap.getAlliance());
                System.out.println("kill player " + ga.getAlliance().toString());
                board.killPlayer(killed, ga.getAlliance());
                ga = board.get(battleField).getFirstGeneral();
                if (ga.getDropAfterWin()){
                    board.occupyTown(ga);
                }
            }else{
                if (ga.canLoseToCap(cap.getCasualties()) && retreat != null){
                    board.injureGeneral(ga, cap.getCasualties());
                    ga = board.get(battleField).getFirstGeneral();
                    board.moveGeneral(ga, retreat);
                }else{
                    board.killGeneral(ga);
                    fillPlayers();
                    Player killed = players.get(ga.getAlliance());
                    if (killed.noGenerals()){
                        System.out.println("kill player " + ga.getAlliance().toString());
                        board.killPlayer(killed, cap.getAlliance());
                    }
                }
            }
        }

        String re = "null";
        if (retreat != null){
            re = retreat.toString();
        }
        addToHistory(",battle," + battleField.toString() + "," + String.valueOf(attackerWon) + "," + re);
        playoutBattles();
    }

    private void moveChief(){
        Player aP = getActivePlayer();
        if (aP != null && aP.willMoveChief()){
            Piece[] pieces = aP.moveChief();
            board.removeChief(pieces[0]);
            board.addChief(pieces[1]);
            String oldEncoded = history.get(history.size() - 1).getEncodedBoard();
            history.add(new GameState(new Board(board), null,  playerTurn, turnStage, oldEncoded));
            histIndex = history.size() - 1;
            setState();
        }
    }

    private void condenseAllocateHistory(){
        //remove all allocate history except for first, make easier to click through
        int nextNext = 0;
        int i = indexOfNoChange + 2;
        boolean searching = true;
        while (searching){
            if (history.get(i).getTurnStage() != 0){
                nextNext = i;
                searching = false;
            }
            i++;
        }
        history.subList(indexOfNoChange + 2, nextNext).clear();
    }

    private Player getActivePlayer(){
        for (Alliance a : players.keySet()){
            if (a.getDataCode() == playerTurn){
                return players.get(a);
            }
        }
        return null;
    }

    private Alliance getUserTeam(){
        int latest = history.get(history.size() - 1).getPlayerTurn();
        for (Alliance a : players.keySet()){
            if (a.getDataCode() == latest){
                return a;
            }
        }
        return null;
    }

    private void fillPlayers(){
        players = new HashMap<>();
        HashMap<Coords, Parcel> totalBoard = board.getTotalBoard();
        for (Coords c : totalBoard.keySet()){
            Parcel p = totalBoard.get(c);
            if (!p.isEmpty()){
                addPlayerPieces(p.getPieces());
            }
        }
    }

    private void initializeTurn(){
        if (gameType == 0){
            playerTurn = 1;
            turnStage = 0;
        }else if (gameType == 1){
            playerTurn = 1;
            turnStage = 0;
        }else if (gameType == 2){
            playerTurn = 1;
            turnStage = 0;
        }else if (gameType == 3){
            playerTurn = 1;
            turnStage = 0;
        }else if (gameType == 4){
            playerTurn = 2;
            turnStage = 0;
        }else if (gameType == 5){
            playerTurn = 1;
            turnStage = 0;
        }else if (gameType == 6){
            playerTurn = 1;
            //for testing, should be 0
            turnStage = 0;
        }
    }

    public HashMap<Coords, GameData> getTotalUpdate(){
        players.clear();
        setState();
        HashMap<Coords, GameData> changeData = new HashMap<>();
        HashMap<Coords, Parcel> totalBoard = board.getTotalBoard();
        for (Coords c : totalBoard.keySet()){
            Parcel p = totalBoard.get(c);
            if (!p.isEmpty() || p.getTerritory() != Alliance.UNOCCUPIED){
                changeData.put(c, p.getGD());
            }
        }
        return changeData;
    }

    private void addPlayerPieces(HashMap<Integer, Piece> pieces){
        for (int i : pieces.keySet()){
            addPlayerPiece(pieces.get(i));
        }
    }

    private void addPlayerPiece(Piece p){
        Alliance a = p.getAlliance();
        if (!players.containsKey(a)){
            players.put(a, new Player(a));
        }
        Player player = players.get(a);
        player.addPiece(p);
    }

    public void clearActive(){
        activeGeneral = null;
        activeCoords = null;
    }

    public int[] getStateInfo(){
        int canAct = 0;
        if (histIndex > indexOfNoChange){
            canAct = 1;
        }
        return new int[]{playerTurn, turnStage, canAct};
    }

    public String hoverTileInfo(Coords c){
        //Board board = getBoard();
        String s = "";
        Parcel activeParcel = board.get(c);
        Alliance userTeam = getUserTeam();
        if (turnStage == 0){
            if (activeParcel.hasTown() && activeParcel.getTown().getAlliance().equals(userTeam)){
                s = userTeam.toString() + " town has troop to give: " + activeParcel.getTown().hasTroop() + "\n";
            }
            if (activeParcel.hasCapitol() && activeParcel.getCapitol().getAlliance().equals(userTeam)){
                s = userTeam.toString() + " capitol has " + activeParcel.getCapitol().getTroops() + " troops to give.\n";
            }
            if (activeParcel.hasSingleGeneral() && activeParcel.getFirstGeneral().getAlliance().equals(userTeam)){
                s += userTeam.toString() + " General " + activeParcel.getFirstGeneral().getType() + " has:\n" +
                        activeParcel.getFirstGeneral().getTroops() + " troops under his command.";
            }
            if (activeParcel.hasSingleGeneral() && !activeParcel.getFirstGeneral().getAlliance().equals(userTeam)){
                s = activeParcel.getFirstGeneral().getAlliance().toString() + " General " +
                        activeParcel.getFirstGeneral().getType() + " has:\n" +
                        activeParcel.getFirstGeneral().getTroops() + " troops under his command.";
            }
        }else{
            if (activeParcel.isTownBattle()){
                General g = activeParcel.getFirstGeneral();
                Town t = activeParcel.getTown();
                s = g.getAlliance().toString() + " General " + g.getType() + " attacking " +
                        t.getAlliance().toString() + " town with " +
                        g.getTroops() + " troops under his command.\n";
                if (!g.getAssistingMe().isEmpty()){
                    for (Coords cords : g.getAssistingMe()){
                        General assisting = board.get(cords).getAllianceGeneral(g.getAlliance());
                        s += "Receiving assistance from  " + assisting.getAlliance().toString() +
                                " General " + assisting.getType() + "\n";
                    }
                }
            }
            else if(activeParcel.hasSingleGeneral() && activeParcel.getFirstGeneral().getAlliance().equals(userTeam)){
                General g = activeParcel.getFirstGeneral();
                s = userTeam.toString() + " General " + g.getType() + " has:\n" +
                        g.getTroops() + " troops under his command.\n" + g.getMovementPoints() + " movement points.\n";
                if (g.getiAmAssisting() != null){
                    General assisted = board.get(g.getiAmAssisting()).getAllianceGeneral(g.getAlliance());
                    s+= "Orders to assist " + userTeam.toString() + " General " + assisted.getType() + "\n";
                }
                if (!g.getAssistingMe().isEmpty()){
                    for (Coords cords : g.getAssistingMe()){
                        General assisting = board.get(cords).getAllianceGeneral(g.getAlliance());
                        s += "Orders to receive assistance from  " + userTeam.toString() +
                                " General " + assisting.getType() + "\n";
                    }
                }
                if (g.hasChief()){
                    s += "The Chief of Staff.\n";
                }
                if (g.wantsChief()){
                    s += "Orders to receive the Chief of Staff.\n";
                }
            }
            else if (activeParcel.hasSingleGeneral() && !activeParcel.getFirstGeneral().getAlliance().equals(userTeam)){
                General g = activeParcel.getFirstGeneral();
                s = g.getAlliance().toString() + " General " + g.getType() + " has:\n" +
                        g.getTroops() + " troops under his command.";
            }
        }
        return s;
    }

    public HashMap<Coords, GameData> click(Coords c, boolean leftClick){
        if (histIndex <= indexOfNoChange){
            System.out.println("not your turn");
            return new HashMap<>();
        }
        rememberClick = false;
        setState();
        System.out.println(c.toString() +
                " isEmpty: " + board.get(c).isEmpty() +
                " turnstage = " + turnStage);
        board.clearChangeData();
        activeGeneral = null;
        if (turnStage == 0){
            if (exposingGeneral){
                exposeGeneral(c);
                exposingGeneral = false;
            }else if (leftClick){
                allocateLeftClick(c);
            }else {
                allocateRightClick(c);
            }
        }else if (turnStage == 1){
            if (settingChief){
                setChiefOrders(c, leftClick);
                settingChief = false;
            }else if (assisting){
                setAssiting(c);
            }else if (assisted){
                setAssisted(c);
            }else if (leftClick){
                moveLeftClick(c);
            }else {
                moveRightClick(c);
            }
        }else{
            if (leftClick){
                specialLeftClick(c);
            }else {
                specialRightClick(c);
            }
        }
        if (rememberClick){
            clearFutureHistory();
            addClickToHistory(c, leftClick);
            activeCoords = nextActiveCoords;
            return board.getChangeData();
        }
        activeCoords = nextActiveCoords;
        return new HashMap<>();
    }

    private void allocateLeftClick(Coords c){
            activeGeneral = board.get(c).getFirstGeneral();
            if (activeGeneral != null){
                Alliance a  = activeGeneral.getAlliance();
                if (a.getDataCode() == playerTurn  && players.get(a).canGeneralAdd(activeGeneral)){
                    addTroopFromTown(activeGeneral);
                    rememberClick = true;
                }
            }
    }

    private void allocateRightClick(Coords c){
        activeGeneral = board.get(c).getFirstGeneral();
        if (activeGeneral != null){
            Alliance a  = activeGeneral.getAlliance();
            if (a.getDataCode() == playerTurn && players.get(a).canGeneralSubtract(activeGeneral)){
                subtractTroop(activeGeneral);
                rememberClick = true;
            }
        }
    }

    private void addTroopFromTown(General g){
        Piece p = players.get(g.getAlliance()).getConnectedTroopGiver(g);
        board.addTroops(g, 1);
        board.subtractTroopFromTown(p);
    }

    private void subtractTroop(General g){
        board.addTroops(g, -1);
    }

    private void exposeGeneral(Coords c){
        General first = board.get(c).getFirstGeneral();
        if (first != null){
            board.setExposedGeneral(first);
            clearFutureHistory();
            histIndex = history.size() - 1;
            String oldEncoded = history.get(histIndex).getEncodedBoard();
            history.add(new GameState(board, activeCoords, playerTurn, turnStage,
                    oldEncoded + ",expose," + c.toString()));
            histIndex ++;
        }
    }

    private void moveLeftClick(Coords c){
        Parcel clickedParcel = board.get(c);
        // find active general
        Alliance a = getUserTeam();
        boolean active = false;
        if (activeCoords != null){
            activeGeneral = board.get(activeCoords).getAllianceGeneral(a);
            if (activeGeneral != null){
                active = true;
            }
        }else {
            activeGeneral = null;
        }
        if (active && activeCoords.isNextTo(c)){
            //move to open parcel
            if (clickedParcel.isEmpty() && activeGeneral.canMove(false))
            {
                board.moveGeneral(activeGeneral, c);
                rememberClick = true;
            }
            //enter own supply line
            else if(clickedParcel.hasOnlySupply() && clickedParcel.getSupply().getAlliance().equals(a) &&
                    activeGeneral.canMove(false))
            {
                board.moveGeneral(activeGeneral, c);
                rememberClick = true;
            }
            //enter own town
            else if (clickedParcel.hasTown() && clickedParcel.getPieces().size() == 1 &&
                    clickedParcel.getTown().getAlliance().equals(a) && activeGeneral.canMove(false))
            {
                board.moveGeneral(activeGeneral, c);
                rememberClick = true;
            }
            //enter own capitol
            else if (clickedParcel.hasCapitol() && clickedParcel.getPieces().size() == 1 &&
                    clickedParcel.getCapitol().getAlliance().equals(a) && activeGeneral.canMove(false))
            {
                board.moveGeneral(activeGeneral, c);
                rememberClick = true;
            }
            //cut line
            else if (clickedParcel.hasOnlySupply() && !clickedParcel.getSupply().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.cutSupply(activeGeneral);
                rememberClick = true;
            }
            //enter unoccupied town
            else if (clickedParcel.hasTown() && clickedParcel.getPieces().size() == 1 &&
                    clickedParcel.getTown().getAlliance().equals(Alliance.UNOCCUPIED) &&
                    activeGeneral.canMove(false))
            {
                board.moveGeneral(activeGeneral, c);
                rememberClick = true;
            }
            //attack empty enemy town
            else if (clickedParcel.hasTown() && clickedParcel.getPieces().size() == 1 &&
                    !clickedParcel.getTown().getAlliance().equals(Alliance.UNOCCUPIED) &&
                    !clickedParcel.getTown().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, false);
                rememberClick = true;
            }
            //attack empty enemy capitol
            else if (clickedParcel.hasCapitol() && clickedParcel.getPieces().size() == 1 &&
                    !clickedParcel.getCapitol().getAlliance().equals(Alliance.UNOCCUPIED) &&
                    !clickedParcel.getCapitol().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, false);
                rememberClick = true;
            }
            //attack field general
            else if (clickedParcel.hasSingleGeneral() && clickedParcel.getPieces().size() == 1 &&
                    !clickedParcel.getFirstGeneral().getAlliance().equals(a) &&
                    activeGeneral.canMove(false))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, false);
                rememberClick = true;
            }
            //attack supply general
            else if (clickedParcel.hasSingleGeneral() && clickedParcel.hasSupplyLine() &&
                    clickedParcel.getPieces().size() == 2 &&
                    !clickedParcel.getFirstGeneral().getAlliance().equals(a) &&
                    !clickedParcel.getSupply().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.cutSupply(activeGeneral);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, false);
                rememberClick = true;
            }
            //attack capitol general
            else if (clickedParcel.hasSingleGeneral() && clickedParcel.hasCapitol() &&
                    clickedParcel.getPieces().size() == 2 &&
                    !clickedParcel.getFirstGeneral().getAlliance().equals(a) &&
                    !clickedParcel.getCapitol().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, false);
                rememberClick = true;
            }
            //attack town general
            else if (clickedParcel.hasSingleGeneral() && clickedParcel.hasTown() &&
                    clickedParcel.getPieces().size() == 2 &&
                    !clickedParcel.getFirstGeneral().getAlliance().equals(a) &&
                    !clickedParcel.getTown().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, false);
                rememberClick = true;
            }
        }
        nextActiveCoords = c;
    }

    private void moveRightClick(Coords c){
        Parcel clickedParcel = board.get(c);
        // find active general
        Alliance a = getUserTeam();
        boolean active = false;
        if (activeCoords != null){
            activeGeneral = board.get(activeCoords).getAllianceGeneral(a);
            if (activeGeneral != null){
                active = true;
            }
        }else {
            activeCoords = c;
            activeGeneral = null;
        }

        //drop troop where he stands
        if (clickedParcel.hasSingleGeneral() && clickedParcel.getPieces().size() == 1 &&
                clickedParcel.getFirstGeneral().canDrop() &&
                clickedParcel.getFirstGeneral().getAlliance().getDataCode() == playerTurn){
            board.dropSupply(clickedParcel.getFirstGeneral());
            rememberClick = true;
        }
        //occupy town he is in
        if (clickedParcel.hasSingleGeneral() &&
                clickedParcel.hasTown() && clickedParcel.getTown().getAlliance().equals(Alliance.UNOCCUPIED) &&
                clickedParcel.getFirstGeneral().canDrop() &&
                clickedParcel.getFirstGeneral().getAlliance().getDataCode() == playerTurn){
            board.occupyTown(clickedParcel.getFirstGeneral());
            rememberClick = true;
        }
        if (active && activeCoords.isNextTo(c)){
            //move and drop a troop
            if (clickedParcel.isEmpty() && activeGeneral.canMoveAndDrop()){
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getFirstGeneral();
                board.dropSupply(activeGeneral);
                rememberClick = true;
            }
            //move and cut and drop
            else if (clickedParcel.hasOnlySupply() && !clickedParcel.getSupply().getAlliance().equals(a)
                    && activeGeneral.canMoveAndDrop()){
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.cutSupply(activeGeneral);
                activeGeneral = board.get(c).getFirstGeneral();
                board.dropSupply(activeGeneral);
                rememberClick = true;
            }
            //enter and occupy unoccupied town
            else if (clickedParcel.hasTown() && clickedParcel.getPieces().size() == 1 &&
                    clickedParcel.getTown().getAlliance().equals(Alliance.UNOCCUPIED) &&
                    activeGeneral.canMoveAndDrop())
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getFirstGeneral();
                board.occupyTown(activeGeneral);
                rememberClick = true;
            }
            //attack empty enemy town
            else if (clickedParcel.hasTown() && clickedParcel.getPieces().size() == 1 &&
                    !clickedParcel.getTown().getAlliance().equals(Alliance.UNOCCUPIED) &&
                    !clickedParcel.getTown().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, true);
                rememberClick = true;
            }
            //attack empty enemy capitol
            else if (clickedParcel.hasCapitol() && clickedParcel.getPieces().size() == 1 &&
                    !clickedParcel.getCapitol().getAlliance().equals(Alliance.UNOCCUPIED) &&
                    !clickedParcel.getCapitol().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, true);
                rememberClick = true;
            }
            //attack field general
            else if (clickedParcel.hasSingleGeneral() && clickedParcel.getPieces().size() == 1 &&
                    !clickedParcel.getFirstGeneral().getAlliance().equals(a) &&
                    activeGeneral.canMove(false))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, true);
                rememberClick = true;
            }
            //attack supply general
            else if (clickedParcel.hasSingleGeneral() && clickedParcel.hasSupplyLine() &&
                    clickedParcel.getPieces().size() == 2 &&
                    !clickedParcel.getFirstGeneral().getAlliance().equals(a) &&
                    !clickedParcel.getSupply().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.cutSupply(activeGeneral);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, true);
                rememberClick = true;
            }
            //attack capitol general
            else if (clickedParcel.hasSingleGeneral() && clickedParcel.hasCapitol() &&
                    clickedParcel.getPieces().size() == 2 &&
                    !clickedParcel.getFirstGeneral().getAlliance().equals(a) &&
                    !clickedParcel.getCapitol().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, true);
                rememberClick = true;
            }
            //attack town general
            else if (clickedParcel.hasSingleGeneral() && clickedParcel.hasTown() &&
                    clickedParcel.getPieces().size() == 2 &&
                    !clickedParcel.getFirstGeneral().getAlliance().equals(a) &&
                    !clickedParcel.getTown().getAlliance().equals(a) &&
                    activeGeneral.canMove(true))
            {
                board.moveGeneral(activeGeneral, c);
                activeGeneral = board.get(c).getAllianceGeneral(a);
                board.setFightingGeneral(activeGeneral, activeCoords, true);
                rememberClick = true;
            }
        }
        nextActiveCoords = c;
    }

    private void setAssiting(Coords c){
        General g = board.get(c).getFirstGeneral();
        if (board.get(c).hasSingleGeneral() && g.getAlliance().equals(getUserTeam())){
            assistingCoords = c;
            assisting = false;
            assisted = true;
        }else{
            assisting = false;
            assisted = false;
        }
    }

    private void setAssisted(Coords c){
        Parcel p = board.get(c);
        General g = board.get(assistingCoords).getFirstGeneral();
        General g1 = p.getFirstGeneral();
        General g2 = p.getSecondGeneral();
        if (g != null && g1 != null && g1.getAlliance().equals(getUserTeam()) &&
                g.canAssist(c))
        {
            board.setAssist(g, g1);
            String oldEncoded = history.get(history.size() - 1).getEncodedBoard();
            //String encoded = nextEncoded(oldEncoded);
            history.add(new GameState(new Board(board), null,  playerTurn, turnStage,
                    oldEncoded + ",assist," + assistingCoords.toString() + "," + c.toString()));
            histIndex = history.size() - 1;

        }else if (g != null &&g2 != null && g2.getAlliance().equals(getUserTeam()) &&
                g.canAssist(c))
        {
            board.setAssist(g, g2);
            String oldEncoded = history.get(history.size() - 1).getEncodedBoard();
            //String encoded = nextEncoded(oldEncoded);
            history.add(new GameState(new Board(board), null,  playerTurn, turnStage,
                    oldEncoded + ",assist," + assistingCoords.toString() + "," + c.toString()));
            histIndex = history.size() - 1;
        }
        assisting = false;
        assisted = false;
    }

    private void setChiefOrders(Coords c, boolean general){
        boolean foundOne = false;
        if (general){
            General first = board.get(c).getFirstGeneral();
            General second = board.get(c).getSecondGeneral();
            if (first != null){
                if (first.getAlliance().getDataCode() == playerTurn){
                    removeActiveChiefOrders();
                    board.setChiefOrders(first, true);
                    foundOne = true;
                }
            }else if(second != null){
                if (second.getAlliance().getDataCode() == playerTurn){
                    removeActiveChiefOrders();
                    board.setChiefOrders(second, true);
                    foundOne = true;
                }
            }
        }else{
            Capitol capitol = board.get(c).getCapitol();
            if (capitol != null && capitol.getAlliance().getDataCode() == playerTurn){
                removeActiveChiefOrders();
                board.setChiefOrders(capitol, true);
                foundOne = true;
            }
        }
        if (foundOne){
            clearFutureHistory();
            histIndex = history.size() - 1;
            String oldEncoded = history.get(histIndex).getEncodedBoard();
            String left = "0";
            if (general){
                left = "1";
            }
            history.add(new GameState(board, activeCoords, playerTurn, turnStage,
                    oldEncoded + ",chief," + c.toString() + "," + left));
            histIndex ++;
        }
    }

    private void removeActiveChiefOrders(){
        Player aP = getActivePlayer();
        if (aP != null){
            ArrayList<Piece> wanters = aP.getChiefWanters();
            for (Piece p : wanters){
                if (p.getType() > 0 && p.getType() < 6){
                    board.setChiefOrders((General)p, false);
                }else if (p.getType() == 7){
                    board.setChiefOrders((Capitol) p, false);
                }
            }
        }
    }

    private void specialLeftClick(Coords c){
    }

    private void specialRightClick(Coords c){
    }

    public int getMapRadius() {
        return mapRadius;
    }

    public void assist(){
        assisting = true;
        assisted = false;
        settingChief = false;
    }

    public void settingChief(){
        settingChief = true;
        assisting = false;
        assisted = false;
    }

    public void setExposingGeneral(){
        exposingGeneral = true;
    }

    public void back(){
        System.out.println("history size: " + history.size() + " histIndex: " + histIndex +
                " ecodedBoard: " + history.get(histIndex).getEncodedBoard());
        if (histIndex > 0){
            histIndex --;
        }
        //setState();
        //fillPlayers(history.get(history.size()-1).getBoard());
        System.out.println("history size: " + history.size() + " histIndex: " + histIndex +
                " ecodedBoard: " + history.get(histIndex).getEncodedBoard() + "\n");
    }

    public void forward(){
        System.out.println("history size: " + history.size() + " histIndex: " + histIndex +
                " ecodedBoard: " + history.get(histIndex).getEncodedBoard());
        if (histIndex < history.size() - 1){
            histIndex ++;
        }
        //setState();
        //fillPlayers(history.get(history.size()-1).getBoard());
        System.out.println("history size: " + history.size() + " histIndex: " + histIndex +
                " ecodedBoard: " + history.get(histIndex).getEncodedBoard() + "\n");
    }

    public void current(){
        histIndex = history.size() -1;
    }

    private void setState(){
        GameState gs = history.get(histIndex);
        board = new Board(gs.getBoard());
        fillPlayers();
        turnStage = gs.getTurnStage();
        playerTurn = gs.getPlayerTurn();
    }
}
