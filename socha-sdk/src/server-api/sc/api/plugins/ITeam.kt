package sc.api.plugins

/** This represents the team a player is in / is playing for.
 *  Concrete implementations belong into the corresponding plugin. */
interface ITeam<out T> {
    val index: Int
    
    fun opponent(): T
    override fun toString(): String
}
