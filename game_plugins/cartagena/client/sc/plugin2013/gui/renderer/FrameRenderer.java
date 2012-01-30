package sc.plugin2013.gui.renderer;

import static sc.plugin2013.gui.renderer.RenderConfiguration.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

		updateBuffer = true;
		this.progressIcon = loadImage("resource/game/kelle.png");
		this.bgImage = loadImage("resource/game/cartagenabg.jpg");
		
		setDoubleBuffered(true);
		setFocusable(true);
		requestFocusInWindow();

		addKeyListener(keyListener);
		addComponentListener(componentListener);
		RenderConfiguration.loadSettings();
		
		resizeBoard();
		repaint();
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
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				new RenderConfigurationDialog(FrameRenderer.this);
				updateBuffer = true;
				repaint();
			}

		}
	};
	
	private ComponentListener componentListener = new ComponentAdapter() {

		@Override
		public void componentResized(ComponentEvent e) {
			resizeBoard();
			repaint();
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

	protected void resizeBoard() {
		int width = getWidth() - 2 * BORDER_SIZE;
		int heigth = getHeight() - 2 * BORDER_SIZE - PROGRESS_BAR_HEIGTH;
		
		if (width > 0 && heigth > 0) {
			MediaTracker tracker = new MediaTracker(this);

			scaledBgImage = new BufferedImage(width, heigth, BufferedImage.TYPE_3BYTE_BGR);
			scaledBgImage.getGraphics().drawImage(bgImage, 0, 0, width, heigth, this);
			tracker.addImage(scaledBgImage, 0);
			try {
				tracker.waitForID(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.gc();
		updateBuffer = true;
		repaint();
		
	}

	private void fillBuffer() {

		int imageType = OPTIONS[TRANSPARANCY] ? BufferedImage.TYPE_INT_ARGB
				: BufferedImage.TYPE_INT_BGR;
		buffer = new BufferedImage(getWidth(), getHeight(), imageType);
		Graphics2D g2 = (Graphics2D) buffer.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		paintStaticComponents(g2);
		/*
		 * if (gameState != null) { // printGameStatus(g2);
		 * paintSemiStaticComponents(g2); }
		 */

		updateBuffer = false;
	}

	private void paintStaticComponents(Graphics2D g2) {
		// hintergrundbild oder farbe
		if (OPTIONS[BACKGROUND] && scaledBgImage != null) {
			g2.drawImage(scaledBgImage, BORDER_SIZE, BORDER_SIZE, getWidth()
					- 2 * BORDER_SIZE, getHeight() - 2 * BORDER_SIZE, this);
		} else {
			g2.setColor(new Color(186, 217, 246));
			g2.fillRect(BORDER_SIZE, BORDER_SIZE, getWidth() - 2 * BORDER_SIZE,
					getHeight() - 2 * BORDER_SIZE);
		}

		// TODO Rest

	}

	private static Image loadImage(String filename) {
		URL url = FrameRenderer.class.getClassLoader().getResource(filename);

		if (url == null) {
			return null;
		}
		return (new ImageIcon(url)).getImage();
	}

}
