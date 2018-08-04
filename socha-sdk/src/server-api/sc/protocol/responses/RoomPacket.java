package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.protocol.requests.ILobbyRequest;

/** Wrapper Class containing Room ID and data */
@XStreamAlias("room")
public final class RoomPacket implements ILobbyRequest {
  
  @XStreamAsAttribute
  private String roomId;

  private ProtocolMessage data;

  /** might be needed by XStream */
  public RoomPacket() {
  }

  public RoomPacket(String roomId, ProtocolMessage o) {
    this.roomId = roomId;
    this.data = o;
  }

  public String getRoomId() {
    return this.roomId;
  }

  public ProtocolMessage getData() {
    return this.data;
  }

  @Override
  public String toString() {
    return String.format("RoomPacket{roomId=%s, data=%s}", roomId, data);
  }
}
