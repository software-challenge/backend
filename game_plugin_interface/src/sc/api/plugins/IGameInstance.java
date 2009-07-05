package sc.api.plugins;

public interface IGameInstance
{
	public IPlayer playerJoined() throws TooManyPlayersException;
	public void playerLeft(IPlayer player);
	public void actionReceived(IPlayer fromPlayer, Object data);
	public void destroy();
	public void addGameListener(IGameListener listener);
	public void removeGameListener(IGameListener listener);
	public void start();
}
