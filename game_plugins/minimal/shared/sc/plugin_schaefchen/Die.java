package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Die{
	
	// farbe des aktuellen spielers
	@XStreamAsAttribute
	public final int value;
	
	public Die(int value){
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		assert obj instanceof Die;
		return this.value == ((Die) obj).value;
	}
	
}
