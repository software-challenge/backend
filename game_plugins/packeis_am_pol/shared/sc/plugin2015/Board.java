package sc.plugin2015;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.plugin2015.util.Constants;

/**
 * Klasse welche eine Spielbrett darstellt. Bestehend aus einem  
 * zweidimensionalen Array aus Fields
 * 
 * @author soed
 * 
 */
@XStreamAlias(value = "board")
public class Board implements Cloneable {

	private Field[] [] fields;

	public Board() {
		this.init();
	}

	private void init() {
		
		fields = new Field[Constants.ROWS][Constants.COLOUMS];
		int oneFish = Constants.ONE_FISH;
		int twoFish = Constants.TWO_FISH;
		int threeFish = Constants.THREE_FISH;
		int rnd;
		while(oneFish!=0 && twoFish!=0 && threeFish!=0)
		{
			for(int x=0;x<Constants.COLOUMS;x++)
			{
				for(int y=0; y<Constants.ROWS;y++)
				{
					 rnd = (int) (Math.random()*3+1);
					 switch(rnd){
					 case 1: 
						 if(oneFish!=0)
						 {
							 fields[x][y].fish=1;
							 oneFish--;
						 }	
						 else{
							 x--;
							 y--;
						 }
						 
					 break;
					 case 2: 
						 if(twoFish!=0)
						 {
							 fields[x][y].fish=2;
							 twoFish--;
						 }	
						 else{
							 x--;
							 y--;
						 }
					 break;
					 case 3: 
						 if(threeFish!=0)
						 {
							 fields[x][y].fish=3;
							 threeFish--;
						 }	
						 else{
							 x--;
							 y--;
						 }
					 break;
					 }
					 
				}
			}
		}
		}
		/**
		 * Fische werden  zufällig auf dem Spielbrett verteilt werden
		 */
	
		
	

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
	public Penguin getPirates(int x, int y) {
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
	 * ist nur fÃ¼r den Server relevant.
	 * 
	 * @param field_x
	 * @param field_y
	 *            das Startfeld auf dem sich der Pinguin befindet
	 * @param nextField_x
	 * @param nextField_y
	 *            das Zielfeld auf das der Pinguin bewegt werden soll
	 * @param color
	 *            die Farbe des Besitzers
	 */
	public void movePenguin(int field_x, int field_y, int nextField_x, int nextField_y, PlayerColor color) {
		Penguin penguin = fields[field_x][field_y].removePenguin(color);
		fields[nextField_x][nextField_y].putPenguin(penguin);
	}

	/**
	 * Gibt eine deep copy des Objektes zurÃ¼ck. Von allen Objekten, welche diese
	 * Klasse beherbergt werden auch Kopien erstellt.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Board clone = (Board) super.clone();
		return clone;
		
	}
}


