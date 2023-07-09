package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.*
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
    
    override fun getSensibleMoves(): List<IMove> {
        // TODO
    }
    
    fun getPossibleActions(): List<Action> {
        val actions: MutableList<Action> = ArrayList()
        val currentShip = currentTeam.pieces.first() as Ship
        val otherShip = otherTeam.pieces.first() as Ship
        if(currentShip.position == otherShip.position) {
            actions.addAll(getPossiblePushs(player, movement))
        } else {
            actions.addAll(getPossibleAdvances(player, movement, player.getDirection(), coal))
            actions.addAll(getPossibleTurns(player, freeTurn, coal))
            if(acceleration) {
                actions.addAll(getPossibleAccelerations(player, coal))
            }
        }
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
        if(from.type === FieldType.SANDBANK) { // niemand darf von einer Sandbank herunterpushen.
            return push
        }
        val direction: HexDirection = currentShip.direction
        for(dirs in HexDirection.values()) {
            val to: Field = board.getFieldInDirection(dirs, from)!!
            if(dirs !== direction.opposite() && to.isPassable() && currentShip.movement >= 1) {
                if(to.type === FieldType.LOG && currentShip.movement >= 2) {
                    // TODO wie wollen wir hier die Order festlegen? Ich dachte daran, dass auf null zu setzen, aber das scheint etwas sketchy zu sein
                    push.add(Push(0, dirs))
                } else if(to.type !== FieldType.LOG) {
                    push.add(Push(0, dirs))
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
            // TODO wie wollen wir hier die Order festlegen? Ich dachte daran, dass auf null zu setzen, aber das scheint etwas sketchy zu sein
            turns.add(Turn(0, currentShip.direction.turnBy(i)))
            turns.add(Turn(0, currentShip.direction.turnBy(-i)))
        }
        return turns
    }
    
    /**
     * Gibt alle Bewegungsaktionen des Spielers zurück, die in die gegebene Richtung
     * mit einer festen Anzahl von Bewegungspunkten möglich sind.
     * @param player Spieler
     * @param movement Geschwindigkeit
     * @param direction Richtung
     * @param coal Kohleeinheiten, die zur Verfügung stehen
     * @return Liste aller möglichen Züge des Spielers in entsprechende Richtung
     */
    fun getPossibleAdvances(player: Player, movement: Int, direction: Direction, coal: Int): List<Advance> {
        var movement = movement
        val step = java.util.ArrayList<Advance>()
        val start: Field = player.getField(board)
        var i = 0
        val enemy: Player = if(player.getPlayerColor() === PlayerColor.RED) blue else red
        if(start.getType() === FieldType.SANDBANK && movement > 0) {
            val fieldBehind: Field = start.getFieldInDirection(direction.getOpposite(), board)
            if(fieldBehind != null && fieldBehind.isPassable()) {
                step.add(Advance(-1))
            }
            val fieldInFront: Field = start.getFieldInDirection(direction, board)
            if(fieldInFront != null && fieldInFront.isPassable()) {
                step.add(Advance(1))
            }
            return step
        }
        while(movement > 0) {
            i++
            val next: Field = start.getFieldInDirection(direction, board)
            if(next != null && next.isPassable()) {
                movement--
                if(next.getType() === FieldType.LOG) { // das Überqueren eines Baumstammfeldes verbraucht doppelt so viele Bewegungspunkte
                    movement--
                    if(movement >= 0) {
                        step.add(Advance(i))
                    }
                } else {
                    if(movement >= 0) {
                        step.add(Advance(i))
                    }
                    if(next.getType() === FieldType.SANDBANK || next.equals(enemy.getField(board))) {
                        return step
                    }
                }
            } else {
                return step
            }
        }
        return step
    }
    
    /**
     * Liefert alle Beschleunigungsaktionen, die höchstens die übergebene Kohlezahl benötigen.
     * @param player Spieler
     * @param coal Kohle, die für die Beschleunigung benötigt werden darf.
     * @return Liste aller Beschleunigungsaktionen
     */
    fun getPossibleAccelerations(player: Player, coal: Int): List<Acceleration> {
        val acc: java.util.ArrayList<Acceleration> = java.util.ArrayList<Acceleration>()
        for(i in 0..coal) {
            if(player.getSpeed() < 6 - i) {
                acc.add(Acceleration(1 + i)) // es wird nicht zu viel beschleunigt
            }
            if(player.getSpeed() > 1 + i) {
                acc.add(Acceleration(-1 - i)) // aber zu viel abgebremst
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
    
    override fun toString(): String = "GameState(board=$board, turn=$turn, lastMove=$lastMove)"
}
