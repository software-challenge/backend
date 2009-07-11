/**
 * 
 */
package sc.plugin2010.gui;

import java.util.LinkedList;
import java.util.List;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.plugin2010.Client;
import sc.protocol.RequestResult;
import sc.protocol.responses.PrepareGameResponse;

/**
 * @author ffi
 * 
 */
public class GamePreparation implements IGamePreparation
{
	private Client		administrativeClient;
	private List<ISlot>	slots	= new LinkedList<ISlot>();

	public GamePreparation(Client client, int playerCount)
	{
		administrativeClient = client;
		RequestResult<PrepareGameResponse> results = null;
		try
		{
			results = administrativeClient.prepareGameAndWait(playerCount);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		PrepareGameResponse response = results.getResult();

		for (String singleResp : response.getReservations())
		{
			slots.add(new Slot(singleResp, administrativeClient));
		}
	}

	@Override
	public List<ISlot> getSlots()
	{
		return slots;
	}

	@Override
	public IObservation getObserver()
	{
		return new GameObservation(administrativeClient);
	}
}
