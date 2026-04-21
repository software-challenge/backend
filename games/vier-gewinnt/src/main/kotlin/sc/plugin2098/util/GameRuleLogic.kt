package sc.plugin2098.util

import sc.api.plugins.Coordinates
import sc.api.plugins.Direction
import sc.api.plugins.ITeam
import sc.api.plugins.Team
import sc.plugin2098.Board
import sc.plugin2098.FieldState
import sc.plugin2098.Move
import sc.shared.IMoveMistake
import sc.shared.MoveMistake
import sc.plugin2098.Connect4MoveMistake
import sc.api.plugins.Vector

/** Regellogik und Hilfsfunktionen für das Piranhas-Spiel. */
object GameRuleLogic {
    /** Anzahl der Fische in der Bewegungsachse des Zuges.
     * @return wie viele Felder weit der Zug sein sollte */
    /*@JvmStatic
    fun movementDistance(board: Board, move: Move): Int {
        var count = 1
        var pos = move.from
        while(true) {
            pos += move.direction
            val field = board.getOrNull(pos) ?: break
            if(field.team != null) {
                count++
            }
        }
        pos = move.from
        while(true) {
            pos += move.direction.opposite
            val field = board.getOrNull(pos) ?: break
            if(field.team != null) {
                count++
            }
        }
        return count
    }*/
    
    /** Berechnet die Zielkoordinaten des Zuges. */
    /*@JvmStatic
    fun targetCoordinates(board: Board, move: Move): Coordinates =
        move.from + move.direction * movementDistance(board, move)*/
    
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
    // TODO: Implementieren
    @JvmStatic
    fun is4Connected(board: Board, team: Team): Boolean {
        return false
    }
    
    /*/** Valide Züge des Fisches auf dem Startfeld(???) [pos]. */
    @JvmStatic
    fun possibleMovesFor(board: Board, pos: Coordinates): Collection<Move> {
        val moves: MutableList<Move> = ArrayList()
        for(direction in Direction.values()) {
            val move = Move(pos, direction)
            if(checkMove(board, move) == null) {
                moves.add(move)
            }
        }
        return moves
    }*/
    
//    /** Sequenz aller gültigen Züge des Fisches auf dem Startfeld [pos]. */
//    fun possibleMovesSequence(board: Board, pos: Coordinates): Sequence<Move> =
//        Direction.values().asSequence()
//            .map { direction -> Move(pos, direction)}
//            .filter { move -> checkMove(board, move) == null }
    
//    /** @return die [Coordinates] aus [parentSet], die Nachbarn von [pos] sind */
//    private fun selectNeighbors(pos: Coordinates, parentSet: Collection<Coordinates>): Collection<Coordinates> {
//        val returnSet = ArrayList<Coordinates>(8)
//        for(i in -1..1) {
//            for(j in -1..1) {
//                val x = pos.x + i
//                val y = pos.y + j
//                if(x < 0 || x >= PiranhaConstants.BOARD_HEIGHT ||
//                   y < 0 || y >= PiranhaConstants.BOARD_WIDTH ||
//                   (i == 0 && j == 0)) continue
//
//                val coord = Coordinates(x, y)
//                if(parentSet.contains(coord)) {
//                    returnSet.add(coord)
//                }
//            }
//        }
//        return returnSet
//    }
//
//    /** Startet mit einem einzelnen Fisch in [swarm] und den verbleibenden [looseFishes],
//     * ruft sich rekursiv mit hinzugefügten Nachbarn auf, um den gesamten Schwarm zu finden. */
//    private fun getSwarm(looseFishes: Collection<Coordinates>, swarm: List<Coordinates>): List<Coordinates> {
//        val swarmNeighbors =
//            swarm.flatMap { selectNeighbors(it, looseFishes) }
//
//        // only search on if any neighbors were added
//        if(swarmNeighbors.isNotEmpty()) {
//            return getSwarm(looseFishes - swarmNeighbors, swarm + swarmNeighbors)
//        }
//        return swarm
//    }

    /*
    /** Findet den schwersten Schwarm innerhalb einer Menge gewichteter Positionen. */
    @JvmStatic
    fun greatestSwarm(fieldsToCheck: Map<Coordinates, Int>): Map<Coordinates, Int>? {
        // Make a copy, so there will be no conflict with direct calls.
        val fieldsLeft = fieldsToCheck.keys.toMutableList()
        var maxSize = -1
        var maxSwarm: Map<Coordinates, Int>? = null
        
        // this is a maximum of MAX_FISH iterations, so it is a linear iteration altogether
        while(!fieldsLeft.isEmpty() && fieldsLeft.size * 3 > maxSize) {
            val swarmStart = listOf(fieldsLeft.removeLast())
            //println("$swarmStart - $fieldsLeft")
            val swarmCoords = getSwarm(fieldsLeft, swarmStart)
            
            fieldsLeft.removeAll(swarmCoords)
            val swarm = fieldsToCheck.filterKeys { swarmCoords.contains(it) }
            val swarmSize = swarm.values.sum()
            //println("$swarmCoords - $swarm - $swarmSize")
            if(maxSize < swarmSize) {
                maxSwarm = swarm
                maxSize = swarmSize
            }
        }
        return maxSwarm
    }
    
    /** @return Größe des schwersten Schwarms innerhalb der gegebenen Felder */
    @JvmStatic
    fun greatestSwarmSize(fields: Map<Coordinates, Int>): Int =
        greatestSwarm(fields)?.values?.sum() ?: -1
    
    /** @return Größe des schwersten Schwarms von [team] */
    @JvmStatic
    fun greatestSwarmSize(board: Board, team: ITeam): Int =
        greatestSwarmSize(board.fieldsForTeam(team))
    
    /** @return ob alle Fische des Teams zusammenhängend sind */
    @JvmStatic
    fun isSwarmConnected(board: Board, team: ITeam): Boolean {
        val fieldsWithFish = board.fieldsForTeam(team)
        val greatestSwarm = greatestSwarm(fieldsWithFish)
        return greatestSwarm?.size == fieldsWithFish.size
    }*/
}
