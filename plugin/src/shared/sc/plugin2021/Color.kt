package sc.plugin2021

import sc.plugin2021.Team
import sc.plugin2021.util.Constants

enum class Color(val team: Team, val corner: Coordinates) {
    BLUE(Team.ONE, Coordinates(0, 0)),
    YELLOW(Team.ONE, Coordinates(Constants.BOARD_SIZE, 0)),
    RED(Team.TWO, Coordinates(Constants.BOARD_SIZE, Constants.BOARD_SIZE)),
    GREEN(Team.TWO, Coordinates(0, Constants.BOARD_SIZE));
}
