package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "scoreCause")
public enum ScoreCause
{
	/**
	 * The player didn't violate against the rules or left the game early.
	 */
	REGULAR,

	/**
	 * The player left the game early (connection loss)
	 */
	LEFT,

	/**
	 * The player violated against the games rules
	 */
	RULE_VIOLATION,

	/**
	 * An error occured during communication. This could indicate a bug in the
	 * server's code.
	 */
	UNKNOWN
}
