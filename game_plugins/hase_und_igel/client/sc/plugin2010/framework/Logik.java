/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.Board;
import sc.plugin2010.Client;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.Player;

/**
 * @author ffi
 * 
 */
public class Logik implements IGameHandler
{
	private IGameUpdateObserver	obs;
	private Client				client;

	public Logik(IGameUpdateObserver obs, Client client)
	{
		this.client = client;
		this.obs = obs;
	}

	@Override
	public void onUpdate(final Board board, int round)
	{
		obs.spiellbrettAktualisiert(board, round);
	}

	@Override
	public void onUpdate(final Player player, boolean own)
	{
		obs.spielerAktualisiert(player, own);
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
