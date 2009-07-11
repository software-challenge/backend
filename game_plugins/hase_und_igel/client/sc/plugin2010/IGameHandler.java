package sc.plugin2010;

public interface IGameHandler
{
	void onUpdate(BoardUpdated bu);

	void onUpdate(PlayerUpdated pu);

	void onRequestAction();

	void sendAction(Move move);
}
