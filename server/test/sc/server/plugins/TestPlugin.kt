package sc.server.plugins

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.plugins.PluginDescriptor
import sc.shared.ScoreDefinition
import java.io.File

@PluginDescriptor(name = "TestPlugin", uuid = TestPlugin.TEST_PLUGIN_UUID)
class TestPlugin: IGamePlugin {
    companion object {
        const val TEST_PLUGIN_UUID = "012345-norris"
    }
    
    override val id: String = TEST_PLUGIN_UUID
    
    override val scoreDefinition: ScoreDefinition =
            ScoreDefinition("winner")
    
    override val gameTimeout = 1000
    
    override fun createGame(): IGameInstance =
            TestGame()
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            TestGame(state as TestGameState)
    
}
