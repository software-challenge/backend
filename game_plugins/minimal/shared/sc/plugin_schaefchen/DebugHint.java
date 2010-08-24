package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class DebugHint {
	
	@XStreamAsAttribute
	public final String key;

	@XStreamAsAttribute
	public final String value;
	
	public DebugHint(String key, String value) {

		this.key = key;
		this.value = value;

	}

}
