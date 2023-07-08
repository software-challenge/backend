package sc.api.plugins

interface IPiece {
    val id: Int
    val position: Coordinates
    val team: ITeam
}