package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.*
import sc.plugin2025.GameRuleLogic.calculateCarrots
import sc.shared.InvalidMoveException

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
    
    fun isAhead(player: Hare = currentPlayer) =
        player.position > player.opponent.position
    
    val currentField: Field
        get() = currentPlayer.field
    
    val Hare.field: Field
        get() = board.getField(position)
    
    val Hare.opponent: Hare
        get() = getHare(team)
    
    fun getHare(team: ITeam) =
        players.find { it.team == team }!!
    
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
        if(mustEatSalad())
            return listOf(EatSalad)
        return (1..GameRuleLogic.calculateMoveableFields(player.carrots)).flatMap { distance ->
            val newField = player.position + distance
            if(!mayEnterField(newField))
                return emptyList()
            when(board.getField(newField)) {
                //Field.HARE -> Card.values().map { Advance(distance, it) }
                //Field.MARKET -> Card.values().map { Advance(distance, it) }
                else -> listOf(Advance(distance))
            }
        } + listOfNotNull(
            FallBack.takeIf { mayFallBack() },
            ExchangeCarrots(10).takeIf { GameRuleLogic.isValidToExchangeCarrots(this, 10) },
            ExchangeCarrots(-10).takeIf { GameRuleLogic.isValidToExchangeCarrots(this, -10) },
        )
    }
    
    override fun moveIterator(): Iterator<HuIMove> = getSensibleMoves().iterator()
    
    override fun performMoveDirectly(move: HuIMove) {
        val mist =
            MoveMistake.MUST_EAT_SALAD.takeIf {
                mustEatSalad() && move != EatSalad
            } ?: move.perform(this)
        if(mist != null)
            throw InvalidMoveException(mist, move)
        turn++
        if(GameRuleLogic.isValidToSkip(this)) {
            turn++
        }
    }
    
    fun moveToField(newPosition: Int, player: Hare = currentPlayer): MoveMistake? =
        if(mayEnterField(newPosition, player)) {
            player.position = newPosition
            null
        } else {
            MoveMistake.CANNOT_ENTER_FIELD
        }
    
    /** Basic validation whether a player may move forward by that distance.
     * Does not validate whether a card can be played on hare field. */
    fun checkAdvance(distance: Int, player: Hare = currentPlayer) =
        when {
            !mayEnterField(player.position + distance, player) -> MoveMistake.CANNOT_ENTER_FIELD
            player.carrots < calculateCarrots(distance) -> MoveMistake.MISSING_CARROTS
            else -> null
        }
    
    /** Basic validation whether a field may be entered via a jump that is not backward.
     * Does not validate whether a card can be played on hare field. */
    fun mayEnterField(newPosition: Int, player: Hare = currentPlayer): Boolean {
        val field = board.getField(newPosition)
        if(field != Field.GOAL && newPosition == currentPlayer.opponent.position)
            return false
        return when(field) {
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
    fun mayFallBack(): Boolean {
        if(mustEatSalad()) return false
        val lastHedgehog: Int? = this.board.getPreviousField(Field.HEDGEHOG, currentPlayer.position)
        return lastHedgehog != null && otherPlayer.position != lastHedgehog
    }
    
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
    fun mayEatSalad(player: Hare = currentPlayer) =
        player.salads > 0 && mustEatSalad(player)
    
    fun mustEatSalad(player: Hare = currentPlayer) =
        player.field == Field.SALAD && player.lastAction != EatSalad
    
    /** Isst einen Salat, keine Überprüfung der Regelkonformität. */
    fun eatSalad(player: Hare = currentPlayer) {
        player.eatSalad()
        if(isAhead(player)) {
            player.carrots += 10
        } else {
            player.carrots += 30
        }
    }
    
}