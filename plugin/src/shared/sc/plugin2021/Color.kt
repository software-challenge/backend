package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Die vier verschiedenen Farben im Spiel. */
@XStreamAlias(value = "color")
enum class Color {
    BLUE,
    YELLOW,
    RED,
    GREEN { init {
        BLUE.next   = YELLOW
        YELLOW.next = RED
        RED.next    = GREEN
        GREEN.next  = BLUE
    }};

    /** Die nächste Farbe in der Reihenfolge. */
    lateinit var next: Color
        private set

    /** Das Team, zu der die Farbe gehört. */
    val team: Team
        get() = when(this) {
            BLUE, RED -> Team.ONE
            YELLOW, GREEN -> Team.TWO
        }

    /** @return [FieldContent], der dieser Farbe entspricht. */
    operator fun unaryPlus(): FieldContent = when(this) {
        BLUE   -> FieldContent.BLUE
        YELLOW -> FieldContent.YELLOW
        RED    -> FieldContent.RED
        GREEN  -> FieldContent.GREEN
    }
}
