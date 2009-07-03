package sc;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

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

	JComponent getContextDisplay();

	JComponent getStatusBar();

	void shutdown();
}
