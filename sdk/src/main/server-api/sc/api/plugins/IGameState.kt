package sc.api.plugins

import sc.framework.PublicCloneable
import sc.protocol.room.RoomMessage

/**
 * Ein `GameState` beinhaltet alle Informationen,
 * die den Spielstand zu einem gegebenen Zeitpunkt,
 * das heisst zwischen zwei Spielzuegen, beschreiben.
 * Dies umfasst:
 * - eine fortlaufende Zugnummer ([round] & [turn]) und wer dran ist
 * - das Spielfeld
 * - der zuletzt getaetigte Spielzug
 *
 * Der `GameState` ist damit das zentrale Objekt
 * ueber das auf alle wesentlichen Informationen des aktuellen Spiels
 * zugegriffen werden kann.
 * Es bietet daher zur einfacheren Handhabung weitere Hilfen, wie:
 * - eine Methode, verfügbare Züge zu berechnen und Züge auszuführen
 * - Abfragen, ob es zu Ende sein sollte
 *
 * Der Spielserver sendet an beide teilnehmenden Spieler
 * nach jedem getaetigten Zug eine neue Kopie des `GameState`,
 * in dem der dann aktuelle Zustand beschrieben wird.
 * Informationen ueber den Spielverlauf sind nur bedingt ueber den `GameState` erfragbar
 * und muessen von einem Spielclient daher bei Bedarf selbst mitgeschrieben werden.
 *
 * Zusaetzlich zu den eigentlichen Informationen koennen bestimmte
 * Teilinformationen abgefragt werden.
 */
interface IGameState : RoomMessage, PublicCloneable<IGameState> {
    /** Aktuelle Zugzahl  */
    val turn: Int

    /** Aktuelle Rundenzahl */
    val round: Int
    
    /** Das Team am Zug. */
    val currentTeam: ITeam
    
    /** Ob das Spiel zu Ende ist. */
    val isOver: Boolean
    
    /** Gibt Punktzahlen des Teams entsprechend der [ScoreDefinition] zurück. */
    fun getPointsForTeam(team: ITeam): IntArray
    
    /** Die möglichen Züge des aktuellen Teams in der aktuellen Situation. */
    fun getPossibleOperations(): Collection<IMove>
}