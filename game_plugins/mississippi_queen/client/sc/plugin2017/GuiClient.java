package sc.plugin2017;

import java.io.IOException;


public class GuiClient extends AbstractClient {
	public GuiClient(String host, int port, EPlayerId id) throws IOException {
		super(host, port, id);
	}

	private IGUIObservation obs;

	public void setObservation(IGUIObservation obs) {
		this.obs = obs;
	}

	public IGUIObservation getObservation() {
		return obs;
	}

	/**
	 * Called when game state has been received. Happens after a client made a
	 * move. We won't receive the actual move but the whole gameState of which
	 * we have to extract the opponent's last move.
	 */
	@Override
	public void onNewState(String roomId, Object state) {
		super.onNewState(roomId, state);

		if (obs != null) {

			if (!alreadyReady) {
				alreadyReady = true;
				obs.ready();
			}
		}
	}

  @Override
  public void onGameObserved(String roomId) {
    // is called when a observation request is acknowledged by the server
    // this is a newly added method, I am not sure if it fits into the architecture
  }

}
