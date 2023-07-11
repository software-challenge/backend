package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.*
import sc.plugin2024.util.PluginConstants
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Erzeugt ein neues Spielfeld anhand der gegebenen Segmente
 * @param segments Spielsegmente des neuen Spielfelds
 */
@XStreamAlias(value = "board")
data class Board(
        @XStreamOmitField
        private val segments: Segments = generateBoard(),
        @XStreamOmitField
        internal var visibleSegments: Int = 2,
): FieldMap<FieldType>(), IBoard {
    
    override fun clone(): Board = Board(this.segments.clone(), visibleSegments)
    
    // TODO direction of segment beyond visible one, set with visibleSegments
    @XStreamAsAttribute
    var nextDirection: HexDirection? = null
    
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
     * Calculates the distance between two fields in the number of segments.
     *
     * @param field1 The first field to calculate distance from.
     * @param field2 The second field to calculate distance from.
     * @return The distance between the given fields in the segment. If any of the fields is not found in
     *         any segment, -1 is returned.
     */
    fun segmentDistance(field1: Field, field2: Field): Int {
        val field1Index = segments.indexOfFirst { segment ->
            segment.fields.any { row -> row.contains(field1) }
        }
        val field2Index = segments.indexOfFirst { segment ->
            segment.fields.any { row -> row.contains(field2) }
        }
        
        return if(field1Index == -1 || field2Index == -1) {
            -1 // return -1 if any of the fields is not found in any segment
        } else {
            abs(field1Index - field2Index) // return distance
        }
    }
    
    /**
     * Methode zur Abholung eines Passagiers auf einem [Ship].
     *
     * @param ship Das [Ship], mit dem der Passagier abgeholt wird.
     * @return `true`, wenn ein Passagier erfolgreich abgeholt wurde, sonst `false`.
     */
    fun pickupPassenger(ship: Ship): Boolean {
        val neighboringFields = ship.position.coordinate.hexNeighbors.map { coordinates ->
            get(coordinates.x, coordinates.y)
        }
        
        neighboringFields.forEach { field ->
            if (field is FieldType.PASSENGER && field.passenger > 0) {
                field.passenger--
                ship.passengers++
                
                return@pickupPassenger true
            }
        }
        
        return false
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
        
        val goals = segments.last().fields.flatten().filter { it.type == FieldType.GOAL }
        if(goals.isNotEmpty()) {
            val ship1Distance = goals.minOfOrNull { ship1.position.coordinate.minus(it.coordinate) }
            val ship2Distance = goals.minOfOrNull { ship2.position.coordinate.minus(it.coordinate) }
            
            if(ship1Distance != null && ship2Distance != null) {
                closestShip = if(ship1Distance <= ship2Distance) ship1 else ship2
            } else if(ship1Distance != null) {
                closestShip = ship1
            } else if(ship2Distance != null) {
                closestShip = ship2
            }
        }
        
        return closestShip
    }
    
    /** Get the field at the given doubled Hex Coordinate. */
    override fun get(x: Int, y: Int): FieldType {
        val segment = (x + 2) / (PluginConstants.SEGMENT_FIELDS_WIDTH * 2)
        // TODO assumes linear board
        return segments[segment][(x - segment * 2 + abs(y)) / 2, y]
    }
    
    override val entries: Set<Map.Entry<Coordinates, FieldType>>
        get() = TODO()
}

