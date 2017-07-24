package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Mögliche Aktionen, die durch das Ausspielen einer Karte ausgelöst
 * werden können.
 */
@XStreamAlias(value = "type")
public enum CardType
{
	/**
	 * Nehme Karotten auf, oder leg sie ab
	 */
	TAKE_OR_DROP_CARROTS,
	/**
	 * Iß sofort einen Salat
	 */
	EAT_SALAD,
	/**
	 * Falle eine Position zurück
	 */
	FALL_BACK,
	/**
	 * Rücke eine Position vor
	 */
	HURRY_AHEAD
}
