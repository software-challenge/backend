package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import java.util.*
import sc.plugin2024.util.PluginConstants as Constants

@XStreamAlias(value = "tile")
class Tile {
    var fields: List<Field> = ArrayList()

    @XStreamAsAttribute
    var isVisible = false
        private set

    /**
     * Index des Spielsegments
     */
    @XStreamAsAttribute
    var index = 0
        private set

    /**
     * Richtung, in die das Spielsegment zeigt
     */
    @XStreamAsAttribute
    var direction = 0
        private set

    /**
     * Nur fuer den Server relevant
     * generates a new tile
     * @param index index of tile
     * @param direction direction of tile
     * @param x x coordinate of middle
     * @param y y coordinate of middle
     * @param passengers passengers on tile
     * @param blockedFields blocked fields in tile
     * @param specialFields special fields (log, sandbar) on tile
     */
    constructor(
        index: Int,
        direction: Int,
        x: Int,
        y: Int,
        passengers: Int,
        blockedFields: Int,
        specialFields: Int
    ) {
        this.index = index
        this.direction = direction
        isVisible = index < 2 // at the beginning only the first 2 Tiles are visible
        generateFields(x, y, passengers, blockedFields, specialFields, index == Constants.NUMBER_OF_TILES - 1)
    }

    protected constructor(fields: List<Field>) {
        this.fields = fields
    }

    // TODO: idk tbh if this works...
    private fun generateFields(x: Int, y: Int, passengers: Int, blocked: Int, special: Int, end: Boolean) {
        fields = ArrayList()
        val offsets = arrayOf(
            intArrayOf(-2, 2),
            intArrayOf(-2, -2),
            intArrayOf(-1, 2),
            intArrayOf(-1, 1),
            intArrayOf(-1, 0),
            intArrayOf(-1, -1),
            intArrayOf(-1, -2),
            intArrayOf(0, 2),
            intArrayOf(0, 1),
            intArrayOf(0, 0),
            intArrayOf(0, -1),
            intArrayOf(0, -2),
            intArrayOf(1, 1),
            intArrayOf(1, 0),
            intArrayOf(1, -1)
        )
        val lastFiveOffsets =
            arrayOf(intArrayOf(1, -2), intArrayOf(1, 2), intArrayOf(2, 1), intArrayOf(2, 0), intArrayOf(2, -1))
        val direction = direction % 6
        for (i in offsets.indices) {
            val xOffset = offsets[i][0]
            val yOffset = offsets[i][1]
            if (i >= offsets.size - 5 && !end) {
                (fields as ArrayList<Field>).add(Field(FieldType.WATER, x + xOffset, y + yOffset, 4))
            } else {
                (fields as ArrayList<Field>).add(Field(FieldType.WATER, x + xOffset, y + yOffset, i / 5))
            }
        }
        val lastDirectionOffsets = lastFiveOffsets[direction]
        for (offset in lastFiveOffsets) {
            (fields as ArrayList<Field>).add(
                Field(
                    if (end) FieldType.GOAL else FieldType.WATER,
                    x + offset[0],
                    y + offset[1],
                    if (offset.contentEquals(lastDirectionOffsets)) 4 else direction
                )
            )
        }
        placeBlocked(blocked)
        placeSpecial(special)
        placePassengers(passengers)
    }


    private fun placeBlocked(blocked: Int) {
        var blocked = blocked
        val rnd = Random()
        while (blocked != 0) {
            var random: Int = rnd.nextInt(fields.size - 5) // may not be played on the last 5 fields
            while (fields[random].type !== FieldType.WATER) {
                random = rnd.nextInt(fields.size - 5)
            }
            fields[random].type = FieldType.BLOCKED
            --blocked
        }
    }

    private fun placeSpecial(special: Int) {
        var special = special
        val rnd = Random()
        while (special != 0) {
            var random: Int = rnd.nextInt(fields.size - 5) // may not be played on the last 5 fields
            while (fields[random].type !== FieldType.WATER) {
                random = rnd.nextInt(fields.size - 5)
            }
            val sandbar: Int = rnd.nextInt(2)
            fields[random].type = if (sandbar == 1) FieldType.SANDBANK else FieldType.LOG
            --special
        }
    }

    private fun placePassengers(passengers: Int) {
        var passengers = passengers
        val rnd = Random()
        while (passengers != 0) {
            var random: Int = rnd.nextInt(fields.size - 5) // may not be played on the last 5 fields
            var passengerDirection: Int = rnd.nextInt(6)
            var start: Field = fields[random]
            var dock: Field? = getFieldInDirection(passengerDirection, start)
            while ((start.type !== FieldType.WATER) || (dock == null) || (dock.type !== FieldType.WATER)) {
                random = rnd.nextInt(fields.size - 5)
                passengerDirection = rnd.nextInt(6)
                start = fields[random]
                dock = getFieldInDirection(passengerDirection, start)
            }
            val passenger: FieldType = when (passengerDirection) {
                0 -> FieldType.PASSENGER0
                1 -> FieldType.PASSENGER1
                2 -> FieldType.PASSENGER2
                3 -> FieldType.PASSENGER3
                4 -> FieldType.PASSENGER4
                else -> FieldType.PASSENGER5
            }
            fields[random].type = passenger
            --passengers
        }
    }

    fun getField(x: Int, y: Int): Field? {
        for (field in fields) {
            if (field.x == x && field.y == y) {
                return field
            }
        }
        return null
    }

    fun setVisibility(visible: Boolean) {
        isVisible = visible
    }

    @Override
    fun clone(): Tile {
        val clonedFields: ArrayList<Field> = ArrayList<Field>()
        for (field in fields) {
            val clonedField: Field = field.clone()
            clonedFields.add(clonedField)
        }
        val clone = Tile(clonedFields)
        clone.direction = direction
        clone.isVisible = isVisible
        clone.index = index
        return clone
    }

    @Override
    override fun equals(other: Any?): Boolean {
        return if (other is Tile) {
            val tile = other as Tile
            val fields1: List<Field> = tile.fields.sortedBy { it.points }
            val fields2: List<Field> = fields.sortedBy { it.points }
            if (fields1.size != fields2.size) {
                return false
            }
            for (i in fields1.indices) {
                if (fields1[i] != fields2[i]) {
                    return false
                }
            }
            true
        } else {
            false
        }
    }

    /**
     * Gibt das Feld in eine bestimmte Richtung zurück. Gibt null zurück, falls sich das Feld nicht auf diesem Tile befindet.
     * @param direction Richtung
     * @param field Startfeld
     * @return Feld in Richtung
     */
    fun getFieldInDirection(direction: Int, field: Field): Field? {
        val x: Int = field.x
        val y: Int = field.y
        when (direction) {
            0 -> return getField(x + 1, y)
            1 -> return getField(if (y % 2 == 0) x + 1 else x, y - 1)
            2 -> return getField(if (y % 2 == 0) x else x - 1, y - 1)
            3 -> return getField(x - 1, y)
            4 -> return getField(if (y % 2 == 0) x else x - 1, y + 1)
            5 -> return getField(if (y % 2 == 0) x + 1 else x, y + 1)
            else -> {}
        }
        return null
    }

    @Override
    override fun toString(): String {
        var toString = "Tile $index Richtung $direction visible $isVisible"
        for (field in fields) {
            toString += """
                
                $field
                """.trimIndent()
        }
        return toString
    }

    override fun hashCode(): Int {
        var result = fields.hashCode()
        result = 31 * result + isVisible.hashCode()
        result = 31 * result + index
        result = 31 * result + direction
        return result
    }
}
