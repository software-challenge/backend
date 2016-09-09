package sc.gui;

import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SCMenuBar extends MenuBar {

  private static final Logger logger = LoggerFactory
      .getLogger(SCMenuBar.class);
	/**
	 * The root frame, i.e. the GUI application
	 */
	private final IGUIApplication root;
	private final PresentationFacade presFac = PresentationFacade.getInstance();
	private Menu help;
	/**
	 * Specific game info menu item
	 */
	private MenuItem specificInfo;
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
		Menu data = new Menu(lang.getProperty("menu_program"));
		Menu game = new Menu(lang.getProperty("menu_game"));
		Menu options = new Menu(lang.getProperty("menu_options"));
		help = new Menu(lang.getProperty("menu_help"));

		// create menu items
		MenuItem close = new MenuItem(lang.getProperty("menu_items_close"));
		close.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        root.closeGUI();

      }
		});

		MenuItem createGame = new MenuItem(lang.getProperty("menu_items_create"));
		createGame.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
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

		MenuItem loadReplay = new MenuItem(lang.getProperty("menu_items_replay"));
		loadReplay.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
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

		MenuItem testRange = new MenuItem(lang.getProperty("menu_items_test"));
		testRange.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
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

		final CheckboxMenuItem noMsg = new CheckboxMenuItem(lang
				.getProperty("menu_items_nomsg"));
		noMsg.setState(GUIConfiguration.instance().suppressWarnMsg());
		noMsg.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIConfiguration.instance().setSuppressWarnMsg(noMsg.getState());

      }
		});

		MenuItem info = new MenuItem(lang.getProperty("menu_items_info"));
		info.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new InfoDialog().setVisible(true);

      }
		});

		MenuItem reportProblem = new MenuItem(lang.getProperty("menu_items_report_problem"));
		info.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://www.software-challenge.de"));
        }
        catch (Exception ex) {
          logger.error("problem opening website", e);
        }
      }
		});

		// add menu items
		data.add(close);

		game.add(createGame);
		game.add(loadReplay);
		game.add(testRange);

		options.add(noMsg);

		help.add(info);
		help.add(reportProblem);

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
		specificInfo = new MenuItem(gameTypeName);
		specificInfo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new GameInfoDialog(gameTypeName, version, image, icon, infoText, author,
            infoYear).setVisible(true);

      }
		});
		help.add(specificInfo);

		// redraw
		//this.validate();
	}
}
