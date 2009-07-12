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

	private void internHasenjoker(Hasenjoker joker, int karrotenAnzahl)
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
						Player.Action.TAKE_OR_DROP_CARROTS, karrotenAnzahl));
				break;
			case RUECKE_VOR:
				logik.sendAction(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.HURRY_AHEAD));
				break;
			default:
				break;
		}
	}

	public void setzeHasenjoker(Hasenjoker joker, int karottenAnzahl)
	{
		internHasenjoker(joker, karottenAnzahl);
	}

	public void setzeHasenjoker(Hasenjoker joker)
	{
		internHasenjoker(joker, 0);
	}

	public void setzeFigur(final int feldNummer)
	{
		logik.sendAction(new Move(Move.MoveTyp.MOVE, feldNummer));
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
	 * @param logik
	 */
	protected void setLogik(Logik logik)
	{
		this.logik = logik;
	}
}
