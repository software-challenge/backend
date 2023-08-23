package sc.plugin2024

import sc.networking.XStreamProvider
import sc.plugin2024.actions.Accelerate
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
            listOf(
                    Accelerate::class.java,
                    Advance::class.java,
                    Push::class.java,
                    Turn::class.java,
                    Board::class.java,
                    GameState::class.java,
                    Move::class.java,
                    Ship::class.java,
                    Field.WATER::class.java,
                    Field.SANDBANK::class.java,
                    Field.ISLAND::class.java,
                    Field.GOAL::class.java,
                    Field.PASSENGER::class.java,
            )
    
}