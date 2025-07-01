package sc.plugin2026.util

import sc.networking.XStreamProvider
import sc.plugin2026.*

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister: List<Class<*>> =
        listOf(
            GameState::class.java,
            Board::class.java,
            FieldState::class.java,
            Move::class.java,
            PiranhaMoveMistake::class.java,
        )
    
}