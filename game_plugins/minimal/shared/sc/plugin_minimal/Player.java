package sc.plugin_minimal;

import java.util.LinkedList;
import java.util.List;

import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ein spieler
 * 
 * @author sca, tkra
 * 
 */
// FIXME: make Player a DAO to remove dependencies from ServerGameInterfaces lib
@XStreamAlias(value = "minimal:player")
public final class Player extends SimplePlayer {

	// spielerfarbe des spielers
	private PlayerColor color;
	
	// liste der schafe die dieser spieler besitzt
	private List<Sheep> sheeps;

	// gesamtzahl der von diesem spieler gesicherten blumen
	private int capturedFlowers;
	
	// gesamtzahl der von diesem spieler gesicherten gegnerischen schafe
	private int capturedSheeps;

	public Player() {
		capturedFlowers = 0;
		capturedSheeps = 0;
		sheeps = new LinkedList<Sheep>();
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
	 * liefert die spielerfarbe des gegners dieses spielers
	 */
	public PlayerColor getOponentColor() {
		PlayerColor result = PlayerColor.NOPLAYER;
		switch (color) {
		case PLAYER1:
			result = PlayerColor.PLAYER2;
			break;

		case PLAYER2:
			result = PlayerColor.PLAYER1;
			break;

		case NOPLAYER:
			result = PlayerColor.NOPLAYER;
			break;
		}

		return result;
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

	
	/**
	 * fuegt dem besitz dieses spielers eine neues schaf hinzu
	 */
	public void addSheep(final Sheep sheep) {
		sheeps.add(sheep);
	}

	/**
	 * entfernt ein schaf aus dem besitz dieses spielers
	 */
	public void removeSheep(final Sheep sheep) {
		sheeps.remove(sheep);
	}

	
	/**
	 * liefert die liste der zu diesem spieler gehoerenden schafe
	 */
	public List<Sheep> getHats() {
		return new LinkedList<Sheep>(sheeps);
	}

	
	public Move getLastMove() {
		// TODO wird im client scheinbar gebraucht
		return null;
	}
}
