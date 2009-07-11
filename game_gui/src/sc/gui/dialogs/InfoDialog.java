package sc.gui.dialogs;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import sc.gui.PresentationFacade;

@SuppressWarnings("serial")
public class InfoDialog extends JDialog {

	public InfoDialog(JFrame frame) {
		super();
		createGUI(frame);
	}

	private void createGUI(JFrame frame) {

		// TODO
		
		// set dialog preferences
		this.setModal(true);
		this.setLocationRelativeTo(frame);
		this.setSize(800, 480);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

}
