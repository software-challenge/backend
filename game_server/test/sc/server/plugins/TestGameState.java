package sc.server.plugins;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.framework.plugins.IPerspectiveAware;
import sc.shared.PlayerColor;

public class TestGameState
{
	public Integer		round	= 0;
	public int			state = 0;
	public int lastPlayerIndex = 0;
	public int turn;
	public PlayerColor currentPlayer;

	public PlayerColor startPlayer;


	public TestPlayer red;
	public TestPlayer blue;


	public TestGameState(){
		this.turn = 0;
		this.currentPlayer = PlayerColor.RED;
		this.startPlayer = PlayerColor.RED;
		this.red = new TestPlayer(PlayerColor.RED);
		this.blue = new TestPlayer(PlayerColor.BLUE);
	}

	/**
	 * wechselt den Spieler, der aktuell an der Reihe ist anhand der Anzahl der Züge <code>turn</code>
	 */
	public void switchCurrentPlayer() {
		if (turn % 2 == 0) {
			currentPlayer = PlayerColor.RED;
		} else {
			currentPlayer = PlayerColor.BLUE;
		}
	}
}
