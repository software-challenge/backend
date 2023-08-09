package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.*
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn
import sc.plugin2024.exceptions.MoveException
import sc.plugin2024.util.PluginConstants
import sc.plugin2024.util.PluginConstants.POINTS_PER_SEGMENT
import sc.shared.InvalidMoveException
import kotlin.math.abs
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
        /** Die Anzahl an bereits getätigten Zügen. */
        @XStreamAsAttribute override var turn: Int = 0,
        val ships: List<Ship> = (CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector).let { start ->
            listOf(
                    Ship(start + CubeDirection.UP_LEFT.vector, Team.ONE),
                    Ship(start + CubeDirection.DOWN_LEFT.vector, Team.TWO)
            )
        },
        /**
         * The player who started the current round.
         * By default, [Team.ONE] is the one starting the first round.
         */
        private var currentRoundStarter: Team = ships.first().team,
        /** Der zuletzt gespielte Zug. */
        override var lastMove: Move? = null,
): TwoPlayerGameState<Move>(currentRoundStarter) {
    
    override fun clone(): GameState = copy(board = board.clone(), ships = ships.clone())
    
    val currentShip: Ship
        get() = ships[currentTeam.index]
    
    val otherShip: Ship
        get() = ships[currentTeam.opponent().index]
    
    /**
     * Get the current [Team] that is allowed to make a move.
     *
     * The current team is determined based on the rules of the game.
     * If it is the end of the round (i.e., both players have made a move),
     * then the next move starts, according to the given rules.
     *
     * @return The current team.
     */
    override val currentTeam: Team
        get() =
            if(turn % 2 == 0) {
                currentRoundStarter
            } else {
                currentRoundStarter.opponent()
            }
    
    /**
     * Determine the team that should go first at the beginning of the round.
     * 1. Weiter vorne
     * 2. Geschwindigkeit
     * 3. Kohle
     * Ansonsten Startspieler zuerst.
     */
    fun determineAheadTeam(): Team =
            ships.maxByOrNull {
                it.points * 100 +
                it.speed * 10 +
                it.coal
            }!!.team
    // TODO or something like this?
    //  Team.values().maxByOrNull { getPointsForTeam(it) }
    
    fun Ship.calculatePoints() =
            board.segmentIndex(this.position)?.let { segmentIndex ->
                segmentIndex * POINTS_PER_SEGMENT +
                board.segments[segmentIndex].globalToLocal(this.position).arrayX + 1
            } ?: 0
    
    /**
     * Führt den angegebenen Zug aus.
     * Der Zug wird auf der aktuellen Instanz ausgeführt.
     * Es sollte also eine Kopie der alten angelegt werden, wenn diese noch gebraucht wird.
     *
     * @param move Der Zug zum Ausführen
     * @throws InvalidMoveException wenn der Zug ungültig ist
     */
    override fun performMoveDirectly(move: Move) {
        if(move.actions.isEmpty()) throw InvalidMoveException(MoveException.NO_ACTIONS)
        
        move.actions.forEachIndexed { index, action ->
            when {
                board[currentShip.position] == Field.SANDBANK && index != 0 -> throw InvalidMoveException(MoveException.SAND_BANK_END)
                action is Acceleration && index != 0 -> throw InvalidMoveException(MoveException.FIRST_ACTION_ACCELERATE)
                currentShip.position == otherShip.position && action !is Push -> throw InvalidMoveException(MoveException.PUSH_ACTION_REQUIRED)
                else -> action.perform(this, currentShip)
            }
        }
        
        when {
            currentShip.movement > 0 -> throw InvalidMoveException(MoveException.MOVEMENT_POINTS_LEFT)
            currentShip.movement < 0 -> throw InvalidMoveException(MoveException.MOVEMENT_POINTS_MISSING)
            currentShip.speed == 1 -> this.board.pickupPassenger(currentShip)
            otherShip.speed == 1 -> this.board.pickupPassenger(otherShip)
        }
        
        currentShip.points = currentShip.calculatePoints()
        if(move.actions.any { it is Push })
            otherShip.calculatePoints()
        
        lastMove = move
        board.revealSegment(board.segmentIndex(currentShip.position) + 1)
        advanceTurn()
    }
    
    /** Increment the turn and update the current team. */
    fun advanceTurn() {
        turn++
        if(turn % 2 == 0)
            currentRoundStarter = determineAheadTeam()
    }
    
    /** Retrieves a list of sensible moves based on the possible actions. */
    override fun getSensibleMoves(): List<IMove> =
            getPossibleMoves(currentShip.coal.coerceAtMost(1)).ifEmpty { getPossibleMoves() }
    
    // TODO this should be a stream
    /** Possible simple Moves (accelerate+turn+move) using at most the given coal amount. */
    fun getPossibleMoves(maxCoal: Int = currentShip.coal): List<IMove> =
            (getPossibleTurns(maxCoal.coerceAtMost(1)) + null).flatMap { turn ->
                val direction = turn?.direction ?: currentShip.direction
                getPossibleAdvances(currentShip.position, direction,
                        currentShip.movement + currentShip.freeAcc + (maxCoal - (turn?.direction?.turnCountTo(currentShip.direction)?.absoluteValue?.minus(currentShip.freeTurns) ?: 0)))
                        .map { advance ->
                            Move(listOfNotNull(Acceleration(advance.distance - currentShip.movement).takeUnless { it.acc == 0 }, turn, advance,
                                    if(currentShip.position + (direction.vector * advance.distance) == otherShip.position) {
                                        val currentRotation = board.findSegment(otherShip.position)?.direction
                                        getPossiblePushs(otherShip.position, direction).maxByOrNull { currentRotation?.turnCountTo(it.direction)?.absoluteValue ?: 2 }
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
    
    /**
     * Retrieves all possible push actions that can be performed with the available movement points.
     *
     * @return A list of all possible push actions.
     */
    fun getPossiblePushs(): List<Push> {
        if(board[currentShip.position] == Field.SANDBANK ||
           currentShip.position != otherShip.position ||
           currentShip.movement >= 1) return emptyList()
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
        if(board[currentShip.position] == Field.SANDBANK || currentShip.position == otherShip.position) return emptyList()
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
        if(currentShip.movement <= 0 || currentShip.position == otherShip.position) return emptyList()
        return getPossibleAdvances(currentShip.position, currentShip.direction, currentShip.movement)
    }
    
    fun getPossibleAdvances(start: CubeCoordinates, direction: CubeDirection, movement: Int): List<Advance> {
        if(board[start] == Field.SANDBANK) return listOf(Advance(1), Advance(-1))
        
        val advances = mutableListOf<Advance>()
        for(i in 1..movement) {
            val destination = start + direction.vector * i
            val next: Field? = board[destination]
            if(next == null || !next.isEmpty) break
            
            advances.add(Advance(i))
            
            if(next == Field.SANDBANK || ships.any { it.position == destination }) break
        }
        return advances
    }
    
    /**
     * Returns the list of all possible Acceleration actions that require at most the given coal number.
     *
     * @return List of all possible Acceleration actions
     */
    fun getPossibleAccelerations(maxCoal: Int = currentShip.coal): List<Acceleration> {
        if(currentShip.position == otherShip.position) return emptyList()
        
        return (0..maxCoal).flatMap { i ->
            listOfNotNull(
                    if(currentShip.speed < 6 - i) Acceleration(1 + i) else null,
                    if(currentShip.speed > 1 + i) Acceleration(-1 - i) else null
            )
        }
    }
    
    fun canMove() =
        getSensibleMoves().isNotEmpty() // TODO make more efficient and take ship as parameter
    
    override val isOver: Boolean
        get() = when {
            // Bedingung 1: ein Dampfer mit 2 Passagieren erreicht ein Zielfeld mit Geschwindigkeit 1
            turn % 2 == 0 && ships.any { it.passengers == 2 && it.speed == 1 && board[it.position] == Field.GOAL } -> true
            // Bedingung 2: ein Spieler macht einen ungültigen Zug.
            // Das wird durch eine InvalidMoveException während des Spiels behandelt.
            // Bedingung 3: am Ende einer Runde liegt ein Dampfer mehr als 3 Spielsegmente zurück
            board.segmentDistance(ships.first().position, ships.last().position).absoluteValue > 3 -> true
            // Bedingung 4: das Rundenlimit von 30 Runden ist erreicht
            turn / 2 >= PluginConstants.ROUND_LIMIT -> true
            // Bedingung 5: Der aktuelle Dampfer kann sich nicht mehr bewegen
            !canMove() -> true
            // ansonsten geht das Spiel weiter
            else -> false
        }
    
    override fun getPointsForTeam(team: ITeam): IntArray =
            ships[team.index].let { ship ->
                intArrayOf(ship.points, ship.speed, ship.coal)
            }
    
    override fun toString() =
            "GameState $turn, $currentTeam ist dran"
    
    override fun longString() =
            "$this\n${ships.joinToString("\n")}\nLast Move: $lastMove"
    
}
