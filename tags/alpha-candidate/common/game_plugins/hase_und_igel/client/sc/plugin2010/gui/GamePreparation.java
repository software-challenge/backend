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
import sc.plugin2010.EPlayerId;
import sc.plugin2010.renderer.RenderFacade;
import sc.protocol.IControllableGame;
import sc.protocol.RequestResult;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.SlotDescriptor;

/**
 * @author ffi
 * 
 */
public class GamePreparation implements IGamePreparation
{
	private List<ISlot>	slots	= new LinkedList<ISlot>();
	private Observation	obs;

	public GamePreparation(Client client, SlotDescriptor... descriptors)
	{
		RequestResult<PrepareGameResponse> results = null;
		try
		{
			results = client.prepareGameAndWait(descriptors);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		PrepareGameResponse response = results.getResult();

		for (String singleResp : response.getReservations())
		{
			slots.add(new Slot(singleResp, client));
		}

		IControllableGame conGame = client.observeAndControl(response);
		ObserverGameHandler handler = new ObserverGameHandler();
		obs = new Observation(conGame, handler);
		client.setObservation(obs);
		RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
	}

	@Override
	public List<ISlot> getSlots()
	{
		return slots;
	}

	@Override
	public IObservation getObserver()
	{
		return obs;
	}
}
