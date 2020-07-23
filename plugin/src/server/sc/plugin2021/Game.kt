package sc.plugin2021

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const
import com.thoughtworks.xstream.annotations.XStreamAlias
import org.slf4j.LoggerFactory
import sc.api.plugins.IGameState
import sc.framework.plugins.ActionTimeout
import sc.framework.plugins.RoundBasedGameInstance
import sc.framework.plugins.Player
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import sc.plugin2021.util.WinReason
import sc.protocol.responses.ProtocolMessage
import sc.shared.*


@XStreamAlias(value = "game")
class Game(UUID: String = GamePlugin.PLUGIN_UUID): RoundBasedGameInstance<Player>() {
    companion object {
        val logger = LoggerFactory.getLogger(Game::class.java)
    }
    
    val availableTeams = mutableListOf(Team.ONE, Team.TWO)
    val gameState = GameState()
    
    init {
        pluginUUID = UUID
    }
    
    override fun start() {
        players.forEach {it.notifyListeners(WelcomeMessage(it.color)) }
        super.start()
    }
   
    override fun onPlayerJoined(): Player {
        val player = gameState.getPlayer(availableTeams.removeAt(0))
                ?: throw NullPointerException("Too many players joined the game!")
        
        players.add(player)
        gameState.addPlayer(player)
        return player
    }
    
    override fun getWinners(): MutableList<Player> {
        TODO("Not yet implemented")
    }
    
    override fun getPlayerScores(): MutableList<PlayerScore> {
        TODO("Not yet implemented")
    }
    
    override fun getPlayers(): MutableList<Player> {
        return players
    }
    
    /**
     * Checks if any player can still make moves.
     * If so, returns null; otherwise returns
     * the player with the highest cumulative score of its colors
     */
    override fun checkWinCondition(): WinCondition? {
        if (!gameState.orderedColors.isEmpty())
            return null
        
        val scoreMap: Map<Team, Int> = Team.valids().map {
            it to gameState.getPointsForPlayer(it)
        }.toMap()
        
        if (scoreMap[Team.ONE]!! > scoreMap[Team.TWO]!!)
            return WinCondition(Team.ONE, WinReason.DIFFERING_SCORES)
        if (scoreMap[Team.TWO]!! > scoreMap[Team.ONE]!!)
            return WinCondition(Team.TWO, WinReason.DIFFERING_SCORES)
        return WinCondition(null, WinReason.EQUAL_SCORE)
    }
    
    override fun loadGameInfo(gameInfo: Any?) {
        TODO("Not yet implemented")
    }
    
    override fun loadFromFile(file: String?) {
        TODO("Not yet implemented")
    }
    
    override fun loadFromFile(file: String?, turn: Int) {
        TODO("Not yet implemented")
    }
    
    override fun getScoreFor(player: Player): PlayerScore {
        val team = player.color as Team
        logger.debug("Get score for player $team (violated: ${if(player.hasViolated()) "yes" else "no"})")
        val opponent = gameState.getOpponent(player)!!
        val winCondition = checkWinCondition()
        
        var cause: ScoreCause = ScoreCause.REGULAR
        var reason: String = ""
        var score: Int = Constants.LOSE_SCORE
        val points = gameState.getPointsForPlayer(team)
        
        // Is the game already finished?
        if (winCondition?.reason == WinReason.EQUAL_SCORE)
            score = Constants.DRAW_SCORE
        if (winCondition?.reason == WinReason.DIFFERING_SCORES)
            if (winCondition.winner == team)
                score = Constants.WIN_SCORE
        
        // Opponent did something wrong
        if (opponent.hasViolated() && !player.hasViolated() ||
                opponent.hasLeft() && !player.hasLeft() ||
                opponent.hasSoftTimeout() ||
                opponent.hasHardTimeout())
            score = Constants.WIN_SCORE
        
        when {
            player.hasSoftTimeout() -> {
                cause = ScoreCause.SOFT_TIMEOUT
                reason = "Der Spieler hat innerhalb von ${getTimeoutFor(player).softTimeout / 1000} Sekunden nach Aufforderung keinen Zug gesendet"
            }
            player.hasHardTimeout() -> {
                cause = ScoreCause.SOFT_TIMEOUT
                reason = "Der Spieler hat innerhalb von ${getTimeoutFor(player).hardTimeout / 1000} Sekunden nach Aufforderung keinen Zug gesendet"
            }
            player.hasViolated() -> {
                cause = ScoreCause.RULE_VIOLATION
                reason = player.violationReason!!
            }
            player.hasLeft() -> {
                cause = ScoreCause.LEFT
                reason = "Der Spieler hat das Spiel verlassen"
            }
            else -> {
                score = Constants.DRAW_SCORE
            }
        }
        return PlayerScore(cause, reason, score, points)
    }
    
    override fun getTimeoutFor(player: Player): ActionTimeout =
            ActionTimeout(true, Constants.HARD_TIMEOUT, Constants.SOFT_TIMEOUT)
    
    @Throws(InvalidMoveException::class, InvalidGameStateException::class)
    override fun onRoundBasedAction(fromPlayer: Player, data: ProtocolMessage?) {
        // This check is already done by super.onAction()
        assert(fromPlayer == activePlayer)
        
        try {
            if (data !is Move)
                throw InvalidMoveException("${fromPlayer.displayName} hat keinen validen Zug gesendet.")
            logger.debug("Performing Move $data")
            logger.debug("Current Board: ${gameState.board}")
            GameRuleLogic.performMove(gameState, data)
            next(gameState.currentPlayer)
        } catch(e: InvalidMoveException) {
            super.catchInvalidMove(e, fromPlayer)
        }
    }
    
    override fun getCurrentState(): IGameState {
        TODO("Not yet implemented")
    }
    
    
}