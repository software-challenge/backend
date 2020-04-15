package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "scoreCause")
public enum ScoreCause {
  /** The player didn't violate against the rules or left the game early. */
  REGULAR,

  /** The player left the game early (connection loss). */
  LEFT,

  /** The player violated against the games rules. */
  RULE_VIOLATION,

  /** The player took to long to respond to the move request. */
  SOFT_TIMEOUT,

  /** The player didn't respond to the move request. */
  HARD_TIMEOUT,

  /** An error occurred during communication. This could indicate a bug in the server's code. */
  UNKNOWN
}
