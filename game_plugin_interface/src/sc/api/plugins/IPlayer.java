package sc.api.plugins;

import sc.api.plugins.host.IPlayerListener;


public interface IPlayer
{
	public void addPlayerListener(IPlayerListener listener);
	public void removePlayerListener(IPlayerListener listener);
}
