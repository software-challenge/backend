package sc.plugin2020.util

enum class Direction {
    RIGHT,
    LEFT,
    UPRIGHT,
    UPLEFT,
    DOWNRIGHT,
    DOWNLEFT;

    fun shift(d: Int): CubeCoordinates { // d is the distance
        var shiftX = 0
        var shiftY = 0
        var shiftZ = 0
        when(this) {
            RIGHT -> {
                shiftX = 1 * d
                shiftY = -1 * d
            }
            LEFT -> {
                shiftX = -1 * d
                shiftY = 1 * d
            }
            UPRIGHT -> {
                shiftX = 1 * d
                shiftZ = -1 * d
            }
            UPLEFT -> {
                shiftY = 1 * d
                shiftZ = -1 * d
            }
            DOWNRIGHT -> {
                shiftY = -1 * d
                shiftZ = 1 * d
            }
            DOWNLEFT -> {
                shiftX = -1 * d
                shiftZ = 1 * d
            }
        }
        return CubeCoordinates(shiftX, shiftY, shiftZ)
    }
}
