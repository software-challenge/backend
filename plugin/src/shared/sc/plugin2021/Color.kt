package sc.plugin2021

enum class Color(val team: Team) {
    BLUE  (Team.ONE),
    
    YELLOW(Team.TWO) { init {
            BLUE.next = YELLOW }},
    RED   (Team.ONE) { init {
            YELLOW.next = RED }},
    GREEN (Team.TWO) { init {
            RED.next = GREEN
            GREEN.next = BLUE }};
    
    lateinit var next: Color
        private set
    
    operator fun unaryPlus(): FieldContent = when(this) {
        BLUE   -> FieldContent.BLUE
        YELLOW -> FieldContent.YELLOW
        RED    -> FieldContent.RED
        GREEN  -> FieldContent.GREEN
    }
}
