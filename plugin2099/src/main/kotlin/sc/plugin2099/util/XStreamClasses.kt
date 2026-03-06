package sc.plugin2099.util

import sc.networking.XStreamProvider
import sc.plugin2099.Board
import sc.plugin2099.FieldState
import sc.plugin2099.GameState
import sc.plugin2099.Move

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister: List<Class<*>> =
        listOf(
            GameState::class.java,
            Board::class.java,
            FieldState::class.java,
            Move::class.java,
        )
    
}