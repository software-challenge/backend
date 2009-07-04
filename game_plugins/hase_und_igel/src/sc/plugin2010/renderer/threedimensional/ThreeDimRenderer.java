/**
 * 
 */
package sc.plugin2010.renderer.threedimensional;

import javax.swing.JFrame;

import sc.plugin2010.gui.EViewerMode;
import sc.plugin2010.renderer.Renderer;

/**
 * @author ffi
 * 
 */
public class ThreeDimRenderer implements Renderer
{
	private JFrame		frame;
	private EViewerMode	viewerMode;

	public ThreeDimRenderer(JFrame frame, EViewerMode mode)
	{
		this.frame = frame;
		this.viewerMode = mode;
		createInitFrame();
	}

	private void createInitFrame()
	{

	}

	public void updateData()
	{
		// TODO Auto-generated method stub

	}

}
