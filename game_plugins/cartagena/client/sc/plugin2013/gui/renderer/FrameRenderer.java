package sc.plugin2013.gui.renderer;

import static sc.plugin2013.gui.renderer.RenderConfiguration.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Stroke;
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
import javax.swing.JPanel;


import sc.plugin2013.util.Constants;
import sc.plugin2013.Board;
import sc.plugin2013.Card;
import sc.plugin2013.Field;
import sc.plugin2013.FieldType;
import sc.plugin2013.MoveType;
import sc.plugin2013.Player;
import sc.plugin2013.PlayerColor;
import sc.plugin2013.GameState;
import sc.plugin2013.SymbolType;

@SuppressWarnings("serial")
public class FrameRenderer extends JComponent {

	// konstanten
	private final static int BORDER_SIZE = 6;
	private static final int PROGRESS_ICON_SIZE = 60;
	private static final int PROGRESS_BAR_HEIGTH = 36;

	private static final int SIDE_BAR_WIDTH = 275;

	private static final int STUFF_GAP = 8;
	private static final int GAP_SIZE = 10;

	private static final int CARD_WIDTH = 48;
	private static final int CARD_HEIGTH = 48 + 16;
	
	private static final int FIELD_WIDTH = 64;
	private static final int FIELD_HEIGHT = FIELD_WIDTH;
	

	// image components
	private BufferedImage buffer;
	private boolean updateBuffer;
	private final Image bgImage;
	private Image scaledBgImage;
	private final Image progressIcon;
	private final Image skullImage;
	private final Image hatImage;
	private final Image daggerImage;
	private final Image bottleImage;
	private final Image keyImage;
	private final Image pistolImage;

	// schrift
	private static final Font h0 = new Font("Helvetica", Font.BOLD, 73);
	private static final Font h1 = new Font("Helvetica", Font.BOLD, 42);
	private static final Font h2 = new Font("Helvetica", Font.BOLD, 27);
	private static final Font h3 = new Font("Helvetica", Font.BOLD, 23);
	private static final Font h4 = new Font("Helvetica", Font.BOLD, 14);
	private static final Font h5 = new Font("Helvetica", Font.PLAIN, 13);

	private static final JPanel fmPanel = new JPanel();
	private static final FontMetrics fmH0 = fmPanel.getFontMetrics(h0);
	// private static final FontMetrics fmH1 = fmPanel.getFontMetrics(h1);
	private static final FontMetrics fmH2 = fmPanel.getFontMetrics(h2);
	private static final FontMetrics fmH3 = fmPanel.getFontMetrics(h3);
	private static final FontMetrics fmH4 = fmPanel.getFontMetrics(h4);
	private static final FontMetrics fmH5 = fmPanel.getFontMetrics(h5);

	private static final Stroke stroke10 = new BasicStroke(1f);
	private static final Stroke stroke15 = new BasicStroke(1.5f);
	private static final Stroke stroke20 = new BasicStroke(2f);
	private static final Stroke stroke30 = new BasicStroke(3f);
	private static final Stroke stroke40 = new BasicStroke(4f);

	// current (game) state
	private PlayerColor currentPlayer;
	private Color currentPlayerColor;
	private GameState gameState;
	private MoveType currentMoveType;

	// sonstiges
	private boolean gameEnded;

	public FrameRenderer() {

		updateBuffer = true;
		this.progressIcon = loadImage("resource/game/kelle.png");
		this.bgImage = loadImage("resource/game/cartagenabg.jpg");

		this.skullImage = loadImage("resource/game/skull.png");
		this.hatImage = loadImage("resource/game/hat.png");
		this.daggerImage = loadImage("resource/game/dagger.png");
		this.bottleImage = loadImage("resource/game/bottle.png");
		this.keyImage = loadImage("resource/game/key.png");
		this.pistolImage = loadImage("resource/game/pistol.png");

		setMinimumSize(new Dimension(2 * Constants.MAX_CARDS_PER_PLAYER
				* (CARD_WIDTH + STUFF_GAP), 600));
		// TODO Dimension richtig

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
		// aktuellen spielstand sichern
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer().getPlayerColor();
		currentPlayerColor = getPlayerColor(currentPlayer);
		updateBuffer = true;

		repaint();
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

		if (gameState != null) {
			paintDynamicComponents(g2);
		}

		if (gameEnded) {
			paintEndMessage(g2);
		}

	}

