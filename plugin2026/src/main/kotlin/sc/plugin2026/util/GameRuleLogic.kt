package sc.plugin2026.util

import sc.api.plugins.Direction
import sc.api.plugins.Team
import sc.plugin2026.Board
import sc.plugin2026.Field

object GameRuleLogic {
    private fun getDirectNeighbour(board: Board, f: Field, parentSet: Set<Field>): Set<Field> {
        val returnSet: MutableSet<Field> = java.util.HashSet()
        for(i in -1..1) {
            for(j in -1..1) {
                val x = f.x + i
                val y = f.y + j
                if(x < 0 || x >= PiranhaConstants.BOARD_LENGTH || y < 0 || y >= PiranhaConstants.BOARD_LENGTH || (i == 0 && j == 0)) continue
                
                val field: Field = board.getField(x, y)
                if(parentSet.contains(field)) {
                    returnSet.add(field)
                }
            }
        }
        return returnSet
    }
    
    private fun getSwarm(board: Board, found: MutableSet<Field>, swarm: MutableSet<Field>): MutableSet<Field> {
        if(swarm.isEmpty() && !found.isEmpty()) {
            val field = found.iterator().next()
            swarm.add(field)
            found.remove(field)
        }
        
        var tmpSwarm: MutableSet<Field> = java.util.HashSet(swarm)
        // O(swarm.size()) time
        for(field in swarm) {
            // Constant time for both calls (max of 8 neighbors)
            val neighbours = getDirectNeighbour(board, field, found)
            tmpSwarm.addAll(neighbours)
        }
        
        // O(found.size()*swarm.size()) time
        // FIXME: Might be improved O(swarm.size()) should be possible
        if(swarm.size != tmpSwarm.size) tmpSwarm = getSwarm(board, found, tmpSwarm)
        
        swarm.addAll(tmpSwarm)
        
        found.removeAll(swarm)
        return swarm
    }
    
    fun greatestSwarm(board: Board, fieldsToCheck: Set<Field>): Set<Field> {
        // Make a copy, so there will be no conflict with direct calls.
        val occupiedFields: MutableSet<Field> = java.util.HashSet(fieldsToCheck)
        var greatestSwarm: Set<Field> = java.util.HashSet()
        var maxSize = -1
        
        // this is a maximum of MAX_FISH iterations, so it is a linear iteration altogether
        while(!occupiedFields.isEmpty() && occupiedFields.size > maxSize) {
            val empty: MutableSet<Field> = java.util.HashSet()
            val swarm: Set<Field> = getSwarm(board, occupiedFields, empty)
            if(maxSize < swarm.size) {
                maxSize = swarm.size
                greatestSwarm = swarm
            }
        }
        return greatestSwarm
    }
    
    fun greatestSwarm(board: Board, player: Team?): Set<Field> {
        val occupiedFields = getOwnFields(board, player)
        return greatestSwarm(board, occupiedFields)
    }
    
    fun greatestSwarmSize(board: Board, player: Team?): Int {
        return greatestSwarm(board, player).size
    }
    
    fun greatestSwarmSize(board: Board, set: Set<Field?>): Int {
        return greatestSwarm(board, set).size
    }
    
    fun isSwarmConnected(board: Board, player: Team?): Boolean {
        val fieldsWithFish = getOwnFields(board, player)
        val numGreatestSwarm: Int = greatestSwarmSize(board, fieldsWithFish)
        return numGreatestSwarm == fieldsWithFish.size
    }
}
