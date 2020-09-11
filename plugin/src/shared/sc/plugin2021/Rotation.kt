package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Determines how often a Piece has been rotated clockwise. */
@XStreamAlias(value = "rotation")
enum class Rotation(val value: Int) {
    NONE(0),
    RIGHT(1),
    MIRROR(2),
    LEFT(3);
    
    fun rotate(rotation: Rotation): Rotation =
            Rotation.values()[(Rotation.values().size + value + rotation.value) % Rotation.values().size]
}