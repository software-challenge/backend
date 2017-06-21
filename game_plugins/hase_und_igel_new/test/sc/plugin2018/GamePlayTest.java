package sc.plugin2018;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamePlayTest
{
	private Game game;
	private GameState	state;
	private Player	red;
	private Player	blue;

	@Before
	public void beforeEveryTest() throws RescuableClientException
	{
		game = new Game();
		state = game.getGameState();
		red = state.getRedPlayer();
		blue = state.getBluePlayer();
	}

	/**
	 * In der ersten Runde stehen beide Spieler am Start, und rot ist an der
	 * Reihe
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
	 * Überprüft den allgemeinen, abwechselnden Spielablauf
	 *
	 * @throws RescuableClientException
	 */
	@Test
	public void basicGameCycle() throws RescuableClientException
	{
//		game.start();
//
//		Move r1 = new Move(MoveTyp.MOVE, b
//				.getNextFieldByTyp(FieldTyp.CARROT, 0));
//		game.onAction(red, r1);
//
//		Assert.assertEquals(Position.FIRST, red.getPosition());
//		Assert.assertTrue(b.isFirst(red));
//
//		Move b1 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT,
//				red.getFieldNumber()));
//		game.onAction(blue, b1);
//
//		Assert.assertEquals(Position.FIRST, blue.getPosition());
//		Assert.assertEquals(Position.SECOND, red.getPosition());
//		Assert.assertTrue(b.isFirst(blue));
	}

	/**
	 * There is only one possible move at the start of a game
	 */
	@Test
	public void justStarted()
	{
	  // player cannot fall_back from start field
		Assert.assertEquals(false, GameRuleLogic.isValidToFallBack(state));
		// player cannot take carrots at start field
		Assert.assertEquals(false, GameRuleLogic.isValidToTakeOrDrop10Carrots(state, 10));
		// player cannot eat salad at start field
    Assert.assertEquals(false, GameRuleLogic.isValidToPlayEatSalad(state));

    // the play must be able to get to a carrot field XXX check why this is necessary
    Assert.assertEquals(true, GameRuleLogic.isValidToAdvance(state, state.getNextFieldByType(FieldType.CARROT, 0)));
	}

	/**
	 * Tests the cases in which takeOrDrop10Carrots is invalid
	 */
	@Test
	public void takeOrDropCarrots()
	{
		red.setFieldNumber(0);
		Assert.assertEquals(false, GameRuleLogic.isValidToTakeOrDrop10Carrots(state, 10));

		int rabbitAt = state.getNextFieldByType(FieldType.RABBIT, 0);
		red.setFieldNumber(rabbitAt);
		Assert.assertEquals(false, GameRuleLogic.isValidToTakeOrDrop10Carrots(state, 10));

		int saladAt = state.getNextFieldByType(FieldType.SALAD, 0);
		red.setFieldNumber(saladAt);
		Assert.assertEquals(false, GameRuleLogic.isValidToTakeOrDrop10Carrots(state, 10));

		int pos1 = state.getNextFieldByType(FieldType.POSITION_1, 0);
		red.setFieldNumber(pos1);
		Assert.assertEquals(false, GameRuleLogic.isValidToTakeOrDrop10Carrots(state, 10));

		int pos2 = state.getNextFieldByType(FieldType.POSITION_2, 0);
		red.setFieldNumber(pos2);
		Assert.assertEquals(false, GameRuleLogic.isValidToTakeOrDrop10Carrots(state, 10));
	}
//
	/**
	 * Checks if round is set correctly and currentPlayer is updated
	 *
	 * @throws RescuableClientException
	 */
	@Test
	public void turnCounting() throws RescuableClientException, InvalidMoveException
	{
	  // XXX check update of current player
    List<Action> actions = new ArrayList<>();
		red.setCarrotsAvailable(100);
		blue.setCarrotsAvailable(100);
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
		int rabbitAt = state.getNextFieldByType(FieldType.RABBIT, red
				.getFieldIndex());
		actions.clear();
		actions.add(new Advance(rabbitAt - red.getFieldIndex()));
		actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, 20, 0));
    move = new Move(actions);
    move.perform(state); // XXX doesn't work look at scenario

		Assert.assertEquals(1, state.getRound());
		nextCarrot = state
				.getNextFieldByType(FieldType.CARROT, blue.getFieldIndex());
		actions.clear();
		actions.add(new Advance(nextCarrot - blue.getFieldIndex()));
    move = new Move(actions);
		move.perform(state);

		Assert.assertEquals(red, state.getCurrentPlayer());

		Assert.assertEquals(2, game.getTurn());
	}

	/**
	 * Überprüft den Ablauf, das Ziel zu erreichen
	 *
	 * @throws RescuableClientException
	 * @throws InterruptedException
	 */
	@Test
	public void enterGoalCycle() throws RescuableClientException, InterruptedException
	{
		game.start();

		int lastCarrot = state.getPreviousFieldByType(FieldType.CARROT, 64);
		int preLastCarrot = state
				.getPreviousFieldByType(FieldType.CARROT, lastCarrot);
		red.setFieldNumber(lastCarrot);
		blue.setFieldNumber(preLastCarrot);

		red.setCarrotsAvailable(GameRuleLogic.calculateCarrots(64 - lastCarrot));
		blue
				.setCarrotsAvailable(GameRuleLogic
						.calculateCarrots(64 - preLastCarrot) + 1);
		red.setSalads(0);
		blue.setSalads(0);
// XXX why sleep?
//		Move r1 = new Move(MoveTyp.MOVE, 64 - red.getFieldNumber());
//		Move b1 = new Move(MoveTyp.MOVE, 64 - blue.getFieldNumber());
//
//		Thread.sleep(14);
//
//		Assert.assertTrue(state.isValid(r1, red));
//		game.onAction(red, r1);
//		Assert.assertTrue(red.inGoal());
//
//		Thread.sleep(14);
//
//		Assert.assertTrue(state.isValid(b1, blue));
//		game.onAction(blue, b1);
//		Assert.assertTrue(blue.inGoal());
//
//		Assert.assertTrue(state.isFirst(red));
//		Assert.assertTrue(game.checkGameOverCondition());
//
//		Assert.assertTrue(game.sum_blue.compareTo(BigInteger.ZERO) == 1);
//		Assert.assertTrue(game.sum_red.compareTo(BigInteger.ZERO) == 1);
	}

	/**
	 * Only when newly entering a rabbit field a card has to be played
	 *
	 * @throws RescuableClientException
	 */
	@Test
	public void mustPlayCard() throws RescuableClientException, InvalidMoveException
	{
	  // XXX advance does not seem to work correctly
	  red.setCarrotsAvailable(200);
	  blue.setCarrotsAvailable(200);
	  List<Action> actions = new ArrayList<>();
	  actions.add(new Advance(state.getNextFieldByType(FieldType.RABBIT,0)));
	  actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS));
    Move move = new Move(actions);
		move.perform(state);

    actions.clear();
    actions.add(new Advance(state.getNextFieldByType(FieldType.CARROT,0)));
    move = new Move(actions);
		move.perform(state);

		Assert.assertFalse(red.mustPlayCard());
	}

