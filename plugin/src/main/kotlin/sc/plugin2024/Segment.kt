package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.*
import sc.framework.PublicCloneable
import sc.framework.shuffledIndices
import sc.plugin2024.util.PluginConstants
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextInt

/** Represents a segment of the board as a list of columns. */
typealias SegmentFields = Array<Array<FieldType>>

typealias Segments = List<Segment>

@XStreamAlias("segment")
class Segment(
        @XStreamAsAttribute val direction: HexDirection,
        @XStreamOmitField val center: Coordinates,
        @XStreamImplicit val segment: SegmentFields,
): PublicCloneable<Segment> {
    override fun clone(): Segment = Segment(direction, center, segment.clone())
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
                shuffledIndices(HexDirection.values().size)
                        .takeWhile {
                            if(fields[x + (field.direction.deltaX + 1) / 2][y + field.direction.dy] == FieldType.WATER)
                                return@takeWhile false
                            fields[x][y] = FieldType.PASSENGER(HexDirection.values()[it], 1)
                            return@takeWhile true
                        }
                if(fields[x + field.direction.deltaX][y + field.direction.dy] != FieldType.WATER)
                    return generateSegment(end, fieldsToPlace)
            }
        }
    }
    return fields
}

internal fun generateBoard(): Segments {
    val segments = ArrayList<Segment>(PluginConstants.NUMBER_OF_SEGMENTS)
    segments.add(Segment(
            HexDirection.RIGHT,
            Coordinates.ORIGIN,
            generateSegment(false, arrayOf())
    ))
    
    val passengerTiles = shuffledIndices(PluginConstants.NUMBER_OF_SEGMENTS - 2, PluginConstants.NUMBER_OF_PASSENGERS).toArray()
    (2..PluginConstants.NUMBER_OF_SEGMENTS).forEach {
        val previous = segments.last()
        val direction = if(it == 2) HexDirection.RIGHT else previous.direction.withNeighbors().random()
        segments.add(Segment(
                direction,
                previous.center + (direction.vector * 4),
                generateSegment(it == PluginConstants.NUMBER_OF_SEGMENTS,
                        Array<FieldType>(Random.nextInt(PluginConstants.MIN_ISLANDS..PluginConstants.MAX_ISLANDS)) { FieldType.BLOCKED } +
                        Array<FieldType>(Random.nextInt(PluginConstants.MIN_SPECIAL..PluginConstants.MAX_SPECIAL)) { FieldType.SANDBANK } +
                        Array<FieldType>(if(passengerTiles.contains(it - 2)) 1 else 0) { FieldType.PASSENGER(HexDirection.random()) }
                )
        ))
    }
    return segments
}

private val IVector.deltaX: Int
    get() = (dx + abs(dy)) / 2

operator fun SegmentFields.get(x: Int, y: Int): FieldType = this[x][y]

operator fun SegmentFields.get(coordinates: Coordinates): FieldType = this[coordinates.x][coordinates.y]