package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Flower {

	// spielfeld auf dem diese blumen stehen
	@XStreamAsAttribute
	public final int node;
	// anzahl an blumen. negative werte fuer pilze
	@XStreamAsAttribute
	public final int amount;

	public Flower(int index, int amount) {
		this.node = index; 
		this.amount = amount;
	}

}
