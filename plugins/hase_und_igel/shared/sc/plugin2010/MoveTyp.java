package sc.plugin2010;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Die aus Hase- und Igel bekannten Aktionen, sowie die Erweiterungen der
 * CAU-Kiel
 */
@XStreamAlias(value="hui:moveType")
public enum MoveTyp
{
	/**
	 * Ziehe <code>n</code> Felder vor
	 */
	MOVE,
	/**
	 * Iß einen Salat - auf einem Salatfeld
	 */
	EAT,
	/**
	 * Nehme 10 Karotten auf oder gib 10 Karotten ab - auf einem Karottenfeld
	 */
	TAKE_OR_DROP_CARROTS,
	/**
	 * Falle zum letzten Igelfeld zurück.
	 */
	FALL_BACK,
	/**
	 * Spielt einen Hasenjoker aus - auf einem Hasenfeld
	 */
	PLAY_CARD,
	/**
	 * Im sehr seltenen Fall, wenn man sich gar nicht bewegen kann.
	 */
	SKIP
}
