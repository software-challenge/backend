package sc;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import sc.common.CouldNotFindAnyLanguageFileException;
import sc.common.CouldNotFindAnyPluginException;
import sc.gui.PresentationFacade;
import sc.logic.GUIConfiguration;
import sc.logic.LogicFacade;

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
			closeGUI();
		} catch (CouldNotFindAnyPluginException e) {
			JOptionPane.showMessageDialog(this, logicFac.getLanguageData().getString(
					"main_error_plugin_msg"), logicFac.getLanguageData().getString(
					"main_error_plugin_title"), JOptionPane.ERROR_MESSAGE);
			closeGUI();
		}
		// get presentation facade
		this.presFac = PresentationFacade.init(this, this, logicFac);
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
		// this.add(presFac.getStatusBar());

		// set window preferences
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		this.setTitle(presFac.getLogicFacade().getLanguageData()
				.getString("window_title"));
		// this.setIconImage(new
		// ImageIcon(getClass().getResource(presFac.getClientIcon
		// ())).getImage());
		// this.setMinimumSize(this.getPreferredSize());
		this.pack();
		this.setPreferredSize(new Dimension(1024, 768));
		this.setSize(this.getPreferredSize());
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
		presFac.shutdown();
		System.exit(0);
	}

	/**
	 * Starts this application.
	 * 
	 * @param args
	 *            nothing expected
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			System.out.println("Setting Pluginfolder to " + args[0]);
			GUIConfiguration.setPluginFolder(args[0]);
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
