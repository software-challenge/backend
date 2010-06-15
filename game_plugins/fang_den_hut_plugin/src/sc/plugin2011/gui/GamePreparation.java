package sc.plugin2011.gui;

import java.util.LinkedList;
import java.util.List;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.networking.clients.IControllableGame;
import sc.plugin2011.gui.Observation;
import sc.plugin2011.GuiClient;
import sc.protocol.helpers.RequestResult;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.SlotDescriptor;

public class GamePreparation implements IGamePreparation {

	private Observation observation;
	private List<ISlot> slots = new LinkedList<ISlot>();
	
	/**
	 * Prepare clients and create slots for them. Setup the observation.
	 * @param client
	 * @param descriptors
	 */
	public GamePreparation(GuiClient client, SlotDescriptor... descriptors) {
		observation = new Observation();
	}
	
	@Override
	public IObservation getObserver() {
		return observation;
	}

	@Override
	public List<ISlot> getSlots() {
		return slots;
	}

}
