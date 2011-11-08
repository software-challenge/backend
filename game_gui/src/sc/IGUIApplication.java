package sc;

import java.awt.Dimension;

/**
 * Defines methods for a main/root GUI application, such as close it.
 * 
 * @author chw
 * @since SC'09
 * 
 */
public interface IGUIApplication {

	/**
	 * Closes this application.
	 */
	void closeGUI();
	/**
	 * Sets the minimum size to the given <code>dim</code>.
	 */
	void setMinimumGameSize(final Dimension dim);
}
