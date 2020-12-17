package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Die vier verschiedenen Farben im Spiel. */
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
    
    /** @return [FieldContent], der dieser Farbe entspricht. */
    operator fun unaryPlus(): FieldContent = when (this) {
        BLUE -> FieldContent.BLUE
        YELLOW -> FieldContent.YELLOW
        RED -> FieldContent.RED
        GREEN -> FieldContent.GREEN
    }
    
    override fun toString() = "Farbe $german"
    
    val german: String
        get() = when (this) {
            RED -> "Rot"
            GREEN -> "Grün"
            YELLOW -> "Gelb"
            BLUE -> "Blau"
        }
    
}