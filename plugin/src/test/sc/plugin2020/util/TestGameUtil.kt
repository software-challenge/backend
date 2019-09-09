package sc.plugin2020.util

import org.junit.Assert
import sc.plugin2020.*
import sc.shared.PlayerColor
import java.security.InvalidParameterException
import java.util.*

object TestGameUtil {

    private fun parsePiece(pc: PlayerColor, c: Char): Piece {
        return when(c) {
            'Q' -> Piece(pc, PieceType.BEE)
            'B' -> Piece(pc, PieceType.BEETLE)
            'G' -> Piece(pc, PieceType.GRASSHOPPER)
            'S' -> Piece(pc, PieceType.SPIDER)
            'A' -> Piece(pc, PieceType.ANT)
            else -> throw InvalidParameterException("Expected piecetype character to be one of Q,B,G,S or A, was: $c")
        }
    }

    fun createCustomBoard(boardString: String): Board {//Hardcoded auf Feldgröße von 9
        val boardStringWithoutWhitespace = boardString.replace(" ".toRegex(), "")
        Assert.assertEquals("Length of boardString does not match size of the Board",
                (Constants.FIELD_AMOUNT * 2).toLong(), boardStringWithoutWhitespace.length.toLong())

        val fields = arrayOf(intArrayOf(0, 4), intArrayOf(1, 3), intArrayOf(2, 2), intArrayOf(3, 1), intArrayOf(4, 0), intArrayOf(-1, 4), intArrayOf(0, 3), intArrayOf(1, 2), intArrayOf(2, 1), intArrayOf(3, 0), intArrayOf(4, -1), intArrayOf(-2, 4), intArrayOf(-1, 3), intArrayOf(0, 2), intArrayOf(1, 1), intArrayOf(2, 0), intArrayOf(3, -1), intArrayOf(4, -2), intArrayOf(-3, 4), intArrayOf(-2, 3), intArrayOf(-1, 2), intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(2, -1), intArrayOf(3, -2), intArrayOf(4, -3), intArrayOf(-4, 4), intArrayOf(-3, 3), intArrayOf(-2, 2), intArrayOf(-1, 1), intArrayOf(0, 0), intArrayOf(1, -1), intArrayOf(2, -2), intArrayOf(3, -3), intArrayOf(4, -4), intArrayOf(-4, 3), intArrayOf(-3, 2), intArrayOf(-2, 1), intArrayOf(-1, 0), intArrayOf(0, -1), intArrayOf(1, -2), intArrayOf(2, -3), intArrayOf(3, -4), intArrayOf(-4, 2), intArrayOf(-3, 1), intArrayOf(-2, 0), intArrayOf(-1, -1), intArrayOf(0, -2), intArrayOf(1, -3), intArrayOf(2, -4), intArrayOf(-4, 1), intArrayOf(-3, 0), intArrayOf(-2, -1), intArrayOf(-1, -2), intArrayOf(0, -3), intArrayOf(1, -4), intArrayOf(-4, 0), intArrayOf(-3, -1), intArrayOf(-2, -2), intArrayOf(-1, -3), intArrayOf(0, -4))
        val fieldDescriptors = boardStringWithoutWhitespace.toCharArray()
        val boardFields = ArrayList<Field>()
        for(i in fields.indices) {
            when(fieldDescriptors[i * 2]) {
                'R' -> Field(fields[i][0], fields[i][1], parsePiece(PlayerColor.RED, fieldDescriptors[i * 2 + 1]))
                'B' -> Field(fields[i][0], fields[i][1], parsePiece(PlayerColor.BLUE, fieldDescriptors[i * 2 + 1]))
                'O' -> Field(fields[i][0], fields[i][1], true)
                '-' -> Field(fields[i][0], fields[i][1])
                else -> throw InvalidParameterException("Expected first character to be either B (blue), R (red) or O (obstructed), was: " + fieldDescriptors[i * 2])
            }.let { boardFields.add(it) }
        }
        return Board(boardFields)
    }

    fun updateUndeployedPiecesFromBoard(gs: GameState) {
        for(color in PlayerColor.values()) {
            val deployed = gs.board.getPiecesFor(color)
            gs.getUndeployedPieces(color).removeIf { p -> deployed.contains(p) }
        }
    }

    fun updateGamestateWithBoard(gs: GameState, customBoard: String) {
        val board = createCustomBoard(customBoard)
        gs.board = board
        updateUndeployedPiecesFromBoard(gs)
    }

}

