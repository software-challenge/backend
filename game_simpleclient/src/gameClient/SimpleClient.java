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
public class SimpleClient extends SpielClient
{

	private final Spielbrett	spielbrett;
	private final Spieler		eigenerSpieler;
	private final Spieler		gegner;

	public SimpleClient(String ip, int port, String spielreservierung)
	{
		// verbinde zum Spiel
		super(ip, port, spielreservierung);

		eigenerSpieler = new Spieler();
		gegner = new Spieler();
		spielbrett = new Spielbrett(eigenerSpieler, gegner);

		// gib interne Referenzen der Logik
		super.setzeSpielbrett(spielbrett);
		super.setzeSpieler(eigenerSpieler);
		super.setzeGegner(gegner);
	}

	public boolean macheStandardAktion()
	{
		boolean zugGemacht = false;

		// Wenn auf einem Salatfeld, dann Salat fressen
		if (Werkzeuge.istValideSalatFressen(spielbrett, eigenerSpieler))
		{
			eigenerSpieler.frissSalat();
			zugGemacht = true;
		}
		else if (eigenerSpieler.holeHasenjoker().size() > 0)
		{
			if (Werkzeuge.istValideHasenjokerSpielen(spielbrett,
					eigenerSpieler, eigenerSpieler.holeHasenjoker().get(0), 0))
			{
				eigenerSpieler.spieleHasenjoker(eigenerSpieler.holeHasenjoker()
						.get(0));
				zugGemacht = true;
			}
			// Wenn auf einem Karottenfeld, dann nimm Karotten oder gib Karotten
			// ab
		}
		else if (Werkzeuge.istValide10KarrotenNehmenAbgeben(spielbrett,
				eigenerSpieler, 10))
		{

			if (eigenerSpieler.holeFeldnummer() < 50)
			{
				eigenerSpieler.nimmKarotten();
				zugGemacht = true;

			}
			else if (Werkzeuge.istValide10KarrotenNehmenAbgeben(spielbrett,
					eigenerSpieler, -10))
			{
				eigenerSpieler.gibKarottenAb();
				zugGemacht = true;
			}
		}
		return zugGemacht;
	}

	@Override
	public void zugAngefordert()
	{
		if (macheStandardAktion() == false)
		{

			int feldNummer = -1;

			// Suche erst nach dem nächsten Salat
			if (eigenerSpieler.holeSalatAnzahl() > 0)
			{
				feldNummer = spielbrett.holeNaechstesSpielfeldNachTyp(
						Spielfeldtyp.SALAT, eigenerSpieler.holeFeldnummer());
			}

			// Wenn ein Salat gefunden wurde
			if (feldNummer > 0)
			{
				if (Werkzeuge.berechneBenoetigteKarotten(feldNummer
						- eigenerSpieler.holeFeldnummer()) <= eigenerSpieler
						.holeKarottenAnzahl())
				{
					eigenerSpieler.setzeFigur(feldNummer);
				}
				else
				{
					feldNummer = spielbrett.holeNaechstesSpielfeldNachTyp(
							Spielfeldtyp.KAROTTEN, eigenerSpieler
									.holeFeldnummer());
					if (feldNummer > 0)
					{
						if (Werkzeuge.berechneBenoetigteKarotten(feldNummer
								- eigenerSpieler.holeFeldnummer()) <= eigenerSpieler
								.holeKarottenAnzahl())
						{
							eigenerSpieler.setzeFigur(feldNummer);
						}
					}
				}
			}
		}
	}
}
