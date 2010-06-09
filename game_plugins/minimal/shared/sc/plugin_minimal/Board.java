package sc.plugin_minimal;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sc.plugin_minimal.FigureColor;
import sc.plugin_minimal.Player;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author ffa, sca
 * 
 */
@XStreamAlias(value = "minimal:board")
public class Board
{
	protected Player		red;
	
	protected Player		blue;

	private Board()
	{
		
	}

	/**
	 * New empty board
	 * 
	 * @return
	 */
	protected static Board create()
	{
		Board b = new Board();
		b.initialize();
		return b;
	}

	/**
	 * Create initial board here, i.e. if you need to randomly place things on the board
	 */
	private final void initialize()
	{
		
	}

	/**
	 * Add player to this board. Here only two players are supported.
	 * @param player
	 */
	protected final void addPlayer(final Player player)
	{
		if (player.getColor().equals(FigureColor.RED))
			red = player;
		else
			blue = player;
	}

	protected final void setPlayerRed(final Player player)
	{
		red = player;
	}

	protected final void setPlayerBlue(final Player player)
	{
		blue = player;
	}
	
	public final Player getOtherPlayer(final Player player)
	{
		assert blue != null;
		assert red != null;
		return player.getColor().equals(FigureColor.RED) ? blue : red;
	}
}