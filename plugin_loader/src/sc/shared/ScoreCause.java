package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "scoreCause")
public enum ScoreCause {
	REGULAR,
	LEFT,
	UNKNOWN
}
