package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.*
import sc.framework.PublicCloneable
import sc.framework.plugins.Constants
import sc.framework.shuffledIndices
import sc.plugin2024.util.PluginConstants
import kotlin.math.absoluteValue
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
        get() = center + (direction.vector * (segment.size / 2))
    
    /** Get Field by global coordinates. */
    operator fun get(coordinates: CubeCoordinates): Field? =
            segment[globalToLocal(coordinates)]
    
    /** Turn global into local CubeCoordinates. */
    fun globalToLocal(coordinates: CubeCoordinates) =
            (coordinates - center).rotatedBy(direction.turnCountTo(CubeDirection.RIGHT))
    
    override fun toString() = "Segment at $center to $direction ${segment.contentDeepToString()}"
    
    override fun clone(): Segment =
            copy(segment = Array(segment.size) { x ->
                Array(segment[x].size) { y -> segment[x][y].clone() }
            })
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Segment) return false
        
        if(direction != other.direction) return false
        if(center != other.center) return false
        if(!segment.contentDeepEquals(other.segment)) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = direction.hashCode()
        result = 31 * result + center.hashCode()
        result = 31 * result + segment.contentDeepHashCode()
        return result
    }
}

/**
 * Fills a segment of the game map with the provided fields
 * and optionally goal fields.
 *
 * @param end whether to place goal fields on the segment
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
    if(!fields.alignPassengers()) {
        // Fallback to new segment on impossible passenger field
        return generateSegment(end, fieldsToPlace)
    }
    return fields
}

/** Rotates all passenger fields towards water.
 * @return false if there is an impossible field */
internal fun SegmentFields.alignPassengers(random: Random = Random): Boolean {
    val fields = this
    fields.forEachIndexed { x, column ->
        column.forEachIndexed { y, field ->
            if(field is Field.PASSENGER) {
                val result = shuffledIndices(CubeDirection.values().size, random = random)
                        .mapToObj { Field.PASSENGER(CubeDirection.values()[it], 1) }
                        .filter {
                            val target = Coordinates(x, y).localToCube() + it.direction.vector
                            get(target) == Field.WATER ||
                            (target.arrayX == -2 && target.r.absoluteValue < 3) // in front of a tile is always water
                        }
                        .findFirst()
                if(result.isPresent)
                    fields[x][y] = result.get()
                else
                // Impossible passenger field
                    return false
            }
        }
    }
    return true
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
    get() = maxOf(q, -s)

fun Coordinates.localToCube(): CubeCoordinates {
    val r = y - 2
    return CubeCoordinates(x - 1 - r.coerceAtLeast(0), r)
}

/** Get a field by local cartesian coordinates. */
operator fun SegmentFields.get(x: Int, y: Int): Field? =
        this.getOrNull(x)?.getOrNull(y)

/** Get a field by CubeCoordinates RELATIVE to segment center, if it exists. */
operator fun SegmentFields.get(coordinates: CubeCoordinates): Field? =
        this[coordinates.arrayX + 1, coordinates.r + 2]