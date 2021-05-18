package sc.plugin2022

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.ITeam

enum class PieceType(val char: Char, vararg val possibleMoves: Vector) {
    Herzmuschel('H', Vector(1, 1), Vector(-1, 1)),
    Moewe('M', *Vector.cardinals),
    Seestern('S', *Vector.diagonals, Vector(0, 1)),
    Robbe('R', *Vector.diagonals.flatMap { listOf(it.copy(dx = it.dx * 2), it.copy(dy = it.dy * 2)) }.toTypedArray());
    val isLight
        get() = this != Robbe
}

/** Ein Spielstein. */
@XStreamAlias(value = "piece")
data class Piece(
        /** Typ des (obersten) Steins. */
        @XStreamAsAttribute val type: PieceType,
        /** Welchem Team dieser Stein/Turm gehört. */
        @XStreamAsAttribute val team: ITeam,
        /** Anzahl der Steine in diesem Turm. */
        @XStreamAsAttribute var count: Int = 1,
) {
    val possibleMoves
        get() = type.possibleMoves.map { it.copy(dy = it.dy * team.direction) }
    
    val isAmber
        get() = count > 3
    
    fun capture(other: Piece) {
        count += other.count
    }
    
    fun shortString() =
            type.char.toString() + if (count == 1) type.char else count
}

val ITeam.direction
    get() = if (index == 0) 1 else -1
