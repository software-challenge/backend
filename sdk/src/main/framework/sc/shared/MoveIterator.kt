package sc.shared

import sc.api.plugins.IGameState
import sc.api.plugins.IMove

class MoveIterator<Move: IMove, Action, GameState: IGameState>(state: GameState, val makeMove: (List<Action>) -> Move, val process: (GameState, Move) -> List<Action>): Iterator<Move> {
    val queue = ArrayDeque<Pair<GameState, List<Action>>>(64)
    
    init {
        queue.add(state to listOf())
    }
    
    fun process(): List<Action> {
        val (state, move) = queue.removeFirst()
        process()
        return move
    }
    
    fun findNext() {
        while(queue.isNotEmpty())
            process()
    }
    
    override fun hasNext(): Boolean {
        findNext()
        return queue.isNotEmpty()
    }
    
    override fun next(): Move {
        findNext()
        return makeMove(process())
    }
}