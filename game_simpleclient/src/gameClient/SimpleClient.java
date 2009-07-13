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
	private boolean				bereitsKarottenGenommen	= false;

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
			System.out.println("Salat gefressen");
			zugGemacht = true;
		}
		// Wenn auf einem Karottenfeld, dann nimm Karotten oder gib Karotten
		// ab
		else if (Werkzeuge
				.istValide10KarrotenNehmen(spielbrett, eigenerSpieler)
				&& !bereitsKarottenGenommen)
		{

			if (eigenerSpieler.holeFeldnummer() < 50)
			{
				eigenerSpieler.nimmKarotten();
				bereitsKarottenGenommen = true;
				System.out.println("Karotten genommen");
				zugGemacht = true;

			}
			else if (Werkzeuge.istValide10KarrotenAbgeben(spielbrett,
					eigenerSpieler))
			{
				eigenerSpieler.gibKarottenAb();
				bereitsKarottenGenommen = true;
				System.out.println("Karotten abgegeben");
				zugGemacht = true;
			}
		}
		else if (eigenerSpieler.holeHasenjoker().size() > 0)
		{
			if (Werkzeuge.istValideHasenjokerSpielen(spielbrett,
					eigenerSpieler, eigenerSpieler.holeHasenjoker().get(0), 0))
			{
				eigenerSpieler.spieleHasenjoker(eigenerSpieler.holeHasenjoker()
						.get(0));
				System.out.println("Hasenjoker");
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
			System.out.println("Keine Standardaktion");

			boolean zugGemacht = false;
			bereitsKarottenGenommen = false;

			int feldNummer = -1;

			// Suche nach dem nächsten Salat
			feldNummer = spielbrett.holeNaechstesSpielfeldNachTyp(
					Spielfeldtyp.SALAT, eigenerSpieler.holeFeldnummer());

			// Wenn ein Salat gefunden wurde
			if (feldNummer > 0)
			{
				if (Werkzeuge.istValideFeldZiehen(spielbrett, eigenerSpieler,
						feldNummer))
				{
					System.out.println("Salatfeld");
					eigenerSpieler.setzeFigur(feldNummer);
					zugGemacht = true;
				}
				else
				{
					feldNummer = spielbrett.holeNaechstesSpielfeldNachTyp(
							Spielfeldtyp.KAROTTEN, eigenerSpieler
									.holeFeldnummer());
					if (feldNummer > 0)
					{
						if (Werkzeuge.istValideFeldZiehen(spielbrett,
								eigenerSpieler, feldNummer))
						{
							eigenerSpieler.setzeFigur(feldNummer);
							System.out.println("Karottenfeld");
							zugGemacht = true;
						}
						else if (Werkzeuge.istValideIgelZurueckfallen(
								spielbrett, eigenerSpieler))
						{
							eigenerSpieler.zurueckAufLetztenIgel();
							System.out.println("Igelfeld1");
							zugGemacht = true;
						}
					}
					else
					{
						if (Werkzeuge.istValideIgelZurueckfallen(spielbrett,
								eigenerSpieler))
						{
							eigenerSpieler.zurueckAufLetztenIgel();
							System.out.println("Igelfeld2");
							zugGemacht = true;
						}
					}
				}
			}
			else
			{
				if (Werkzeuge.istValideIgelZurueckfallen(spielbrett,
						eigenerSpieler))
				{
					eigenerSpieler.zurueckAufLetztenIgel();
					System.out.println("Igelfeld3");
					zugGemacht = true;
				}
			}

			if (!zugGemacht)
			{
				for (int i = eigenerSpieler.holeFeldnummer() + 1; i < 65; i++)
				{
					if (Werkzeuge.istValideFeldZiehen(spielbrett,
							eigenerSpieler, i))
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
	public void spielBeendet()
	{
		// Spiel wurde beendet
	}
}
