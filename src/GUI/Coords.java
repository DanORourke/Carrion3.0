package GUI;

public class Coords {
    private final int q;
    private final int r;
    private final int s;

    public Coords(int q, int r, int s){
        this.q = q;
        this.r = r;
        this.s = s;
    }

    public Coords(Coords c){
        this.q = c.getQ();
        this.r = c.getR();
        this.s = c.getS();
    }


    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public int getS() {
        return s;
    }

    public int[] getAll(){
        return new int[]{q, r, s};
    }

    public boolean isNextTo(Coords c){
        int dq = Math.abs(q - c.getQ());
        int dr = Math.abs(r - c.getR());
        int ds = Math.abs(s - c.getS());
        return ((dq == 0 && dr == 1) || (dr == 0 && dq == 1) || (ds == 0 && dq == 1));
    }

    @Override
    public String toString(){
        return q + "," + r + "," + s;
    }

    @Override
    public int hashCode() {
        int hash = 31;
        hash = 17 * hash + q;
        hash = 17 * hash + r;
        hash = 17 * hash + s;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Coords))
            return false;
        if (obj == this)
            return true;

        Coords c = (Coords) obj;
        return (q == c.getQ() && r == c.getR() && s == c.getS());
    }

}
