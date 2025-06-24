/**
 *
 */
package sc.plugin2017.gui;

import java.io.IOException;

import sc.guiplugin.interfaces.ISlot;
import sc.plugin2017.EPlayerId;
import sc.plugin2017.GuiClient;
import sc.plugin2017.gui.renderer.RenderFacade;

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
		EPlayerId nextPlayerId = adminclient.claimNextHumanPlayerId();
		humanClient = new GuiClient(adminclient.getHost(), adminclient.getPort(), nextPlayerId);
		HumanGameHandler handler = new HumanGameHandler(humanClient);
		humanClient.setHandler(handler);
		RenderFacade.getInstance().setHandler(handler, humanClient.getID());
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
