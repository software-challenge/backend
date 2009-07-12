package sc.protocol;

public interface IPollsHistory
{
	public void start();
	
	public void addListener(IHistoryListener listener);

	public void removeListener(IHistoryListener listener);
}
