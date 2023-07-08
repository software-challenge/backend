package sc.api.plugins

interface IPiece<FIELD : IField<FIELD>> {
    val id: Int
    val position: FIELD
    val team: ITeam
}