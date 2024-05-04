package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Die unterschiedlichen Spielfelder aus dem Hasen und Igel Original. */
@XStreamAlias(value = "field")
enum class Field(val short: String, val unicode: String = short) {
    /**
     * Zahl- und Flaggenfelder.
     * Die veränderten Spielregeln sehen nur noch die Felder 1,2 vor.
     * Die Positionsfelder 3 und 4 wurden in Möhrenfelder umgewandelt,
     * und (1,5,6) sind jetzt Position-1-Felder.
     */
    POSITION_1("P1"), POSITION_2("P2"),
    /** Igelfeld */
    HEDGEHOG("I", "\uD83E\uDD94"),
    /** Salatfeld */
    SALAD("S", "\uD83E\uDD57"),
    /** Karottenfeld */
    CARROT("K", "\uD83E\uDD55"),
    /** Hasenfeld */
    HARE("H"),
    /** Das Zielfeld */
    GOAL("Z", "🏁"),
    /** Das Startfeld */
    START("0", "▶"),
}
