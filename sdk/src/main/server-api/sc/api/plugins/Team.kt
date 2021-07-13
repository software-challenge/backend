package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Die beiden verfügbaren Teams. */
@XStreamAlias(value = "team")
enum class Team(override val index: Int): ITeam {
    ONE(0) {
        override fun opponent(): Team = TWO
    },

    TWO(1) {
        override fun opponent(): Team = ONE
    };
}

/** This represents the team a player is in / is playing for. */
interface ITeam {
    val index: Int
    val name: String
    
    fun opponent(): ITeam
}
