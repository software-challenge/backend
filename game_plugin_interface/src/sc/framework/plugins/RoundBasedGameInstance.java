package sc.framework.plugins;

import sc.api.plugins.IPlayer;
import sc.api.plugins.RescueableClientException;

public abstract class RoundBasedGameInstance<P extends SimplePlayer> extends
		SimpleGameInstance<P>
{
	private P	activePlayer	= null;

	@Override
	public final void onAction(IPlayer fromPlayer, Object data) throws RescueableClientException
	{
		if (fromPlayer.equals(this.activePlayer))
		{
			onRoundBasedAction(fromPlayer, data);
		}
		else
		{
			throw new RuntimeException("aaa");
		}
	}

	protected abstract void onRoundBasedAction(IPlayer fromPlayer, Object data) throws RescueableClientException;

	protected abstract boolean checkGameOverCondition();

	@Override
	public void destroy()
	{
		// in most of the cases theres nothing to do
	}

	@Override
	public void start()
	{
		this.activePlayer = this.players.get(0);
		notifyActivePlayer();
	}

	protected void next()
	{
		int activePlayerId = this.players.indexOf(this.activePlayer);
		activePlayerId = (activePlayerId + 1) % this.players.size();
		next(this.players.get(activePlayerId));
	}

	protected void next(P nextPlayer)
	{
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

	public void notifyActivePlayer()
	{
		this.activePlayer.requestMove();
	}
}
