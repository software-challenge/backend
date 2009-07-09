package sc.gui.dialogs;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class TestRangeDialog extends JDialog {

	public TestRangeDialog(JFrame frame) {
		super();
		createGUI(frame);
	}

	private void createGUI(JFrame frame) {
		//TODO
		
		// set dialog preferences
		this.setModal(true);
		this.setLocationRelativeTo(frame);
		this.setSize(800, 480);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

}
