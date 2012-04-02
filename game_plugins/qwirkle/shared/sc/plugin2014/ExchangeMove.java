package sc.plugin2014;

import javax.swing.text.Segment;
import sc.plugin2014.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Ein Bauzug. Dieser beinhaltet Informationen, welcher Baustein wohin gesetzt
 * wird.
 * 
 */
@XStreamAlias(value = "qw:exchangemove")
@XStreamConverter(BuildMoveConverter.class)
public class ExchangeMove extends Move implements Cloneable {

    public final int city;

    public final int slot;

    public final int size;

    /**
     * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
     * Deserialisierung von Objekten aus XML-Nachrichten.
     */
    public ExchangeMove() {
        city = -1;
        slot = -1;
        size = -1;
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
    public ExchangeMove(int city, int slot, int size) {
        this.city = city;
        this.slot = slot;
        this.size = size;
    }

    /**
     * klont dieses Objekt
     * 
     * @return ein neues Objekt mit gleichen Eigenschaften
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    void perform(GameState state, Player player) throws InvalideMoveException {

        if ((0 > city) || (city >= Constants.CITIES)) {
            throw new InvalideMoveException("Es gibt keine Stadt mit Index "
                    + city);
        }

        if ((0 > slot) || (slot >= Constants.SLOTS)) {
            throw new InvalideMoveException(
                    "Es gibt keinen Bauplatz mit Index " + slot);
        }

        if ((0 >= size) || (size > Constants.MAX_SEGMENT_SIZE)) {
            throw new InvalideMoveException(
                    "Es gibt kein Bauelement der Größe " + size);
        }

        Stone tower = state.getTower(city, slot);
        PlayerColor color = player.getPlayerColor();
        Segment segment = player.getSegment(size);

        if ((segment == null) || (segment.getUsable() < 1)) {
            throw new InvalideMoveException(player.getDisplayName()
                    + " hat kein Bauelement der Größe " + size);
        }

        if (!player.hasCard(slot)) {
            throw new InvalideMoveException(player.getDisplayName()
                    + " hat keine Karte für den Bauplatz " + (slot + 1)); // Bugfix
                                                                          // #729

        }

        segment.use();
        /*
         * first remove card, then add new, because if stack is mixed, the just
         * used card
         * needs to be mixed too
         */
        player.removeCard(slot);
        player.addCard(state.drawCard());
    }

    @Override
    public MoveType getMoveType() {
        return MoveType.BUILD;
    }

}
