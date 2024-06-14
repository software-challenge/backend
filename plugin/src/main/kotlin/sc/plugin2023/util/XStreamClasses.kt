package sc.plugin2023.util

import sc.api.plugins.Coordinates
import sc.networking.XStreamProvider
import sc.plugin2023.Board
import sc.plugin2023.GameState
import sc.plugin2023.Move

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
        listOf(
            Board::class.java,
            GameState::class.java,
            Move::class.java,
            Coordinates::class.java,
        )
    
}