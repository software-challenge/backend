package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IField
import sc.api.plugins.Team
import sc.framework.DeepCloneable

/**
 * Feldzustand mit Fischgröße bzw. Spezialfeld.
 *
 * @property size Fischgröße oder 0 für leere/gesperrte Felder.
 */
@XStreamAlias("field")
enum class FieldState(val size: Int): IField, DeepCloneable<FieldState> {
    ONE_S(1),
    ONE_M(2),
    ONE_L(3),
    TWO_S(1),
    TWO_M(2),
    TWO_L(3),
    SQUID(0),
    EMPTY(0);
    
    /** Liefert eine Kopie dieses Feldzustands. */
    override fun deepCopy(): FieldState = this
    
    /** Gibt an, ob das Feld komplett leer ist. */
    override val isEmpty: Boolean
        get() = this == EMPTY
    
    /** Zugehöriges [Team] oder null, falls kein Fisch vorhanden ist. */
    val team: Team?
        get() = when(this) {
            ONE_S, ONE_M, ONE_L -> Team.ONE
            TWO_S, TWO_M, TWO_L -> Team.TWO
            SQUID, EMPTY -> null
        }
    
    /** Liefert eine Textrepräsentation des Feldes. */
    override fun toString() =
        when(this) {
            SQUID -> "Krake"
            EMPTY -> "Leer "
            else -> team?.color.toString() + size.toString()
        }
    
    /** Gibt eine zweibuchstabige Darstellung für Textkarten zurück. */
    fun asLetters() =
        when(this) {
            SQUID -> "X "
            EMPTY -> "  "
            else -> team?.letter.toString() + size.toString()
        }
    
    /** @suppress */
    companion object {
        /** Erstellt den passenden [FieldState] für [team] und Fischgröße [size]. */
        fun from(team: Team, size: Int): FieldState =
            when(team) {
                Team.ONE -> when(size) {
                    1 -> ONE_S
                    2 -> ONE_M
                    3 -> ONE_L
                    else -> throw IllegalArgumentException("Invalid size: $size")
                }
                Team.TWO -> when(size) {
                    1 -> TWO_S
                    2 -> TWO_M
                    3 -> TWO_L
                    else -> throw IllegalArgumentException("Invalid size: $size")
                }
            }
        
    }
    
}
