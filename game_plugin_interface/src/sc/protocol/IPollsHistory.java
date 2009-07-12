package sc.protocol;

public interface IPollsHistory
{
	public void addListener(IHistoryListener listener);

	public void removeListener(IHistoryListener listener);
}
