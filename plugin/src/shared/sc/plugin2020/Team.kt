package sc.plugin2020

import sc.api.plugins.ITeam

enum class Team(override val index: Int, val displayName: String): ITeam<Team> {
    RED(0, "Rot") {
        val letter = name.first()
        
        override fun opponent(): Team = BLUE
        override fun toString(): String = displayName
    },
    BLUE(1, "Blau") {
        val letter = name.first()
        
        override fun opponent(): Team = RED
        override fun toString(): String = displayName
    };
}