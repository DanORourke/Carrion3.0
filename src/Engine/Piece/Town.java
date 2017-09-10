package Engine.Piece;

import Engine.Alliance;
import Engine.Board;
import Engine.Piece.General.General;
import GUI.Coords;

public class Town extends Piece {
    private final boolean haveTroop;

    public Town(Coords coords,Alliance alliance){
        super(coords, 6, alliance);
        this.haveTroop = true;
    }

    public Town(Coords coords,Alliance alliance, boolean haveTroop){
        super(coords, 6, alliance);
        this.haveTroop = haveTroop;
    }

    @Override
    public Piece copy(){
        return new Town(getCoords(), getAlliance(), haveTroop);
    }

    public Town createNewTroop(boolean haveTroop){
        return new Town(getCoords(), getAlliance(), haveTroop);
    }

    public Town resetTownAllocate(){
        return new Town(getCoords(), getAlliance(), true);
    }

    public boolean hasTroop(){
        return haveTroop;
    }

    public int getDefendedBonus(General attacker, General defender){
        return 1;
    }

    public int getDefendAloneBonus(Board board, General attacker) {
        int bonus = 1;
        bonus += addTerritoryBonus(board, attacker);
        return bonus;
    }

    private int addTerritoryBonus(Board board, General attacker){
        if (!board.get(getCoords()).getTerritory().equals(attacker.getAlliance()) &&
                !board.get(getCoords()).getTerritory().equals(Alliance.UNOCCUPIED) ){
            return 1;
        }else if (board.get(getCoords()).getTerritory().equals(attacker.getAlliance())){
            return -1;
        }else {
            return 0;
        }
    }

    public int getCasualties(General g){
        return 1;
    }

    public String getAllocateString(Alliance turnTeam){
        String s = "";
        if (getAlliance().equals(Alliance.UNOCCUPIED)){
            s = "Town is unoccupied\n\n";
        }else if (!getAlliance().equals(turnTeam)){
            s = getAlliance().toString() + " Town.\n\n";
        }else if (getAlliance().equals(turnTeam)){
            if (hasTroop()){
                s = getAlliance().toString() + " Town has " + 1 +" unassigned troop.\n\n";
            }else {
                s = getAlliance().toString() + " Town has " + 0 +" unassigned troops.\n\n";
            }
        }
        return s;
    }

    public String getMoveString(){
        if (getAlliance().equals(Alliance.UNOCCUPIED)){
            return "Town is unoccupied\n\n";
        }else{
            return getAlliance().toString() + " Town.\n\n";
        }
    }

    public String getBattleString(Board board, General g){
        return "Defending " + getAlliance().toString() + " Town\n+" + addTerritoryBonus(board, g) + " territory bonus.\n+" +
                getDefendAloneBonus(board, g) + " total fight bonus.\n+" +
                + getCasualties(g) + " total inflicted casualties.\n\n";
    }
}
