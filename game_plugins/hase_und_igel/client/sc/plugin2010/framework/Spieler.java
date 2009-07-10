/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.Player;
import sc.plugin2010.PlayerUpdated;

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

	public boolean mussNaechsteRundeAussetzen()
	{
		return player.isSuspended();
	}

	public int holeKarottenAnzahl()
	{
		return player.getCarrotsAvailable();
	}

	public Farbe holeSpielerFarbe()
	{
		if (player.getColor() == Player.FigureColor.BLUE)
		{
			return Farbe.BLAU;
		}
		else
		{
			return Farbe.ROT;
		}
	}

	public int holeFeldnummer()
	{
		return player.getPosition();
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

	/**
	 * @param pu
	 */
	public void update(PlayerUpdated pu)
	{
		// TODO Auto-generated method stub

	}
}
