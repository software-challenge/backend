package sc.plugin2020;

import sc.framework.plugins.Player;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

/**
 * Ein allgemeines Interface für spielfähige Klassen.
 * Eine Spielstrategie / Logik muss dieses Interface implementieren.
 *
 * @author sven
 */
public interface IGameHandler {
  /**
   * wird aufgerufen, wenn Spieler aktualisiert werden
   *
   * @param player      eigener Spieler
   * @param otherPlayer anderer Spieler
   */
  void onUpdate(Player player, Player otherPlayer);

  /** wird aufgerufen, wenn sich das Spielbrett aendert. */
  void onUpdate(GameState gameState);

  /** wird aufgerufen, wenn der Spieler zum Zug aufgefordert wurde. */
  void onRequestAction();

  /**
   * sendet dem Spielserver den übergebenen Zug
   *
   * @param move zu taetigender Zug
   */
  void sendAction(Move move);

  /**
   * aufgerufen, wenn das Spiel beendet ist.
   *
   * @param data         mit getScores() kann man die Punkte erfragen
   * @param color        Playercolor
   * @param errorMessage Fehlernachricht
   */
  void gameEnded(GameResult data, PlayerColor color, String errorMessage);
}
