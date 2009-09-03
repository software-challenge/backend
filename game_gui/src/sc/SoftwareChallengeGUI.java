package sc;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import sc.common.CouldNotFindAnyLanguageFileException;
import sc.common.CouldNotFindAnyPluginException;
import sc.gui.PresentationFacade;
import sc.logic.GUIConfiguration;
import sc.logic.ILogicFacade;
import sc.logic.LogicFacade;
import sc.server.Configuration;

/**
 * TODO
 * button icons
 * test range gameEnded(), newTurn()
 * button enabled/disabled
 */

/**
 * The executable application of the Software Challenge GUI.
 * 
 * @author chw
 * @since SC'09
 */
@SuppressWarnings("serial")
public class SoftwareChallengeGUI extends JFrame implements IGUIApplication {

	/**
	 * The presentation facade to be used
	 */
	private final PresentationFacade presFac;

	/**
	 * Constructs a new Software Challenge GUI
	 */
	public SoftwareChallengeGUI() {
		super();
		// get logic facade
		LogicFacade logicFac = LogicFacade.getInstance();
		try {
			logicFac.loadLanguageData();
			logicFac.loadPlugins();
		} catch (CouldNotFindAnyLanguageFileException e) {
			JOptionPane.showMessageDialog(this, "Could not load any language file.",
					"Missing any language file.", JOptionPane.ERROR_MESSAGE);
			logicFac.unloadPlugins();
			System.exit(-1);
		} catch (CouldNotFindAnyPluginException e) {
			JOptionPane.showMessageDialog(this, logicFac.getLanguageData().getProperty(
					"main_error_plugin_msg"), logicFac.getLanguageData().getProperty(
					"main_error_plugin_title"), JOptionPane.ERROR_MESSAGE);
			System.exit(-2);
		}
		// get presentation facade
		this.presFac = PresentationFacade.init(this, logicFac);
		createGUI();
	}

	/**
	 * Creates all necessary GUI components and sets window preferences
	 */
	private void createGUI() {

		// add GUI components
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setJMenuBar(presFac.getMenuBar());
		this.add(presFac.getContextDisplay());
		this.add(presFac.getStatusBar());

		// set window preferences
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setTitle(presFac.getLogicFacade().getLanguageData().getProperty(
				"window_title"));
		this.setIconImage(new ImageIcon(getClass().getResource(presFac.getClientIcon()))
				.getImage());
		// set application size to 80 per cent of screen size
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screen.height = (int) Math.round(0.8 * screen.height);
		screen.width = (int) Math.round(0.8 * screen.width);
		this.setSize(screen);
		// center application
		this.setLocationRelativeTo(null);

		// before closing this frame
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeGUI();
			}
		});
	}

	@Override
	public void closeGUI() {
		ILogicFacade logic = presFac.getLogicFacade();
		if (logic.isGameActive()) {
			Properties lang = logic.getLanguageData();
			if (JOptionPane.showConfirmDialog(null, lang.getProperty("main_close_msg"),
					lang.getProperty("main_close_title"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				// quit application
				presFac.shutdown();
				System.exit(0);
			}
		}
	}

	/**
	 * Starts this application.
	 * 
	 * @param args
	 *            nothing expected
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			String path = args[0];
			System.out.println("Setting Pluginfolder to " + path);
			GUIConfiguration.setPluginFolder(path);
			Configuration.set(Configuration.PLUGIN_PATH_KEY, path);
		}
		setSystemLookAndFeel();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new SoftwareChallengeGUI().setVisible(true);
			}
		});
	}

	private static void setSystemLookAndFeel() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
	}

}
