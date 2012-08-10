package sc.plugin2013;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.plugin2013.util.Constants;

/**
 * Klasse welche eine Spielbrett darstellt. Beherbergt eine Liste von Feldern (
 * {@link Field}).
 * 
 * @author fdu
 * 
 */
@XStreamAlias(value = "cartagena:board")
public class Board implements Cloneable {

	private List<Field> fields;

	public Board() {
		this.init();
	}

	private void init() {
		// Größe Festgelegt durch Startfeld, Zielfeld und Segmente * Symbole
		fields = new ArrayList<Field>(Constants.SEGMENTS * Constants.SYMBOLS
				+ 2);
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
		// Fülle das Startfeld mit Piraten
		Field start = fields.get(0);
		for (int i = 0; i < Constants.PIRATES; i++) {
			start.putPirate(new Pirate(PlayerColor.RED));
			start.putPirate(new Pirate(PlayerColor.BLUE));
		}
		// FIXME: test für den letzten zug. Piraten im letzten Segment
//		 fields.get(fields.size()-8).putPirate(new Pirate(PlayerColor.RED));
//		 fields.get(fields.size()-7).putPirate(new Pirate(PlayerColor.RED));
//		 fields.get(fields.size()-2).putPirate(new Pirate(PlayerColor.BLUE));
//		 fields.get(fields.size()-1).putPirate(new Pirate(PlayerColor.BLUE));
//		 fields.get(fields.size()-1).putPirate(new Pirate(PlayerColor.BLUE));
//		 fields.get(fields.size()-1).putPirate(new Pirate(PlayerColor.BLUE));
//		 fields.get(fields.size()-1).putPirate(new Pirate(PlayerColor.BLUE));
//		 fields.get(fields.size()-1).putPirate(new Pirate(PlayerColor.BLUE));
	}

	/**
	 * Gibt das Feld am übergebenen Index zurück
	 * 
	 * @param index
	 * @return das Feld am index
	 */
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
		if (start == this.size() - 1) {
			// startFeld ist Zielfeld. Zug dorthin nicht möglich.
			return -1;
		}
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
	 * Gibt die Anzahl der Piraten eines Spielers auf einem bestimmten Feld
	 * zurück
	 * 
	 * @param index
	 * @param color
	 * @return Die Anzahl der Piraten von Spieler color auf dem Feld index
	 */
	public int numPiratesOf(int index, PlayerColor color) {
		int num = 0;
		Iterator<Pirate> pirateIterator = this.getPirates(index).iterator();
		while (pirateIterator.hasNext()) {
			if (pirateIterator.next().getOwner().equals(color)) {
				num++;
			}
		}
		return num;
	}

	/**
	 * Gibt das nächste zurückliegende Feld zurück, auf welchem sich Piraten
	 * befinden
	 * 
	 * @param start
	 * @return index des Feldes
	 */
	public int getPreviousField(int start) {
		// Suche zurückliegendes Feld auf dem, weniger als 3 Piraten stehen
		for (int i = start - 1; i > 0; i--) {
			if (!fields.get(i).getPirates().isEmpty()
					&& fields.get(i).getPirates().size() < 3) {
				return i;
			}
		}
		return -1; // Nichts gefunden, gib -1 zurück;
	}

	/**
	 * Gibt zurück ob ein Spieler an einer Position einen Piraten hat.
	 * 
	 * @param index
	 *            Die Spielbrettposition
	 * @param color
	 *            Die Spielerfarbe
	 * @return true wenn sich ein Pirat des Spielers an der Position befindet
	 */
	public boolean hasPirates(int index, PlayerColor color) {
		List<Pirate> list = getPirates(index);
		boolean ret = false;
		for (Pirate p : list) {
			if (p.getOwner() == color) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * Bewegt einen Piraten von einem Startfeld auf ein Zielfeld. Diese Methode
	 * ist nur für den Server relevant.
	 * 
	 * @param field
	 *            das Startfeld auf dem sich der Pirat befindet
	 * @param nextField
	 *            das Zielfeld auf das der Pirat bewegt werden soll
	 * @param color
	 *            die Farbe des Besitzers
	 */
	public void movePirate(int field, int nextField, PlayerColor color) {
		Pirate pirate = fields.get(field).removePirate(color);
		fields.get(nextField).putPirate(pirate);
	}

	/**
	 * Gibt die Größe des Spielbrettes zurück. Also die Anzahl der Spielfelder
	 * inklusive Start und Zielfeld.
	 * 
	 * @return
	 */
	public int size() {
		return fields.size();
	}

	/**
	 * Gibt eine deep copy des Objektes zurück. Von allen Objekten, welche diese
	 * Klasse beherbergt werden auch Kopien erstellt.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Board clone = (Board) super.clone();
		if (fields != null) {
			clone.fields = new LinkedList<Field>();
			for (Field f : this.fields) {
				clone.fields.add((Field) f.clone());
			}
		}
		return clone;
	}

}
