package sc.plugin2010.framework;

import sc.plugin2010.Board;
import sc.plugin2010.Player;

/**
 * Dieses Interface dient zum Aktualisieren des SpielClients. Dieser
 * implementiert dieses Interface.
 * 
 * @author ffi
 * 
 */
public interface IGameUpdateObserver
{
	/**
	 * Methode, die aufgerufen wird, wenn ein Zug angefordert wurde
	 */
	public void zugAngefordert();

	/**
	 * Methode, die aufgerufen wird, wenn das Spielbrett aktualsiert wurde
	 * 
	 * @param board
	 *            das aktualisierte Spielbrett
	 * @param round
	 *            die aktuelle Runde des Spieles
	 */
	public void spiellbrettAktualisiert(Board board, int round);

	/**
	 * Methode, die aufgerufen wird, wenn die Spieler aktualsiert wurden
	 * 
	 * @param player
	 *            "eigener" aktualisierter Spieler
	 * @param otherPlayer
	 *            der aktualisierte Spieler des anderen
	 */
	public void spielerAktualisiert(Player player, Player otherPlayer);

	/**
	 * Methode, die aufgerufen wird, wenn das Spiel beendet wurde
	 * 
	 * @param statistik
	 *            die Resulte des Spiels (index 0 (Rot) und 2 (Blau) = gewinner
	 *            oder verlierer; index 1 (Rot) und 3 (Blau) die gemachten Züge)
	 * @param abgebrochen
	 *            wurde das Spiel nicht regulär beendet? (Zum Beispiel durch
	 *            invaliden Zug)
	 */
	public void spielBeendet(String[] statistik, boolean abgebrochen);
}
