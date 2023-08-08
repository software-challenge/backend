package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.*
import sc.framework.PublicCloneable
import sc.framework.shuffledIndices
import sc.plugin2024.util.PluginConstants
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

/** Represents a segment of the board as a list of columns. */
typealias SegmentFields = Array<Array<Field>>

typealias Segments = List<Segment>

@XStreamAlias("segment")
data class Segment(
        @XStreamAsAttribute val direction: CubeDirection,
        @XStreamOmitField val center: CubeCoordinates,
        @XStreamImplicit @XStreamAlias("segment") val segment: SegmentFields,
): PublicCloneable<Segment> {
    val tip: CubeCoordinates
        get() = center + (direction.vector * 2)
    
    /** Get Field by global coordinates. */
    operator fun get(coordinates: CubeCoordinates): Field? =
        segment[(coordinates - center).rotatedBy(direction.turnCountTo(CubeDirection.RIGHT))]
    
    override fun clone(): Segment = copy(segment = segment.clone()) // FIXME deepCopy<FieldType>())
}

/**
 * Fills a segment of the game map with a specified number of passengers,
 * blocked islands, special islands, and a goal island.
 *
 * @param end a flag indicating whether to place a goal island in the segment.
 * True if a goal island should be placed, false otherwise.
 */
internal fun generateSegment(
        end: Boolean,
        fieldsToPlace: Array<Field>,
): SegmentFields {
    val fields: SegmentFields = Array(PluginConstants.SEGMENT_FIELDS_WIDTH) { Array(PluginConstants.SEGMENT_FIELDS_HEIGHT) { Field.WATER } }
    val columnsButLast = fields.size - 1
    
    var currentField = 0
    shuffledIndices(columnsButLast * fields.first().size, fieldsToPlace.size)
            .forEach {
                fields[it.mod(columnsButLast)][it.div(columnsButLast)] = fieldsToPlace[currentField++]
            }
    // TODO are we allowed to place other stuff on the last segment?
    if(end) {
        // Place Goal fields in the last column, except for top and bottom row
        val lastColumn = fields.last()
        fields[fields.lastIndex] =
                lastColumn.mapIndexed { index, fieldType ->
                    assert(fieldType == Field.WATER)
                    if(index == 0 || index == lastColumn.lastIndex) {
                        fieldType
                    } else {
                        Field.GOAL
                    }
                }.toTypedArray()
    }
    fields.forEachIndexed { x, fieldTypes ->
        fieldTypes.forEachIndexed { y, field ->
            if(field is Field.PASSENGER) {
                // Rotate Passenger fields to water
                // TODO I am not entirely sure what happened here,
                //  but before it always went straight to the fallback
                //  This *seems* to work, but not tested
                val neighborFields = field.direction.withNeighbors().mapNotNull {
                    val i = x + it.vector.arrayX
                    val j = y + it.vector.r
                    
                    if(i in fields.indices && j in fields[i].indices) fields[i][j] else null
                }.toList()
                
                neighborFields.firstOrNull { it == Field.WATER }?.let { waterNeighbor ->
                    fields[x][y] = Field.PASSENGER(field.direction.withNeighbors()[neighborFields.indexOf(waterNeighbor)])
                } ?: run {
                    // Fallback to new segment on impossible passenger field
                    return generateSegment(end, fieldsToPlace)
                }
            }
        }
    }
    return fields
}

internal fun generateBoard(): Segments {
    val segments = ArrayList<Segment>(PluginConstants.NUMBER_OF_SEGMENTS)
    segments.add(Segment(
            CubeDirection.RIGHT,
            CubeCoordinates.ORIGIN,
            generateSegment(false, arrayOf())
    ))
    
    val passengerTiles = shuffledIndices(PluginConstants.NUMBER_OF_SEGMENTS - 2, PluginConstants.NUMBER_OF_PASSENGERS).toArray()
    (2..PluginConstants.NUMBER_OF_SEGMENTS).forEach {
        val previous = segments.last()
        val direction = if(it == 2) CubeDirection.RIGHT else previous.direction.withNeighbors().random()
        segments.add(Segment(
                direction,
                previous.center + (direction.vector * 4),
                generateSegment(it == PluginConstants.NUMBER_OF_SEGMENTS,
                        Array<Field>(Random.nextInt(PluginConstants.MIN_ISLANDS..PluginConstants.MAX_ISLANDS)) { Field.BLOCKED } +
                        Array<Field>(Random.nextInt(PluginConstants.MIN_SPECIAL..PluginConstants.MAX_SPECIAL)) { Field.SANDBANK } +
                        Array<Field>(if(passengerTiles.contains(it - 2)) 1 else 0) { Field.PASSENGER(CubeDirection.random()) }
                )
        ))
    }
    return segments
}

val CubeCoordinates.arrayX: Int
    get() = maxOf(q, -s) + 1

/** Get a field by local cartesian coordinates. */
operator fun SegmentFields.get(x: Int, y: Int): Field = this[x][y]

/** Get a field by RELATIVE CubeCoordinates if it exists. */
operator fun SegmentFields.get(coordinates: CubeCoordinates): Field? =
        this.getOrNull(coordinates.arrayX)?.getOrNull(coordinates.r + 2)