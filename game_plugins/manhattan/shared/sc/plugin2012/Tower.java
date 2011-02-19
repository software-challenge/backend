package sc.plugin2012;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "mh:tower")
public class Tower {

	// index des stadt in der dieser turm steht
	@XStreamAsAttribute
	public final int city;

	// index des slots auf dem dieser turm steht
	@XStreamAsAttribute
	public final int slot;

	// rote teile in diesem turm
	@XStreamAsAttribute
	private int redParts;

	// blaue teile in diesem turm
	@XStreamAsAttribute
	private int blueParts;

	// farbe des besitzers dieses turms
	@XStreamAsAttribute
	private PlayerColor owner = null;

	public Tower(int city, int slot) {
		this.city = city;
		this.slot = slot;
	}

	/**
	 * fueft dem turm ein bauteil hinzui wenn dies den regeln entsprechend
	 * erlaubt ist
	 */
	public boolean addPart(PlayerColor color, int size) {
		if (canAddPart(color, size)) {
			owner = color;
			if (color == PlayerColor.RED) {
				redParts += size;
			} else {
				blueParts += size;
			}
			return true;
		}
		return false;
	}

	/**
	 * gibt an ob ein spieler ein bauteil auf diesen turm setzen darf
	 */
	public boolean canAddPart(PlayerColor color, int size) {
		return color == owner || size >= Math.abs(redParts - blueParts);
	}

	/**
	 * gibt an ob ein spieler ein bauteil auf diesen turm setzen darf
	 */
	public boolean canAddPart(Player player, int size) {
		return canAddPart(player.getPlayerColor(), size);
	}

	/**
	 * liefert den momentanen besitzer dieses turms
	 */
	public PlayerColor getOwner() {
		return owner;
	}

	public int getRedParts(){
		return redParts;
	}
	

	public int getBlueParts(){
		return blueParts;
	}
	
	
	public int getHeight() {
		return blueParts + redParts;
	}

	
}
