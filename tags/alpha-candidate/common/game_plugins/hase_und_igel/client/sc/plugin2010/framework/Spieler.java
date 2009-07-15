/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.Move;
import sc.plugin2010.Player;

/**
 * @author ffi
 * 
 */
public class Spieler extends AllgemeinerSpieler
{
	private Logik	logik;

	public Spieler()
	{

	}

	public void spieleHasenjoker(Hasenjoker joker, int karottenAnzahl)
	{
		switch (joker)
		{
			case FALLE_ZURUECK:
				logik.sendAction(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.FALL_BACK));
				break;
			case FRISS_SALAT:
				logik.sendAction(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.EAT_SALAD));
				break;
			case NIMM_ODER_GIB_20_KAROTTEN:
				logik.sendAction(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.TAKE_OR_DROP_CARROTS, karottenAnzahl));
				break;
			case RUECKE_VOR:
				logik.sendAction(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.HURRY_AHEAD));
				break;
			default:
				break;
		}
	}

	public void spieleHasenjoker(Hasenjoker joker)
	{
		spieleHasenjoker(joker, 0);
	}

	/**
	 * 
	 */
	public void setzeAus()
	{
		logik.sendAction(new Move(Move.MoveTyp.SKIP));
	}

	public void setzeFigur(final int feldNummer)
	{
		if (feldNummer < 0 && feldNummer > 64)
		{
			throw new IllegalArgumentException();
		}

		logik.sendAction(new Move(Move.MoveTyp.MOVE, feldNummer
				- holeFeldnummer()));
	}

	public void frissSalat()
	{
		logik.sendAction(new Move(Move.MoveTyp.EAT));
	}

	public void zurueckAufLetztenIgel()
	{
		logik.sendAction(new Move(Move.MoveTyp.FALL_BACK));
	}

	public void gibKarottenAb()
	{
		logik.sendAction(new Move(Move.MoveTyp.TAKE_OR_DROP_CARROTS, -10));
	}

	public void nimmKarotten()
	{
		logik.sendAction(new Move(Move.MoveTyp.TAKE_OR_DROP_CARROTS, 10));
	}

	/**
	 * Die nicht Kara-Variante. Für erfahrenere Schüler.
	 * 
	 * @param zug
	 */
	public void sendeZug(Zug zug)
	{
		if (zug == null)
		{
			throw new IllegalArgumentException();
		}

		logik.sendAction(Werkzeuge.convertZug(zug));
	}

	/**
	 * @param logik
	 */
	protected void setLogik(Logik logik)
	{
		this.logik = logik;
	}
}
