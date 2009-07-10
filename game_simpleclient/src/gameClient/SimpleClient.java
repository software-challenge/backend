package gameClient;

import sc.plugin2010.framework.SpielClient;
import sc.plugin2010.framework.Spielbrett;
import sc.plugin2010.framework.Spieler;
import sc.plugin2010.framework.Spielfeldtyp;
import sc.plugin2010.framework.Werkzeuge;

/**
 * Ein SimpleClient für das Spiel Hase und Igel. Zu beachten ist, 
 * dass sobald eine Aktion, wie "frissSalat", auf einem Spieler aufgerufen wurde. 
 * Sämtliche Anweisungen, wie "setzeFigur", für diese Runde ignoriert werden, 
 * da der Spieler seinen Zug bereits gemacht hat.
 */

/**
 * @author ffi
 * 
 */
public class SimpleClient extends SpielClient {

	private Spielbrett spielbrett;
	private Spieler spieler;
	private Spieler gegner;

	public SimpleClient(String ip, int port) {
		// verbinde zum Spiel
		super(ip, port);

		spieler = new Spieler();
		gegner = new Spieler();
		spielbrett = new Spielbrett(spieler, gegner);

		// gib interne Referenz der Logik
		super.setzeSpielbrett(spielbrett);
		super.setzeSpieler(spieler);
		super.setzeGegner(gegner);
	}

	/**
	 * Diese Methode wird beim Start des Programmes aufgerufen. Die Parameter
	 * werden für das automatische Verbinden benötigt.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		SimpleClient simpleClient = new SimpleClient(args[0], Integer
				.valueOf(args[1]));
	}

	public void macheStandardAktion() {
		// TODO
		// Wenn auf einem Salatfeld, dann Salat fressen
		spieler.frissSalat(); // TODO danach die Prozedur abbrechen?
		// Wenn auf einem Hasenfeld, dann setze zufällig ersten Hasenjoker ein
		if (spieler.holeHasenjoker().size() > 0) {
			spieler.setzeHasenjoker(spieler.holeHasenjoker().get(0));
		}
		// Wenn auf einem Karottenfeld, dann nimm Karotten
		spieler.nimmKarotten();
	}

	@Override
	public void zugAngefordert() {
		macheStandardAktion();

		int feldNummer = -1;

		// Suche erst nach dem nächsten Salat
		if (spieler.holeSalatAnzahl() > 0) {
			feldNummer = spielbrett.holeNaechstesSpielfeldNachTyp(
					Spielfeldtyp.SALAT, spieler.holeFeldnummer());
		}

		// Wenn ein Salat gefunden wurde, dann
		if (feldNummer > 0) {
			if (Werkzeuge.berechneBenoetigteKarotten(feldNummer
					- spieler.holeFeldnummer()) <= spieler.holeKarottenAnzahl()) {
				spieler.setzeFigur(feldNummer);
			}
		}
	}
}
