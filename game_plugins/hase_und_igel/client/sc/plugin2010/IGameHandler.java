package sc.plugin2010;

public interface IGameHandler
{
	void onUpdate(BoardUpdated bu);

	void onUpdate(PlayerUpdated pu);

	void onRequestAction(String roomid);

	void sendAction(String action);
}
