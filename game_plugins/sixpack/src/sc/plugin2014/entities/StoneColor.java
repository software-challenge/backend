package sc.plugin2014.entities;

/**
 * Enum der Steinfarben.
 * @author ffi
 *
 */
public enum StoneColor {
    BLUE, GREEN, MAGENTA, ORANGE, VIOLET, YELLOW;

    public static StoneColor getColorFromIndex(int index) {
        return StoneColor.values()[index];
    }
}
