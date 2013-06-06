package sc.plugin2014.entities;

public enum StoneShape {
    ACORN, BELL, CLUBS, DIAMOND, HEART, SPADES;

    public static StoneShape getShapeFromIndex(int index) {
        return StoneShape.values()[index];
    }
}
