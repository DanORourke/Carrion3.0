package GUI;

public class Tile {
    private final int x;
    private final int y;
    private final GameData gd;

    public Tile(int x, int y, GameData gd){
        this.x = x;
        this.y = y;
        this.gd = gd;
    }

    public Tile(int x, int y){
        this.x = x;
        this.y = y;
        this.gd = new GameData();
    }

    public Tile(){
        this.x = 0;
        this.y = 0;
        this.gd = new GameData();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public GameData getGd() {
        return gd;
    }
}
