/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.CardLayout;
import java.awt.Image;
import java.awt.Panel;

import javax.swing.JPanel;

import sc.plugin2010.Board;
import sc.plugin2010.EPlayerId;
import sc.plugin2010.FigureColor;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Player;
import sc.plugin2010.renderer.threedimensional.ThreeDimRenderer;
import sc.plugin2010.renderer.twodimensional.FrameRenderer;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class RenderFacade
{
	private IRenderer						observer;

	private IRenderer						player1;
	private IRenderer						player2;
	private JPanel							panel;

	private EPlayerId						activePlayer;
	private GUIMode							currentMode;

	private String							connectString			= "connectingPanel";
	private String							observerString			= "observerPanel";
	private String							player1String			= "player1Panel";
	private String							player2String			= "player2Panel";

	private boolean							threeDimensional;
	private boolean							alreadyCreatedPlayerOne	= false;

	/**
	 * Singleton instance
	 */
	private static volatile RenderFacade	instance;

	private RenderFacade()
	{ // Singleton
	}

	public static RenderFacade getInstance()
	{
		if (null == instance)
		{
			synchronized (RenderFacade.class)
			{
				if (null == instance)
				{
					instance = new RenderFacade();
				}
			}
		}
		return instance;
	}

	public enum GUIMode
	{
		CONNECT, OBSERVER, PLAYER_ONE, PLAYER_TWO;
	}
	
	public IRenderer getObserver()
	{
		return observer;
	}

	public void setRenderContext(final JPanel panel,
			final boolean threeDimensional)
	{
		this.threeDimensional = threeDimensional;
		this.panel = panel;
		observer = null;
		player1 = null;
		player2 = null;
		activePlayer = null;
		currentMode = null;
		alreadyCreatedPlayerOne = false;

		if (panel != null)
		{
			panel.setDoubleBuffered(true);

			panel.setLayout(new CardLayout());
			// create components
			Panel con = new Panel();
			// add components
			panel.add(con, connectString);

			setGUIMode(GUIMode.CONNECT);
		}
	}

	private void hideAll()
	{
		if (observer != null)
		{
			observer.hidden();
		}
		if (player1 != null)
		{
			player1.hidden();
		}
		if (player2 != null)
		{
			player2.hidden();
		}
	}

	/**
	 * Switches to the given GUI <code>mode</code>.
	 * 
	 * @param mode
	 */
	public void setGUIMode(GUIMode mode)
	{
		if (currentMode != mode && panel != null)
		{
			switch (mode)
			{
				case CONNECT:
					CardLayout layout = (CardLayout) panel.getLayout();
					layout.show(panel, connectString);
					currentMode = mode;
					break;
				case OBSERVER:
					CardLayout layout1 = (CardLayout) panel.getLayout();
					layout1.show(panel, observerString);
					hideAll();
					observer.shown();
					currentMode = mode;
					break;
				case PLAYER_ONE:
					CardLayout layout2 = (CardLayout) panel.getLayout();
					layout2.show(panel, player1String);
					hideAll();
					player1.shown();
					currentMode = mode;
					break;
				case PLAYER_TWO:
					CardLayout layout3 = (CardLayout) panel.getLayout();
					layout3.show(panel, player2String);
					hideAll();
					player2.shown();
					currentMode = mode;
					break;
			}
		}

		panel.repaint();
	}

	/**
	 * creates a new panel
	 * 
	 * @param asPlayer1
	 *            if asPlayer1 is true than panel will be created for player 1
	 *            else it is created for player2
	 */
	public void createPanel(IGameHandler handler, EPlayerId target)
	{
		if (panel != null)
		{
			if (threeDimensional)
			{
				switch (target)
				{
					case OBSERVER:
						observer = new ThreeDimRenderer(handler, true);
						panel.add((ThreeDimRenderer) observer, observerString);
						break;
					case PLAYER_ONE:
						player1 = new ThreeDimRenderer(handler, false);
						panel.add((ThreeDimRenderer) player1, player1String);
						setAlreadyCreatedPlayerOne(true);
						break;
					case PLAYER_TWO:
						player2 = new ThreeDimRenderer(handler, false);
						panel.add((ThreeDimRenderer) player2, player2String);
						break;
					default:
						break;
				}
			}
			else
			{
				if (target == EPlayerId.OBSERVER)
				{
					observer = new FrameRenderer(handler, true);
					panel.add((FrameRenderer) observer, observerString);
				}
				else if (target == EPlayerId.PLAYER_ONE)
				{
					player1 = new FrameRenderer(handler, false);
					panel.add((FrameRenderer) player1, player1String);
					setAlreadyCreatedPlayerOne(true);
				}
				else if (target == EPlayerId.PLAYER_TWO)
				{
					player2 = new FrameRenderer(handler, false);
					panel.add((FrameRenderer) player2, player2String);
				}
			}
		}
	}

	public void updatePlayer(final Player myplayer, final Player otherPlayer,
			final EPlayerId target)
	{
		if (panel != null)
		{
			switch (target)
			{
				case OBSERVER:
					observer.updatePlayer(myplayer, otherPlayer);
					break;
				case PLAYER_ONE:
					player1.updatePlayer(myplayer, otherPlayer);
					break;
				case PLAYER_TWO:
					player2.updatePlayer(myplayer, otherPlayer);
					break;
				default:
					break;
			}
		}
	}

	public void updateBoard(final Board board, int round, final EPlayerId target)
	{
		if (panel != null)
		{
			if (target == EPlayerId.OBSERVER)
			{
				observer.updateBoard(board, round);
			}
			else if (target == EPlayerId.PLAYER_ONE)
			{
				player1.updateBoard(board, round);
			}
			else if (target == EPlayerId.PLAYER_TWO)
			{
				player2.updateBoard(board, round);
			}
		}
	}

	public void updateChat(final String chatMsg, final EPlayerId target)
	{
		if (panel != null)
		{
			if (target == EPlayerId.OBSERVER)
			{
				observer.updateChat(chatMsg);
			}
			else if (target == EPlayerId.PLAYER_ONE)
			{
				player1.updateChat(chatMsg);
			}
			else if (target == EPlayerId.PLAYER_TWO)
			{
				player2.updateChat(chatMsg);
			}
		}
	}

	public Image getImage()
	{
		if (panel != null)
		{
			return observer.getImage();
		}

		return null;
	}

	/**
	 * 
	 */
	public void switchToPlayer(EPlayerId playerView)
	{
		if (panel != null)
		{
			if (playerView == null)
			{
				playerView = EPlayerId.OBSERVER;
			}

			switch (playerView)
			{
				case OBSERVER:
					setGUIMode(GUIMode.OBSERVER);
					break;
				case PLAYER_ONE:
					setGUIMode(GUIMode.PLAYER_ONE);
					break;
				case PLAYER_TWO:
					setGUIMode(GUIMode.PLAYER_TWO);
					break;
				default:
					break;
			}
		}
	}

	public void setAlreadyCreatedPlayerOne(boolean alreadyCreatedPlayerOne)
	{
		this.alreadyCreatedPlayerOne = alreadyCreatedPlayerOne;
	}

	public boolean getAlreadyCreatedPlayerOne()
	{
		return alreadyCreatedPlayerOne;
	}

	/**
	 * @param id
	 */
	public void requestMove(EPlayerId id)
	{
		if (panel != null)
		{
			switch (id)
			{
				case PLAYER_ONE:
					player1.requestMove();
					setActivePlayer(id);
					break;
				case PLAYER_TWO:
					player2.requestMove();
					setActivePlayer(id);
					break;
				default:
					break;
			}
		}
	}
	
	public void gameError(String errorMessage) {
		observer.gameError(errorMessage);
	}

	/**
	 * @param data
	 * @param id
	 */
	public void gameEnded(GameResult data, EPlayerId target, FigureColor color, String errorMessage)
	{
		if (panel != null)
		{
			if (target == EPlayerId.OBSERVER)
			{
				observer.gameEnded(data, color, errorMessage);
			}
			else if (target == EPlayerId.PLAYER_ONE)
			{
				player1.gameEnded(data, color, errorMessage);
			}
			else if (target == EPlayerId.PLAYER_TWO)
			{
				player2.gameEnded(data, color, errorMessage);
			}
		}
	}

	private void setActivePlayer(EPlayerId activePlayer)
	{
		this.activePlayer = activePlayer;
	}

	public EPlayerId getActivePlayer()
	{
		return activePlayer;
	}
}
