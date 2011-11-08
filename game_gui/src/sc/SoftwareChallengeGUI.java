package sc;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.LoggerFactory;

import sc.common.CouldNotFindAnyLanguageFileException;
import sc.common.CouldNotFindAnyPluginException;
import sc.gui.GameControlBar;
import sc.gui.PresentationFacade;
import sc.gui.dialogs.ReplayDialog;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.helpers.ManifestHelper;
import sc.logic.LogicFacade;
import sc.logic.save.GUIConfiguration;
import sc.server.Configuration;
import sc.shared.GameResult;

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
		loadCodeVersionFromManifest();
		// get logic facade
		LogicFacade logicFac = LogicFacade.getInstance();
		try {
			logicFac.loadLanguageData();
			logicFac.loadPlugins();
		} catch (CouldNotFindAnyLanguageFileException e) {
			JOptionPane.showMessageDialog(this,
					"Could not load any language file.",
					"Missing any language file.", JOptionPane.ERROR_MESSAGE);
			logicFac.unloadPlugins();
			System.exit(-1);
		} catch (CouldNotFindAnyPluginException e) {
			JOptionPane.showMessageDialog(this, logicFac.getLanguageData()
					.getProperty("main_error_plugin_msg"), logicFac
					.getLanguageData().getProperty("main_error_plugin_title"),
					JOptionPane.ERROR_MESSAGE);
			System.exit(-2);
		}
		// get presentation facade
		this.presFac = PresentationFacade.init(this, logicFac);
		createGUI();
		if (GUIConfiguration.stepSpeedToSet > -1) {
			GameControlBar conBar = presFac.getContextDisplay()
					.getGameControlBar();
			int stepSpeed = GUIConfiguration.stepSpeedToSet
					% (conBar.stepSpeed.getMaximum() + 1);
			stepSpeed = conBar.stepSpeed.getMaximum() - stepSpeed;
			conBar.setStepSpeed(stepSpeed);
		}

		if (GUIConfiguration.finaleMode) {
			LoggerFactory.getLogger(this.getClass()).info(
					"Starting server in finale mode");
			presFac.getContextDisplay().getGameControlBar().btn_toBegin
					.setVisible(false);
			presFac.getContextDisplay().getGameControlBar().btn_toEnd
					.setVisible(false);
			presFac.getContextDisplay().getGameControlBar().stepSpeed
					.setVisible(false);
			presFac.getStatusBar().setVisible(false);
		}

		if (GUIConfiguration.replayFileToLoad != null) {
			ReplayDialog replay = new ReplayDialog(this);
			replay.startReplay(GUIConfiguration.replayFileToLoad);
			if (GUIConfiguration.autoStart) {
				System.out.println("Start playing now");
				presFac.getContextDisplay().startPlaying();
			}
			if (GUIConfiguration.repeat) {
				presFac.getLogicFacade().getObservation().addGameEndedListener(
						new IGameEndedListener() {
							@Override
							public void onGameEnded(GameResult data,
									String gameResultString) {
								TimerTask task = new TimerTask() {
									@Override
									public void run() {
										LoggerFactory
												.getLogger(this.getClass())
												.debug("Repeating now");
										if (presFac.getLogicFacade()
												.getObservation().isAtEnd()) {
											presFac.getLogicFacade()
													.getObservation().reset();
											presFac.getContextDisplay()
													.startPlaying();
										}
									}
								};
								Timer t = new Timer();
								LoggerFactory.getLogger(this.getClass()).info(
										"Repeating in "
												+ GUIConfiguration.repeatDelay
												+ "ms...");
								t.schedule(task, GUIConfiguration.repeatDelay);
							}
						});
			}
		}
	}

	private void loadCodeVersionFromManifest() {
		String version = ManifestHelper.getModuleVersion(this.getClass());

		if (version != null) {
			Configuration.set("code-version", version);
		}
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
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setTitle(presFac.getLogicFacade().getLanguageData().getProperty(
				"window_title"));
		this.setIconImage(new ImageIcon(getClass().getResource(
				presFac.getClientIcon())).getImage());
		// set application size to 80 per cent of screen size
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screen.height = Math.min(768, (int) (0.8 * screen.height));
		screen.width = Math.min(1024, (int) (0.8 * screen.width));
		this.setSize(screen);
		this.setMinimumSize(screen);
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
		LogicFacade logic = presFac.getLogicFacade();
		if (logic.isGameActive() && !logic.getObservation().isFinished()) {
			// if showing warning message is enabled
			if (!GUIConfiguration.instance().suppressWarnMsg()) {
				Properties lang = logic.getLanguageData();
				if (JOptionPane.showConfirmDialog(null, lang
						.getProperty("main_close_msg"), lang
						.getProperty("main_close_title"),
						JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					// do not quit
					return;
				}
			}
		}
		// quit application
		presFac.shutdown();
		System.exit(0);
	}

	/**
	 * Starts this application.
	 * 
	 * @param args
	 *            nothing expected
	 */
	public static void main(String[] args) throws IllegalOptionValueException,
			UnknownOptionException {
		System.setProperty("file.encoding", "UTF-8");

		parseArguments(args);
		setSystemLookAndFeel();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				SoftwareChallengeGUI gui = new SoftwareChallengeGUI();
				gui.setVisible(true);
				if (GUIConfiguration.startMaximized) {
					System.out.println("Start maximized");
					gui.setExtendedState(Frame.MAXIMIZED_BOTH);
				}
			}
		});
	}

	private static void parseArguments(String[] params)
			throws IllegalOptionValueException, UnknownOptionException {
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option plugin = parser.addStringOption('p', "plugin");
		CmdLineParser.Option replay = parser.addStringOption('r', "replay");
		CmdLineParser.Option stepSpeedOption = parser
				.addIntegerOption("stepspeed");
		CmdLineParser.Option maximizedOption = parser.addBooleanOption('m',
				"maximized");
		CmdLineParser.Option finaleOption = parser.addBooleanOption('f',
				"finale");
		CmdLineParser.Option heapSizeOption = parser
				.addIntegerOption("max_client_heapsize");
		CmdLineParser.Option minStepSpeedOption = parser
				.addIntegerOption("min_step_speed");
		CmdLineParser.Option autoPlayOption = parser
				.addBooleanOption("autoplay");
		CmdLineParser.Option repeatOption = parser.addBooleanOption("repeat");
		CmdLineParser.Option repeatDelayOption = parser
				.addIntegerOption("repeat-delay");
		parser.parse(params);

		String pluginPath = (String) parser.getOptionValue(plugin, null);
		String replayFile = (String) parser.getOptionValue(replay, null);
		int stepSpeed = ((Integer) parser.getOptionValue(stepSpeedOption, -1))
				.intValue();
		boolean startMaximized = (Boolean) parser.getOptionValue(
				maximizedOption, false);
		boolean finaleMode = (Boolean) parser.getOptionValue(finaleOption,
				false);
		int heapSize = ((Integer) parser.getOptionValue(heapSizeOption, 1250))
				.intValue();
		int minStepSpeed = ((Integer) parser.getOptionValue(minStepSpeedOption,
				200)).intValue();
		GUIConfiguration.autoStart = (Boolean) parser.getOptionValue(
				autoPlayOption, false);
		GUIConfiguration.repeat = (Boolean) parser.getOptionValue(repeatOption,
				false);
		GUIConfiguration.repeatDelay = ((Integer) parser.getOptionValue(
				repeatDelayOption, 0)).intValue();

		if (pluginPath != null) {
			GUIConfiguration.setPluginFolder(pluginPath);
			Configuration.set(Configuration.PLUGIN_PATH_KEY, pluginPath);
		}

		if (replayFile != null) {
			GUIConfiguration.replayFileToLoad = replayFile;
		}

		if (stepSpeed > -1) {
			GUIConfiguration.stepSpeedToSet = stepSpeed;
		}

		if (startMaximized) {
			GUIConfiguration.startMaximized = true;
		}

		if (finaleMode) {
			GUIConfiguration.finaleMode = true;
		}

		GUIConfiguration.maxHeapSize = heapSize;
		GUIConfiguration.minStepSpeed = minStepSpeed;
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

	@Override
	public void setMinimumGameSize(Dimension dim) {
		this.setMinimumSize(dim);
	}
}
