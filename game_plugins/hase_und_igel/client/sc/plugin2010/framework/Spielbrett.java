package sc.plugin2010.framework;

import sc.plugin2010.Board;
import sc.plugin2010.Player.FigureColor;

/**
 * @author ffi
 * 
 */
public class Spielbrett
{
	private Board	brett;
	private Spieler	spieler;
	private Spieler	gegner;

	private int		runde;

	public Spielbrett(Spieler spieler, Spieler gegner)
	{
		this.spieler = spieler;
		this.gegner = gegner;
	}

	/**
	 * interne Methode um das Spielbrett zu aktualisieren
	 * 
	 * @param board
	 */
	protected void update(Board board, int round)
	{
		brett = board;
		runde = round;
	}

	public boolean kannSpielerInsZiel()
	{
		return brett.canEnterGoal(spieler.getPlayer());
	}

	public boolean kannGegnerInsZiel()
	{
		return brett.canEnterGoal(gegner.getPlayer());

	}

	public boolean istErster(Spieler spieler)
	{
		return brett.isFirst(spieler.getPlayer());

	}

	public boolean darfSpielerAufFeld(Spieler spieler, int feldNummer)
	{
		return brett.isMoveable(feldNummer, spieler.getPlayer());
	}

	public int naechstesFreiesFeld(Spieler spieler, int feldbegin)
	{
		return brett.nextFreeFieldFor(spieler.getPlayer(), feldbegin);
	}

	public Spielfeldtyp holeSpielfeldtyp(final int feldNummer)
	{
		return Werkzeuge.convertFieldtype(brett.getTypeAt(feldNummer));
	}

	public boolean istSpielfeldBesetzt(final int feldNummer)
	{
		return (brett.getPlayerAt(feldNummer) != null);
	}

	public int holeNaechstesSpielfeldNachTyp(Spielfeldtyp typ,
			int startFeldNummer)
	{
		return brett.getNextFieldByTyp(Werkzeuge.convertSpielfeldtyp(typ),
				startFeldNummer);
	}

	public int holeVorherigesSpielfeldNachTyp(Spielfeldtyp typ,
			int startFeldNummer)
	{
		return brett.getPreviousFieldByTyp(Werkzeuge.convertSpielfeldtyp(typ),
				startFeldNummer);
	}

	public Spieler holeSpielerAufFeld(int feldNummer)
	{
		if (brett.getPlayerAt(feldNummer).getColor() == FigureColor.BLUE)
		{
			if (spieler.holeSpielerfarbe() == Spielerfarbe.BLAU)
			{
				return spieler;
			}
			else
			{
				return gegner;
			}
		}
		else if (brett.getPlayerAt(feldNummer).getColor() == FigureColor.RED)
		{
			if (spieler.holeSpielerfarbe() == Spielerfarbe.ROT)
			{
				return spieler;
			}
			else
			{
				return gegner;
			}
		}

		// not found TODO Exception IllegalArgument?
		return null;
	}

	public int holeRunde()
	{
		return runde;
	}

	protected Board getBoard()
	{
		return brett;
	}
}
