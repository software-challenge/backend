package sc.plugin2014.moves;

import java.util.List;
import sc.plugin2014.GameState;
import sc.plugin2014.converters.LayMoveConverter;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.exceptions.InvalidMoveException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Ein Bauzug. Dieser beinhaltet Informationen, welcher Baustein wohin gesetzt
 * wird.
 * 
 */
@XStreamAlias(value = "qw:exchangemove")
@XStreamConverter(LayMoveConverter.class)
public class ExchangeMove extends Move implements Cloneable {

    public List<Stone> stones;

    /**
     * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
     * Deserialisierung von Objekten aus XML-Nachrichten.
     */
    public ExchangeMove() {
        stones = null;
    }

    /**
     * 
     * Erzeugt einen neuen Bauzug mit Stadt, Position und Bauteilgroesse
     * 
     * @param city
     *            Index der Zielstadt
     * @param slot
     *            Index der Zielposition
     * @param size
     *            Groesse des Bausteins
     */
    public ExchangeMove(List<Stone> stones) {
        this.stones = stones;
    }

    /**
     * klont dieses Objekt
     * 
     * @return ein neues Objekt mit gleichen Eigenschaften
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {

        // TODO
        return super.clone();
    }

    @Override
    public void perform(GameState state, Player player)
            throws InvalidMoveException {

        if (stones.isEmpty()) {
            throw new InvalidMoveException(
                    "Es muss mindestens 1 Stein getauscht werden");
        }

        for (Stone stoneOnHand : player.getStones()) {
            boolean notFound = true;
            for (Stone stoneExchange : stones) {
                if (stoneExchange == stoneOnHand) {
                    notFound = false;
                }
            }

            if (notFound) {
                throw new InvalidMoveException(
                        "Der Stein muss von der Hand sein");
            }
        }

        for (Stone stoneExchange : stones) {
            player.removeStone(stoneExchange);
        }

        for (int i = 0; i < stones.size(); i++) {
            player.addStone(state.drawStone());
        }
    }

}
