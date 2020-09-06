package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.ITeam
import sc.shared.WelcomeMessage

@XStreamAlias(value = "team")
enum class Team(override val index: Int, val colors: List<Color>): ITeam {
    ONE(0, listOf(Color.BLUE, Color.YELLOW)) {
        override fun opponent(): Team = TWO
        override fun toString(): String = "One"
    },
    TWO(1, listOf(Color.RED, Color.GREEN)) {
        override fun opponent(): Team = ONE
        override fun toString(): String = "Two"
    };
}

val WelcomeMessage.playerColor
    get() = Team.valueOf(color)