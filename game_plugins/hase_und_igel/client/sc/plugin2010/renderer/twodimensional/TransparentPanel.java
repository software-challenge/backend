/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Color;

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
		setDoubleBuffered(true);
		setVisible(true);
	}
}
