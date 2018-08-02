package sc.plugin2011;

import java.io.IOException;

//
//import sc.plugin_schaefchen.Move;
//import sc.plugin_schaefchen.Player;
//import sc.plugin_schaefchen.PlayerColor;

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

	// /**
	// * Send the last turn made by player oldPlayer to the game observation
	// (not
	// * the room!)
	// *
	// * @param oldPlayer
	// * @param playerId
	// */
	// private void sendLastTurn(Player oldPlayer, int playerId) {
	// //Move move = oldPlayer.getLastMove();
	// // Move move = null;
	// // obs.newTurn(playerId, "nothing");
	// }

	/**
	 * Called when game state has been received Happens, after a client made a
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

}
