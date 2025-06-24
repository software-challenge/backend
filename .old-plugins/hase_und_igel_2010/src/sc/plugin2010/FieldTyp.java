package sc.plugin2010;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Die unterschiedlichen Spielfelder aus dem Hase und Igel Original
 */
@XStreamAlias(value = "hui:field")
public enum FieldTyp
{
	/**
	 * Zahl- und Flaggenfelder Die veränderten Spielregeln sehen nur noch die
	 * Felder 1,2 vor. Die Positionsfelder 3 und 4 wurden in Möhrenfelder
	 * umgewandelt, und (1,5,6) sind jetzt Position-1-Felder.
	 */
	POSITION_1, POSITION_2,
	/**
	 * Igelfeld
	 */
	HEDGEHOG,
	/**
	 * Salatfeld
	 */
	SALAD,
	/**
	 * Karottenfeld
	 */
	CARROT,
	/**
	 * Hasenfeld
	 */
	RABBIT,
	/**
	 * Außerhalb des Spielfeldes
	 */
	INVALID,
	/**
	 * Das Zielfeld
	 */
	GOAL,
	/**
	 * Das Startfeld
	 */
	START,
}
