package sc.plugin2010;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.plugin2010.Board.FieldTyp;
import sc.plugin2010.Move.MoveTyp;
import sc.plugin2010.Player.Action;
import sc.plugin2010.Player.FigureColor;

public class GamePlayTest
{
	private Game	g;
	private Board	b;
	private Player	red;
	private Player	blue;

	@Before
	public void beforeTest() throws TooManyPlayersException
	{
		g = new Game();
		b = g.getBoard();
		red = (Player) g.onPlayerJoined();
		blue = (Player) g.onPlayerJoined();
	}

	/**
	 * In der ersten Runde stehen beide Spieler am Start
	 */
	@Test
	public void firstRound()
	{
		Assert.assertEquals(red.getColor(), FigureColor.RED);
		Assert.assertEquals(blue.getColor(), FigureColor.BLUE);

		Assert.assertEquals(0, red.getFieldNumber());
		Assert.assertEquals(0, blue.getFieldNumber());
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
		red.setPosition(0);
		Move m = new Move(MoveTyp.TAKE_OR_DROP_CARROTS, 10);
		Assert.assertEquals(false, b.isValid(m, red));
		
		int rabbitAt = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
		red.setPosition(rabbitAt);
		Assert.assertEquals(false, b.isValid(m, red));
		
		int saladAt = b.getNextFieldByTyp(FieldTyp.SALAD, 0);
		red.setPosition(saladAt);
		Assert.assertEquals(false, b.isValid(m, red));
		
		int pos1 = b.getNextFieldByTyp(FieldTyp.POSITION_1, 0);
		red.setPosition(pos1);
		Assert.assertEquals(false, b.isValid(m, red));
		
		int pos2 = b.getNextFieldByTyp(FieldTyp.POSITION_2, 0);
		red.setPosition(pos2);
		Assert.assertEquals(false, b.isValid(m, red));
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
	 * @throws RescueableClientException 
	 */
	@Test
	public void eatSaladCycle() throws RescueableClientException
	{
		g.start();
		
		red.setCarrotsAvailable(100);
		int saladAt = b.getNextFieldByTyp(FieldTyp.SALAD,	0);
		Move r1 = new Move(MoveTyp.MOVE, saladAt);
		g.onAction(red, r1);
		
		Move b1 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT, 0));
		g.onAction(blue, b1);
		
		int before = red.getSaladsToEat();
		Move r2 = new Move(MoveTyp.EAT);
		g.onAction(red, r2);
		Assert.assertEquals(before-1, red.getSaladsToEat());
	}
	
	/**
	 * Simuliert den Ablauf einen Hasenjoker auszuspielen
	 * @throws RescueableClientException 
	 */
	@Test
	public void playCardCycle() throws RescueableClientException
	{
		g.start();
		
		int rabbitAt = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
		Move r1 = new Move(MoveTyp.MOVE, rabbitAt);
		g.onAction(red, r1);
		
		Assert.assertTrue(red.getActions().contains(Action.TAKE_OR_DROP_CARROTS));
		Move r2 = new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, 20);
		Assert.assertEquals(red, g.getActivePlayer());
		g.onAction(red, r2);
		Assert.assertFalse(red.getActions().contains(Action.TAKE_OR_DROP_CARROTS));
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

		Move m2 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT, red.getPosition()));
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

		Move m2 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT, red.getPosition()));
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
		red.setPosition(field);

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

		Move m1 = new Move(MoveTyp.MOVE, hedgehog);
		Assert.assertEquals(false, b.isValid(m1, red));

		blue.setPosition(hedgehog + 1);
		red.setPosition(hedgehog + 2);

		Move m2 = new Move(MoveTyp.FALL_BACK);
		Assert.assertEquals(false, b.isValid(m2, red));

		blue.setPosition(hedgehog - 1);
		int rabbit = b.getNextFieldByTyp(FieldTyp.RABBIT, 0);
		red.setPosition(rabbit);

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
		blue.setPosition(hedgehog);

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

		blue.setPosition(secondRabbit - 1);
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

		int carrotAfter = b.getNextFieldByTyp(FieldTyp.CARROT, firstHedgehog+1);
		red.setFieldNumber(carrotAfter);

		Move m = new Move(MoveTyp.FALL_BACK);
		Assert.assertTrue(b.isValid(m, red));
	}
	
	/**
	 * Simuliert den Verlauf einer Zurückfallen-Aktion
	 * @throws RescueableClientException 
	 */
	@Test
	public void fallbackCycle() throws RescueableClientException
	{
		g.start();
		
		int firstHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);
		int carrotAfter = b.getNextFieldByTyp(FieldTyp.CARROT, firstHedgehog+1);
		
		Move r1 = new Move(MoveTyp.MOVE, carrotAfter);
		red.setCarrotsAvailable(200);
		g.onAction(red, r1);
		
		Move b1 = new Move(MoveTyp.MOVE, b.getNextFieldByTyp(FieldTyp.CARROT, 0));
		g.onAction(blue, b1);
		
		Move r2 = new Move(MoveTyp.FALL_BACK);
		int carrotsBefore = red.getCarrotsAvailable();
		int diff = red.getFieldNumber() - firstHedgehog;
		g.onAction(red, r2);
		
		Assert.assertEquals(carrotsBefore+diff*10, red.getCarrotsAvailable());
	}
}
