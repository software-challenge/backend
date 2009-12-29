package sc.framework.plugins;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.host.IGameListener;
import sc.shared.PlayerScore;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public abstract class RoundBasedGameInstance<P extends SimplePlayer> extends
		SimpleGameInstance<P> implements IPauseable
{
	@SuppressWarnings("hiding")
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

	public int getTurn()
	{
		return this.turn;
	}

	@Override
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
					onPlayerLeft(fromPlayer);
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

	@Override
	public void destroy()
	{
		logger.info("Destroying Game");
		
		if(this.requestTimeout != null)
		{
			this.requestTimeout.stop();
		}
	}

	@Override
	public void start()
	{
		if (this.listeners.size() == 0)
		{
			logger.warn("Couldn't find any listeners. Is this intended?");
		}

		this.activePlayer = this.players.get(0);
		onActivePlayerChanged(this.activePlayer);
		notifyOnNewState(getCurrentState());
		notifyActivePlayer();
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
		boolean newTurn = false;
		if (increaseTurnIfNecessary(nextPlayer))
		{
			this.turn++;
			newTurn = true;
			// onNewTurn();
		}

		this.activePlayer = nextPlayer;
		if (newTurn)
		{
			onNewTurn();
		}
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

		this.requestTimeout = timeout;
		timeout.start(new Runnable() {
			@Override
			public void run()
			{
				logger.warn("Player {} reached the timeout of {}ms",
						playerToTimeout, timeout.getHardTimeout());
				onPlayerLeft(playerToTimeout);
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

	@Override
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

	@Override
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
}
