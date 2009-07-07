package sc.plugin2010.gui;

import java.awt.Image;

import javax.swing.JPanel;

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
	 * @param panel
	 *            JPanel instance on which the game should display
	 */
	public void setRenderContext(final JPanel panel,
			final boolean threeDimensional, final EViewerMode viewerMode)
	{
		RenderFacade.getInstance().createInitFrame(panel, threeDimensional,
				viewerMode);
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
	 * starts a game on the server
	 */
	public void startGame(final int playercount)
	{

	}

	public void endGame()
	{

	}

	public void pauseGame()
	{

	}

	public boolean connectToServer(final String ip, final int port)
	{
		return false; // TODO
	}
}
