package sc.plugin2014.entities;


public class Field {

    private final int posX;
    private final int posY;
    private Stone     stone;

    public Field(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Stone getStone() {
        return stone;
    }

    protected void setStone(Stone stone) {
        this.stone = stone;
    }

}
