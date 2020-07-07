package sc.plugin2021

/** Determines how often a Piece has been rotated clockwise. */
enum class Rotation(val value: Int) {
    NONE(0),
    RIGHT(1),
    MIRROR(2),
    LEFT(3);
}