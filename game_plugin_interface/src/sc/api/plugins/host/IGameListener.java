package sc.api.plugins.host;

import java.util.Map;

import sc.api.plugins.IPlayer;

public interface IGameListener
{
	public void onGameOver(Map<IPlayer, IPlayerScore> results);
	public void onPlayerLeft(IPlayer player);
	public void onPlayerJoined(IPlayer player);
	public void onStateChanged(Object data);
}
