package sc.server.protocol;

public class JoinPreparedRoomRequest extends JoinRoomRequest
{
	private String authorizationKey;
	
	public JoinPreparedRoomRequest(String authorizationKey) {
		this.authorizationKey = authorizationKey;
	}
}
