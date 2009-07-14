/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.Board;
import sc.plugin2010.Player;

/**
 * @author ffi
 * 
 */
public interface IGameUpdateObserver
{
	public void zugAngefordert();

	public void spiellbrettAktualisiert(Board board, int round);

	public void spielerAktualisiert(Player player, Player otherPlayer);

	public void spielBeendet(String[] statistik, boolean abgebrochen);
}
