package sc.plugin2022.util

import sc.api.plugins.Team
import sc.networking.XStreamProvider
import sc.plugin2022.*

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
            listOf(
                    Board::class.java, Coordinates::class.java,
                    GameState::class.java,
                    Move::class.java, Piece::class.java,
                    Color::class.java, Team::class.java)
    
}