//	/**
//	 * Überprüft, ob ein Spieler eine Runde aussetzen kann.
//	 * Getestet wird:
//	 * - 0 Karotten und das Igelfeld hinter dem Spieler ist belegt
//	 * @throws GameLogicException
//	 */
//	@Test
//	public void canSkip() throws GameLogicException
//	{
//		game.start();
//
//		int redPos = b.getNextFieldByTyp(FieldTyp.POSITION_2, red
//				.getFieldNumber());
//		red.setFieldNumber(redPos);
//		red.setCarrotsAvailable(0);
//
//		int bluePos = b.getPreviousFieldByTyp(FieldTyp.HEDGEHOG, red
//				.getFieldNumber());
//		blue.setFieldNumber(bluePos);
//
//		Move r1 = new Move(MoveTyp.SKIP);
//		Assert.assertTrue(b.isValid(r1, red));
//
//		game.onAction(red, r1);
//
//		Move b1 = new Move(MoveTyp.SKIP);
//		Assert.assertFalse(b.isValid(b1, blue));
//	}
//
//	/**
//	 * Überprüft die Bedingungen, unter denen ein Spieler auf den
//	 * Positionsfeldern
//	 * Karotten bekommt.
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void onPositionField() throws RescueableClientException
//	{
//		game.start();
//
//		red.setCarrotsAvailable(5000);
//		blue.setCarrotsAvailable(5000);
//		int redCarrotsBefore = red.getCarrotsAvailable();
//		int pos1At = b.getPreviousFieldByTyp(FieldTyp.POSITION_1, b
//				.getPreviousFieldByTyp(FieldTyp.POSITION_1, 64));
//		Move r1 = new Move(MoveTyp.MOVE, pos1At);
//		int redMoveCosts = GameRuleLogic.calculateCarrots(r1.getN());
//		game.onAction(red, r1);
//
//		Assert.assertEquals(redCarrotsBefore - redMoveCosts, red
//				.getCarrotsAvailable());
//
//		int blueCarrotsBefore = blue.getCarrotsAvailable();
//		int pos2At = b.getPreviousFieldByTyp(FieldTyp.POSITION_2, pos1At);
//		Move b1 = new Move(MoveTyp.MOVE, pos2At);
//		int blueMoveCosts = GameRuleLogic.calculateCarrots(b1.getN());
//		game.onAction(blue, b1);
//
//		// Rot hat den Bonus auf Position 1 bekommen
//		Assert.assertEquals(game.getActivePlayer(), red);
//		Assert.assertEquals(redCarrotsBefore - redMoveCosts + 10, red
//				.getCarrotsAvailable());
//
//		Move r2 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT,
//				red.getFieldNumber())
//				- red.getFieldNumber());
//		game.onAction(red, r2);
//
//		// Blau hat den Bonus auf Position 2 bekommen
//		Assert.assertEquals(game.getActivePlayer(), blue);
//		Assert.assertEquals(blueCarrotsBefore - blueMoveCosts + 30, blue
//				.getCarrotsAvailable());
//	}
//
//	/**
//	 * Überprüft, dass Karotten nur abgegeben werden dürfen, wenn man mehr als
//	 * 20
//	 * Karotten besitzt.
//	 */
//	@Test
//	public void playDropCarrotsCard()
//	{
//		game.start();
//
//		red.setFieldNumber(b.getNextFieldByTyp(FieldTyp.RABBIT, 0));
//		Move r = new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, -20);
//		Assert.assertTrue(red.getCarrotsAvailable() > 20);
//		Assert.assertTrue(b.isValid(r, red));
//
//		red.setCarrotsAvailable(19);
//		Assert.assertFalse(b.isValid(r, red));
//	}
//
//	/**
//	 * Überprüft die Bedingungen, unter denen das Ziel betreten werden kann
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void enterGoal() throws RescueableClientException
//	{
//		int carrotAt = b.getPreviousFieldByTyp(FieldTyp.CARROT, 64);
//		red.setFieldNumber(carrotAt);
//		int toGoal = 64 - red.getFieldNumber();
//		Move m = new Move(MoveTyp.MOVE, toGoal);
//		Assert.assertFalse(b.isValid(m, red));
//
//		red.setCarrotsAvailable(10);
//		Assert.assertFalse(b.isValid(m, red));
//
//		red.setSaladsToEat(0);
//		Assert.assertTrue(red.getSaladsToEat() == 0);
//		Assert.assertTrue(red.getCarrotsAvailable() <= 10);
//		Assert.assertTrue(b.isValid(m, red));
//	}
//
//	/**
//	 * Überprüft, dass blau einen letzen Zug bekommt, wenn rot vor Ihr das Ziel
//	 * erreicht.
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void blueHasLastMove() throws RescueableClientException
//	{
//		game.start();
//
//		int carrotAt = b.getPreviousFieldByTyp(FieldTyp.CARROT, 64);
//		red.setFieldNumber(carrotAt);
//		int toGoal = 64 - red.getFieldNumber();
//		Move m = new Move(MoveTyp.MOVE, toGoal);
//		red.setCarrotsAvailable(10);
//		red.setSaladsToEat(0);
//
//		game.onAction(red, m);
//		Assert.assertTrue(game.hasLastMove());
//	}
//
//	/**
//	 * Überprüft, dass rot keinen letzen Zug bekommt, wenn blau vor Ihr das Ziel
//	 * erreicht.
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void redHasNoLastMove() throws RescueableClientException
//	{
//		game.start();
//
//		Move r = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT, 0));
//		game.onAction(red, r);
//
//		int carrotAt = b.getPreviousFieldByTyp(FieldTyp.CARROT, 64);
//		blue.setFieldNumber(carrotAt);
//		int toGoal = 64 - blue.getFieldNumber();
//		Move b = new Move(MoveTyp.MOVE, toGoal);
//		blue.setCarrotsAvailable(10);
//		blue.setSaladsToEat(0);
//
//		game.onAction(blue, b);
//		Assert.assertFalse(game.hasLastMove());
//	}
//
//	/**
//	 * Überprüft, dass Salate nur auf Salatfeldern gefressen werden dürfen
//	 */
//	@Test
//	public void eatSalad()
//	{
//		int saladAt = b.getNextFieldByTyp(FieldTyp.SALAD, 0);
//		red.setFieldNumber(saladAt);
//
//		Move m = new Move(MoveTyp.EAT);
//		Assert.assertTrue(b.isValid(m, red));
//
//		red.setSaladsToEat(0);
//		Assert.assertFalse(b.isValid(m, red));
//	}
//
//	/**
//	 * Simuliert den Ablauf von Salat-Fressen
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void eatSaladCycle() throws RescueableClientException
//	{
//		game.start();
//
//		red.setCarrotsAvailable(100);
//		int saladAt = b.getNextFieldByTyp(FieldTyp.SALAD, 0);
//		Move r1 = new Move(MoveTyp.MOVE, saladAt);
//		game.onAction(red, r1);
//
//		Move b1 = new Move(MoveTyp.MOVE, b
//				.getNextFieldByTyp(FieldTyp.CARROT, 0));
//		game.onAction(blue, b1);
//
//		int before = red.getSaladsToEat();
//		Move r2 = new Move(MoveTyp.EAT);
//		game.onAction(red, r2);
//		Assert.assertEquals(before - 1, red.getSaladsToEat());
//	}
//
//	/**
//	 * Simuliert den Ablauf einen Hasenjoker auszuspielen
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void playCardCycle() throws RescueableClientException
//	{
//		game.start();
//
//		int rabbitAt = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
//		Move r1 = new Move(MoveTyp.MOVE, rabbitAt);
//		game.onAction(red, r1);
//		Assert.assertTrue(red.mustPlayCard());
//
//		Move rFail = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(
//				FieldTyp.CARROT, red.getFieldNumber())
//				- red.getFieldNumber());
//		Assert.assertFalse(b.isValid(rFail, red));
//
//		Assert.assertTrue(red.getActions()
//				.contains(Action.TAKE_OR_DROP_CARROTS));
//		Move r2 = new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, 20);
//		Assert.assertEquals(red, game.getActivePlayer());
//		game.onAction(red, r2);
//		Assert.assertFalse(red.getActions().contains(
//				Action.TAKE_OR_DROP_CARROTS));
//	}
//
//	/**
//	 * Simuliert das Fressen von Karotten auf einem Karottenfeld
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void takeCarrotsCycle() throws RescueableClientException
//	{
//		game.start();
//
//		int carrotsAt = b.getNextFieldByTyp(FieldTyp.CARROT, 0);
//		Move m1 = new Move(MoveTyp.MOVE, carrotsAt);
//		game.onAction(red, m1);
//
//		Move m2 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT,
//				red.getFieldNumber()));
//		game.onAction(blue, m2);
//
//		Move m3 = new Move(MoveTyp.TAKE_OR_DROP_CARROTS, 10);
//		Assert.assertEquals(true, b.isValid(m3, red));
//		int carrotsBefore = red.getCarrotsAvailable();
//
//		game.onAction(red, m3);
//		Assert.assertEquals(carrotsBefore + 10, red.getCarrotsAvailable());
//	}
//
//	/**
//	 * Simuliert das Ablegen von Karotten auf einem Karottenfeld
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void dropCarrotsCycle() throws RescueableClientException
//	{
//		game.start();
//
//		int carrotsAt = b.getNextFieldByTyp(FieldTyp.CARROT, 0);
//		Move m1 = new Move(MoveTyp.MOVE, carrotsAt);
//		game.onAction(red, m1);
//
//		Move m2 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT,
//				red.getFieldNumber()));
//		game.onAction(blue, m2);
//
//		Move m3 = new Move(MoveTyp.TAKE_OR_DROP_CARROTS, -10);
//		Assert.assertEquals(true, b.isValid(m3, red));
//		int carrotsBefore = red.getCarrotsAvailable();
//
//		game.onAction(red, m3);
//		Assert.assertEquals(carrotsBefore - 10, red.getCarrotsAvailable());
//	}
//
//	/**
//	 * Auf einem Karottenfeld darf kein Hasenjoker gespielt werden
//	 */
//	@Test
//	public void actioncardOnFieldTypCarrot()
//	{
//		int field = b.getNextFieldByTyp(FieldTyp.CARROT, 0);
//		red.setFieldNumber(field);
//
//		Move m1 = new Move(MoveTyp.PLAY_CARD, Action.EAT_SALAD);
//		Assert.assertEquals(false, b.isValid(m1, red));
//
//		Move m2 = new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK);
//		Assert.assertEquals(false, b.isValid(m2, red));
//
//		Move m3 = new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD);
//		Assert.assertEquals(false, b.isValid(m3, red));
//
//		Move m4 = new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS);
//		Assert.assertEquals(false, b.isValid(m4, red));
//	}
//
//	/**
//	 * Ein Spieler darf nicht direkt auf ein Igelfeld ziehen;
//	 */
//	@Test
//	public void directMoveOntoHedgehog()
//	{
//		int hedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);
//
//		// direkter zug
//		Move m1 = new Move(MoveTyp.MOVE, hedgehog);
//		Assert.assertEquals(false, b.isValid(m1, red));
//
//		blue.setFieldNumber(hedgehog + 1);
//		int rabbit = b
//				.getNextFieldByTyp(FieldTyp.RABBIT, blue.getFieldNumber());
//		red.setFieldNumber(rabbit);
//
//		// mit fallback
//		Move m2 = new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK);
//		Assert.assertEquals(false, b.isValid(m2, red));
//
//		blue.setFieldNumber(hedgehog - 1);
//		rabbit = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
//		red.setFieldNumber(rabbit);
//
//		// mit hurry ahead
//		Move m3 = new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD);
//		Assert.assertEquals(false, b.isValid(m3, red));
//	}
//
//	/**
//	 * Ohne Hasenjoker darf man kein Hasenfeld betreten!
//	 */
//	@Test
//	public void moveOntoRabbitWithoutCard()
//	{
//		int rabbit = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
//		red.setActions(Arrays.asList(new Action[] {}));
//		Move m = new Move(MoveTyp.MOVE, rabbit);
//		Assert.assertEquals(false, b.isValid(m, red));
//	}
//
//	/**
//	 * Indirekte Züge auf einen Igel sind nicht erlaubt
//	 */
//	@Test
//	public void indirectHurryAheadOntoHedgehog()
//	{
//		int hedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);
//		blue.setFieldNumber(hedgehog - 1);
//
//		int rabbit = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
//		red.setActions(Arrays.asList(Action.HURRY_AHEAD));
//
//		Move m = new Move(MoveTyp.MOVE, rabbit);
//		Assert.assertEquals(false, b.isValid(m, red));
//	}
//
//	/**
//	 * Indirekte Züge auf einen Hasen sind nur erlaubt, wenn man danach noch
//	 * einen Hasenjoker anwenden kann.
//	 */
//	@Test
//	public void indirectHurryAheadOntoRabbit()
//	{
//		int firstRabbit = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
//		int secondRabbit = b
//				.getNextFieldByTyp(FieldTyp.RABBIT, firstRabbit + 1);
//
//		blue.setFieldNumber(secondRabbit - 1);
//		red.setActions(Arrays.asList(Action.HURRY_AHEAD));
//
//		Move m1 = new Move(MoveTyp.MOVE, firstRabbit);
//		Assert.assertEquals(false, b.isValid(m1, red));
//
//		red.setActions(Arrays.asList(new Action[] { Action.HURRY_AHEAD,
//				Action.EAT_SALAD }));
//		Assert.assertEquals(true, b.isValid(m1, red));
//	}
//
//	/**
//	 * Ein Spieler darf sich auf ein Igelfeld zurückfallen lassen.
//	 */
//	@Test
//	public void fallback()
//	{
//		int firstHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);
//
//		int carrotAfter = b.getNextFieldByTyp(FieldTyp.CARROT,
//				firstHedgehog + 1);
//		red.setFieldNumber(carrotAfter);
//
//		Move m = new Move(MoveTyp.FALL_BACK);
//		Assert.assertTrue(b.isValid(m, red));
//	}
//
//	/**
//	 * Simuliert den Verlauf einer Zurückfallen-Aktion
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void fallbackCycle() throws RescueableClientException
//	{
//		game.start();
//
//		int firstHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);
//		int carrotAfter = b.getNextFieldByTyp(FieldTyp.CARROT,
//				firstHedgehog + 1);
//
//		Move r1 = new Move(MoveTyp.MOVE, carrotAfter);
//		red.setCarrotsAvailable(200);
//		game.onAction(red, r1);
//
//		Move b1 = new Move(MoveTyp.MOVE, b
//				.getNextFieldByTyp(FieldTyp.CARROT, 0));
//		game.onAction(blue, b1);
//
//		Move r2 = new Move(MoveTyp.FALL_BACK);
//		int carrotsBefore = red.getCarrotsAvailable();
//		int diff = red.getFieldNumber() - firstHedgehog;
//		game.onAction(red, r2);
//
//		Assert.assertEquals(carrotsBefore + diff * 10, red
//				.getCarrotsAvailable());
//	}
//
//	/**
//	 * Ein Spieler kann sich zweimal hintereinander zurückfallen lassen
//	 *
//	 * @throws RescueableClientException
//	 */
//	@Test
//	public void fallbackTwice() throws RescueableClientException
//	{
//		game.start();
//
//		int firstHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, red
//				.getFieldNumber());
//		int carrotAt = b.getNextFieldByTyp(FieldTyp.CARROT, firstHedgehog);
//		int secondHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, carrotAt);
//		carrotAt = b.getNextFieldByTyp(FieldTyp.CARROT, secondHedgehog);
//
//		red.setFieldNumber(carrotAt);
//		Move r1 = new Move(MoveTyp.FALL_BACK);
//		game.onAction(red, r1);
//		Assert.assertEquals(red.getFieldNumber(), secondHedgehog);
//
//		Move b1 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(
//				FieldTyp.POSITION_2, 0));
//		game.onAction(blue, b1);
//
//		Move r2 = new Move(MoveTyp.FALL_BACK);
//		Assert.assertTrue(b.isValid(r2, red));
//		game.onAction(red, r2);
//		Assert.assertEquals(red.getFieldNumber(), firstHedgehog);
//	}
}
