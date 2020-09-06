package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

@XStreamAlias(value = "color")
enum class Color(val team: Team) {
    BLUE  (Team.ONE),
    YELLOW(Team.TWO),
    RED   (Team.ONE),
    GREEN (Team.TWO) { init {
        BLUE.next   = YELLOW
        YELLOW.next = RED
        RED.next    = GREEN
        GREEN.next  = BLUE
    }};
    
    lateinit var next: Color
        private set
    
    operator fun unaryPlus(): FieldContent = when(this) {
        BLUE   -> FieldContent.BLUE
        YELLOW -> FieldContent.YELLOW
        RED    -> FieldContent.RED
        GREEN  -> FieldContent.GREEN
    }
}
