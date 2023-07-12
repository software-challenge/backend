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
typealias SegmentFields = Array<Array<FieldType>>

typealias Segments = List<Segment>

@XStreamAlias("segment")
class Segment(
        @XStreamAsAttribute val direction: CubeDirection,
        @XStreamOmitField val center: CubeCoordinates,
        @XStreamImplicit val segment: SegmentFields,
): PublicCloneable<Segment> {
    override fun clone(): Segment = Segment(direction, center, segment.clone()) // FIXME deepCopy<FieldType>())
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
        fieldsToPlace: Array<FieldType>,
): SegmentFields {
    val fields: SegmentFields = Array(PluginConstants.SEGMENT_FIELDS_HEIGHT) { Array(PluginConstants.SEGMENT_FIELDS_WIDTH) { FieldType.WATER } }
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
        lastColumn.mapIndexed { index, fieldType ->
            assert(fieldType == FieldType.WATER)
            if(index == 0 || index == lastColumn.lastIndex) {
                fieldType
            } else {
                FieldType.GOAL
            }
        }
    }
    fields.forEachIndexed { x, fieldTypes ->
        fieldTypes.forEachIndexed { y, field ->
            if(field is FieldType.PASSENGER) {
                // Rotate Passenger fields to water
                shuffledIndices(CubeDirection.values().size)
                        .takeWhile {
                            if(fields[x + field.direction.vector.arrayX][y + field.direction.vector.r] == FieldType.WATER)
                                return@takeWhile false
                            fields[x][y] = FieldType.PASSENGER(CubeDirection.values()[it], 1)
                            return@takeWhile true
                        }
                // Fallback to new segment on impossible passenger field
                if(fields[x + field.direction.vector.arrayX][y + field.direction.vector.r] != FieldType.WATER)
                    return generateSegment(end, fieldsToPlace)
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
                        Array<FieldType>(Random.nextInt(PluginConstants.MIN_ISLANDS..PluginConstants.MAX_ISLANDS)) { FieldType.BLOCKED } +
                        Array<FieldType>(Random.nextInt(PluginConstants.MIN_SPECIAL..PluginConstants.MAX_SPECIAL)) { FieldType.SANDBANK } +
                        Array<FieldType>(if(passengerTiles.contains(it - 2)) 1 else 0) { FieldType.PASSENGER(CubeDirection.random()) }
                )
        ))
    }
    return segments
}

val CubeCoordinates.arrayX: Int
    get() = min(q, s) + 1

operator fun SegmentFields.get(x: Int, y: Int): FieldType = this[x][y]

/** Get a field by RELATIVE CubeCoordinates if it exists. */
operator fun SegmentFields.get(coordinates: CubeCoordinates): FieldType? = this.getOrNull(coordinates.arrayX)?.getOrNull(coordinates.r)