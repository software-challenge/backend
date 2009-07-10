/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.BoardUpdated;
import sc.plugin2010.PlayerUpdated;

/**
 * @author ffi
 * 
 */
public interface IGameUpdateObserver
{
	public void zugAngefordert();

	public void spiellbrettAktualisiert(BoardUpdated bu);

	public void spielerAktualisiert(PlayerUpdated bu);
}
