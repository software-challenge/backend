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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import sc.plugin2013.Move;
import sc.plugin2013.gui.renderer.RenderFacade;
import sc.plugin2013.util.Constants;
import sc.plugin2013.util.InvalidMoveException;
import sc.plugin2013.BackwardMove;
import sc.plugin2013.Board;
import sc.plugin2013.Card;
import sc.plugin2013.DebugHint;
import sc.plugin2013.Field;
import sc.plugin2013.FieldType;
import sc.plugin2013.ForwardMove;
import sc.plugin2013.MoveContainer;
import sc.plugin2013.MoveType;
import sc.plugin2013.Pirate;
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

	// sonstiges
	private boolean gameEnded;
	private int turnToAnswer;
	private LinkedList<Point> BoardMap;
	private int movesMade = 0;
	private final int movesToMake = 3;
	private Move firstMove;
	private Move secondMove;
	private Move thirdMove;
	private int selectedField = -1;
	private LinkedList<Integer> possibleFields = new LinkedList<Integer>();

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

		this.BoardMap = new LinkedList<Point>();

		setMinimumSize(new Dimension(2 * Constants.MAX_CARDS_PER_PLAYER
				* (CARD_WIDTH + STUFF_GAP), 720));
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
		currentPlayer = gameState.getCurrentPlayerColor();
		currentPlayerColor = getPlayerColor(currentPlayer);
		updateBuffer = true;

		repaint();

		gameEnded = gameState.gameEnded();
	}

	public synchronized void requestMove(final int turn) {
		turnToAnswer = turn;
		// Set MouseListener
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	private synchronized void sendMove(final MoveContainer move) {

		removeMouseListener(mouseAdapter);
		removeMouseMotionListener(mouseAdapter);

		if ((turnToAnswer == gameState.getTurn()) && !gameEnded) {
			RenderFacade.getInstance().sendMove(move);
			turnToAnswer = -1;
		}
	}

	public Image getImage() {
		// copied from 2011
		BufferedImage img;
		img = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
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

		for (Card c : gameState.getOpenCards()) {
			paintCard(g2, x, y, c.symbol);
			x += CARD_WIDTH + STUFF_GAP;
		}

		// Spielerinfo Rot
		Player player = gameState.getRedPlayer();
		x = BORDER_SIZE + STUFF_GAP;
		y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP
				- CARD_HEIGTH;

		for (Card card : player.getCards()) {
			paintCard(g2, x, y, card.symbol);
			x += CARD_WIDTH + STUFF_GAP;
		}

		g2.setFont(h3);
		y -= (STUFF_GAP + 5);
		g2.setColor(getPlayerColor(PlayerColor.RED));
		g2.drawString(player.getDisplayName(), 2 * BORDER_SIZE, y);

		// Spielerinfo Blau
		player = gameState.getBluePlayer();
		x = getWidth() - BORDER_SIZE - STUFF_GAP - CARD_WIDTH;
		y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP
				- CARD_HEIGTH;

		for (Card card : player.getCards()) {
			paintCard(g2, x, y, card.symbol);
			x -= CARD_WIDTH + STUFF_GAP;
		}

		g2.setFont(h3);
		y -= (STUFF_GAP + 5);
		g2.setColor(getPlayerColor(PlayerColor.BLUE));
		int nameWidth = fmH3.stringWidth(player.getDisplayName());
		g2.drawString(player.getDisplayName(), getWidth() - 2 * BORDER_SIZE
				- nameWidth, y);

		// Spielbrett

		LinkedList<Point> boardMap = new LinkedList<Point>();
		x = BORDER_SIZE + STUFF_GAP;
		y = BORDER_SIZE + 3 * STUFF_GAP + CARD_HEIGTH;
		Board board = gameState.getBoard();

		g2.setStroke(stroke20);
		g2.setColor(Color.WHITE);

		boolean direction = true; // true = right

		for (int i = 0; i < board.size(); i++) {
			Field field = board.getField(i);
			List<Pirate> pirates = field.getPirates();
			switch (field.type) {
			case START:
				// DRAW START FIELD
				g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setColor(Color.DARK_GRAY);
				g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setFont(h4);
				String s = "Start";
				int sW = fmH4.stringWidth(s);
				g2.drawString(s, x + 4, y + FIELD_HEIGHT / 2);
				g2.setColor(Color.WHITE);
				// Spielfiguren
				if (field.numPirates(PlayerColor.RED) > 0) {
					paintToken(g2, x, y, field.numPirates(PlayerColor.RED),
							PlayerColor.RED);
				}
				if (field.numPirates(PlayerColor.BLUE) > 0) {
					paintToken(g2, x, y, field.numPirates(PlayerColor.BLUE),
							PlayerColor.BLUE);
				}
				boardMap.add(new Point(x, y));
				y += FIELD_HEIGHT;
				break;
			case FINISH:
				// DRAW FINISH FIELD
				// y += FIELD_HEIGHT;
				g2.setColor(Color.WHITE);
				g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setColor(Color.DARK_GRAY);
				g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setFont(h4);
				s = "Ziel";
				sW = fmH4.stringWidth(s);
				g2.drawString(s, x + 4, y + FIELD_HEIGHT / 2);
				g2.setColor(Color.WHITE);
				// Spielfiguren
				if (field.numPirates(PlayerColor.RED) > 0) {
					paintToken(g2, x, y, field.numPirates(PlayerColor.RED),
							PlayerColor.RED);
				}
				if (field.numPirates(PlayerColor.BLUE) > 0) {
					paintToken(g2, x, y, field.numPirates(PlayerColor.BLUE),
							PlayerColor.BLUE);
				}
				boardMap.add(new Point(x, y));
				break;
			case SYMBOL:
				// SELECT IMAGE
				Image img;
				switch (field.symbol) {
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
				if (x + FIELD_WIDTH >= getBoardRightX() - STUFF_GAP
						&& direction == true) {
					direction = false;

					y += FIELD_HEIGHT;
					x -= FIELD_WIDTH;
					g2.setColor(Color.DARK_GRAY);
					g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.drawImage(img, x + 2, y + 2, FIELD_WIDTH - 2,
							FIELD_HEIGHT - 2, this);
					// Spielfiguren
					if (field.numPirates(PlayerColor.RED) > 0) {
						paintToken(g2, x, y, field.numPirates(PlayerColor.RED),
								PlayerColor.RED);
					}
					if (field.numPirates(PlayerColor.BLUE) > 0) {
						paintToken(g2, x, y,
								field.numPirates(PlayerColor.BLUE),
								PlayerColor.BLUE);
					}
					boardMap.add(new Point(x, y));
					y += FIELD_HEIGHT;
				} else
				// would the next field be out of left border?
				if (x < BORDER_SIZE + STUFF_GAP && direction == false) {
					direction = true;

					y += FIELD_HEIGHT;
					x += FIELD_WIDTH;
					// g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.setColor(Color.DARK_GRAY);
					g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.drawImage(img, x + 2, y + 2, FIELD_WIDTH - 2,
							FIELD_HEIGHT - 2, this);
					// Spielfiguren
					if (field.numPirates(PlayerColor.RED) > 0) {
						paintToken(g2, x, y, field.numPirates(PlayerColor.RED),
								PlayerColor.RED);
					}
					if (field.numPirates(PlayerColor.BLUE) > 0) {
						paintToken(g2, x, y,
								field.numPirates(PlayerColor.BLUE),
								PlayerColor.BLUE);
					}
					boardMap.add(new Point(x, y));
					y += FIELD_HEIGHT;
				} else if (direction == true) {
					// draw and go to the right
					// g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.setColor(Color.DARK_GRAY);
					g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.drawImage(img, x + 2, y + 2, FIELD_WIDTH - 2,
							FIELD_HEIGHT - 2, this);
					// Spielfiguren
					if (field.numPirates(PlayerColor.RED) > 0) {
						paintToken(g2, x, y, field.numPirates(PlayerColor.RED),
								PlayerColor.RED);
					}
					if (field.numPirates(PlayerColor.BLUE) > 0) {
						paintToken(g2, x, y,
								field.numPirates(PlayerColor.BLUE),
								PlayerColor.BLUE);
					}
					boardMap.add(new Point(x, y));
					x += FIELD_WIDTH;
				} else {
					// got to the left
					// g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.setColor(Color.DARK_GRAY);
					g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
					g2.drawImage(img, x + 2, y + 2, FIELD_WIDTH - 2,
							FIELD_HEIGHT - 2, this);
					// Spielfiguren
					if (field.numPirates(PlayerColor.RED) > 0) {
						paintToken(g2, x, y, field.numPirates(PlayerColor.RED),
								PlayerColor.RED);
					}
					if (field.numPirates(PlayerColor.BLUE) > 0) {
						paintToken(g2, x, y,
								field.numPirates(PlayerColor.BLUE),
								PlayerColor.BLUE);
					}
					boardMap.add(new Point(x, y));
					x -= FIELD_WIDTH;
				}
				break;
			}
		}
		// possible fields
		if (!possibleFields.isEmpty()) {
			g2.setColor(Color.GREEN);
			for (int index = 0; index < possibleFields.size(); index++) {
				Point p = boardMap.get(possibleFields.get(index));
				g2.drawRect(p.x, p.y, FIELD_WIDTH, FIELD_HEIGHT);
				System.out.println("Cartagena DEBUG- : possibleFIeld: "
						+ possibleFields.get(index));
			}
		}

		// Selected field
		if (selectedField != -1) {
			Point p = boardMap.get(selectedField);
			g2.setColor(getPlayerColor(currentPlayer));
			g2.drawRect(p.x, p.y, FIELD_WIDTH, FIELD_HEIGHT);
		}
		System.out
				.println("Cartagena DEBUG- : selectedFIeld: " + selectedField);
		this.BoardMap = boardMap;

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

		g2.drawImage(img, x + 2, imgY, CARD_WIDTH - 4, imgHeight, this);
	}

	private void paintToken(Graphics2D g2, int x, int y, int num,
			PlayerColor color) {
		// Spielfiguren
		g2.setFont(h5);
		if (color == PlayerColor.RED) {
			g2.setColor(Color.RED);
			g2.drawString(Integer.toString(num), x + 2, y + fmH5.getHeight());
		} else {
			g2.setColor(Color.BLUE);
			g2.drawString(Integer.toString(num), (x + FIELD_WIDTH) - 10, y
					+ fmH5.getHeight());
		}

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
		int heigth = PROGRESS_BAR_HEIGTH + 2 * STUFF_GAP + fmH3.getHeight()
				+ CARD_HEIGTH;
		g2.fillRect(BORDER_SIZE, getHeight() - BORDER_SIZE - heigth, getWidth()
				- 2 * BORDER_SIZE, heigth);

		// seitenleiste, hintergrund
		g2.fillRect(getWidth() - BORDER_SIZE - SIDE_BAR_WIDTH, BORDER_SIZE,
				SIDE_BAR_WIDTH, getHeight() - 2 * BORDER_SIZE - heigth);

		// obere Leiste Kartenstapel
		g2.fillRect(BORDER_SIZE, BORDER_SIZE, getWidth() - 2 * BORDER_SIZE
				- SIDE_BAR_WIDTH, CARD_HEIGTH + 2 * STUFF_GAP);

		// seitenleiste info

		Player player = gameState.getRedPlayer();
		int x = getWidth() - BORDER_SIZE - SIDE_BAR_WIDTH;
		int y = BORDER_SIZE + STUFF_GAP + fmH0.getHeight();

		// red player
		g2.setFont(h0);
		g2.setColor(getPlayerColor(player.getPlayerColor()));
		g2.drawString(String.valueOf(player.getPoints()), x, y);

		y += fmH3.getHeight();
		if (currentPlayer == PlayerColor.RED) {
			g2.setColor(Color.black);
			g2.setFont(h3);
			if (movesToMake - movesMade > 1) {
				g2.drawString(movesToMake - movesMade + " Züge übrig.", x, y);
			} else {
				g2.drawString(movesToMake - movesMade + " Zug übrig.", x, y);
			}

			String endTurn = "Zug Beenden";
			g2.setStroke(stroke15);
			g2.setColor(Color.WHITE);
			g2.fillRoundRect(x, y, fmH3.stringWidth(endTurn), fmH3.getHeight(),
					10, 10);
			g2.setColor(Color.DARK_GRAY);
			g2.drawRoundRect(x, y, fmH3.stringWidth(endTurn), fmH3.getHeight(),
					10, 10);
			y += fmH3.getHeight();
			g2.setColor(Color.black);
			g2.drawString(endTurn, x, y);
		}
		// blue player
		g2.setFont(h0);
		y += fmH0.getHeight();
		player = gameState.getBluePlayer();
		g2.setColor(getPlayerColor(player.getPlayerColor()));
		g2.drawString(String.valueOf(player.getPoints()), x, y);

		y += fmH3.getHeight();
		if (currentPlayer == PlayerColor.BLUE) {
			g2.setColor(Color.black);
			g2.setFont(h3);
			if (movesToMake - movesMade > 1) {
				g2.drawString(movesToMake - movesMade + " Züge übrig.", x, y);
			} else {
				g2.drawString(movesToMake - movesMade + " Zug übrig.", x, y);
			}
		}

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

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			requestFocusInWindow();
			if (e.getButton() == MouseEvent.BUTTON1) {
				int x = e.getX();
				int y = e.getY();
				// System.out.println("CARTAGENA - DEBUG: x: " + x + " - y: " +
				// y);

				for (int i = 0; i < BoardMap.size(); i++) {
					Point p = BoardMap.get(i);
					if (x > p.x && x < p.x + FIELD_WIDTH && y > p.y
							&& y < p.y + FIELD_HEIGHT) {
						System.out
								.println("CARTAGENA - DEBUG: Klick auf Feld mit Index "
										+ i);
						if (gameState.getBoard().hasPirates(i,
								gameState.getCurrentPlayerColor())
								&& selectedField == -1) {
							selectedField = i;
							System.out
									.println("CARTAGENA - DEBUG: Feld mit Piraten geklickt "
											+ selectedField);
							// fill the possible Field list
							possibleFields = new LinkedList<Integer>();

							// LinkedList<Card> cards = (LinkedList<Card>)
							// gameState
							// .getCurrentPlayer().getCards();
							for (SymbolType symbol : SymbolType.values()) {
								if (gameState.getCurrentPlayer()
										.hasCard(symbol)) {
									possibleFields
											.add(gameState.getBoard()
													.getNextField(
															selectedField,
															symbol));
									System.out
											.println("CARTAGENA DEBUG - Adding possible Field: "
													+ gameState
															.getBoard()
															.getNextField(
																	selectedField,
																	symbol));
								}
							}
							if (gameState.getBoard().getPreviousField(
									selectedField) != -1) {
								possibleFields.add(gameState.getBoard()
										.getPreviousField(selectedField));
							}
						} else if (selectedField != -1) {
							// Überprüfe ob geklicktes Feld wählbar ist und
							// führe move aus
							Move move = null;
							for (int pF : possibleFields) {
								if (i == pF) {
									if (i < selectedField) {
										// backward Move
										move = new BackwardMove(selectedField);
									} else if (i > selectedField) {
										// Forward Move
										move = new ForwardMove(selectedField,
												gameState.getBoard()
														.getField(i).symbol);
										// Spezialfall Zielfeld
										// TODO Spieler fragen welche Karte
										// abgelegt werden soll.
										if (i == gameState.getBoard().size() - 1) {
											move = new ForwardMove(
													selectedField,
													gameState
															.getCurrentPlayer()
															.getCards().get(0).symbol);
										}
									}
								}
							}
							if (move != null) {
								try {
									System.out.println("board.haspirates: "
											+ gameState.getBoard().hasPirates(
													selectedField,
													gameState
															.getCurrentPlayer()
															.getPlayerColor()));
									move.perform(gameState,
											gameState.getCurrentPlayer());
									selectedField = -1;
									possibleFields = new LinkedList<Integer>();
								} catch (InvalidMoveException exception) {
									System.out.println("CARTAGENA GUI "
											+ exception.getMessage());
								}
								switch (movesMade) {
								case 0:
									firstMove = move;
									movesMade += 1;
									break;
								case 1:
									secondMove = move;
									movesMade += 1;
									break;
								case 2:
									thirdMove = move;
								default:
									MoveContainer moveC = new MoveContainer(
											firstMove, secondMove, thirdMove);
									movesMade = 0;
									sendMove(moveC);
								}

							} else {
								selectedField = -1;
								possibleFields = new LinkedList<Integer>();
							}

						} else {
							selectedField = -1;
							possibleFields = new LinkedList<Integer>();
							// System.out
							// .println("CARTAGENA - DEBUG: Else fall:" +
							// selectedField);
						}
					}
				}
				// nach jedem Klick wird neu gezeichnet
				updateBuffer = true;
				repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}
	};

	private int getBoardTopY() {
		return BORDER_SIZE + CARD_HEIGTH + 2 * STUFF_GAP;
	}

	private int getBoardBottomY() {
		return this.getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH
				- CARD_HEIGTH - fmH3.getHeight() - 2 * STUFF_GAP;
	}

	private int getBoardRightX() {
		return this.getWidth() - BORDER_SIZE - SIDE_BAR_WIDTH;
	}

	private class Point {
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
