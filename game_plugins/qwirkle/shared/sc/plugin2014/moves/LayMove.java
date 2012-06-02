package sc.plugin2014.moves;

import java.util.*;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;
import sc.plugin2014.laylogic.LayLogicFacade;
import sc.plugin2014.laylogic.PointsCalculator;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "qw:laymove")
// @XStreamConverter(LayMoveConverter.class)
public class LayMove extends Move implements Cloneable {

    private final Map<Stone, Field> stoneToFieldMapping;

    public LayMove() {
        stoneToFieldMapping = new HashMap<Stone, Field>();
    }

    public Map<Stone, Field> getStoneToFieldMapping() {
        return stoneToFieldMapping;
    }

    public void layStoneOntoField(Stone stone, Field field) {
        checkFieldAndStoneNotNull(stone, field);

        getStoneToFieldMapping().put(stone, field);
    }

    private void checkFieldAndStoneNotNull(Stone stone, Field field) {
        if (stone == null) {
            throw new IllegalArgumentException("Stein darf nicht null sein");
        }

        if (field == null) {
            throw new IllegalArgumentException("Feld darf nicht null sein");
        }
    }

    public void clearStoneToFieldMapping() {
        getStoneToFieldMapping().clear();
    }

    @Override
    public void perform(GameState state, Player player)
            throws InvalidMoveException {
        super.perform(state, player);

        checkAtLeastOneStone();

        checkIfStonesAreFromPlayerHand(getStonesToLay(), player);

        LayLogicFacade.checkIfLayMoveIsValid(getStoneToFieldMapping(),
                state.getBoard(), !state.getBoard().hasStones());

        int points = PointsCalculator.getPointsForMove(stoneToFieldMapping,
                state.getBoard());

        player.addPoints(points);

        List<Integer> freePositions = new ArrayList<Integer>();

        int stonesToLaySize = getStonesToLay().size();

        for (int i = 0; i < stonesToLaySize; i++) {
            Stone stoneToLay = getStonesToLay().get(i);

            freePositions.add(player.getStonePosition(stoneToLay));
            player.removeStone(stoneToLay);

            Field field = getStoneToFieldMapping().get(stoneToLay);
            state.layStone(stoneToLay, field.getPosX(), field.getPosY());
        }

        for (int i = 0; i < stonesToLaySize; i++) {
            Stone drawStone = state.drawStone();
            if (drawStone != null) {
                player.addStone(drawStone, freePositions.get(i));
            }
            else {
                // TODO stonebag is empty
            }
        }
    }

    private List<Stone> getStonesToLay() {
        return new LinkedList<Stone>(getStoneToFieldMapping().keySet());
    }

    private void checkAtLeastOneStone() throws InvalidMoveException {
        if (getStoneToFieldMapping().keySet().isEmpty()) {
            throw new InvalidMoveException(
                    "Es muss mindestens 1 Stein gesetzt werden");
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO
        return super.clone();
    }

}
