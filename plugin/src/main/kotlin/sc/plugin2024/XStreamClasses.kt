package sc.plugin2024

import com.thoughtworks.xstream.XStream
import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.networking.XStreamProvider
import sc.plugin2024.util.GetSegmentsOfBoard

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
            listOf(
                    Board::class.java,
                    Coordinates::class.java,
                    GameState::class.java,
                    Move::class.java,
                    Team::class.java
            )
    
    override fun setup(xStream: XStream) {
        xStream.registerConverter(GetSegmentsOfBoard())
    }
}