package sc.player2011.kara;

import java.util.Random;


/**
 * Dies ist eine Logik, die Kara verwendet um eine
 * einfache Strategie zu realisieren (Zufall)
 */
public class KaraLogic extends KaraLogicHandler{
	
	public KaraLogic(Starter client) {
		super(client);
	}

	@Override
	public Zug macheZug() {
		Random rand = new Random(System.currentTimeMillis());
		return status.holeMoeglicheZuege().get(rand.nextInt(status.holeMoeglicheZuege().size()));
	}

}
