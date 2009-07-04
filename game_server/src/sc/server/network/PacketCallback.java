package sc.server.network;

public class PacketCallback
{
	private Object	packet;
	private boolean	processed	= false;

	public PacketCallback(Object packet)
	{
		this.packet = packet;
	}

	public Object getPacket()
	{
		return packet;
	}

	public boolean isProcessed()
	{
		return processed;
	}

	public void setProcessed()
	{
		this.processed = true;
	}
}
