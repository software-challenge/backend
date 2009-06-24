package sc.server.gaming;

import sc.server.network.Client;
import sc.server.network.IClientRole;

public class ObserverRole implements IClientRole
{

	public ObserverRole(Client owner)
	{

	}

    @Override
    public void setClient(Client client) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
