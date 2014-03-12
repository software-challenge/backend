package shared.sc.plugin2015;

import static shared.sc.plugin2015.util.Constants.MAX_SEGMENT_SIZE;
import static shared.sc.plugin2015.util.Constants.SELECTION_SIZE;
import shared.sc.plugin2015.util.InvalideMoveException;
import shared.sc.plugin2015.util.SelectMoveConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
/**
 * 
 * Ein Auswahlzug zur Wahl der Bauelemente für einen Spielabschnitt.
 * @see Move
 *
 */
@XStreamAlias(value = "manhattan:select")
@XStreamConverter(SelectMoveConverter.class)
public class SelectMove extends Move implements Cloneable{

	private int[] selections = new int[MAX_SEGMENT_SIZE];

         /**
         * XStream benötigt eventuell einen parameterlosen Konstruktor 
         * bei der Deserialisierung von Objekten aus XML-Nachrichten.
         */
        public SelectMove() {
        }

        /**
	 * Einen Auswahlzug anhand eines int-Arrays erzeugen,
	 * bei dem selections[i] die Anzahl Bausteine mit der
	 * Groesse i+1 angibt.
	 * @param selections die jeweilige Anzahl Bausteine
	 * der Groesse i+1
	 */
	public SelectMove(int[] selections) /*throws IllegalArgumentException*/ {
		for (int i = 0; i < selections.length; i++) {
			if (selections[i] < 0) {
				throw new IllegalArgumentException("Auswahlzug konnte nicht konstruiert werden: Negative Anzahl eines Bauelementes gewählt!");
			}
			this.selections[i] = selections[i];
		}
	}
        /**
         * klont dieses Objekt
         * @return ein neues Objekt mit gleichen Eigenschaften
         * @throws CloneNotSupportedException 
         */
        @Override
        public Object clone() throws CloneNotSupportedException {
            SelectMove clone = (SelectMove) super.clone();
            clone.selections = selections.clone();
            return clone;
        }
	/**
	 * Gibt ein int-Array zurueck, bei dem das i-te Element
	 * fuer die Anzahl gewaehlter Bausteine der Groesse i+1 steht.
	 * @return die jeweilige Anzahl Bausteine der Groesse i+1
	 */
	public int[] getSelections() {
		return selections;
	}
	
	@Override
	void perform(GameState state, Player player) throws InvalideMoveException {

		Segment[] segments = new Segment[MAX_SEGMENT_SIZE];
		for (int i = 1; i <= MAX_SEGMENT_SIZE; i++) {
			segments[i - 1] = player.getSegment(i);
		}
		int selectionSum = 0;
		for (int i = 0; i < MAX_SEGMENT_SIZE; i++) {
			if (selections[i] < 0) {
				throw new InvalideMoveException("Illegale Auswahl: Negative Anzahl eines Bauelementes gewählt");
			}
			selectionSum += selections[i];
			if (segments[i].getRetained() < selections[i]) {
				throw new InvalideMoveException(player.getDisplayName()
						+ " hat nicht genug Bauelemente der Groesse " + (i + 1));
			}
		}

		if (selectionSum != SELECTION_SIZE) {
			throw new InvalideMoveException(player.getDisplayName() + " hat nicht genau " + SELECTION_SIZE
					+ " Bauelemente gewählt");
		}

		for (int i = 0; i < MAX_SEGMENT_SIZE; i++) {
			segments[i].select(selections[i]);
		}

	}


	@Override
	public MoveType getMoveType() {
		return MoveType.SELECT;
	}
}
