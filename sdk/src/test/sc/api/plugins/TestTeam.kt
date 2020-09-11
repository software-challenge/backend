package sc.api.plugins

enum class TestTeam : ITeam {
    BLUE;
    override val index = 0
    override fun opponent() = this
}