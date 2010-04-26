package finals;

import java.awt.Graphics;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class PresentationFrame extends JFrame {
	public PresentationFrame(MainFrame parent) throws HeadlessException {
		super();
		this.parent = parent;
	}

	MainFrame parent;

	@Override
	public void paint(Graphics g) {
		parent.repaint();
		super.paint(g);
	}
}
