package sc.server.plugins

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.plugins.PluginDescriptor
import sc.shared.ScoreDefinition

@PluginDescriptor(name = "TestPlugin", uuid = TestPlugin.TEST_PLUGIN_UUID)
class TestPlugin: IGamePlugin {
    companion object {
        const val TEST_PLUGIN_UUID = "012345-norris"
    }
    
    override fun id(): String = TEST_PLUGIN_UUID
    
    override fun createGame(): IGameInstance = TestGame()
    
    override fun getScoreDefinition(): ScoreDefinition =
            ScoreDefinition("winner")
    
}
