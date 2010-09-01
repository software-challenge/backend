package sc.gui;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

import sc.IGUIApplication;
import sc.logic.LogicFacade;
import sc.logic.save.GUIConfiguration;

/**
 * The Software Challenge's implementation of {@link IPresentationFacade}.
 * 
 * @author chw
 * @since SC'09
 */
public class PresentationFacade {

	/**
	 * The menu bar
	 */
	private SCMenuBar menuBar;
	/**
	 * The context display where to let render a game
	 */
	private ContextDisplay contextDisplay;
	/**
	 * The status bar
	 */
	private StatusBar statusBar;
	/**
	 * The logic facade
	 */
	private LogicFacade logic;
	/**
	 * Singleton instance
	 */
	private static volatile PresentationFacade instance;

	private PresentationFacade() { // Singleton
	}

	public static PresentationFacade getInstance() {
		if (null == instance) {
			synchronized (PresentationFacade.class) {
				if (null == instance) {
					instance = new PresentationFacade();
				}
			}
		}
		return instance;
	}

	/**
	 * Initializes all internal attributes. Should only be called once at
	 * creation.
	 * 
	 * @param frame
	 * @param root
	 * @param logic
	 */
	public static PresentationFacade init(final IGUIApplication root,
			final LogicFacade logic) {

		// create instance
		getInstance();

		instance.logic = logic;
		instance.contextDisplay = new ContextDisplay();
		instance.menuBar = new SCMenuBar(root);
		instance.statusBar = new StatusBar();

		return instance;
	}

	public ContextDisplay getContextDisplay() {
		return contextDisplay;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public String getClientIcon() {
		return "/resource/guilogo.png";
	}

	public JComponent getStatusBar() {
		return statusBar;
	}

	public void shutdown() {
		GUIConfiguration.instance().save();
		logic.stopServer();
	}

	public LogicFacade getLogicFacade() {
		return logic;
	}

}
