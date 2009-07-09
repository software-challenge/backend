package sc.guiplugin.interfaces;

public interface ISlot {

	/**
	 * Reserves the slot for a human player.
	 */
	void asHuman();

	/**
	 * Reserves the slot for a client.
	 * 
	 * @return the necessary command-line arguments to be passed
	 */
	String[] asClient();
}
