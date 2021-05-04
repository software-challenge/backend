package sc.api.plugins.host;


import sc.protocol.RoomMessage;

public interface IPlayerListener {
  void onPlayerEvent(RoomMessage request);
}
