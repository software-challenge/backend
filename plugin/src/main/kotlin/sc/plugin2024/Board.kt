package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.HexDirection
import sc.api.plugins.RectangularBoard
import sc.api.plugins.TwoDBoard
import sc.plugin2024.util.Pattern
import sc.plugin2024.util.PassengerDirection
import sc.plugin2024.util.PluginConstants.MAX_ISLANDS
import sc.plugin2024.util.PluginConstants.MAX_SPECIAL
import sc.plugin2024.util.PluginConstants.MIN_ISLANDS
import sc.plugin2024.util.PluginConstants.MIN_SPECIAL
import sc.plugin2024.util.PluginConstants.NUMBER_OF_PASSENGERS
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random
import sc.plugin2024.util.PluginConstants as Constants

/**
 * A class representing the game board.
 *
 * @constructor Creates a new Board instance.
 * @property gameField The two-dimensional board representing the game field.
 * @property segments The list of segments on the board.
 */
@XStreamAlias(value = "board")
open class Board(
        gameField: TwoDBoard<Field> = initBoard(),
        var segments: ArrayList<Segment> = ArrayList(),
): RectangularBoard<Field>(gameField) {
    
    init {
        createSegment(direction = HexDirection.RIGHT, segmentStart = Coordinates(0, 0), passengers = 0, blocked = 0, special = 0, end = false)
    }
    
    /**
     * Returns the field adjacent to the given field in the specified direction.
     *
     * @param direction the direction in which to find the adjacent field
     * @param field the field for which to find the adjacent field
     * @return the adjacent field if it exists, null otherwise
     */
    open fun getFieldInDirection(direction: HexDirection, field: Field): Field? {
        val coordinateInDirection = field.coordinate.plus(direction)
        return if(coordinateInDirection.x in gameField.indices && coordinateInDirection.y in 0 until gameField[coordinateInDirection.x].size) {
            gameField[coordinateInDirection.x][coordinateInDirection.y]
        } else {
            null
        }
    }
    
    /**
     * Indicates whether a passenger can be picked up on the player's current field.
     *
     * @param ship the ship object representing the player's position and state
     * @return true if a passenger can be picked up at the player's position, false otherwise
     */
    fun canPickupPassenger(ship: Ship): Boolean {
        return PassengerDirection.values().any { passengerField ->
            val field = getFieldInDirection(passengerField.direction, ship.position)
            field?.type === passengerField.type
        } && ship.passengers < 2
    }
    
    /**
     * Calculates the distance between two fields in the number of segments.
     *
     * @param field1 The first field to calculate distance from.
     * @param field2 The second field to calculate distance from.
     * @return The distance between the given fields in the segment. If any of the fields is not found in
     *         any segment, -1 is returned.
     */
    fun segmentDistance(field1: Field, field2: Field): Int {
        val field1Index = segments.indexOfFirst { segment ->
            segment.gameField.any { row -> row.contains(field1) }
        }
        val field2Index = segments.indexOfFirst { segment ->
            segment.gameField.any { row -> row.contains(field2) }
        }
        
        return if (field1Index == -1 || field2Index == -1) {
            -1 // return -1 if any of the fields is not found in any segment
        } else {
            abs(field1Index - field2Index) // return distance
        }
    }
    
    /**
     * Picks up a passenger on the given ship.
     *
     * @param ship The ship on which to pick up the passenger.
     */
    fun pickupPassenger(ship: Ship) {
        for(passengerField in PassengerDirection.values()) {
            val field = getFieldInDirection(passengerField.direction, ship.position)
            if(field?.type == passengerField.type) {
                field.type = FieldType.BLOCKED
                ship.passengers += 1
                break
            }
        }
    }
    /**
     * Finds the closest ship to the goal position.
     *
     * @param ship1 the first ship to compare distance with.
     * @param ship2 the second ship to compare distance with.
     * @return the ship that is closest to the goal position, or null if no goal positions exist.
     */
    fun closestShipToGoal(ship1: Ship, ship2: Ship): Ship? {
        var closestShip: Ship? = null
        
        val goals = segments.last().gameField.flatten().filter { it.type == FieldType.GOAL }
        if (goals.isNotEmpty()) {
            val ship1Distance = goals.minOfOrNull { ship1.position.coordinate.minus(it.coordinate) }
            val ship2Distance = goals.minOfOrNull { ship2.position.coordinate.minus(it.coordinate) }
            
            if (ship1Distance != null && ship2Distance != null) {
                closestShip = if (ship1Distance <= ship2Distance) ship1 else ship2
            } else if (ship1Distance != null) {
                closestShip = ship1
            } else if (ship2Distance != null) {
                closestShip = ship2
            }
        }
        
        return closestShip
    }
    
    /**
     * Adds a segment of field elements to the specified segment list based on the given pattern and starting coordinates.
     *
     * @param segment The segment list to add the field elements to.
     * @param pattern The pattern to determine the positions of the field elements in the segment.
     * @param segmentStart The starting coordinates of the segment in the game field.
     */
    private fun addSegment(
            segment: ArrayList<ArrayList<Field>>,
            pattern: List<Pair<Int, Int>>,
            segmentStart: Coordinates,
    ) {
        val segmentPattern = Pattern.RIGHT.pattern
        for(i in pattern.indices) {
            val offsetCoordinate = Coordinates(segmentStart.x + pattern[i].first, segmentStart.y + pattern[i].second).fromDoubledHex()
            val currentPositionInSegment = Coordinates(segmentPattern[i].first, segmentPattern[i].second).fromDoubledHex()
            if(segment.size <= currentPositionInSegment.x) {
                segment.add(ArrayList())
            }
            segment[currentPositionInSegment.x].add(this.gameField[offsetCoordinate.x][offsetCoordinate.y])
        }
    }
    
    /**
     * Creates and adds a new segment to the game.
     *
     * @param seed The seed used for random number generation. Defaults to a random value.
     * @param direction The direction in which the new segment will be created. Defaults to a random direction.
     * @param segmentStart The starting coordinates for the new segment. Defaults to the appropriate coordinates for the given direction.
     * @param passengers The number of passengers in the new segment. Defaults to the constant value NUMBER_OF_PASSENGERS.
     * @param blocked The number of blocked cells in the new segment. Defaults to a random value between MIN_ISLANDS and MAX_ISLANDS.
     * @param special The number of special cells in the new segment. Defaults to a random value between MIN_SPECIAL and MAX_SPECIAL.
     * @param end Whether the new segment is the last segment in the game. Defaults to false.
     */
    private fun createSegment(
            seed: Int = Random.nextInt(),
            direction: HexDirection = getRandomSegmentDirection(),
            segmentStart: Coordinates = getSegmentStart(direction),
            passengers: Int = NUMBER_OF_PASSENGERS,
            blocked: Int = Random.nextInt(MIN_ISLANDS, MAX_ISLANDS),
            special: Int = Random.nextInt(MIN_SPECIAL, MAX_SPECIAL),
            end: Boolean,
    ) {
        val segment: ArrayList<ArrayList<Field>> = ArrayList()
        val pattern = Pattern.match(direction).pattern
        addSegment(segment, pattern, segmentStart)
        
        val newSegment = Segment(gameField = segment, seed = seed,
                lastSegment = if(segments.isNotEmpty()) segments.last() else null,
                nextSegment = null, direction = direction, blocked = blocked,
                passengers = passengers, special = special, end = end)
        segments.last().nextSegment = newSegment
        segments.add(newSegment)
    }
    
    /**
     * Returns a random segment direction based on the direction of the last segment.
     * The last segment's direction determines the list of possible directions for the random selection.
     *
     * @return A random HexDirection.
     */
    private fun getRandomSegmentDirection(): HexDirection {
        return when(segments.last().direction) {
            HexDirection.RIGHT -> {
                listOf(HexDirection.UP_RIGHT, HexDirection.DOWN_RIGHT, HexDirection.RIGHT).random()
            }
            
            HexDirection.UP_RIGHT -> {
                listOf(HexDirection.UP_RIGHT, HexDirection.UP_LEFT, HexDirection.RIGHT).random()
            }
            
            HexDirection.UP_LEFT -> {
                listOf(HexDirection.UP_RIGHT, HexDirection.UP_LEFT, HexDirection.LEFT).random()
            }
            
            HexDirection.LEFT -> {
                listOf(HexDirection.UP_LEFT, HexDirection.DOWN_LEFT, HexDirection.LEFT).random()
            }
            
            HexDirection.DOWN_LEFT -> {
                listOf(HexDirection.DOWN_LEFT, HexDirection.DOWN_RIGHT, HexDirection.LEFT).random()
            }
            
            HexDirection.DOWN_RIGHT -> {
                listOf(HexDirection.DOWN_RIGHT, HexDirection.DOWN_LEFT, HexDirection.RIGHT).random()
            }
        }
    }
    
    /**
     * Returns the starting coordinates of a segment based on the given direction.
     *
     * @param direction the direction in which the segment is located
     * @return the starting coordinates of the segment
     * @throws IllegalArgumentException if the direction is not supported
     */
    private fun getSegmentStart(direction: HexDirection): Coordinates {
        val lastSegment: Segment = segments.last()
        
        return when(direction) {
            HexDirection.RIGHT -> {
                val field: Field = lastSegment.gameField.last().first()
                field.coordinate.plus(HexDirection.RIGHT)
            }
            
            HexDirection.UP_RIGHT, HexDirection.DOWN_RIGHT -> {
                val field: Field = lastSegment.gameField.last()[round((lastSegment.gameField.last().size / 2.0)).toInt()]
                field.coordinate.plus(direction)
            }
            
            HexDirection.UP_LEFT, HexDirection.DOWN_LEFT -> {
                val field: Field = lastSegment.gameField.first()[round((lastSegment.gameField.first().size / 2.0)).toInt()]
                field.coordinate.plus(direction)
            }
            
            HexDirection.LEFT -> {
                val field: Field = lastSegment.gameField.first().first()
                field.coordinate.plus(HexDirection.LEFT)
            }
            
            else -> throw IllegalArgumentException("Direction not supported")
        }
    }
    
    companion object {
        /**
         * Initializes the game board with the specified width and height.
         *
         * @param width The width of the game board. Defaults to Constants.NUMBER_OF_SEGMENTS * (Constants.SEGMENT_FIELDS_WIDTH + 1).
         * @param height The height of the game board. Defaults to Constants.NUMBER_OF_SEGMENTS * Constants.SEGMENT_FIELDS_HEIGHT.
         * @return A 2-dimensional list representing the game board, where each element is a mutable field object.
         */
        fun initBoard(
                width: Int = Constants.NUMBER_OF_SEGMENTS * (Constants.SEGMENT_FIELDS_WIDTH + 1),
                height: Int = Constants.NUMBER_OF_SEGMENTS * Constants.SEGMENT_FIELDS_HEIGHT,
        ): List<MutableList<Field>> {
            return List(width) { i ->
                MutableList(height) { j ->
                    Field(Coordinates(i, j), FieldType.VOID, 0)
                }
            }
        }
    }
}

