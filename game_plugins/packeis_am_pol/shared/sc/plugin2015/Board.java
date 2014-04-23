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
	 * Fische werden  zufällig auf dem Spielbrett verteilt werden
	 */
	private void init() {
		
		fields = new Field[Constants.ROWS][Constants.COLUMNS];
		int rnd;
		
		List<Integer> fish = new LinkedList<Integer>();
		
		for(int i = 0; i<3; i++) {
			for(int j = 0; j< Constants.FISH[i]; j++) {
				fish.add(i+1);
			}
		}
		for(int x = 0; x < Constants.COLUMNS; x++) {
			for(int y = 0; y < Constants.ROWS; y++) {
				if((y & 1) == 0 && x == Constants.COLUMNS - 1) {
					fields[x][y] = new Field();
				} else {
					rnd = (int) (Math.random()*fish.size());
					fields[x][y] = new Field(fish.get(rnd));
					fish.remove(rnd);
				}
			}
		}
	}
	
		
	

	/**
	 * Gibt das Feld mit angegebenen Koordinaten  zurück
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
	 * Gibt zurück ob sich ein Pinguin auf dem Feld befindet
	 * @param x
	 * @param y
	 * @return true falls sich ein Pinguin auf dem Feld befindet
	 */
	public boolean penguin(int x, int y ) {
		if(fields[x][y].getPenguin()== null)
			return false;
		else 
			return true;
	}


	/**
	 * Gibt zurÃ¼ck ob ein Spieler an einer Position einen Pinguin hat.
	 * 
	 * @param x
	 * @param y
	 * @param color
	 *            Die Spielerfarbe
	 * @return true wenn sich ein Pinguin des Spielers an der Position befindet
	 */
	public boolean hasPinguin(int x, int y, PlayerColor color) {
		if(fields[x][y].getPenguin() == null)
			return false;
		if(fields[x][y].getPenguin().getOwner()== color)
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
	public int getFish(int x, int y)
	{
		return fields[x][y].getFish();
	}

	/**
	 * Bewegt einen Pinguin von einem Startfeld auf ein Zielfeld. Diese Methode
	 * ist nur für den Server relevant.
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
	public void movePenguin(int fromX, int fromY, int toX, int toY, PlayerColor color) {
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
	protected Object clone() throws CloneNotSupportedException {
		Board clone = new Board();
		for(int x = 0; x < Constants.COLUMNS; x++) {
			for(int y = 0; y < Constants.ROWS; y++) {
				clone.fields[x][y].fish = this.getField(x, y).getFish();
				clone.fields[x][y].putPenguin(new Penguin(this.getPenguin(x, y).getOwner()));
			}
		}
		return clone;
		
	}
}


