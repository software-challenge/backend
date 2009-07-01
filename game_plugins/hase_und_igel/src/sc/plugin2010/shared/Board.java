package sc.plugin2010.shared;

import java.util.List;

/**
 * @author rra
 * @since Jul 1, 2009
 * 
 */
public class Board
{
	/**
	 * Mögliche Aktionen, die durch das Ausspielen einer Hasenkarte ausgelöst
	 * werden können.
	 */
	public enum Action
	{
		/**
		 * Einmal aussetzen
		 */
		SUSPENDED,
		/**
		 * Ziehe ein Hasenfeld vor
		 */
		NEXT_RABBIT_FIELD,
		/**
		 * Falle ein Hasenfeld zurück
		 */
		LAST_RABBIT_FIELD,
		/**
		 * Ziehe gleich noch einmal
		 */
		MOVE_AGAIN,
		/**
		 * Der Zug war kostenlos
		 */
		FREE_MOVEMENT,
		/**
		 * Nehme 10 Karotten auf
		 */
		TAKE_10_CARROTS,
		/**
		 * Gebe 10 Karotten ab
		 */
		DROP_10_CARROTS,
		/**
		 * Iß sofort einen Salat
		 */
		EAT_SALAD,
		/**
		 * Falle eine Position zurück
		 */
		FALL_BACK,
		/**
		 * Rücke eine Position vor
		 */
		HURRY_AHEAD
	}

	/**
	 * Die unterschiedlichen Spielfelder aus dem Hase und Igel Original
	 */
	public enum FieldTyp
	{
		/**
		 * Zahl- und Flaggenfelder
		 */
		POSITION_1, POSITION_2, POSITION_3, POSITION_4, POSITION_5, POSITION_6,
		/**
		 * Igelfeld
		 */
		HEDGEHOG,
		/**
		 * Salatfeld
		 */
		SALAD,
		/**
		 * Karottenfeld
		 */
		CARROT,
		/**
		 * Hasenfeld
		 */
		RABBIT
	}

	private List<FieldTyp>	track;
	private List<Player>	players;
	
	private List<Action>	unusedCards;
	private List<Action>	usedCards;
}
