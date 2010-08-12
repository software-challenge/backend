package sc.plugin_minimal;

import java.awt.Color;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * abstrakte spielerfarben die echte farben liefern
 * @author tkra
 *
 */
@XStreamAlias(value="minimal:color")
public enum PlayerColor
{
	
	/**
	 * First player is always red
	 */
	PLAYER1(Color.RED),
	/**
	 *  Second player always blue
	 */
	PLAYER2(Color.BLUE), 
	/**
	 * 
	 */
	NOPLAYER(Color.YELLOW);

	
	private Color color;
	
	private PlayerColor(Color c){
		this.color = c;
	}
	
	public Color getColor() {
		return color;
	}
	
}
