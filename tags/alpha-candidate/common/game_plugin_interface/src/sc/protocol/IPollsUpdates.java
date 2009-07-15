package sc.protocol;

import sc.protocol.clients.IUpdateListener;

public interface IPollsUpdates
{
	void addListener(IUpdateListener u);

	void removeListener(IUpdateListener u);
}
