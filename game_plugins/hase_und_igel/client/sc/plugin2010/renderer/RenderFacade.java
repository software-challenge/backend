/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.CardLayout;
import java.awt.Image;

import javax.swing.JPanel;

import sc.plugin2010.BoardUpdated;
import sc.plugin2010.EPlayerId;
import sc.plugin2010.Player;
import sc.plugin2010.gui.GUIGameHandler;
import sc.plugin2010.renderer.threedimensional.ThreeDimRenderer;
import sc.plugin2010.renderer.twodimensional.ConnectingPanel;
import sc.plugin2010.renderer.twodimensional.FrameRenderer;

/**
 * @author ffi
 * 
 */
public class RenderFacade
{
	private Renderer						observer;
	private Renderer						player1;
	private Renderer						player2;
	private JPanel							panel;

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

	public void setRenderContext(final JPanel panel,
			final boolean threeDimensional)
	{
		this.threeDimensional = threeDimensional;
		this.panel = panel;

		panel.setLayout(new CardLayout());
		// create components
		ConnectingPanel con = new ConnectingPanel();
		FrameRenderer obs = new FrameRenderer();
		// add components
		panel.add(con, connectString);
		panel.add(obs, observerString);

		setGUIMode(GUIMode.CONNECT);
	}

	/**
	 * Switches to the given GUI <code>mode</code>.
	 * 
	 * @param mode
	 */
	public void setGUIMode(GUIMode mode)
	{

		switch (mode)
		{
			case CONNECT:
				CardLayout layout = (CardLayout) panel.getLayout();
				layout.show(panel, connectString);
				break;
			case OBSERVER:
				CardLayout layout1 = (CardLayout) panel.getLayout();
				layout1.show(panel, observerString);
				break;
			case PLAYER_ONE:
				CardLayout layout2 = (CardLayout) panel.getLayout();
				layout2.show(panel, player1String);
				break;
			case PLAYER_TWO:
				CardLayout layout3 = (CardLayout) panel.getLayout();
				layout3.show(panel, player2String);
				break;
		}
		// redraw
		panel.validate();
	}

	/**
	 * creates a new panel
	 * 
	 * @param asPlayer1
	 *            if asPlayer1 is true than panel will be created for player 1
	 *            else it is created for player2
	 */
	public void createPanel(GUIGameHandler handler)
	{
		if (threeDimensional)
		{
			if (!alreadyCreatedPlayerOne)
			{
				player1 = new ThreeDimRenderer(handler);
				panel.add((ThreeDimRenderer) player1, player1String);
				setAlreadyCreatedPlayerOne(true);
			}
			else
			{
				player2 = new ThreeDimRenderer(handler);
				panel.add((ThreeDimRenderer) player2, player2String);
			}
		}
		else
		{
			if (!alreadyCreatedPlayerOne)
			{
				player1 = new FrameRenderer(handler);
				panel.add((FrameRenderer) player1, player1String);
				setAlreadyCreatedPlayerOne(true);
			}
			else
			{
				player2 = new FrameRenderer(handler);
				panel.add((FrameRenderer) player2, player2String);
			}
		}
	}

	public void updatePlayer(final Player myplayer, final boolean own)
	{
		observer.updatePlayer(myplayer, own);
	}

	public void updateBoard(final BoardUpdated board)
	{
		observer.updateBoard(board);
	}

	public void updateChat(final String chatMsg)
	{
		observer.updateChat(chatMsg);
	}

	public void updateAction(final String doneAction)
	{
		observer.updateAction(doneAction);
	}

	public Image getImage()
	{
		return observer.getImage();
	}

	/**
	 * 
	 */
	public void switchToPlayer(EPlayerId playerView)
	{
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

	public void setAlreadyCreatedPlayerOne(boolean alreadyCreatedPlayerOne)
	{
		this.alreadyCreatedPlayerOne = alreadyCreatedPlayerOne;
	}

	public boolean getAlreadyCreatedPlayerOne()
	{
		return alreadyCreatedPlayerOne;
	}
}
