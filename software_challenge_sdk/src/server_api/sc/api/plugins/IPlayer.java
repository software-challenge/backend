package sc.api.plugins;

import sc.api.plugins.host.IPlayerListener;


public interface IPlayer
{
	public void addPlayerListener(IPlayerListener listener);
	public void removePlayerListener(IPlayerListener listener);
	public void setDisplayName(String displayName);
	public void setShouldBePaused(boolean shouldBePaused);
	public void setCanTimeout(boolean canTimeout);
	public void setViolated(boolean violated);
	public boolean hasViolated();
}
