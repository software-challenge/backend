package sc.server.network;

public class PacketCallback
{
	private final Object	packet;
	private boolean			processed	= false;

	public PacketCallback(Object packet)
	{
		this.packet = packet;
	}

	public Object getPacket()
	{
		return this.packet;
	}

	public boolean isProcessed()
	{
		return this.processed;
	}

	public void setProcessed()
	{
		this.processed = true;
	}
}
