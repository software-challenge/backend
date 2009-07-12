package sc.protocol;

public interface IHistoryListener
{
	public void onNewState(String roomId, Object o);

	public void onGameOver(String roomId, Object o);
}
