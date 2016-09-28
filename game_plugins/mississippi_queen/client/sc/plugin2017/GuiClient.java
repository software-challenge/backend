package sc.plugin2017;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;


public class GuiClient extends AbstractClient {
	public GuiClient(String host, int port, EPlayerId id) throws IOException {
		super(host, port, id);
		// NOTE that the set is ordered by declaration order of the enums, not order
		// of adding them to the set.
		availablePlayerIds = new TreeSet<>();
		availablePlayerIds.add(EPlayerId.PLAYER_ONE);
		availablePlayerIds.add(EPlayerId.PLAYER_TWO);
	}

	private IGUIObservation obs;
	private SortedSet<EPlayerId> availablePlayerIds;

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

  /**
   * Used when this GuiClient is an administrative client.
   * @return player id which should be attached to the other client representing human players.
   */
	public EPlayerId claimNextHumanPlayerId() {
	  if (availablePlayerIds.isEmpty()) {
	    throw new RuntimeException("No more player ids left! Did you try to add more than two human players?");
	  }
	  EPlayerId next = availablePlayerIds.first();
	  availablePlayerIds.remove(next);
	  return next;
	}
}
