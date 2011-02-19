package sc.plugin2012;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Selection {
	
	// segmentgroesse
	@XStreamAsAttribute
	public final int size;
	
	// anzahl
	@XStreamAsAttribute
	public final int amount;
	
	public Selection(int size, int amount){
		this.size = size;
		this.amount = amount;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Selection(size, amount);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Selection && ((Selection) obj).size == size;
	}

	@Override
	public int hashCode() {
		return size;
	}
	
	
	

}
