/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.BoardUpdated;
import sc.plugin2010.IGameHandler;
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
		this.obs = obs;
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

	@Override
	public void onRequestAction(String roomid)
	{
		obs.zugAngefordert();
	}

	@Override
	public void sendAction(String action)
	{
		// TODO Auto-generated method stub

	}

}
