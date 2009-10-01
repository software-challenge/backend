package gameClient;

import sc.plugin2010.framework.SpielClient;
import sc.plugin2010.framework.Spielbrett;
import sc.plugin2010.framework.Spieler;
import sc.plugin2010.framework.Spielfeldtyp;

/**
 * Ein SimpleClient für das Spiel Hase und Igel. Zu beachten ist, dass sobald
 * eine Aktion, wie "frissSalat", auf einem Spieler aufgerufen wurde, sämtliche
 * Anweisungen, wie "setzeFigur", zum Beenden des Spiels führen.
 * 
 * @author ffi
 * 
 */
public class SimpleClient extends SpielClient
{

	// interne Zustaende
	private final Spielbrett	spielbrett;
	private final Spieler		eigenerSpieler;
	private final Spieler		gegner;

	// zeigt an, ob in der letzten Runde schon karotten genommen / abgegeben
	// wurden
	private boolean				bereitsKarottenGenommen	= false;

	/**
	 * wird beim Spielstart aufgerufen
	 * 
	 * @param ip
	 *            die IP mit welcher der Client verbinden soll
	 * @param port
	 *            der Port mit welchem der Client verbinden soll
	 * @param spielreservierung
	 *            falls eine Spielreservierung beim Server für den Client
	 *            vorliegt
	 */
	public SimpleClient(String ip, int port, String spielreservierung)
	{
		// verbinde zum Spiel
		super(ip, port, spielreservierung);

		eigenerSpieler = new Spieler();
		gegner = new Spieler();
		spielbrett = new Spielbrett(eigenerSpieler, gegner);
		eigenerSpieler.setzteGegner(gegner);
		eigenerSpieler.setzteSpielbrett(spielbrett);

		// gib interne Referenzen der Logik
		super.setzeSpielbrett(spielbrett);
		super.setzeSpieler(eigenerSpieler);
		super.setzeGegner(gegner);

		super.starteSpiel();
	}

	/**
	 * diese Methode arbeitet die Standardaktion, wie "Friss Salat" oder
	 * "Friss Karotten" ab
	 * 
	 * @return true, falls eine Standardaktion ausgeführt wurde sonst false
	 */
	public boolean macheStandardAktion()
	{
		// zeigt an, ob eine Standardaktion ausgeführt wurde
		boolean zugGemacht = false;

		// Wenn keine Aktion möglich ist, dann muss der Spieler aussetzen
		if (eigenerSpieler.mussAussetzen())
		{
			eigenerSpieler.setzeAus();
			System.out.println("Spieler muss aussetzen");
			zugGemacht = true;
		}
		else if (eigenerSpieler.kannSalatFressen())
		{
			eigenerSpieler.frissSalat();
			System.out.println("Salat gefressen");
			zugGemacht = true;
		}
		// Wenn auf einem Karottenfeld, dann nimm Karotten oder gib Karotten
		// ab
		else if (eigenerSpieler.kannKarottenAufnehmen()
				&& !bereitsKarottenGenommen)
		{

			// wenn unter Feldnummer 45, dann nimm 10 Karotten
			if (eigenerSpieler.holeKarottenAnzahl() < 50
					&& eigenerSpieler.holeFeldnummer() < 50)
			{
				eigenerSpieler.nimmKarotten();
				bereitsKarottenGenommen = true;
				System.out.println("Karotten genommen");
				zugGemacht = true;

			} // sonst gib Karotten ab (wenn möglich)
			else if (eigenerSpieler.kannKarottenAbgeben()
					&& eigenerSpieler.holeKarottenAnzahl() < 50
					&& eigenerSpieler.holeFeldnummer() > 50)
			{
				eigenerSpieler.gibKarottenAb();
				bereitsKarottenGenommen = true;
				System.out.println("Karotten abgegeben");
				zugGemacht = true;
			}
		} // falls wir noch Hasenjoker haben
		else if (eigenerSpieler.holeHasenjoker().size() > 0)
		{ // spiele den ersten Hasenjoker auf der Hand
			if (eigenerSpieler.kannHasenjokerSpielen(eigenerSpieler
					.holeHasenjoker().get(0)))
			{
				eigenerSpieler.spieleHasenjoker(eigenerSpieler.holeHasenjoker()
						.get(0));
				System.out.println("Hasenjoker gespielt");
				zugGemacht = true;
			}
		}

		return zugGemacht;
	}

