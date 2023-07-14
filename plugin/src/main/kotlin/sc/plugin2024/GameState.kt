package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.*
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn
import sc.plugin2024.exceptions.AccException
import sc.plugin2024.exceptions.MoveException
import sc.plugin2024.util.PluginConstants
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
        
        val ships: List<Ship> = listOf(),
): TwoPlayerGameState<Move>(Team.ONE) {
    
    override fun clone(): GameState = copy(board = board.clone(), ships = ships.clone())
    
    val currentShip: Ship
        get() {
            return if(currentTeam == Team.ONE) ships.first() else ships.last()
        }
    
    val otherShip: Ship
        get() {
            return if(currentTeam == Team.ONE) ships.last() else ships.first()
        }
    
    /**
     * The player who started the current round.
     * By default, [Team.ONE] is the one starting the first round.
     */
    private var currentRoundStarter: Team = Team.ONE
    
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
        get() {
            // Wenn es das Ende der Runde ist (d.h., beide Spieler haben einen Zug gemacht),
            // dann beginnt der nächste Zug gemäß den gegebenen Regeln
            if(turn % 2 == 0) {
                val shipOne = ships.first()
                val shipTwo = ships.last()
                
                currentRoundStarter = when {
                    // Erstens, der Spieler, dessen Dampfer sich am dichtesten am Ziel befindet, beginnt
                    board.closestShipToGoal(shipOne, shipTwo) != null -> {
                        board.closestShipToGoal(shipOne, shipTwo)?.team as Team
                    }
                    // Zweitens, sollte der Dampfer mit der höheren Geschwindigkeit beginnen
                    shipOne.speed != shipTwo.speed -> {
                        if(shipOne.speed == maxOf(shipOne.speed, shipTwo.speed)) Team.ONE else Team.TWO
                    }
                    // Drittens, sollte der Dampfer mit dem höheren Kohlevorrat beginnen
                    shipOne.coal != shipTwo.coal -> {
                        if(shipOne.coal == maxOf(shipOne.coal, shipTwo.coal)) Team.ONE else Team.TWO
                    }
                    // Viertens, sollte der Dampfer, der am weitesten rechts steht (höchste X-Koordinate), beginnen
                    shipOne.position.q != shipTwo.position.q -> {
                        if(shipOne.position.q > shipTwo.position.q) Team.ONE else Team.TWO
                    }
                    // Fünftens, sollte der Dampfer, der am weitesten unten steht (höchste Y-Koordinate), beginnen
                    else -> {
                        if(shipOne.position.r > shipTwo.position.r) Team.ONE else Team.TWO
                    }
                }
            }
            return if(currentRoundStarter == Team.ONE) Team.TWO else Team.ONE
        }
    
    /**
     * Executes the specified move.
     *
     * @param move The move to perform.
     * @throws InvalidMoveException if the move is invalid.
     */
    override fun performMoveDirectly(move: Move) {
        if(move.actions.isEmpty()) {
            throw InvalidMoveException(MoveException.NO_ACTIONS)
        }
        move.actions.forEachIndexed { index, action ->
            
            if(board[currentShip.position] == FieldType.SANDBANK && index != 0) {
                throw InvalidMoveException(MoveException.SAND_BANK_END)
            }
            
            if(action is Acceleration && index != 0) {
                throw InvalidMoveException(AccException.FIRST_ACTION_ACCELERATE)
            }
            
            if(currentShip.position == otherShip.position && action !is Push) {
                throw InvalidMoveException(
                        MoveException.PUSH_ACTION_REQUIRED)
            }
            
            action.perform(this, currentShip)
        }
        // pick up passenger
        if(currentShip.speed == 1) {
            this.board.pickupPassenger(currentShip)
        }
        // otherPlayer could possibly pick up Passenger in enemy turn
        if(otherShip.speed == 1) {
            this.board.pickupPassenger(otherShip)
        }
        if(currentShip.movement > 0) { // check whether movement points are left
            throw InvalidMoveException((MoveException.MOVEMENT_POINTS_LEFT))
        }
        if(currentShip.movement < 0) { // check whether movement points are left
            throw InvalidMoveException(MoveException.EXCESS_MOVEMENT_POINTS)
        }
    }
    
    /**
     * Retrieves a list of sensible moves based on the possible actions.
     *
     * @return a list of sensible moves
     */
    override fun getSensibleMoves(): List<IMove> {
        val actions = getPossibleActions()
        val moves = mutableListOf<Move>()
        
        for(i in 1..actions.size) {
            val combos = generateActionCombinations(actions, i)
            for(combo in combos) {
                moves.add(Move(combo))
            }
        }
        return moves
    }
    
    private fun generateActionCombinations(actions: List<Action>, comboSize: Int): List<List<Action>> {
        return if(comboSize == 0) {
            listOf(emptyList())
        } else if(actions.isEmpty()) {
            emptyList()
        } else {
            val head = actions.first()
            val tail = actions.drop(1)
            val withHead = generateActionCombinations(tail, comboSize - 1).map { combo -> combo + head }
            val withoutHead = generateActionCombinations(tail, comboSize)
            withHead + withoutHead
        }
    }
    
    /**
     * Retrieves the list of possible [Action]s for the current [Ship]
     * at the current [GameState].
     * If you want to get the follow-up [Action]s,
     * you need tho perform one of these [Action]s as a [Move]
     * and use this method again on the new [GameState]
     *
     * @return a list of the possible actions for the current ship.
     */
    fun getPossibleActions(): List<Action> {
        val actions: MutableList<Action> = ArrayList()
        
        actions.addAll(getPossiblePushs())
        actions.addAll(getPossibleAdvances())
        actions.addAll(getPossibleTurns())
        actions.addAll(getPossibleAccelerations())
        
        return actions
    }
    
    /**
     * Retrieves all possible push actions that can be performed with the available movement points.
     *
     * @return A list of all possible push actions.
     */
    fun getPossiblePushs(): List<Push> {
        val push = mutableListOf<Push>()
        val from: CubeCoordinates = currentShip.position
        if(board[from] == FieldType.SANDBANK || currentShip.position == otherShip.position) { // niemand darf von einer Sandbank herunterpushen.
            return push
        }
        val direction: CubeDirection = currentShip.direction
        CubeDirection.values().forEach { dirs ->
            board.getFieldInDirection(dirs, from)?.let { to ->
                if(dirs !== direction.opposite() && to.isEmpty && currentShip.movement >= 1) {
                    push.add(Push(dirs))
                }
            }
        }
        return push
    }
    
    /**
     * Returns a list of all possible turn actions for the current player
     * that consume at most the specified number of coal units.
     *
     * @return List of all turn actions
     */
    fun getPossibleTurns(maxCoal: Int = currentShip.coal): List<Turn> {
        val turns = ArrayList<Turn>()
        if(board[currentShip.position] == FieldType.SANDBANK) {
            return turns
        }
        // TODO hier sollte man vielleicht einfach die ausführbaren turns in freeTurns speichern, statt die generellen Turns
        val maxTurn = min(3.0, (currentShip.coal + currentShip.freeTurns).toDouble()).toInt()
        for(i in 1..maxTurn) {
            turns.add(Turn(currentShip.direction.rotatedBy(i)))
            turns.add(Turn(currentShip.direction.rotatedBy(-i)))
        }
        return turns
    }
    
    /**
     * Gibt eine Liste aller möglichen [Advance]s für das aktuelle [Ship] in die aktuelle [HexDirection] zurück
     * mit der Anzahl der Bewegungspunkte, die das Schiff hat.
     *
     * @return List of all possible advances in the corresponding direction
     */
    fun getPossibleAdvances(): List<Advance> {
        val step = mutableListOf<Advance>()
        val start: CubeCoordinates = currentShip.position
        
        val eligibleForMovement = board[start] == FieldType.SANDBANK && currentShip.movement > 0
        if(!eligibleForMovement) return step.toList()
        
        val directions = listOf(
                Pair(currentShip.direction.opposite(), -1),
                Pair(currentShip.direction, 1)
        )
        
        directions.forEach { (direction, coordinate) ->
            board.getFieldInDirection(direction, start)?.let {
                if(it.isEmpty) {
                    step.add(Advance(coordinate))
                }
            }
        }
        
        while(currentShip.movement > 0) {
            val next: FieldType? = board.getFieldInDirection(currentShip.direction, start)
            val isNextEmptyOrNull = next?.isEmpty ?: return step.toList()

            if(isNextEmptyOrNull) {
                currentShip.movement--

                if(currentShip.movement >= 0) {
                    step.add(Advance(0))
                }
                
                val destination = currentShip.position + currentShip.direction.vector

                if(next == FieldType.SANDBANK || destination == otherShip.position) {
                    return step.toList()
                }
            }

            return step.toList()
        }
        return step.toList()
    }
    
    /**
     * Returns the list of all possible Acceleration actions that require at most the given coal number.
     *
     * @return List of all possible Acceleration actions
     */
    fun getPossibleAccelerations(): List<Acceleration> {
        val acc: java.util.ArrayList<Acceleration> = java.util.ArrayList<Acceleration>()
        for(i in 0..currentShip.coal) {
            if(currentShip.speed < 6 - i) {
                acc.add(Acceleration(1 + i)) // es wird nicht zu viel beschleunigt
            }
            if(currentShip.speed > 1 + i) {
                acc.add(Acceleration(-1 - i)) // aber zu viel abgebremst
            }
        }
        return acc
    }

    private fun immovable(ship: ITeam) = true
    
    override val isOver: Boolean
        get() {
            val shipOne = ships.first()
            val shipTwo = ships.last()
            
            // Bedingung 1: ein Dampfer mit 2 Passagieren erreicht ein Zielfeld mit Geschwindigkeit 1
            if((shipOne.passengers == 2 && shipOne.speed == 1 && board[shipOne.position] == FieldType.GOAL) ||
               (shipTwo.passengers == 2 && shipTwo.speed == 1 && board[shipTwo.position] == FieldType.GOAL)) {
                return true
            }
            
            // Bedingung 2: ein Spieler macht einen ungültigen Zug
            // Dies wird durch eine InvalidMoveException während des Spiels behandelt.
            
            // Bedingung 3: am Ende einer Runde liegt ein Dampfer mehr als 3 Spielsegmente zurück
            if(board.segmentDistance(shipOne.position, shipTwo.position)?.let { abs(it) }!! > 3) {
                return true
            }
            
            // Bedingung 4: das Rundenlimit von 30 Runden ist erreicht
            if(turn / 2 >= PluginConstants.ROUND_LIMIT) {
                return true
            }
            
            // ansonsten geht das Spiel weiter
            return false
        }
    
    override fun getPointsForTeam(team: ITeam): IntArray {
        val ship = if(team == Team.ONE) ships.first() else ships.last()
        return intArrayOf(ship.points)
    }
    
    override fun toString(): String = "GameState(board=$board, turn=$turn, lastMove=$lastMove)"
}
