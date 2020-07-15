package sc.plugin2021

import sc.api.plugins.ITeam

enum class Team(override val index: Int, val colors: List<Color>): ITeam<Team> {
    NONE(0, listOf(Color.NONE)) {
        override fun opponent(): Team = this
        override fun toString(): String = "[None]"
    },
    ONE(1, listOf(Color.BLUE, Color.YELLOW)) {
        override fun opponent(): Team = TWO
        override fun toString(): String = "One"
    },
    TWO(2, listOf(Color.RED, Color.GREEN)) {
        override fun opponent(): Team = ONE
        override fun toString(): String = "Two"
    };
}