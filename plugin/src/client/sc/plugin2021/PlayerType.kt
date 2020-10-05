package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Beschreibt den Typ eines Clients. */
@XStreamAlias(value = "playertype")
enum class PlayerType {
    NONE,
    OBSERVER,
    PLAYER_ONE,
    PLAYER_TWO,
}