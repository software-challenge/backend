/**
 * 
 */
package sc.plugin2010.renderer;

import javax.swing.JFrame;

import sc.plugin2010.gui.EViewerMode;

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

	@Override
	public void updateData()
	{
		// TODO Auto-generated method stub

	}

}
