package sc.gui.dialogs;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class InfoDialog extends JDialog {

	public InfoDialog(JFrame frame) {
		super();
		createGUI(frame);
	}

	private void createGUI(JFrame frame) {

		this.setLayout(new BorderLayout());
		
		JPanel scPanel = new JPanel();
		scPanel.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel devPanel = new JPanel();
		devPanel.setLayout(new BoxLayout(devPanel, BoxLayout.PAGE_AXIS));
		
		this.add(scPanel, BorderLayout.CENTER);
		this.add(devPanel, BorderLayout.PAGE_END);
		
		//----------------------------
		
		JLabel lblImage = new JLabel();
		lblImage.setIcon(new ImageIcon(""));//FIXME
		scPanel.add(lblImage);
		
		JLabel developer = new JLabel("Christian Wulf");
		devPanel.add(developer);
		developer = new JLabel("Florian Fittkau");
		devPanel.add(developer);
		developer = new JLabel("Marcel Jackwerth");
		devPanel.add(developer);
		developer = new JLabel("Raphael Randschau");
		devPanel.add(developer);
		
		// set dialog preferences
		this.setModal(true);
		this.setLocationRelativeTo(frame);
		this.setSize(800, 480);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

}
