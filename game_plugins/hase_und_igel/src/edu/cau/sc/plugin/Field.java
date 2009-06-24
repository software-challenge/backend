package edu.cau.sc.plugin;

public class Field
{
	/**
	 * Different types of fields a player can encounter in <code>HaseUndIgel</code>
	 * The descriptions are taken from the german manual.
	 * 
	 * Auf jedem Feld darf immer nur eine Figur stehen; man zieht also immer nur 
	 * auf freie Felder. Man darf besetzte Felder überspringen und zählt sie mit.
	 * @author rra
	 */
	public static enum FieldTypes
	{
		/**
		 * 
		 */
		START,
		
		/**
		 * Es geht darum, als erster ins Ziel einzulaufen. Dies ist erst möglich,
		 * nachdem man unterwegs seine Salat-Karten losgeworden ist. Außerdem darf
		 * man, wenn man die Ziellinie überschreitet, nicht zu viele Karotten
		 * übrig haben (<=10x Position)
		 */
		GOAL,
		
		/**
		 * Beim Vorwärtsziehen darf man nicht auf Igel-Feldern landen. Diese Felder
		 * werden nur durch Rückwärtsziehen erreicht. Dabei zahlt man nichts,
		 * sondern erhült im Gegenteil viele Karotten. Man darf nur zum nächsten
		 * Igel-Feld zurückziehen und auch das nur, wenn es unbesetzt ist. Man 
		 * zählt die Zahl der Felder, die man bis zum Igel-Feld zurückgeht und 
		 * erhält das 10fache dieser Zahl an Karotten auf den Vorrat.
		 * Man kann von einem Igel-Feld im nächsten Zug wieder vorwärts gehen
		 * oder auch weiter zurück zum nächsten Igel-Feld ziehen.
		 */
		HEDGEHOG,
		
		/**
		 * Jeder Spieler muß Stück für Stück seine drei Salatkarten loswerden, 
		 * weil er sonst nicht ins Ziel laufen darf; man nennt das "seinen Salat
		 * fressen".
		 * Den Salat darf man nur auf den Salat-Feldern fressen und nur zu diesen
		 * Zweck darf man Salat-Felder betreten. Wer all seinen Salat gefressen 
		 * hat, darf nicht mehr auf ein Salat-Feld.
		 * Wenn man auf dem Salat-Feld landet, geschieht zunächst nichts. Wenn
		 * man das nächste Mal an der Reihe ist, zieht man nicht, sondern gibt
		 * eine seiner Salat-Karten ab. Für die abgegebene Salat-Karte erhält man
		 * Karotten und zwar entsprechend der Position, in der man sich in diesem
		 * Augenblick befindet.
		 * Wenn man das übernächste Mal an der Reihe ist, muß man vom Salat-Feld
		 * weiterziehen. Man kann also nicht unmittelbar nacheinander zwei
		 * Salat-Karten auf dem gleichen Salat-Feld loswerden. Man darf aber
		 * mehrmals auf dem gleichen Salat-Feld landen, wenn man dazwischen auf
		 * ein Igel-Feld zurückgeht und dann wieder vorwärts zieht.
		 */
		SALAD,
		
		/**
		 * Wenn man auf einem Karotten-Feld landet und eim nächsten Mal normal 
		 * weiterzieht, geschieht gar nichts. Man darf aber aus diesen Feldern
		 * aussetzen und erhält dafür 10 Karotten auf Vorrat, ohne Rücksicht
		 * auf die Position, in der man sich befindet. Man kann dies so oft man
		 * will wiederholen, also immer wieder aussetzen und dafür 10 Karotten 
		 * einnehmen.
		 * Man darf auf den Karotten-Feldern auch Karotten loswerden. Dies ist
		 * besonders gegen Ende des Rennens wichtig, wenn man noch zu viele
		 * Karotten auf der Hand hat. In diesem Fall bleibt man ebenfalls anstatt
		 * zu ziehen auf dem Karotten-Feld stehen und gibt dafür 10 Karotten in 
		 * den Vorrat ab. Auch dies kann man so oft man will wiederholen.
		 */
		CARROT,
		
		/**
		 * Wenn man auf einem Hasen-Feld landet, nimmt man sofort die oberste 
		 * Hasen-Karte und führt die dort gegebene Anordnung aus. Die Hasen-Karte
		 * wird weider  unter den Stapel zurückgesteckt.
		 * Die Anordnung auf den Hasen-Karten gehen den sonst für die Züge und für
		 * die einzelnen Felder geltenden Regeln vor. 
		 */
		RABBIT,
		
		EMPTY,
		FIRST,
		SECOND,
		THIRD,
		FOURTH,
		SPECIAL,
	}

	private FieldTypes	type;
	private Player		occupiedBy;

	public Field(FieldTypes type)
	{
		this.type = type;
	}

	public FieldTypes getType()
	{
		return this.type;
	}

	/**
	 * Sets the <code>Player</code> sitting on this field. Other
	 * players won't be able to move onto this field anymore.
	 * @param occupiedBy
	 */
	public void setOccupiedBy(Player occupiedBy)
	{
		this.occupiedBy = occupiedBy;
	}

	public boolean isOccupied()
	{
		return this.occupiedBy != null;
	}
}
