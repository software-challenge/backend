package sc.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class StatusBar extends JPanel {

	public StatusBar() {
		super();
		createGUI();
	}

	private void createGUI() {
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new GridLayout(1, 1));

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JLabel lblSC = new JLabel("Software Challenge GUI");
		left.add(lblSC);
		
		JLabel lblStatus = new JLabel("Status: ");
		right.add(lblStatus);
		
		this.add(left);
		this.add(right);
	}
	
}
