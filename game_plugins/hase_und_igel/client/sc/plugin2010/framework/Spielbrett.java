package sc.plugin2010.framework;

import sc.plugin2010.Board;

/**
 * @author ffi
 * 
 */
public class Spielbrett
{
	private Board	brett;

	public Spielbrett()
	{

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
		}

		return null;
	}

	public boolean istSpielfeldBesetzt(final int feldNummer)
	{
		return (brett.getPlayerAt(feldNummer) != null);
	}

	public Spieler werStehtAufFeld(final int feldNummer)
	{
		// return brett.getPlayerAt(feldNummer); // TODO
		return null;
	}
}
