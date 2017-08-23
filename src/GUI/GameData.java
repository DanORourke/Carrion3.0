package GUI;

public class GameData {
    private final boolean border;
    private final boolean outskirts;
    private final int outColor;
    private final int[] data;
    //{type, color, type, color....}
    //type; 0 = supply, 1 = 1gen, 2 = 2gen, 3 = 3gen, 4 = 4gen, 5 = 5gen, 6 = town,
    // 7 = capitol, 8 = chief, 9 = fieldBattle(followed by two colors), 10 = home territory(no color)
    //color; 0 = un, 1 = red, 2 = white, 3 = green, 4 = blue, 5 = purple, 6 = yellow

    GameData(){
        this.border = false;
        this.outskirts = false;
        this.outColor = 0;
        this.data = new int[0];
    }

    GameData(boolean border){
        this.border = border;
        this.outskirts = false;
        this.outColor = 0;
        this.data = new int[0];    }

    public GameData(int[] data){
        this.border = false;
        this.outskirts = false;
        this.outColor = 0;
        this.data = data;
    }

    GameData(boolean outskirts, int outColor){
        this.border = false;
        this.outskirts = outskirts;
        this.outColor = outColor;
        this.data = new int[0];
    }

    boolean isBorder() {
        return border;
    }

    int[] getData() {
        return data;
    }

    boolean isOutskirts() {
        return outskirts;
    }

    int getOutColor() {
        return outColor;
    }
}
