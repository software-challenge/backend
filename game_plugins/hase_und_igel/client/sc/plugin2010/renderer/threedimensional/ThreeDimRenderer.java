/**
 * 
 */
package sc.plugin2010.renderer.threedimensional;

import javax.swing.JFrame;

import sc.plugin2010.Board;
import sc.plugin2010.Player;
import sc.plugin2010.gui.EViewerMode;
import sc.plugin2010.renderer.Renderer;

/**
 * @author ffi
 * 
 */
public class ThreeDimRenderer implements Renderer
{
	private final JFrame		frame;
	private final EViewerMode	viewerMode;

	public ThreeDimRenderer(final JFrame frame, final EViewerMode mode)
	{
		this.frame = frame;
		viewerMode = mode;
		createInitFrame();
	}

	private void createInitFrame()
	{

	}

	@Override
	public void updateAction(final String doneAction)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBoard(final Board bo)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateChat(final String chatMsg)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateInfos(final int round)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePlayer(final Player player, final boolean own)
	{
		// TODO Auto-generated method stub

	}
}
