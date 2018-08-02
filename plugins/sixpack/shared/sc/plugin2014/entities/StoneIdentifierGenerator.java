package sc.plugin2014.entities;

/**
 * Klasse, welche die Identifikatoren der Spielsteine generiert.
 * 
 * @author ffi
 * 
 */
public class StoneIdentifierGenerator {
	private static int nextId = 0;

	public static void reset() {
		nextId = 0;
	}

	public static int getNextId() {
		nextId++;
		return nextId;
	}
}
