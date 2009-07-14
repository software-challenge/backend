/**
 * 
 */
package sc.plugin2010;

import java.io.IOException;

import sc.guiplugin.interfaces.ISlot;
import sc.plugin2010.gui.GUIGameHandler;
import sc.plugin2010.renderer.RenderFacade;

/**
 * @author ffi
 * 
 */
public class Slot implements ISlot
{
	private String	reservation;
	private Client	adminclient;

	public Slot(String reservation, Client adminclient)
	{
		this.reservation = reservation;
		this.adminclient = adminclient;
	}

	@Override
	public String[] asClient()
	{
		String[] res = { "--host " + adminclient.getHost(),
				"--port " + String.valueOf(adminclient.getPort()),
				"--reservation " + reservation };
		return res;
	}

	@Override
	public void asHuman() throws IOException
	{
		Client humanClient;
		if (!RenderFacade.getInstance().getAlreadyCreatedPlayerOne())
		{
			humanClient = new Client(adminclient.getHost(), adminclient
					.getPort(), EPlayerId.PLAYER_ONE);
		}
		else
		{
			humanClient = new Client(adminclient.getHost(), adminclient
					.getPort(), EPlayerId.PLAYER_TWO);
		}

		GUIGameHandler handler = new GUIGameHandler(humanClient);
		humanClient.setHandler(handler);
		RenderFacade.getInstance().createPanel(handler, humanClient.getID());
		humanClient.joinPreparedGame(reservation);
	}

	@Override
	public void asRemote()
	{
		adminclient.freeReservation(reservation);
	}
}
