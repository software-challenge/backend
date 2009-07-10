package sc.plugin2010.framework;

import sc.plugin2010.Board;
import sc.plugin2010.BoardUpdated;
import sc.plugin2010.Player;

/**
 * @author ffi
 * 
 */
public class Spielbrett
{
	private Board	brett;

	private int		runde;

	public Spielbrett()
	{

	}

	/**
	 * interne Methode um das Spielbrett zu aktualisieren
	 * 
	 * @param board
	 */
	public void update(BoardUpdated bu)
	{
		brett = bu.getBoard();
		runde = bu.getRound();
	}

	public SpielfeldTyp holeSpielfeldType(final int feldNummer)
	{
		switch (brett.getTypeAt(feldNummer))
		{
			case SALAD:
				return SpielfeldTyp.SALAT;
			case CARROT:
				return SpielfeldTyp.KAROTTEN;
			case RABBIT:
				return SpielfeldTyp.HASE;
			case HEDGEHOG:
				return SpielfeldTyp.IGEL;
			case POSITION_1:
				return SpielfeldTyp.POSITION_1;
			case POSITION_2:
				return SpielfeldTyp.POSITION_2;
			case INVALID:
				return SpielfeldTyp.INVALIDE;
			case GOAL:
				return SpielfeldTyp.ZIEL;
			case START:
				return SpielfeldTyp.START;
			default:
				return SpielfeldTyp.INVALIDE;
		}
	}

	public boolean istSpielfeldBesetzt(final int feldNummer)
	{
		return (brett.getPlayerAt(feldNummer) != null);
	}

	public boolean stehtGegnerAufFeld(final int feldNummer, Gegner gegner)
	{
		if (brett.getPlayerAt(feldNummer).getColor() == Player.FigureColor.BLUE
				&& gegner.holeFarbe() == Farbe.BLAU)
		{ // TODO
			return true;
		}
		else
		{
			return false;
		}
	}

	public int holeRunde()
	{
		return runde;
	}
}
