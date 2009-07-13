package sc.plugin2010;

import sc.shared.GameResult;

public interface IGameHandler
{
	void onUpdate(Player player, boolean own);

	void onUpdate(Board board, int round);

	void onRequestAction();

	void sendAction(Move move);

	void gameEnded(GameResult data);
}
