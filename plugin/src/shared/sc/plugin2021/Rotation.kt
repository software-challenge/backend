package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Beschreibt, wie weit eine [PieceShape] gedreht werden soll. */
@XStreamAlias(value = "rotation")
enum class Rotation(val value: Int) {
    NONE(0),
    RIGHT(1),
    /** Dreht die Form um 180 grad. */
    MIRROR(2),
    LEFT(3);

    /** 
     * Summiere beide Rotationen auf.
     * (Die resultierende Rotation hat den gleichen Effekt wie die beiden Rotationen einzeln).
     */
    fun rotate(rotation: Rotation): Rotation =
            Rotation.values()[(Rotation.values().size + value + rotation.value) % Rotation.values().size]
}
