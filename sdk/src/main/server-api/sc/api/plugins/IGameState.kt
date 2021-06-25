package sc.api.plugins

import sc.protocol.room.RoomMessage

/**
 * Ein `GameState` beinhaltet alle Informationen, die den Spielstand zu
 * einem gegebenen Zeitpunkt, das heisst zwischen zwei Spielzuegen, beschreiben.
 * Dies umfasst eine fortlaufende Zugnummer ([round] & [turn]), die
 * der Spielserver als Antwort von einem der beiden Spieler erwartet.
 * Weiterhin gehoeren die Informationen ueber die beiden Spieler und das Spielfeld
 * zum Zustand. Zuseatzlich wird ueber den zuletzt getaetigeten Spielzung und ggf.
 * ueber das Spielende informiert.
 *
 * Der `GameState` ist damit das zentrale Objekt ueber das auf alle
 * wesentlichen Informationen des aktuellen Spiels zugegriffen werden kann.
 *
 * Der Spielserver sendet an beide teilnehmenden Spieler nach jedem getaetigten
 * Zug eine neue Kopie des `GameState`, in dem der dann aktuelle Zustand
 * beschrieben wird. Informationen ueber den Spielverlauf sind nur bedingt ueber
 * den `GameState` erfragbar und muessen von einem Spielclient daher bei
 * Bedarf selbst mitgeschrieben werden.
 *
 * Zusaetzlich zu den eigentlichen Informationen koennen bestimmte
 * Teilinformationen abgefragt werden.
 *
 * @author Niklas, Sören, Janek
 */
interface IGameState : RoomMessage, Cloneable {
    /** Aktuelle Zugzahl  */
    val turn: Int

    /** Aktuelle Rundenzahl */
    val round: Int
    
    /** Das Team am Zug. */
    val currentTeam: ITeam
    
    /** Die möglichen Züge des aktuellen Teams in der aktuellen Situation. */
    val possibleMoves: Collection<IMove>
    
    /** Eine tiefe Kopie des Status.. */
    public override fun clone(): IGameState
}