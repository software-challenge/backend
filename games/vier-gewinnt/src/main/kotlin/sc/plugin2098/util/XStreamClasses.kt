package sc.plugin2026.util

import sc.networking.XStreamProvider
import sc.plugin2026.*

/** @suppress */
class XStreamClasses: XStreamProvider {
    
    /** Klassen, die im Netzwerkverkehr serialisiert werden. */
    override val classesToRegister: List<Class<*>> =
        listOf(
            GameState::class.java,
            Board::class.java,
            FieldState::class.java,
            Move::class.java,
            PiranhaMoveMistake::class.java,
        )
    
}