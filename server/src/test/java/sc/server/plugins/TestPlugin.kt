package sc.server.plugins

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.shared.ScoreDefinition

class TestPlugin: IGamePlugin<TestMove> {
    companion object {
        const val TEST_PLUGIN_UUID = "012345-norris"
    }
    
    override val id: String = TEST_PLUGIN_UUID
    
    override val name = "Test Plugin"
    
    override val scoreDefinition: ScoreDefinition =
            ScoreDefinition("winner", "index", "turn")
    
    override val turnLimit
        get() = throw NotImplementedError()
    
    override val moveClass: Class<TestMove> = TestMove::class.java
    
    override fun createGame(): IGameInstance =
            TestGame()
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            TestGame(state as TestGameState)
    
}
