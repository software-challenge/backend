package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.*
import sc.plugin2024.actions.Accelerate
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn
import sc.plugin2024.mistake.AdvanceProblem
import sc.plugin2024.mistake.MoveMistake
import sc.plugin2024.util.MQConstants
import sc.plugin2024.util.MQConstants.POINTS_PER_SEGMENT
import sc.plugin2024.util.MQWinReason
import sc.shared.InvalidMoveException
import sc.shared.WinCondition
import kotlin.math.absoluteValue

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
        val ships: List<Ship> = (CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector).let { start ->
            listOf(
                    Ship(start + CubeDirection.UP_LEFT.vector, Team.ONE),
                    Ship(start + CubeDirection.DOWN_LEFT.vector, Team.TWO)
            )
        },
        /** Das [Team], das am Zug ist. */
        @XStreamAsAttribute
        override var currentTeam: Team = ships.first().team,
        /** Der zuletzt gespielte Zug. */
        override var lastMove: Move? = null,
): TwoPlayerGameState<Move>(currentTeam) {
    
    val currentShip: Ship
        get() = getShip(currentTeam)
    
    val otherShip: Ship
        get() = getShip(currentTeam.opponent())
    
    fun getShip(team: Team) =
        ships[team.index]
    
    /**
     * Determine the team that should go first at the beginning of the round.
     * 1. Weiter vorne
     * 2. Geschwindigkeit
     * 3. Kohle
     *
     * Ansonsten Startspieler zuerst.
     */
    fun determineAheadTeam(): Team =
            ships.maxByOrNull { ship ->
                shipAdvancePoints(ship) * 100 +
                ship.speed * 10 +
                ship.coal
            }?.team ?: startTeam
    
    fun shipAdvancePoints(ship: Ship) =
            board.segmentIndex(ship.position).let { segmentIndex ->
                segmentIndex * POINTS_PER_SEGMENT +
                board.segments[segmentIndex].globalToLocal(ship.position).arrayX + 1
            }
    
    fun calculatePoints(ship: Ship) =
        shipAdvancePoints(ship) + ship.passengers * MQConstants.POINTS_PER_PASSENGER
    
    fun isCurrentShipOnCurrent() =
            board.doesFieldHaveCurrent(currentShip.position)
    
    /**
     * Führt den angegebenen Zug aus.
     * Der Zug wird auf der aktuellen Instanz ausgeführt.
     * Es sollte also eine Kopie der alten angelegt werden, wenn diese noch gebraucht wird.
     *
     * @param move Der Zug zum Ausführen
     * @throws InvalidMoveException wenn der Zug ungültig ist
     */
    override fun performMoveDirectly(move: Move) {
        if(move.actions.isEmpty()) throw InvalidMoveException(MoveMistake.NO_ACTIONS)
        
        val actions = move.actions.fold(ArrayList<Action>()) { acc, act ->
            val last = acc.lastOrNull()
            if(last is Advance && act is Advance) {
                acc[acc.lastIndex] = last + act
            } else {
                acc.add(act)
            }
            acc
        }
        actions.forEachIndexed { index, action ->
            when {
                board[currentShip.position] == Field.SANDBANK && index != 0 -> throw InvalidMoveException(MoveMistake.SAND_BANK_END, move)
                mustPush && action !is Push -> throw InvalidMoveException(MoveMistake.PUSH_ACTION_REQUIRED, move)
                action is Accelerate && index != 0 -> throw InvalidMoveException(MoveMistake.FIRST_ACTION_ACCELERATE, move)
                else -> action.perform(this)?.let { throw InvalidMoveException(it, move) }
            }
        }
        
        when {
            currentShip.movement > 0 -> throw InvalidMoveException(MoveMistake.MOVEMENT_POINTS_LEFT, move)
            currentShip.movement < 0 -> throw InvalidMoveException(MoveMistake.MOVEMENT_POINTS_MISSING, move)
        }
        
        board.pickupPassenger(currentShip)
        currentShip.points = calculatePoints(currentShip)
        if(actions.any { it is Push }) {
            if(otherShip.speed == 1)
                board.pickupPassenger(otherShip)
            otherShip.points = calculatePoints(otherShip)
        }
        
        lastMove = move
        board.revealSegment(board.segmentIndex(currentShip.position) + 2)
        advanceTurn()
    }
    
    /** Increment the turn and update the current team. */
    fun advanceTurn() {
        currentShip.freeAcc = 1
        currentShip.freeTurns = 1
        currentShip.movement = currentShip.speed
        turn++
        currentTeam = if(turn % 2 == 0) determineAheadTeam() else currentTeam.opponent()
        if(!canMove() && !isOver) {
            lastMove = null
            currentShip.crashed = true
            advanceTurn()
        }
    }
    
    /** Retrieves a list of sensible moves based on the possible actions. */
    override fun getSensibleMoves(): List<Move> =
            getSimpleMoves(currentShip.coal.coerceAtMost(1)).ifEmpty { moves().toList() }
    
    /** Performs the given [Action] on a GameState cloned just deep enough not to affect the original.
     * @return the new State */
    fun actionPerformed(action: Action): GameState =
            copy(ships = ships.clone()).also { action.perform(it) }
    
    /** Gibt progressiv alle möglichen Züge in der aktuellen Spielsituation zurück.
     * Sinnvollere Züge kommen tendenziell früher. */
    override fun moveIterator(): Iterator<Move> = object: Iterator<Move> {
        val queue = ArrayDeque<Pair<GameState, List<Action>>>(64)
        
        init {
            queue.add(this@GameState to listOf())
        }
        
        fun process(): List<Action> {
            val (state, move) = queue.removeFirst()
            if(move.lastOrNull() !is Advance) {
                state.getPossibleAdvances().forEach { adv ->
                    val newState = state.actionPerformed(adv)
                    val newMove = (move + adv)
                    val pushes = newState.getPossiblePushs()
                    pushes.takeUnless { it.isEmpty() }?.forEach { push ->
                        queue.add(newState.actionPerformed(push) to (newMove + push))
                    } ?: queue.add(newState to newMove)
                }
            }
            if(move.lastOrNull() !is Turn) {
                state.getPossibleTurns().forEach { turn ->
                    queue.add(state.actionPerformed(turn) to (move + turn))
                }
            }
            if(move.isEmpty()) {
                state.getPossibleAccelerations().forEach { acc ->
                    queue.add(state.copy(ships = ships.map { ship ->
                        ship.takeUnless { it.team == state.currentTeam } ?: ship.clone().also { acc.accelerate(it) }
                    }) to listOf(acc))
                }
            }
            return move
        }
        
        fun findNext() {
            while(queue.isNotEmpty() && queue.first().first.currentShip.movement != 0)
                process()
        }
        
        override fun hasNext(): Boolean {
            findNext()
            return queue.isNotEmpty()
        }
        
        override fun next(): Move {
            findNext()
            return Move(process())
        }
    }
    
    /** Possible simple Moves (accelerate+turn+move) using at most the given coal amount.
     * If a push is needed, only one push direction is offered for simplicity. */
    fun getSimpleMoves(maxCoal: Int = currentShip.coal): List<Move> =
            // SANDBANK checkSandbankAdvances(currentShip)?.map { Move(it) } ?:
            (ArrayList<Turn?>().apply { add(null); addAll(getPossibleTurns(maxCoal.coerceAtMost(1))) }).flatMap { turn ->
                val direction = turn?.direction ?: currentShip.direction
                val availableCoal = (maxCoal - (turn?.coalCost(currentShip) ?: 0))
                val info = checkAdvanceLimit(currentShip.position, direction,
                        currentShip.movement + currentShip.freeAcc + availableCoal)
                val minMovementPoints = (currentShip.movement - currentShip.freeAcc - availableCoal).coerceAtLeast(1)
                val minDistance = info.costs.indexOfFirst { it >= minMovementPoints } + 1
                if(minDistance < 1)
                    return@flatMap emptyList()
                (minDistance..info.distance)
                        .mapNotNull { dist ->
                            Move(listOfNotNull(Accelerate(info.costUntil(dist) + (if(dist == info.distance && info.problem == AdvanceProblem.SHIP_ALREADY_IN_TARGET) 1 else 0) - currentShip.movement).takeUnless { it.acc == 0 || dist < 1 },
                                    turn, Advance(dist),
                                    if(currentShip.position + (direction.vector * dist) == otherShip.position) {
                                        val currentRotation = board.findSegment(otherShip.position)?.direction
                                        getPossiblePushs(otherShip.position, direction).maxByOrNull {
                                            currentRotation?.turnCountTo(it.direction)?.absoluteValue ?: 2
                                        } ?: return@mapNotNull null
                                    } else null
                            ))
                        }
            }
    
    /**
     * Retrieves the list of possible [Action]s for the current [Ship]
     * at the current [GameState].
     * If you want to get the follow-up [Action]s,
     * you need to perform one of these [Action]s as a [Move]
     * and use this method again on the new [GameState].
     *
     * @param position the rank the action has in the [Move]
     * @return a list of the possible actions for the current ship.
     */
    fun getPossibleActions(position: Int, maxCoal: Int = currentShip.coal): List<Action> {
        val actions: MutableList<Action> = ArrayList()
        
        if(position == 0) actions.addAll(getPossibleAccelerations(maxCoal))
        actions.addAll(getPossibleTurns(maxCoal))
        actions.addAll(getPossibleAdvances())
        if(position != 0) actions.addAll(getPossiblePushs())
        
        return actions
    }
    
    val mustPush: Boolean
        get() = currentShip.position == otherShip.position
    
    /**
     * Retrieves all possible push actions that can be performed with the available movement points.
     *
     * @return A list of all possible push actions.
     */
    fun getPossiblePushs(): List<Push> {
        if(board[currentShip.position] == Field.SANDBANK ||
           !mustPush ||
           currentShip.movement < 1) return emptyList()
        return getPossiblePushs(currentShip.position, currentShip.direction)
    }
    
    fun getPossiblePushs(position: CubeCoordinates, incomingDirection: CubeDirection): List<Push> =
            CubeDirection.values().filter { dir ->
                dir != incomingDirection.opposite() &&
                board.getFieldInDirection(dir, position)?.isEmpty == true
            }.map { Push(it) }
    
    /**
     * Returns a list of all possible turn actions for the current player
     * that consume at most the specified number of coal units.
     *
     * @return List of all turn actions
     */
    fun getPossibleTurns(maxCoal: Int = currentShip.coal): List<Turn> {
        if(board[currentShip.position] == Field.SANDBANK || mustPush) return emptyList()
        // TODO hier sollte man vielleicht einfach die ausführbaren turns in freeTurns speichern, statt die generellen Turns
        val maxTurnCount = (maxCoal + currentShip.freeTurns).coerceAtMost(3)
        return (1..maxTurnCount).flatMap { i ->
            listOf(
                    Turn(currentShip.direction.rotatedBy(i)),
                    Turn(currentShip.direction.rotatedBy(-i))
            )
        }.take(5)
    }
    
    /**
     * Gibt eine Liste aller möglichen [Advance]s für das aktuelle [Ship] in die aktuelle [HexDirection] zurück
     * mit der Anzahl der Bewegungspunkte, die das Schiff hat.
     *
     * @return List of all possible advances in the corresponding direction
     */
    fun getPossibleAdvances(): List<Advance> {
        if(currentShip.movement < 1 || mustPush) return emptyList()
        return getPossibleAdvances(currentShip)
    }
    
    fun getPossibleAdvances(ship: Ship): List<Advance> =
            checkSandbankAdvances(ship) ?: checkAdvanceLimit(ship).advances()
    
    /** @return a list of possible advances in case the ship is on a sandbank. */
    fun checkSandbankAdvances(ship: Ship): List<Advance>? {
        if(board[ship.position] == Field.SANDBANK) {
            return listOfNotNull(Advance(1).takeIf { checkAdvanceLimit(ship.position, ship.direction, 1).distance > 1 },
                    Advance(-1).takeIf { checkAdvanceLimit(ship.position, ship.direction.opposite(), 1).distance > 1 })
        }
        return null
    }
    
    data class AdvanceInfo(internal val costs: IntArray, val problem: AdvanceProblem) {
        fun costUntil(distance: Int) =
                costs[distance - 1]
        
        fun advances() = (distance downTo 1).map { Advance(it) }
        
        val distance
            get() = costs.size
    }
    
    fun checkAdvanceLimit(ship: Ship) =
            checkAdvanceLimit(ship.position, ship.direction, ship.movement)
    
    /**
     * Check how far an advancement is possible in the given direction.
     * Does not honor special conditions of the starting tile.
     * @return how far movement is possible, how many movement points it costs and why not further
     * */
    fun checkAdvanceLimit(start: CubeCoordinates, direction: CubeDirection, maxMovementPoints: Int): AdvanceInfo {
        var currentPosition = start
        var totalCost = 0
        var hasCurrent = false
        val maxMovement = maxMovementPoints.coerceIn(0, MQConstants.MAX_SPEED)
        val result = ArrayList<Int>(maxMovement)
        
        fun result(condition: AdvanceProblem) =
                AdvanceInfo(result.toIntArray(), condition)
        while(totalCost < maxMovement) {
            currentPosition += direction.vector
            val currentField = board[currentPosition]
            totalCost++
            
            if(currentField == null || !currentField.isEmpty)
                return result(AdvanceProblem.FIELD_IS_BLOCKED)
            
            if(!hasCurrent && board.doesFieldHaveCurrent(currentPosition)) {
                hasCurrent = true
                if(totalCost < maxMovement) {
                    totalCost++
                } else {
                    break
                }
            }
            
            if(ships.any { it.position == currentPosition }) {
                if(totalCost < maxMovement) {
                    result.add(totalCost)
                    return result(AdvanceProblem.SHIP_ALREADY_IN_TARGET)
                }
                return result(AdvanceProblem.INSUFFICIENT_PUSH)
            }
            
            if(currentField == Field.SANDBANK)
                return result(AdvanceProblem.MOVE_END_ON_SANDBANK)
            
            result.add(totalCost)
        }
        return result(AdvanceProblem.MOVEMENT_POINTS_MISSING)
    }
    
    /**
     * Returns the list of all possible Acceleration actions that require at most the given coal amount.
     * @param maxCoal maximum amount of coal to consume per action
     * @return List of all possible Acceleration actions
     */
    fun getPossibleAccelerations(maxCoal: Int = currentShip.coal): List<Accelerate> {
        if(mustPush) return emptyList()
        
        return (1..maxCoal + currentShip.freeAcc).flatMap { i ->
            listOfNotNull(
                    Accelerate(i).takeIf { MQConstants.MAX_SPEED >= currentShip.speed + i },
                    Accelerate(-i).takeIf { MQConstants.MIN_SPEED <= currentShip.speed - i }
            )
        }
    }
    
    // In rare cases this returns true on the server even though the player cannot move
    // because the target tile is not revealed yet
    fun canMove() = !currentShip.crashed && moveIterator().hasNext()
    
    override val winCondition: WinCondition?
        get() =
            arrayOf({ ships.singleOrNull { inGoal(it) }?.let { WinCondition(it.team, MQWinReason.GOAL) } },
                    { ships.singleOrNull { it.crashed }?.let { WinCondition(it.team.opponent(), MQWinReason.CRASHED) } },
                    {
                        val dist = board.segmentDistance(ships.first().position, ships.last().position)
                        WinCondition(ships[if(dist > 0) 0 else 1].team, MQWinReason.SEGMENT_DISTANCE).takeIf { dist.absoluteValue > 3 }
                    },
            ).firstNotNullOfOrNull { it() }
    
    override val isOver: Boolean
        get() = when {
            // Bedingung 1: ein Dampfer mit 2 Passagieren erreicht ein Zielfeld mit Geschwindigkeit 1
            turn % 2 == 0 && ships.any { inGoal(it) } -> true
            // Bedingung 2: ein Spieler macht einen ungültigen Zug.
            // Das wird durch eine InvalidMoveException während des Spiels behandelt.
            // Bedingung 3: am Ende einer Runde liegt ein Dampfer mehr als 3 Spielsegmente zurück
            board.segmentDistance(ships.first().position, ships.last().position).absoluteValue > 3 -> true
            // Bedingung 4: das Rundenlimit von 30 Runden ist erreicht
            turn / 2 >= MQConstants.ROUND_LIMIT -> true
            // Bedingung 5: beide Spieler können sich nicht mehr bewegen
            lastMove == null && !canMove() -> true
            // ansonsten geht das Spiel weiter
            else -> false
        }
    
    fun inGoal(ship: Ship) =
            ship.passengers > 1 && board.effectiveSpeed(ship) < 2 && board[ship.position] == Field.GOAL
    
    override fun getPointsForTeam(team: ITeam): IntArray =
            ships[team.index].let { ship ->
                if(ship.crashed)
                    intArrayOf(0, 0)
                else
                    intArrayOf(ship.points, ship.passengers)
            }
    
    override fun getPointsForTeamExtended(team: ITeam): IntArray =
            ships[team.index].let { ship ->
                intArrayOf(*getPointsForTeam(team), ship.coal * 2, if(inGoal(ship)) MQConstants.FINISH_POINTS else 0)
            }
    
    override fun teamStats(team: ITeam): List<Pair<String, Int>> =
            ships.first { it.team == team }.let {
                listOf(
                        "Passagiere" to it.passengers,
                        "Geschwindigkeit" to it.speed,
                        "Kohle" to it.coal,
                )
            }
    
    override fun clone(): GameState = copy(board = board.clone(), ships = ships.clone())
    
    override fun toString() =
            "GameState $turn, $currentTeam ist dran [${ships.joinToString { "${it.team.index.plus(1)}:C${it.coal}S${it.speed}" }}]"
    
    override fun longString() =
            "$this\n${ships.joinToString("\n")}\nLast Move: $lastMove\n$board"
    
}
