package sc.plugin2010.gui;

import java.awt.Image;

import javax.swing.JFrame;

import sc.plugin2010.renderer.RenderFacade;

/**
 * 
 * @author ffi
 * 
 */
public class GUIFacade
{
	/**
	 * Singleton instance
	 */
	private static volatile GUIFacade	instance;

	private GUIFacade()
	{ // Singleton
	}

	public static GUIFacade getInstance()
	{
		if (null == instance)
		{
			synchronized (GUIFacade.class)
			{
				if (null == instance)
				{
					instance = new GUIFacade();
				}
			}
		}
		return instance;
	}

	/**
	 * sets the rendercontext. So that the game can be displayed on
	 * <code>frame</code>.
	 * 
	 * @param frame
	 *            jframe instance on which the game should display
	 */
	public void setRenderContext(JFrame frame, boolean threeDimensional)
	{
		RenderFacade.getInstance().createInitFrame(frame, threeDimensional);
	}

	/**
	 * gets an Image of the current game state. External viewers that can not
	 * display jframes need this.
	 * 
	 * @return Image of the current game state.
	 */
	public Image getCurrentStateImage()
	{
		return RenderFacade.getInstance().getImage();

	}

	public String getPluginVersion()
	{
		return "0.1 alpha"; // TODO
	}

	/**
	 * 
	 * @return the magic hash that is used to start a game with this plugin
	 */
	public String getPluginMagicHash()
	{
		return ""; // TODO
	}

	/**
	 * starts a game on the server
	 */
	public void startGame(int playercount)
	{

	}

	public void endGame()
	{

	}

	public void pauseGame()
	{

	}

	public boolean connectToServer(String ip, int port)
	{
		return false; // TODO
	}
}
