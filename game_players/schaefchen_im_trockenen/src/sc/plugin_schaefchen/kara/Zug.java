package sc.plugin_schaefchen.kara;

import java.util.List;

import sc.plugin_schaefchen.DebugHint;
import sc.plugin_schaefchen.Move;

public class Zug{
	public Zug(Move move, Spielstatus state) {
		this.move = move;
		this.schaf = state.holeSchafMitIndex(move.sheep);
		this.ziel = state.holeFeldMitIndex(move.target);
	}

	private Move move;
	public Move getMove() {
		return move;
	}

	private Schaf schaf;
	private Feld ziel;
	
	/**
	 * Gibt das Schaf zurück, welches bewegt werden soll
	 */
	public Schaf holeSchaf(){
		return schaf;
	}
	
	/**
	 * Gibt das Feld zurück auf das die Herde ziehen soll
	 */
	public Feld holeZiel(){
		return ziel;
	}
	
	/**
	 * Fuegt eine Hilfestellung hinzu, die in der GUI angezeigt wird und
	 * es einfacher machen kann Fehler zu beheben.
	 * @param name
	 * 		Der Name, der Hilfestellung
	 * @param meldung
	 * 		Die Meldung, der Hilfestellung
	 */
	public void fuegeDebugHilfestellungHinzu(String name, String meldung){
		move.addHint(new DebugHint(name,meldung));
	}
	
	/**
	 * Gibt die aktuell gespeicherten Debug Hilfestellungen zurück!
	 */
	public String[] gibtDebugHilfestellungenZurueck(){
		String[] debugHints = new String[move.getHints().size()];
		for (int i = 0; i < debugHints.length; i++) {
			debugHints[i] = move.getHints().get(i).content;
		}
		return debugHints;
	}
	
	
	
}
