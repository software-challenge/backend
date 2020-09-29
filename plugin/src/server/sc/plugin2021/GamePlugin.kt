package sc.plugin2021

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.plugin2021.xstream.BoardConverter
import sc.plugins.PluginDescriptor
import sc.protocol.helpers.LobbyProtocol
import sc.shared.ScoreAggregation
import sc.shared.ScoreDefinition
import sc.shared.ScoreFragment
import sc.helpers.xStream as xstream

@PluginDescriptor(name = "Blokus", uuid = "swc_2021_blokus", author = "")
class GamePlugin: IGamePlugin {
    
    companion object {
        val PLUGIN_AUTHOR = ""
        val PLUGIN_UUID = "swc_2021_blokus"
        
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
            LobbyProtocol.registerAdditionalMessages(xstream, classesToRegister)
            
            xstream.registerConverter(BoardConverter())
        }
        
        @JvmStatic
        val xStream by lazy {
            registerXStream()
            xstream
        }
    }
    
    override fun createGame(): IGameInstance {
        return Game()
    }
    
    override fun initialize() {
        registerXStream()
    }
    
    override fun getScoreDefinition(): ScoreDefinition = SCORE_DEFINITION
    
}
