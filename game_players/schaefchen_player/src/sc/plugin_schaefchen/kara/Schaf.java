package sc.plugin_schaefchen.kara;

import sc.plugin_schaefchen.HundStatus;
import sc.plugin_schaefchen.PlayerColor;
import sc.plugin_schaefchen.Sheep;

public class Schaf {
	private Spieler owner;
	private Sheep sheep;
	private Feld position;
	private Feld target;
	
	public Schaf(Sheep sheep, Spieler owner, int position, Spielstatus stat){
		this.sheep = sheep;
		this.owner = owner;
		this.position = stat.holeFeldMitIndex(position);
		this.target = stat.holeFeldMitIndex(sheep.getTarget());
	}
	
	public Spieler holeHalter(){
		return owner;
	}
	
	/**
	 * Gibt das Ziel zurück, zu dem das Schaf sich bewegen muss
	 * (das einzige Heimatfeld, welches von ihm betreten werden darf)
	 */
	public Feld holeZiel(){
		return target;
	}
	/**
	 * Gibt die Anzahl der Schafe oder Hunde des angegebenen 
	 * Spielers in dieser Herde an
	 */
	public int holeSpielerGroesse(Spieler spieler){
		return sheep.getSize(spieler.getPlayer().getPlayerColor());
	}
	
	/**
	 * Gibt die Anzahl der Schafe oder Hunde in dieser
	 * Herde an (unabhängig von dem Spieler, dem diese
	 * zuzuordnen sind)
	 */
	public int holeGesamteGroesse(){
		return sheep.getSize(PlayerColor.PLAYER1)+sheep.getSize(PlayerColor.PLAYER2);
	}
	
	/**
	 * Gibt das Feld zurueck auf dem sich dieses Schaf befindet
	 */
	public Feld holePosition(){
		return position;
	}
	
	/**
	 * Gibt die Anzahl der Blumen zurueck, die diese Herde gesammelt hat
	 */
	public int holeBlumen(){
		return sheep.getFlowers();
	}
	
	/**
	 * Gibt zurück, ob die Herde durch einen Hund begleitet wird, bzw. ob dieser
	 * aktuell aktiv oder inaktiv ist.
	 */
	public HundStatus holeHundeStatus(){
		HundStatus status = HundStatus.NICHT_VORHANDEN;
		switch(sheep.getDogState()){
		case ACTIVE: status = HundStatus.AKTIV;
		break;
		case PASSIVE: status = HundStatus.INAKTIV;
		}
		return status;
	}
	
	/**
	 * Gibt den Index dieses Schafes zurück
	 */
	public int holeIndex(){
		return sheep.index;
	}
	
	public Sheep getSheep(){
		return sheep;
	}
}
