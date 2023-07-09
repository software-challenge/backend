package sc.plugin2024.util

import sc.api.plugins.HexDirection

/**
 * Enumeration representing different patterns.
 *
 * This enumeration provides patterns for different directions, such as right, left, up, down, etc.
 * Each pattern is defined by a list of pairs representing the pattern's movement in the X and Y coordinates.
 */
enum class Pattern(val pattern: List<Pair<Int, Int>>) {
    RIGHT(createPatternRight()),
    DOWN_RIGHT(createPatternDownRight()),
    LEFT(RIGHT.pattern.map { it.copy(first = -it.first) }),
    DOWN_LEFT(DOWN_RIGHT.pattern.reversed().map { it.copy(first = -it.first) }),
    UP_RIGHT(DOWN_RIGHT.pattern.map { it.copy(second = -it.second) }),
    UP_LEFT(DOWN_RIGHT.pattern.reversed().map { it.copy(first = -it.first, second = -it.second) });
    
    companion object {
        /**
         * Returns the corresponding Pattern for a given HexDirection.
         *
         * @param hexDirection The HexDirection to get the Pattern for.
         * @return The corresponding Pattern for the given HexDirection.
         */
        fun match(hexDirection: HexDirection): Pattern {
            return when(hexDirection) {
                HexDirection.RIGHT -> RIGHT
                HexDirection.UP_RIGHT -> UP_RIGHT
                HexDirection.UP_LEFT -> UP_LEFT
                HexDirection.LEFT -> LEFT
                HexDirection.DOWN_LEFT -> DOWN_LEFT
                HexDirection.DOWN_RIGHT -> DOWN_RIGHT
            }
        }
    }
}

/**
 * Returns a list representing a right pattern.
 *
 * The pattern is a list of pairs, where each pair represents the coordinates of a point.
 * The points are arranged in a right pattern, with each row having two points on alternating columns.
 * The pattern starts in the top-left corner and continues to the bottom-right corner.
 * The coordinates are represented as pairs of integers, where the first value is the x-coordinate and the second value is the y-coordinate.
 *
 * @return a list representing the right pattern
 */
private fun createPatternRight() = listOf(
        0 to 0, 2 to 0, 4 to 0, 6 to 0,
        1 to 1, 3 to 1, 5 to 1, 7 to 1,
        2 to 2, 4 to 2, 6 to 2, 8 to 2,
        1 to 3, 3 to 3, 5 to 3, 7 to 3,
        0 to 4, 2 to 4, 4 to 4, 6 to 4
)

/**
 * Creates a pattern in the down-right direction.
 *
 * This method returns a list of coordinates representing a specific pattern in the down-right direction.
 *
 * @return a list of coordinates representing the pattern
 */
private fun createPatternDownRight() = listOf(
        0 to 0, -1 to 1, 1 to 1, -6 to 2,
        -4 to 2, -2 to 2, 0 to 2, 2 to 2,
        -5 to 3, -3 to 3, -1 to 3, 1 to 3,
        3 to 3, -4 to 4, -2 to 4, 0 to 4,
        2 to 4, -3 to 5, -1 to 5, 1 to 5,
        0 to 6
)