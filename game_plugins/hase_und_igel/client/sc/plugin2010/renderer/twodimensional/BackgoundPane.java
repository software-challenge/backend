/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;

import javax.swing.JPanel;

import sc.plugin2010.renderer.RendererUtil;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class BackgoundPane extends JPanel
{
	private Image	img	= null;

	public BackgoundPane(String imagefile)
	{
		if (imagefile != null)
		{
			MediaTracker mt = new MediaTracker(this);
			img = RendererUtil.getImage(imagefile);
			mt.addImage(img, 0);
			try
			{
				mt.waitForAll();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}
}
