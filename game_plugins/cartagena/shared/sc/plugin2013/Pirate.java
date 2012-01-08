package sc.plugin2013;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Repräsentiert eine Spielfigur, besitzt Information darüber, welchem Spieler sie gehört
 * @author felix
 *
 */
@XStreamAlias(value = "cartagena:pirate")
public class Pirate implements Cloneable {
	//Der Besitzer der Spielfigur
	@XStreamOmitField
	private final PlayerColor owner;
	
	public Pirate(){
		this.owner = null;
	}
	
	public Pirate(PlayerColor color){
		this.owner = color;
	}
	
	public PlayerColor getOwner(){
		return this.owner;
	}
	

}
