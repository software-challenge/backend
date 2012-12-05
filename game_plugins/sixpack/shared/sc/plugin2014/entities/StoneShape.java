package sc.plugin2014.entities;

public enum StoneShape {
    ACORN, BELL, CLUBS, DIAMONT, HEART, SPADES;

    public static StoneShape getShapeFromIndex(int index) {
        return StoneShape.values()[index];
    }
}
