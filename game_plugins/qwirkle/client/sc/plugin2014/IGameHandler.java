package sc.plugin2014;

import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.PlayerColor;
import sc.plugin2014.moves.Move;
import sc.shared.GameResult;

/**
 * Ein allgemeines Interface fuer spielfaehige Klassen. <br/>
 * Eine Spielstrategie / Logik muss dieses Interface implementieren,
 * damit sie in der LogicFactory verwaltet werden kann.
 * 
 * @author sven
 * 
 */
public interface IGameHandler {
    /**
     * wird aufgerufen, wenn Spieler aktualisiert werden
     * 
     * @param player
     *            eigener Spieler
     * @param otherPlayer
     *            anderer Spieler
     */
    void onUpdate(Player player, Player otherPlayer);

    /**
     * wird aufgerufen, wenn sich das Spielbrett aendert.
     * 
     * @param gameState
     *            der Spielstatus
     */
    void onUpdate(GameState gameState);

    /**
     * wird aufgreufen, wenn der Spieler zum Zug aufgefordert wurde.
     */
    void onRequestAction();

    /**
     * sendet dem Spielserver den uebergebenen Zug
     * 
     * @param move
     *            zu taetigender Zug
     */
    void sendAction(Move move);

    /**
     * aufgerufen, wenn das Spiel beendet ist.
     * 
     * @param data
     *            mit getScores() kann man die Punkte erfragen
     */
    void gameEnded(GameResult data, PlayerColor color, String errorMessage);
}
