package sc.plugin2014.entities;

public enum StoneColor {
    BLUE, GREEN, ORANGE, PURPLE, RED, YELLOW;

    public static StoneColor getColorFromIndex(int index) {
        return StoneColor.values()[index];
    }
}
