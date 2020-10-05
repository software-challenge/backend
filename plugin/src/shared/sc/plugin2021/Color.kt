package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Die vier verschiedenen Farben im Spiel. */
@XStreamAlias(value = "color")
enum class Color() {
    BLUE,
    YELLOW,
    RED,
    GREEN { init {
        BLUE.next   = YELLOW
        YELLOW.next = RED
        RED.next    = GREEN
        GREEN.next  = BLUE
    }};

    /**
     * Die Farbe, die als nächstes am Zug ist, sofern sie noch im Spiel ist.
     * Achtung: Da die Farbe den Spielstand nicht kennt, werden auch Farben ausgegeben, die nicht mehr im Spiel sind.
     */
    lateinit var next: Color
        private set

    /** Das Team, zu der die Farbe gehört. */
    val team: Team
        get() = when(this) {
            BLUE, RED -> Team.ONE
            YELLOW, GREEN -> Team.TWO
        }

    /** Konvertiert die Farbe zu einem entsprechenden [FieldContent]. */
    operator fun unaryPlus(): FieldContent = when(this) {
        BLUE   -> FieldContent.BLUE
        YELLOW -> FieldContent.YELLOW
        RED    -> FieldContent.RED
        GREEN  -> FieldContent.GREEN
    }
}
