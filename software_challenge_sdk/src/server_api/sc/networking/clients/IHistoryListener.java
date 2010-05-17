package sc.networking.clients;

import sc.protocol.responses.ErrorResponse;
import sc.shared.GameResult;

public interface IHistoryListener
{
	public void onNewState(String roomId, Object o);

	public void onGameOver(String roomId, GameResult o);
	
	public void onGameError(String roomId, ErrorResponse error);
}
