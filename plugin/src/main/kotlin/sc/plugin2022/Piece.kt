package sc.plugin2022

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.ITeam
import sc.api.plugins.Team

enum class PieceType(val char: Char, vararg val possibleMoves: Vector) {
    /** Bewegt sich nur diagonal vorwärts. */
    Herzmuschel('H', Vector(1, 1), Vector(1, -1)),
    /** Bewegt sich nur auf Nachbarfelder. */
    Moewe('M', *Vector.cardinals),
    /** Bewegt sich diagonal oder vorwärts. */
    Seestern('S', *Vector.diagonals, Vector(1, 0)),
    /** Wie ein Springer im Schach. Einzige nicht-Leichtfigur */
    Robbe('R', *Vector.diagonals.flatMap { listOf(it.copy(dx = it.dx * 2), it.copy(dy = it.dy * 2)) }.toTypedArray());
    
    val isLight
        get() = this != Robbe
    
    fun teamPieces() = Team.values().map { Piece(this, it) }
}

/** Ein Spielstein. */
@XStreamAlias(value = "piece")
data class Piece(
        /** Typ des (obersten) Steins. */
        @XStreamAsAttribute val type: PieceType,
        /** Welchem Team dieser Stein/Turm gehört. */
        @XStreamAsAttribute val team: Team,
        /** Anzahl der Steine in diesem Turm. */
        @XStreamAsAttribute val count: Int = 1,
) {
    val possibleMoves
        get() = type.possibleMoves.map { it.copy(dx = it.dx * team.direction) }
    
    val isAmber
        get() = count >= 3
    
    /** @return ein neuer Spielstein, dem [other] unterliegt. */
    fun capture(other: Piece) = copy(count = count + other.count)
    
    fun shortString(): String {
        val char = type.char.run { if(team.index > 0) toLowerCase() else this }.toString()
        return if (count == 1) char + char else char + count
    }
    
    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        fun fromString(string: String): Piece {
            val type = string.first()
            return Piece(PieceType.values().first { it.char.equals(type, true) },
                    if(type.isLowerCase()) Team.TWO else Team.ONE,
                    string.last().digitToIntOrNull() ?: 1)
        }
    }
}

val ITeam.direction
    get() = if (index == 0) 1 else -1
