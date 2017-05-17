package sc.networking.clients;

import sc.framework.plugins.SimplePlayer;
import sc.protocol.responses.ErrorResponse;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.GameResult;

public interface ILobbyClientListener
{

	void onNewState(String roomId, Object state);

	void onError(String roomId, ErrorResponse error);

	void onRoomMessage(String roomId, Object data);

	void onGamePrepared(PrepareGameResponse response);

	void onGameLeft(String roomId);

	void onGameJoined(String roomId);

	void onGameOver(String roomId, GameResult data);

	void onGamePaused(String roomId, SimplePlayer nextPlayer);

	void onGameObserved(String roomId);
}
