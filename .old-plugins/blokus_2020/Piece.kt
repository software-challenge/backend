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

enum class PieceType(val letter: Char) {
    ANT('A'),
    BEE('Q'),
    BEETLE('B'),
    GRASSHOPPER('G'),
    SPIDER('S')
}
