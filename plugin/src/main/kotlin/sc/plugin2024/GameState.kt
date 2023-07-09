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
 * Der aktuelle Spielstand.
 *
 * Er hält alle Informationen zur momentanen Runde,
 * mit deren Hilfe der nächste Zug berechnet werden kann.
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
    
    override val currentTeam: Team
        get() = currentTeamFromTurn().run { takeIf { !immovable(it) } ?: opponent() }
    
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
        
        for (i in 1..actions.size) {
            val combos = generateActionCombinations(actions, i)
            for (combo in combos) {
                moves.add(Move(combo))
            }
        }
        return moves
    }
    
    /**
     * Generates all possible combinations of actions of a given size.
     *
     * @param actions the list of actions to generate combinations from.
     * @param comboSize the size of the combinations to generate.
     * @return the list of combinations of actions.
     */
    private fun generateActionCombinations(actions: List<Action>, comboSize: Int): List<List<Action>> {
        return if (comboSize == 0) {
            listOf(emptyList())
        } else if (actions.isEmpty()) {
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
                if (dirs !== direction.opposite() && to.isPassable() && currentShip.movement >= 1) {
                    when (to.type) {
                        // TODO wie wollen wir hier die Order festlegen?
                        //  Ich dachte daran, dass auf null zu setzen, aber das scheint etwas sketchy zu sein
                        FieldType.LOG -> if (currentShip.movement >= 2) push.add(Push(0, dirs))
                        else -> push.add(Push(0, dirs))
                    }
                }
            }
        }
        return push
    }
    
    /**
     * Returns a list of all possible turn actions that consume at most the specified amount of coal units.
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
