package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Determines how often a Piece has been rotated clockwise. */
@XStreamAlias(value = "rotation")
enum class Rotation(val value: Int) {
    NONE(0),
    RIGHT(1),
    MIRROR(2),
    LEFT(3);
}