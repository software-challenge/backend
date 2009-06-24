package edu.cau.sc.plugin.protocol;

import java.nio.ByteBuffer;

import sc.api.plugins.protocol.IPluginPacket;


public abstract class AbstractPacket implements IPluginPacket
{
	@Override
	public ByteBuffer serialize()
	{
		return Serializer.serialize(this);
	}
}
