/**
 * 
 */
package sc.plugin_minimal.gui;

import java.io.IOException;

import sc.guiplugin.interfaces.ISlot;
import sc.plugin_minimal.EPlayerId;
import sc.plugin_minimal.GuiClient;
import sc.plugin_minimal.renderer.RenderFacade;

/**
 * @author ffi
 * 
 */
public class Slot implements ISlot
{
	private String		reservation;
	private GuiClient	adminclient;

	public Slot(String reservation, GuiClient adminclient)
	{
		this.reservation = reservation;
		this.adminclient = adminclient;
	}

	@Override
	public String[] asClient()
	{
		String[] res = { "--host", adminclient.getHost(), "--port",
				String.valueOf(adminclient.getPort()), "--reservation",
				reservation };
		return res;
	}

	@Override
	public void asHuman() throws IOException
	{
		GuiClient humanClient;
		if (!RenderFacade.getInstance().getAlreadyCreatedPlayerOne())
		{
			humanClient = new GuiClient(adminclient.getHost(), adminclient
					.getPort(), EPlayerId.PLAYER_ONE);
		}
		else
		{
			humanClient = new GuiClient(adminclient.getHost(), adminclient
					.getPort(), EPlayerId.PLAYER_TWO);
		}

		HumanGameHandler handler = new HumanGameHandler(humanClient);
		humanClient.setHandler(handler);
		if (humanClient.getID() == EPlayerId.PLAYER_ONE)
		{
			RenderFacade.getInstance()
					.createPanel(handler, humanClient.getID());
		}
		else
		{
			RenderFacade.getInstance()
					.createPanel(handler, humanClient.getID());
		}
		humanClient.joinPreparedGame(reservation);
	}

	@Override
	public void asRemote()
	{
		adminclient.freeReservation(reservation);
	}
}
