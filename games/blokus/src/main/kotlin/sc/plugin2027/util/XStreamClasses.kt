package sc.plugin2027.util

import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.networking.XStreamProvider
import sc.plugin2027.*

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
        listOf(
            GameState::class.java,
            Board::class.java,
            Coordinates::class.java,
            Field::class.java,
            Move::class.java,
            Piece::class.java,
            Team::class.java,
            BlokusMoveMistake::class.java,
        )
    
}