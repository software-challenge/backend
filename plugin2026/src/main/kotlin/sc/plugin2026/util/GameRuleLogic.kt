package sc.plugin2026.util

import sc.api.plugins.Coordinates
import sc.api.plugins.Direction
import sc.api.plugins.ITeam
import sc.plugin2026.Board
import sc.plugin2026.FieldState
import sc.plugin2026.Move
import sc.plugin2026.PiranhaMoveMistake
import sc.shared.IMoveMistake
import sc.shared.MoveMistake

object GameRuleLogic {
    /** Anzahl der Fische in der Bewegungsachse des Zuges. */
    @JvmStatic
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
    }
    
    /** Prüft ob ein Zug gültig ist.
     * @team null wenn der Zug valide ist, sonst ein entsprechender [IMoveMistake]. */
    @JvmStatic
    fun checkMove(board: Board, move: Move): IMoveMistake? {
        val distance = movementDistance(board, move)
        var pos = move.from
        
        val team = board[move.from].team ?: return MoveMistake.START_EMPTY
        val opponent = team.opponent()
        
        var moved = 1
        while(moved < distance) {
            pos += move.direction
            val field = board.getOrNull(pos) ?: return MoveMistake.DESTINATION_OUT_OF_BOUNDS
            if(field.team == opponent) {
                return PiranhaMoveMistake.JUMP_OVER_OPPONENT
            }
            moved++
        }
        pos += move.direction
        val state = board.getOrNull(pos)
        return when(state) {
            null -> MoveMistake.DESTINATION_OUT_OF_BOUNDS
            FieldState.SQUID -> MoveMistake.DESTINATION_BLOCKED
            else -> {
                if(state.team == team) {
                    MoveMistake.DESTINATION_BLOCKED_BY_SELF
                } else {
                    null
                }
            }
        }
    }
    
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
    }
    
    fun possibleMovesSequence(board: Board, pos: Coordinates): Sequence<Move> =
        Direction.values().asSequence()
            .map { direction -> Move(pos, direction)}
            .filter { move -> checkMove(board, move) == null }
    
    private fun getDirectNeighbour(f: Coordinates, parentSet: Set<Coordinates>): Set<Coordinates> {
        val returnSet: MutableSet<Coordinates> = HashSet()
        for(i in -1..1) {
            for(j in -1..1) {
                val x = f.x + i
                val y = f.y + j
                if(x < 0 || x >= PiranhaConstants.BOARD_LENGTH || y < 0 || y >= PiranhaConstants.BOARD_LENGTH || (i == 0 && j == 0)) continue
                
                val coord = Coordinates(x, y)
                if(parentSet.contains(coord)) {
                    returnSet.add(coord)
                }
            }
        }
        return returnSet
    }
    
    private fun getSwarm(found: MutableSet<Coordinates>, swarm: MutableSet<Coordinates>): MutableSet<Coordinates> {
        if(swarm.isEmpty() && !found.isEmpty()) {
            val field = found.iterator().next()
            swarm.add(field)
            found.remove(field)
        }
        
        var tmpSwarm: MutableSet<Coordinates> = HashSet(swarm)
        // O(swarm.size()) time
        for(field in swarm) {
            // Constant time for both calls (max of 8 neighbors)
            val neighbours = getDirectNeighbour(field, found)
            tmpSwarm.addAll(neighbours)
        }
        
        // O(found.size()*swarm.size()) time
        // FIXME: Might be improved O(swarm.size()) should be possible
        if(swarm.size != tmpSwarm.size) tmpSwarm = getSwarm(found, tmpSwarm)
        
        swarm.addAll(tmpSwarm)
        
        found.removeAll(swarm)
        return swarm
    }
    
    @JvmStatic
    fun greatestSwarm(fieldsToCheck: Set<Coordinates>): Set<Coordinates> {
        // Make a copy, so there will be no conflict with direct calls.
        val occupiedFields: MutableSet<Coordinates> = HashSet(fieldsToCheck)
        var greatestSwarm: Set<Coordinates> = HashSet()
        var maxSize = -1
        
        // this is a maximum of MAX_FISH iterations, so it is a linear iteration altogether
        while(!occupiedFields.isEmpty() && occupiedFields.size > maxSize) {
            val swarm: Set<Coordinates> = getSwarm(occupiedFields, HashSet())
            if(maxSize < swarm.size) {
                maxSize = swarm.size
                greatestSwarm = swarm
            }
        }
        return greatestSwarm
    }
    
    @JvmStatic
    fun greatestSwarm(board: Board, team: ITeam): Set<Coordinates> {
        val occupiedFields = board.fieldsForTeam(team)
        return greatestSwarm(occupiedFields.toHashSet())
    }
    
    @JvmStatic
    fun greatestSwarmSize(board: Board, team: ITeam): Int =
        greatestSwarm(board, team).size
    
    @JvmStatic
    fun greatestSwarmSize(set: Set<Coordinates>): Int =
        greatestSwarm(set).size
    
    @JvmStatic
    fun isSwarmConnected(board: Board, team: ITeam): Boolean {
        val fieldsWithFish = board.fieldsForTeam(team)
        val numGreatestSwarm: Int = greatestSwarmSize(fieldsWithFish.toHashSet())
        return numGreatestSwarm == fieldsWithFish.size
    }
}
