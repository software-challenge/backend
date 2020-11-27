package sc.server.plugins

import org.slf4j.LoggerFactory
import sc.api.plugins.ITeam
import sc.framework.plugins.Player

class TestPlayer(pc: ITeam) : Player(pc) {
    override fun requestMove() {
        val request = TestTurnRequest()
        logger.info("$this is requesting a move")
        notifyListeners(request)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TestPlayer::class.java)
    }
}