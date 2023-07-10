package sc.plugin2024.util

import sc.framework.plugins.Constants
import sc.plugin2023.util.PluginConstants

/** Eine Sammlung an verschiedenen Konstanten, die im Spiel verwendet werden. */
object PluginConstants {
    // TODO honor limit in GameState
    const val ROUND_LIMIT = 30
    
    const val SEGMENT_FIELDS_WIDTH = 4
    const val SEGMENT_FIELDS_HEIGHT = 5
    const val NUMBER_OF_SEGMENTS = 8
    
    const val NUMBER_OF_PASSENGERS = 5
    
    const val POINTS_PER_PASSENGER = 5
    const val POINTS_PER_SEGMENTS = 5
    
    const val START_COAL = 6
    
    const val MAX_SPECIAL = 1
    const val MIN_SPECIAL = 0
    const val MAX_ISLANDS = 3
    const val MIN_ISLANDS = 2
    
    const val PASSENGER_ON_LAST_TILE = false
}