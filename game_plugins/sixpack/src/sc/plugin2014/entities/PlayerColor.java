package sc.plugin2014.entities;

public enum PlayerColor {

    RED, BLUE;

    public PlayerColor getOpponent() {
        return this == RED ? BLUE : RED;
    }
}
