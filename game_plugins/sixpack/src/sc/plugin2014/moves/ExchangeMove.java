package sc.plugin2014.moves;

import java.util.ArrayList;
import java.util.List;
import sc.plugin2014.GameState;
import sc.plugin2014.converters.ExchangeMoveConverter;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.exceptions.InvalidMoveException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias(value = "qw:exchangemove")
@XStreamConverter(ExchangeMoveConverter.class)
public class ExchangeMove extends Move implements Cloneable {

    private final List<Stone> stones;

    public ExchangeMove() {
        stones = new ArrayList<Stone>();
    }

    public ExchangeMove(List<Stone> stones) {
        this.stones = stones;
    }

    public List<Stone> getStonesToExchange() {
        return stones;
    }

    @Override
    public void perform(GameState state, Player player)
            throws InvalidMoveException {
        super.perform(state, player);

        checkAtLeastOneStone();

        checkIfStonesAreFromPlayerHand(getStonesToExchange(), player);

        checkIfPlayerHasStoneAmountToExchange(player);

        List<Integer> freePositions = new ArrayList<Integer>();
        List<Stone> putAsideStones = new ArrayList<Stone>();

        int stonesToExchangeSize = getStonesToExchange().size();

        for (int i = 0; i < stonesToExchangeSize; i++) {
            Stone stoneExchange = getStonesToExchange().get(i);

            freePositions.add(player.getStonePosition(stoneExchange));
            player.removeStone(stoneExchange);
            putAsideStones.add(stoneExchange);
        }

        for (int i = 0; i < stonesToExchangeSize; i++) {
            Stone stone = state.drawStone();
            if (stone != null) {
                player.addStone(stone, freePositions.get(i));
            }
            else {
                throw new InvalidMoveException(
                        "Der Beutel ist leer - Tauschen nicht möglich");
            }
        }

        for (Stone stone : putAsideStones) {
            state.putBackStone(stone);
        }

        state.updateStonesInBag();
    }

    private void checkIfPlayerHasStoneAmountToExchange(Player player)
            throws InvalidMoveException {
        if (getStonesToExchange().size() > player.getStones().size()) {
            throw new InvalidMoveException(
                    "Nicht ausreichend Steine auf der Hand um "
                            + getStonesToExchange().size()
                            + " Steine zu tauschen");
        }
    }

    private void checkAtLeastOneStone() throws InvalidMoveException {
        if (getStonesToExchange().isEmpty()) {
            throw new InvalidMoveException(
                    "Es muss mindestens 1 Stein getauscht werden");
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO
        return super.clone();
    }

}
