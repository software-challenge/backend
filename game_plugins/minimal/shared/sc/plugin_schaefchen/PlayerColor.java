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

	PLAYER1(Color.RED), PLAYER2(Color.BLUE), NOPLAYER(Color.YELLOW.darker());

	// die zugehoerige echte farbe
	private Color color;

	private PlayerColor(Color c) {
		this.color = c;
	}

	/**
	 * liefert die zugehoerige echte farbe
	 */
	public Color getColor() {
		return color;
	}

}
