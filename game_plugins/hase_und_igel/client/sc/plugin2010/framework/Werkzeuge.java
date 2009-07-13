package sc.plugin2010.framework;

import sc.plugin2010.Board;
import sc.plugin2010.GameUtil;
import sc.plugin2010.Move;
import sc.plugin2010.Move.MoveTyp;
import sc.plugin2010.Player.Action;

/**
 * Eine Werkzeug Klasse, um nützliche und häufig gebrauchte Funktionen zur
 * Verfügung zu stellen
 */
public class Werkzeuge
{
	/**
	 * berechnet die benötigte Karottenanzahl, die man haben muss, um
	 * <code>zugAnzahl</code> Fehler weiterzuziehen
	 * 
	 * @param zugAnzahl
	 *            die Anzahl an Feldern, die man nach vorne möchte
	 * @return Karottenanzahl, welche für diesen Zug benötigt wird
	 */
	public static int berechneBenoetigteKarotten(int zugAnzahl)
	{
		return GameUtil.calculateCarrots(zugAnzahl);
	}

	/**
	 * berechnet die maximale Zugzahl, welche man mit <code>karotten</code>
	 * machen kann
	 * 
	 * @param karrotten
	 *            die Anzahl an Karotten, die man ausgeben will
	 * @return Felderanzahl, welche man mit der Karottenanzahl ziehen kann
	 */
	public static int berechneMaximaleZugzahl(int karotten)
	{
		return GameUtil.calculateMoveableFields(karotten);
	}

	public static boolean istValideSalatFressen(Spielbrett brett,
			Spieler spieler)
	{
		return GameUtil.isValidToEat(brett.getBoard(), spieler.getPlayer());
	}

	public static boolean istValideIgelZurueckfallen(Spielbrett brett,
			Spieler spieler)
	{
		return GameUtil
				.isValidToFallBack(brett.getBoard(), spieler.getPlayer());
	}

	public static boolean istValideFeldZiehen(Spielbrett brett,
			Spieler spieler, int feldAnzahl)
	{
		return GameUtil.isValidToMove(brett.getBoard(), spieler.getPlayer(),
				feldAnzahl - spieler.getPlayer().getFieldNumber());
	}

	/**
	 * Überprüft, ob der Spieler <code>spieler</code> 10 Karotten abgeben darf.
	 * 
	 * @param brett
	 *            das aktuelle Spielbrett
	 * @param spieler
	 *            der Spieler für den die Aktion überprüft werden soll
	 * @return falls valide, dann true sonst false
	 */
	public static boolean istValide10KarrotenAbgeben(Spielbrett brett,
			Spieler spieler)
	{
		return GameUtil.isValidToTakeOrDrop10Carrots(brett.getBoard(), spieler
				.getPlayer(), -10);
	}

	/**
	 * Überprüft, ob der Spieler <code>spieler</code> 10 Karotten nehmen darf.
	 * 
	 * @param brett
	 *            das aktuelle Spielbrett
	 * @param spieler
	 *            der Spieler für den die Aktion überprüft werden soll
	 * @return falls valide, dann true sonst false
	 */
	public static boolean istValide10KarrotenNehmen(Spielbrett brett,
			Spieler spieler)
	{
		return GameUtil.isValidToTakeOrDrop10Carrots(brett.getBoard(), spieler
				.getPlayer(), 10);
	}

	/**
	 * Überprüft, ob der Spieler <code>spieler</code> den Hasenjoker
	 * <code>joker</code> spielen darf.
	 * 
	 * @param brett
	 *            das aktuelle Spielbrett
	 * @param spieler
	 *            der Spieler für den die Aktion überprüft werden soll
	 * @return falls valide, dann true sonst false
	 */
	public static boolean istValideHasenjokerSpielen(Spielbrett brett,
			Spieler spieler, Hasenjoker joker)
	{
		return istValideHasenjokerSpielen(brett, spieler, joker, 0);
	}

	/**
	 * Überprüft, ob der Spieler <code>spieler</code> den Hasenjoker
	 * <code>joker</code> spielen darf. Bei dem Joker
	 * <code>NIMM_ODER_GIB_20_KAROTTEN</code> wird beim Nehmen 20, beim Abgeben
	 * -20 oder beim Nichts tun 0 angegeben.
	 * 
	 * @param brett
	 *            das aktuelle Spielbrett
	 * @param spieler
	 *            der Spieler für den die Aktion überprüft werden soll
	 * @param karottenAnzahl
	 *            Nehmen: 20, Nichts tun: 0, Abgeben: -20
	 * @return falls valide, dann true sonst false
	 */
	public static boolean istValideHasenjokerSpielen(Spielbrett brett,
			Spieler spieler, Hasenjoker joker, int karottenAnzahl)
	{
		Action type;
		switch (joker)
		{
			case FALLE_ZURUECK:
				type = Action.FALL_BACK;
				break;
			case FRISS_SALAT:
				type = Action.EAT_SALAD;
				break;
			case NIMM_ODER_GIB_20_KAROTTEN:
				type = Action.TAKE_OR_DROP_CARROTS;
				break;
			case RUECKE_VOR:
				type = Action.HURRY_AHEAD;
				break;
			default:
				return false;
		}
		return GameUtil.isValidToPlayCard(brett.getBoard(),
				spieler.getPlayer(), type, karottenAnzahl);
	}

	public static boolean mussSpielerSichBewegen(Spieler spieler)
	{
		return GameUtil.playerMustMove(spieler.getPlayer(), MoveTyp.EAT);
	}

	// /////////////////////////////////////
	// interne Konvertierungsmethoden
	// /////////////////////////////////////

	/**
	 * @param zug
	 * @return
	 */
	protected static Move convertZug(Zug zug)
	{

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param zug
	 * @return
	 */
	protected static Zug convertMove(Move move)
	{
		// TODO Auto-generated method stub
		return null;
	}

	protected static Board.FieldTyp convertSpielfeldtyp(Spielfeldtyp typ)
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

	protected static Spielfeldtyp convertFieldtype(Board.FieldTyp typ)
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