	private void paintEndMessage(Graphics2D g2) {
		// TODO Auto-generated method stub

	}

	private void paintDynamicComponents(Graphics2D g2) {
		// TODO Auto-generated method stub

	}

	protected void resizeBoard() {
		int width = getWidth() - 2 * BORDER_SIZE;
		int heigth = getHeight() - 2 * BORDER_SIZE - PROGRESS_BAR_HEIGTH;

		if (width > 0 && heigth > 0) {
			MediaTracker tracker = new MediaTracker(this);

			scaledBgImage = new BufferedImage(width, heigth,
					BufferedImage.TYPE_3BYTE_BGR);
			scaledBgImage.getGraphics().drawImage(bgImage, 0, 0, width, heigth,
					this);
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

		if (gameState != null) {
			// printGameStatus(g2);
			paintSemiStaticComponents(g2);
		}

		updateBuffer = false;
	}

	private void paintSemiStaticComponents(Graphics2D g2) {
		// fortschrittsbalken
		g2.setColor(Color.BLACK);
		g2.setFont(h4);
		int left = fmH4.stringWidth("Spielfortschritt:") + BORDER_SIZE + 30;
		int right = getWidth() - left - 30;
		int fontY = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH / 2
				+ fmH4.getHeight() / 2 - 4;
		g2.drawString("Spielfortschritt:", BORDER_SIZE + 10, fontY);

		int round = gameState.getTurn() + 1;
		String roundString = Integer.toString(gameState.getTurn() + 1);
		if (round > Constants.ROUND_LIMIT) {
			roundString = Integer.toString(Constants.ROUND_LIMIT);
		}

		g2.drawString("Runde " + roundString + " von " + Constants.ROUND_LIMIT,
				right + 30, fontY);

		int progress = (gameState.getTurn() * (right - left))
				/ (2 * Constants.ROUND_LIMIT);
		int progressTop = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH + 8;

		g2.setColor(Color.GRAY);
		g2.fillRoundRect(left, progressTop, right - left,
				PROGRESS_BAR_HEIGTH - 16, 10, 10);

		g2.setColor(Color.GREEN);
		g2.fillRoundRect(left, progressTop, progress, PROGRESS_BAR_HEIGTH - 16,
				10, 10);

		g2.setColor(Color.DARK_GRAY);
		g2.drawRoundRect(left, progressTop, right - left,
				PROGRESS_BAR_HEIGTH - 16, 10, 10);

		int sectionWidth = (right - left) / 4;
		g2.setStroke(stroke15);
		progressTop += 2;
		g2.drawLine(left + sectionWidth, progressTop, left + sectionWidth,
				progressTop + 16);
		g2.drawLine(left + 2 * sectionWidth, progressTop, left + 2
				* sectionWidth, progressTop + 16);
		g2.drawLine(left + 3 * sectionWidth, progressTop, left + 3
				* sectionWidth, progressTop + 16);

		// fortschrittsbalken, icon
		g2.drawImage(progressIcon,
				left + progress - PROGRESS_ICON_SIZE / 2 + 3, getHeight()
						- PROGRESS_ICON_SIZE - 3, PROGRESS_ICON_SIZE,
				PROGRESS_ICON_SIZE, this);

		// rahmen
		g2.setColor(currentPlayerColor);
		g2.fillRect(0, 0, getWidth(), BORDER_SIZE);
		g2.fillRect(0, getHeight() - BORDER_SIZE, getWidth(), BORDER_SIZE);
		g2.fillRect(0, 0, BORDER_SIZE, getHeight());
		g2.fillRect(getWidth() - BORDER_SIZE, 0, BORDER_SIZE, getHeight());

		// Offener Kartenstapel		
		int x = BORDER_SIZE + STUFF_GAP;
		int y = BORDER_SIZE + STUFF_GAP;
		
		for(Card c: gameState.getOpenCards()){
			paintCard(g2, x, y, c.symbol);
			x += CARD_WIDTH + STUFF_GAP;
		}
		
		//Spielerinfo Rot
		Player player = gameState.getRedPlayer();
		x = BORDER_SIZE + STUFF_GAP;
		y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP - CARD_HEIGTH;

		for (Card card : player.getCards()) {
			paintCard(g2, x, y, card.symbol);
			x += CARD_WIDTH + STUFF_GAP;
		}
		
		g2.setFont(h3);
		y -= (STUFF_GAP + 5);
		g2.setColor(getPlayerColor(PlayerColor.RED));
		g2.drawString(player.getDisplayName(), 2 * BORDER_SIZE, y);
		
		//Spielerinfo Blau
		player = gameState.getBluePlayer();
		x = getWidth() - BORDER_SIZE - STUFF_GAP - CARD_WIDTH;
		y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP - CARD_HEIGTH;

		for (Card card : player.getCards()) {
			paintCard(g2, x, y, card.symbol);
			x -= CARD_WIDTH + STUFF_GAP;
		}
		
		g2.setFont(h3);
		y -= (STUFF_GAP + 5);
		g2.setColor(getPlayerColor(PlayerColor.BLUE));
		int nameWidth = fmH3.stringWidth(player.getDisplayName());
		g2.drawString(player.getDisplayName(), getWidth() - 2 * BORDER_SIZE - nameWidth, y);
		
		//Spielbrett
		
		x = BORDER_SIZE + STUFF_GAP;
		y = BORDER_SIZE + 3* STUFF_GAP + CARD_HEIGTH;
		Board board = gameState.getBoard();
		
		g2.setStroke(stroke20);
		g2.setColor(Color.WHITE);
		
		boolean direction = true; //true = right
		
		for(int i = 0; i< board.size(); i++){
			Field field = board.getField(i);
			switch(field.type){
			case START:
				//DRAW START FIELD
				g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setColor(Color.DARK_GRAY);
				g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setFont(h4);				
				String s = "Start";
				int sW = fmH4.stringWidth(s);
				g2.drawString(s, x + 4 , y + FIELD_HEIGHT / 2);
				g2.setColor(Color.WHITE);
				
				y += FIELD_HEIGHT;
				break;
			case FINISH:
				//DRAW FINISH FIELD
				//y += FIELD_HEIGHT;
				g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setColor(Color.DARK_GRAY);
				g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setFont(h4);
				s = "Ziel";
				sW = fmH4.stringWidth(s);
				g2.drawString(s, x + 4 , y + FIELD_HEIGHT / 2);
				g2.setColor(Color.WHITE);
				break;
			case SYMBOL:
				// SELECT IMAGE
				Image img;
				switch(field.symbol){
				case SKULL:
					img = skullImage;
					break;
				case HAT:
					img = hatImage;
					break;
				case DAGGER:
					img = daggerImage;
					break;
				case BOTTLE:
					img = bottleImage;
					break;
				case KEY:
					img = keyImage;
					break;
				case PISTOL:
					img = pistolImage;
					break;
				default:
					img = skullImage;
				}
				// would the current field be out of right side?
				if(x + FIELD_WIDTH >= getWidth() - BORDER_SIZE - SIDE_BAR_WIDTH - STUFF_GAP && direction == true){					
					direction = false;
					
					y += FIELD_HEIGHT;
					x -= FIELD_WIDTH;
					g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.setColor(Color.DARK_GRAY);
					g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.drawImage(img, x+2, y+2, FIELD_WIDTH-4, FIELD_HEIGHT-1, this);
					y+= FIELD_HEIGHT;
				}else
				//would the next field be out of left border?
				if(x < BORDER_SIZE + STUFF_GAP && direction == false){
					direction = true;
					
					y += FIELD_HEIGHT;
					x += FIELD_WIDTH;
					g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.setColor(Color.DARK_GRAY);
					g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.drawImage(img, x+2, y+2, FIELD_WIDTH-4, FIELD_HEIGHT-1, this);
					y+= FIELD_HEIGHT;
				}else if(direction == true){
					// go to the right
					g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.setColor(Color.DARK_GRAY);
					g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.drawImage(img, x+2, y+2, FIELD_WIDTH-4, FIELD_HEIGHT-1, this);
					x += FIELD_WIDTH;
				}else{
					// got to the left
					g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.setColor(Color.DARK_GRAY);
					g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.drawImage(img, x+2, y+2, FIELD_WIDTH-4, FIELD_HEIGHT-1, this);
					x -= FIELD_WIDTH;
				}
				
				
				
				break;
			}
		}
		

	}

	private void paintCard(Graphics2D g2, int x, int y, SymbolType type) {

		g2.setStroke(stroke15);
		g2.setColor(Color.WHITE);
		g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGTH, 10, 10);

		g2.setColor(Color.DARK_GRAY);
		g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGTH, 10, 10);

