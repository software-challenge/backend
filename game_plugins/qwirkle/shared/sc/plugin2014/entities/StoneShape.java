package sc.plugin2014.entities;

public enum StoneShape {
    CIRCLE, FLOWER, FOURSPIKES, RECTANGLE, RHOMBUS, STAR;

    public static StoneShape getShapeFromIndex(int index) {
        return StoneShape.values()[index];
    }
}
