package sc.plugin2024


enum class Direction(val value: Int, private val translation: String) {
    RIGHT(0, "rechts"),
    UP_RIGHT(1, "oben rechts"),
    UP_LEFT(2, "oben links"),
    LEFT(3, "links"),
    DOWN_LEFT(4, "unten links"),
    DOWN_RIGHT(5, "unten rechts");

    private fun getForValue(`val`: Int): Direction {
        for (dir in values()) {
            if (`val` == dir.value) {
                return dir
            }
        }
        throw IllegalArgumentException("no direction for value $`val`")
    }

    val opposite: Direction
        /**
         * Berechnet die gegenüberliegende Richtung, die durch Drehen um 180 Grad ensteht
         * @return opposite direction
         */
        get() = getTurnedDirection(3)

    /**
     * Berechnet die Richtung nach drehen von turn Schritten
     * @param turn Anzahl der zu drehenden Schritte (positive Werte gegen den Uhrzeigersinn, negative Werte im Uhrzeigersinn).
     * @return Richtung, zu der gedreht wird
     */
    fun getTurnedDirection(turn: Int): Direction {
        return getForValue((value + turn + 6) % 6)
    }

    /**
     * Gibt die Anzahl der Drehungen bei einer Drehung von der aktuellen Richtung zu toDir zurück
     * @param toDir Endrichtung
     * @return Anzahl der Drehungen
     */
    fun turnToDir(toDir: Direction): Int {
        var direction = (value - toDir.value + 6) % 6
        direction = if (direction >= 3) {
            6 - direction
        } else {
            -direction
        }
        return direction
    }

    override fun toString(): String {
        return translation
    }
}