		Image img;
		switch (type) {
		case SKULL:
			img = skullImage;
			break;
		case HAT:
			img = hatImage;
			break;
		case DAGGER:
			img = daggerImage;
			break;
		case BOTTLE:
			img = bottleImage;
			break;
		case KEY:
			img = keyImage;
			break;
		case PISTOL:
			img = pistolImage;
			break;
		default:
			img = skullImage;
		}
		
		int imgY = y + 16;
		int imgHeight = 32;

		g2.drawImage(img, x + 2 , imgY, CARD_WIDTH-4, imgHeight, this);
	}

	private void paintStaticComponents(Graphics2D g2) {
		/*
		 * Komponenten die nur bei Resize aktualisiert werden
		 */

		// hintergrundbild oder farbe
		if (OPTIONS[BACKGROUND] && scaledBgImage != null) {
			g2.drawImage(scaledBgImage, BORDER_SIZE, BORDER_SIZE, getWidth()
					- 2 * BORDER_SIZE, getHeight() - 2 * BORDER_SIZE, this);
		} else {
			g2.setColor(new Color(186, 217, 246));
			g2.fillRect(BORDER_SIZE, BORDER_SIZE, getWidth() - 2 * BORDER_SIZE,
					getHeight() - 2 * BORDER_SIZE);
		}

		// fortschrittsleiste, spielerinfo und seitenleiste
		g2.setColor(getTransparentColor(new Color(255, 255, 255), 192));

		// fortschrittsleite, spielerinfo hintergrund
		int heigth = PROGRESS_BAR_HEIGTH + 2 * STUFF_GAP + fmH3.getHeight() + CARD_HEIGTH;
		g2.fillRect(BORDER_SIZE, getHeight() - BORDER_SIZE - heigth, getWidth()
				- 2 * BORDER_SIZE, heigth);

		// seitenleiste, hintergrund
		g2.fillRect(getWidth() - BORDER_SIZE - SIDE_BAR_WIDTH, BORDER_SIZE,
				SIDE_BAR_WIDTH, getHeight() - 2 * BORDER_SIZE - heigth);

		// obere Leiste Kartenstapel
		g2.fillRect(BORDER_SIZE, BORDER_SIZE, getWidth() - 2 * BORDER_SIZE
				- SIDE_BAR_WIDTH, BORDER_SIZE + CARD_HEIGTH + 2 * STUFF_GAP);

	}

	private static Image loadImage(String filename) {
		URL url = FrameRenderer.class.getClassLoader().getResource(filename);

		if (url == null) {
			return null;
		}
		return (new ImageIcon(url)).getImage();
	}

	private Color getTransparentColor(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(),
				OPTIONS[TRANSPARANCY] ? alpha : 255);
	}

	private Color getPlayerColor(PlayerColor player) {
		return getPlayerColor(player, false);
	}

	private Color getPlayerColor(PlayerColor player, boolean forced) {
		Color color;

		if (player == null || (player != currentPlayer && !forced)) {
			return Color.DARK_GRAY;
		}

		switch (player) {
		case RED:
			color = Color.RED;
			break;
		case BLUE:
			color = Color.BLUE;
			break;

		default:
			color = Color.DARK_GRAY;
		}

		return color;
	}

}
