/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Graphics;
import java.awt.Image;

import sc.plugin2010.renderer.RendererUtil;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class SaladPanel extends BackgroundPane
{
	private Image	img		= RendererUtil.getImage("resource/game/salad.png");
	private int		count	= 5;
	private int		size	= 24;

	public SaladPanel()
	{

	}

	public void setIconSize(int size)
	{
		this.size = size;
	}

	public void setSaladCount(int count)
	{
		this.count = count;
		repaint();
	}

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		// super.paintComponent(g);
		int middleHeight = (getHeight() - size) / 2;
		int indent = 5;
		for (int i = 0; i < count; i++)
		{
			g.drawImage(img, i * size + indent * i, middleHeight, size, size,
					this);
		}
	}
}
