package edu.cau.sc.plugin.protocol;

import java.nio.ByteBuffer;

import sc.api.plugins.protocol.IPacketFactory;
import sc.api.plugins.protocol.IPluginPacket;


public class PacketFactory implements IPacketFactory
{
	private static PacketFactory	instance;

	@Override
	public IPluginPacket deserialize(ByteBuffer data)
	{
		return Serializer.deserialize(data);
	}

	public static PacketFactory getInstance()
	{
		if (instance == null)
		{
			instance = new PacketFactory();
		}

		return instance;
	}
}
