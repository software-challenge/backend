package sc.plugin_schaefchen;



import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ein spieler
 * 
 * @author sca, tkra
 * 
 */
@XStreamAlias(value = "sit:player")
public final class Player extends SimplePlayer {

	// spielerfarbe des spielers
	private PlayerColor color;

	// gesamtzahl der von diesem spieler gesicherten blumen
	private int capturedFlowers;
	
	// gesamtzahl der von diesem spieler gesicherten gegnerischen schafe
	private int capturedSheeps;

	public Player() {
		capturedFlowers = 0;
		capturedSheeps = 0;
	}

	public Player(final PlayerColor color) {
		this();
		this.color = color;
	}

	/**
	 * liefert die spielrfarbe dieses spielers
	 */
	public PlayerColor getPlayerColor() {
		return color;
	}

	/**
	 * liefert die gesamtzahl der von diesem spieler gesicherten blumen
	 */
	public int getCapturedFlowers() {
		return capturedFlowers;
	}

	/**
	 * erhoeht die gesamtzahl der von diesem spielr gesicherten blumen
	 */
	public void addCapturedFlowers(int flowers) {
		this.capturedFlowers += flowers;
	}

	/**
	 * liefert die gesamtzahl der von diesem spieler gesicherten  schafe
	 */
	public int getCapturedSheeps() {
		return capturedSheeps;
	}

	/**
	 * erhoeht die gesamtzahl der von diesem spieler gesicherten schafe
	 */
	public void addCapturedSheeps(int sheeps) {
		this.capturedSheeps += sheeps;
	}

	
	public Move getLastMove() {
		// TODO wird im client scheinbar gebraucht
		return null;
	}
}