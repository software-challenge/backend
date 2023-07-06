package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import sc.api.plugins.IField
import sc.plugin2024.util.FieldConverter

/**
 * Represents a field on a game board.
 *
 * @property type The type of the field.
 * @property x The x-coordinate of the field.
 * @property y The y-coordinate of the field.
 * @property points The number of points assigned to the field.
 */
@XStreamConverter(FieldConverter::class)
@XStreamAlias("field")
data class Field(var type: FieldType, val x: Int, val y: Int, val points: Int) : IField<Field> {
    override val isEmpty: Boolean
        get() = type == FieldType.WATER
    override val isOccupied: Boolean
        get() = type == FieldType.BLOCKED

    /**
     * Retrieves the field on the game board that is located in the given direction.
     *
     * @param direction The given direction.
     * @param board The game board.
     *
     * @return The field in the specified direction.
     */
    fun getFieldInDirection(direction: Direction?, board: Board?): Field? {
        return _getFieldInDirection(direction!!, board!!, true)
    }

    /**
     * Returns the field in the specified direction from the current field.
     *
     * @param direction The direction to move to.
     * @param board The board on which the field exists.
     * @param onlyVisible Specifies whether to only return visible fields.
     * @return The field in the specified direction, or null if the field does not exist.
     */
    private fun _getFieldInDirection(direction: Direction, board: Board, onlyVisible: Boolean): Field? {
        val targetX: Int
        val targetY: Int
        val onEvenRow = y % 2 == 0
        when (direction) {
            Direction.RIGHT -> {
                targetX = x + 1
                targetY = y
            }

            Direction.UP_RIGHT -> {
                targetX = if (onEvenRow) x + 1 else x
                targetY = y - 1
            }

            Direction.UP_LEFT -> {
                targetX = if (onEvenRow) x else x - 1
                targetY = y - 1
            }

            Direction.LEFT -> {
                targetX = x - 1
                targetY = y
            }

            Direction.DOWN_LEFT -> {
                targetX = if (onEvenRow) x else x - 1
                targetY = y + 1
            }

            Direction.DOWN_RIGHT -> {
                targetX = if (onEvenRow) x + 1 else x
                targetY = y + 1
            }

        }
        return if (onlyVisible) {
            board.getField(targetX, targetY)
        } else {
            board.alwaysGetField(targetX, targetY)
        }
    }
    
    override fun clone(): Field = Field(type, x, y, points)
    
    override fun toString(): String = "Field($type, $x, $y, $points)"
}
