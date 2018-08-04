package sc.api.plugins.host;


import sc.protocol.responses.ProtocolMessage;

public interface IPlayerListener
{
	void onPlayerEvent(ProtocolMessage request);
}
