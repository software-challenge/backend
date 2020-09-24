package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

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
    
    lateinit var next: Color
        private set

    val team: Team
        get() = when(this) {
            BLUE, RED -> Team.ONE
            YELLOW, GREEN -> Team.TWO
        }

    operator fun unaryPlus(): FieldContent = when(this) {
        BLUE   -> FieldContent.BLUE
        YELLOW -> FieldContent.YELLOW
        RED    -> FieldContent.RED
        GREEN  -> FieldContent.GREEN
    }
}
