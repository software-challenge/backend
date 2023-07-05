package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import sc.api.plugins.IField
import sc.plugin2024.util.FieldConverter

@XStreamConverter(FieldConverter::class)
@XStreamAlias("field")
data class Field(var type: FieldType, val x: Int, val y: Int, val points: Int) : IField<Field> {
    override val isEmpty: Boolean
        get() = type == FieldType.WATER
    override val isOccupied: Boolean
        get() = type == FieldType.BLOCKED

    /**
     * Gibt das Feld zurück, welches auf dem Spielbrett in der gegebenen Richtung liegt
     * @param direction die gegebene Richtung
     * @param board das Spielbrett
     * @return das Feld in der Richtung
     */
    fun getFieldInDirection(direction: Direction?, board: Board?): Field? {
        return _getFieldInDirection(direction!!, board!!, true)
    }

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
