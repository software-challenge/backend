package sc.api.plugins.host;

import sc.protocol.room.RoomMessage;

public interface IPlayerListener {
  void onPlayerEvent(RoomMessage request);
}
