package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.ITeam

@XStreamAlias(value = "piece")
data class Piece(
        @field:XStreamAsAttribute
        val owner: ITeam,
        @field: XStreamAsAttribute
        val type: PieceType)

enum class PieceType(val letter: Char) {
    ANT('A'),
    BEE('Q'),
    BEETLE('B'),
    GRASSHOPPER('G'),
    SPIDER('S')
}
