package sc.server.helpers

import sc.api.plugins.ITeam

enum class TestTeam(override val index: Int, val displayName: String): ITeam<TestTeam> {
    RED(0, "Rot") {
        val letter = name.first()
        
        override fun opponent(): TestTeam = BLUE
        override fun toString(): String = displayName
    },
    BLUE(1, "Blau") {
        val letter = name.first()
        
        override fun opponent(): TestTeam = RED
        override fun toString(): String = displayName
    };
}