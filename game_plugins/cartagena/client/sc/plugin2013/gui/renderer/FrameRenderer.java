package sc.plugin2013.gui.renderer;

import static sc.plugin2013.gui.renderer.RenderConfiguration.*;
import static sc.plugin2013.gui.renderer.RenderConfiguration.ANTIALIASING;
import static sc.plugin2013.gui.renderer.RenderConfiguration.OPTIONS;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import sc.plugin2013.GameState;

@SuppressWarnings("serial")
public class FrameRenderer extends JComponent {

	// konstanten
	private final static int BORDER_SIZE = 6;
	private static final int PROGRESS_ICON_SIZE = 60;
	private static final int PROGRESS_BAR_HEIGTH = 36;

	// image components
	private BufferedImage buffer;
	private boolean updateBuffer;
	private final Image bgImage;
	private Image scaledBgImage;
	private final Image progressIcon;

	public FrameRenderer() {
		this.progressIcon = loadImage("resource/game/kelle.png");
		this.bgImage = loadImage("resource/game/cartagena.jpg");
	}

	public void updateGameState(GameState gameState) {
		// TODO Auto-generated method stub

	}

	public void requestMove(int maxTurn) {
		// TODO Auto-generated method stub

	}

	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	private KeyListener keyListener = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			new RenderConfigurationDialog(FrameRenderer.this);
			//updateBuffer = true;
			//repaint();
		}
	};

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);
		if (updateBuffer) {
			fillBuffer();
		}

		g2.drawImage(buffer, 0, 0, getWidth(), getHeight(), this);
		

	}
	
	private void fillBuffer() {

		int imageType = OPTIONS[TRANSPARANCY] ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_BGR;
		buffer = new BufferedImage(getWidth(), getHeight(), imageType);
		Graphics2D g2 = (Graphics2D) buffer.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		/*paintStaticComponents(g2);
		if (gameState != null) {
			// printGameStatus(g2);
			paintSemiStaticComponents(g2);
		}*/

		updateBuffer = false;
	}

	private static Image loadImage(String filename) {
		URL url = FrameRenderer.class.getClassLoader().getResource(filename);

		if (url == null) {
			return null;
		}
		return (new ImageIcon(url)).getImage();
	}

}
