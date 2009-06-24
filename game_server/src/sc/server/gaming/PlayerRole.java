package sc.server.gaming;

import sc.server.network.Client;
import sc.server.network.IClientRole;

public class PlayerRole implements IClientRole
{
	public PlayerRole(Client owner)
	{
		
	}

    @Override
    public void setClient(Client client) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
