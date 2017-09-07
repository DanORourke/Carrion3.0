package Engine.Piece;

import Engine.Alliance;
import GUI.Coords;

public class Piece {
    private final Coords coords;
    private final int type;
    private final Alliance alliance;

    public Piece (){
        this.coords = null;
        this.type = 0;
        this.alliance = null;
    }

    public Piece(Coords coords, int type, Alliance alliance){
        this.coords = coords;
        this.type = type;
        //type; 0 = supply, 1 = 1gen, 2 = 2gen, 3 = 3gen, 4 = 4gen, 5 = 5gen, 6 = town,
        // 7 = capitol, 8 = chief,
        this.alliance = alliance;
    }

    @Override
    public int hashCode() {
        int hash = 31;
        hash = 17 * hash + coords.getQ();
        hash = 17 * hash + coords.getR();
        hash = 17 * hash + coords.getS();
        hash = 17 * hash + type;
        hash = 17 * hash + alliance.getDataCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Piece)) {
            return false;
        }if (obj == this) {
            return true;
        }
        Piece p = (Piece) obj;
        Coords pc = p.getCoords();
        boolean same = (coords.getQ() == pc.getQ() && coords.getR() == pc.getR() &&
                coords.getS() == pc.getS() && type == p.getType() &&
                alliance.getDataCode() == p.getAlliance().getDataCode());
        return same;
    }

    public Piece copy(){
        return new Piece(coords, type, alliance);
    }

    public Coords getCoords() {
        return coords;
    }

    public int getType() {
        return type;
    }

    public Alliance getAlliance() {
        return alliance;
    }

    public boolean isGeneral(){
        return (type > 0 && type < 6);
    }


    public boolean isSupply() {
        return type == 0;
    }

    public boolean isCapitol() {
        return type == 7;
    }

    public boolean isTown() {
        return type == 6;
    }
}
