package sc.plugin2024

import sc.api.plugins.*
import sc.plugin2024.util.PluginConstants
import java.util.stream.IntStream
import kotlin.math.abs
import kotlin.random.Random

private typealias SegmentFields = Array<Array<FieldType>>

fun shuffledIndices(max: Int, limit: Int = max): IntStream =
        IntStream.generate { Random.nextInt(max) }
                .distinct()
                .limit(limit.toLong())

/** Represents a segment of the board as a list of columns. */
class Segment(
        val fields: SegmentFields
): FieldMap<FieldType>() {
    
    companion object {
        /**
         * Fills a segment of the game map with a specified number of passengers,
         * blocked islands, special islands, and a goal island.
         *
         * @param end a flag indicating whether to place a goal island in the segment.
         * True if a goal island should be placed, false otherwise.
         */
        fun generate(
                end: Boolean,
                fieldsToPlace: Array<FieldType>
        ): Segment {
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
                            return generate(end, fieldsToPlace)
                    }
                }
            }
            return Segment(fields)
        }
        
        private val IVector.deltaX: Int
            get() = (dx + abs(dy)) / 2
    }
    
    override operator fun get(x: Int, y: Int): FieldType = fields[x][y]
    
    override val entries: Set<Map.Entry<Coordinates, FieldType>>
        get() = fields.flatMapIndexedTo(HashSet()) { x, row ->
            row.mapIndexed { y, field ->
                FieldPosition(Coordinates(x, y), field)
            }
        }
    
}