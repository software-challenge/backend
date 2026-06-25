package sc.plugin2027

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Team

/**
 * Die vier verschiedenen Farben im Spiel.
 * Das erste Team besteht aus Blau und Rot, das zweite Team aus Gelb und Grün.
 */
@XStreamAlias(value = "color")
enum class Color {
    BLUE,
    YELLOW,
    RED,
    GREEN;
    
    /** Die nächste Farbe in der Reihenfolge. */
    val next: Color
        get() = when (this) {
            BLUE -> YELLOW
            YELLOW -> RED
            RED -> GREEN
            GREEN -> BLUE
        }
    
    /** Das Team, zu der die Farbe gehört. */
    val team: Team
        get() = when (this) {
            BLUE, RED -> Team.ONE
            YELLOW, GREEN -> Team.TWO
        }
    
    /**
     * Gibt den entsprechenden [FieldContent] zurück der der Spielerfarbe entspricht.
     *
     * @return [FieldContent], der dieser Farbe entspricht.
     */
    fun toFieldContent(): FieldContent = when (this) {
        BLUE -> FieldContent.BLUE
        YELLOW -> FieldContent.YELLOW
        RED -> FieldContent.RED
        GREEN -> FieldContent.GREEN
    }
    
    /**
     * Gibt die englische Bezeichnung der Farbe zurück.
     * Dies wird intern verwendet, um das Spielbrett zu serialisieren und
     * um in der GUI spielerspezifische Farben anzuzeigen.
     */
    fun toEnString() = when (this) {
        RED -> "red"
        GREEN -> "green"
        YELLOW -> "yellow"
        BLUE -> "blue"
    }
    
    override fun toString() = "Farbe $german"
    
    /** Gibt die deutsche Bezeichnung der Farbe zurück. */
    val german: String
        get() = when (this) {
            RED -> "Rot"
            GREEN -> "Grün"
            YELLOW -> "Gelb"
            BLUE -> "Blau"
        }
    
}