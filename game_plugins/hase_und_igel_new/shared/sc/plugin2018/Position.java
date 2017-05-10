package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="position")
public enum Position
{
	SECOND, FIRST,
	/**
	 * In the rare case where both players are on the same spot (START, GOAL).
	 */
	TIE
}
