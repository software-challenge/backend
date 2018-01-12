/**
 *
 */
package sc.plugin2017.gui;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.networking.clients.IControllableGame;
import sc.plugin2017.GuiClient;
import sc.protocol.helpers.RequestResult;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.shared.SlotDescriptor;
import sc.networking.clients.ControllingClient;

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

  private static final Logger logger = LoggerFactory.getLogger(GamePreparation.class);
  
	public GamePreparation(GuiClient client, SlotDescriptor... descriptors) {
		RequestResult<PrepareGameProtocolMessage> results = null;
		try {
			results = client.prepareGameAndWait(descriptors);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		PrepareGameProtocolMessage response = results.getResult();

		for (String singleResp : response.getReservations()) {
			slots.add(new Slot(singleResp, client));
		}
		logger.debug("Gamepreparation response has roomId: {}", response.getRoomId());
		IControllableGame conGame = client.observeAndControl(response);
		logger.debug("Gamepreparation conGame has roomId: {}", ((ControllingClient)conGame).roomId);
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
