package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.IMove

@XStreamAlias("move")
/**
 * Represents a move in a game.
 *
 * A move consists of a list of actions.
 *
 * @property actions The list of actions in the move.
 */
data class Move(
        @XStreamImplicit
        val actions: List<Action>,
): IMove, Comparable<Move> {
    
    constructor(vararg actions: Action) : this(actions.asList())
    
    /**
     * Compares this Move instance with the specified Move for order.
     *
     * The comparison is based on the size of the actions list.
     * The Move with a smaller size will be considered smaller,
     * the Move with a larger size will be considered larger,
     * and if the sizes are equal, the Moves are considered equal.
     *
     * @param other the Move to be compared.
     * @return a negative integer if this Move is smaller than the specified Move,
     *         zero if they are equal in length,
     *         or a positive integer if this Move is larger than the specified Move.
     */
    override fun compareTo(other: Move): Int =
            actions.size.compareTo(other.actions.size)
    
    /** @return true if the specified object is a Move and contains the same actions as this move, false otherwise */
    override fun equals(other: Any?): Boolean = other is Move && actions == other.actions
    
    override fun hashCode(): Int = actions.hashCode()
    
    override fun toString(): String =
            actions.joinToString(separator = ", ", prefix = "Zug[", postfix = "]")
    
}