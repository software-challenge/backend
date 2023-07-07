package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.HexDirection
import sc.api.plugins.RectangularBoard
import sc.api.plugins.TwoDBoard
import sc.plugin2023.util.Pattern
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
        segments: ArrayList<Segment> = ArrayList(),
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
    
    private fun addSegment(
            segment: ArrayList<ArrayList<Field>>,
            pattern: List<Pair<Int, Int>>,
            segmentStart: Coordinates,
    ) {
        val segmentPattern = Pattern.RIGHT.pattern
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
            end: Boolean,
    ) {
        val segment: ArrayList<ArrayList<Field>> = ArrayList()
        val pattern = Pattern.match(direction).pattern
        addSegment(segment, pattern, segmentStart)
    }
    
    companion object {
        fun initBoard(
                width: Int = Constants.NUMBER_OF_SEGMENTS * (Constants.SEGMENT_FIELDS_WIDTH + 1),
                height: Int = Constants.NUMBER_OF_SEGMENTS * Constants.SEGMENT_FIELDS_HEIGHT,
        ): List<MutableList<Field>> {
            return List(width) { i ->
                MutableList(height) { j ->
                    Field(Coordinates(i, j), FieldType.VOID, 0)
                }
            }
        }
    }
}

