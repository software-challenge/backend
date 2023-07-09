package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.*
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn
import sc.plugin2024.exceptions.MoveException
import sc.shared.InvalidMoveException
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
): TwoPlayerGameState<Move>(Team.ONE) {
    
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
                val shipOne = Team.ONE.pieces.first() as Ship
                val shipTwo = Team.TWO.pieces.first() as Ship
                
                currentRoundStarter = when {
                    // Erstens, der Spieler, dessen Dampfer sich am dichtesten am Ziel befindet, beginnt
                    board.closestShipToGoal(shipOne, shipTwo) != null -> {
                        board.closestShipToGoal(shipOne, shipTwo)?.team as Team
                    }
                    
                    // Zweitens, sollte der Dampfer mit der höheren Geschwindigkeit beginnen
                    shipOne.speed != shipTwo.speed -> decideStarter(shipOne, shipTwo) {
                        (it.pieces.first() as Ship).speed == maxOf(shipOne.speed, shipTwo.speed)
                    }
                    
                    // Drittens, sollte der Dampfer mit dem höheren Kohlevorrat beginnen
                    else -> decideStarter(shipOne, shipTwo) {
                        (it.pieces.first() as Ship).coal == maxOf(shipOne.coal, shipTwo.coal)
                    }
                }
            }
            return if(currentRoundStarter == Team.ONE) Team.TWO else Team.ONE
        }
    
    /**
     * Determines which [Team] will start the current round.
     *
     * The starting team is determined based on the following criteria:
     * 1. If the currentRoundStarter function returns true for [Team.ONE], then [Team.ONE] starts.
     * 2. If the currentRoundStarter function returns true for [Team.TWO], then [Team.TWO] starts.
     * 3. If both currentRoundStarter function calls return false, the ship with the highest X-coordinate starts.
     * 4. If both ships have the same X-coordinate, the ship with the highest Y-coordinate starts.
     *
     * @param shipOne The first ship.
     * @param shipTwo The second ship.
     * @param currentRoundStarter A function that determines the starting team based on the current round.
     *
     * @return The team that will start the current round.
     */
    private fun decideStarter(shipOne: Ship, shipTwo: Ship, currentRoundStarter: (teamOne: Team) -> Boolean): Team {
        return when {
            currentRoundStarter.invoke(Team.ONE) -> Team.ONE
            currentRoundStarter.invoke(Team.TWO) -> Team.TWO
            // Viertens, sollte der Dampfer, der am weitesten rechts steht (höchste X-Koordinate), beginnen
            shipOne.position.coordinate.x > shipTwo.position.coordinate.x -> Team.ONE
            shipOne.position.coordinate.x < shipTwo.position.coordinate.x -> Team.TWO
            // Fünftens, sollte der Dampfer, der am weitesten unten steht (höchste Y-Koordinate), beginnen
            shipOne.position.coordinate.y > shipTwo.position.coordinate.y -> Team.ONE
            else -> Team.TWO
        }
    }
    
    /**
     * Executes the specified move and returns the resulting game state.
     *
     * @param move The move to perform.
     * @return The game state after performing the move.
     * @throws InvalidMoveException if the move is invalid.
     */
    override fun performMove(move: Move): GameState {
        move.orderActions()
        val copiedState = this.copy()
        val currentShip = copiedState.currentTeam.pieces.first() as Ship
        val otherShip = copiedState.otherTeam.pieces.first() as Ship
        
        if(move.actions.isEmpty()) {
            throw InvalidMoveException(MoveException.NO_ACTIONS)
        }
        for(action in move.actions) {
            
            if(currentShip.position.coordinate == otherShip.position.coordinate && action !is Push) {
                throw InvalidMoveException(
                        MoveException.PUSH_ACTION_REQUIRED)
            }
            
            if(action is Advance && action.endsTurn) {
                if(action.order < move.actions.size - 1) {
                    throw InvalidMoveException(MoveException.SAND_BANK_END)
                }
            }
            
            action.perform(copiedState, currentShip)
        }
        // pick up passenger
        if(currentShip.speed == 1 && board.canPickupPassenger(currentShip)) {
            copiedState.board.pickupPassenger(currentShip)
        }
        // otherPlayer could possibly pick up Passenger in enemy turn
        if(otherShip.speed == 1 && board.canPickupPassenger(otherShip)) {
            copiedState.board.pickupPassenger(otherShip)
        }
        if(currentShip.movement > 0) { // check whether movement points are left
            throw InvalidMoveException((MoveException.MOVEMENT_POINTS_LEFT))
        }
        if(currentShip.movement < 0) { // check whether movement points are left
            throw InvalidMoveException(MoveException.EXCESS_MOVEMENT_POINTS)
        }
        
        return copiedState
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
    
    private fun generateActionCombinations(actions: List<Action>, comboSize: Int, orderShift: Int = 0): List<List<Action>> {
        return if(comboSize == 0) {
            listOf(emptyList())
        } else if(actions.isEmpty()) {
            emptyList()
        } else {
            val head = actions.first().apply { order += orderShift } // adding size to order attribute
            val tail = actions.drop(1)
            val withHead = generateActionCombinations(tail, comboSize - 1, orderShift + 1).map { combo -> combo + head }
            val withoutHead = generateActionCombinations(tail, comboSize, orderShift + 1)
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
        val push = java.util.ArrayList<Push>()
        val currentShip = currentTeam.pieces.first() as Ship
        val otherShip = otherTeam.pieces.first() as Ship
        val from: Field = currentShip.position
        if(from.type == FieldType.SANDBANK || currentShip.position == otherShip.position) { // niemand darf von einer Sandbank herunterpushen.
            return push
        }
        val direction: HexDirection = currentShip.direction
        HexDirection.values().forEach { dirs ->
            board.getFieldInDirection(dirs, from)?.let { to ->
                if(dirs !== direction.opposite() && to.isPassable() && currentShip.movement >= 1) {
                    when(to.type) {
                        // TODO wie wollen wir hier die Order festlegen?
                        //  Ich dachte daran, dass auf null zu setzen, aber das scheint etwas sketchy zu sein
                        FieldType.LOG -> if(currentShip.movement >= 2) push.add(Push(0, dirs))
                        else -> push.add(Push(0, dirs))
                    }
                }
            }
        }
        return push
    }
    
    /**
     * Returns a list of all possible turn actions that consume at most the specified number of coal units.
     *
     * @return List of all turn actions
     */
    fun getPossibleTurns(): List<Turn> {
        val turns: java.util.ArrayList<Turn> = java.util.ArrayList<Turn>()
        val currentShip = currentTeam.pieces.first() as Ship
        if(currentShip.position.type == FieldType.SANDBANK) {
            return turns
        }
        // TODO hier sollte man vielleicht einfach die ausführbaren turns in freeTurns speichern, statt die generellen Turns
        val maxTurn = min(3.0, (currentShip.coal + currentShip.freeTurns).toDouble()).toInt()
        for(i in 1..maxTurn) {
            // TODO wie wollen wir hier die Order festlegen?
            //  Ich dachte daran, dass auf null zu setzen, aber das scheint etwas sketchy zu sein
            turns.add(Turn(0, currentShip.direction.turnBy(i)))
            turns.add(Turn(0, currentShip.direction.turnBy(-i)))
        }
        return turns
    }
    
    /**
     * Returns a list of all possible advances for the current ship in the given direction
     * with the number of movement points the ship has.
     *
     * @return List of all possible advances in the corresponding direction
     */
    fun getPossibleAdvances(): List<Advance> {
        val step = java.util.ArrayList<Advance>()
        val currentShip = currentTeam.pieces.first() as Ship
        val start: Field = currentShip.position
        val otherShip = otherTeam.pieces.first() as Ship
        
        val eligibleForMovement = start.type == FieldType.SANDBANK && currentShip.movement > 0
        if(!eligibleForMovement) return step
        
        val directions = listOf(
                Pair(currentShip.direction.opposite(), -1),
                Pair(currentShip.direction, 1)
        )
        
        directions.forEach { (direction, coordinate) ->
            board.getFieldInDirection(direction, start)?.let {
                if(it.isPassable()) {
                    // TODO wie wollen wir hier die Order festlegen?
                    //  Ich dachte daran, dass auf null zu setzen, aber das scheint etwas sketchy zu sein
                    step.add(Advance(0, coordinate))
                }
            }
        }
        
        var i = 0
        while(currentShip.movement > 0) {
            i++
            val next: Field? = board.getFieldInDirection(currentShip.direction, start)
            if(next != null && next.isPassable()) {
                currentShip.movement--
                if(next.type == FieldType.LOG) currentShip.movement--
                
                if(currentShip.movement >= 0) {
                    // TODO wie wollen wir hier die Order festlegen?
                    //  Ich dachte daran, dass auf null zu setzen, aber das scheint etwas sketchy zu sein
                    step.add(Advance(0, i))
                }
                
                if(next.type == FieldType.LOG) return step
                
                if(next.type == FieldType.SANDBANK || next == otherShip.position) {
                    return step
                }
            } else {
                return step
            }
        }
        return step
    }
    
    /**
     * Returns the list of all possible Acceleration actions that require at most the given coal number.
     *
     * @return List of all possible Acceleration actions
     */
    fun getPossibleAccelerations(): List<Acceleration> {
        val currentShip = currentTeam.pieces.first() as Ship
        val acc: java.util.ArrayList<Acceleration> = java.util.ArrayList<Acceleration>()
        for(i in 0..currentShip.coal) {
            if(currentShip.speed < 6 - i) {
                // TODO wie wollen wir hier die Order festlegen?
                //  Ich dachte daran, dass auf null zu setzen, aber das scheint etwas sketchy zu sein
                acc.add(Acceleration(0, 1 + i)) // es wird nicht zu viel beschleunigt
            }
            if(currentShip.speed > 1 + i) {
                acc.add(Acceleration(0, -1 - i)) // aber zu viel abgebremst
            }
        }
        return acc
    }
    
    // TODO ich glaube das man nie immovable sein kann, oder?
    private fun immovable(ship: ITeam) = true
    
    // TODO
    override val isOver: Boolean = true
    override fun getPointsForTeam(team: ITeam): IntArray {
        // TODO
        return intArrayOf(0, 0)
    }
    
    override fun clone(): IGameState {
        TODO("Not yet implemented")
    }
    
    override fun toString(): String = "GameState(board=$board, turn=$turn, lastMove=$lastMove)"
}
