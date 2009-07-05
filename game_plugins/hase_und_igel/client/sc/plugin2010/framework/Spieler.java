/**
 * 
 */
package sc.plugin2010.framework;

/**
 * @author ffi
 * 
 */
public class Spieler
{
	/**
	 * die Hasen Karten, welche man im Spiel bekommen kann
	 * 
	 */
	public enum HasenKarte
	{
		AUSSETZEN,
		NAECHSTES_HASEN_FELD,
		LETZTES_HASEN_FELD,
		ZIEHE_NOCH_EINMAL,
		LETZTER_ZUG_OHNE_KOSTEN,
		NIMM_10_KAROTTEN,
		GEBE_10_KAROTTEN_AB,
		FRISS_SALAT,
		FALLE_ZURUECK,
		/**
		 * Rücke eine Position vor
		 */
		RUECKE_VOR
	}

	public Spieler()
	{

	}

	public int holeKarottenAnzahl()
	{
		return 0; // TODO
	}

	public int holeSpielerNummer()
	{
		return 0; // TODO
	}

	public int holeAktuellesFeld()
	{
		return 0; // TODO
	}

	public int holeXXX()
	{
		return 0; // TODO
	}

	public void setzeFigur(int feldNummer)
	{

	}

	public void frissSalat()
	{

	}

	public void zurueckAufLetztenIgel()
	{

	}

	public void frissKarotten()
	{

	}

	public void nimmKarotten()
	{

	}

	public HasenKarte holeNeueHasenKarte()
	{
		return HasenKarte.FRISS_SALAT;
	}
}
