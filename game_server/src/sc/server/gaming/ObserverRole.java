package sc.server.gaming;

import sc.server.network.Client;
import sc.server.network.IClientRole;

public class ObserverRole implements IClientRole
{

	public ObserverRole(Client owner)
	{

	}

	@Override
	public void onClientDisconnected(Client source)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequest(Client source, Object packet)
	{
		// TODO Auto-generated method stub
		
	}

}
