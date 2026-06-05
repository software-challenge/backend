package sc.plugin2098.util

import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.plugin2098.Board
import sc.plugin2098.Move
import sc.shared.IMoveMistake
import sc.shared.MoveMistake
import sc.plugin2098.Connect4MoveMistake
import sc.api.plugins.Vector

/** Regellogik und Hilfsfunktionen für das Piranhas-Spiel. */
object GameRuleLogic {
    
    /** Prüft, ob ein Zug gültig ist.
     * @return null wenn der Zug valide ist, sonst ein entsprechender [IMoveMistake]. */
    @JvmStatic
    fun checkMove(board: Board, move: Move): IMoveMistake? {

        // Überprüfen, ob die Koordinaten in auf dem Spielfeld sind
        if (move.position.x < 0 || move.position.x >= Connect4Constants.BOARD_WIDTH ||
                move.position.y < 0 || move.position.y >= Connect4Constants.BOARD_HEIGHT)
            return MoveMistake.DESTINATION_OUT_OF_BOUNDS
        
        // Überprüfen ob das Feld nicht leer ist
        if(!board[move.position].isEmpty)
            return MoveMistake.DESTINATION_BLOCKED

        // Überprüfen ob das Feld unter dem Ziel nicht unterhalb des Spielfelds ist und nicht von einem Plätchen belegt ist
        val fieldBelow = move.position + Vector(0, -1)
        if(move.position.y != 0 && board[fieldBelow].isEmpty) {
            return Connect4MoveMistake.DESTINATION_IN_AIR
        }

        return null
    }
    
    /** Prüft ob 4 Plätchen einer Farbe verbunden sind.
     * @return true wenn 4 Plätchen verbunden sind, sonst false.
     */
    @JvmStatic
    fun is4Connected(board: Board, team: Team): Boolean {
        return get4Connected(board, team).isNotEmpty()
    }
    
    // Up could be ignored in some cases for better performance
    val directions = arrayOf(
        Vector(1, 0),   // Right
        Vector(-1, 0),  // Left
        Vector(0, 1),   // Up
        Vector(0, -1),  // Down
        Vector(1, 1),   // Up-Right
        Vector(-1, -1), // Down-Left
        Vector(1, -1),  // Down-Right
        Vector(-1, 1),  // Up-Left
    )
    
    /** Prüft ob 4 Plätchen einer Farbe verbunden sind und gibt diese Zurück.
     * Diese Version dieser Funktion ist deutlich effizienter, setzt aber vorraus, dass das als letze platzierte Pltächen bekannt ist.
     * @return Liste mit 4 {@link Coordinates} der 4 gleichfarbige Plätchen die verbunden sind, sonst leere Liste.
     */
    @JvmStatic
    fun get4Connected(board: Board, team: Team, piece: Coordinates): List<Coordinates> {
        
        if(board.get(piece.x, piece.y).team != team) return listOf()
        
        for(direction in directions) {
            var connected = true
            
            for(i in 1 until 4) {
                val nextX = piece.x + direction.dx * i
                val nextY = piece.y + direction.dy * i
                
                if(
                    nextX !in 0 until Connect4Constants.BOARD_WIDTH ||
                    nextY !in 0 until Connect4Constants.BOARD_HEIGHT ||
                    board.get(nextX, nextY).team != team
                ) {
                    connected = false
                    break
                }
            }
            
            if(connected) {
                val chipCords = mutableListOf<Coordinates>()
                
                for(i in 1 until 4) {
                    chipCords.add(Coordinates(piece.x + direction.dx * i, piece.y + direction.dy * i))
                }
                
                return chipCords
            }
        }
        
        return listOf()
    }
    
    /** Prüft ob 4 Plätchen einer Farbe verbunden sind und gibt diese Zurück.
     * @return Liste mit 4 {@link Coordinates} der 4 gleichfarbige Plätchen die verbunden sind, sonst leere Liste.
     */
    @JvmStatic
    fun get4Connected(board: Board, team: Team): List<Coordinates> {
        for (x in 0 until Connect4Constants.BOARD_WIDTH) {
            // Check for each column every piece starting from the top going downwards until a non-empty field is found
            // This must be the last piece placed in this column and thus can be the only one in the row ending the game
            // If a piece below the top piece would cause a win, there is either a double win (which still would be caught by this algorithm)
            // or there was a win condition earlier in the game that wasn't caught
            for (y in Connect4Constants.BOARD_HEIGHT - 1 downTo 0) {
                if (board.get(x, y).isEmpty) continue
                get4Connected(board, team, Coordinates(x, y)).let { if (it.isNotEmpty()) return it }
                break
            }
        }

        return listOf()
    }
}
