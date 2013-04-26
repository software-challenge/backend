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
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
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
import sc.plugin2013.ForwardMove;
import sc.plugin2013.MoveContainer;
//import sc.plugin2013.MoveType;
import sc.plugin2013.Pirate;
import sc.plugin2013.Player;
import sc.plugin2013.PlayerColor;
import sc.plugin2013.GameState;
import sc.plugin2013.SymbolType;

@SuppressWarnings("serial")
public class FrameRenderer extends JComponent {

	// konstanten
	private final static int BORDER_SIZE = 6;
	private static final int PROGRESS_ICON_WIDTH = 90;
	private static final int PROGRESS_ICON_HEIGHT = 60;
	private static final int PROGRESS_BAR_HEIGTH = 36;

	private static final int SIDE_BAR_WIDTH = 275;

	private static final int STUFF_GAP = 8;
	private static final int SEGMENT_GAP = 6;
	private static final int GAP_SIZE = 10;

	private static final int CARD_WIDTH = 48;
	private static final int CARD_HEIGTH = 48 + 16;

	private static final long waitTime = 350l;

	private int FIELD_WIDTH = 64;
	private int FIELD_HEIGHT = FIELD_WIDTH;

	// image components
	private BufferedImage buffer;
	private boolean updateBuffer;
	private final Image bgImage;
	private Image scaledBgImage;
	private final Image progressIcon;
	private final Image finishImage;
	private final Image finishImageGreen;
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
	private boolean gameEnded = false;
	private int turnToAnswer = -1;
	// liste der oberen, linken eckpunkte der spielfelder
	private LinkedList<Point> BoardMap;
	// Feldnummer über der eine Spielfigur abgelegt werden kann
	private int hoveredField = -1;
	// liste der oberen linken eckpunkte der roten spielerkarten
	private LinkedList<Point> redCardMap;
	// liste der oberen linken eckpunkte der blauen spielerkarten
	private LinkedList<Point> blueCardMap;
	// liste der Spielfiguren
	private LinkedList<Token> tokenList;
	// Position des Zug Beenden Buttons
	private Point cancelMoveButtonPos;
	private boolean humanMove = false;
	private int movesMade = 0;
	private final int movesToMake = 3;
	private Move firstMove;
	private Move secondMove;
	private Move thirdMove;
	// private MoveContainer lastMoveSend;
	private int selectedField = -1;
	private HashSet<Integer> possibleFields = new HashSet<Integer>();
	// Variablen für den spezialfall "Aufs Zielfeld ziehen"
	private boolean throwAwayCard = false;
	private HashSet<SymbolType> possibleCards;
	// Spielfigur die beim ziehen bewegt wird
	private Token movingToken;
	// Anzahl von Karten die nach einem Rückwärtszug hervorgehoben werden
	// private int numCardsHighlight = 0;

	// Strings
	private String endTurn = "Zug beenden";
	private Object LOCK = new Object();

