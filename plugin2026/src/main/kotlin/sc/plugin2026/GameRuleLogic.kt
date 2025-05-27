package sc.plugin2026

import sc.api.plugins.Direction
import sc.api.plugins.Team
import sc.framework.plugins.Constants
import sc.plugin2026.util.PiranhaConstants

class GameRuleLogic private constructor() {
    init {
        throw java.lang.IllegalStateException("Can't be instantiated.")
    }
    
    companion object {
        /**
         * Gibt eine Liste aller möglichen Züge zurück
         *
         * @return Liste von Move Objekten
         */
        fun getPossibleMoves(state: sc.plugin2026.GameState): java.util.ArrayList<Move> {
            val possibleMoves = java.util.ArrayList<Move>()
            val fields: Collection<Field> = getOwnFields(state.board, state.getCurrentTeam())
            for(field in fields) {
                for(direction in Direction.values()) {
                    val x = field.x
                    val y = field.y
                    val dist = calculateMoveDistance(state.board, x, y, direction)
                    try {
                        if(dist > 0 && isValidToMove(state, x, y, direction, dist)) {
                            val m = Move(x, y, direction)
                            //TODO ((Player.getPosition), Destination)
                            possibleMoves.add(m)
                        }
                    } catch(ignore: InvalidMoveException) {
                        /**TODO PiranhaMoveMistake for InvalidMoveException? because InvalidMoveException
                        * is missing in skd shared
                         */
                    }
                }
            }
            return possibleMoves
        }
        
        @Throws(InvalidMoveException::class)
        fun isValidToMove(
            state: sc.plugin2019.GameState,
            x: Int,
            y: Int,
            direction: Direction,
            distance: Int
        ): Boolean {
            if(x >= PiranhaConstants.BOARD_LENGTH || y >= PiranhaConstants.BOARD_LENGTH || x < 0 || y < 0) throw InvalidMoveException("x or y are not within the field range")
            val board = state.board
            val curField: Field = board.getField(x, y)
            val curFieldPlayer: java.util.Optional<Team> = curField.piranha
            if(!curFieldPlayer.isPresent() || curFieldPlayer.get() !== state.getCurrentTeam()) {
                throw InvalidMoveException("Field does not belong to the current player")
            }
            
            
            if(calculateMoveDistance(
                    board,
                    x,
                    y,
                    direction
                ) != distance
            ) throw InvalidMoveException("Move distance was incorrect")
            
            val nextField: Field
            try {
                nextField = getFieldInDirection(board, x, y, direction, distance)
            } catch(e: ArrayIndexOutOfBoundsException) {
                throw InvalidMoveException("Move in that direction would not be on the board")
            }
            
            val fieldsInDirection = getFieldsInDirection(board, x, y, direction)
            
            val opponentFieldColor = FieldState.from(state.getCurrentTeam().opponent())
            
            for(f in fieldsInDirection) {
                if(f.state == opponentFieldColor) {
                    throw InvalidMoveException("Path to the new position is not clear")
                }
            }
            
            val nextFieldPlayer: java.util.Optional<Team> = nextField.piranha
            if(nextFieldPlayer.isPresent() && nextFieldPlayer.get() === state.getCurrentTeam()) {
                throw InvalidMoveException("Field obstructed with own piranha")
            }
            if(nextField.isObstructed) {
                throw InvalidMoveException("Field is obstructed")
            }
            return true
        }
        
        fun getOwnFields(board: Board, player: Team?): Set<Field> {
            val fields: MutableSet<Field> = java.util.HashSet()
            var size = 0
            var i = 0
            while(i < PiranhaConstants.BOARD_LENGTH && MAX_FISH > size) {
                var j = 0
                while(j < PiranhaConstants.BOARD_LENGTH && MAX_FISH > size) {
                    val curField: Field = board.getField(i, j)
                    if(curField.piranha.isPresent() && curField.piranha.get().equals(player)) {
                        fields.add(curField)
                        size++
                    }
                    j++
                }
                i++
            }
            return fields
        }
        
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
        
        /** Überprüft nicht, ob Feld innerhalb der Feldgrenzen  */
        fun getFieldInDirection(board: Board, x: Int, y: Int, direction: Direction, distance: Int): Field {
            val shift: Point = direction.shift()
            return board.getField(x + shift.getX() * distance, y + shift.getY() * distance)
        }
        
        fun getFieldsInDirection(board: Board, x: Int, y: Int, d: Direction): List<Field> {
            val distance = calculateMoveDistance(board, x, y, d)
            val fields: MutableList<Field> = java.util.ArrayList()
            val shift: Point = d.shift()
            
            for(i in 0 until distance) {
                fields.add(board.getField(x + shift.getX() * i, y + shift.getY() * i))
            }
            return fields
        }
        
        /**
         * Calculate the minimum steps to take from given position in given direction
         *
         * @param x         coordinate to calculate from
         * @param y         coordinate to calculate from
         * @param direction of the calcualtion
         *
         * @return -1 if Invalid move, else the steps to take
         */
        fun calculateMoveDistance(board: Board, x: Int, y: Int, direction: Direction): Int {
            when(direction) {
                LEFT, RIGHT -> return moveDistanceHorizontal(board, x, y)
                UP, DOWN -> return moveDistanceVertical(board, x, y)
                UP_RIGHT, DOWN_LEFT -> return moveDistanceDiagonalRising(board, x, y)
                DOWN_RIGHT, UP_LEFT -> return moveDistanceDiagonalFalling(board, x, y)
            }
            return -1
        }
        
        private fun moveDistanceHorizontal(board: Board, ignore: Int, y: Int): Int {
            var count = 0
            for(i in 0 until BOARD_SIZE) {
                if(board.getField(i, y).getPiranha().isPresent()) {
                    count++
                }
            }
            return count
        }
        
        private fun moveDistanceVertical(board: Board, x: Int, ignore: Int): Int {
            var count = 0
            for(i in 0 until BOARD_SIZE) {
                if(board.getField(x, i).getPiranha().isPresent()) {
                    count++
                }
            }
            return count
        }
        
        private fun moveDistanceDiagonalRising(board: Board, x: Int, y: Int): Int {
            var count = 0
            var cX = x
            var cY = y
            // Move down left
            while(cX >= 0 && cY >= 0) {
                if(board.getField(cX, cY).getPiranha().isPresent()) {
                    count++
                }
                cY--
                cX--
            }
            
            // Move up right
            cX = x + 1
            cY = y + 1
            while(cX < BOARD_SIZE && cY < BOARD_SIZE) {
                if(board.getField(cX, cY).getPiranha().isPresent()) {
                    count++
                }
                cY++
                cX++
            }
            return count
        }
        
        private fun moveDistanceDiagonalFalling(board: Board, x: Int, y: Int): Int {
            var count = 0
            var cX = x
            var cY = y
            // Move down left
            while(cX < BOARD_SIZE && cY >= 0) {
                if(board.getField(cX, cY).getPiranha().isPresent()) {
                    count++
                }
                cY--
                cX++
            }
            
            // Move up right
            cX = x - 1
            cY = y + 1
            while(cX >= 0 && cY < BOARD_SIZE) {
                if(board.getField(cX, cY).getPiranha().isPresent()) {
                    count++
                }
                cY++
                cX--
            }
            return count
        }
    }
}
