/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.Image;

import javax.swing.JFrame;

import sc.plugin2010.gui.EViewerMode;

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

	public void createInitFrame(JFrame frame, boolean threeDimensional,
			EViewerMode mode)
	{
		if (threeDimensional)
		{
			currentRenderer = new ThreeDimRenderer(frame, mode);
		}
		else
		{
			currentRenderer = new FrameRenderer(frame, mode);
		}
	}

	public void updateFrame()
	{

	}

	public Image getImage()
	{
		return null;

	}
}
