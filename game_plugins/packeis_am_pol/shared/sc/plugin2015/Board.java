package sc.plugin2015;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.plugin2015.util.Constants;

/**
 * Klasse welche eine Spielbrett darstellt. Bestehend aus einem
 * zweidimensionalen Array aus Feldern
 * 
 * @author soed
 * 
 */
@XStreamAlias(value = "board")
public class Board implements Cloneable {

	private Field[][] fields;

	public Board() {
		this.init();
	}

	/**
	 * Konstruktor, der entweder ein zufällig generiertes Spielfeld oder ein
	 * leeres erzeugt.
	 * 
	 * @param init
	 *            Zufallsgeneration an/aus
	 */
	public Board(boolean init) {
		if (init)
			this.init();
		else
			this.makeClearBoard();
	}

	/**
	 * generiert ein neues, leeres Spielfeld.
	 */
	private void makeClearBoard() {
		fields = new Field[Constants.ROWS][Constants.COLUMNS];
		for (int x = 0; x < Constants.COLUMNS; x++) {
			for (int y = 0; y < Constants.ROWS; y++) {
				fields[x][y] = new Field();
			}
		}
	}

	/**
	 * Generiert ein neues Spielfeld mit zufällig auf dem Spielbrett verteilten
	 * Fischen.
	 */
	private void init() {

		fields = new Field[Constants.ROWS][Constants.COLUMNS];
		int rnd;

		List<Integer> fish = new LinkedList<Integer>();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < Constants.FISH[i]; j++) {
				fish.add(i + 1);
			}
		}
		for (int x = 0; x < Constants.COLUMNS; x++) {
			for (int y = 0; y < Constants.ROWS; y++) {
				if ((y & 1) == 0 && x == Constants.COLUMNS - 1) {
					fields[x][y] = new Field();
				} else {
					rnd = (int) (Math.random() * fish.size());
					fields[x][y] = new Field(fish.get(rnd));
					fish.remove(rnd);
				}
			}
		}
	}

	/**
	 * Gibt das Feld mit angegebenen Koordinaten zurück
	 * 
	 * @param x
	 * @param y
	 * @return das Feld an den Koordinaten
	 */
	public Field getField(int x, int y) {
		return fields[x][y];
	}

	/**
	 * @param x
	 * @param y
	 * @return Pinguin auf dem Feld mit den Koordinaten
	 */
	public Penguin getPenguin(int x, int y) {
		return fields[x][y].getPenguin();
	}

	/**
	 * Setzt einen Pinguin an gewählte Koordinaten.
	 * 
	 * @param x
	 *            X-Koordinate
	 * @param y
	 *            Y-Koordinate
	 * @param penguin
	 *            Pinguin, der gesetzt werden soll.
	 */
	public void putPenguin(int x, int y, Penguin penguin)
			throws IllegalArgumentException {
		if (x < 0 || y < 0 || x >= Constants.COLUMNS || y >= Constants.ROWS
				|| fields[x][y].getFish() == 0
				|| fields[x][y].getPenguin() != null)
			throw new IllegalArgumentException();
		this.fields[x][y].putPenguin(penguin);
	}

	/**
	 * Gibt zurück, ob sich ein Pinguin auf dem Feld befindet
	 * 
	 * @param x
	 * @param y
	 * @return true falls sich ein Pinguin auf dem Feld befindet
	 */
	public boolean penguin(int x, int y) {
		if (fields[x][y].getPenguin() == null)
			return false;
		else
			return true;
	}

	/**
	 * Gibt zurück, ob ein Spieler an einer Position einen Pinguin hat.
	 * 
	 * @param x
	 * @param y
	 * @param color
	 *            Die Spielerfarbe
	 * @return true wenn sich ein Pinguin des Spielers an der Position befindet
	 */
	public boolean hasPinguin(int x, int y, PlayerColor color) {
		if (fields[x][y].getPenguin() == null)
			return false;
		if (fields[x][y].getPenguin().getOwner() == color)
			return true;
		else
			return false;

	}

	/**
	 * Gibt die Anzahl der Fische an bestimmten Koordinaten zurück
	 * 
	 * @param x
	 * @param y
	 * 
	 * @return integer Anzahl der Fische
	 * 
	 */
	public int getFishNumber(int x, int y) {
		return fields[x][y].getFish();
	}

	/**
	 * Bewegt einen Pinguin von einem Startfeld auf ein Zielfeld.
	 * 
	 * @param fromX
	 * @param fromY
	 *            das Startfeld auf dem sich der Pinguin befindet
	 * @param toX
	 * @param toY
	 *            das Zielfeld auf das der Pinguin bewegt werden soll
	 * @param color
	 *            die Farbe des Besitzers
	 */
	public void movePenguin(int fromX, int fromY, int toX, int toY,
			PlayerColor color) {
		Penguin penguin = fields[fromX][fromY].removePenguin(color);
		fields[fromX][fromY].setFish(0);
		fields[toX][toY].putPenguin(penguin);
	}

	/**
	 * Gibt eine deep copy des Objektes zurück. Von allen Objekten, welche diese
	 * Klasse beherbergt werden auch Kopien erstellt.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Board clone = new Board(false);
		for (int x = 0; x < Constants.COLUMNS; x++) {
			for (int y = 0; y < Constants.ROWS; y++) {
				clone.fields[x][y].fish = this.getField(x, y).getFish();
				if (this.getField(x, y).hasPenguin())
					clone.fields[x][y].putPenguin(new Penguin(this.getPenguin(
							x, y).getOwner()));
				// else
				// clone.fields[x][y].putPenguin(null);
			}
		}
		return clone;

	}

	public boolean equals(Object o) {
		if(!(o instanceof Board))
			return false;
		Board board = (Board) o;
		
		for(int x = 0; x < Constants.COLUMNS; x++) {
			for(int y = 0; y < Constants.ROWS; y++) {
				if(!(this.getPenguin(x, y) == null && board.getPenguin(x, y) == null) 
						&& (!(this.getPenguin(x, y).equals(board.getPenguin(x,y)) || !(this.getFishNumber(x, y) == board.getFishNumber(x, y)))))
						return false;
			}
		}
		return true;
	}
}
