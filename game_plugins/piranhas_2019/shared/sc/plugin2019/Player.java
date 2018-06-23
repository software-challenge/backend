package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import sc.framework.plugins.SimplePlayer;
import sc.shared.PlayerColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Ein Spieler aus Hase- und Igel.
 * 
 */
@XStreamAlias(value = "player")
public class Player extends SimplePlayer implements Cloneable {

  /** Farbe der Spielfiguren */
	@XStreamAsAttribute
	private PlayerColor color;

	/** only for XStream */
	protected Player() {}

	protected Player(PlayerColor color) {
		this.color = color;
	}

	public final PlayerColor getPlayerColor() {
		return color;
	}

	/**
	 * Nur für den Server relevant. Setzt Spielerfarbe des Spielers.
	 * @param playerColor Spielerfarbe
	 */
	public void setPlayerColor(PlayerColor playerColor) {
		this.color = playerColor;
	}

	/**
	 * Erzeugt eine deepcopy dieses Spielers
	 * @return Klon des Spielers
	 */
	public Player clone() {
		Player clone = new Player(color);
		return clone;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(String.format("Player %s (color: %s)\n", getDisplayName(), color));
		return res.toString();
	}
  
}
