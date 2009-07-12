package sc.plugin2010.framework;

import sc.plugin2010.Board;

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

	public boolean istZugValide(Zug zug, int feldNummer)
	{
		return false;
		// brett.isValid(move, player)
	}

	public boolean kannSpielerInsZiel()
	{
		return brett.canEnterGoal(spieler.getPlayer());
	}

	public boolean kannGegnerInsZiel()
	{
		return brett.canEnterGoal(gegner.getPlayer());
	}

	public Spielfeldtyp holeSpielfeldtyp(final int feldNummer)
	{
		return convertFieldtype(brett.getTypeAt(feldNummer));
	}

	public boolean istSpielfeldBesetzt(final int feldNummer)
	{
		return (brett.getPlayerAt(feldNummer) != null);
	}

	public int holeNaechstesSpielfeldNachTyp(Spielfeldtyp typ,
			int startFeldNummer)
	{
		return brett.getNextFieldByTyp(convertSpielfeldtyp(typ),
				startFeldNummer);
	}

	public boolean stehtGegnerAufFeld(final int feldNummer, Gegner gegner)
	{
		if (brett.getPlayerAt(feldNummer).getColor() == gegner.getPlayer()
				.getColor())
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

	// ///////////////////////////////////
	// einige interne Konvertierungen
	// ///////////////////////////////////

	private Board.FieldTyp convertSpielfeldtyp(Spielfeldtyp typ)
	{
		switch (typ)
		{
			case SALAT:
				return Board.FieldTyp.SALAD;
			case KAROTTEN:
				return Board.FieldTyp.CARROT;
			case HASE:
				return Board.FieldTyp.RABBIT;
			case IGEL:
				return Board.FieldTyp.HEDGEHOG;
			case POSITION_1:
				return Board.FieldTyp.POSITION_1;
			case POSITION_2:
				return Board.FieldTyp.POSITION_2;
			case INVALIDE:
				return Board.FieldTyp.INVALID;
			case ZIEL:
				return Board.FieldTyp.GOAL;
			case START:
				return Board.FieldTyp.START;
			default:
				return Board.FieldTyp.INVALID;
		}
	}

	private Spielfeldtyp convertFieldtype(Board.FieldTyp typ)
	{
		switch (typ)
		{
			case SALAD:
				return Spielfeldtyp.SALAT;
			case CARROT:
				return Spielfeldtyp.KAROTTEN;
			case RABBIT:
				return Spielfeldtyp.HASE;
			case HEDGEHOG:
				return Spielfeldtyp.IGEL;
			case POSITION_1:
				return Spielfeldtyp.POSITION_1;
			case POSITION_2:
				return Spielfeldtyp.POSITION_2;
			case INVALID:
				return Spielfeldtyp.INVALIDE;
			case GOAL:
				return Spielfeldtyp.ZIEL;
			case START:
				return Spielfeldtyp.START;
			default:
				return Spielfeldtyp.INVALIDE;
		}
	}
}
