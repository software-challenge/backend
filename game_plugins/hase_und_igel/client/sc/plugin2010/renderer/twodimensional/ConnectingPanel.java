/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class ConnectingPanel extends JPanel
{
	private JLabel	status	= new JLabel("Connecting...");

	public ConnectingPanel()
	{
		LayoutManager layout = new BorderLayout();
		setLayout(layout);

		add(status, BorderLayout.CENTER);
	}
}
