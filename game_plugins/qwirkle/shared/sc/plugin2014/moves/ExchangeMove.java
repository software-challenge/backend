package sc.plugin2014.moves;

import java.util.ArrayList;
import java.util.List;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.exceptions.InvalidMoveException;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "qw:exchangemove")
// @XStreamConverter(ExchangeMoveConverter.class)
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

        List<Integer> freePositions = new ArrayList<Integer>();
        ArrayList<Stone> putAsideStones = new ArrayList<Stone>();

        int stonesToExchangeSize = getStonesToExchange().size();

        for (int i = 0; i < stonesToExchangeSize; i++) {
            Stone stoneExchange = getStonesToExchange().get(i);

            freePositions.add(player.getStonePosition(stoneExchange));
            player.removeStone(stoneExchange);
            putAsideStones.add(stoneExchange);
        }

        for (int i = 0; i < stonesToExchangeSize; i++) {
            player.addStone(state.drawStone(), freePositions.get(i));
        }

        for (Stone stone : putAsideStones) {
            state.putBackStone(stone);
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