	public FrameRenderer() {

		updateBuffer = true;
		this.progressIcon = loadImage("resource/game/boot.png");
		this.bgImage = loadImage("resource/game/background.png");

		this.finishImage = loadImage("resource/game/boot-senkrecht.png");
		this.finishImageGreen = loadImage("resource/game/boot-senkrecht-green.png");
		this.skullImage = loadImage("resource/game/skull.png");
		this.hatImage = loadImage("resource/game/hat.png");
		this.daggerImage = loadImage("resource/game/dagger.png");
		this.bottleImage = loadImage("resource/game/bottle.png");
		this.keyImage = loadImage("resource/game/key.png");
		this.pistolImage = loadImage("resource/game/pistol.png");
		this.humanMove = false;

		this.BoardMap = new LinkedList<Point>();
		this.tokenList = new LinkedList<Token>();

		setMinimumSize(new Dimension(2 * Constants.MAX_CARDS_PER_PLAYER
				* (CARD_WIDTH + STUFF_GAP), 720));

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

		if (this.gameState != null) {
			int turnDiff = gameState.getTurn() - this.gameState.getTurn();
			MoveContainer mC = gameState.getLastMove();
			if (turnDiff < 0 || gameState.getTurn() == 0) {
				this.turnToAnswer = -1;
			}

			if (!humanMove && turnDiff == 1 && mC != null) {
				if (mC.firstMove != null) {
					animateTokenMovement(mC.firstMove);
					try {
						mC.firstMove.perform(this.gameState,
								this.gameState.getCurrentPlayer());
						this.gameState.prepareNextTurn(mC.firstMove);
					} catch (InvalidMoveException e) {
						e.printStackTrace();
					}

					generateBoardMap();
					generateTokenMap();
					repaint();
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (mC.secondMove != null) {
					animateTokenMovement(mC.secondMove);
					try {
						mC.secondMove.perform(this.gameState,
								this.gameState.getCurrentPlayer());
						this.gameState.prepareNextTurn(mC.secondMove);
					} catch (InvalidMoveException e) {
						e.printStackTrace();
					}
					generateBoardMap();
					generateTokenMap();
					repaint();
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (mC.thirdMove != null) {
					animateTokenMovement(mC.thirdMove);
					try {
						mC.thirdMove.perform(this.gameState,
								this.gameState.getCurrentPlayer());
						this.gameState.prepareNextTurn(mC.thirdMove);
					} catch (InvalidMoveException e) {
						e.printStackTrace();
					}
					generateBoardMap();
					generateTokenMap();
					repaint();
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// Spieler geht einen Zug zurück
		// numCardsHighlight = 0;
		humanMove = false;
		removeMouseListener(mouseAdapter);
		removeMouseListener(mouseAdapter);
		if (gameState.getTurn() == turnToAnswer) {
			// Spieler ist zurück und wieder nach vorn gegangen und ist nun beim
			// noch nicht beantworteten Zug
			humanMove = true;
			addMouseListener(mouseAdapter);
			addMouseMotionListener(mouseAdapter);
		}

		// aktuellen spielstand sichern
		try {
			this.gameState = (GameState) gameState.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		currentPlayer = gameState.getCurrentPlayerColor();
		currentPlayerColor = getPlayerColor(currentPlayer);

		movesMade = 0;

		updateBuffer = true;
		generateBoardMap();
		generateTokenMap();
		repaint();

		gameEnded = gameState.gameEnded();
	}

	private void generateBoardMap() {
		LinkedList<Point> boardMap = new LinkedList<Point>();
		int x = BORDER_SIZE + STUFF_GAP;
		int y = BORDER_SIZE + 3 * STUFF_GAP + CARD_HEIGTH;

		Board board = gameState.getBoard();
		boolean direction = true; // true = right

		for (int i = 0; i < board.size(); i++) {
			Field field = board.getField(i);
			switch (field.type) {
			case START:
				boardMap.add(new Point(x, y));
				x += FIELD_WIDTH;
				break;
			case FINISH:
				if (x + FIELD_WIDTH >= getBoardRightX() - STUFF_GAP
						&& direction == true) {
					// out of right border
					y += FIELD_HEIGHT + SEGMENT_GAP + (FIELD_HEIGHT / 2);
					x -= FIELD_WIDTH;
				} else if (x < BORDER_SIZE + STUFF_GAP && direction == false) {
					// out of left border
					y += FIELD_HEIGHT + SEGMENT_GAP + (FIELD_HEIGHT / 2);
					x += FIELD_WIDTH;
				} else if (direction == true) {
					x += SEGMENT_GAP;
				} else {
					x -= SEGMENT_GAP;
				}
				boardMap.add(new Point(x, y));
				break;
			case SYMBOL:
				// would the current field be out of right side?
				if (x + FIELD_WIDTH >= getBoardRightX() - STUFF_GAP
						&& direction == true) {
					direction = false;
					if (i % 6 == 1) {
						y += SEGMENT_GAP;
					}

					y += FIELD_HEIGHT;
					x -= FIELD_WIDTH;
					boardMap.add(new Point(x, y));
					// if (i % 6 == 0) {
					// y += SEGMENT_GAP;
					// }
					y += FIELD_HEIGHT;
				} else
				// would the next field be out of left border?
				if (x < BORDER_SIZE + STUFF_GAP && direction == false) {
					direction = true;

					if (i % 6 == 1) {
						y += SEGMENT_GAP;
					}
					y += FIELD_HEIGHT;
					x += FIELD_WIDTH;
					boardMap.add(new Point(x, y));

					y += FIELD_HEIGHT;
				} else if (direction == true) {

					if (i % 6 == 1) {
						x += SEGMENT_GAP;
					}
					boardMap.add(new Point(x, y));
					x += FIELD_WIDTH;
				} else {

					if (i % 6 == 1) {
						x -= SEGMENT_GAP;
					}
					boardMap.add(new Point(x, y));
					x -= FIELD_WIDTH;
				}
				break;
			}
		}
		this.BoardMap = boardMap;
	}

	private void generateTokenMap() {
		Board board = this.gameState.getBoard();
		this.tokenList = new LinkedList<Token>();
		for (int index = 0; index < board.size(); index++) {
			LinkedList<Pirate> pirates = (LinkedList<Pirate>) board.getField(
					index).getPirates();
			if (!pirates.isEmpty()) {
				if (board.hasPirates(index, PlayerColor.BLUE)) {
					tokenList.add(new Token(BoardMap.get(index).x, BoardMap
							.get(index).y, PlayerColor.BLUE, board
							.numPiratesOf(index, PlayerColor.BLUE), index));
				}
				if (board.hasPirates(index, PlayerColor.RED)) {
					tokenList.add(new Token(BoardMap.get(index).x, BoardMap
							.get(index).y, PlayerColor.RED, board.numPiratesOf(
							index, PlayerColor.RED), index));
				}

			}
		}
	}

	private synchronized void animateTokenMovement(final Move move) {
		final int FPS = 30;
		int startField = 0;
		int targetField = 0;
		if (move.getClass().equals(ForwardMove.class)) {
			ForwardMove fMove = (ForwardMove) move;
			startField = fMove.fieldIndex;
			targetField = this.gameState.getBoard().getNextField(startField,
					fMove.symbol);
		} else if (move.getClass().equals(BackwardMove.class)) {
			BackwardMove bMove = (BackwardMove) move;
			startField = bMove.fieldIndex;
			targetField = this.gameState.getBoard()
					.getPreviousField(startField);
		}

		Token tokenToMove = null;
		boolean add = false;
		for (Token t : tokenList) {
			if (t.fieldIndex == startField
					&& t.owner == gameState.getCurrentPlayerColor()) {
				if (t.number == 1) {
					tokenToMove = t;
				} else {
					t.changeNum(t.number--);
					tokenToMove = new Token(t.x, t.y,
							gameState.getCurrentPlayerColor(), 1, targetField);
					add = true;
				}

			}
		}
		if (add) {
			tokenList.add(tokenToMove);
		}
		Point startPoint = BoardMap.get(startField);
		Point targetPoint = BoardMap.get(targetField);
		Point renderPoint = new Point(startPoint.x, startPoint.y);
		if (OPTIONS[MOVEMENT]) {
			System.out.println("Animation takes place");
			double pixelPerFrame = ((double) getWidth()) / (1.5 * FPS);
			double distance = Math.sqrt(Math.pow(startPoint.x - targetPoint.x,
					2) + Math.pow(startPoint.y - targetPoint.y, 2));
			final int frames = (int) Math.ceil(distance / pixelPerFrame);
			long startTime = System.currentTimeMillis();

			final Point o = new Point(startPoint.x, startPoint.y);
			final Point dP = new Point(targetPoint.x - startPoint.x,
					targetPoint.y - startPoint.y);
			for (int frame = 0; frame < frames; frame++) {
				double scale = (double) frame / (double) frames;
				tokenToMove.moveToken(renderPoint.x, renderPoint.y);
				renderPoint.x = o.x + (int) (scale * (double) dP.x);
				renderPoint.y = o.y + (int) (scale * (double) dP.y);
				tokenToMove.moveToken(renderPoint.x, renderPoint.y);

				updateBuffer = true;
				repaint();

				synchronized (LOCK) {
					LOCK.notify();
				}

				try {
					long duration = startTime + (frame + 1) * (1500 / FPS)
							- System.currentTimeMillis();
					Thread.sleep(duration > 0 ? duration : 0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		tokenToMove.moveToken(targetPoint.x, targetPoint.y);
		setEnabled(true);
	}

	public synchronized void requestMove(final int turn) {
		turnToAnswer = turn;
		removeMouseListener(mouseAdapter);
		removeMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		humanMove = true;
		updateBuffer = true;
		repaint();
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
		if (updateBuffer && gameState != null) {
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
		String msg = "Das Spiel ist zu Ende!";

		PlayerColor winner = gameState.winner();
		if (winner == PlayerColor.RED) {
			msg = gameState.getPlayerNames()[0] + " hat gewonnen!";
		} else if (winner == PlayerColor.BLUE) {
			msg = gameState.getPlayerNames()[1] + " hat gewonnen!";
		}

		String info = gameState.winningReason();
		int delim = info.indexOf("\\n");

		String info1 = info;
		String info2 = "";
		if (delim >= 0) {
			info1 = info.substring(0, delim);
			info2 = info.substring(delim + 2);
		}

		int msgW = fmH2.stringWidth(msg);
		int msgH = fmH2.getHeight();
		int info1W = fmH4.stringWidth(info1);
		int info2W = fmH4.stringWidth(info2);
		int infoW = Math.max(info1W, info2W);
		int infoH = 2 * fmH4.getHeight() + 3;
		int w = Math.max(msgW, infoW);
		int h = msgH + infoH;
		int xCenter = BORDER_SIZE + (getWidth() - SIDE_BAR_WIDTH) / 2;

		g2.setColor(getTransparentColor(new Color(255, 255, 255), 192));
		g2.fillRoundRect(xCenter - w / 2 - 20,
				getHeight() / 2 - msgH - 5 - 100, w + 40, h + 15, 20, 20);

		h = getHeight() / 2 - 5 - 100;
		g2.setFont(h2);
		g2.setColor(getPlayerColor(winner, true));
		g2.drawString(msg, xCenter - msgW / 2, h);

		h += msgH - 10;
		g2.setFont(h4);
		g2.setColor(Color.BLACK);
		g2.drawString(info1, xCenter - info1W / 2, h);

		h += 20;
		g2.drawString(info2, xCenter - info2W / 2, h);

	}

	private void paintDynamicComponents(Graphics2D g2) {
		for (Token t : tokenList) {
			t.paintToken(g2);
		}
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
		setFieldWidthHeight();

		System.gc();
		updateBuffer = true;
		if (this.gameState != null) {
			generateBoardMap();
			generateTokenMap();
		}
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

		int round = gameState.getRound() + 1;
		String roundString = Integer.toString(gameState.getRound() + 1);
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

		g2.setColor(new Color(75, 143, 207));
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
		g2.drawImage(progressIcon, left + progress - PROGRESS_ICON_WIDTH / 2
				+ 3, getHeight() - PROGRESS_ICON_HEIGHT - 3,
				PROGRESS_ICON_WIDTH, PROGRESS_ICON_HEIGHT, this);

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
			paintCard(g2, x, y, c.symbol, false);
			x += CARD_WIDTH + STUFF_GAP;
		}
		// erste Karte Kennzeichnen;
		g2.setColor(Color.gray);
		g2.drawRoundRect(BORDER_SIZE + STUFF_GAP, BORDER_SIZE + STUFF_GAP,
				CARD_WIDTH, CARD_HEIGTH, 10, 10);

		// Spielerinfo Rot
		Player player = gameState.getRedPlayer();
		x = BORDER_SIZE + STUFF_GAP;
		y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP
				- CARD_HEIGTH;

		// Spielkartenmap
		redCardMap = new LinkedList<Point>();
		for (Card card : player.getCards()) {
			if (throwAwayCard && currentPlayer == PlayerColor.RED) {
				paintCard(g2, x, y, card.symbol, true);
			} else {
				paintCard(g2, x, y, card.symbol, false);
			}
			redCardMap.add(new Point(x, y));
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
		blueCardMap = new LinkedList<Point>();
		for (Card card : player.getCards()) {
			if (throwAwayCard && currentPlayer == PlayerColor.BLUE) {
				paintCard(g2, x, y, card.symbol, true);
			} else {
				paintCard(g2, x, y, card.symbol, false);
			}

			blueCardMap.add(new Point(x, y));
			x -= CARD_WIDTH + STUFF_GAP;
		}

		g2.setFont(h3);
		y -= (STUFF_GAP + 5);
		g2.setColor(getPlayerColor(PlayerColor.BLUE));
		int nameWidth = fmH3.stringWidth(player.getDisplayName());
		g2.drawString(player.getDisplayName(), getWidth() - 2 * BORDER_SIZE
				- nameWidth, y);

		// Cards Highlighting
		// if (numCardsHighlight > 0) {
		// LinkedList<Point> cardMap;
		// if (gameState.getCurrentPlayerColor() == PlayerColor.RED) {
		// cardMap = redCardMap;
		//
		// } else {
		// cardMap = blueCardMap;
		// }
		// g2.setColor(getPlayerColor(currentPlayer));
		//
		// for(int i= cardMap.size()-numCardsHighlight; i < cardMap.size();
		// i++){
		// g2.drawRoundRect(cardMap.get(i).x, cardMap.get(i).y, CARD_WIDTH,
		// CARD_HEIGTH, 10, 10);
		// }
		// }
		paintBoard(g2); // Spielbrett zeichnen
		paintPlayerPoints(g2); // Seitenleiste info zeichnen

	}

	private void paintCard(Graphics2D g2, int x, int y, SymbolType type,
			boolean throwAway) {

		g2.setStroke(stroke40);
		g2.setColor(Color.WHITE);
		g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGTH, 10, 10);

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

		g2.drawImage(img, x, y, CARD_WIDTH, CARD_HEIGTH, this);

		if (throwAway && possibleCards.contains(type)) {
			g2.setColor(Color.GREEN);
		} else {
			g2.setColor(Color.BLACK);
		}
		g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGTH, 10, 10);

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
	}

	private void paintBoard(Graphics2D g2) {
		Board board = gameState.getBoard();
		int x = 0;
		int y = 0;
		// Spielfelder einzeln
		g2.setStroke(stroke20);
		for (int i = 0; i < board.size(); i++) {
			Field field = board.getField(i);
			switch (field.type) {
			case START:
				// DRAW START FIELD
				x = BoardMap.get(i).x;
				y = BoardMap.get(i).y;
				g2.setColor(this.getTransparentColor(Color.WHITE, 192));
				g2.fillRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setColor(Color.DARK_GRAY);
				g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.setFont(h4);
				// String s = "Start";
				// int sW = fmH4.stringWidth(s);
				// g2.drawString(s, x + 4, y + FIELD_HEIGHT / 2);
				g2.setColor(Color.WHITE);
				x += FIELD_WIDTH;
				break;
			case FINISH:
				// DRAW FINISH FIELD
				x = BoardMap.get(i).x;
				y = BoardMap.get(i).y;
				g2.setColor(Color.DARK_GRAY);
				g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.drawImage(finishImage, x, y - (FIELD_HEIGHT / 2),
						FIELD_WIDTH, 2 * FIELD_HEIGHT, this);
				g2.setFont(h4);
				g2.setColor(Color.WHITE);
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
				x = BoardMap.get(i).x;
				y = BoardMap.get(i).y;
				g2.setColor(Color.DARK_GRAY);
				g2.drawRect(x, y, FIELD_WIDTH, FIELD_HEIGHT);
				g2.drawImage(img, x + 2, y + 2, FIELD_WIDTH - 2,
						FIELD_HEIGHT - 2, this);
				break;
			}
		}
		// possible fields
		if (!possibleFields.isEmpty()) {
			g2.setColor(Color.GREEN);
			Iterator<Integer> pFI = possibleFields.iterator();

			while (pFI.hasNext()) {
				int next = pFI.next();
				if (next == BoardMap.size() - 1) {
					// finish Feld
					g2.drawImage(finishImageGreen, x, y - (FIELD_HEIGHT / 2),
							FIELD_WIDTH, 2 * FIELD_HEIGHT, this);

				} else {
					Point p = BoardMap.get(next);
					g2.drawRect(p.x, p.y, FIELD_WIDTH, FIELD_HEIGHT);
				}
			}
		}

		// Selected field
		if (selectedField != -1) {
			Point p = BoardMap.get(selectedField);
			g2.setColor(getPlayerColor(currentPlayer));
			g2.drawRect(p.x, p.y, FIELD_WIDTH, FIELD_HEIGHT);
		}

		if (hoveredField != -1) {
			Point p = BoardMap.get(hoveredField);
			g2.setColor(getTransparentColor(Color.green, 140));
			g2.fillRect(p.x, p.y, FIELD_WIDTH, FIELD_HEIGHT);
		}
	}

	private void paintPlayerPoints(Graphics2D g2) {
		// seitenleiste info

		Player player = gameState.getRedPlayer();
		int x = getWidth() - BORDER_SIZE - SIDE_BAR_WIDTH + 2 * STUFF_GAP;
		int y = BORDER_SIZE + STUFF_GAP + fmH0.getHeight();

		// red player
		g2.setFont(h0);
		g2.setColor(getPlayerColor(player.getPlayerColor()));
		g2.drawString(String.valueOf(player.getPoints()), x, y);

		y += fmH3.getHeight();

		if (currentPlayer == PlayerColor.RED && !gameEnded) {
			g2.setColor(Color.black);
			g2.setFont(h3);
			if (movesToMake - movesMade > 1) {
				g2.drawString(movesToMake - movesMade + " Teilzüge übrig.", x,
						y);
			} else {
				g2.drawString(movesToMake - movesMade + " Teilzug übrig.", x, y);
			}
			if (humanMove) {
				y += STUFF_GAP;
				g2.setStroke(stroke15);
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(x, y, fmH3.stringWidth(endTurn) + 5,
						fmH3.getHeight() + 6, 10, 10);
				g2.setColor(Color.DARK_GRAY);
				g2.drawRoundRect(x, y, fmH3.stringWidth(endTurn) + 5,
						fmH3.getHeight() + 6, 10, 10);
				this.cancelMoveButtonPos = new Point(x, y);
				y += fmH3.getHeight();
				g2.setColor(Color.black);
				g2.drawString(endTurn, x + 2, y);
			}
		}
		// blue player
		g2.setFont(h0);
		y += fmH0.getHeight();
		player = gameState.getBluePlayer();
		g2.setColor(getPlayerColor(player.getPlayerColor()));
		g2.drawString(String.valueOf(player.getPoints()), x, y);

		y += fmH3.getHeight();
		if (currentPlayer == PlayerColor.BLUE && !gameEnded) {
			g2.setColor(Color.black);
			g2.setFont(h3);
			if (movesToMake - movesMade > 1) {
				g2.drawString(movesToMake - movesMade + " Teilzüge übrig.", x,
						y);
			} else {
				g2.drawString(movesToMake - movesMade + " Teilzug übrig.", x, y);
			}
			if (humanMove) {
				y += STUFF_GAP;
				g2.setStroke(stroke15);
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(x, y, fmH3.stringWidth(endTurn) + 5,
						fmH3.getHeight() + 6, 10, 10);
				g2.setColor(Color.DARK_GRAY);
				g2.drawRoundRect(x, y, fmH3.stringWidth(endTurn) + 5,
						fmH3.getHeight() + 6, 10, 10);
				this.cancelMoveButtonPos = new Point(x, y);

				y += fmH3.getHeight();
				g2.setColor(Color.black);
				g2.drawString(endTurn, x + 2, y);
			}
		}

		boolean hints = OPTIONS[DEBUG_VIEW];
		// Move Hints
		if (hints && this.gameState.getLastMove() != null) {
			y += fmH4.getHeight() + STUFF_GAP;
			g2.setFont(h4);
			g2.drawString("Letzter Zug : ", x, y);
			if (gameState.getLastMove().firstMove != null) {
				y += fmH4.getHeight() + STUFF_GAP;
				g2.drawString("1: ", x, y);
				g2.setFont(h5);
				List<DebugHint> hintList = gameState.getLastMove().firstMove
						.getHints();
				for (DebugHint hint : hintList) {
					y += fmH5.getHeight() + STUFF_GAP;
					g2.drawString(hint.content, x, y);
				}
			}
			if (gameState.getLastMove().secondMove != null) {
				y += fmH4.getHeight() + STUFF_GAP;
				g2.drawString("2: ", x, y);
				g2.setFont(h5);
				List<DebugHint> hintList = gameState.getLastMove().secondMove
						.getHints();
				for (DebugHint hint : hintList) {
					y += fmH5.getHeight() + STUFF_GAP;
					g2.drawString(hint.content, x, y);
				}
			}
			if (gameState.getLastMove().thirdMove != null) {
				y += fmH4.getHeight() + STUFF_GAP;
				g2.drawString("3: ", x, y);
				g2.setFont(h5);
				List<DebugHint> hintList = gameState.getLastMove().thirdMove
						.getHints();
				for (DebugHint hint : hintList) {
					y += fmH5.getHeight() + STUFF_GAP;
					g2.drawString(hint.content, x, y);
				}
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

	private void constructMoves(Move move) {
		try {
			// wenn ein move konstruiert wurde
			// int numCards = gameState.getCurrentPlayer().getNumCards();
			// if (move.getClass().equals(BackwardMove.class) && numCards < 8) {
			// BackwardMove bM = (BackwardMove) move;
			// int numPirates = gameState
			// .getBoard()
			// .getPirates(
			// gameState.getBoard().getPreviousField(
			// bM.fieldIndex)).size();
			// if (numCards == 7) {
			// numCardsHighlight = 1;
			// } else {
			// numCardsHighlight = numPirates;
			// }
			// }
			move.perform(gameState, gameState.getCurrentPlayer());
			gameState.prepareNextTurn(move);
			selectedField = -1;
			hoveredField = -1;
			possibleFields = new HashSet<Integer>();
		} catch (InvalidMoveException exception) {
			System.out.println("CARTAGENA GUI " + exception.getMessage());
		}
		if (gameState.playerFinished(currentPlayer)) {
			MoveContainer moveC = new MoveContainer();
			switch (movesMade) {
			case 0:
				moveC.addMove(move);
				break;
			case 1:
				moveC.addMove(firstMove);
				moveC.addMove(move);
				break;
			case 2:
				moveC.addMove(firstMove);
				moveC.addMove(secondMove);
				moveC.addMove(move);
				break;
			}
			sendMove(moveC);
			movesMade = 0;
		} else {
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
				MoveContainer moveC = new MoveContainer(firstMove, secondMove,
						thirdMove);
				movesMade = 0;
				sendMove(moveC);
			}
		}
	}

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public synchronized void mousePressed(MouseEvent e) {
			requestFocusInWindow();
			if (e.getButton() == MouseEvent.BUTTON1) {
				int x = e.getX();
				int y = e.getY();
				// überprüfe ob der cancelButton geklickt wurde

				if (x > cancelMoveButtonPos.x
						&& x < cancelMoveButtonPos.x
								+ fmH3.stringWidth(endTurn) + 5
						&& y > cancelMoveButtonPos.y
						&& y < cancelMoveButtonPos.y + fmH3.getHeight() + 6) {
					MoveContainer moveC;
					switch (movesMade) {
					case 1:
						moveC = new MoveContainer(firstMove);
						break;
					case 2: // do
						moveC = new MoveContainer(firstMove, secondMove);
						break;
					default:
						moveC = new MoveContainer();
					}
					movesMade = 0;
					sendMove(moveC);
				}
				// wenn eine Karte weggeschmissen werden soll überprüfe ob eine
				// Karte geklickt wurde
				if (throwAwayCard) {
					LinkedList<Point> cardMap;
					ForwardMove move = null;
					if (currentPlayer == PlayerColor.RED) {
						cardMap = redCardMap;
					} else {
						cardMap = blueCardMap;
					}
					boolean pressedCard = false;
					for (int i = 0; i < cardMap.size(); i++) {
						Point point = new Point(cardMap.get(i).x,
								cardMap.get(i).y);
						if (x > point.x && x < point.x + CARD_WIDTH
								&& y > point.y && y < point.y + CARD_HEIGTH) {
							pressedCard = true;
							if (possibleCards
									.contains(gameState.getCurrentPlayer()
											.getCards().get(i).symbol)) {
								// eine wegwerfbare Karte wurde geklickt,
								// konstruiere einen move
								move = new ForwardMove(selectedField,
										gameState.getCurrentPlayer().getCards()
												.get(i).symbol);
								throwAwayCard = false;
								break;
							}
						}
					}

					if (move != null) {
						constructMoves(move);
						selectedField = -1;
						possibleFields = new HashSet<Integer>();
						generateBoardMap();
						generateTokenMap();
						updateBuffer = true;
						repaint();
					}

					if (!pressedCard) {
						throwAwayCard = false;
						selectedField = -1;
						possibleFields = new HashSet<Integer>();
						generateBoardMap();
						generateTokenMap();
						updateBuffer = true;
						repaint();
					}
				}

				// überprüfe ob ein Feld geklickt wurde
				if (!throwAwayCard) {
					// wenn nicht im wegwerfmodus
					for (int i = 0; i < BoardMap.size(); i++) {
						Point p = BoardMap.get(i);
						if (x > p.x && x < p.x + FIELD_WIDTH && y > p.y
								&& y < p.y + FIELD_HEIGHT) {
							if (gameState.getBoard().hasPirates(i,
									gameState.getCurrentPlayerColor())
									&& selectedField == -1) {
								// es wurde noch kein feld ausgewählt und auf
								// dem
								// gelickten Feld sind Piraten des Spieler
								// vorhanden
								selectedField = i;
								// fill the possible Field list
								possibleFields = new HashSet<Integer>();

								for (SymbolType symbol : SymbolType.values()) {
									if (gameState.getCurrentPlayer().hasCard(
											symbol)) {
										if (gameState.getBoard().getNextField(
												selectedField, symbol) != -1) {
											possibleFields.add(gameState
													.getBoard().getNextField(
															selectedField,
															symbol));
										}
									}
								}
								if (gameState.getBoard().getPreviousField(
										selectedField) != -1) {
									possibleFields.add(gameState.getBoard()
											.getPreviousField(selectedField));
								}
								// Ziehbares Token hinzufügen
								for (Token t : tokenList) {
									if (t.fieldIndex == selectedField
											&& t.owner == gameState
													.getCurrentPlayerColor()) {
										if (t.number == 1) {
											movingToken = t;
											movingToken.x = x;
											movingToken.y = y;
											movingToken.setDragable(true);
											break;
										} else {
											t.changeNum(t.number--);
											movingToken = new Token(
													x,
													y,
													gameState
															.getCurrentPlayerColor(),
													1, -1);
											tokenList.add(movingToken);
											movingToken.setDragable(true);
											break;
										}

									}
								}
								updateBuffer = true;
								repaint();
							} else {
								// kein valides feld geklickt
								selectedField = -1;
								possibleFields = new HashSet<Integer>();
								generateBoardMap();
								generateTokenMap();
								updateBuffer = true;
								repaint();
							}
						}
					}
				}
			}

		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if (selectedField != -1) {
				movingToken.x = x;
				movingToken.y = y;
				// Prüfung auf hovered Field
				for (int i = 0; i < BoardMap.size(); i++) {
					Point p = BoardMap.get(i);
					if (x > p.x && x < p.x + FIELD_WIDTH && y > p.y
							&& y < p.y + FIELD_HEIGHT) {
						if (possibleFields.contains(i)) {
							hoveredField = i;
							break;
						}
					} else {
						hoveredField = -1;
					}
				}
				updateBuffer = true;
				// nur bereich um die Maus neu zeichnen
				repaint(x - 2 * FIELD_WIDTH, y - 2 * FIELD_HEIGHT, x + 2
						* FIELD_WIDTH, y + 2 * FIELD_HEIGHT);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if (selectedField != -1) {
				// überprüfen ob auf einem Feld losgelassen wurde
				int releasedField = -1;
				for (int i = 0; i < BoardMap.size(); i++) {
					Point p = BoardMap.get(i);
					if (x > p.x && x < p.x + FIELD_WIDTH && y > p.y
							&& y < p.y + FIELD_HEIGHT) {
						releasedField = i;
						break;
					}
				}
				if (releasedField != -1) {
					Move move = null;
					for (int pF : possibleFields) {
						if (releasedField == pF) {
							if (releasedField < selectedField) {
								// backward Move
								move = new BackwardMove(selectedField);
								break;
							} else if (releasedField > selectedField) {
								// Forward Move
								move = new ForwardMove(selectedField,
										gameState.getBoard().getField(
												releasedField).symbol);
								// Spezialfall Zielfeld
								// eine Karte muss abgelegt werden
								if (releasedField == gameState.getBoard()
										.size() - 1) {
									throwAwayCard = true;

									// Spielfigur auf Schaluppe positionieren
									movingToken.x = BoardMap.getLast().x
											+ FIELD_WIDTH / 2;
									movingToken.y = BoardMap.getLast().y
											+ FIELD_HEIGHT / 12;
									// movingToken = null;

									possibleCards = new HashSet<SymbolType>();
									possibleCards.add(SymbolType.BOTTLE);
									possibleCards.add(SymbolType.DAGGER);
									possibleCards.add(SymbolType.HAT);
									possibleCards.add(SymbolType.KEY);
									possibleCards.add(SymbolType.PISTOL);
									possibleCards.add(SymbolType.SKULL);
									for (int j = selectedField + 1; j < gameState
											.getBoard().size() - 1; j++) {
										// remove all fields between
										// current field and finish
										// that contain no pirates
										if (gameState.getBoard().getField(j)
												.getPirates().isEmpty()) {
											possibleCards
													.remove(gameState
															.getBoard()
															.getField(j).symbol);
										}
									}

									// remove all Cards, that the active Player
									// doesnt have on hand
									for (SymbolType s : SymbolType.values()) {
										if (!gameState.getCurrentPlayer()
												.getCards()
												.contains(new Card(s))) {
											possibleCards.remove(s);
										}
									}

									if (possibleCards.size() == 1) {
										move = new ForwardMove(selectedField,
												(SymbolType) possibleCards
														.toArray()[0]);
										throwAwayCard = false;
									} else {
										move = null;
									}
									// letztes Feld ist das einzige
									possibleFields = new HashSet<Integer>();
									possibleFields.add(gameState.getBoard()
											.size() - 1);
								}
								break;
							} else {
								// nicht auf validem feld losgelassen
								selectedField = -1;
								hoveredField = -1;
								generateBoardMap();
								generateTokenMap();
								updateBuffer = true;
								repaint();
							}
						}
					}
					if (move != null) {
						constructMoves(move);
					} else if (!throwAwayCard) {
						// kein move konstruiert
						selectedField = -1;
						hoveredField = -1;
						possibleFields = new HashSet<Integer>();
					}
				} else {
					// nicht auf einem Feld losgelassen
					selectedField = -1;
					hoveredField = -1;
					possibleFields = new HashSet<Integer>();
				}
				generateBoardMap();
				generateTokenMap();
				updateBuffer = true;
				repaint();

			}
		}
	};

	/**
	 * Setzt die Größe der Spielfelder in Abhängigkeit der Boardgröße
	 * 
	 */
	private void setFieldWidthHeight() {
		int boardWidth = getBoardRightX() - BORDER_SIZE;
		int boardHeight = getBoardBottomY() - getBoardTopY();

		int numFields = Constants.SEGMENTS * Constants.SYMBOLS;
		int width = ((boardWidth - 3 * STUFF_GAP) / (numFields / 3));
		int height = (int) ((boardHeight - 2 * STUFF_GAP) / 5.5);
		int size = Math.min(width, height);
		this.FIELD_WIDTH = size;
		this.FIELD_HEIGHT = size;

	}

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

	private class Token {
		private int x, y;
		private PlayerColor owner;
		private int number, fieldIndex;
		private boolean dragable = false;

		public Token(int x, int y, PlayerColor playerColor, int num, int index) {
			this.x = x;
			this.y = y;
			this.owner = playerColor;
			this.number = num;
			this.fieldIndex = index;
		}

		public void moveToken(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void changeNum(int num) {
			this.number = num;
		}

		public void setDragable(boolean dragable) {
			this.dragable = dragable;
		}

		public void paintToken(Graphics2D g2) {
			int fieldSizeDiv12 = FIELD_HEIGHT / 12;
			// Setting up the Fontsize
			Font myFont = new Font("Helvetica", Font.BOLD, 3 * fieldSizeDiv12);
			FontMetrics myFM = fmPanel.getFontMetrics(myFont);
			g2.setFont(myFont);
			if (!dragable) {
				if (this.owner == PlayerColor.RED) {
					g2.setColor(Color.RED);
					int[] xVals = new int[3];
					int[] yVals = new int[3];
					xVals[0] = x + 3 * fieldSizeDiv12;
					xVals[1] = x + fieldSizeDiv12;
					xVals[2] = x + 5 * fieldSizeDiv12;
					yVals[0] = y + 2 * fieldSizeDiv12;
					yVals[1] = y + 11 * fieldSizeDiv12;
					yVals[2] = y + 11 * fieldSizeDiv12;
					g2.fillPolygon(xVals, yVals, 3);
					g2.setColor(Color.BLACK);
					g2.drawPolygon(xVals, yVals, 3);
					g2.setColor(Color.RED);
					g2.fillOval(x + fieldSizeDiv12, y + fieldSizeDiv12,
							4 * fieldSizeDiv12, 4 * fieldSizeDiv12);
					g2.setColor(Color.BLACK);
					g2.drawOval(x + fieldSizeDiv12, y + fieldSizeDiv12,
							4 * fieldSizeDiv12, 4 * fieldSizeDiv12);
					g2.setColor(Color.WHITE);
					if (number > 1) {

						g2.drawString(Integer.toString(number), x + 2
								* fieldSizeDiv12,
								y + fieldSizeDiv12 + myFM.getHeight());
					}

				} else {
					g2.setColor(Color.BLUE);
					int[] xVals = new int[3];
					int[] yVals = new int[3];
					xVals[0] = x + 9 * fieldSizeDiv12;
					xVals[1] = x + 7 * fieldSizeDiv12;
					xVals[2] = x + 11 * fieldSizeDiv12;
					yVals[0] = y + 2 * fieldSizeDiv12;
					yVals[1] = y + 11 * fieldSizeDiv12;
					yVals[2] = y + 11 * fieldSizeDiv12;
					g2.fillPolygon(xVals, yVals, 3);
					g2.setColor(Color.BLACK);
					g2.drawPolygon(xVals, yVals, 3);
					g2.setColor(Color.BLUE);
					g2.fillOval(x + 7 * fieldSizeDiv12, y + fieldSizeDiv12,
							4 * fieldSizeDiv12, 4 * fieldSizeDiv12);
					g2.setColor(Color.BLACK);
					g2.drawOval(x + 7 * fieldSizeDiv12, y + fieldSizeDiv12,
							4 * fieldSizeDiv12, 4 * fieldSizeDiv12);
					g2.setColor(Color.WHITE);
					if (number > 1) {
						g2.drawString(Integer.toString(number), x + 8
								* fieldSizeDiv12,
								y + fieldSizeDiv12 + myFM.getHeight());
					}
				}
			} else {
				Color pCol;
				if (this.owner == PlayerColor.RED) {
					pCol = Color.red;

				} else {
					pCol = Color.blue;
				}
				g2.setColor(pCol);
				int[] xVals = new int[3];
				int[] yVals = new int[3];
				xVals[0] = x;
				xVals[1] = x - 2 * fieldSizeDiv12;
				xVals[2] = x + 2 * fieldSizeDiv12;
				yVals[0] = y - fieldSizeDiv12;
				yVals[1] = y + 8 * fieldSizeDiv12;
				yVals[2] = y + 8 * fieldSizeDiv12;
				g2.fillPolygon(xVals, yVals, 3);
				g2.setColor(Color.BLACK);
				g2.drawPolygon(xVals, yVals, 3);
				g2.setColor(pCol);
				g2.fillOval(x - 2 * fieldSizeDiv12, y - 2 * fieldSizeDiv12,
						4 * fieldSizeDiv12, 4 * fieldSizeDiv12);
				g2.setColor(Color.BLACK);
				g2.drawOval(x + -2 * fieldSizeDiv12, y - 2 * fieldSizeDiv12,
						4 * fieldSizeDiv12, 4 * fieldSizeDiv12);
			}
		}
	}
}
