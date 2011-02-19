package sc.plugin2012;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "mh:segment")
public class Segment {

	// segmentgroesse
	@XStreamAsAttribute
	public final int size;

	// anzahl verwendeter segmente dieser groesse
	@XStreamAsAttribute
	private int used;

	// anzahl verfügbarere segmente dieser groesse
	@XStreamAsAttribute
	private int usable;

	// anzahl aufbewahrter segmente dieser groesse
	@XStreamAsAttribute
	private int retained;

	public Segment(int size, int amount) {
		this.size = size;
		used = 0;
		usable = 0;
		retained = amount;
	}

	/**
	 * liefert die anzahl der bereits verwendeten segmente dieser groesse
	 */
	public int getUsed() {
		return used;
	}

	/**
	 * liefert die anzahl der momentan verwendbaren segmente dieser groesse
	 */
	public int getUsable() {
		return usable;
	}

	/**
	 * liefert die anzahl der noch aufbewahrten segmente dieser groesse
	 */
	public int getRetained() {
		return retained;
	}

	/**
	 * ein segment dieser groesse benutzen
	 */
	public void use() {
		usable--;
		used++;
	}

	/**
	 * segmente dieser groesse auswaehlen
	 */
	public void select(int amount) {
		retained -= amount;
		usable  += amount;
	}

}
