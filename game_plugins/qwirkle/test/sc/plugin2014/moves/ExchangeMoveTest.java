package sc.plugin2014.moves;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;

public class ExchangeMoveTest {

    @Test
    public void testExchangeMove() {
        ArrayList<Stone> stones = new ArrayList<Stone>();
        Stone stone = new Stone();
        stones.add(stone);

        ExchangeMove exchangeMove = new ExchangeMove(stones);
        assertEquals(1, exchangeMove.getStonesToExchange().size());
        assertEquals(stone, exchangeMove.getStonesToExchange().get(0));
    }

    @Test
    public void testPerformOneStone() {
        ArrayList<Stone> stones = new ArrayList<Stone>();
        Stone stone = new Stone();
        stones.add(stone);

        Player player = new Player(PlayerColor.RED);
        player.addStone(stone);

        GameState gameState = new GameState();

        ExchangeMove exchangeMove = new ExchangeMove(stones);
        assertEquals(1, exchangeMove.getStonesToExchange().size());

        try {
            exchangeMove.perform(gameState, player);
        }
        catch (InvalidMoveException e) {
            fail("Invalid move when it should not");
        }

        assertEquals(1, player.getStones().size());
        assertNotSame(stone, player.getStones().get(0));

        assertEquals(107, gameState.getStoneCountInBag());
    }

    @Test
    public void testPerformTwoStones() {
        ArrayList<Stone> stones = new ArrayList<Stone>();
        Stone stone = new Stone();
        Stone stone2 = new Stone();
        stones.add(stone);
        stones.add(stone2);

        Player player = new Player(PlayerColor.RED);
        player.addStone(stone);
        player.addStone(stone2);

        GameState gameState = new GameState();

        ExchangeMove exchangeMove = new ExchangeMove(stones);
        assertEquals(2, exchangeMove.getStonesToExchange().size());

        try {
            exchangeMove.perform(gameState, player);
        }
        catch (InvalidMoveException e) {
            fail("Invalid move when it should not");
        }

        assertEquals(2, player.getStones().size());
        assertNotSame(stone, player.getStones().get(0));
        assertNotSame(stone, player.getStones().get(1));
        assertNotSame(stone2, player.getStones().get(0));
        assertNotSame(stone2, player.getStones().get(1));

        assertEquals(106, gameState.getStoneCountInBag());
    }

    @Test
    public void testPerformTwoStonesButOne() {
        ArrayList<Stone> stones = new ArrayList<Stone>();
        Stone stone = new Stone();
        Stone stone2 = new Stone();
        stones.add(stone);

        Player player = new Player(PlayerColor.RED);
        player.addStone(stone);
        player.addStone(stone2);

        GameState gameState = new GameState();

        ExchangeMove exchangeMove = new ExchangeMove(stones);
        assertEquals(1, exchangeMove.getStonesToExchange().size());

        try {
            exchangeMove.perform(gameState, player);
        }
        catch (InvalidMoveException e) {
            fail("Invalid move when it should not");
        }

        assertEquals(2, player.getStones().size());
        assertNotSame(stone, player.getStones().get(0));
        assertNotSame(stone, player.getStones().get(1));
        assertSame(stone2, player.getStones().get(1));

        assertEquals(107, gameState.getStoneCountInBag());
    }

    @Test(expected = InvalidMoveException.class)
    public void testPerformNoStone() throws InvalidMoveException {
        ArrayList<Stone> stones = new ArrayList<Stone>();
        Player player = new Player(PlayerColor.RED);
        GameState gameState = new GameState();

        ExchangeMove exchangeMove = new ExchangeMove(stones);
        assertEquals(0, exchangeMove.getStonesToExchange().size());

        exchangeMove.perform(gameState, player);
    }

    @Test(expected = InvalidMoveException.class)
    public void testPerformNotOwnStone() throws InvalidMoveException {
        ArrayList<Stone> stones = new ArrayList<Stone>();
        Stone stone = new Stone();
        stones.add(stone);

        Player player = new Player(PlayerColor.RED);

        GameState gameState = new GameState();

        ExchangeMove exchangeMove = new ExchangeMove(stones);
        assertEquals(1, exchangeMove.getStonesToExchange().size());

        exchangeMove.perform(gameState, player);
    }

    @Test(expected = InvalidMoveException.class)
    public void testPerformNotOwnStone2() throws InvalidMoveException {
        ArrayList<Stone> stones = new ArrayList<Stone>();
        Stone stone = new Stone();
        Stone stone2 = new Stone();
        stones.add(stone);
        stones.add(stone2);

        Player player = new Player(PlayerColor.RED);
        player.addStone(stone);

        GameState gameState = new GameState();

        ExchangeMove exchangeMove = new ExchangeMove(stones);
        assertEquals(2, exchangeMove.getStonesToExchange().size());

        exchangeMove.perform(gameState, player);
    }

    @Test
    public void testClone() {
        // TODO
    }

}
