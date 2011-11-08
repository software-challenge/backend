package sc.gui;

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import sc.IGUIApplication;
import sc.gui.dialogs.CreateGameDialog;
import sc.gui.dialogs.GameInfoDialog;
import sc.gui.dialogs.InfoDialog;
import sc.gui.dialogs.ReplayDialog;
import sc.gui.dialogs.TestRangeDialog;
import sc.logic.save.GUIConfiguration;

/**
 * The menu bar.
 * 
 * @author chw
 * @since SC'09
 * 
 */
@SuppressWarnings("serial")
public class SCMenuBar extends JMenuBar {

	/**
	 * The root frame, i.e. the GUI application
	 */
	private final IGUIApplication root;
	private final PresentationFacade presFac = PresentationFacade.getInstance();
	private JMenu help;
	/**
	 * Specific game info menu item
	 */
	private JMenuItem specificInfo;
	private Properties lang;

	/**
	 * Constructs the SC menu bar.
	 */
	public SCMenuBar(IGUIApplication root) {
		super();
		this.root = root;
		this.lang = presFac.getLogicFacade().getLanguageData();
		createMenuBar();
	}

	/**
	 * Creates the menu bar.
	 */
	private void createMenuBar() {

		// create menus
		JMenu data = new JMenu(lang.getProperty("menu_program"));
		JMenu game = new JMenu(lang.getProperty("menu_game"));
		JMenu options = new JMenu(lang.getProperty("menu_options"));
		help = new JMenu(lang.getProperty("menu_help"));

		// create menu items
		JMenuItem close = new JMenuItem(lang.getProperty("menu_items_close"));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				root.closeGUI();
			}
		});

		JMenuItem createGame = new JMenuItem(lang.getProperty("menu_items_create"));
		createGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (presFac.getLogicFacade().isGameActive()
						&& !presFac.getLogicFacade().getObservation().isFinished()) {
					if (JOptionPane.showConfirmDialog(null, lang
							.getProperty("dialog_create_gameactive_msg"), lang
							.getProperty("dialog_create_gameactive_title"),
							JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}
				}
				// show create-game dialog
				new CreateGameDialog(root).setVisible(true);
			}
		});

		JMenuItem loadReplay = new JMenuItem(lang.getProperty("menu_items_replay"));
		loadReplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (presFac.getLogicFacade().isGameActive()
						&& !presFac.getLogicFacade().getObservation().isFinished()) {
					if (JOptionPane.showConfirmDialog(null, lang
							.getProperty("dialog_replay_gameactive_msg"), lang
							.getProperty("dialog_replay_gameactive_title"),
							JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}
				}
				// show replay dialog
				new ReplayDialog(root).setVisible(true);
			}
		});

		JMenuItem testRange = new JMenuItem(lang.getProperty("menu_items_test"));
		testRange.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (presFac.getLogicFacade().isGameActive()
						&& !presFac.getLogicFacade().getObservation().isFinished()) {
					if (JOptionPane.showConfirmDialog(null, lang
							.getProperty("dialog_test_gameactive_msg"), lang
							.getProperty("dialog_test_gameactive_title"),
							JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}
				}
				new TestRangeDialog().setVisible(true);
			}
		});

		final JCheckBoxMenuItem noMsg = new JCheckBoxMenuItem(lang
				.getProperty("menu_items_nomsg"));
		noMsg.setSelected(GUIConfiguration.instance().suppressWarnMsg());
		noMsg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				GUIConfiguration.instance().setSuppressWarnMsg(noMsg.isSelected());
			}
		});

		JMenuItem info = new JMenuItem(lang.getProperty("menu_items_info"));
		info.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new InfoDialog().setVisible(true);
			}
		});

		// add menu items
		data.add(close);

		game.add(createGame);
		game.add(loadReplay);
		game.add(testRange);

		options.add(noMsg);

		help.add(info);

		// add menus
		this.add(data);
		this.add(game);
		this.add(options);
		this.add(help);
	}

	public void setGameSpecificInfo(final String gameTypeName, final String version,
			final Image image, final Image icon, final String infoText,
			final String author, final int infoYear) {

		// remove old info item
		if (null != specificInfo) {
			help.remove(specificInfo);
		}

		// add new info item
		specificInfo = new JMenuItem(gameTypeName);
		specificInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new GameInfoDialog(gameTypeName, version, image, icon, infoText, author,
						infoYear).setVisible(true);
			}
		});
		help.add(specificInfo);

		// redraw
		this.validate();
	}
}
