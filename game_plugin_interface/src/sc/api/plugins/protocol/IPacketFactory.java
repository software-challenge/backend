package sc.api.plugins.protocol;

import java.nio.ByteBuffer;

public interface IPacketFactory
{
	public IPluginPacket deserialize(ByteBuffer data);
}
