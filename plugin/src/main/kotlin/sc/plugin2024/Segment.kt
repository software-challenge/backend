package sc.plugin2024

import sc.api.plugins.HexDirection
import sc.api.plugins.TwoDBoard
import sc.plugin2024.util.PluginConstants
import kotlin.random.Random

/**
 * Represents a segment of the board.
 *
 * @property seed The seed used for generating random values.
 * @property lastSegment The previous segment connected to this segment.
 * @property nextSegment The next segment connected to this segment.
 * @property direction The direction in which the segment is facing.
 * @property passengers The number of passengers placed in the segment.
 * @property blocked The number of blocked fields placed in the segment.
 * @property special The number of special fields placed in the segment.
 * @property end Indicates whether a goal fields is placed in the segment.
 * @property gameField The two-dimensional board representing the game map.
 */
class Segment(
        val seed: Int = Random.nextInt(),
        val lastSegment: Segment?,
        val nextSegment: Segment?,
        val direction: HexDirection,
        val passengers: Int = PluginConstants.NUMBER_OF_PASSENGERS,
        val blocked: Int = Random.nextInt(PluginConstants.MIN_ISLANDS, PluginConstants.MAX_ISLANDS),
        val special: Int = Random.nextInt(PluginConstants.MIN_SPECIAL, PluginConstants.MAX_SPECIAL),
        val end: Boolean,
        gameField: TwoDBoard<Field>,
): Board(gameField) {
    
    init {
        fillSegment(passengers, blocked, special, end)
    }
    
    /**
     * Returns the field in the given direction from the specified field.
     *
     * @param direction the direction in which to search for the field
     * @param field the field from which to start the search
     * @return the field in the given direction, or null if it is out of bounds
     */
    override fun getFieldInDirection(direction: HexDirection, field: Field): Field? {
        val coordinateInDirection = field.coordinate.plus(direction)
        return if(coordinateInDirection.x in gameField.indices && coordinateInDirection.y in 0 until gameField[coordinateInDirection.x].size) {
            gameField[coordinateInDirection.x][coordinateInDirection.y]
        } else {
            null
        }
    }
    
    /**
     * Returns a random Field from the gameField.
     *
     * @return a random Field from the gameField.
     */
    private fun getRandomField(): Field {
        val rand = Random(seed) // Don't place it on the last column
        val x = rand.nextInt(gameField.size - 1)
        val y = rand.nextInt(gameField[0].size)
        return gameField[x][y]
    }
    
    /**
     * Platziert eine bestimmte Anzahl von blockierten Feldern zufällig auf dem Spielfeld.
     *
     * @param blocked die Anzahl der blockierten Felder, die auf dem Spielfeld platziert werden sollen
     */
    private fun placeBlocked(blocked: Int) {
        var localBlocked = blocked
        while(localBlocked != 0) {
            val field = getRandomField()
            if(field.type == FieldType.WATER) {
                field.type = FieldType.BLOCKED
                --localBlocked
            }
        }
    }
    
    /**
     * Places a specified number of special items on the game field.
     *
     * @param special the number of special items to place
     */
    private fun placeSpecial(special: Int) {
        var localSpecial = special
        while(localSpecial != 0) {
            val field = getRandomField()
            if(field.type == FieldType.WATER) {
                field.type = when(Random.nextInt(2)) {
                    0 -> FieldType.SANDBANK
                    else -> FieldType.LOG
                }
                --localSpecial
            }
        }
    }
    
    /**
     * Places the given number of passengers randomly on the game field.
     *
     * @param passengers the number of passengers to place
     */
    private fun placePassengers(passengers: Int) {
        var localPassengers = passengers
        var field: Field
        var passengerDirection: HexDirection
        var dock: Field?
        
        while(localPassengers != 0) {
            do {
                field = getRandomField()
                passengerDirection = HexDirection.random()
                dock = getFieldInDirection(passengerDirection, field)
            } while(field.type !== FieldType.WATER || dock == null || dock.type !== FieldType.WATER)
            
            val passenger: FieldType = when(passengerDirection) {
                HexDirection.RIGHT -> FieldType.PASSENGER0
                HexDirection.UP_RIGHT -> FieldType.PASSENGER1
                HexDirection.UP_LEFT -> FieldType.PASSENGER2
                HexDirection.LEFT -> FieldType.PASSENGER3
                HexDirection.DOWN_LEFT -> FieldType.PASSENGER4
                else -> FieldType.PASSENGER5
            }
            field.type = passenger
            --localPassengers
        }
    }
    
    /**
     * Places a goal in the game field at the most right column.
     * The goal is placed at the middle row of the most right column.
     *
     * The goal is represented by the [FieldType].GOAL enum value.
     *
     * @see FieldType
     */
    private fun placeGoal() {
        val mostRightColumnIndex = gameField.size - 1
        val columnSize = gameField[mostRightColumnIndex].size
        val middleIndex = columnSize / 2
        
        val fields = listOf(middleIndex - 1, middleIndex, middleIndex + 1).filter {
            it in gameField[mostRightColumnIndex].indices
        }
        
        fields.forEach { gameField[mostRightColumnIndex][it].type = FieldType.GOAL }
    }
    
    /**
     * Fills a segment of the game map with specified number of passengers, blocked islands, special islands, and a goal island.
     *
     * @param passengers the number of passengers to place in the segment. Default value is PluginConstants.NUMBER_OF_PASSENGERS.
     * @param blocked the number of blocked islands to place in the segment. Default value is a random value between PluginConstants.MIN_ISLANDS and PluginConstants.MAX_ISLANDS (inclusive).
     * @param special the number of special islands to place in the segment. Default value is a random value between PluginConstants.MIN_SPECIAL and PluginConstants.MAX_SPECIAL (inclusive).
     * @param end a flag indicating whether to place a goal island in the segment. True if a goal island should be placed, false otherwise.
     */
    private fun fillSegment(
            passengers: Int = PluginConstants.NUMBER_OF_PASSENGERS,
            blocked: Int = Random.nextInt(PluginConstants.MIN_ISLANDS, PluginConstants.MAX_ISLANDS),
            special: Int = Random.nextInt(PluginConstants.MIN_SPECIAL, PluginConstants.MAX_SPECIAL),
            end: Boolean,
    ) {
        placeBlocked(blocked)
        placeSpecial(special)
        placePassengers(passengers)
        if(end) placeGoal()
        
    }
    
}