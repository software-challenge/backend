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
    
    fun createCustomBoard(boardString: String): Board {
        val boardStringWithoutWhitespace = boardString.replace(" ".toRegex(), "")
        Assert.assertEquals("Length of boardString does not match size of the Board",
                (Constants.FIELD_AMOUNT * 2).toLong(), boardStringWithoutWhitespace.length.toLong())
        
        val fields = Board()
                .fields
                .sortedWith(Comparator.comparing(Field::z).then(Comparator.comparing(Field::x)))
                .map { intArrayOf(it.x, it.y) }
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

    fun updateUndeployedPiecesFromBoard(gs: GameState, reset: Boolean = false) {
        if(reset) {
            PlayerColor.values().forEach {
                gs.getUndeployedPieces(it).clear()
                gs.getUndeployedPieces(it).addAll(GameState.parsePiecesString(Constants.STARTING_PIECES, it))
            }
        }
        gs.board.getPieces().forEach {
            gs.getUndeployedPieces(it.owner).remove(it)
        }
    }

    fun updateGamestateWithBoard(gs: GameState, customBoard: String) {
        val board = createCustomBoard(customBoard)
        gs.board = board
        updateUndeployedPiecesFromBoard(gs)
    }

}

