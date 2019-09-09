package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.shared.PlayerColor

@XStreamAlias(value = "piece")
data class Piece(
        @field:XStreamAsAttribute
        val owner: PlayerColor,
        @field: XStreamAsAttribute
        val type: PieceType)

enum class PieceType {
    ANT,
    BEE,
    BEETLE,
    GRASSHOPPER,
    SPIDER
}
