/**
 * 
 */
package sc.plugin_schaefchen.renderer;

import java.awt.CardLayout;
import java.awt.Image;
import java.awt.Panel;

import javax.swing.JPanel;

import sc.plugin_schaefchen.Board;
import sc.plugin_schaefchen.EPlayerId;
import sc.plugin_schaefchen.IGameHandler;
import sc.plugin_schaefchen.Player;
import sc.plugin_schaefchen.PlayerColor;
import sc.plugin_schaefchen.renderer.twodimensional.FrameRenderer;
import sc.shared.GameResult;

/**
 * This facade contains the main game panel. It uses a CardLayout to switch between the subpanels for every player.
 * @author ffi, sca
 * 
 */
public class RenderFacade
{
	private IRenderer						observer; // The observer gets an extra panel (shown while a AI client acts)

	private IRenderer						player1; // One panel for player1 (shown at player one's turn)
	private IRenderer						player2; // One panel for player2 (shown at player two's turn)
	
	private JPanel							panel; // The main panel

	private EPlayerId						activePlayer;
	
	/*
	 * GUI modes are used to determine what the gui is showing at the moment
	 * Can be connection dialogue, player1, ...
	 */
	private GUIMode							currentMode;
	
	/*
	 * These strings are used to identify the different panels in the CardLayout
	 */
	private String							connectString			= "connectingPanel";
	private String							observerString			= "observerPanel";
	private String							player1String			= "player1Panel";
	private String							player2String			= "player2Panel";

	private boolean							threeDimensional;
	
	/*
	 * Used here to determine what panel (IRenderer) should be used when a new panel is created.
	 * This solution only works with two players
	 */
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
	public void gameEnded(GameResult data, EPlayerId target, PlayerColor color, String errorMessage)
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
