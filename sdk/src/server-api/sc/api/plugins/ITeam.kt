package sc.api.plugins

/** This represents the team a player is in / is playing for.
 *  Concrete implementations belong into the corresponding plugin. */
interface ITeam {
    val index: Int
    val name: String
    
    fun opponent(): ITeam
}