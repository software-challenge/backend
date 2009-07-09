package sc.plugin2010;

/**
 * @author rra
 * @since Jul 4, 2009
 *
 */
public final class Move
{
	/**
	 * Die aus Hase- und Igel bekannten Aktionen, sowie die Erweiterungen der
	 * CAU-Kiel
	 */
	public enum MoveTyp {
		/**
		 * Ziehe <code>n</code> Felder vor
		 */
		MOVE,
		/**
		 * Iß einen Salat. Nur verfügbar wenn der Spieler auf einem Salatfeld steht
		 */
		EAT,
		/**
		 * Nehme 10 Karotten auf einem Karottenfeld auf
		 */
		TAKE_10_CARROTS,
		/**
		 * Lege 10 Karotten auf einem Karottenfeld ab
		 */
		DROP_10_CARROTS,
		/**
		 * Falle zum letzten Igelfeld zurück. Nur verfügbar, wenn ein unbesetztes 
		 * Igelfeld hinter dem Spieler vorhanden ist.
		 */
		FALL_BACK,
		/**
		 * Spielt die <code>n</code>. Hasenkarte von der Hand aus 
		 * EXPERIMENTAL Erweiterung der CAU-Kiel
		 */
		PLAY_CARD,
	}
	
	private final int n;
	private final MoveTyp typ;
	
	public Move(final MoveTyp typ)
	{
		this.typ = typ;
		n = 0;
	}
	
	public Move(final MoveTyp typ, final int n)
	{
		this.typ = typ;
		this.n = n;
	}
	
	public final int getN()
	{
		return n;
	}
	
	public final MoveTyp getTyp()
	{
		return typ;
	}
}
