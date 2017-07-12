package sc.plugin2018;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GamePlayTest
{
	private Game game;
	private GameState	state;
	private Player	red;
	private Player	blue;

	// TODO test multiply cards (etc: fall_back->hurry_ahead->EatSalad) may need to construct board to test this
  // TODO check the mustMove criteria?

	@Before
	public void beforeEveryTest() throws RescuableClientException
	{
		game = new Game();
		state = game.getGameState();
		red = state.getRedPlayer();
		blue = state.getBluePlayer();
	}

	/**
   * Both players start on the start field (index 0), red has to make the first move
	 */
	@Test
	public void firstRound()
	{
		Assert.assertEquals(red.getPlayerColor(), PlayerColor.RED);
		Assert.assertEquals(blue.getPlayerColor(), PlayerColor.BLUE);

		Assert.assertEquals(0, red.getFieldIndex());
		Assert.assertEquals(0, blue.getFieldIndex());
		Assert.assertEquals(red, state.getStartPlayer());
	}

	/**
	 * There is only one possible move at the start of a game
	 */
	@Test
	public void justStarted()
	{
	  // player cannot fall_back from start field
		Assert.assertFalse(GameRuleLogic.isValidToFallBack(state));
		// player cannot take carrots at start field
		Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 10));
		// player cannot eat salad at start field
    Assert.assertFalse(GameRuleLogic.isValidToPlayEatSalad(state));

    // the play must be able to get to a carrot field
    Assert.assertTrue(GameRuleLogic.isValidToAdvance(state, state.getNextFieldByType(FieldType.CARROT, 0)));
	}

	/**
	 * Tests the cases in which takeOrDrop10Carrots is invalid
	 */
	@Test
	public void takeOrDropCarrots()
	{
		red.setFieldIndex(0);
		Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 10));

		int rabbitAt = state.getNextFieldByType(FieldType.HARE, 0);
		red.setFieldIndex(rabbitAt);
		Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 10));
    Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 9));

		int saladAt = state.getNextFieldByType(FieldType.SALAD, 0);
		red.setFieldIndex(saladAt);
		Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 10));

		int pos1 = state.getNextFieldByType(FieldType.POSITION_1, 0);
		red.setFieldIndex(pos1);
		Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 10));

		int pos2 = state.getNextFieldByType(FieldType.POSITION_2, 0);
		red.setFieldIndex(pos2);
		Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 10));
	}
