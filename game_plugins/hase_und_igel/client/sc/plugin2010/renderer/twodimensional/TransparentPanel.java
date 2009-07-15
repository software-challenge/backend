/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class TransparentPanel extends JPanel
{
	public TransparentPanel()
	{
		setBackground(new Color(255, 255, 255, 0));
		setVisible(true);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
}
