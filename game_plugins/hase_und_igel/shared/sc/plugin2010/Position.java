package sc.plugin2010;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="hui:position")
public enum Position
{
	SECOND, FIRST,
	/**
	 * In the rare case where both players are on the same spot (START).
	 */
	TIE
}
