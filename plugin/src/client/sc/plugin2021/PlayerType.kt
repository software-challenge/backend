package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

/** The role of a Client. */
@XStreamAlias(value = "playertype")
enum class PlayerType {
    NONE,
    OBSERVER,
    PLAYER_ONE,
    PLAYER_TWO,
}