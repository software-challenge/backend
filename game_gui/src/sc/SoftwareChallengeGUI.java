package sc;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * The executable application of the Software Challenge GUI.
 * 
 * @author chw
 * @since SC'09
 */
@SuppressWarnings("serial")
public class SoftwareChallengeGUI extends JFrame {

	/**
	 * The presentation facade to be used
	 */
	private final IPresentationFacade presFac = new PresentationFacade();

	/**
	 * Constructs a new Software Challenge GUI
	 */
	public SoftwareChallengeGUI() {
		super();
		createGUI();
	}

	/**
	 * Creates all necessary GUI components and sets window preferences
	 */
	private void createGUI() {

		// add GUI components
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setJMenuBar(presFac.getMenuBar());
		this.add(presFac.getContextDisplay());
		this.add(presFac.getStatusBar());

		// set window preferences
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setTitle("Server GUI");
		this.setIconImage(new ImageIcon(getClass().getResource(presFac.getClientIcon()))
				.getImage());
		// this.setMinimumSize(this.getPreferredSize());
		this.pack();
		this.setPreferredSize(new Dimension(1024, 768));
		this.setSize(this.getPreferredSize());
		this.setLocationRelativeTo(null);
		// before closing this application
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				presFac.shutdown();
				System.exit(0);
			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SoftwareChallengeGUI gui = new SoftwareChallengeGUI();
		gui.setVisible(true);
	}

}
