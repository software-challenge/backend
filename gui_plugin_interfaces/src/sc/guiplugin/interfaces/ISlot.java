package sc.guiplugin.interfaces;

public interface ISlot {

	/**
	 * Reserves the slot for a human player.
	 */
	void asHuman();

	/**
	 * Reserves the slot for a client.
	 * 
	 * @return the necessary parameters to be passed
	 */
	String asClient();
}
