/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.Image;

import javax.swing.JPanel;

import sc.plugin2010.Board;
import sc.plugin2010.Player;
import sc.plugin2010.gui.EViewerMode;
import sc.plugin2010.renderer.threedimensional.ThreeDimRenderer;
import sc.plugin2010.renderer.twodimensional.FrameRenderer;

/**
 * @author ffi
 * 
 */
public class RenderFacade
{
	private Renderer						currentRenderer;

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

	public void createInitFrame(final JPanel panel,
			final boolean threeDimensional, final EViewerMode mode)
	{
		if (threeDimensional)
		{
			setCurrentRenderer(new ThreeDimRenderer(panel, mode));
		}
		else
		{
			setCurrentRenderer(new FrameRenderer(panel, mode));
		}
	}

	public void updatePlayer(final Player myplayer, final boolean own)
	{
		getCurrentRenderer().updatePlayer(myplayer, own);
	}

	public void updateBoard(final Board board)
	{
		getCurrentRenderer().updateBoard(board);
	}

	public void updateChat(final String chatMsg)
	{
		getCurrentRenderer().updateChat(chatMsg);
	}

	public void updateAction(final String doneAction)
	{
		getCurrentRenderer().updateAction(doneAction);
	}

	public Image getImage()
	{
		return null;

	}

	private void setCurrentRenderer(final Renderer currentRenderer)
	{
		this.currentRenderer = currentRenderer;
	}

	private Renderer getCurrentRenderer()
	{
		return currentRenderer;
	}
}
