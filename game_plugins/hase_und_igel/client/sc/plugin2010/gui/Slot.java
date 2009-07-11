/**
 * 
 */
package sc.plugin2010.gui;

import java.io.IOException;

import sc.guiplugin.interfaces.ISlot;
import sc.plugin2010.Client;
import sc.plugin2010.EPlayerId;
import sc.plugin2010.renderer.RenderFacade;

/**
 * @author ffi
 * 
 */
public class Slot implements ISlot
{
	private String	roomId;
	private String	reservation;
	private Client	adminclient;

	public Slot(String roomId, String reservation, Client adminclient)
	{
		this.reservation = reservation;
		this.roomId = roomId;
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
		Client humanClient = new Client(adminclient.getHost(), adminclient
				.getPort(), EPlayerId.PLAYER_ONE);
		GUIGameHandler handler = new GUIGameHandler(humanClient);
		humanClient.setHandler(handler);
		RenderFacade.getInstance().createPanel(true, handler);
	}
}
