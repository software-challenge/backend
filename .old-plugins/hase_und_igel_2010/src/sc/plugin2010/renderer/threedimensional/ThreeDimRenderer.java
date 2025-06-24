/**
 * 
 */
package sc.plugin2010.renderer.threedimensional;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.JPanel;

import sc.plugin2010.Board;
import sc.plugin2010.FigureColor;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Player;
import sc.plugin2010.gui.HumanGameHandler;
import sc.plugin2010.renderer.IRenderer;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
@SuppressWarnings( { "serial", "unused" })
public class ThreeDimRenderer extends JPanel implements IRenderer
{
	// GUI Components
	private final IGameHandler	handler;

	// local instances of current players and board
	private Player					player;
	private Player					enemy;
	private Board					board;

	// only draw the board the first time it updates
	private boolean					boardWasCreated	= false;
	private boolean					myturn			= false;
	private boolean					onlyObserving	= false;
	// Strings used for asking Questions to the user
	private static final String		moveForward		= "Weiter ziehen";
	private static final String		takeCarrots		= "10 Karotten nehmen";
	private static final String		dropCarrots		= "10 Karotten abgeben";
	private static final String		carrotAnswer	= "carrots";

	public ThreeDimRenderer(IGameHandler handler, boolean onlyObserving)
	{
		super();
		this.handler = handler;
		this.onlyObserving = onlyObserving;
		createInitFrame();
	}

	private void createInitFrame()
	{
	}

	@Override
	public void updateBoard(Board board, int round)
	{
		this.board = board;
	}

	@Override
	public void updateChat(final String chatMsg)
	{
		// TODO
	}

	@Override
	public void updatePlayer(final Player player, final Player otherPlayer)
	{
		this.player = player;
		enemy = otherPlayer;
	}

	@Override
	public Image getImage()
	{
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
	}

	@Override
	public void requestMove()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void gameEnded(GameResult data, FigureColor color, String errorMessage)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sc.plugin2010.renderer.IRenderer#hidden()
	 */
	@Override
	public void hidden()
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sc.plugin2010.renderer.IRenderer#shown()
	 */
	@Override
	public void shown()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void gameError(String errorMessage)
	{
		// TODO Auto-generated method stub
		
	}

}
