package sc.plugin2024

import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.networking.XStreamProvider

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
            listOf(
                    Board::class.java, Coordinates::class.java, GameState::class.java,
                    Move::class.java, Team::class.java)
    
}