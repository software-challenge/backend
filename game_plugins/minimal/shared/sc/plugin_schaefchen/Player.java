package sc.plugin_schaefchen;



import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * ein spieler
 * 
 * @author sca, tkra
 * 
 */
@XStreamAlias(value = "sit:player")
public final class Player extends SimplePlayer {

	// spielerfarbe des spielers
	@XStreamAsAttribute
	private PlayerColor playerId;

	// gesamtzahl der von diesem spieler gesicherten blumen
	@XStreamAsAttribute
	private int munchedFlowers;
	
	// gesamtzahl der von diesem spieler gesicherten gegnerischen schafe
	@XStreamAsAttribute
	private int stolenSheeps;


	public Player() {
		munchedFlowers = 0;
		stolenSheeps = 0;
	}

	public Player(final PlayerColor color) {
		this();
		this.playerId = color;
	}

	/**
	 * liefert die spielrfarbe dieses spielers
	 */
	public PlayerColor getPlayerColor() {
		return playerId;
	}

	/**
	 * liefert die gesamtzahl der von diesem spieler gesicherten blumen
	 */
	public int getMunchedFlowers() {
		return munchedFlowers;
	}

	/**
	 * erhoeht die gesamtzahl der von diesem spielr gesicherten blumen
	 */
	protected void munchFlowers(int flowers) {
		this.munchedFlowers += flowers;
	}

	/**
	 * liefert die gesamtzahl der von diesem spieler gesicherten  schafe
	 */
	public int getStolenSheeps() {
		return stolenSheeps;
	}

	/**
	 * erhoeht die gesamtzahl der von diesem spieler gesicherten schafe
	 */
	protected void stealSheeps(int sheeps) {
		this.stolenSheeps += sheeps;
	}

}