package sc.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class StatusBar extends JPanel {

	private JLabel lblStatus;
	private final Properties lang;

	public StatusBar() {
		super();
		lang = PresentationFacade.getInstance().getLogicFacade().getLanguageData();
		createGUI();
	}

	/**
	 * Create status bar
	 */
	private void createGUI() {
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new GridLayout(1, 1));

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JLabel lblSC = new JLabel("Software Challenge GUI");
		left.add(lblSC);
		
		lblStatus = new JLabel(lang.getProperty("statusbar_status_nogame"));
		right.add(lblStatus);
		
		this.add(left);
		this.add(right);
	}
	
	/**
	 * Sets the new status.
	 * @param status
	 */
	public void setStatus(String status) {
		lblStatus.setText(status);
	}
}
