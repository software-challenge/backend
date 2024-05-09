package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.*
import sc.plugin2025.GameRuleLogic.calculateCarrots
import sc.plugin2025.GameRuleLogic.mustEatSalad

/**
 * The GameState class represents the current state of the game.
 *
 * It holds all the information about the current round, which is used
 * to calculate the next move.
 *
 * @property board The current game board.
 * @property turn The number of turns already made in the game.
 * @property lastMove The last move made in the game.
 */
@XStreamAlias(value = "state")
data class GameState @JvmOverloads constructor(
    /** Das aktuelle Spielfeld. */
    override val board: Board = Board(),
    /** Die Anzahl an bereits getätigten Zügen.
     * Modifikation nur via [advanceTurn]. */
    @XStreamAsAttribute override var turn: Int = 0,
    @XStreamImplicit
    val players: List<Hare> = Team.values().map { Hare(it) },
    /** Der zuletzt gespielte Zug. */
    override var lastMove: HuIMove? = null,
): TwoPlayerGameState<HuIMove>(players.first().team) {
    
    val currentPlayer
        get() = getHare(currentTeam)

    val otherPlayer
        get() = getHare(otherTeam)

    val aheadPlayer
        get() = players.maxByOrNull { it.position }!!
    
    val Hare.opponent: Hare
        get() = getHare(team)
    
    fun getHare(team: ITeam) =
        players.find { it.team == team }!!
    
    val currentField
        get() = board.getField(currentPlayer.position)
    
    /** Das [Team], das am Zug ist. */
    override val currentTeam: Team
        get() = currentTeamFromTurn()
    
    override val isOver: Boolean
        get() = players.any { it.inGoal }
    
    override fun clone(): GameState =
        copy(board = board.clone(), players = players.clone())
    
    override fun getPointsForTeam(team: ITeam): IntArray =
        getHare(team).let { intArrayOf(it.position, it.salads) }
    
    override fun getSensibleMoves(): List<HuIMove> = getSensibleMoves(currentPlayer)
    
    fun getSensibleMoves(player: Hare): List<HuIMove> {
        if(currentField == Field.SALAD && player.lastAction != EatSalad)
                return listOf(EatSalad)
        return (1..GameRuleLogic.calculateMoveableFields(player.carrots)).mapNotNull { distance ->
            val newPos = player.position + distance
            Advance(distance).takeIf {
                board.getField(newPos) != Field.HEDGEHOG && canEnterField(newPos)
            }
        } + listOf(FallBack).takeIf { isValidToFallBack() }.orEmpty() +
               listOf()
    }
    
    override fun moveIterator(): Iterator<HuIMove> = getSensibleMoves().iterator()
    
    override fun performMoveDirectly(move: HuIMove) {
        move.perform(this)
        turn++
        if(GameRuleLogic.isValidToSkip(this)) {
            turn++
        }
    }
    
    /** Basic validation whether a field may be entered via a jump that is not backward.
     * Does not validate whether a Hare card can be played on hare field. */
    fun canEnterField(newPosition: Int, player: Hare = currentPlayer): Boolean {
        val field = board.getField(newPosition)
        if(field != Field.GOAL && newPosition == currentPlayer.opponent.position)
            return false
        return when (field) {
            Field.SALAD -> player.salads > 0
            Field.MARKET -> player.carrots >= 10
            Field.HARE -> player.getCards().isNotEmpty()
            Field.GOAL -> player.carrots - calculateCarrots(newPosition - player.position) <= 10 && player.salads == 0
            Field.HEDGEHOG -> false
            else -> true
        }
    }
    
    /**
     * Überprüft `FallBack` Züge auf Korrektheit
     *
     * @param state GameState
     * @return true, falls der currentPlayer einen Rückzug machen darf
     */
    fun isValidToFallBack(): Boolean {
        if (mustEatSalad(this)) return false
        val newPosition: Int? = this.board.getPreviousField(Field.HEDGEHOG, this.currentPlayer.position)
        return (newPosition != -1) && this.otherPlayer.position != newPosition
    }
    
    
    fun mustPlayCard(player: Hare = currentPlayer) =
        currentField == Field.HARE &&
        player.lastAction !is CardAction
    
    /**
     * Überprüft `EatSalad` Zug auf Korrektheit.
     * Um einen Salat zu verzehren, muss der Spieler sich:
     *
     * - auf einem Salatfeld befinden
     * - noch mindestens einen Salat besitzen
     * - vorher kein Salat auf diesem Feld verzehrt wurde
     *
     * @return true, falls ein Salat gegessen werden darf
     */
    fun canEatSalad(player: Hare = currentPlayer) =
        player.salads > 0 &&
        board.getField(player.position) == Field.SALAD &&
        player.lastAction != EatSalad
    
}