package sc.framework.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

/**
 * XXX 
 * @param <P>
 */
public abstract class RoundBasedGameInstance<P extends SimplePlayer> implements IGameInstance
{
	private static Logger	logger				= LoggerFactory
														.getLogger(RoundBasedGameInstance.class);
	protected P				activePlayer		= null;

	private int				turn				= 0;

	@XStreamOmitField
	private boolean			paused				= false;

	@XStreamOmitField
	private Runnable		afterPauseAction	= null;

	@XStreamOmitField
	private Object			afterPauseLock		= new Object();

	@XStreamOmitField
	private ActionTimeout	requestTimeout		= null;
	
	@XStreamOmitField
	protected final List<IGameListener>	listeners	= new LinkedList<>();

	@XStreamImplicit(itemFieldName = "player")
	protected final List<P>				players		= new ArrayList<>();

	public int getTurn()
	{
		return this.turn;
	}

	/**
	 * Called by the Server once an action was received.
	 * 
	 * @param fromPlayer
	 *            The player who invoked this action.
	 * @param data
	 *            The plugin-secific data.
	 * @throws GameLogicException	if any invalid action is done, i.e. game rule violation
	 */
	public final void onAction(IPlayer fromPlayer, Object data)
			throws GameLogicException
	{
		if (fromPlayer.equals(this.activePlayer))
		{
			if (wasMoveRequested())
			{
				this.requestTimeout.stop();

				if (this.requestTimeout.didTimeout())
				{
					logger.warn("Client hit soft-timeout.");
					fromPlayer.setSoftTimeout(true);
					onPlayerLeft(fromPlayer, ScoreCause.SOFT_TIMEOUT);
				}
				else
				{
					onRoundBasedAction(fromPlayer, data);
				}
			}
			else
			{
				throw new GameLogicException(
						"We didn't request a move from you yet.");
			}
		}
		else
		{
			throw new GameLogicException("It's not your turn yet.");
		}
	}

	private boolean wasMoveRequested()
	{
		return this.requestTimeout != null;
	}

	protected abstract void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws GameLogicException;

	protected abstract boolean checkGameOverCondition();

	/**
	 * At any time this method might be invoked by the server. Any open handles
	 * should be removed. No events should be sent out (GameOver etc) after this
	 * method has been called.
	 */
	public void destroy()
	{
		logger.info("Destroying Game");

		if(this.requestTimeout != null)
		{
			this.requestTimeout.stop();
			this.requestTimeout = null;
		}
	}

	/**
	 * Server or an administrator requests the game to start now.
	 */
	public void start()
	{
		if (this.listeners.isEmpty())
		{
			logger.warn("Couldn't find any listeners. Is this intended?");
		}
	
		this.activePlayer = this.players.get(0);
		onActivePlayerChanged(this.activePlayer);
		notifyOnNewState(getCurrentState());
		notifyActivePlayer();
	}
	
	/**
	 * On violation player is removed forcefully, if player has not violated, he has left by himself (i.e. Exception)
	 * @param player
	 */
	public void onPlayerLeft(IPlayer player) {
	  if (!player.hasViolated()) {
	    player.setLeft(true);
	    onPlayerLeft(player, ScoreCause.LEFT);
	  } else {
	    onPlayerLeft(player, ScoreCause.RULE_VIOLATION);
	  }
	}
	
	/**
	 * XXX
	 * Handle leave of player
	 */
	public void onPlayerLeft(IPlayer player, ScoreCause cause) {
    Map<IPlayer, PlayerScore> res = generateScoreMap();

    for (Entry<IPlayer, PlayerScore> entry : res.entrySet()) {
      PlayerScore score = entry.getValue();

      if (entry.getKey() == player) {
        score.setCause(cause);
      }
    }

    notifyOnGameOver(res);
  }

	protected void onActivePlayerChanged(P newActivePlayer)
	{
		// optional callback
	}

	protected void next()
	{
		next(getPlayerAfter(this.activePlayer));
	}

	protected final P getPlayerAfter(P player)
	{
		return getPlayerAfter(player, 1);
	}

	protected final P getPlayerAfter(P player, int step)
	{
		int playerPos = this.players.indexOf(player);
		playerPos = (playerPos + step) % this.players.size();
		return this.players.get(playerPos);
	}

