package sc.plugin2010;

import sc.plugin2010.Player.Action;

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
	public enum MoveTyp
	{
		/**
		 * Ziehe <code>n</code> Felder vor
		 */
		MOVE,
		/**
		 * Iß einen Salat. Nur verfügbar wenn der Spieler auf einem Salatfeld
		 * steht
		 */
		EAT,
		/**
		 * Nehme 10 Karotten auf einem Karottenfeld auf
		 */
		TAKE_OR_DROP_CARROTS,
		/**
		 * Falle zum letzten Igelfeld zurück. Nur verfügbar, wenn ein
		 * unbesetztes
		 * Igelfeld hinter dem Spieler vorhanden ist.
		 */
		FALL_BACK,
		/**
		 * Spielt eine der 4 Hasenkarten von der Hand des Spielers
		 * Veränderung der CAU Kiel
		 */
		PLAY_CARD
	}

	private final int		n;
	private final MoveTyp	typ;
	private final Action	card;

	public Move(final MoveTyp t)
	{
		typ = t;
		card = null;
		n = 0;
	}
	
	public Move(final MoveTyp t, int val)
	{
		typ = t;
		card = null;
		n = val;
	}
	
	public Move(final MoveTyp t, final Action a)
	{
		typ = t;
		card = a;
		n = 0;
	}

	public Move(final MoveTyp t, final Action a, final int val)
	{
		typ = t;
		card = a;
		n = val;
	}

	public final int getN()
	{
		return n;
	}

	public final MoveTyp getTyp()
	{
		return typ;
	}
	
	public Action getCard()
	{
		return card;
	}
}
