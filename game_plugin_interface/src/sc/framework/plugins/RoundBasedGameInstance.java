package sc.framework.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.RescueableClientException;

public abstract class RoundBasedGameInstance<P extends SimplePlayer> extends
		SimpleGameInstance<P>
{
	private static Logger	logger			= LoggerFactory
													.getLogger(RoundBasedGameInstance.class);
	private P				activePlayer	= null;

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
		this.activePlayer.requestMove();
	}
}