//
	/**
	 * Checks if round is set correctly and currentPlayer is updated
	 *
	 */
	@Test
	public void turnCounting() throws RescuableClientException, InvalidMoveException
	{
    List<Action> actions = new ArrayList<>();
		Assert.assertEquals(0, state.getRound());
		int firstCarrot = state.getNextFieldByType(FieldType.CARROT, red
				.getFieldIndex());
		actions.add(new Advance(firstCarrot));
		Move move = new Move(actions);
		move.perform(state);

		Assert.assertEquals(0, state.getRound());
		int nextCarrot = state.getNextFieldByType(FieldType.CARROT, red
				.getFieldIndex());
		actions.clear();
		actions.add(new Advance(nextCarrot - blue.getFieldIndex()));
		move = new Move(actions);
		move.perform(state);

		Assert.assertEquals(1, state.getRound());
		int rabbitAt = state.getNextFieldByType(FieldType.HARE, red
				.getFieldIndex());
		actions.clear();
		actions.add(new Advance(rabbitAt - red.getFieldIndex()));
		actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, 20, 1));
    move = new Move(actions);
    move.perform(state);

		Assert.assertEquals(1, state.getRound());
		nextCarrot = state
				.getNextFieldByType(FieldType.CARROT, blue.getFieldIndex());
		actions.clear();
		actions.add(new Advance(nextCarrot - blue.getFieldIndex()));
    move = new Move(actions);
		move.perform(state);

		Assert.assertEquals(red, state.getCurrentPlayer());

		Assert.assertEquals(2, state.getRound());
	}

	/**
	 * Checks reaching of goal
	 *
	 */
	@Test
	public void enterGoalCycle() throws RescuableClientException, InterruptedException, InvalidMoveException
	{
		int lastCarrot = state.getPreviousFieldByType(FieldType.CARROT, 64);
		int preLastCarrot = state
				.getPreviousFieldByType(FieldType.CARROT, lastCarrot);
		red.setFieldIndex(lastCarrot);
		blue.setFieldIndex(preLastCarrot);

		red.setCarrotsAvailable(GameRuleLogic.calculateCarrots(64 - lastCarrot));
		blue
				.setCarrotsAvailable(GameRuleLogic
						.calculateCarrots(64 - preLastCarrot) + 1);
		red.setSalads(0);
		blue.setSalads(0);
		List<Action> actions = new ArrayList<>();
		actions.add(new Advance(64 - red.getFieldIndex()));
		Move move = new Move(actions);

		move.perform(state);
		Assert.assertTrue(red.inGoal());

		actions.clear();
		actions.add(new Advance(64 - blue.getFieldIndex()));
		move = new Move(actions);
		move.perform(state);
		Assert.assertTrue(blue.inGoal());
		Assert.assertTrue(state.isFirst(red));
		Assert.assertTrue(game.checkGameOverCondition());
		Assert.assertEquals(PlayerColor.RED, game.checkGoalReached().getPlayerColor());
	}

	/**
	 * Only when newly entering a rabbit field a card has to be played
	 *
	 */
	@Test
	public void mustPlayCard() throws InvalidMoveException
	{
	  List<Action> actions = new ArrayList<>();
	  actions.add(new Advance(state.getNextFieldByType(FieldType.HARE,0)));
	  actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, 1));
    Move move = new Move(actions);
		move.perform(state);

    actions.clear();
    actions.add(new Advance(state.getNextFieldByType(FieldType.CARROT,0)));
    move = new Move(actions);
		move.perform(state);

		Assert.assertFalse(red.mustPlayCard());
	}

	/**
	 * Checks whether a player is allowed to Skip
	 * Testing with:
	 * - 0 carrots and opponent is on previous hedgehog field
	 */
	@Test
	public void canSkip() throws InvalidMoveException
	{
    List<Action> actions = new ArrayList<>();
		int redPos = state.getNextFieldByType(FieldType.POSITION_2, red.getFieldIndex());
		red.setFieldIndex(redPos);
		red.setCarrotsAvailable(0);

		int bluePos = state.getPreviousFieldByType(FieldType.HEDGEHOG, red.getFieldIndex());
		blue.setFieldIndex(bluePos);
    actions.add(new Skip());

		Move move = new Move(actions);
		move.perform(state); // test whether red can Skip
    try {
      move.perform(state); // test whether blue can Skip
      Assert.fail("Blue is not allowed to Skip");
    } catch(InvalidMoveException e) {
      // everything is fine
    }
	}

	/**
	 * Checks all possibilities of a player to get carrots from position fields
   * (as first or as second)
	 */
	@Test
	public void onPositionField() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
		red.setCarrotsAvailable(5000);
		blue.setCarrotsAvailable(5000);
		int redCarrotsBefore = red.getCarrotsAvailable();
		int pos1At = state.getPreviousFieldByType(FieldType.POSITION_1,
            state.getPreviousFieldByType(FieldType.POSITION_1, 64));
		actions.add(new Advance(pos1At));
		int redMoveCosts = GameRuleLogic.calculateCarrots(pos1At);
		Move move = new Move(actions);
		move.perform(state);

		Assert.assertEquals(redCarrotsBefore - redMoveCosts, red
				.getCarrotsAvailable());
    actions.clear();

		int blueCarrotsBefore = blue.getCarrotsAvailable();
		int pos2At = state.getPreviousFieldByType(FieldType.POSITION_2, pos1At);
		actions.add(new Advance(pos2At));
		move = new Move(actions);
		int blueMoveCosts = GameRuleLogic.calculateCarrots(pos2At);
		move.perform(state);
		actions.clear();

		Assert.assertEquals(state.getCurrentPlayer(), red);
		Assert.assertEquals(redCarrotsBefore - redMoveCosts + 10, red
				.getCarrotsAvailable()); // assert that red got 10 carrots for being first

    actions.add(new Advance(state.getNextFieldByType(FieldType.CARROT,
            red.getFieldIndex()) - red.getFieldIndex())); // random valid move from red
    move = new Move(actions);
    move.perform(state);

		Assert.assertEquals(state.getCurrentPlayer(), blue);
		Assert.assertEquals(blueCarrotsBefore - blueMoveCosts + 30, blue
				.getCarrotsAvailable()); // assert that red got 30 carrots for being second
	}

	/**
	 * Checks whether it is only allowed to drop 20 carrots iff player has at least 20
	 */
	@Test
	public void playDropCarrotsCard() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
		red.setFieldIndex(state.getNextFieldByType(FieldType.HARE, 0));
		actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, -20 ,0));

		Assert.assertTrue(red.getCarrotsAvailable() > 20);
		Move move = new Move(actions);
		move.perform(state);

		blue.setCarrotsAvailable(19);
		try {
      move.perform(state);
      Assert.fail("Not enough carrots");
    } catch (InvalidMoveException e) {
      // everything is fine
    }
	}

	/**
	 * Checks the conditions for advancing to the goal field
	 */
	@Test
	public void enterGoal()
	{
		int carrotAt = state.getPreviousFieldByType(FieldType.CARROT, 64);
		red.setFieldIndex(carrotAt);
		int toGoal = 64 - red.getFieldIndex();
		Assert.assertFalse(GameRuleLogic.isValidToAdvance(state, toGoal));

		red.setCarrotsAvailable(10);
    Assert.assertFalse(GameRuleLogic.isValidToAdvance(state, toGoal));

		red.setSalads(0);
		Assert.assertTrue(red.getSalads() == 0);
		Assert.assertTrue(red.getCarrotsAvailable() <= 10);
    Assert.assertTrue(GameRuleLogic.isValidToAdvance(state, toGoal));
	}

	/**
	 * Checks whether game ends only after a round (blue has last move)
	 */
	@Test
	public void blueHasLastMove() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
		int carrotAt = state.getPreviousFieldByType(FieldType.CARROT, 64);
		red.setFieldIndex(carrotAt);
		int toGoal = 64 - red.getFieldIndex();
		actions.add(new Advance(toGoal));
		red.setCarrotsAvailable(10);
		red.setSalads(0);
    Move move = new Move(actions);
		move.perform(state);
		Assert.assertFalse(game.checkGameOverCondition());
	}

	/**
	 * Checks whether game ends only after a round (red has no last move)
	 */
	@Test
	public void redHasNoLastMove() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
    int firstCarrot = state.getNextFieldByType(FieldType.CARROT, 0);
    actions.add(new Advance(firstCarrot));
		Move move = new Move(actions);
		move.perform(state);
		actions.clear();
		int carrotAt = state.getPreviousFieldByType(FieldType.CARROT, 64);
		blue.setFieldIndex(carrotAt);
		int toGoal = 64 - blue.getFieldIndex();
		actions.add(new Advance(toGoal));
		blue.setCarrotsAvailable(10);
		blue.setSalads(0);
    move = new Move(actions);
		move.perform(state);
    Assert.assertTrue(game.checkGameOverCondition());
	}

	/**
	 * Checks the conditions for eating a salad on a salad field
	 */
	@Test
	public void eatSalad()
	{
		int saladAt = state.getNextFieldByType(FieldType.SALAD, 0);
		red.setFieldIndex(saladAt);
		Assert.assertTrue(GameRuleLogic.isValidToEat(state));
		red.setSalads(0);
		Assert.assertFalse(GameRuleLogic.isValidToEat(state));
	}

  /**
   * Checks the conditions for eating a salad on a salad field
   */
  @Test
  public void mustEatSalad()
  {
    int hedgehog = state.getNextFieldByType(FieldType.HEDGEHOG, 0);
    int saladAt = state.getNextFieldByType(FieldType.SALAD, hedgehog);
    int carrot = state.getNextFieldByType(FieldType.CARROT, saladAt);
    red.setFieldIndex(saladAt);
    red.setLastNonSkipAction(new Advance(1));
    Assert.assertTrue(GameRuleLogic.isValidToEat(state));
    Assert.assertFalse(GameRuleLogic.isValidToAdvance(state, carrot - saladAt));
    Assert.assertFalse(GameRuleLogic.isValidToFallBack(state));
    Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 10));
    Assert.assertFalse(GameRuleLogic.isValidToSkip(state));
    red.setLastNonSkipAction(new Card(CardType.HURRY_AHEAD));
    Assert.assertTrue(GameRuleLogic.isValidToEat(state));
    Assert.assertFalse(GameRuleLogic.isValidToAdvance(state, carrot - saladAt));
    Assert.assertFalse(GameRuleLogic.isValidToFallBack(state));
    Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 10));
    Assert.assertFalse(GameRuleLogic.isValidToSkip(state));
    red.setLastNonSkipAction(new Card(CardType.FALL_BACK));
    Assert.assertTrue(GameRuleLogic.isValidToEat(state));
    Assert.assertFalse(GameRuleLogic.isValidToAdvance(state, carrot - saladAt));
    Assert.assertFalse(GameRuleLogic.isValidToFallBack(state));
    Assert.assertFalse(GameRuleLogic.isValidToExchangeCarrots(state, 10));
    Assert.assertFalse(GameRuleLogic.isValidToSkip(state));
  }

	/**
	 * Checks the perform method for eating salad
	 *
	 */
	@Test
	public void eatSaladCycle() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
		red.setCarrotsAvailable(100);
		int saladAt = state.getNextFieldByType(FieldType.SALAD, 0);
    int carrotAt = state.getNextFieldByType(FieldType.CARROT, 0);
    actions.add(new Advance(saladAt));
		Move move = new Move(actions);
		move.perform(state);
    actions.clear();

		actions.add(new Advance(carrotAt));
		move = new Move(actions);
		move.perform(state);
		actions.clear();

		int saladsBefore = red.getSalads();
		int carrotsBefore = red.getCarrotsAvailable();
		actions.add(new EatSalad());
		move = new Move(actions);
		move.perform(state);
		Assert.assertEquals(saladsBefore - 1, red.getSalads());
    Assert.assertEquals(carrotsBefore + 10, red.getCarrotsAvailable());
	}

	/**
	 * Checks the perform method when using a rabbit joker
	 */
	@Test
	public void playCardCycle() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
		int rabbitAt = state.getNextFieldByType(FieldType.HARE, 0);
		actions.add(new Advance(rabbitAt));
		actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, 20, 1));
		Move move = new Move(actions);
    Assert.assertTrue(red.getCards().contains(CardType.TAKE_OR_DROP_CARROTS));
		move.perform(state);
		actions.clear();
		Assert.assertFalse(red.getCards().contains(CardType.TAKE_OR_DROP_CARROTS));
	}

	/**
	 * Checks the perform method when taking carrots
	 *
	 */
	@Test
	public void takeCarrotsCycle() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
		int carrotsAt = state.getNextFieldByType(FieldType.CARROT, 0);
		actions.add(new Advance(carrotsAt));
		Move move = new Move(actions);
		move.perform(state);
		actions.clear();

    carrotsAt = state.getNextFieldByType(FieldType.CARROT, red.getFieldIndex());
    actions.add(new Advance(carrotsAt));
    move = new Move(actions);
		move.perform(state);
		actions.clear();

		actions.add(new ExchangeCarrots(10));
		Assert.assertTrue(GameRuleLogic.isValidToExchangeCarrots(state, 10));
		int carrotsBefore = red.getCarrotsAvailable();
    move = new Move(actions);
		move.perform(state);
		Assert.assertEquals(carrotsBefore + 10, red.getCarrotsAvailable());
	}

	/**
	 * Checks the perform method when dropping carrots
	 *
	 */
	@Test
	public void dropCarrotsCycle() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
		int carrotsAt = state.getNextFieldByType(FieldType.CARROT, 0);
		actions.add(new Advance(carrotsAt));
		Move move = new Move(actions);
		move.perform(state);
		actions.clear();

		carrotsAt = state.getNextFieldByType(FieldType.CARROT, red.getFieldIndex());
		actions.add(new Advance(carrotsAt));
		move = new Move(actions);
		move.perform(state);
		actions.clear();

		actions.add(new ExchangeCarrots(-10));
		move = new Move(actions);
		Assert.assertTrue(GameRuleLogic.isValidToExchangeCarrots(state,-10));
		int carrotsBefore = red.getCarrotsAvailable();

		move.perform(state);
		Assert.assertEquals(carrotsBefore - 10, red.getCarrotsAvailable());
	}

	/**
	 * Checks that a rabbit joker can only be played on a rabbit field
	 */
	@Test
	public void actioncardOnField()
	{
    Assert.assertFalse(GameRuleLogic.canPlayCard(state));
		int carrotAt = state.getNextFieldByType(FieldType.CARROT, 0);
    int pos1At = state.getNextFieldByType(FieldType.POSITION_1, 0);
    int pos2At = state.getNextFieldByType(FieldType.POSITION_2, 0);
    int hedgehogAt = state.getNextFieldByType(FieldType.HEDGEHOG, 0);
    int goalAt = state.getNextFieldByType(FieldType.GOAL, 0);
    int saladAt = state.getNextFieldByType(FieldType.SALAD, 0);

		red.setFieldIndex(carrotAt);
		Assert.assertFalse(GameRuleLogic.canPlayCard(state));

    red.setFieldIndex(pos1At);
    Assert.assertFalse(GameRuleLogic.canPlayCard(state));

    red.setFieldIndex(pos2At);
    Assert.assertFalse(GameRuleLogic.canPlayCard(state));

    red.setFieldIndex(hedgehogAt);
    Assert.assertFalse(GameRuleLogic.canPlayCard(state));

    red.setFieldIndex(goalAt);
    Assert.assertFalse(GameRuleLogic.canPlayCard(state));

    red.setFieldIndex(saladAt);
    Assert.assertFalse(GameRuleLogic.canPlayCard(state));

	}

	/**
	 * It is not allowed to advance to a hedgehog field or to use a card to move to it
	 */
	@Test
	public void directMoveOntoHedgehog() {
		int hedgehog = state.getNextFieldByType(FieldType.HEDGEHOG, 0);

		// advance
		Assert.assertFalse(GameRuleLogic.isValidToAdvance(state, hedgehog));

		// fall back
		blue.setFieldIndex(hedgehog + 1);
		int rabbit = state.getNextFieldByType(FieldType.HARE, blue.getFieldIndex());
    red.setFieldIndex(rabbit);

    Assert.assertFalse(GameRuleLogic.isValidToPlayFallBack(state));


		// hurry ahead
    blue.setFieldIndex(hedgehog - 1);
    rabbit = state.getNextFieldByType(FieldType.HARE, 0);
    red.setFieldIndex(rabbit);
		Assert.assertFalse(GameRuleLogic.isValidToPlayHurryAhead(state));
	}

	/**
	 * It is not allowed to enter a rabbit field without a card
	 */
	@Test
	public void moveOntoRabbitWithoutCard()
	{
		int rabbit = state.getNextFieldByType(FieldType.HARE, 0);
		red.setCards(Collections.emptyList());
		Assert.assertFalse(GameRuleLogic.isValidToAdvance(state, rabbit));
	}

	/**
	 * It is not allowed a use a rabbit koker to enter a rabbit field, if no other rabbit card is available
	 */
	@Test
	public void indirectHurryAheadOntoRabbit()
	{
		int firstRabbit = state.getNextFieldByType(FieldType.HARE, 0);
		int secondRabbit = state.getNextFieldByType(FieldType.HARE, firstRabbit + 1);

		blue.setFieldIndex(secondRabbit - 1);
		red.setCards(Collections.singletonList(CardType.HURRY_AHEAD));

		Assert.assertFalse(GameRuleLogic.isValidToAdvance(state, firstRabbit));
    ArrayList<CardType> cards = new ArrayList<>();
    cards.add(CardType.HURRY_AHEAD);
    cards.add(CardType.EAT_SALAD);
		red.setCards(cards);
		Assert.assertTrue(GameRuleLogic.isValidToAdvance(state, firstRabbit));
	}

	/**
	 * Checks if a player is allowed to fall back to a hedgehog field
	 */
	@Test
	public void fallback()
	{
		int firstHedgehog = state.getNextFieldByType(FieldType.HEDGEHOG, 0);

		int carrotAfter = state.getNextFieldByType(FieldType.CARROT,firstHedgehog + 1);
		red.setFieldIndex(carrotAfter);

		Assert.assertTrue(GameRuleLogic.isValidToFallBack(state));
	}

	/**
	 * Checks to perform method when falling back
	 */
	@Test
	public void fallbackCycle() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
		int firstHedgehog = state.getNextFieldByType(FieldType.HEDGEHOG, 0);
		int carrotAfter = state.getNextFieldByType(FieldType.CARROT,
				firstHedgehog + 1);
    actions.add(new Advance(carrotAfter));
		red.setCarrotsAvailable(200);
		Move move = new Move(actions);
		move.perform(state);
		actions.clear();

		actions.add(new Advance(state.getNextFieldByType(FieldType.CARROT, 0)));

		move = new Move(actions);
		move.perform(state);
		actions.clear();

		actions.add(new FallBack());
		int carrotsBefore = red.getCarrotsAvailable();
		int diff = red.getFieldIndex() - firstHedgehog;
		move = new Move(actions);
		move.perform(state);
		Assert.assertEquals(carrotsBefore + diff * 10, red.getCarrotsAvailable());
	}

	/**
	 * A player is allowed to fall back, even if he did the same last turn
	 */
	@Test
	public void fallbackTwice() throws InvalidMoveException {
    List<Action> actions = new ArrayList<>();
		int firstHedgehog = state.getNextFieldByType(FieldType.HEDGEHOG, red
				.getFieldIndex());
		int carrotAt = state.getNextFieldByType(FieldType.CARROT, firstHedgehog);
		int secondHedgehog = state.getNextFieldByType(FieldType.HEDGEHOG, carrotAt);
		carrotAt = state.getNextFieldByType(FieldType.CARROT, secondHedgehog);

		red.setFieldIndex(carrotAt);
		actions.add(new FallBack());
		Move move = new Move(actions);
		move.perform(state);
		actions.clear();

		Assert.assertEquals(red.getFieldIndex(), secondHedgehog);

		actions.add(new Advance(state.getNextFieldByType(FieldType.POSITION_2, 0)));
		move = new Move(actions);
		move.perform(state);
		actions.clear();

		actions.add(new FallBack());
		Assert.assertTrue(GameRuleLogic.isValidToFallBack(state));
		move = new Move(actions);
		move.perform(state);
		Assert.assertEquals(red.getFieldIndex(), firstHedgehog);
	}
}
