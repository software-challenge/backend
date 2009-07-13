package sc.plugin2010;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

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
		
		Move m4 = new Move(MoveTyp.MOVE, 2);
		Assert.assertEquals(true, b.isValid(m4, red));
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
		
		blue.setPosition(hedgehog+1);
		red.setPosition(hedgehog+2);
		
		Move m2 = new Move(MoveTyp.FALL_BACK);
		Assert.assertEquals(false, b.isValid(m2, red));
		
		blue.setPosition(hedgehog-1);
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
		red.setActions(Arrays.asList(new Action[]{}));
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
	 * Ein Spieler darf sich auf ein Igelfeld zurückfallen lassen. 
	 */
	@Test
	public void fallbackOntoHedgehog()
	{
		int firstHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0);
		int secondHedgehog = b.getNextFieldByTyp(FieldTyp.HEDGEHOG, firstHedgehog+1);
		
		red.setPosition(secondHedgehog-1);
		
		Move m = new Move(MoveTyp.FALL_BACK);
		Assert.assertEquals(false, b.isValid(m, red));
	}
}
