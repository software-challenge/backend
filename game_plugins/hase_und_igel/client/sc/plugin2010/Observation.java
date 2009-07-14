/**
 * 
 */
package sc.plugin2010;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sc.api.plugins.host.ReplayBuilder;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.plugin2010.Player.FigureColor;
import sc.plugin2010.renderer.RenderFacade;
import sc.protocol.IControllableGame;
import sc.protocol.clients.IUpdateListener;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class Observation implements IObservation, IUpdateListener
{
	private IControllableGame			conGame;

	private IGameHandler				handler;

	private List<IGameEndedListener>	gameEndedListeners	= new LinkedList<IGameEndedListener>();
	private List<INewTurnListener>		newTurnListeners	= new LinkedList<INewTurnListener>();
	private List<IReadyListener>		readyListeners		= new LinkedList<IReadyListener>();

	public Observation(IControllableGame conGame, IGameHandler handler)
	{
		this.conGame = conGame;
		this.handler = handler;
		RenderFacade.getInstance().createPanel(null, EPlayerId.OBSERVER);
		conGame.addListener(this);
	}

	@Override
	public void addGameEndedListener(IGameEndedListener listener)
	{
		gameEndedListeners.add(listener);
	}

	@Override
	public void addNewTurnListener(INewTurnListener listener)
	{
		newTurnListeners.add(listener);
	}

	@Override
	public void addReadyListener(IReadyListener listener)
	{
		readyListeners.add(listener);
	}

	@Override
	public void back()
	{
		conGame.previous();
	}

	@Override
	public void cancel()
	{
		conGame.cancel();
	}

	@Override
	public void next()
	{
		conGame.next();
	}

	@Override
	public void pause()
	{
		conGame.pause();
	}

	@Override
	public void removeGameEndedListener(IGameEndedListener listener)
	{
		gameEndedListeners.remove(listener);
	}

	@Override
	public void removeNewTurnListener(INewTurnListener listener)
	{
		newTurnListeners.remove(listener);
	}

	@Override
	public void removeReadyListener(IReadyListener listener)
	{
		readyListeners.remove(listener);
	}

	@Override
	public void saveReplayToFile(String filename) throws IOException
	{
		ReplayBuilder.saveReplay(conGame, filename);
	}

	@Override
	public void start()
	{
		conGame.unpause();
	}

	@Override
	public void unpause()
	{
		conGame.unpause();
		RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
	}

	/**
	 * 
	 */
	public void ready()
	{
		for (IReadyListener list : readyListeners)
		{
			list.ready();
		}
	}

	/**
	 * @param data
	 * 
	 */
	public void gameEnded(GameResult data)
	{
		for (IGameEndedListener list : gameEndedListeners)
		{
			list.gameEnded(data);
		}
	}

	/**
	 * 
	 */
	public void newTurn(int id, String info)
	{
		for (INewTurnListener list : newTurnListeners)
		{
			list.newTurn(id, info);
		}
	}

	@Override
	public void onUpdate(Object sender)
	{
		assert sender == conGame;
		GameState gameState = (GameState) conGame.getCurrentState();

		if (gameState != null)
		{
			Game game = gameState.getGame();
			handler.onUpdate(game.getBoard(), game.getTurn());

			if (game.getActivePlayer().getColor() == FigureColor.RED)
			{ // active player is own
				handler.onUpdate(game.getActivePlayer(), game.getBoard()
						.getOtherPlayer(game.getActivePlayer()));
			}
			else
			// active player is the enemy
			{
				handler.onUpdate(game.getBoard().getOtherPlayer(
						game.getActivePlayer()), game.getActivePlayer());

			}
		}
	}

	@Override
	public boolean hasNext()
	{
		return conGame.hasNext();
	}

	@Override
	public boolean hasPrevious()
	{
		return conGame.hasPrevious();
	}
}
