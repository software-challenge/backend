package sc.plugin2010.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import sc.protocol.responses.ProtocolErrorMessage;
import sc.shared.GameResult;
import sc.shared.ScoreCause;

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

	private boolean						notifiedOnGameEnded	= false;

	private static final Logger			logger				= LoggerFactory
																	.getLogger(Observation.class);

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
		notifyOnGameEnded(this, conGame.getResult());
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

	public void ready()
	{
		for (IReadyListener list : readyListeners)
		{
			list.ready();
		}
	}

	@SuppressWarnings("unused")
	private String createGameEndedString(GameResult data)
	{
		String result = "----------------\n";

		if (data == null)
		{
			result += "Leeres Spielresultat!";
			return result;
		}
		
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
		
		if (conGame.getCurrentError() != null) {
			ProtocolErrorMessage error = (ProtocolErrorMessage) conGame.getCurrentError();
			result += (game.getActivePlayer().getColor() == FigureColor.RED ? name1 : name2);
			result += " hat einen Fehler gemacht: \n" + error.getMessage() + "\n";
		}

		result += "Spielresultat:\n";

		if (data.getScores().get(0).getCause() == ScoreCause.LEFT)
		{
			result += name1;
			result += " hat das Spiel verlassen!\n";
		}

		if (data.getScores().get(1).getCause() == ScoreCause.LEFT)
		{
			result += name2;
			result += " hat das Spiel verlassen!\n";
		}

		if (data.getScores().get(0).getCause() == ScoreCause.RULE_VIOLATION)
		{
			result += name1;
			result += " hat einen falschen Zug gesetzt!\n";
		}

		if (data.getScores().get(1).getCause() == ScoreCause.RULE_VIOLATION)
		{
			result += name2;
			result += " hat einen falschen Zug gesetzt!\n";
		}
		
		if (data.getScores().get(0).getCause() == ScoreCause.HARD_TIMEOUT)
		{
			result += name1;
			result += " hat das HardTimeout überschritten!\n";
		}
		
		if (data.getScores().get(1).getCause() == ScoreCause.HARD_TIMEOUT)
		{
			result += name2;
			result += " hat das HardTimeout überschritten!\n";
		}

		if (data.getScores().get(0).getCause() == ScoreCause.SOFT_TIMEOUT)
		{
			result += name1;
			result += " hat das SoftTimeout überschritten!\n";
		}
		
		if (data.getScores().get(1).getCause() == ScoreCause.SOFT_TIMEOUT)
		{
			result += name2;
			result += " hat das SoftTimeout überschritten!\n";
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
	private synchronized void notifyOnGameEnded(Object sender, GameResult data)
	{
		if (!notifiedOnGameEnded)
		{
			notifiedOnGameEnded = true;

			for (IGameEndedListener listener : gameEndedListeners)
			{
				try
				{
					listener.onGameEnded(data, null);
				}
				catch (Exception e)
				{
					logger.error("GameEnded Notification caused an exception.",
							e);
				}
			}
		}

		Object errorObject = conGame.getCurrentError();
		String errorMessage = null;
		if (errorObject != null) {
			errorMessage = ((ProtocolErrorMessage) errorObject).getMessage();
		}
		Object curStateObject = conGame.getCurrentState();
		FigureColor color = null;
		if (curStateObject != null) {
			GameState gameState = (GameState) curStateObject;
			color = gameState.getGame().getActivePlayer().getColor();
		}
		handler.gameEnded(data, color, errorMessage);
	}

	private void notifyOnNewTurn()
	{
		notifyOnNewTurn(0, "");
	}

	private void notifyOnNewTurn(int id, String info)
	{
		for (INewTurnListener listener : newTurnListeners)
		{
			try
			{
				listener.newTurn(id, info);
			}
			catch (Exception e)
			{
				logger.error("NewTurn Notification caused an exception.", e);
			}
		}
	}

	public void newTurn(int id, String info)
	{
		notifyOnNewTurn(id, info);
	}

	@Override
	public void onUpdate(Object sender)
	{
		assert sender == conGame;
		GameState gameState = (GameState) conGame.getCurrentState();
		Object errorObject = conGame.getCurrentError();
		if (errorObject != null) {
			ProtocolErrorMessage error = (ProtocolErrorMessage) errorObject;
			logger.info("Received error response: " + error);
			//RenderFacade.getInstance().gameError(error.getMessage());
			//RenderFacade.getInstance().getObserver().
			//notifyOnGameEnded(sender, conGame.getResult());
			/*//notifyOnGameEnded(sender, conGame.getResult());
			if (conGame.hasNext())
			{
				RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
			} else {
				logger.debug("Game ended");
				//notifyOnGameEnded(sender, conGame.getResult());
			}*/
		}
		
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
				//handler.onUpdate(game.getBoard().getOtherPlayer(
						//game.getActivePlayer()), game.getActivePlayer());
				handler.onUpdate(game.getActivePlayer(), game.getBoard()
						.getOtherPlayer(game.getActivePlayer()));

			}

			if (conGame.isGameOver() && conGame.isAtEnd())
			{
				notifyOnGameEnded(sender, conGame.getResult());
			}

			if (conGame.hasNext())
			{
				RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
			}
		}

		notifyOnNewTurn();
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

	@Override
	public void onError(String errorMessage)
	{
		RenderFacade.getInstance().gameError(errorMessage);
	}
	
	public void reset() {
		goToFirst();
		notifiedOnGameEnded = false;
	}
}
