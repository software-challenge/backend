package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.ITeam
import sc.shared.WelcomeMessage

/** Die beiden verfügbaren Teams. */
@XStreamAlias(value = "team")
enum class Team(override val index: Int, val colors: List<Color>): ITeam {

    /** Das erste Team: Es kontrolliert Blau und Rot. */
    ONE(0, listOf(Color.BLUE, Color.RED)) {
        override fun opponent(): Team = TWO
        override fun toString(): String = "One"
    },

    /** Das zweite Team: Es steuert Gelb und Grün. */
    TWO(1, listOf(Color.YELLOW, Color.GREEN)) {
        override fun opponent(): Team = ONE
        override fun toString(): String = "Two"
    };
}

val WelcomeMessage.playerColor
    get() = Team.valueOf(color)