package sc.plugin2011;

import sc.plugin2011.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * containerklasse fuer einen wuerfel mitr augenzahl zwischen 1 und
 * Constants.DIE_SIZE
 * 
 * @author tkra
 * 
 */
public final class Die implements Cloneable {

	/**
	 * die von diesem wuerfel repraesentierte augenzahl
	 */
	@XStreamAsAttribute
	public final int value;

	public Die(final int value) throws IllegalArgumentException {
		if (value < 1 || value > Constants.DIE_SIZE) {
			throw new IllegalArgumentException("keine gueltige augenzahl: "
					+ value);
		}
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		assert obj instanceof Die;
		return this.value == ((Die) obj).value;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Die(value);
	}

}
