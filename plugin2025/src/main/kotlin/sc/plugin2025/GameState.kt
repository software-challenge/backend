package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.*
import sc.plugin2025.GameRuleLogic.calculateCarrots
import sc.plugin2025.GameRuleLogic.calculateMoveableFields
import sc.plugin2025.util.HuIConstants
import sc.plugin2025.util.HuIWinReason
import sc.shared.InvalidMoveException
import sc.shared.WinCondition
import kotlin.math.pow
import kotlin.math.sqrt

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
    /** Die Anzahl an bereits getätigten Zügen. */
    @XStreamAsAttribute override var turn: Int = 0,
    @XStreamImplicit val players: List<Hare> = Team.values().map { Hare(it) },
    /** Der zuletzt gespielte Zug. */
    override var lastMove: Move? = null,
): TwoPlayerGameState<Move>(players.first().team) {
    
    val currentPlayer
        get() = getHare(currentTeam)
    
    val otherPlayer
        get() = getHare(otherTeam)
    
    val aheadPlayer
        get() = players.maxByOrNull { it.position }!!
    
    fun isAhead(player: Hare = currentPlayer) =
        player.position > player.opponent.position
    
    fun isAhead(team: Team) =
        isAhead(getHare(team))
    
    val currentField: Field?
        get() = currentPlayer.field
    
    val Hare.field: Field?
        get() = board.getField(position)
    
    val Hare.opponent: Hare
        get() = getHare(team.opponent())
    
    fun getHare(team: ITeam) =
        players.find { it.team == team }!!
    
    /** Das [Team], das am Zug ist. */
    override val currentTeam: Team
        get() = currentTeamFromTurn()
    
    override val isOver: Boolean
        get() = players.any { it.inGoal } && turn.mod(2) == 0 || round >= HuIConstants.ROUND_LIMIT
    
    override val winCondition: WinCondition?
        get() = players.singleOrNull { it.inGoal }?.team?.let { WinCondition(it, HuIWinReason.GOAL) }
    
    val Hare.inGoal
        get() = position == board.size - 1
    
    override fun clone(): GameState =
        copy(players = players.clone())
    
    fun cloneCurrentPlayer(transform: (Hare) -> Unit) =
        copy(players = players.map { if(it.team == currentTeam) it.clone().apply(transform) else it })
    
    override fun getSensibleMoves(): List<Move> = getSensibleMoves(currentPlayer)
    
    fun getSensibleMoves(player: Hare): List<Move> {
        if(mustEatSalad(player))
            return listOf(EatSalad)
        return (1..calculateMoveableFields(player.carrots).coerceAtMost(board.size - player.position)).flatMap { distance ->
            if(checkAdvance(distance, player) != null)
                return@flatMap emptyList()
            return@flatMap possibleCardMoves(distance, player) ?: listOf(Advance(distance))
        } + listOfNotNull(
            FallBack.takeIf { nextFallBack(player) != null },
            *possibleExchangeCarrotMoves(player).toTypedArray()
        )
    }
    
    /** Possible Advances including buying/playing of cards.
     * @return null if target field is neither market nor hare, empty list if no possibilities, otherwise possible Moves */
    fun possibleCardMoves(distance: Int, player: Hare = currentPlayer): List<Advance>? {
        val state =
            copy(players = players.map { if(it.team == player.team) it.clone().apply { advanceBy(distance) } else it })
        return state.nextCards(state.getHare(player.team))?.map { cards ->
            Advance(distance, *cards)
        }
    }
    
    fun possibleExchangeCarrotMoves(player: Hare = currentPlayer) =
        listOfNotNull(
            ExchangeCarrots(10).takeIf { mayExchangeCarrots(10, player) },
            ExchangeCarrots(-10).takeIf { mayExchangeCarrots(-10, player) },
        )
    
    fun nextCards(player: Hare = currentPlayer): Collection<Array<Card>>? =
        when(player.field) {
            Field.HARE -> {
                player.getCards().toSet().flatMap { card ->
                    if(card.check(this) == null) {
                        val newState = clone()
                        newState.currentPlayer.removeCard(card)
                        card.play(newState)
                        if(card.moves)
                            return@flatMap newState.nextCards(newState.getHare(player.team))?.map { arrayOf(card, *it) }
                                           ?: listOf(arrayOf(card))
                        listOf(arrayOf(card))
                    } else {
                        listOf()
                    }
                }
            }
            
            Field.MARKET -> {
                if(player.carrots >= 10)
                    Card.values().map { arrayOf(it) }
                else
                    listOf()
            }
            
            else -> null
        }
    
    override fun moveIterator(): Iterator<Move> = getSensibleMoves().iterator()
    
    override fun performMoveDirectly(move: Move) {
        val mist =
            HuIMoveMistake.MUST_EAT_SALAD.takeIf {
                move != EatSalad && mustEatSalad()
            } ?: move.perform(this)
        if(mist != null)
            throw InvalidMoveException(mist, move)
        currentPlayer.lastAction =
            if(move is Advance && currentField != Field.MARKET)
                move.getCards().lastOrNull() ?: move
            else move
        lastMove = move
        turn++
        awardPositionFields()
        if(!isOver && !moveIterator().hasNext()) {
            turn++
            awardPositionFields()
        }
    }
    
    fun awardPositionFields(hare: Hare = currentPlayer) {
        when(hare.field) {
            Field.POSITION_1 -> {
                if(isAhead(hare)) {
                    hare.carrots += 10
                }
            }
            
            Field.POSITION_2 -> {
                if(!isAhead(hare)) {
                    hare.carrots += 30
                }
            }
            
            else -> {}
        }
    }
    
    fun moveToField(newPosition: Int, player: Hare = currentPlayer): HuIMoveMistake? =
        validateTargetField(newPosition, player) ?: run {
            player.position = newPosition
            null
        }
    
    /** Basic validation whether a player may move forward by that distance.
     * Does not validate whether a card can be played on hare field. */
    fun checkAdvance(distance: Int, player: Hare = currentPlayer): HuIMoveMistake? {
        return validateTargetField(
            player.position + distance,
            player,
            (player.carrots - calculateCarrots(distance)).takeIf { it >= 0 } ?: return HuIMoveMistake.MISSING_CARROTS
        )
    }
    
    /** Basic validation whether a field may be entered via a jump that is not backward.
     * Does not validate whether a card can be played on hare field. */
    fun validateTargetField(
        newPosition: Int,
        player: Hare = currentPlayer,
        carrots: Int = player.carrots,
    ): HuIMoveMistake? {
        if(newPosition == 0)
            return HuIMoveMistake.CANNOT_ENTER_FIELD
        val field = board.getField(newPosition)
        if(field != Field.GOAL && newPosition == player.opponent.position)
            return HuIMoveMistake.FIELD_OCCUPIED
        when(field) {
            Field.SALAD -> player.salads > 0 || return HuIMoveMistake.NO_SALAD
            Field.MARKET -> carrots >= 10 || return HuIMoveMistake.MISSING_CARROTS
            Field.HARE -> player.getCards().isNotEmpty() || return HuIMoveMistake.CARD_NOT_OWNED
            Field.GOAL -> carrots <= 10 && player.salads == 0 || return HuIMoveMistake.GOAL_CONDITIONS
            Field.HEDGEHOG -> return HuIMoveMistake.HEDGEHOG_ONLY_BACKWARDS
            null -> return HuIMoveMistake.FIELD_NONEXISTENT
            else -> return null
        }
        return null
    }
    
    /**
     * Überprüft `FallBack` Züge auf Korrektheit
     *
     * @param state GameState
     * @return Igelfeldposition, falls der currentPlayer einen Rückzug machen darf
     */
    fun nextFallBack(player: Hare = currentPlayer): Int? =
        this.board.getPreviousField(Field.HEDGEHOG, player.position)?.takeUnless { player.opponent.position == it }
    
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
    
    /**
     * Überprüft, ob der derzeitige Spieler 10 Karotten nehmen oder abgeben kann.
     * @param state GameState
     * @param n 10 oder -10 je nach Fragestellung
     * @return true, falls die durch n spezifizierte Aktion möglich ist.
     */
    fun mayExchangeCarrots(n: Int, player: Hare = currentPlayer): Boolean =
        player.field == Field.CARROTS && (n == 10 || (n == -10 && player.carrots >= 10))
    
    /** Gibt zurück, ob der Spieler eine Karte spielen kann. */
    fun canPlayAnyCard(player: Hare = currentPlayer): Boolean =
        board.getField(player.position) === Field.HARE && player.getCards().any { it.check(this) == null }
    
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
    
    override fun getPointsForTeam(team: ITeam): IntArray =
        getHare(team).let { intArrayOf(it.position, it.carrots) }
    
    override fun getPointsForTeamExtended(team: ITeam): IntArray =
        getHare(team).copy().let { hare ->
            awardPositionFields(hare)
            // 1 at the beginning, 12 at the end
            val positionFactor = 1.04.pow(hare.position)
            intArrayOf(
                when {
                    hare.inGoal -> 100
                    hare.field == Field.SALAD -> 3
                    else -> 0
                },
                hare.position,
                ((55 - hare.position.toDouble()).pow(0.3) / 2 * sqrt(hare.carrots.toDouble())).toInt(),
                -(hare.salads * positionFactor).toInt(),
                (hare.getCards().size * (10 - positionFactor) / 3).toInt(),
            )
        }
    
    override fun teamStats(team: ITeam) =
        getHare(team).run {
            listOf(
                Stat("   ⃞ Position", this.position),
                Stat("▾ Karotten", this.carrots),
                Stat("Salate", this.salads, "   ⃝ "),
                //Stat("Karten", this.getCards().count(), "◼ "),
            )
        }
    
    override fun longString(): String =
        toString()
    
}