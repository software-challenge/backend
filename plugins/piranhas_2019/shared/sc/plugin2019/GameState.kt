package sc.plugin2019

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import org.slf4j.LoggerFactory
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.AbstractPlayer
import sc.plugin2019.util.Constants
import sc.plugin2019.util.Constants.BOARD_SIZE
import sc.plugin2019.util.Constants.MAX_FISH
import sc.plugin2019.util.GameRuleLogic.isValidToMove
import sc.shared.InvalidGameStateException
import sc.shared.InvalidMoveException
import sc.shared.PlayerColor
import java.awt.Point
import java.util.*

class GameState(
        override var red: Player = Player(PlayerColor.RED),
        override var blue: Player = Player(PlayerColor.BLUE),
        override val board: Board = Board()) : TwoPlayerGameState<Player, Move>(), Cloneable {

    constructor(state: GameState) : this(state.red.clone(), state.blue.clone(), state.board.clone()) {
        lastMove = state.lastMove?.clone()
        turn = state.turn
        currentPlayerColor = state.currentPlayerColor
        startPlayerColor = state.startPlayerColor
    }

    override fun clone() = GameState(this)

    @XStreamAsAttribute
    override var turn = 0
        set(value) {
            val turnLimit = Constants.ROUND_LIMIT * 2
            if (value > turnLimit) throw InvalidGameStateException("Turn $value exceeded maxTurn $turnLimit")
            field = value
        }

    fun getField(x: Int, y: Int) = board.getField(x, y)

    /** wechselt den Spieler, der aktuell an der Reihe ist, anhand der Zugzahl [turn]  */
    fun switchCurrentPlayer() {
        currentPlayerColor = if (turn % 2 == 0) PlayerColor.RED else PlayerColor.BLUE
    }

    override fun getPointsForPlayer(playerColor: PlayerColor) = greatestSwarmSize(playerColor)

    fun getPlayerStats(player: AbstractPlayer): IntArray = getPlayerStats(player.playerColor)

    /**
     * Liefert Statusinformationen zu einem Spieler als Array mit folgenden
     * Einträgen:
     *
     *  * [0] - Punktekonto des Spielers (Größe des Schwarms)
     *
     */
    fun getPlayerStats(playerColor: PlayerColor): IntArray =
            getGameStats()[if (playerColor == PlayerColor.RED) Constants.GAME_STATS_RED_INDEX else Constants.GAME_STATS_BLUE_INDEX]

    /**
     * Liefert Statusinformationen zum Spiel. Diese sind ein Array der
     * [Spielerstats][.getPlayerStats], wobei getGameStats()[0],
     * einem Aufruf von getPlayerStats(PlayerColor.RED) entspricht.
     *
     * @return Statusinformationen beider Spieler
     *
     * @see [getPlayerStats]
     */
    fun getGameStats(): Array<IntArray> {
        val stats = Array(2) { IntArray(1) }
        stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_SWARM_SIZE] = this.getPointsForPlayer(PlayerColor.RED)
        stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_SWARM_SIZE] = this.getPointsForPlayer(PlayerColor.BLUE)
        return stats
    }

    /**
     * Fuegt einem Spiel einen weiteren Spieler hinzu.
     *
     * Diese Methode ist nur fuer den Spielserver relevant und sollte vom
     * Spielclient i.A. nicht aufgerufen werden!
     */
    fun addPlayer(player: Player) {
        if (player.playerColor == PlayerColor.RED) {
            red = player
        } else if (player.playerColor == PlayerColor.BLUE) {
            blue = player
        }
    }

    /**
     * Gibt eine Liste aller möglichen Züge zurück
     *
     * @return Liste von Move Objekten
     */
    fun getPossibleMoves(): ArrayList<Move> {
        val possibleMoves = ArrayList<Move>()
        val fields = getOwnFields(currentPlayer)
        for (field in fields) {
            for (direction in Direction.values()) {
                val x = field.x
                val y = field.y
                val dist = calculateMoveDistance(x, y, direction)
                try {
                    if (dist > 0 && isValidToMove(x, y, direction, dist, this)) {
                        val m = Move(x, y, direction)
                        possibleMoves.add(m)
                    }
                } catch (ignore: InvalidMoveException) {
                }

            }
        }
        return possibleMoves
    }

    fun getOwnFields(player: AbstractPlayer) = getOwnFields(player.playerColor)

    fun getOwnFields(player: PlayerColor): Set<Field> {
        val fields = HashSet<Field>()
        var size = 0
        var i = 0
        while (i < BOARD_SIZE && MAX_FISH > size) {
            var j = 0
            while (j < BOARD_SIZE && MAX_FISH > size) {
                val curField = board.getField(i, j) as Field
                if (curField.piranha.isPresent && curField.piranha.get() == player) {
                    fields.add(curField)
                    size++
                }
                j++
            }
            i++
        }
        return fields
    }

    private fun getDirectNeighbour(f: Field, parentSet: Set<Field>): Set<Field> {
        val returnSet = HashSet<Field>()
        val b = board
        for (i in -1..1) {
            for (j in -1..1) {
                val x = f.x + i
                val y = f.y + j
                if (x < 0 || x >= Constants.BOARD_SIZE || y < 0 || y >= Constants.BOARD_SIZE || i == 0 && j == 0) continue

                val field = b.getField(x, y) as Field
                if (parentSet.contains(field))
                    returnSet.add(field)
            }
        }
        return returnSet
    }

    private fun getSwarm(found: MutableSet<Field>, swarm: MutableSet<Field>): MutableSet<Field> {
        if (swarm.isEmpty() && !found.isEmpty()) {
            val field = found.iterator().next()
            swarm.add(field)
            found.remove(field)
        }

        var tmpSwarm: MutableSet<Field> = HashSet(swarm)
        // O(swarm.size()) time
        for (field in swarm) {
            // Constant time for both calls (max of 8 neighbors)
            val neighbours = getDirectNeighbour(field, found)
            tmpSwarm.addAll(neighbours)
        }

        // O(found.size()*swarm.size()) time
        // FIXME: Might be improved O(swarm.size()) should be possible
        if (swarm.size != tmpSwarm.size)
            tmpSwarm = getSwarm(found, tmpSwarm)


        swarm.addAll(tmpSwarm)

        found.removeAll(swarm)
        return swarm
    }

    fun greatestSwarm(fieldsToCheck: Set<Field>): Set<Field> {
        // Make a copy, so there will be no conflict with direct calls.
        val occupiedFields = HashSet(fieldsToCheck)
        var greatestSwarm: Set<Field> = HashSet()
        var maxSize = -1

        // this is a maximum of MAX_FISH iterations, so it is a linear iteration altogether
        while (!occupiedFields.isEmpty() && occupiedFields.size > maxSize) {
            val empty = HashSet<Field>()
            val swarm = getSwarm(occupiedFields, empty)
            if (maxSize < swarm.size) {
                maxSize = swarm.size
                greatestSwarm = swarm
            }
        }
        return greatestSwarm
    }

    fun greatestSwarm(player: PlayerColor): Set<Field> {
        val occupiedFields = getOwnFields(player)

        return greatestSwarm(occupiedFields)
    }

    fun greatestSwarmSize(player: PlayerColor): Int {
        return greatestSwarm(player).size
    }

    fun greatestSwarmSize(set: Set<Field>): Int {
        return greatestSwarm(set).size
    }

    fun isSwarmConnected(player: AbstractPlayer): Boolean {
        val fieldsWithFish = getOwnFields(player)
        val numGreatestSwarm = greatestSwarmSize(fieldsWithFish)
        return numGreatestSwarm == fieldsWithFish.size
    }

    private fun moveDistanceHorizontal(ignore: Int, y: Int): Int {
        var count = 0
        for (i in 0 until BOARD_SIZE) {
            if (board.getField(i, y).piranha.isPresent) {
                count++
            }
        }
        return count
    }

    private fun moveDistanceVertical(x: Int, ignore: Int): Int {
        var count = 0
        for (i in 0 until BOARD_SIZE) {
            if (board.getField(x, i).piranha.isPresent) {
                count++
            }
        }
        return count
    }

    private fun moveDistanceDiagonalRising(x: Int, y: Int): Int {
        var count = 0
        var cX = x
        var cY = y
        // Move down left
        while (cX >= 0 && cY >= 0) {
            if (board.getField(cX, cY).piranha.isPresent) {
                count++
            }
            cY--
            cX--
        }

        // Move up right
        cX = x + 1
        cY = y + 1
        while (cX < BOARD_SIZE && cY < BOARD_SIZE) {
            if (board.getField(cX, cY).piranha.isPresent) {
                count++
            }
            cY++
            cX++
        }
        return count
    }

    private fun moveDistanceDiagonalFalling(x: Int, y: Int): Int {
        var count = 0
        var cX = x
        var cY = y
        // Move down left
        while (cX < BOARD_SIZE && cY >= 0) {
            if (board.getField(cX, cY).piranha.isPresent) {
                count++
            }
            cY--
            cX++
        }

        // Move up right
        cX = x - 1
        cY = y + 1
        while (cX >= 0 && cY < BOARD_SIZE) {
            if (board.getField(cX, cY).piranha.isPresent) {
                count++
            }
            cY++
            cX--
        }
        return count
    }

    /**
     * Calculate the minimum steps to take from given position in given direction
     *
     * @param x         coordinate to calculate from
     * @param y         coordinate to calculate from
     * @param direction of the calcualtion
     *
     * @return -1 if Invalid move, else the steps to take
     */
    fun calculateMoveDistance(x: Int, y: Int, direction: Direction) = when (direction) {
        Direction.LEFT, Direction.RIGHT -> moveDistanceHorizontal(x, y)
        Direction.UP, Direction.DOWN -> moveDistanceVertical(x, y)
        Direction.UP_RIGHT, Direction.DOWN_LEFT -> moveDistanceDiagonalRising(x, y)
        Direction.DOWN_RIGHT, Direction.UP_LEFT -> moveDistanceDiagonalFalling(x, y)
    }

    /** Überprüft nicht, ob Feld innerhalb der Feldgrenzen */
    fun getFieldInDirection(x: Int, y: Int, direction: Direction, distance: Int): Field {
        val shift = directionShift(direction)
        return board.getField(x + shift.x * distance, y + shift.y * distance)
    }

    private fun directionShift(d: Direction): Point {
        var shiftX = 0
        var shiftY = 0
        when (d) {
            Direction.UP_RIGHT -> {
                shiftX = 1
                shiftY = 1
            }
            Direction.UP -> shiftY = 1
            Direction.DOWN_RIGHT -> {
                shiftY = -1
                shiftX = 1
            }
            Direction.RIGHT -> shiftX = 1
            Direction.DOWN_LEFT -> {
                shiftX = -1
                shiftY = -1
            }
            Direction.DOWN -> shiftY = -1
            Direction.UP_LEFT -> {
                shiftY = 1
                shiftX = -1
            }
            Direction.LEFT -> shiftX = -1
        }
        return Point(shiftX, shiftY)
    }

    fun getFieldsInDirection(x: Int, y: Int, d: Direction): List<Field> {
        val distance = calculateMoveDistance(x, y, d)
        val fields = LinkedList<Field>()
        val shift = directionShift(d)

        val b = board
        for (i in 0 until distance) {
            fields.add(b.getField(x + shift.x * i, y + shift.y * i))
        }
        return fields
    }

}