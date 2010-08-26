package sc.plugin_schaefchen.simple;

import java.util.Random;

import sc.plugin_schaefchen.kara.KaraLogicHandler;
import sc.plugin_schaefchen.kara.Zug;

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
