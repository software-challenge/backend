package sc.guiplugin.interfaces;

import java.util.List;

public interface IGamePreparation {

	/**
	 * Returns the slots of a prepared game. The size of the slots is the number
	 * of players passed to the prepareGame()-Method.
	 * 
	 * @return
	 */
	List<ISlot> getSlots();

	/**
	 * Returns the observation object to control a game.
	 * 
	 * @return
	 */
	IObservation getObserver();
}
