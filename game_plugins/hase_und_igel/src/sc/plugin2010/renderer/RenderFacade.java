/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.Image;

import javax.swing.JFrame;

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

	public void createInitFrame(JFrame frame, boolean threeDimensional)
	{
		if (threeDimensional)
		{
			currentRenderer = new ThreeDimRenderer(frame);
		}
		else
		{
			currentRenderer = new FrameRenderer(frame);
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
