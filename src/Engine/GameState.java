package Engine;

import GUI.Coords;

public class GameState {
    private final Board board;
    private final Coords activeCoords;
    private final int playerTurn;
    private final int turnStage;

    GameState(Board board, Coords activeCoords, int playerTurn, int turnStage){
        this.board = new Board(board);
        if (activeCoords == null){
            this.activeCoords = null;
        }else{
            this.activeCoords = new Coords(activeCoords);
        }
        this.playerTurn = playerTurn;
        this.turnStage = turnStage;
    }

    Board getBoard() {
        return board;
    }

    int getPlayerTurn() {
        return playerTurn;
    }

    int getTurnStage() {
        return turnStage;
    }

    Coords getActiveCoords() {
        return activeCoords;
    }
}
