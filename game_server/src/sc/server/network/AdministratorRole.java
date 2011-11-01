package sc.server.network;

public class AdministratorRole implements IClientRole
{
	private Client	client;

	public AdministratorRole(Client client)
	{
		this.client = client;
	}

	@Override
	public Client getClient()
	{
		return this.client;
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
	}
}
