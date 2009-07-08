package sc.common;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

		// create menus
		JMenu data = new JMenu("Programm");
		JMenu config = new JMenu("Konfiguration");
		JMenu help = new JMenu("Hilfe");

		// create menu items
		JMenuItem close = new JMenuItem("Beenden");
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				root.closeGUI();
			}
		});

		// add menu items
		data.add(close);

		// add menus
		this.add(data);
		this.add(config);
		this.add(help);
	}
}
