/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.Player;

/**
 * @author ffi
 * 
 */
public class Spieler
{
	private Player	player;

	/**
	 * die Hasen Karten, welche man im Spiel bekommen kann
	 * 
	 */
	public enum HasenKarte
	{
		AUSSETZEN, NIMM_GIB_20_KAROTTEN, FRISS_SALAT, FALLE_ZURUECK,
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
		return player.getCarrots();
	}

	public int holeSpielerNummer()
	{
		return 0; // TODO
	}

	public int holeAktuellesFeld()
	{
		return player.getPosition(); // TODO
	}

	public void setzeFigur(final int feldNummer)
	{

	}

	public void frissSalat()
	{

	}

	public void zurueckAufLetztenIgel()
	{

	}

	public void gibKarottenAb()
	{

	}

	public void nimmKarotten()
	{

	}

	public HasenKarte holeNeusteHasenKarte() // TODO
	{
		return HasenKarte.FRISS_SALAT;
	}
}
