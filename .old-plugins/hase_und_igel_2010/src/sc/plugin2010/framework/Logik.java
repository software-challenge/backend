package sc.plugin2010.framework;

import sc.plugin2010.Board;
import sc.plugin2010.Client;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
import sc.shared.GameResult;

/**
 * Die Logik Klasse, welche zum Weiterleiten der von Netzwerk empfangenen
 * Nachrichten dient.
 * 
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
	public void onUpdate(final Player player, final Player otherPlayer)
	{
		obs.spielerAktualisiert(player, otherPlayer);
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

	@Override
	public void gameEnded(GameResult data, sc.plugin2010.FigureColor color, String errorMessage)
	{
		String[] redResults = data.getScores().get(0).toStrings();
		String[] blueResults = data.getScores().get(1).toStrings();

		String[] stats = { redResults[0], redResults[1], blueResults[0],
				blueResults[1] };

		Boolean reg = !data.isRegular();
		obs.spielBeendet(stats, reg);
	}
}
