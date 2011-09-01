package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.protocol.requests.ILobbyRequest;

@XStreamAlias("room")
public final class RoomPacket implements ILobbyRequest
{
	@XStreamAsAttribute
	private String	roomId;
	
	private Object	data;

        /**
         * might be needed by XStream
         */
        public RoomPacket() {
        }

	public RoomPacket(String roomId, Object o)
	{
		this.roomId = roomId;
		this.data = o;
	}

	public String getRoomId()
	{
		return this.roomId;
	}

	public Object getData()
	{
		return this.data;
	}
}
