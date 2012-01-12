package sc.plugin2013;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.plugin2013.util.Constants;

@XStreamAlias(value = "cartagena:board")
public class Board implements Cloneable{

	private List<Field> fields;

	public Board() {
		this.init();
	}

	private void init() {
		// Größe Festgelegt durch Startfeld, Zielfeld und Segmente * Symbole
		fields = new ArrayList<Field>(Constants.SEGMENTS * 6 + 2);
		fields.add(new Field(FieldType.START));
		for (int i = 0; i < Constants.SEGMENTS; i++) {
			LinkedList<Field> segment = new LinkedList<Field>();
			for (SymbolType s : SymbolType.values()) {
				segment.add(new Field(s));
			}
			Collections.shuffle(segment, new SecureRandom());

			for (int j = 0; j < 6; j++) {
				fields.add(segment.get(j));
			}
		}
		fields.add(new Field(FieldType.FINISH));
	}

	public Field getField(int index) {
		return fields.get(index);
	}

	/**
	 * Sucht das nächste Feld, welches das übergebene Symbol trägt und nicht
	 * belegt ist, vom Startindex aus.
	 * 
	 * @param start
	 * @param symbol
	 * @return Der Index des nächsten Feldes welches das übergebene Symbol
	 *         trägt. Gesucht wird ab StartIndex
	 */
	public int getNextField(int start, SymbolType symbol) {
		for (int i = start + 1; i < fields.size(); i++) {
			if (fields.get(i).symbol == symbol
					&& fields.get(i).getPirates().isEmpty()) {
				return i;
			}
		}
		return fields.size() - 1; // nichts gefunden, gib FinishFeld zurück
	}

	/**
	 * @param index
	 * @return Liste mit Piraten auf dem Feld mit index
	 */
	public List<Pirate> getPirates(int index) {
		return fields.get(index).getPirates();
	}

	/**
	 * Gibt das nächste zurückliegende Feld zurück, auf welchem sich Piraten
	 * befinden
	 * 
	 * @param start
	 * @return index des Feldes
	 */
	public int getPreviousField(int start) {
		for (int i = start - 1; i >= 0; i--) {
			if (!fields.get(i).getPirates().isEmpty()) {
				return i;
			}
		}
		return 0; // Nichts gefunden, gib StartFeld zurück;
	}

}
