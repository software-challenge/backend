package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.HexDirection
import sc.api.plugins.RectangularBoard
import sc.api.plugins.TwoDBoard
import sc.plugin2024.util.PluginConstants.MAX_ISLANDS
import sc.plugin2024.util.PluginConstants.MAX_SPECIAL
import sc.plugin2024.util.PluginConstants.MIN_ISLANDS
import sc.plugin2024.util.PluginConstants.MIN_SPECIAL
import sc.plugin2024.util.PluginConstants.NUMBER_OF_PASSENGERS
import kotlin.random.Random
import sc.plugin2024.util.PluginConstants as Constants

@XStreamAlias(value = "board")
open class Board(
    gameField: TwoDBoard<Field> = initBoard(),
    segments: ArrayList<Segment> = ArrayList()
):
    RectangularBoard<Field>(gameField) {
    
    private fun getYCoordinateInDirection(y: Int, direction: HexDirection?) {
    
    }
    
    private fun getXCoordinateInDirection(x: Int, direction: HexDirection?) {
    
    }
    
    open fun getFieldInDirection(direction: HexDirection, field: Field): Field? {
        val coordinateInDirection = field.coordinate.plus(direction)
        return if(
            coordinateInDirection.x in gameField.indices &&
            coordinateInDirection.y in 0 until gameField[coordinateInDirection.x].size
        ) {
            gameField[coordinateInDirection.x][coordinateInDirection.y]
        } else {
            null
        }
    }
    
    private fun buildPattern(direction: HexDirection): List<Pair<Int, Int>> {
        val rightPattern = listOf(
            Pair(0, 0), Pair(2, 0), Pair(4, 0), Pair(6, 0), Pair(1, 1), Pair(3, 1), Pair(5, 1), Pair(7, 1),
            Pair(2, 2), Pair(4, 2), Pair(6, 2), Pair(8, 2), Pair(1, 3), Pair(3, 3), Pair(5, 3), Pair(7, 3),
            Pair(0, 4), Pair(2, 4), Pair(4, 4), Pair(6, 4)
        )
        val downRightPattern = listOf(
            Pair(0, 0), Pair(-1, 1), Pair(1, 1), Pair(-6, 2), Pair(-4, 2), Pair(-2, 2), Pair(0, 2), Pair(2, 2),
            Pair(-5, 3), Pair(-3, 3), Pair(-1, 3), Pair(1, 3), Pair(3, 3), Pair(-4, 4), Pair(-2, 4), Pair(0, 4),
            Pair(2, 4), Pair(-3, 5), Pair(-1, 5), Pair(1, 5), Pair(0, 6)
        )
        return when(direction) {
            HexDirection.RIGHT -> rightPattern
            HexDirection.LEFT -> rightPattern.map { Pair(-it.first, it.second) }
            HexDirection.DOWN_RIGHT -> downRightPattern
            HexDirection.DOWN_LEFT -> downRightPattern.reversed().map { Pair(-it.first, it.second) }
            HexDirection.UP_RIGHT -> downRightPattern.map { Pair(it.first, -it.second) }
            HexDirection.UP_LEFT -> downRightPattern.reversed().map { Pair(-it.first, -it.second) }
        }
    }
    
    private fun addSegment(
        segment: ArrayList<ArrayList<Field>>,
        pattern: List<Pair<Int, Int>>,
        segmentStart: Coordinates
    ) {
        val segmentPattern = buildPattern(HexDirection.RIGHT)
        for(i in pattern.indices) {
            val offsetCoordinate = Coordinates(
                segmentStart.x + pattern[i].first,
                segmentStart.y + pattern[i].second
            ).fromDoubledHex()
            val currentPositionInSegment =
                Coordinates(segmentPattern[i].first, segmentPattern[i].second).fromDoubledHex()
            if(segment.size <= currentPositionInSegment.x) {
                segment.add(ArrayList())
            }
            segment[currentPositionInSegment.x].add(this.gameField[offsetCoordinate.x][offsetCoordinate.y])
        }
    }
    
    private fun initSegment(
        seed: Int = Random.nextInt(),
        lastSegment: Int,
        segmentStart: Coordinates,
        direction: HexDirection,
        passengers: Int = NUMBER_OF_PASSENGERS,
        blocked: Int = Random.nextInt(MIN_ISLANDS, MAX_ISLANDS),
        special: Int = Random.nextInt(MIN_SPECIAL, MAX_SPECIAL),
        end: Boolean
    ) {
        val segment: ArrayList<ArrayList<Field>> = ArrayList()
        val pattern = buildPattern(direction)
        addSegment(segment, pattern, segmentStart)
    }
    
    companion object {
        fun initBoard(
            width: Int = Constants.NUMBER_OF_SEGMENTS * (Constants.SEGMENT_FIELDS_WIDTH + 1),
            height: Int = Constants.NUMBER_OF_SEGMENTS * Constants.SEGMENT_FIELDS_HEIGHT
        ): List<MutableList<Field>> {
            return List(width) { i ->
                MutableList(height) { j ->
                    Field(Coordinates(i, j), FieldType.VOID, 0)
                }
            }
        }
    }
}

