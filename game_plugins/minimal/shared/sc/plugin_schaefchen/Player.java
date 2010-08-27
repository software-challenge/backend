package sc.plugin_schaefchen;

import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * ein spieler. er wird durch seine spielerfarbe identifiziert und weiss wie
 * viele schafe seine schafe gestohlen haben und wie viele blumen seine schafe
 * gefressen haben.
 * 
 * @author sca, tkra
 * 
 */
public final class Player extends SimplePlayer {

	private static Player player1 = new Player(PlayerColor.RED);
	private static Player player2 = new Player(PlayerColor.BLUE);

	// spielerfarbe des spielers
	@XStreamAsAttribute
	private PlayerColor color;

	// gesamtzahl der von diesem spieler gesicherten blumen
	@XStreamAsAttribute
	private int munchedFlowers;

	// gesamtzahl der von diesem spieler gesicherten gegnerischen schafe
	@XStreamAsAttribute
	private int stolenSheeps;

	/*
	 * einen neuen spieler erstellen und ihm eine spielerfarbe zuweisen
	 * 
	 * @param color seine spielerfarbe
	 */
	private Player(final PlayerColor color) {
		munchedFlowers = 0;
		stolenSheeps = 0;
		this.color = color;
	}

	/**
	 * liefert den spieler einer gegebenen spielerfarbe
	 * 
	 * @throws NullPointerException
	 *             wenn keine spielerfarbe angegebenwurde
	 */
	public static Player getPlayer(PlayerColor color)
			throws NullPointerException {
		if (color == null) {
			throw new NullPointerException("keine spielerfarbe angegeben");
		} else if (color == PlayerColor.RED) {
			return player1;

		} else {
			return player2;
		}
 
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