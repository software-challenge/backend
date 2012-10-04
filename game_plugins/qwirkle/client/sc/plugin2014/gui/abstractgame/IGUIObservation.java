package sc.plugin2014.gui.abstractgame;

/**
 * @author ffi
 * 
 */
public interface IGUIObservation {

    /**
     * @param activePlayerId
     * @param actionFromOther
     */
    void newTurn(int activePlayerId, String actionFromOther);

    /**
	 * 
	 */
    void ready();

}
