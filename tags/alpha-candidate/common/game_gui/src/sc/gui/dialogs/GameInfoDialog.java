package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class GameInfoDialog extends JDialog {

	private final String gameTypeName;
	private final String version;
	private final Image image;
	private final String infoText;
	private final String author;

	public GameInfoDialog(String gameTypeName, String version, Image image,
			String infoText, String author) {
		super();
		this.gameTypeName = gameTypeName;
		this.version = version;
		this.image = image;
		this.infoText = infoText;
		this.author = author;
		createGUI();
	}

	private void createGUI() {

		this.setLayout(new BorderLayout());

		JPanel pnlImage = new JPanel();
		pnlImage.setBorder(BorderFactory.createEtchedBorder());
		JPanel pnlText = new JPanel(new GridLayout(0, 1));
		pnlText.setBorder(BorderFactory.createEtchedBorder());

		// ------------------------------------------

		// JLabel lblImage = new JLabel(new ImageIcon(image));
		JLabel lblImage = new JLabel();
		pnlImage.add(lblImage);

		JLabel lblText = new JLabel(infoText);
		pnlText.add(lblText);
		JLabel lblVersion = new JLabel(version);
		pnlText.add(lblVersion);
		JLabel lblAuthor = new JLabel(author);
		pnlText.add(lblAuthor);

		// add components
		this.add(pnlImage, BorderLayout.CENTER);
		this.add(pnlText, BorderLayout.PAGE_END);

		// set pref
		this.setTitle(gameTypeName);
		this.setModal(true);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

}
