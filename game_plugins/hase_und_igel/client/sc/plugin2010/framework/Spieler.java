package sc.plugin2010.framework;

import sc.plugin2010.Action;
import sc.plugin2010.Move;
import sc.plugin2010.MoveTyp;

/**
 * Repräsentiert den Spieler in Hase und Igel. Hier wird nach dem "Kara-Prinzip"
 * vorgegangen.
 * 
 * @author ffi
 * 
 */
public class Spieler extends AllgemeinerSpieler
{
	private Logik	logik;
	private Spielbrett spielbrett;
	private Spieler gegner;
	
	public void setzteGegner(Spieler gegner) {
		this.gegner = gegner;
	}
	
	public void setzteSpielbrett(Spielbrett brett) {
		this.spielbrett = brett;
	}

	public Spieler()
	{

	}

	/**
	 * Spielt den Hasenjoker <code>joker</code>. Mit der
	 * <code>karottenAnzahl</code> wird bei NIMM_ODER_GIB_20_KAROTTEN
	 * beschreiben, welche Aktion genau gemacht werden soll
	 * 
	 * @param joker
	 *            der zu spielende Hasenjoker
	 * @param karottenAnzahl
	 *            Nur bei NIMM_ODER_GIB_20_KAROTTEN entscheidet: Wenn 20 dann
	 *            nimm 20 Karotten, wenn -20 dann gib 20 Karotten ab und bei 0
	 *            mache keine Aktion.
	 */
	public void spieleHasenjoker(Hasenjoker joker, int karottenAnzahl)
	{
		switch (joker)
		{
			case FALLE_ZURUECK:
				logik.sendAction(new Move(MoveTyp.PLAY_CARD,
						Action.FALL_BACK));
				break;
			case FRISS_SALAT:
				logik.sendAction(new Move(MoveTyp.PLAY_CARD,
						Action.EAT_SALAD));
				break;
			case NIMM_ODER_GIB_20_KAROTTEN:
				logik.sendAction(new Move(MoveTyp.PLAY_CARD,
						Action.TAKE_OR_DROP_CARROTS, karottenAnzahl));
				break;
			case RUECKE_VOR:
				logik.sendAction(new Move(MoveTyp.PLAY_CARD,
						Action.HURRY_AHEAD));
				break;
			default:
				break;
		}
	}

	/**
	 * Spielt den Hasenjoker <code>joker</code>.
	 * 
	 * @param joker
	 *            der zu spielende Hasenjoker
	 */
	public void spieleHasenjoker(Hasenjoker joker)
	{
		spieleHasenjoker(joker, 0);
	}

	/**
	 * Manchmal ist es nur noch möglich auszusetzen. Dann setze hiermit aus.
	 * ACHTUNG: Aussetzen ist nur erlaubt, wenn kein anderer Zug mehr möglich
	 * ist.
	 */
	public void setzeAus()
	{
		logik.sendAction(new Move(MoveTyp.SKIP));
	}

	/**
	 * Setzt den Spieler auf das Feld <code>feldNummer</code>.
	 * 
	 * @param feldNummer
	 *            die absolute Feldnummer des Zielfeldes
	 */
	public void setzeFigur(final int feldNummer)
	{
		if (feldNummer < 0 && feldNummer > 64)
		{
			throw new IllegalArgumentException();
		}

		logik.sendAction(new Move(MoveTyp.MOVE, feldNummer
				- holeFeldnummer()));
	}

	/**
	 * Frisst einen Salat.
	 */
	public void frissSalat()
	{
		logik.sendAction(new Move(MoveTyp.EAT));
	}

	/**
	 * Setzt den Spieler auf den letzten Igel zurück. Dabei kriegt der Spieler
	 * x*10 Karotten für x Felder zurück.
	 */
	public void zurueckAufLetztenIgel()
	{
		logik.sendAction(new Move(MoveTyp.FALL_BACK));
	}

	/**
	 * Gibt auf einem Karottenfeld 10 Karotten ab.
	 */
	public void gibKarottenAb()
	{
		logik.sendAction(new Move(MoveTyp.TAKE_OR_DROP_CARROTS, -10));
	}

	/**
	 * Nimmt auf einem Karottenfeld 10 Karotten.
	 */
	public void nimmKarotten()
	{
		logik.sendAction(new Move(MoveTyp.TAKE_OR_DROP_CARROTS, 10));
	}

	/**
	 * Die nicht "Kara"-Variante. Für erfahrenere Schüler. Dies ist eine
	 * Alternative zu den Methoden "nimmKarotten" etc.
	 * 
	 * @param zug
	 *            der zu machende Zug
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
	 * setzt den Logikhandler
	 * 
	 * @param logik
	 */
	protected void setLogik(Logik logik)
	{
		this.logik = logik;
	}
	
	/**
	 * Muss der Spieler einen Zug aussetzen, weil kein anderer Zug möglich ist?
	 * @return
	 */
	public boolean mussAussetzen() {
		return Werkzeuge.istValideAussetzen(spielbrett, this);
	}
	
	/**
	 * Kann der Spieler einen Salat fressen?
	 * @return
	 */
	public boolean kannSalatFressen() {
		return Werkzeuge.istValideSalatFressen(spielbrett, this);
	}
	
	/**
	 * Kann der Spieler Karotten aufnehmen?
	 * @return
	 */
	public boolean kannKarottenAufnehmen() {
		return Werkzeuge.istValide10KarrotenNehmen(spielbrett, this);
	}
	
	/**
	 * Kann der Spieler Karotten abgeben?
	 * @return
	 */
	public boolean kannKarottenAbgeben() {
		return Werkzeuge.istValide10KarrotenAbgeben(spielbrett, this);
	}
	
	/**
	 * Kann der Spieler einen Hasenjoker ausspielen?
	 * @param joker
	 * @return
	 */
	public boolean kannHasenjokerSpielen(Hasenjoker joker) {
		return Werkzeuge.istValideHasenjokerSpielen(spielbrett,
				this, joker, 0);
	}
	
	/**
	 * Kann der Spieler auf ein Feld ziehen?
	 * @param feldnummer
	 * @return
	 */
	public boolean kannAufFeldZiehen(int feldnummer) {
		return Werkzeuge.istValideFeldZiehen(spielbrett, this, feldnummer);
	}
	
	/**
	 * Kann der Spieler auf einen Igel zurueckfallen?
	 * @return
	 */
	public boolean kannZurueckfallen() {
		return Werkzeuge.istValideIgelZurueckfallen(spielbrett, this);
	}
}
