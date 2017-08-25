package Engine;

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
    private int indexOfNoChange = 0;

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
            }else{
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
        System.out.println(history.size());
    }

    private void addClickToHistory(Coords c, boolean leftClick){
        String encoded = createClickEncoded(c, leftClick);
        System.out.println(encoded);
        history.add(new GameState(board, activeCoords, playerTurn, turnStage, encoded));
        histIndex ++;
    }

    private String createClickEncoded(Coords c, boolean leftClick){
        //could remove s coord and calculate it up top instead, q + r + s = 0, but might keep it to use as verification
        String oldEncoded = history.get(histIndex).getEncodedBoard();
        String left;
        if (leftClick){
            left = "1";
        }else{
            left = "0";
        }
        return oldEncoded + "," + activeCoords.toString() + "," + c.toString() + "," + left;
    }

    private void clearFutureHistory(){
        if (history.size()-1 > histIndex){
            history.subList(histIndex + 1, history.size()).clear();
        }
    }

    private void nextPhase(){
        GameState state = getLatestState();
        playerTurn = state.getPlayerTurn();
        turnStage = state.getTurnStage();
        board = state.getBoard();
        fillPlayers();
        rotatePhase();
        String oldEncoded = history.get(histIndex).getEncodedBoard();
        history.add(new GameState(new Board(board), null,  playerTurn, turnStage, oldEncoded + ",next"));
        histIndex++;
    }

    private void rotatePhase(){
        turnStage++;
        if (turnStage > 1){
            turnStage = 0;
            rotatePlayer();
        }
    }

    private void rotatePlayer(){
        boolean looking = true;
        while(looking){
            playerTurn++;
            if (playerTurn > 6){
                playerTurn = 0;
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
            s = g.getTroops() + " " + g.getMovementPoints();
        }
        return s;
    }

    public HashMap<Coords, GameData> click(Coords c, boolean leftClick){
        if (histIndex < indexOfNoChange){
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
                allocateLeftClick(c, board);
            }else {
                allocateRightClick(c, board);
            }
        }else if (turnStage == 1){
            if (leftClick){
                moveLeftClick(c, board);
            }else {
                moveRightClick(c, board);
            }
        }else{
            if (leftClick){
                specialLeftClick(c, board);
            }else {
                specialRightClick(c, board);
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

    private void allocateLeftClick(Coords c, Board board){
            activeGeneral = board.get(c).getFirstGeneral();
            if (activeGeneral != null){
                Alliance a  = activeGeneral.getAlliance();
                if (a.getDataCode() == playerTurn  && players.get(a).canGeneralAdd(c)){//
                    addTroops(activeGeneral, 1, board);
                    rememberClick = true;
                }
            }
    }

    private void allocateRightClick(Coords c, Board board){
        activeGeneral = board.get(c).getFirstGeneral();
        if (activeGeneral != null){
            Alliance a  = activeGeneral.getAlliance();
            if (a.getDataCode() == playerTurn && players.get(a).canGeneralSubtract(c)){
                addTroops(activeGeneral, -1, board);
                rememberClick = true;
            }
        }
    }

    private void addTroops(General g, int n, Board board){
        board.addTroops(g, n);
        players.get(g.getAlliance()).addTroops(g, n);
    }

    private void moveLeftClick(Coords c, Board board){
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

    private void moveRightClick(Coords c, Board board){
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
            dropSupply(clickedParcel.getFirstGeneral(), board);
            rememberClick = true;
        }
        if (active && clickedParcel.isEmpty() && activeCoords.isNextTo(c) && activeGeneral.canMoveAndDrop() &&
                activeGeneral.getAlliance().getDataCode() == playerTurn){
            moveGeneral(activeGeneral, c, board);
            dropSupply(activeGeneral,board);
            rememberClick = true;
        }
        nextActiveCoords = c;

    }

    private void moveGeneral(General g, Coords c, Board board){
        board.moveGeneral(g, c);
        activeGeneral = board.get(c).getFirstGeneral();
    }

    private void dropSupply(General g, Board board){
        board.dropSupply(g);
    }

    private void specialLeftClick(Coords c, Board board){
    }

    private void specialRightClick(Coords c, Board board){
    }

    public int getMapRadius() {
        return mapRadius;
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

    private void setState(){
        GameState gs = history.get(histIndex);
        board = new Board(gs.getBoard());
        fillPlayers();
        turnStage = gs.getTurnStage();
        playerTurn = gs.getPlayerTurn();
    }
}
