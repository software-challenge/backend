/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class BackgroundPane extends JPanel
{
	private Image	img	= null;

	public BackgroundPane()
	{

	}

	public void setBackground(Image img)
	{
		this.img = img;
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
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}
}
