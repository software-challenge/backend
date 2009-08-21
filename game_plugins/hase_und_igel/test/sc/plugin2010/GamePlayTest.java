package sc.plugin2010;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;

public class GamePlayTest
{
	private Game	g;
	private Board	b;
	private Player	red;
	private Player	blue;

	@Before
	public void beforeEveryTest() throws TooManyPlayersException
	{
		g = new Game();
		b = g.getBoard();
		red = (Player) g.onPlayerJoined();
		blue = (Player) g.onPlayerJoined();
	}

	/**
	 * In der ersten Runde stehen beide Spieler am Start, und rot ist an der
	 * Reihe
	 */
	@Test
	public void firstRound()
	{
		Assert.assertEquals(red.getColor(), FigureColor.RED);
		Assert.assertEquals(blue.getColor(), FigureColor.BLUE);

		Assert.assertEquals(0, red.getFieldNumber());
		Assert.assertEquals(0, blue.getFieldNumber());

		g.start();
		Assert.assertEquals(red, g.getActivePlayer());
	}

	/**
	 * Überprüft den allgemeinen, abwechselnden Spielablauf
	 * 
	 * @throws RescueableClientException
	 */
	@Test
	public void basicGameCycle() throws RescueableClientException
	{
		g.start();

		Move r1 = new Move(MoveTyp.MOVE, b
				.getNextFieldByTyp(FieldTyp.CARROT, 0));
		g.onAction(red, r1);

		Assert.assertEquals(Position.FIRST, red.getPosition());
		Assert.assertTrue(b.isFirst(red));

		Move b1 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT,
				red.getFieldNumber()));
		g.onAction(blue, b1);

		Assert.assertEquals(Position.FIRST, blue.getPosition());
		Assert.assertEquals(Position.SECOND, red.getPosition());
		Assert.assertTrue(b.isFirst(blue));
	}

	/**
	 * Wenn beide Spieler am Start stehen ist nur ein Zug möglich
	 */
	@Test
	public void justStarted()
	{
		Move m1 = new Move(MoveTyp.FALL_BACK);
		Assert.assertEquals(false, b.isValid(m1, red));

		Move m2 = new Move(MoveTyp.TAKE_OR_DROP_CARROTS, 10);
		Assert.assertEquals(false, b.isValid(m2, red));

		Move m3 = new Move(MoveTyp.PLAY_CARD, Action.EAT_SALAD);
		Assert.assertEquals(false, b.isValid(m3, red));

		Move m4 = new Move(MoveTyp.MOVE, b
				.getNextFieldByTyp(FieldTyp.CARROT, 0));
		Assert.assertEquals(true, b.isValid(m4, red));
	}

	/**
	 * Überprüft, dass Karotten nur auf einem Karottenfeld aufgenommen
	 * oder abgelegt werden dürfen
	 */
	@Test
	public void takeOrDropCarrots()
	{
		red.setFieldNumber(0);
		Move m = new Move(MoveTyp.TAKE_OR_DROP_CARROTS, 10);
		Assert.assertEquals(false, b.isValid(m, red));

		int rabbitAt = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
		red.setFieldNumber(rabbitAt);
		Assert.assertEquals(false, b.isValid(m, red));

		int saladAt = b.getNextFieldByTyp(FieldTyp.SALAD, 0);
		red.setFieldNumber(saladAt);
		Assert.assertEquals(false, b.isValid(m, red));

		int pos1 = b.getNextFieldByTyp(FieldTyp.POSITION_1, 0);
		red.setFieldNumber(pos1);
		Assert.assertEquals(false, b.isValid(m, red));

		int pos2 = b.getNextFieldByTyp(FieldTyp.POSITION_2, 0);
		red.setFieldNumber(pos2);
		Assert.assertEquals(false, b.isValid(m, red));
	}

	/**
	 * Überprüft, dass der Rundenzähler korrekt gesetzt wird.
	 * 
	 * @throws RescueableClientException
	 */
	@Test
	public void turnCounting() throws RescueableClientException
	{
		g.start();

		red.setCarrotsAvailable(100);
		blue.setCarrotsAvailable(100);
		Assert.assertEquals(0, g.getTurn());
		int firstCarrot = b.getNextFieldByTyp(FieldTyp.CARROT, red
				.getFieldNumber());
		Move r1 = new Move(MoveTyp.MOVE, firstCarrot);
		g.onAction(red, r1);

		Assert.assertEquals(0, g.getTurn());
		int nextCarrot = b.getNextFieldByTyp(FieldTyp.CARROT, red
				.getFieldNumber());
		Move b1 = new Move(MoveTyp.MOVE, nextCarrot - blue.getFieldNumber());
		g.onAction(blue, b1);

		Assert.assertEquals(1, g.getTurn());
		int rabbitAt = b.getNextFieldByTyp(FieldTyp.RABBIT, red
				.getFieldNumber());
		Move r2 = new Move(MoveTyp.MOVE, rabbitAt - red.getFieldNumber());

		g.onAction(red, r2);
		Assert.assertEquals(1, g.getTurn());
		Move r3 = new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, 20);
		g.onAction(red, r3);

		Assert.assertEquals(1, g.getTurn());
		nextCarrot = b
				.getNextFieldByTyp(FieldTyp.CARROT, blue.getFieldNumber());
		Move b2 = new Move(MoveTyp.MOVE, nextCarrot - blue.getFieldNumber());
		g.onAction(blue, b2);

		Assert.assertEquals(red, g.getActivePlayer());

		Assert.assertEquals(2, g.getTurn());
	}

	/**
	 * Überprüft den Ablauf, das Ziel zu erreichen
	 * 
	 * @throws RescueableClientException
	 */
	@Test
	public void enterGoalCycle() throws RescueableClientException
	{
		g.start();

		int lastCarrot = b.getPreviousFieldByTyp(FieldTyp.CARROT, 64);
		int preLastCarrot = b
				.getPreviousFieldByTyp(FieldTyp.CARROT, lastCarrot);
		red.setFieldNumber(lastCarrot);
		blue.setFieldNumber(preLastCarrot);

		red.setCarrotsAvailable(GameUtil.calculateCarrots(64 - lastCarrot));
		blue
				.setCarrotsAvailable(GameUtil
						.calculateCarrots(64 - preLastCarrot) + 1);
		red.setSaladsToEat(0);
		blue.setSaladsToEat(0);

		Move r1 = new Move(MoveTyp.MOVE, 64 - red.getFieldNumber());
		Move b1 = new Move(MoveTyp.MOVE, 64 - blue.getFieldNumber());

		g.onAction(red, r1);
		Assert.assertTrue(red.inGoal());

		g.onAction(blue, b1);
		Assert.assertTrue(blue.inGoal());

		Assert.assertTrue(b.isFirst(red));
	}

	/**
	 * Wenn ein Spieler ein Hasenfeld neu betritt _MUSS_ eine Hasenkarte
	 * gespielt werden
	 * @throws RescueableClientException 
	 */
	@Test
	public void mustPlayCard() throws RescueableClientException
	{
		g.start();
		
		Move r1 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.RABBIT, 0));
		g.onAction(red, r1);
		
		Assert.assertTrue(red.mustPlayCard());
		
		Move r2 = new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, 20);
		g.onAction(red, r2);
		
		Assert.assertFalse(red.mustPlayCard());

		Move b1 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT, 0));
		g.onAction(blue, b1);
		
		Assert.assertFalse(red.mustPlayCard());
	}
	
	/**
	 * Überprüft, ob ein Spieler eine Runde aussetzen kann.
	 * Getestet wird:
	 *  - 0 Karotten und das Igelfeld hinter dem Spieler ist belegt
	 */
	@Test
	public void canSkip() {
		g.start();
		
		int redPos = b.getNextFieldByTyp(FieldTyp.POSITION_2, red.getFieldNumber());
		red.setFieldNumber(redPos);
		red.setCarrotsAvailable(0);
		
		int bluePos = b.getPreviousFieldByTyp(FieldTyp.HEDGEHOG, red.getFieldNumber());
		blue.setFieldNumber(bluePos);
		
		Move m = new Move(MoveTyp.SKIP);
		Assert.assertTrue(b.isValid(m, red));
	}
	
	/**
	 * Überprüft die Bedingungen, unter denen ein Spieler auf den Positionsfeldern
	 * Karotten bekommt.
	 * @throws RescueableClientException
	 */
	@Test
	public void onPositionField() throws RescueableClientException
	{
		g.start();
		
		red.setCarrotsAvailable(1000);
		blue.setCarrotsAvailable(1000);
		int carrotsBefore = red.getCarrotsAvailable();
		Move r1 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.POSITION_1, 0));
		int moveCosts = GameUtil.calculateCarrots(r1.getN());
		g.onAction(red, r1);
		
		Assert.assertEquals(carrotsBefore-moveCosts, red.getCarrotsAvailable());
		
		Move b1 = new Move(MoveTyp.MOVE, b.getPreviousFieldByTyp(FieldTyp.CARROT, red.getFieldNumber()));
		g.onAction(blue, b1);
		
		Assert.assertEquals(g.getActivePlayer(), red);
		Assert.assertEquals(carrotsBefore-moveCosts+10, red.getCarrotsAvailable());
	}

	/**
	 * Überprüft die Bedingungen, unter denen das Ziel betreten werden kann
	 */
	@Test
	public void enterGoal()
	{
		int carrotAt = b.getPreviousFieldByTyp(FieldTyp.CARROT, 64);
		red.setFieldNumber(carrotAt);
		int toGoal = 64 - red.getFieldNumber();
		Move m = new Move(MoveTyp.MOVE, toGoal);
		Assert.assertFalse(b.isValid(m, red));

		red.setCarrotsAvailable(10);
		Assert.assertFalse(b.isValid(m, red));

		red.setSaladsToEat(0);
		Assert.assertTrue(red.getSaladsToEat() == 0);
		Assert.assertTrue(red.getCarrotsAvailable() <= 10);
		Assert.assertTrue(b.isValid(m, red));
	}

	/**
	 * Überprüft, dass Salate nur auf Salatfeldern gefressen werden dürfen
	 */
	@Test
	public void eatSalad()
	{
		int saladAt = b.getNextFieldByTyp(FieldTyp.SALAD, 0);
		red.setFieldNumber(saladAt);

		Move m = new Move(MoveTyp.EAT);
		Assert.assertTrue(b.isValid(m, red));

		red.setSaladsToEat(0);
		Assert.assertFalse(b.isValid(m, red));
	}

	/**
	 * Simuliert den Ablauf von Salat-Fressen
	 * 
	 * @throws RescueableClientException
	 */
	@Test
	public void eatSaladCycle() throws RescueableClientException
	{
		g.start();

		red.setCarrotsAvailable(100);
		int saladAt = b.getNextFieldByTyp(FieldTyp.SALAD, 0);
		Move r1 = new Move(MoveTyp.MOVE, saladAt);
		g.onAction(red, r1);

		Move b1 = new Move(MoveTyp.MOVE, b
				.getNextFieldByTyp(FieldTyp.CARROT, 0));
		g.onAction(blue, b1);

		int before = red.getSaladsToEat();
		Move r2 = new Move(MoveTyp.EAT);
		g.onAction(red, r2);
		Assert.assertEquals(before - 1, red.getSaladsToEat());
	}

	/**
	 * Simuliert den Ablauf einen Hasenjoker auszuspielen
	 * 
	 * @throws RescueableClientException
	 */
	@Test
	public void playCardCycle() throws RescueableClientException
	{
		g.start();

		int rabbitAt = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
		Move r1 = new Move(MoveTyp.MOVE, rabbitAt);
		g.onAction(red, r1);
		Assert.assertTrue(red.mustPlayCard());

		Move rFail = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(
				FieldTyp.CARROT, red.getFieldNumber())
				- red.getFieldNumber());
		Assert.assertFalse(b.isValid(rFail, red));

		Assert.assertTrue(red.getActions()
				.contains(Action.TAKE_OR_DROP_CARROTS));
		Move r2 = new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, 20);
		Assert.assertEquals(red, g.getActivePlayer());
		g.onAction(red, r2);
		Assert.assertFalse(red.getActions().contains(
				Action.TAKE_OR_DROP_CARROTS));
	}

	/**
	 * Simuliert das Fressen von Karotten auf einem Karottenfeld
	 * 
	 * @throws RescueableClientException
	 */
	@Test
	public void takeCarrotsCycle() throws RescueableClientException
	{
		g.start();

		int carrotsAt = b.getNextFieldByTyp(FieldTyp.CARROT, 0);
		Move m1 = new Move(MoveTyp.MOVE, carrotsAt);
		g.onAction(red, m1);

		Move m2 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT,
				red.getFieldNumber()));
		g.onAction(blue, m2);

		Move m3 = new Move(MoveTyp.TAKE_OR_DROP_CARROTS, 10);
		Assert.assertEquals(true, b.isValid(m3, red));
		int carrotsBefore = red.getCarrotsAvailable();

		g.onAction(red, m3);
		Assert.assertEquals(carrotsBefore + 10, red.getCarrotsAvailable());
	}

	/**
	 * Simuliert das Ablegen von Karotten auf einem Karottenfeld
	 * 
	 * @throws RescueableClientException
	 */
	@Test
	public void dropCarrotsCycle() throws RescueableClientException
	{
		g.start();

		int carrotsAt = b.getNextFieldByTyp(FieldTyp.CARROT, 0);
		Move m1 = new Move(MoveTyp.MOVE, carrotsAt);
		g.onAction(red, m1);

		Move m2 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT,
				red.getFieldNumber()));
		g.onAction(blue, m2);

		Move m3 = new Move(MoveTyp.TAKE_OR_DROP_CARROTS, -10);
		Assert.assertEquals(true, b.isValid(m3, red));
		int carrotsBefore = red.getCarrotsAvailable();

		g.onAction(red, m3);
		Assert.assertEquals(carrotsBefore - 10, red.getCarrotsAvailable());
	}

	/**
	 * Auf einem Karottenfeld darf kein Hasenjoker gespielt werden
	 */
	@Test
	public void actioncardOnFieldTypCarrot()
	{
		int field = b.getNextFieldByTyp(FieldTyp.CARROT, 0);
		red.setFieldNumber(field);

		Move m1 = new Move(MoveTyp.PLAY_CARD, Action.EAT_SALAD);
		Assert.assertEquals(false, b.isValid(m1, red));

		Move m2 = new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK);
		Assert.assertEquals(false, b.isValid(m2, red));

		Move m3 = new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD);
		Assert.assertEquals(false, b.isValid(m3, red));

		Move m4 = new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS);
		Assert.assertEquals(false, b.isValid(m4, red));
	}

	/**
	 * Ein Spieler darf nicht direkt auf ein Igelfeld ziehen;
	 */
	@Test
	public void directMoveOntoHedgehog()
	{
		int hedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);

		// direkter zug
		Move m1 = new Move(MoveTyp.MOVE, hedgehog);
		Assert.assertEquals(false, b.isValid(m1, red));

		blue.setFieldNumber(hedgehog + 1);
		int rabbit = b
				.getNextFieldByTyp(FieldTyp.RABBIT, blue.getFieldNumber());
		red.setFieldNumber(rabbit);

		// mit fallback
		Move m2 = new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK);
		Assert.assertEquals(false, b.isValid(m2, red));

		blue.setFieldNumber(hedgehog - 1);
		rabbit = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
		red.setFieldNumber(rabbit);

		// mit hurry ahead
		Move m3 = new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD);
		Assert.assertEquals(false, b.isValid(m3, red));
	}

	/**
	 * Ohne Hasenjoker darf man kein Hasenfeld betreten!
	 */
	@Test
	public void moveOntoRabbitWithoutCard()
	{
		int rabbit = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
		red.setActions(Arrays.asList(new Action[] {}));
		Move m = new Move(MoveTyp.MOVE, rabbit);
		Assert.assertEquals(false, b.isValid(m, red));
	}

	/**
	 * Indirekte Züge auf einen Igel sind nicht erlaubt
	 */
	@Test
	public void indirectHurryAheadOntoHedgehog()
	{
		int hedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);
		blue.setFieldNumber(hedgehog - 1);

		int rabbit = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
		red.setActions(Arrays.asList(Action.HURRY_AHEAD));

		Move m = new Move(MoveTyp.MOVE, rabbit);
		Assert.assertEquals(false, b.isValid(m, red));
	}

	/**
	 * Indirekte Züge auf einen Hasen sind nur erlaubt, wenn man danach noch
	 * einen Hasenjoker anwenden kann.
	 */
	@Test
	public void indirectHurryAheadOntoRabbit()
	{
		int firstRabbit = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
		int secondRabbit = b
				.getNextFieldByTyp(FieldTyp.RABBIT, firstRabbit + 1);

		blue.setFieldNumber(secondRabbit - 1);
		red.setActions(Arrays.asList(Action.HURRY_AHEAD));

		Move m1 = new Move(MoveTyp.MOVE, firstRabbit);
		Assert.assertEquals(false, b.isValid(m1, red));

		red.setActions(Arrays.asList(new Action[] { Action.HURRY_AHEAD,
				Action.EAT_SALAD }));
		Assert.assertEquals(true, b.isValid(m1, red));
	}

	/**
	 * Ein Spieler darf sich auf ein Igelfeld zurückfallen lassen.
	 */
	@Test
	public void fallback()
	{
		int firstHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);

		int carrotAfter = b.getNextFieldByTyp(FieldTyp.CARROT,
				firstHedgehog + 1);
		red.setFieldNumber(carrotAfter);

		Move m = new Move(MoveTyp.FALL_BACK);
		Assert.assertTrue(b.isValid(m, red));
	}

	/**
	 * Simuliert den Verlauf einer Zurückfallen-Aktion
	 * 
	 * @throws RescueableClientException
	 */
	@Test
	public void fallbackCycle() throws RescueableClientException
	{
		g.start();

		int firstHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);
		int carrotAfter = b.getNextFieldByTyp(FieldTyp.CARROT,
				firstHedgehog + 1);

		Move r1 = new Move(MoveTyp.MOVE, carrotAfter);
		red.setCarrotsAvailable(200);
		g.onAction(red, r1);

		Move b1 = new Move(MoveTyp.MOVE, b
				.getNextFieldByTyp(FieldTyp.CARROT, 0));
		g.onAction(blue, b1);

		Move r2 = new Move(MoveTyp.FALL_BACK);
		int carrotsBefore = red.getCarrotsAvailable();
		int diff = red.getFieldNumber() - firstHedgehog;
		g.onAction(red, r2);

		Assert.assertEquals(carrotsBefore + diff * 10, red
				.getCarrotsAvailable());
	}
	
	/**
	 * Ein Spieler kann sich zweimal hintereinander zurückfallen lassen
	 * @throws RescueableClientException 
	 */
	@Test
	public void fallbackTwice() throws RescueableClientException
	{
		g.start();
		
		int firstHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, red.getFieldNumber());
		int carrotAt = b.getNextFieldByTyp(FieldTyp.CARROT, firstHedgehog);
		int secondHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, carrotAt);
		carrotAt = b.getNextFieldByTyp(FieldTyp.CARROT, secondHedgehog);
		
		red.setFieldNumber(carrotAt);
		Move r1 = new Move(MoveTyp.FALL_BACK);
		g.onAction(red, r1);
		Assert.assertEquals(red.getFieldNumber(), secondHedgehog);
		
		Move b1 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.POSITION_2, 0));
		g.onAction(blue, b1);
		
		Move r2 = new Move(MoveTyp.FALL_BACK);
		Assert.assertTrue(b.isValid(r2, red));
		g.onAction(red, r2);
		Assert.assertEquals(red.getFieldNumber(), firstHedgehog);
	}
}