	/**
	 * Diese Methode wird aufgerufen, wenn der Server von dem Client einen Zug
	 * erwartet
	 */
	@Override
	public void zugAngefordert()
	{
		if (macheStandardAktion() == false)
		{
			boolean zugGemacht = false;
			bereitsKarottenGenommen = false;

			int feldNummer = -1;

			if (eigenerSpieler.kannAufFeldZiehen(64))
			{
				eigenerSpieler.setzeFigur(64);
				zugGemacht = true;
			}

			// Suche nach dem nächsten Salat
			feldNummer = spielbrett.holeNaechstesSpielfeldNachTyp(
					Spielfeldtyp.SALAT, eigenerSpieler.holeFeldnummer());

			// Wenn ein Salat gefunden wurde
			if (feldNummer > 0)
			{ // wenn es möglich ist auf den Salat zu ziehen
				if (eigenerSpieler.kannAufFeldZiehen(feldNummer))
				{
					System.out.println("Salatfeld");
					eigenerSpieler.setzeFigur(feldNummer);
					zugGemacht = true;
				}
				else
				{ // suchen nächstes Karottenfeld
					feldNummer = spielbrett.holeNaechstesSpielfeldNachTyp(
							Spielfeldtyp.KAROTTEN, eigenerSpieler
									.holeFeldnummer());
					if (feldNummer > 0)
					{ // wenn man auf das Karottenfeld ziehen darf, setze dort
						// hin
						if (eigenerSpieler.kannAufFeldZiehen(feldNummer))
						{
							eigenerSpieler.setzeFigur(feldNummer);
							System.out.println("Karottenfeld");
							zugGemacht = true;
						} // sonst falle auf letzten Igel zurück
						else if (eigenerSpieler.kannZurueckfallen())
						{
							eigenerSpieler.zurueckAufLetztenIgel();
							System.out.println("Igelfeld1");
							zugGemacht = true;
						}
					}
					else
					{ // sonst falle auf letzten Igel zurück
						if (eigenerSpieler.kannZurueckfallen())
						{
							eigenerSpieler.zurueckAufLetztenIgel();
							System.out.println("Igelfeld2");
							zugGemacht = true;
						}
					}
				}
			}
			else
			{// sonst falle auf letzten Igel zurück
				if (eigenerSpieler.kannZurueckfallen())
				{
					eigenerSpieler.zurueckAufLetztenIgel();
					System.out.println("Igelfeld3");
					zugGemacht = true;
				}
			}

			// wenn bis hier hin noch kein Zug gemacht wurd, wähle zufällig
			if (!zugGemacht)
			{
				for (int i = eigenerSpieler.holeFeldnummer() + 1; i < 65; i++)
				{
					if (eigenerSpieler.kannAufFeldZiehen(i))
					{
						System.out.println("Zufall");
						eigenerSpieler.setzeFigur(i);
						break;
					}
				}
			}
		}
	}

	@Override
	public void spielBeendet(String[] statistik, boolean abgebrochen)
	{
		// Spiel wurde abgeschlossen
		if (abgebrochen)
		{
			System.out.println("Spiel wurde abgebrochen!");
		}
		else
		{
			System.out.println(statistik[0]);
			if (statistik[0].equals("1"))
			{
				System.out.println("Gewinner: Rot");
			}
			else if (statistik[0].equals("0"))
			{
				System.out.println("Verlierer: Rot");
			}

			System.out.println("Erreichtes Feld Rot: " + statistik[1]);

			if (statistik[2].equals("1"))
			{
				System.out.println("Gewinner: Blau");
			}
			else if (statistik[2].equals("0"))
			{
				System.out.println("Verlierer: Blau");
			}

			System.out.println("Erreichtes Feld Blau: " + statistik[3]);
		}

	}
}
