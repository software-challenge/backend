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
import kotlin.math.min

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
        /** Der zuletzt gespielte Zug. */
        override var lastMove: Move? = null,
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
        private var currentRoundStarter: Team = Team.ONE
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
    
    fun determineCurrentTeam(): Team {
        val shipOne = ships.first()
        val shipTwo = ships.last()
        val shipOneHorizontalDistance = CubeCoordinates.ORIGIN.horizontalDistanceTo(shipOne.position)
        val shipTwoHorizontalDistance = CubeCoordinates.ORIGIN.horizontalDistanceTo(shipTwo.position)
        val shipOneVerticalDistance = CubeCoordinates.ORIGIN.verticalDistanceTo(shipOne.position)
        val shipTwoVerticalDistance = CubeCoordinates.ORIGIN.verticalDistanceTo(shipTwo.position)
        
        //val criteria = arrayOf<(Ship) -> Comparable<*>>(
        //        {
        //            val segment = board.findSegment(it.position)!!
        //            board.segments[segment].tip
        //            segment * 10 +
        //        },
        //)
        //ships.sortedBy { CubeCoordinates.ORIGIN.verticalDistanceTo(it.position) }
        
        return when {
            // Erstens, der Dampfer, der am weitesten vorne steht
            board.closestShipToGoal(shipOne, shipTwo) != null -> board.closestShipToGoal(shipOne, shipTwo)?.team as Team
            // Zweitens, der Dampfer mit der höheren Geschwindigkeit
            shipOne.speed != shipTwo.speed -> if(shipOne.speed > shipTwo.speed) Team.ONE else Team.TWO
            // Drittens, der Dampfer mit dem höheren Kohlevorrat
            shipOne.coal != shipTwo.coal -> if(shipOne.coal > shipTwo.coal) Team.ONE else Team.TWO
            // Viertens, der Dampfer, der am weitesten rechts steht (höchste Q-Koordinate)
            shipOneHorizontalDistance != shipTwoHorizontalDistance ->
                if(shipOneHorizontalDistance > shipTwoHorizontalDistance) Team.ONE else Team.TWO
            // Fünftens, der Dampfer, der am weitesten außen steht
            shipOneVerticalDistance != shipTwoVerticalDistance ->
                if(shipOneVerticalDistance > shipTwoVerticalDistance) Team.ONE else Team.TWO
            else -> startTeam
        }
    }
    
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
        currentShip.points += board.findSegment(currentShip.position)?.times(POINTS_PER_SEGMENT) ?: 0
        board.findSegment(currentShip.position)?.let {
            val segmentIndex = board.segments[it]
            val xPositionInSegment = ((currentShip.position - segmentIndex.center).rotatedBy(segmentIndex.direction.turnCountTo(CubeDirection.RIGHT)).q + 1)
            currentShip.points += xPositionInSegment
        }
        when {
            currentShip.movement > 0 -> throw InvalidMoveException(MoveException.MOVEMENT_POINTS_LEFT)
            currentShip.movement < 0 -> throw InvalidMoveException(MoveException.MOVEMENT_POINTS_MISSING)
            currentShip.speed == 1 -> this.board.pickupPassenger(currentShip)
            otherShip.speed == 1 -> this.board.pickupPassenger(otherShip)
        }
        
        advanceTurn()
    }
    
    /** Increment the turn and update the current team. */
    fun advanceTurn() {
        turn++
        if(turn % 2 == 0)
            currentRoundStarter = determineCurrentTeam()
    }
    
    /**
     * Retrieves a list of sensible moves based on the possible actions.
     *
     * @return a list of sensible moves
     */
    override fun getSensibleMoves(): List<IMove> =
            getPossibleMoves(1).ifEmpty { getPossibleMoves() }
    
    fun getPossibleMoves(maxCoal: Int = currentShip.coal): List<IMove> {
        val actions = getPossibleActions(0, maxCoal)
        val moves = mutableListOf<Move>()
        
        for(action in actions) {
            moves.add(Move(listOf(action)))
            // TODO
        }
        return moves
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
           currentShip.position != otherShip.position) return emptyList()
        
        return CubeDirection.values().mapNotNull { dir ->
            board.getFieldInDirection(dir, currentShip.position)?.let { to ->
                if(dir !== currentShip.direction.opposite() && to.isEmpty && currentShip.movement >= 1) Push(dir) else null
            }
        }
    }
    
    /**
     * Returns a list of all possible turn actions for the current player
     * that consume at most the specified number of coal units.
     *
     * @return List of all turn actions
     */
    fun getPossibleTurns(maxCoal: Int = currentShip.coal): List<Turn> {
        if(board[currentShip.position] == Field.SANDBANK || currentShip.position == otherShip.position) return emptyList()
        // TODO hier sollte man vielleicht einfach die ausführbaren turns in freeTurns speichern, statt die generellen Turns
        val maxTurn = min(3.0, (maxCoal + currentShip.freeTurns).toDouble()).toInt()
        return (1..maxTurn).flatMap { i ->
            listOf(
                    Turn(currentShip.direction.rotatedBy(i)),
                    Turn(currentShip.direction.rotatedBy(-i))
            )
        }
    }
    
    /**
     * Gibt eine Liste aller möglichen [Advance]s für das aktuelle [Ship] in die aktuelle [HexDirection] zurück
     * mit der Anzahl der Bewegungspunkte, die das Schiff hat.
     *
     * @return List of all possible advances in the corresponding direction
     */
    fun getPossibleAdvances(): List<Advance> {
        when {
            currentShip.movement <= 0 || currentShip.position == otherShip.position -> return emptyList()
            board[currentShip.position] == Field.SANDBANK -> return listOf(Advance(1), Advance(-1))
        }
        
        val advances = mutableListOf<Advance>()
        for(i in 1..currentShip.movement) {
            val destination = currentShip.position + currentShip.direction.vector * i
            val next: Field? = board[destination]
            if(next == null || !next.isEmpty) break
            
            advances.add(Advance(i))
            
            if(next == Field.SANDBANK || destination == otherShip.position) break
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
    
    override val isOver: Boolean
        get() {
            val shipOne = ships.first()
            val shipTwo = ships.last()
            
            return when {
                // Bedingung 1: ein Dampfer mit 2 Passagieren erreicht ein Zielfeld mit Geschwindigkeit 1
                (shipOne.passengers == 2 && shipOne.speed == 1 && board[shipOne.position] == Field.GOAL) ||
                (shipTwo.passengers == 2 && shipTwo.speed == 1 && board[shipTwo.position] == Field.GOAL) -> true
                // Bedingung 2: ein Spieler macht einen ungültigen Zug.
                // Das wird durch eine InvalidMoveException während des Spiels behandelt.
                // Bedingung 3: am Ende einer Runde liegt ein Dampfer mehr als 3 Spielsegmente zurück
                board.segmentDistance(shipOne.position, shipTwo.position)?.let { abs(it) }!! > 3 -> true
                // Bedingung 4: das Rundenlimit von 30 Runden ist erreicht
                turn / 2 >= PluginConstants.ROUND_LIMIT -> true
                // ansonsten geht das Spiel weiter
                else -> false
            }
        }
    
    override fun getPointsForTeam(team: ITeam): IntArray = intArrayOf(if(team == Team.ONE) ships.first().points else ships.last().points)
    
    override fun toString(): String = "GameState(board=$board, turn=$turn, lastMove=$lastMove)"
}
