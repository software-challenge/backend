package sc.plugin_schaefchen.kara;

import sc.plugin_schaefchen.GameState;
import sc.plugin_schaefchen.IGameHandler;
import sc.plugin_schaefchen.Move;
import sc.plugin_schaefchen.Player;
import sc.plugin_schaefchen.PlayerColor;
import sc.plugin_schaefchen.simple.Starter;
import sc.shared.GameResult;

/**
 * Diese Implementierung des IGameHandler, stellt die Schnittstelle für einen
 * Kara Client dar. Um eine Logik mit Kara zu programmieren muss nur von dieser
 * abstrakten Klasse abgeleitet werden.
 * 
 * Wenn ein Zug ausgeführt wird, so wird die Methode macheZug() aufgerufen, in
 * der die Taktik realisiert werden kann. Der von dieser Methode zurückgegebene
 * Zug wird dann automatisch an den Server gesendet.
 * 
 * Die Informationen zum aktuellen Zustand des Spiels stehen immer aktuell in
 * dem Spielstatus Objekt mit dem Namen "status" zur Verfügung.
 */
public abstract class KaraLogicHandler implements IGameHandler {

	private Starter client;
	private Player currentPlayer;
	
	/**
	 * Speichert den aktuellen Spielstatus und kann zur Berechnung des
	 * gewünschten Zuges verwendet werden.
	 */
	public Spielstatus status;

	public KaraLogicHandler(Starter client) {
		this.client = client;
	}

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
		System.out.println("*** das spiel ist beendet");
	}

	@Override
	public void onRequestAction() {
		System.out.println("*** es wurde ein zug angefordert");
		sendAction(macheZug().getMove());
	}

	/**
	 * Diese Methode i
	 */
	public abstract Zug macheZug();

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;
		System.out.println("*** spielerwechsel: " + player.getPlayerColor());

	}

	@Override
	public void onUpdate(GameState gameState) {
		currentPlayer = gameState.getCurrentPlayer();
		status = new Spielstatus(gameState);

		System.out.println("*** das spiel geht vorran: " + gameState.getTurn()
				+ " " + currentPlayer.getPlayerColor());
	}

	@Override
	public void sendAction(Move move) {
		client.sendMove(move);

		System.out.println("*** sende zug: " + move.sheep + " -> "
				+ move.target);
	}

}
