package sc.plugin2014.entities;

public enum StoneColor {
    YELLOW, GREEN, RED, ORANGE, BLUE, PURPLE;

    public static StoneColor getColorFromIndex(int index) {
        return StoneColor.values()[index];
    }
}
