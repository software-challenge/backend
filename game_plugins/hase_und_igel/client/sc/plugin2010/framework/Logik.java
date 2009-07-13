/**
 * 
 */
package sc.plugin2010.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.plugin2010.Board;
import sc.plugin2010.Client;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class Logik implements IGameHandler
{
	private static final Logger	logger	= LoggerFactory.getLogger(Client.class);

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
		logger.info("board update");
	}

	@Override
	public void onUpdate(final Player player, boolean own)
	{
		obs.spielerAktualisiert(player, own);
		logger.info("player update");
	}

	@Override
	public void onRequestAction()
	{
		logger.info("zug anford");
		obs.zugAngefordert();
	}

	@Override
	public void sendAction(Move move)
	{
		client.sendMove(move);
	}

	@Override
	public void gameEnded(GameResult data)
	{
		// data.get TODO
		obs.spielBeendet();
	}
}
