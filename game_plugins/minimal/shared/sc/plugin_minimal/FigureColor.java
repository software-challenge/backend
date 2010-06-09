package sc.plugin_minimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Define player colors here. Maybe would be nicer to name it PlayerColor not FigureColor
 */
@XStreamAlias(value="minimal:color")
public enum FigureColor
{
	/**
	 * First player is always red
	 */
	RED,
	/**
	 *  Second player always blue
	 */
	BLUE
}
