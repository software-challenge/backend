/**
 * 
 */
package sc.plugin2010.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sc.api.plugins.host.ReplayBuilder;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.networking.clients.IControllableGame;
import sc.networking.clients.IUpdateListener;
import sc.plugin2010.EPlayerId;
import sc.plugin2010.FigureColor;
import sc.plugin2010.Game;
import sc.plugin2010.GameState;
import sc.plugin2010.IGUIObservation;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.renderer.RenderFacade;
import sc.plugin2010.util.Configuration;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class Observation implements IObservation, IUpdateListener,
		IGUIObservation
{
	private IControllableGame			conGame;

	private IGameHandler				handler;

	private List<IGameEndedListener>	gameEndedListeners	= new LinkedList<IGameEndedListener>();
	private List<INewTurnListener>		newTurnListeners	= new LinkedList<INewTurnListener>();
	private List<IReadyListener>		readyListeners		= new LinkedList<IReadyListener>();

	private boolean						notifiedAboutEnd;

	public Observation(IControllableGame conGame, IGameHandler handler)
	{
		this.conGame = conGame;
		this.handler = handler;
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
		onGameEnded(this, conGame.getResult());
	}

	@Override
	public void next()
	{
		conGame.next();
		showActivePlayerIfNecessary();
	}

	private void showActivePlayerIfNecessary()
	{
		if (!conGame.hasNext())
		{
			if (RenderFacade.getInstance().getActivePlayer() != null)
			{
				RenderFacade.getInstance().switchToPlayer(
						RenderFacade.getInstance().getActivePlayer());
			}
		}
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
		ReplayBuilder.saveReplay(Configuration.getXStream(), conGame, filename);
	}

	@Override
	public void start()
	{
		conGame.unpause();
		if (RenderFacade.getInstance().getActivePlayer() != null)
		{
			RenderFacade.getInstance().switchToPlayer(
					RenderFacade.getInstance().getActivePlayer());
		}
	}

	@Override
	public void unpause()
	{
		RenderFacade.getInstance().switchToPlayer(
				RenderFacade.getInstance().getActivePlayer());
		conGame.unpause();
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

	private String createGameEndedString(GameResult data)
	{
		String result = "----------------\n";

		if (data == null)
		{
			result += "Leeres Spielresultat!";
			return result;
		}

		result += "Spielresultat:\n";

		GameState gameState = (GameState) conGame.getCurrentState();
		Game game = gameState.getGame();

		String name1 = "Spieler 1";
		String name2 = "Spieler 2";

		if (game.getActivePlayer().getColor() == FigureColor.RED)
		{
			name1 = game.getActivePlayer().getDisplayName();
			name2 = game.getBoard().getOtherPlayer(game.getActivePlayer())
					.getDisplayName();
		}
		else
		{
			name1 = game.getBoard().getOtherPlayer(game.getActivePlayer())
					.getDisplayName();
			name2 = game.getActivePlayer().getDisplayName();
		}

		String[] results = data.getScores().get(0).toStrings();
		if (results[0].equals("1"))
		{
			result += name1;
			result += ": Gewinner\n";
		}
		else if (results[0].equals("0"))
		{
			result += name1;
			result += ": Verlierer\n";
		}
		else
		{
			result += "Unentschieden\n";
		}

		result += name1 + ": erreichtes Feld: " + results[1] + "\n";

		result += name2;

		results = data.getScores().get(1).toStrings();
		if (results[0].equals("1"))
		{
			result += ": Gewinner\n";
		}
		else if (results[0].equals("0"))
		{
			result += ": Verlierer\n";
		}

		result += name2 + ": erreichtes Feld: " + results[1];

		return result;
	}

	/**
	 * @param data
	 * 
	 */
	@Override
	public void onGameEnded(Object sender, GameResult data)
	{
		if (!notifiedAboutEnd && !conGame.isReplay())
		{
			notifiedAboutEnd = true;

			for (IGameEndedListener list : gameEndedListeners)
			{
				list.gameEnded(data, createGameEndedString(data));
			}
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
			// ready();
			Game game = gameState.getGame();
			handler.onUpdate(game.getBoard(), game.getTurn());

			if (game.getActivePlayer().getColor() == FigureColor.RED)
			{
				// active player is own
				handler.onUpdate(game.getActivePlayer(), game.getBoard()
						.getOtherPlayer(game.getActivePlayer()));
			}
			else
			{
				// active player is the enemy
				handler.onUpdate(game.getBoard().getOtherPlayer(
						game.getActivePlayer()), game.getActivePlayer());

			}

			if (conGame.isGameOver() && conGame.isAtEnd())
			{
				handler.gameEnded(conGame.getResult());
			}

			if (conGame.hasNext())
			{
				RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
			}
		}

		for (INewTurnListener turnListener : newTurnListeners)
		{
			turnListener.newTurn(0, "");
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

	@Override
	public boolean isPaused()
	{
		return conGame.isPaused();
	}

	@Override
	public boolean isFinished()
	{
		return conGame.isGameOver();
	}

	@Override
	public boolean isAtEnd()
	{
		return conGame.isAtEnd();
	}

	@Override
	public boolean isAtStart()
	{
		return conGame.isAtStart();
	}

	@Override
	public void goToFirst()
	{
		conGame.goToFirst();
	}

	@Override
	public void goToLast()
	{
		conGame.goToLast();
		showActivePlayerIfNecessary();
	}

	@Override
	public boolean canTogglePause()
	{
		return conGame.canTogglePause();
	}
}
