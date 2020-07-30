package sc.plugin2021.helper

import io.kotlintest.shouldBe
import org.junit.jupiter.api.assertDoesNotThrow
import sc.plugin2021.*


/** Helper class to convert Strings to Moves. Used to manually verify moves.
 *  Moves are in the form of 'COLOR|kind:rotation:flip|x,y`, i.e.:
 *  'RED|12:2:1|4,16' -> Piece(RED, 12, Rotation.MIRROR, true, Coordinates(4, 16))
 *
 *  PassMoves are 'COLOR|pass'
 */
object MoveParser {
    fun parse(move: String): Move {
        val pieceFragments = move.split("|")
        val color = Color.valueOf(pieceFragments[0])
        
        if (pieceFragments[1] == "pass")
            return PassMove(color)
        
        val type = pieceFragments[1].split(":")
        val kind = type[0].toInt()
        val rotation = Rotation.values()[type[1].toInt()]
        val isFlipped = type[2] != "0"
        
        val coords = pieceFragments[2].split(",")
        val position = Coordinates(coords[0].toInt(), coords[1].toInt())
        
        return SetMove(Piece(color, kind, rotation, isFlipped, position))
    }
    
    private fun selfCheck() {
        val moves: List<Pair<String, Move>> = listOf(
            "BLUE|3:0:0|3,18"   to SetMove(Piece(Color.BLUE, 3, Rotation.NONE, false, Coordinates(3, 18))),
            "YELLOW|14:1:1|0,2" to SetMove(Piece(Color.YELLOW, 14, Rotation.RIGHT, true, Coordinates(0, 2))),
            "RED|10:3:0|13,15"  to SetMove(Piece(Color.RED, 10, Rotation.LEFT, false, Coordinates(13, 15))),
            "GREEN|pass" to PassMove(Color.GREEN)
        )
        moves.forEach{
            parse(it.first).toString() shouldBe it.second.toString()
        }
    }
    
    init {
        assertDoesNotThrow {
            selfCheck()
        }
    }
}