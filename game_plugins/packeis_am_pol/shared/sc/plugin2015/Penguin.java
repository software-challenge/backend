package sc.plugin2015;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Repräsentiert einen Pinguin, besitzt Information darüber, welchem Spieler sie gehört
 * @author fdu
 *
 */
@XStreamAlias(value = "penguin")
public class Penguin implements Cloneable {
	//Der Besitzer der Spielfigur
	@XStreamAsAttribute
	private final PlayerColor owner;
	
	public Penguin(){
		this.owner = null;
	}
	
	public Penguin(PlayerColor color){
		this.owner = color;
	}
	
	/** Gibt die Farbe des zugehörigen Spielers zurück
	 * @return
	 */
	public PlayerColor getOwner(){
		return this.owner;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Penguin clone = new Penguin(this.owner);
		return clone;
	}
	

}

