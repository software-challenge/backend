package sc.plugin2014.entities;

public enum StoneShape {
    CIRCLE, FLOWER, FOUR_SPIKE, RHOMBUS, SQUARE, STAR;

    public static StoneShape getShapeFromIndex(int index) {
        return StoneShape.values()[index];
    }
}
