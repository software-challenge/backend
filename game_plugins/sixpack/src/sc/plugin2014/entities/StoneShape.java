package sc.plugin2014.entities;

/**
 * Enum der Steinformen.
 * @author ffi
 *
 */
public enum StoneShape {
    ACORN, BELL, CLUBS, DIAMOND, HEART, SPADES;

    public static StoneShape getShapeFromIndex(int index) {
        return StoneShape.values()[index];
    }
}
