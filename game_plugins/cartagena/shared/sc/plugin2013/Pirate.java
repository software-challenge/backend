package sc.plugin2013;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Repräsentiert eine Spielfigur, besitzt Information darüber, welchem Spieler sie gehört
 * @author fdu
 *
 */
@XStreamAlias(value = "cartagena:pirate")
public class Pirate implements Cloneable {
	//Der Besitzer der Spielfigur
	@XStreamAsAttribute
	private final PlayerColor owner;
	
	public Pirate(){
		this.owner = null;
	}
	
	public Pirate(PlayerColor color){
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
		Pirate clone = new Pirate(this.owner);
		return clone;
	}
	

}
