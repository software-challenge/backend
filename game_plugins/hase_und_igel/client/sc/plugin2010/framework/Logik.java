/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.BoardUpdated;
import sc.plugin2010.Client;
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
	private Client				client;

	public Logik(IGameUpdateObserver obs)
	{
		this.obs = obs;
	}

	@Override
	public void onUpdate(final BoardUpdated bu)
	{
		obs.spiellbrettAktualisiert(bu);
	}

	@Override
	public void onUpdate(final PlayerUpdated pu)
	{
		obs.spielerAktualisiert(pu);
	}

	@Override
	public void onRequestAction()
	{
		obs.zugAngefordert();
	}

	@Override
	public void sendAction(Move move)
	{
		client.sendMove(move);
	}
}
