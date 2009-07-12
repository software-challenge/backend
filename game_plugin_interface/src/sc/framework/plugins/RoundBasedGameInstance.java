package sc.framework.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.RescueableClientException;

public abstract class RoundBasedGameInstance<P extends SimplePlayer> extends
		SimpleGameInstance<P> implements IPauseable
{
	private static Logger	logger				= LoggerFactory
														.getLogger(RoundBasedGameInstance.class);
	private P				activePlayer		= null;
	private boolean			paused				= false;
	private Runnable		afterPauseAction	= null;
	private Object			afterPauseLock		= new Object();

	@Override
	public final void onAction(IPlayer fromPlayer, Object data)
			throws RescueableClientException
	{
		if (fromPlayer.equals(this.activePlayer))
		{
			onRoundBasedAction(fromPlayer, data);
		}
		else
		{
			throw new RescueableClientException("It's not your turn yet.");
		}
	}

	protected abstract void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws RescueableClientException;

	protected abstract boolean checkGameOverCondition();

	@Override
	public void destroy()
	{
		// in most of the cases theres nothing to do
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
		int activePlayerId = this.players.indexOf(this.activePlayer);
		activePlayerId = (activePlayerId + 1) % this.players.size();
		next(this.players.get(activePlayerId));
	}

	protected void next(P nextPlayer)
	{
		notifyOnNewState(getCurrentState());

		this.activePlayer = nextPlayer;

		if (checkGameOverCondition())
		{
			notifyOnGameOver();
		}
		else
		{
			notifyActivePlayer();
		}
	}

	protected abstract Object getCurrentState();

	public void notifyActivePlayer()
	{
		final P currentActivePlayer = this.activePlayer;

		if (this.paused)
		{
			synchronized (this.afterPauseLock)
			{
				logger.debug("Setting AfterPauseAction");

				this.afterPauseAction = new Runnable() {
					@Override
					public void run()
					{
						currentActivePlayer.requestMove();
					}
				};
			}
		}
		else
		{
			currentActivePlayer.requestMove();
		}
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
}