	protected final void next(P nextPlayer)
	{
		if (increaseTurnIfNecessary(nextPlayer))
		{
			this.turn++;

			// change player before calling new turn callback
			this.activePlayer = nextPlayer;
			onNewTurn();
		}
		logger.debug("next turn ({}) for player {}", this.turn, nextPlayer);

		this.activePlayer = nextPlayer;
		notifyOnNewState(getCurrentState());

		if (checkGameOverCondition())
		{
			notifyOnGameOver(generateScoreMap());
		}
		else
		{
			notifyActivePlayer();
		}
	}

	protected abstract PlayerScore getScoreFor(P p);

	protected boolean increaseTurnIfNecessary(P nextPlayer)
	{
		return (this.activePlayer != nextPlayer && this.players
				.indexOf(nextPlayer) == 0);
	}

	protected abstract void onNewTurn();

	/**
	 * Gets the current state representation.
	 *
	 * @return
	 */
	protected abstract Object getCurrentState();

	/**
	 * Notifies the active player that it's his/her time to make a move. If the
	 * game is paused, the request will be hold back.
	 */
	protected final void notifyActivePlayer()
	{
		final P currentActivePlayer = this.activePlayer;

		if (this.paused && currentActivePlayer.isShouldBePaused())
		{
			synchronized (this.afterPauseLock)
			{
				logger.debug("Setting AfterPauseAction");

				this.afterPauseAction = new Runnable() {
					@Override
					public void run()
					{
						requestMove(currentActivePlayer);
					}
				};

				for (IGameListener listener : this.listeners)
				{
					listener.onPaused(currentActivePlayer);
				}
			}
		}
		else
		{
			requestMove(currentActivePlayer);
		}
	}

	/**
	 * Sends a MoveRequest directly to the player (does not take PAUSE into
	 * account)
	 *
	 * @param player
	 */
	protected synchronized final void requestMove(P player)
	{
		final ActionTimeout timeout = player.isCanTimeout() ? getTimeoutFor(player)
				: new ActionTimeout(false);

		final Logger logger = RoundBasedGameInstance.logger;
		final P playerToTimeout = player;

    // Signal the JVM to do a GC run now and lower the propability that the GC
    // runs when the player sends back its move, resulting in disqualification
    // because of soft timeout.
    System.gc();

		this.requestTimeout = timeout;
		timeout.start(new Runnable() {
			@Override
			public void run()
			{
				logger.warn("Player {} reached the timeout of {}ms",
						playerToTimeout, timeout.getHardTimeout());
				playerToTimeout.setHardTimeout(true);
				onPlayerLeft(playerToTimeout, ScoreCause.HARD_TIMEOUT);
			}
		});

		player.requestMove();
	}

	protected ActionTimeout getTimeoutFor(P player)
	{
		return new ActionTimeout(true);
	}

	protected final boolean isPaused()
	{
		return this.paused;
	}

	/**
	 * XXX
	 */
	public void afterPause()
	{
		synchronized (this.afterPauseLock)
		{
			if (this.afterPauseAction == null)
			{
				logger
						.error("AfterPauseAction was null. Might cause a deadlock.");
			}
			else
			{
				Runnable action = this.afterPauseAction;
				this.afterPauseAction = null;
				action.run();
			}
		}
	}

	/**
	 * XXX Pauses game
	 * @param pause
	 */
	public void setPauseMode(boolean pause)
	{
		this.paused = pause;
	}

	protected Map<IPlayer, PlayerScore> generateScoreMap()
	{
		Map<IPlayer, PlayerScore> map = new HashMap<IPlayer, PlayerScore>();

		for (final P p : this.players)
		{
			map.put(p, getScoreFor(p));
		}

		return map;
	}
	
	// XXX methods from former SimpleGameInstance
	
	/**
	 * Extends the set of listeners.
	 * 
	 * @param listener
	 */
	public void addGameListener(IGameListener listener)
	{
		this.listeners.add(listener);
	}

	/**
	 * Removes listener XXX is this right/complete?
	 *
	 * @param listener
	 */
	public void removeGameListener(IGameListener listener)
	{
		this.listeners.remove(listener);
	}

	protected void notifyOnGameOver(Map<IPlayer, PlayerScore> map)
	{
		for (IGameListener listener : this.listeners)
		{
			try
			{
				listener.onGameOver(map);
			}
			catch (Exception e)
			{
				logger.error("GameOver Notification caused an exception.", e);
			}
		}
	}

	protected void notifyOnNewState(Object mementoState)
	{
		for (IGameListener listener : this.listeners)
		{
			logger.debug("notifying {} about new game state", listener);
			try
			{
				listener.onStateChanged(mementoState);
			}
			catch (Exception e)
			{
				logger.error("NewState Notification caused an exception.", e);
			}
		}
	}
}
