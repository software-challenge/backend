package sc.networking.clients;

public interface IPollsHistory
{
	public void start();
	
	public void addListener(IHistoryListener listener);

	public void removeListener(IHistoryListener listener);
}
