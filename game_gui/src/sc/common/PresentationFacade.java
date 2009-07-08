package sc.common;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

import sc.IGUIApplication;
import sc.IPresentationFacade;
import sc.contextframe.ContextDisplay;

/**
 * The Software Challenge's implementation of {@link IPresentationFacade}.
 * 
 * @author chw
 * @since SC'09
 */
public class PresentationFacade implements IPresentationFacade {

	/**
	 * The menu bar
	 */
	private final SCMenuBar menuBar;
	/**
	 * The context display where to let render a game
	 */
	private final ContextDisplay contextDisplay;
	/**
	 * The status bar
	 */
	private final StatusBar statusBar;

	/**
	 * Constructs a new presentation facade
	 * 
	 * @param root
	 *            the main GUI application
	 */
	public PresentationFacade(IGUIApplication root) {
		contextDisplay = new ContextDisplay();
		menuBar = new SCMenuBar(root);
		statusBar = new StatusBar();
	}

	@Override
	public JComponent getContextDisplay() {
		return contextDisplay;
	}

	@Override
	public JMenuBar getMenuBar() {
		return menuBar;
	}

	@Override
	public String getClientIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JComponent getStatusBar() {
		return statusBar;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
	}

}
