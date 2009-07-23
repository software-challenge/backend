package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import sc.gui.PresentationFacade;

@SuppressWarnings("serial")
public class InfoDialog extends JDialog {

	private Properties lang;

	public InfoDialog(JFrame frame) {
		super();
		this.lang = PresentationFacade.getInstance().getLogicFacade().getLanguageData();
		createGUI(frame);
	}

	private void createGUI(JFrame frame) {

		this.setLayout(new BorderLayout());

		JPanel scPanel = new JPanel();
		scPanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel devPanel = new JPanel();
		devPanel.setBorder(BorderFactory.createEtchedBorder());
		//devPanel.setLayout(new BoxLayout(devPanel, BoxLayout.PAGE_AXIS));
		devPanel.setLayout(new GridLayout(0,1));

		this.add(scPanel, BorderLayout.CENTER);
		this.add(devPanel, BorderLayout.PAGE_END);

		// ----------------------------
		ImageIcon image = new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon()));
		
		JLabel lblImage = new JLabel(image);
		scPanel.add(lblImage);

		JLabel developer = new JLabel(lang.getProperty("dialog_info_developers"), JLabel.CENTER);
		devPanel.add(developer);
		developer = new JLabel("Christian Wulf", JLabel.CENTER);
		devPanel.add(developer);
		developer = new JLabel("Florian Fittkau", JLabel.CENTER);
		devPanel.add(developer);
		developer = new JLabel("Marcel Jackwerth", JLabel.CENTER);
		devPanel.add(developer);
		developer = new JLabel("Raphael Randschau", JLabel.CENTER);
		devPanel.add(developer);

		// set dialog preferences
		setIconImage(new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon())).getImage());
		this.setPreferredSize(new Dimension(400,300));
		this.setResizable(false);
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

}
