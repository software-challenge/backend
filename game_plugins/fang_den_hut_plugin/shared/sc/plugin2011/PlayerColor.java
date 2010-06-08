package sc.plugin2011;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Alle Spielfiguren aus dem Hase und Igel Original Mit Veränderungen der CAU
 */
@XStreamAlias(value="hui:color")
public enum PlayerColor
{
	/**
	 * Der erste Spieler ist immer rot 
	 */
	RED,
	/**
	 *  Der zweite Spieler ist immer blau
	 */
	BLUE
}
