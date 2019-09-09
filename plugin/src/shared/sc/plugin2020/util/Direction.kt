package sc.plugin2020.util

enum class Direction {
    RIGHT,
    LEFT,
    UPRIGHT,
    UPLEFT,
    DOWNRIGHT,
    DOWNLEFT;

    fun shift(distance: Int): CubeCoordinates {
        return this.shift(CubeCoordinates(0, 0, 0), distance)
    }

    fun shift(start: CubeCoordinates, distance: Int = 1): CubeCoordinates {
        var shiftX = 0
        var shiftY = 0
        var shiftZ = 0
        when(this) {
            RIGHT -> {
                shiftX = start.x + distance
                shiftY = start.y - distance
            }
            LEFT -> {
                shiftX = start.x - distance
                shiftY = start.y + distance
            }
            UPRIGHT -> {
                shiftX = start.x + distance
                shiftZ = start.z - distance
            }
            UPLEFT -> {
                shiftY = start.y + distance
                shiftZ = start.z - distance
            }
            DOWNRIGHT -> {
                shiftY = start.y - distance
                shiftZ = start.z + distance
            }
            DOWNLEFT -> {
                shiftX = start.x - distance
                shiftZ = start.z + distance
            }
        }
        return CubeCoordinates(shiftX, shiftY, shiftZ)
    }
}
