package sc.plugin2021.xstream

import sc.networking.XStreamProvider
import sc.plugin2021.*

class BlokusXStream: XStreamProvider {
    
    override val classesToRegister =
            listOf(
                    Board::class.java, Coordinates::class.java,
                    Field::class.java, GameState::class.java,
                    Move::class.java, Piece::class.java,
                    Color::class.java, Team::class.java)
    
}