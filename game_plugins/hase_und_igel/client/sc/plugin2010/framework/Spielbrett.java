package sc.plugin2010.framework;

import sc.plugin2010.Board;
import sc.plugin2010.Player.FigureColor;

/**
 * Das Spielbrett in einem Hase und Igel Spiel.
 * 
 * @author ffi
 * 
 */
public class Spielbrett
{
	// interne Referenzen um einfachen Zugriff zu gewähren, ohne die Referenzen
	// jedes mal zu übergeben
	private Board	brett;
	private Spieler	spieler;
	private Spieler	gegner;

	// die aktuelle Rundenanzahl
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
	 *            das aktualisierte SpielBrett
	 */
	protected void update(Board board, int round)
	{
		brett = board;
		runde = round;
	}

	/**
	 * Kann der Spieler <code>spieler</code> das Ziel betreten? Dies ist der
	 * Fall, wenn er alle Salat gefressen hat und höchstens 10 Karotten hat
	 * 
	 * @param spieler
	 *            zu prüfender Spieler
	 * @return true, falls der Spieler ins Ziel kann, sonst false
	 */
	public boolean kannSpielerInsZiel(Spieler spieler)
	{
		return brett.canEnterGoal(spieler.getPlayer());
	}

	/**
	 * Ist der Spieler <code>spieler</code> an erster Position?
	 * 
	 * @param spieler
	 *            zu prüfender Spieler
	 * @return true, falls <code>spieler</code> an erster Position ist, sonst
	 *         false
	 */
	public boolean istErster(Spieler spieler)
	{
		return brett.isFirst(spieler.getPlayer());

	}

	/**
	 * Darf der Spieler <code>spieler</code> auf das Feld
	 * <code>feldNummer</code> ziehen?
	 * 
	 * @param spieler
	 *            der zu prüfende Spieler
	 * @param feldNummer
	 *            die absolute Feldnummer auf die der Spieler ziehen möchte
	 * @return true, falls der Spieler dies darf, sonst false
	 */
	public boolean darfSpielerAufFeld(Spieler spieler, int feldNummer)
	{
		return brett.isMoveable(feldNummer, spieler.getPlayer());
	}

	/**
	 * Bestimmt das nächste freie Feld für den Spieler <code>spieler</code> mit
	 * dem Anfang <code>feldbegin</code>.
	 * 
	 * @param spieler
	 *            der zu prüfende Spieler
	 * @param feldbegin
	 *            der Anfang von wo aus die Suche gestartet werden soll
	 * @return die absolute Feldnummer des nächsten freien gefundenen Felds
	 */
	public int naechstesFreiesFeld(Spieler spieler, int feldbegin)
	{
		return brett.nextFreeFieldFor(spieler.getPlayer(), feldbegin);
	}

	/**
	 * Holt den Typ des Spielfeldes <code>feldNummer</code>.
	 * 
	 * @param feldNummer
	 *            die absolute Feldnummer des Feldes
	 * @return Typ des Feldes <code>feldNummer</code>
	 */
	public Spielfeldtyp holeSpielfeldtyp(final int feldNummer)
	{
		return Werkzeuge.convertFieldtype(brett.getTypeAt(feldNummer));
	}

	/**
	 * Prüft, ob das Spielfeld mit der Nummer <code>feldNummer</code> besetzt
	 * ist.
	 * 
	 * @param feldNummer
	 *            absolute Feldnummer
	 * @return true, wenn es besetzt ist, sonst false
	 */
	public boolean istSpielfeldBesetzt(final int feldNummer)
	{
		return (brett.getPlayerAt(feldNummer) != null);
	}

	/**
	 * Holt das nächste Spielfeld vom Typ <code>typ</code>.
	 * 
	 * @param typ
	 *            Der Typ, welcher gesucht werden soll
	 * @param startFeldNummer
	 *            ab welchem Startfeld gesucht werden soll
	 * @return absolute Feldnummer des gefundenen Feldes, falls es nicht
	 *         gefunden dann -1
	 */
	public int holeNaechstesSpielfeldNachTyp(Spielfeldtyp typ,
			int startFeldNummer)
	{
		return brett.getNextFieldByTyp(Werkzeuge.convertSpielfeldtyp(typ),
				startFeldNummer);
	}

	/**
	 * Holt das vorherige Spielfeld vom Typ <code>typ</code>.
	 * 
	 * @param typ
	 *            Der Typ, welcher gesucht werden soll
	 * @param startFeldNummer
	 *            ab welchem Startfeld gesucht werden soll
	 * @return absolute Feldnummer des gefundenen Feldes, falls es nicht
	 *         gefunden dann -1
	 */
	public int holeVorherigesSpielfeldNachTyp(Spielfeldtyp typ,
			int startFeldNummer)
	{
		return brett.getPreviousFieldByTyp(Werkzeuge.convertSpielfeldtyp(typ),
				startFeldNummer);
	}

	/**
	 * Holt den Spieler, welcher auf dem Feld <code>feldNummer</code> steht.
	 * 
	 * @param feldNummer
	 *            absolute Feldnummer des Feldes
	 * @return Der Spieler, welcher auf dem Spielfeld steht, wenn keiner darauf
	 *         steht, dann wird <code>null</code> gegeben
	 */
	public Spieler holeSpielerAufFeld(int feldNummer)
	{
		if (feldNummer < 0 && feldNummer > 64)
		{
			throw new IllegalArgumentException();
		}

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

		return null;
	}

	/**
	 * Holt die aktuelle Runde des Spieles
	 * 
	 * @return Rundenanzahl
	 */
	public int holeRunde()
	{
		return runde;
	}

	/**
	 * holt die interne Referenz
	 * 
	 * @return
	 */
	protected Board getBoard()
	{
		return brett;
	}
}
