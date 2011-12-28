/**
 * 
 */
package sc.plugin2013.gui;

import java.io.IOException;

import sc.guiplugin.interfaces.ISlot;
import sc.plugin2013.EPlayerId;
import sc.plugin2013.GuiClient;
import sc.plugin2013.gui.renderer.RenderFacade;

/**
 * Holds a place for a potential client (that can be a remote client or a human
 * client i.e.)
 * 
 * @author ffi
 * 
 */
public class Slot implements ISlot {
	private String reservation;
	private GuiClient adminclient;

	public Slot(String reservation, GuiClient adminclient) {
		this.reservation = reservation;
		this.adminclient = adminclient;
	}

	/**
	 * Local client, started by server
	 */
	@Override
	public String[] asClient() {
		String[] res = { "--host", adminclient.getHost(), "--port",
				String.valueOf(adminclient.getPort()), "--reservation",
				reservation };
		return res;
	}

	/**
	 * Create new human client and new Panel on the Facade
	 */
	@Override
	public void asHuman() throws IOException {
		GuiClient humanClient;
		if (!RenderFacade.getInstance().getAlreadyCreatedPlayerOne()) {
			humanClient = new GuiClient(adminclient.getHost(), adminclient
					.getPort(), EPlayerId.PLAYER_ONE);
		} else {
			humanClient = new GuiClient(adminclient.getHost(), adminclient
					.getPort(), EPlayerId.PLAYER_TWO);
		}

		HumanGameHandler handler = new HumanGameHandler(humanClient);
		humanClient.setHandler(handler);
		if (humanClient.getID() == EPlayerId.PLAYER_ONE) {
			RenderFacade.getInstance()
					.setHandler(handler, humanClient.getID());
		} else {
			RenderFacade.getInstance()
					.setHandler(handler, humanClient.getID());
		}
		humanClient.joinPreparedGame(reservation);
	}

	/**
	 * Remote client, has to connect to the server manually
	 */
	@Override
	public void asRemote() {
		adminclient.freeReservation(reservation);
	}
}
