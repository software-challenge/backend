package sc.plugin2010.gui;

import java.awt.Image;

import javax.swing.JPanel;

import sc.IGUIPluginFacade;
import sc.IGamePreparation;
import sc.plugin2010.renderer.RenderFacade;

/**
 * 
 * @author ffi
 * 
 */
public class GUIPluginFacade implements IGUIPluginFacade
{
	/**
	 * Singleton instance
	 */
	private static volatile GUIPluginFacade	instance;

	private GUIPluginFacade()
	{ // Singleton
	}

	public static GUIPluginFacade getInstance()
	{
		if (null == instance)
		{
			synchronized (GUIPluginFacade.class)
			{
				if (null == instance)
				{
					instance = new GUIPluginFacade();
				}
			}
		}
		return instance;
	}

	/**
	 * sets the rendercontext. So that the game can be displayed on
	 * <code>panel</code>.
	 * 
	 * @param panel
	 *            JPanel instance on which the game should display
	 */
	@Override
	public void setRenderContext(final JPanel panel,
			final boolean threeDimensional)
	{
		RenderFacade.getInstance().createInitFrame(panel, threeDimensional,
				null); // TODO
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

	@Override
	public IGamePreparation prepareGame(final String ip, final int port)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
