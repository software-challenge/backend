package sc.protocol;

public interface ILobbyClientListener
{

	void onNewState(String roomId, Object state);

	void onError(ErrorResponse error);

	void onRoomMessage(String roomId, Object data);

}
