package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import sc.gui.PresentationFacade;

@SuppressWarnings("serial")
public class GameInfoDialog extends JDialog {

	private final String gameTypeName;
	private final String version;
	private final Image image;
	private final String infoText;
	private final String author;
	private final Properties lang;
	private final int infoYear;
	private final Image icon;

	/**
	 * Create <code>GameInfoDialog</code>
	 * 
	 * @param gameTypeName
	 * @param version
	 * @param image
	 * @param infoText
	 * @param author
	 */
	public GameInfoDialog(String gameTypeName, String version, final Image image,
			final Image icon, String infoText, String author, int infoYear) {
		super();
		this.gameTypeName = gameTypeName;
		this.version = version;
		this.image = image;
		this.icon = icon;
		this.infoText = infoText;
		this.author = author;
		this.lang = PresentationFacade.getInstance().getLogicFacade().getLanguageData();
		this.infoYear = infoYear;
		createGUI();
	}

	/**
	 * Create GUI
	 */
	private void createGUI() {

		this.setLayout(new BorderLayout());

		JPanel pnlImage = new JPanel();
		pnlImage.setBorder(BorderFactory.createEtchedBorder());
		JPanel pnlText = new JPanel(new GridLayout(0, 1));
		pnlText.setBorder(BorderFactory.createEtchedBorder());

		// ------------------------------------------

		JLabel lblImage = new JLabel(new ImageIcon(image));
		pnlImage.add(lblImage);

		JLabel lblDescriptionText = new JLabel(lang
				.getProperty("dialog_gameinfo_description"));
		pnlText.add(lblDescriptionText);
		// infoText is in html format, so show it in a separate label
		JLabel lblText = new JLabel(infoText);
		pnlText.add(lblText);
		JLabel lblYear = new JLabel(lang.getProperty("dialog_gameinfo_year") + ": "
				+ infoYear);
		pnlText.add(lblYear);
		JLabel lblVersion = new JLabel(lang.getProperty("dialog_gameinfo_version") + ": "
				+ version);
		pnlText.add(lblVersion);
		JLabel lblAuthor = new JLabel(lang.getProperty("dialog_gameinfo_author") + ": "
				+ author);
		pnlText.add(lblAuthor);

		// add components
		this.add(pnlImage, BorderLayout.CENTER);
		this.add(pnlText, BorderLayout.PAGE_END);

		// set pref
		this.setIconImage(icon);
		this.setTitle(gameTypeName);
		this.setModal(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

}
