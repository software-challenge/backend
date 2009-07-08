/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.BoardUpdated;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.PlayerUpdated;

/**
 * @author ffi
 * 
 */
public class Logik implements IGameHandler
{
	private IGameUpdateObserver	obs;

	public Logik(IGameUpdateObserver obs)
	{

	}

	@Override
	public Move onAction()
	{
		obs.zugAngefordert();
		return null;// TODO
	}

	@Override
	public void onUpdate(final BoardUpdated bu)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(final PlayerUpdated pu)
	{
		// TODO Auto-generated method stub

	}

}
