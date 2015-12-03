package sc.plugin2016;

public enum FieldType {
	/**
	 * Sumpffeld, auf dem wegen des weichen Untergrundes kein Strommast stehen kann
	 */
	SWAMP, 
	/**
	 * Start- / Zielfeld des roten Spielers
	 */
	RED, 
	/**
	 * Start- / Zielfeld des blauen Spielers
	 */
	BLUE, 
	/**
	 * Ein einfaches Feld, auf das ein Strommast gesetzt werden kann
	 */
	NORMAL
}
