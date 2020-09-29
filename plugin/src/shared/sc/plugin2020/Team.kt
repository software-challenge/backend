package sc.plugin2020

import sc.api.plugins.ITeam

enum class Team(override val index: Int, val displayName: String): ITeam {
    RED(0, "Rot") {
        override fun opponent(): Team = BLUE
    },
    BLUE(1, "Blau") {
        override fun opponent(): Team = RED
    };
    override fun toString(): String = displayName
}