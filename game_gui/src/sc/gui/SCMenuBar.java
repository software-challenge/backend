package sc.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import sc.IGUIApplication;

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

	/**
	 * Constructs the SC menu bar.
	 */
	public SCMenuBar(IGUIApplication root) {
		super();
		this.root = root;
		createMenuBar();
	}

	/**
	 * Creates the menu bar.
	 */
	private void createMenuBar() {
		
		ResourceBundle lang = PresentationFacade.getInstance().getLanguageData();
		
		// create menus
		JMenu data = new JMenu(lang.getString("menu_program"));
		JMenu game = new JMenu(lang.getString("menu_game"));
		JMenu help = new JMenu(lang.getString("menu_help"));

		// create menu items
		JMenuItem close = new JMenuItem(lang.getString("menu_items_close"));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				root.closeGUI();
			}
		});
		
		JMenuItem createGame = new JMenuItem(lang.getString("menu_items_create"));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//TODO
			}
		});
		
		JMenuItem loadReplay = new JMenuItem(lang.getString("menu_items_replay"));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//TODO
			}
		});
		
		JMenuItem testRange = new JMenuItem(lang.getString("menu_items_test"));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//TODO
			}
		});
		
		JMenuItem info = new JMenuItem(lang.getString("menu_items_info"));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//TODO
			}
		});

		// add menu items
		data.add(close);
		
		game.add(createGame);
		game.add(loadReplay);
		game.add(testRange);
		
		help.add(info);

		// add menus
		this.add(data);
		this.add(game);
		this.add(help);
	}
}
