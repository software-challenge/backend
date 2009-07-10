package gameClient;

import sc.plugin2010.framework.Gegner;
import sc.plugin2010.framework.SpielClient;
import sc.plugin2010.framework.Spielbrett;
import sc.plugin2010.framework.Spieler;

/**
 * 
 */

/**
 * @author ffi
 * 
 */
public class SimpleClient extends SpielClient {

	private Spielbrett spielbrett;
	private Spieler spieler;
	private Gegner gegner;

	public SimpleClient(String ip, int port) {
		// verbinde zum Spiel
		super(ip, port);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleClient simpleClient = new SimpleClient(args[0], Integer
				.valueOf(args[1]));
	}

	@Override
	public void zugAngefordert() {
		// TODO Auto-generated method stub

	}
}
