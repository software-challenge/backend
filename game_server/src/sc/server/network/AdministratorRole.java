package sc.server.network;

public class AdministratorRole implements IClientRole
{
	private Client	client;

	public AdministratorRole(Client client)
	{
		this.client = client;
	}

	@Override
	public void onClientDisconnected(Client source)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequest(Client source, PacketCallback callback)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Client getClient()
	{
		return this.client;
	}
}
