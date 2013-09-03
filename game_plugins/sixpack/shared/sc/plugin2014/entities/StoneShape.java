package sc.plugin2014.entities;

/**
 * Repräsentiert dei Steinform
 * @author ffi
 *
 */
public enum StoneShape {
    ACORN, BELL, CLUBS, DIAMOND, HEART, SPADES;

    public static StoneShape getShapeFromIndex(int index) {
        return StoneShape.values()[index];
    }
}
