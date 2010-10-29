package sc.plugin2011;

import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * ein spieler. er wird durch seine spielerfarbe identifiziert und weiss wie
 * viele schafe seine schafe gestohlen haben und wie viele blumen seine schafe
 * gefressen haben.
 * 
 * @author sca, tkra
 * 
 */
@XStreamAlias(value = "sit:player")
public final class Player extends SimplePlayer {

	// spielerfarbe des spielers
	@XStreamAsAttribute
	private PlayerColor color;

	// gesamtzahl der von diesem spieler gesicherten blumen
	@XStreamAsAttribute
	private int munchedFlowers;

	// gesamtzahl der von diesem spieler gesicherten gegnerischen schafe
	@XStreamAsAttribute
	private int stolenSheeps;

	/**
	 * einen neuen spieler erstellen und ihm eine spielerfarbe zuweisen
	 * 
	 * @param color seine spielerfarbe
	 */
	public Player(final PlayerColor color) {
		munchedFlowers = 0;
		stolenSheeps = 0;
		this.color = color;
	}


	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Player) && ((Player) obj).color == this.color;
	}

	/**
	 * liefert die spielerfarbe dieses spielers
	 */
	public PlayerColor getPlayerColor() {
		return color;
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
	 * liefert die gesamtzahl der von diesem spieler gesicherten schafe
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