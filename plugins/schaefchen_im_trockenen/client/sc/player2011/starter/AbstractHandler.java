package sc.player2011.starter;

import sc.plugin2011.AbstractClient;
import sc.plugin2011.IGameHandler;

public abstract class AbstractHandler implements IGameHandler {

	private AbstractClient client;

	public AbstractHandler() {

	}

	public void setClient(AbstractClient client) {
		this.client = client;
	}

	public AbstractClient getClient() {
		return client;
	}

}