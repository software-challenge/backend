package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Die beiden verfügbaren Teams. */
@XStreamAlias(value = "team")
enum class Team(override val index: Int): ITeam {
    ONE(0) {
        override fun opponent(): Team = TWO
        override val letter = 'R'
        override val color = "blue" // FIXME maybe add secondary color
    },
    
    TWO(1) {
        override fun opponent(): Team = ONE
        override val letter = 'B'
        override val color = "yellow" // FIXME maybe add secondary color
    };
    
    abstract val color: String
    
    abstract override fun opponent(): Team
}

/** This represents the team a player is in / is playing for. */
interface ITeam {
    val index: Int
    val name: String
    val letter: Char
    
    fun opponent(): ITeam
}
