package sc.api.plugins;

public interface IGameListener
{
	public void onGameOver();
	public void onPlayerLeft(IPlayer player);
	public void onPlayerJoined(IPlayer player);
}
