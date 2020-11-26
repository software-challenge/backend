package sc.plugin2021

import com.thoughtworks.xstream.XStream
import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.helpers.xStream
import sc.plugin2021.xstream.BoardConverter
import sc.plugins.PluginDescriptor
import sc.protocol.helpers.LobbyProtocol
import sc.shared.ScoreAggregation
import sc.shared.ScoreDefinition
import sc.shared.ScoreFragment
import java.util.concurrent.atomic.AtomicBoolean

@PluginDescriptor(name = "Blokus", uuid = "swc_2021_blokus")
class GamePlugin: IGamePlugin {
    
    companion object {
        val PLUGIN_UUID = "swc_2021_blokus"
        val loaded = AtomicBoolean(false)
        
        val SCORE_DEFINITION = ScoreDefinition(arrayOf(
                ScoreFragment("Gewinner"),
                ScoreFragment("\u2205 Punkte", ScoreAggregation.AVERAGE)
        ))
        
        private val classesToRegister: List<Class<*>>
            get() = listOf(Board::class.java, Coordinates::class.java,
                    Field::class.java, GameState::class.java,
                    Move::class.java, Piece::class.java,
                    Color::class.java, Team::class.java)
        
        fun registerXStream() {
            if (loaded.compareAndSet(false, true)) {
                LobbyProtocol.registerAdditionalMessages(xStream, classesToRegister)
                xStream.registerConverter(BoardConverter())
            }
        }
    
        @JvmStatic
        fun loadXStream(): XStream {
            registerXStream()
            return xStream
        }
    }
    
    override fun id() = PLUGIN_UUID
    
    override fun createGame(): IGameInstance {
        return Game()
    }
    
    override fun initialize() {
        registerXStream()
    }
    
    override fun getScoreDefinition(): ScoreDefinition = SCORE_DEFINITION
    
}
