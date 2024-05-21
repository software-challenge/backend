package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.*
import sc.plugin2025.GameRuleLogic.calculateCarrots
import sc.plugin2025.GameRuleLogic.calculateMoveableFields
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
    /** Die Anzahl an bereits getätigten Zügen. */
    @XStreamAsAttribute override var turn: Int = 0,
    @XStreamImplicit val players: List<Hare> = Team.values().map { Hare(it) },
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
        get() = players.any { it.inGoal }
    
    override fun clone(): GameState =
        copy(players = players.clone())
    
    fun cloneCurrentPlayer(transform: (Hare) -> Unit) =
        copy(players = players.map { if(it.team == currentTeam) it.clone().apply(transform) else it })
    
    override fun getPointsForTeam(team: ITeam): IntArray =
        getHare(team).let { intArrayOf(if(it.inGoal) 1 else 0, it.position, it.salads) }
    
    override fun getSensibleMoves(): List<HuIMove> = getSensibleMoves(currentPlayer)
    
    fun getSensibleMoves(player: Hare): List<HuIMove> {
        if(mustEatSalad(player))
            return listOf(EatSalad)
        return (1..calculateMoveableFields(player.carrots).coerceAtMost(board.size - player.position)).flatMap { distance ->
            val newField = player.position + distance
            if(validateTargetField(newField, player) != null)
                return@flatMap emptyList()
            val newState = copy(players = players.map { if(it.team == player.team) it.clone().apply { advanceBy(distance) } else it })
            return@flatMap newState.nextCards(newState.getHare(player.team))?.map { cards ->
                Advance(distance, *cards)
            } ?: listOf(Advance(distance))
        } + listOfNotNull(
            FallBack.takeIf { nextFallBack(player) != null },
            *possibleExchangeCarrotMoves(player).toTypedArray()
        )
    }
    
    fun possibleExchangeCarrotMoves(player: Hare = currentPlayer) =
        listOfNotNull(
            ExchangeCarrots(10).takeIf { mayExchangeCarrots(10, player) },
            ExchangeCarrots(-10).takeIf { mayExchangeCarrots(-10, player) },
        )
    
    fun nextCards(player: Hare = currentPlayer): Collection<Array<Card>>? =
        when(player.field) {
            Field.HARE -> {
                player.getCards().flatMap { card ->
                    if(card.playable(this) == null) {
                        val newState = clone()
                        card.play(newState)
                        if(card.moves)
                            return@flatMap newState.nextCards(player)?.map { arrayOf(card, *it) } ?: listOf(arrayOf(card))
                        listOf(arrayOf(card))
                    } else {
                        listOf()
                    }
                }
            }
            Field.MARKET -> Card.values().map { arrayOf(it) }
            else -> null
        }
    
    override fun moveIterator(): Iterator<HuIMove> = getSensibleMoves().iterator()
    
    override fun performMoveDirectly(move: HuIMove) {
        val mist =
            MoveMistake.MUST_EAT_SALAD.takeIf {
                mustEatSalad() && move != EatSalad
            }.also { currentPlayer.saladEaten = false } ?: move.perform(this)
        if(mist != null)
            throw InvalidMoveException(mist, move)
        turn++
        awardPositionFields()
        if(!moveIterator().hasNext()) {
            turn++
            awardPositionFields()
        }
    }
    
    fun awardPositionFields() {
        when(currentField) {
            Field.POSITION_1 -> {
                if(isAhead()) {
                    currentPlayer.carrots += 10
                }
            }
            Field.POSITION_2 -> {
                if(!isAhead()) {
                    currentPlayer.carrots += 30
                }
            }
            else -> {}
        }
    }
    
    fun moveToField(newPosition: Int, player: Hare = currentPlayer): MoveMistake? =
        validateTargetField(newPosition, player) ?: run {
            player.position = newPosition
            null
        }
    
    /** Basic validation whether a player may move forward by that distance.
     * Does not validate whether a card can be played on hare field. */
    fun checkAdvance(distance: Int, player: Hare = currentPlayer): MoveMistake? {
        if(player.carrots < calculateCarrots(distance))
            return MoveMistake.MISSING_CARROTS
        return validateTargetField(player.position + distance, player)
    }
    
    /** Basic validation whether a field may be entered via a jump that is not backward.
     * Does not validate whether a card can be played on hare field. */
    fun validateTargetField(newPosition: Int, player: Hare = currentPlayer): MoveMistake? {
        val field = board.getField(newPosition)
        if(field != Field.GOAL && newPosition == player.opponent.position)
            return MoveMistake.FIELD_OCCUPIED
        when(field) {
            Field.SALAD -> player.salads > 0 || return MoveMistake.NO_SALAD
            Field.MARKET -> player.carrots >= 10 || return MoveMistake.MISSING_CARROTS
            Field.HARE -> player.getCards().isNotEmpty() || return MoveMistake.CARD_NOT_OWNED
            Field.GOAL -> player.carrots - calculateCarrots(newPosition - player.position) <= 10 && player.salads == 0 || return MoveMistake.GOAL_CONDITIONS
            Field.HEDGEHOG -> return MoveMistake.HEDGEHOG_ONLY_BACKWARDS
            null -> return MoveMistake.FIELD_NONEXISTENT
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
        board.getField(player.position) === Field.HARE && player.getCards().any { it.playable(this) == null }
    
    fun mustEatSalad(player: Hare = currentPlayer) =
        player.field == Field.SALAD && !player.saladEaten
    
    /** Isst einen Salat, keine Überprüfung der Regelkonformität. */
    fun eatSalad(player: Hare = currentPlayer) {
        player.eatSalad()
        if(isAhead(player)) {
            player.carrots += 10
        } else {
            player.carrots += 30
        }
    }
    
    override fun teamStats(team: ITeam): List<Pair<String, Int>> =
        getHare(team).run {
            listOf(
                " ⃞ Position" to this.position,
                "▾ Karotten" to this.carrots,
                " ⃝ Salate" to this.salads,
                "Karten" to this.getCards().count(),
            )
        }
    
}