package sc.api.plugins.protocol;

import java.nio.ByteBuffer;

/**
 * An interface for all the plugins the 
 */
public interface IPluginPacket
{
	public ByteBuffer serialize();
}
