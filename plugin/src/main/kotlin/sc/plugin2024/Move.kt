package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IMove
import sc.plugin2024.actions.Push

@XStreamAlias("move")
/** Ein Spielzug. */
data class Move(
        val actions: ArrayList<Action?>,
): IMove, Comparable<Move> {

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
     *         zero if they are equal, or a positive integer if this Move is larger than the specified Move.
     */
    override fun compareTo(other: Move): Int =
            actions.size.compareTo(other.actions.size)

    /**
     * Returns a string representation of the object.
     *
     * @return The string representation of the object in the format "Move(action1, action2, ..., actionN)".
     */
    override fun toString(): String =
                actions.joinToString(separator = ", ", prefix = "Move(", postfix = ")")

    /**
     * Compares this move with the specified object to check if they are equal.
     *
     * @param o the object to compare with this move
     * @return true if the specified object is a Move and contains the same actions as this move, false otherwise
     */
    override fun equals(o: Any?): Boolean {
        if (o is Move) {
            for (action in o.actions) {
                if (!actions.contains(action)) {
                    return false
                }
            }
            for (action in actions) {
                if (!o.actions.contains(action)) {
                    return false
                }
            }
            return true
        }
        return false
    }

    /**
     * Check if the list of actions contains any Push action.
     *
     * @return true if the list of actions contains a Push action, false otherwise.
     */
    fun containsPushAction(): Boolean {
        for (action in actions) {
            if (action!!.javaClass == Push::class.java) {
                return true
            }
        }
        return false
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return the hash code value for this object.
     */
    override fun hashCode(): Int {
        return actions.hashCode()
    }
}