package sc.plugin2024

import com.thoughtworks.xstream.XStream
import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.networking.XStreamProvider
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn
import sc.plugin2024.util.BoardConverter

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
            )
    
}