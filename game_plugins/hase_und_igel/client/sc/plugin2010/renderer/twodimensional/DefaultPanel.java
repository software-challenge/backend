/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class DefaultPanel extends JPanel
{
	// private JLabel status = new JLabel("Bitte ein Spiel starten");

	public DefaultPanel()
	{
		LayoutManager layout = new BorderLayout();
		setLayout(layout);

		// add(status, BorderLayout.CENTER);
	}
}
