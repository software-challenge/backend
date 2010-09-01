package sc.plugin_schaefchen.kara;

import sc.plugin_schaefchen.Flower;

/**
 * Stellt eine Blume im Spiel dar
 */
public class Blume {
	Flower flower;
	Feld feld;
	
	public Blume(Flower f, Feld n){
		flower = f;
		feld = n;
	}
	
	/**
	 * Gibt das Feld zurueck an welchem sich die Blume befindet
	 */
	public Feld holeFeld(){
		return feld;
	}
	
	/**
	 * Gibt den Wert/Menge dieser Blume(n) zurueck
	 */
	public int holeAnzahl(){
		return flower.amount;
	}
}
