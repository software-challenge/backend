package sc.plugin2014.moves;

import java.util.*;
import sc.plugin2014.GameState;
import sc.plugin2014.converters.LayMoveConverter;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;
import sc.plugin2014.util.GameUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Ein Bauzug. Dieser beinhaltet Informationen, welcher Baustein wohin gesetzt
 * wird.
 * 
 */
@XStreamAlias(value = "qw:laymove")
@XStreamConverter(LayMoveConverter.class)
public class LayMove extends Move implements Cloneable {

    public final Map<Stone, Field> stoneToFieldMapping;

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
    public LayMove() {
        stoneToFieldMapping = new HashMap<Stone, Field>();
    }

    public void layStoneOntoField(Stone stone, Field field) {
        if (stone == null) {
            throw new IllegalArgumentException("Stein darf nicht null sein");
        }

        if (field == null) {
            throw new IllegalArgumentException("Feld darf nicht null sein");
        }

        stoneToFieldMapping.put(stone, field);
    }

    public void clearStoneToFieldMapping() {
        stoneToFieldMapping.clear();
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

        boolean allStonesBelongToPlayer = GameUtil.compareStoneList(
                new LinkedList<Stone>(stoneToFieldMapping.keySet()),
                player.getStones());

        if (!allStonesBelongToPlayer) {
            throw new InvalidMoveException(
                    "Ein übergebener Stein gehört nicht dem Spieler");
        }

    }

}
