/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.Board;

/**
 * @author ffi
 * 
 */
public interface IGameUpdateObserver
{
	public void zugAngefordert();

	public void spiellbrettAktualisiert(Board board);
}
