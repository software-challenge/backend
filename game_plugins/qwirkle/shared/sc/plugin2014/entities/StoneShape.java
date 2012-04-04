package sc.plugin2014.entities;

public enum StoneShape {
    FLOWER, STAR, CIRCLE, RHOMBUS, SQUARE, FOUR_SPIKE;

    public static StoneShape getShapeFromIndex(int index) {
        return StoneShape.values()[index];
    }
}
