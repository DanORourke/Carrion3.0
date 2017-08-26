package Engine;

import Engine.Piece.Capitol;
import Engine.Piece.General.General;
import Engine.Piece.Piece;
import GUI.Coords;
import GUI.GameData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
    private boolean settingChief = false;

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
                int q = Integer.valueOf(moves.get(i));
                int r = Integer.valueOf(moves.get(i+1));
                int s = Integer.valueOf(moves.get(i+2));
                int leftClick = Integer.valueOf(moves.get(i+3));
                click(new Coords(q, r, s), leftClick == 1);
                i+=4;
            }else{
                //turnstage == 1
                //q, r, s (of active coords)  ,q, r, s (of clicked coords) , boolean leftclick(0=false, 1=true)
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
//        GameState state = getLatestState();
//        playerTurn = state.getPlayerTurn();
//        turnStage = state.getTurnStage();
//        board = state.getBoard();
//        fillPlayers();//why do this??
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
        turnStage++;
        if (turnStage > 1){
            turnStage = 0;
            rotatePlayer();
        }
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
                    looking = false;
                }
            }
        }
        indexOfNoChange = histIndex;
    }

    private void playoutBattles(){
        //TODO this
    }

    private void moveChief(){
        Player aP = getActivePlayer();
        System.out.println("Move chief false");
        if (aP != null && aP.willMoveChief()){
            System.out.println("Move chief true");
            Piece[] pieces = aP.moveChief();
            board.removeChief(pieces[0]);
            board.addChief(pieces[1]);
            String oldEncoded = history.get(history.size() - 1).getEncodedBoard();
            history.add(new GameState(new Board(board), null,  playerTurn, turnStage,
                    oldEncoded + ",chief," + pieces[1].getCoords().toString()));
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

    private GameState getLatestState(){
        return history.get(history.size()-1);
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

    private void removePlayerPiece(Piece p){
        Player player = players.get(p.getAlliance());
        player.removePiece(p);
    }

    public void clearActive(){
        activeGeneral = null;
        activeCoords = null;
    }

    public String clickTileInfo(Coords c){
        return "you clicked me?";
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
        General g = board.get(c).getFirstGeneral();
        if (g != null){
            s = g.getTroops() + " " + g.getMovementPoints() + " wants " + g.wantsChief() + " has " + g.hasChief();
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
        System.out.println(c.toString() + " isEmpty: " + board.get(c).isEmpty() + " turnstage = " + turnStage);
        board.clearChangeData();
        activeGeneral = null;
        if (turnStage == 0){
            if (leftClick){
                allocateLeftClick(c);
            }else {
                allocateRightClick(c);
            }
        }else if (turnStage == 1){
            if (settingChief){
                setChiefOrders(c, leftClick);
                settingChief = false;
            }
            else if (leftClick){
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
                if (a.getDataCode() == playerTurn  && players.get(a).canGeneralAdd(c)){//
                    addTroops(activeGeneral, 1);
                    rememberClick = true;
                }
            }
    }

    private void allocateRightClick(Coords c){
        activeGeneral = board.get(c).getFirstGeneral();
        if (activeGeneral != null){
            Alliance a  = activeGeneral.getAlliance();
            if (a.getDataCode() == playerTurn && players.get(a).canGeneralSubtract(c)){
                addTroops(activeGeneral, -1);
                rememberClick = true;
            }
        }
    }

    private void addTroops(General g, int n){
        board.addTroops(g, n);
        players.get(g.getAlliance()).addTroops(g, n);
    }

    private void setChiefOrders(Coords c, boolean general){

        if (general){
            General first = board.get(c).getFirstGeneral();
            General second = board.get(c).getSecondGeneral();
            if (first != null){
                if (first.getAlliance().getDataCode() == playerTurn){
                    removeActiveChiefOrders();
                    board.setChiefOrders(first, true);
                }
            }else if(second != null){
                if (second.getAlliance().getDataCode() == playerTurn){
                    removeActiveChiefOrders();
                    board.setChiefOrders(second, true);
                }
            }
        }else{
            Capitol capitol = board.get(c).getCapitol();
            if (capitol.getAlliance().getDataCode() == playerTurn){
                removeActiveChiefOrders();
                board.setChiefOrders(capitol, true);
            }
        }
        clearFutureHistory();
        histIndex = history.size() - 1;
        String oldEncoded = history.get(histIndex).getEncodedBoard();
        history.add(new GameState(board, activeCoords, playerTurn, turnStage,
                oldEncoded + ",wantsChief," + c.toString()));
        histIndex ++;
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

    private void moveLeftClick(Coords c){
        Parcel clickedParcel = board.get(c);
        // find active general
        boolean active = false;
        if (activeCoords != null){
            activeGeneral = board.get(activeCoords).getFirstGeneral();
            if (activeGeneral != null){
                active = true;
            }
        }else {
            activeGeneral = null;
        }
        if (active && clickedParcel.isEmpty() && activeCoords.isNextTo(c) &&
                activeGeneral.getAlliance().getDataCode() == playerTurn){
            board.moveGeneral(activeGeneral, c);
            rememberClick = true;
        }
        nextActiveCoords = c;

    }

    private void moveRightClick(Coords c){
        Parcel clickedParcel = board.get(c);
        // find active general
        boolean active = false;
        if (activeCoords != null){
            activeGeneral = board.get(activeCoords).getFirstGeneral();
            if (activeGeneral != null){
                active = true;
            }
        }else {
            activeGeneral = null;
        }
        if (clickedParcel.hasSingleGeneral() &&
                !clickedParcel.hasSupplyLine() && clickedParcel.getFirstGeneral().canDrop() &&
                clickedParcel.getFirstGeneral().getAlliance().getDataCode() == playerTurn){
            dropSupply(clickedParcel.getFirstGeneral());
            rememberClick = true;
        }
        if (active && clickedParcel.isEmpty() && activeCoords.isNextTo(c) && activeGeneral.canMoveAndDrop() &&
                activeGeneral.getAlliance().getDataCode() == playerTurn){
            moveGeneral(activeGeneral, c);
            dropSupply(activeGeneral);
            rememberClick = true;
        }
        nextActiveCoords = c;

    }

    private void moveGeneral(General g, Coords c){
        board.moveGeneral(g, c);
        activeGeneral = board.get(c).getFirstGeneral();
    }

    private void dropSupply(General g){
        board.dropSupply(g);
    }

    private void specialLeftClick(Coords c){
    }

    private void specialRightClick(Coords c){
    }

    public int getMapRadius() {
        return mapRadius;
    }

    public void settingChief(){
        this.settingChief = true;
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
