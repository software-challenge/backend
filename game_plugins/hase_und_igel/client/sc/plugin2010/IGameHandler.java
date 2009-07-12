package sc.plugin2010;

public interface IGameHandler
{
	void onUpdate(Player player, boolean own);

	void onUpdate(Board board, int round);

	void onRequestAction();

	void sendAction(Move move);
}
