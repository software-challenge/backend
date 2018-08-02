package sc.player2011.kara;

import java.util.LinkedList;
import java.util.List;

import sc.plugin2011.Node;

/**
 * Beschreibt ein Feld des Spiels
 */
public class Feld {
	public Feld(Node node, Spielstatus curr) {
		this.node = node;
		this.state = curr;
	}

	private Node node;
	private Spielstatus state;
	
	/**
	 * Gibt den Index dieses Feldes zurück
	 */
	public int holeIndex(){
		return node.index;
	}
	
	public Node getNode(){
		return node;
	}
	
	/**
	 * Gibt die Nachbarn des Feldes zurück
	 */
	public List<Feld> holeNachbarn(){
		List<Feld> neighbors = new LinkedList<Feld>();
		for(Integer i : node.getNeighbours()){
			neighbors.add(state.holeFeldMitIndex(i));
		}
		return neighbors;
	}
	
	/**
	 * Gibt die Nachbarn des Feldes zurück, 
	 * die mit einem bestimmten Würfel erreichbar sind
	 */
	public List<Feld> holeNachbarn(int wuerfel){
		List<Feld> neighbors = new LinkedList<Feld>();
		for(Integer i : node.getNeighbours(wuerfel)){
			neighbors.add(state.holeFeldMitIndex(i));
		}
		return neighbors;
	}
	
	/**
	 * Gibt den Typ dieses Feldes zurück
	 */
	public FeldTyp holeFeldTyp(){
		FeldTyp typ;
		switch (node.getNodeType()) {
		case HOME1: typ = FeldTyp.HEIMATFELD1;
		break;
		case HOME2: typ = FeldTyp.HEIMATFELD2;
		break;
		case GRASS: typ = FeldTyp.NORMALES_FELD;
		break;
		default: typ = FeldTyp.SICHERHEITSFELD;
		}
		return typ;
	}
}
