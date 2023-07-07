package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.HexDirection
import sc.api.plugins.IBoard
import sc.api.plugins.RectangularBoard
import sc.plugin2024.util.PluginConstants as Constants
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Erzeugt ein neues Spielfeld anhand der gegebenen Segmente
 * @param tiles Spielsegmente des neuen Spielfelds
 */
@XStreamAlias(value = "board")
data class Board(val tiles: List<Tile> = generateBoard(), var visibleTiles: Int = 2): IBoard {
    
    fun visibleTiles(): List<Tile> = tiles.subList(0, visibleTiles)
    
    companion object {
        fun generateBoard(): List<Tile> {
            val tiles = ArrayList<Tile>()
            val rnd = Random()
            val direction = arrayOfNulls<HexDirection>(Constants.NUMBER_OF_TILES)
            val startCoordinates = Array<IntArray>(Constants.NUMBER_OF_TILES) { IntArray(2) }
            val tilesWithPassengers = ArrayList<Int>() // holds all tiles numbers with a passenger field
            for(i in 0 until Constants.NUMBER_OF_PASSENGERS) {
                // They cannot be a passenger on the starting tile change to -2 for no passenger on last Tile
                var number: Int
                do {
                    number = rnd.nextInt(Constants.NUMBER_OF_TILES - if(Constants.PASSENGER_ON_LAST_TILE) 1 else 2) + 1
                } while(tilesWithPassengers.contains(number))
                tilesWithPassengers.add(number)
            }
            direction[0] = HexDirection.RIGHT
            startCoordinates[0][0] = 0
            startCoordinates[0][1] = 0
            // generate directions of tiles
            for(i in 1 until Constants.NUMBER_OF_TILES) {
                val dir: Int = if(i == 1) {
                    // The tile after the starting tile should always point in the same
                    // direction. Otherwise, one player would have a disadvantage.
                    0
                } else {
                    if(direction[i - 1] === HexDirection.DOWN_LEFT) {
                        // last direction was down left, don't allow more turning to the right (to avoid circles)
                        rnd.nextInt(2) // 0 or 1 only straight or turning left
                    } else if(direction[i - 1] === HexDirection.UP_LEFT) {
                        // last direction was up left, don't allow more turning to the left (to avoid circles)
                        rnd.nextInt(2) - 1 // 0 or -1 only straight or turning right
                    } else {
                        rnd.nextInt(3) - 1 // -1, 0 or 1
                    }
                }
                direction[i] = direction[i - 1]!!.getTurnedDirection(dir)
                startCoordinates[i][0] = getXCoordinateInDirection(startCoordinates[i - 1][0], direction[i])
                startCoordinates[i][1] = getYCoordinateInDirection(startCoordinates[i - 1][1], direction[i])
            }
            tiles.add(Tile(0, 0, 0, 0, 0, 0, 0)) // start tile only water
            for(i in 1 until Constants.NUMBER_OF_TILES) {
                generateTile(
                        i, tilesWithPassengers.contains(i),
                        direction[i]!!, startCoordinates[i][0], startCoordinates[i][1]
                )
            }
        }
        
        /**
         * Nur fuer den Server relevant. Gibt Koordiante 4 Felder in Richtung zurück
         * @param y y Koordinate
         * @param direction Richtung
         * @return y Koordinate des neuen Feldes
         */
        private fun getYCoordinateInDirection(y: Int, direction: HexDirection?): Int {
            when (direction) {
                HexDirection.RIGHT, HexDirection.LEFT -> return y
                HexDirection.UP_RIGHT, HexDirection.UP_LEFT -> return y - 4
                HexDirection.DOWN_LEFT, HexDirection.DOWN_RIGHT -> return y + 4
            }
            return 0
        }
        
        /**
         * Nur fuer den Server relevant. Gibt Koordiante 4 Felder in Richtung zurück
         * @param x x Koordinate
         * @param direction Richtung
         * @return x Koordinate des neuen Feldes
         */
        private fun getXCoordinateInDirection(x: Int, direction: HexDirection?): Int {
            when (direction) {
                HexDirection.RIGHT -> return x + 4
                HexDirection.LEFT -> return x - 4
                HexDirection.UP_RIGHT, HexDirection.DOWN_RIGHT -> return x + 2
                HexDirection.DOWN_LEFT, HexDirection.UP_LEFT -> return x - 2
            }
            return 0
        }
        
        /**
         * Nur fuer den Server relevant
         * generates tile
         * @param index index of Tile
         * @param hasPassenger has the Tile a passenger?
         * @param direction direction of tile
         * @param x x Coordinate of middle
         * @param y y Coordinate of middle
         */
        private fun generateTile(index: Int, hasPassenger: Boolean, direction: HexDirection, x: Int, y: Int): Tile {
            val rnd = Random()
            val blocked: Int =
                    rnd.nextInt(Constants.MAX_ISLANDS - Constants.MIN_ISLANDS + 1) + Constants.MIN_ISLANDS // 2 to 3 blocked fields
            val special: Int =
                    rnd.nextInt(Constants.MAX_SPECIAL - Constants.MIN_SPECIAL + 1) + Constants.MIN_SPECIAL // 1 oder 2 special fields
            return Tile(index, direction.ordinal, x, y, if (hasPassenger) 1 else 0, blocked, special)
        }
    }

    /**
     * Gibt ein Feld zurück
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @return Feld an entsprechenden Koordinaten, gibt null zurück, sollte das Feld nicht (mehr) existieren
     */
    fun getField(x: Int, y: Int): FieldType {
        for (tile in tiles) {
            if (tile.isVisible) {
                val field = tile.getField(x, y)
                if (field != null) {
                    return field
                }
            }
        }
        return null
    }

    /**
     * Gibt ein Feld zurück
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @return Feld an entsprechenden Koordinaten, gibt null zurück, sollte das Feld nicht (mehr) existieren
     */
    fun alwaysGetField(x: Int, y: Int): FieldType {
        for (tile in tiles) {
            val field = tile.getField(x, y)
            if (field != null) {
                return field
            }
        }
        return null
    }

    /**
     * Erzeugt eine Deepcopy des Spielbretts
     */
    override fun clone(): Board {
        val clonedTiles = ArrayList<Tile>()
        for (tile in tiles) {
            val clonedTile = tile.clone()
            clonedTiles.add(clonedTile)
        }
        return Board(clonedTiles)
    }

    override fun toString(): String {
        var toString = "Board:\n"
        for (tile in tiles) {
            toString += """
                
                $tile
                """.trimIndent()
        }
        return toString
    }
}

