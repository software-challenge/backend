package sc.plugin2098.util

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
        // TODO: Überprüfen, ob der untere Rand tatsächlich y=0 ist
        val fieldBelow = move.position + Vector(0, -1)
        if(move.position.y != 0 && board[fieldBelow].isEmpty) {
            return Connect4MoveMistake.DESTINATION_IN_AIR
        }

        return null
    }

    /** Prüft ob 4 Plätchen einer Farbe verbunden sind
     * @return true wenn 4 gleichfarbige Plätchen verbunden sind, sonst false */
    @JvmStatic
    fun is4Connected(board: Board, team: Team): Boolean {
        val directions = arrayOf(
            Vector(1, 0),   // Rechts
            Vector(0, 1),   // Oben
            Vector(1, 1),   // Oben-Rechts
            Vector(1, -1),  // Unten-Rechts
        )

        for (y in 0 until Connect4Constants.BOARD_HEIGHT) {
            for (x in 0 until Connect4Constants.BOARD_WIDTH) {
                if (board[x, y].team != team) continue

                for (direction in directions) {
                    var connected = true

                    for (i in 1 until 4) {
                        val nextX = x + direction.dx * i
                        val nextY = y + direction.dy * i

                        if (
                            nextX !in 0 until Connect4Constants.BOARD_WIDTH ||
                            nextY !in 0 until Connect4Constants.BOARD_HEIGHT ||
                            board[nextX, nextY].team != team
                        ) {
                            connected = false
                            break
                        }
                    }

                    if (connected) return true
                }
            }
        }

        return false
    }
}
