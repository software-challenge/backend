package sc.plugin2021

import sc.plugin2021.Team
import sc.plugin2021.util.Constants

enum class Color(val team: Team, val letter: Char, val corner: Coordinates) {
    BLUE  (Team.ONE,  'B', Coordinates(0, 0)),
    
    YELLOW(Team.ONE,  'Y', Coordinates(Constants.BOARD_SIZE -1, 0)) { init {
            BLUE.next = YELLOW }},
    RED   (Team.TWO,  'R', Coordinates(Constants.BOARD_SIZE -1, Constants.BOARD_SIZE -1)) { init {
            YELLOW.next = RED }},
    GREEN (Team.TWO,  'G', Coordinates(0, Constants.BOARD_SIZE -1)) { init {
            RED.next = GREEN
            GREEN.next = BLUE }};
    
    lateinit var next: Color
        private set
}
