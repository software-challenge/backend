package sc.api.plugins;

import java.io.Serializable;

public interface IGameInstance
{
	public IPlayer playerJoined() throws TooManyPlayersException;

	public void playerLeft(IPlayer player);

	public void actionReceived(IPlayer fromPlayer, Serializable data);

	public void destroy();

	public void addGameListener(IGameListener listener);

	public void removeGameListener(IGameListener listener);
}
