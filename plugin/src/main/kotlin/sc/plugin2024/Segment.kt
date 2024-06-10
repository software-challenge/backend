package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.Coordinates
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.deepCopy
import sc.framework.PublicCloneable
import sc.framework.shuffledIndices
import sc.plugin2024.util.PluginConstants
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.random.nextInt

/** Represents a segment of the board as a list of columns. */
typealias SegmentFields = Array<Array<Field>>

typealias Segments = List<Segment>

/** Corner coordinates, using offset system.
 * @return ((min-x, max-x), (min-y, max-y)) */
val Segments.bounds
    get() = fold(Pair(99 to -99, 99 to -99)) { acc, segment ->
        val center = segment.center
        val x = center.x / 2
        Pair(acc.first.first.coerceAtMost(x - 2) to acc.first.second.coerceAtLeast(x + 2),
                acc.second.first.coerceAtMost(center.r - 2) to acc.second.second.coerceAtLeast(center.r + 2))
    }

/** Size of the rectangle surrounding all segments. */
val Segments.rectangleSize: Coordinates
    get() = bounds.let { Coordinates(it.first.second - it.first.first + 1, it.second.second - it.second.first + 1) }

@XStreamAlias("segment")
data class Segment(
        @XStreamAsAttribute val direction: CubeDirection,
        // could be omitted but helpful since ships also come with cubecoords @XStreamOmitField
        val center: CubeCoordinates,
        @XStreamImplicit/*(itemFieldName = "column")*/ val fields: SegmentFields,
): PublicCloneable<Segment> {
    
    val tip: CubeCoordinates
        get() = center + (direction.vector * (fields.size / 2))
    
    /** Iterate over each field paired with its GLOBAL coordinates. */
    fun forEachField(handler: (CubeCoordinates, Field) -> Unit) =
            fields.forEachField { coordinates, field -> handler(localToGlobal(coordinates), field) }
    
    /** Get Field by global coordinates. */
    operator fun get(coordinates: CubeCoordinates): Field? =
            fields[globalToLocal(coordinates)]
    
    fun localToGlobal(coordinates: Coordinates): CubeCoordinates =
            coordinates
                    .localToCube()
                    .rotatedBy(CubeDirection.RIGHT.turnCountTo(direction))
                    .plus(center)
    
    /** Turn global into local CubeCoordinates. */
    fun globalToLocal(coordinates: CubeCoordinates): CubeCoordinates =
            (coordinates - center).rotatedBy(direction.turnCountTo(CubeDirection.RIGHT))
    
    override fun toString() =
            "Segment at $center to $direction\n" + fields.first().mapIndexed { y, _ ->
                fields.mapIndexed { x, column ->
                    val cubeCoordinates = localToGlobal(Coordinates(x, y))
                    "${column[y].letter} (${cubeCoordinates.q}, ${cubeCoordinates.r})"
                }.joinToString("|")
            }.joinToString("\n")
    
    override fun clone(): Segment = copy(fields = fields.deepCopy())
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Segment) return false
        
        if(direction != other.direction) return false
        if(center != other.center) return false
        if(!fields.contentDeepEquals(other.fields)) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = direction.hashCode()
        result = 31 * result + center.hashCode()
        result = 31 * result + fields.contentDeepHashCode()
        return result
    }
    
    companion object {
        fun inDirection(previousCenter: CubeCoordinates, direction: CubeDirection, fields: SegmentFields) =
                Segment(direction, previousCenter + direction.vector * PluginConstants.SEGMENT_FIELDS_WIDTH, fields)
        
        fun empty(center: CubeCoordinates = CubeCoordinates.ORIGIN, direction: CubeDirection = CubeDirection.RIGHT) =
                Segment(direction, center, generateSegment(false, arrayOf()))
    }
}

internal fun SegmentFields.forEachField(handler: (Coordinates, Field) -> Unit) =
        this.forEachIndexed { x, column ->
            column.forEachIndexed { y, field ->
                handler(Coordinates(x, y), field)
            }
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
                            // this rotation is relative!
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
    // TODO currently we place other stuff on the last segment, but should we?
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

internal fun generateBoard(): Segments {
    val segments = ArrayList<Segment>(PluginConstants.NUMBER_OF_SEGMENTS)
    segments.add(Segment(
            CubeDirection.RIGHT,
            CubeCoordinates.ORIGIN,
            generateSegment(false, arrayOf())
    ))
    
    val passengerTiles = shuffledIndices(PluginConstants.NUMBER_OF_SEGMENTS - 2, PluginConstants.NUMBER_OF_PASSENGERS).toArray()
    (2..PluginConstants.NUMBER_OF_SEGMENTS).forEach { index ->
        val previous = segments.last()
        val direction = if(index == 2) CubeDirection.RIGHT else previous.direction.withNeighbors().filter { it != segments.takeLast(3).first().direction.opposite() }.random() // Do not allow three consecutive turns in one direction to prevent clashes
        
        val segment =
                generateSegment(index == PluginConstants.NUMBER_OF_SEGMENTS,
                        Array<Field>(Random.nextInt(PluginConstants.MIN_ISLANDS..PluginConstants.MAX_ISLANDS)) { Field.ISLAND } +
                        Array<Field>(Random.nextInt(PluginConstants.MIN_SPECIAL..PluginConstants.MAX_SPECIAL)) { Field.SANDBANK } +
                        Array<Field>(if(passengerTiles.contains(index - 2)) 1 else 0) { Field.PASSENGER() }
                )
        segment.forEachField { c, f ->
            // Turn local passenger field rotation into global
            if(f is Field.PASSENGER) {
                segment[c.x][c.y] = f.copy(f.direction.rotatedBy(CubeDirection.RIGHT.turnCountTo(direction)))
            }
        }
        segments.add(Segment.inDirection(previous.center, direction, segment))
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