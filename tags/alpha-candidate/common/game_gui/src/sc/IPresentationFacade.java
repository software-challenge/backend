package sc;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 * Defines the common structure of the Software Challenge GUI.
 * 
 * @author chw
 * @since SC'09
 * 
 */
public interface IPresentationFacade {

	String getClientIcon();

	JMenuBar getMenuBar();

	JPanel getContextDisplay();

	JComponent getStatusBar();

	void shutdown();
}
