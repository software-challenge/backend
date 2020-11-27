package sc.server.plugins

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.helpers.xStream
import sc.plugins.PluginDescriptor
import sc.protocol.helpers.LobbyProtocol.registerAdditionalMessages
import sc.server.plugins.TestPlugin
import sc.shared.ScoreDefinition
import java.util.*

@PluginDescriptor(name = "TestPlugin", uuid = TestPlugin.TEST_PLUGIN_UUID)
class TestPlugin : IGamePlugin {
    override fun id(): String = TEST_PLUGIN_UUID

    override fun createGame(): IGameInstance = TestGame()

    override fun initialize() {
        registerAdditionalMessages(xStream, Arrays.asList(TestTurnRequest::class.java, TestGameState::class.java, TestPlayer::class.java, TestMove::class.java))
    }

    override fun unload() {}

    override fun getScoreDefinition(): ScoreDefinition = SCORE_DEFINITION

    companion object {
        const val TEST_PLUGIN_UUID = "012345-norris"
        val SCORE_DEFINITION = ScoreDefinition("winner")
    }
}