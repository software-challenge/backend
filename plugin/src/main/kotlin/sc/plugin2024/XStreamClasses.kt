package sc.plugin2024

import sc.networking.XStreamProvider
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
            listOf(
                    Acceleration::class.java,
                    Advance::class.java,
                    Push::class.java,
                    Turn::class.java,
                    Board::class.java,
                    GameState::class.java,
                    Move::class.java,
                    Ship::class.java,
                    Field.WATER::class.java,
                    Field.SANDBANK::class.java,
                    Field.BLOCKED::class.java,
                    Field.GOAL::class.java,
                    Field.PASSENGER::class.java,
            )
    
}