package Engine.Piece;

import Engine.Alliance;
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

    public int getDefendBonus(General g) {
        return 1;
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
}
