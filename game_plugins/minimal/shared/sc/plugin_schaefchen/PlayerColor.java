package sc.plugin_schaefchen;

import java.awt.Color;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * abstrakte spielerfarben die echte farben liefern
 * 
 * @author tkra
 * 
 */
@XStreamAlias(value = "sit:color")
public enum PlayerColor {

	PLAYER1, PLAYER2, NOPLAYER;

	/**
	 * liefert die spielerfarbe des gegners dieses spielers
	 */
	public PlayerColor oponent() {
		PlayerColor result = PlayerColor.NOPLAYER;
		switch (this) {
		case PLAYER1:
			result = PlayerColor.PLAYER2;
			break;

		case PLAYER2:
			result = PlayerColor.PLAYER1;
			break;

		case NOPLAYER:
			result = PlayerColor.NOPLAYER;
			break;
		}

		return result;
	}

}
