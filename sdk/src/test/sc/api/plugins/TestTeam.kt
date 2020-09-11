package sc.api.plugins

object TestTeam : ITeam {
    override val index = 0
    override fun opponent() = this
    override fun toString() = "BLUE"
}