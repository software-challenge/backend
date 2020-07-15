package sc.plugin2021

import sc.plugin2021.Team
import sc.plugin2021.util.Constants

enum class Color(val team: Team, val letter: Char, val corner: Coordinates) {
    NONE  (Team.NONE, '-', Coordinates(-1,                -1)),
    BLUE  (Team.ONE,  'B', Coordinates(0,                 0)),
    YELLOW(Team.ONE,  'Y', Coordinates(Constants.BOARD_SIZE, 0)),
    RED   (Team.TWO,  'R', Coordinates(Constants.BOARD_SIZE, Constants.BOARD_SIZE)),
    GREEN (Team.TWO,  'G', Coordinates(0,                 Constants.BOARD_SIZE));
    
    override fun toString(): String = letter.toString()
}
