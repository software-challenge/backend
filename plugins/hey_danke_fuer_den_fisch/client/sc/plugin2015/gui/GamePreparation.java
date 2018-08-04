package sc.plugin2015.gui;

import java.util.LinkedList;
import java.util.List;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.networking.clients.IControllableGame;
import sc.plugin2015.GuiClient;
import sc.protocol.helpers.RequestResult;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.SlotDescriptor;

/**
 * A game preparation lets the client connect to the server and open a new game.
 * It then opens slots for actual clients
 * 
 * @author sca
 * 
 */
public class GamePreparation implements IGamePreparation {
	private List<ISlot> slots = new LinkedList<ISlot>();
	private Observation observation;

	public GamePreparation(GuiClient client, SlotDescriptor... descriptors) {
		RequestResult<PrepareGameResponse> results = null;
		try {
			results = client.prepareGameAndWait(descriptors);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		PrepareGameResponse response = results.getResult();

		for (String singleResp : response.getReservations()) {
			slots.add(new Slot(singleResp, client));
		}

		IControllableGame conGame = client.observeAndControl(response);
		ObserverGameHandler handler = new ObserverGameHandler();
		observation = new Observation(conGame, handler);
		client.setObservation(observation);
	}

	@Override
	public List<ISlot> getSlots() {
		return slots;
	}

	@Override
	public IObservation getObserver() {
		return observation;
	}
}
