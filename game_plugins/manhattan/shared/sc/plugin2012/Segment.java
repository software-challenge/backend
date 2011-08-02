package sc.plugin2012;

import sc.plugin2012.util.SegmentConverter;

import com.thoughtworks.xstream.annotations.XStreamConverter;
/**
 * 
 * Klasse für die Verwaltung einer Bauteilart (Art=Groesse), unabhaengig
 * vom Spieler. <br/>
 * Das heisst um eine Spielsituation komplett zu modellieren
 * benoetigt man pro Spieler/Groesse-Kombination ein Segmentobjekt.
 *
 */
@XStreamConverter(SegmentConverter.class)
public class Segment {

	// segmentgroesse
	public final int size;

	// anzahl verwendeter segmente dieser groesse
	private int used;

	// anzahl verfügbarere segmente dieser groesse
	private int usable;

	// anzahl aufbewahrter segmente dieser groesse
	private int retained;
	/**
	 * Erzeugt einen Bauteilvorrat mit {@code amount} Elementen der Groesse size
	 * @param size Groesse der Bauteile
	 * @param amount Anzahl der Bauteile
	 */
	public Segment(int size, int amount) {
		this.size = size;
		used = 0;
		usable = 0;
		retained = amount;
	}

	/**
	 * Liefert die Anzahl bereits verwendeter Bauteile dieser Art
	 * @return Anzahl bereits verwendeter Bauteile
	 */
	public int getUsed() {
		return used;
	}

	/**
	 * Liefert die Anzahl momentan zur Verfuegung stehender Bauteile dieser Art
	 * @return Anzahl momentan verfuegbarer Bauteile
	 */
	public int getUsable() {
		return usable;
	}

	/**
	 * Liefert die Anzahl noch aufbewahrter Bauteile dieser Art
	 * @return Anzahl noch aufbewahrter Bauteile
	 */
	public int getRetained() {
		return retained;
	}

	/**
	 * Simuliert die Benutzung eines der Bauteile
	 */
	public void use() {
		usable--;
		used++;
	}

	/**
	 * Simuliert das Auswaehlen von Bauteilen
	 * @param amount Anzahl ausgewählter Bauteile
	 */
	public void select(int amount) {
		retained -= amount;
		usable  += amount;
	}

}
