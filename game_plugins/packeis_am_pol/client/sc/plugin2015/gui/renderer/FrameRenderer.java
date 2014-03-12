/**
 * 
 */
package client.sc.plugin2015.gui.renderer;

import static client.sc.plugin2015.gui.renderer.RenderConfiguration.ANTIALIASING;
import static client.sc.plugin2015.gui.renderer.RenderConfiguration.BACKGROUND;
import static client.sc.plugin2015.gui.renderer.RenderConfiguration.OPTIONS;
import static client.sc.plugin2015.gui.renderer.RenderConfiguration.TRANSPARANCY;
import static client.sc.plugin2015.gui.renderer.RenderConfiguration.CRANES;
import static client.sc.plugin2015.gui.renderer.RenderConfiguration.MOVEMENT;
import static client.sc.plugin2015.gui.renderer.RenderConfiguration.DEBUG_VIEW;

import static shared.sc.plugin2015.util.Constants.CARDS_PER_PLAYER;
import static shared.sc.plugin2015.util.Constants.CITIES;
import static shared.sc.plugin2015.util.Constants.MAX_SEGMENT_SIZE;
import static shared.sc.plugin2015.util.Constants.SELECTION_SIZE;
import static shared.sc.plugin2015.util.Constants.SLOTS;

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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import shared.sc.plugin2015.BuildMove;
import shared.sc.plugin2015.Card;
import shared.sc.plugin2015.DebugHint;
import shared.sc.plugin2015.GameState;
import shared.sc.plugin2015.Move;
import shared.sc.plugin2015.MoveType;
import shared.sc.plugin2015.Player;
import shared.sc.plugin2015.PlayerColor;
import shared.sc.plugin2015.Segment;
import shared.sc.plugin2015.SelectMove;
import shared.sc.plugin2015.Tower;
import shared.sc.plugin2015.util.Constants;

/**
 * @author tkra, ffi
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JComponent {

	// konstanten
	private final static int BORDER_SIZE = 6;
	private static final int PROGRESS_ICON_SIZE = 60;
	private static final int PROGRESS_BAR_HEIGTH = 36;

	private static final int SIDE_BAR_WIDTH = 275;

	private static final int TOWER_LEFT_WIDTH = 25;
	private static final int TOWER_LEFT_HEIGTH = 10;
	private static final int TOWER_RIGHT_WIDTH = 15;
	private static final int TOWER_RIGHT_HEIGTH = 15;
	private static final int TOWER_TOTAL_WIDTH = TOWER_LEFT_WIDTH + TOWER_RIGHT_WIDTH;
	private static final int TOWER_STORIE_HEIGTH = 14;

	private static final int STUFF_GAP = 8;
	private static final int GAP_SIZE = 10;
	private static final int GAP_TOWER_TOP = TOWER_RIGHT_HEIGTH;
	private static final int GAP_TOWER_BOTTOM = TOWER_LEFT_HEIGTH;
	private static final int DIST_TOWER_SELECT = 15;

	private static final int DIAGONAL_GAP = 10;
	private static final int CITY_GAP = 42;

	private static final int CARD_DOT = 8;
	private static final int CARD_WIDTH = 8 + SLOTS * 6 - 6 + CARD_DOT;
	private static final int CARD_HEGTH = 8 + SLOTS * (CARD_DOT + 4) - 4;

	private static final int MAX_SEGMENT_HEIGTH = MAX_SEGMENT_SIZE * TOWER_STORIE_HEIGTH + TOWER_RIGHT_HEIGTH
			+ TOWER_LEFT_HEIGTH;

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

	private static final String SELECT_STRING = Constants.SELECTION_SIZE + " Bauelemente auswählen";
	private static final int MIN_DIALOG_SIZE = fmH2.stringWidth(SELECT_STRING) + 4 * GAP_SIZE;
	private static final int STATUS_HEIGTH = PROGRESS_BAR_HEIGTH + 2 * STUFF_GAP
			+ Math.max(CARD_HEGTH, MAX_SEGMENT_HEIGTH) + fmH3.getHeight();

	private static final Object LOCK = new Object();

	private static final int[] js = new int[] { 0, 0, 0, 2, 1, 0 };
	private static final String[] CITY_NAMES = new String[] { "Manhattan", "Frankfurt", "Hong Kong", "Dubai" };

	// current (game) state
	private PlayerColor currentPlayer;
	private Color currentPlayerColor;
	private GameState gameState;
	private MoveType currentMoveType;

	// image components
	private BufferedImage buffer;
	private boolean updateBuffer;
	private final Image bgImage;
	private Image scaledBgImage;
	private final Image progressIcon;

	// selektionsmenue
	private final List<TowerData> selectTowers;
	private int differentSegments;
	private int differentSegmentsAccN;
	private int mostSegments;
	private int selectHeight;
	private int selectWidth;
	private int selectX;
	private int selectY;
	private int[] selectButton;
	private boolean selectionOkay;
	private boolean selectionPressed;

	// tuerme zeichnen
	private final int highestSegments[] = new int[4];

	// selektion
	private final int[] selections;
	private int selectionSize;

	// spielersegmente
	private final List<TowerData> redSegments;
	private final List<TowerData> blueSegments;
	private List<TowerData> sensetiveSegments;
	private final List<TowerData> sensetiveTowers;
	private TowerData selectedSegment;
	private int dx, dy;
	private int ox, oy;

	// sonstiges
	private final TowerData[][] cityTowers;
	private TowerData selectedTower;
	private TowerData droppedSegment;
	private int turnToAnswer = -1;
	private boolean gameEnded;
	private int fontX;

	private final MouseAdapter selectMouseAdapter = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {

			int x = e.getX();
			int y = e.getY();
			requestFocusInWindow();

			if (selectionOkay) {

				if (x > selectButton[0] && x < selectButton[0] + selectButton[2]) {
					if (y > selectButton[1] && y < selectButton[1] + selectButton[3]) {
						selectionPressed = true;
						repaint();
					}
				}
			}

			if (!selectionPressed) {
				for (TowerData tower : selectTowers) {
					if (inner(x, y, tower.xs, tower.ys)) {
						if (tower.owner == null) {
							tower.owner = currentPlayer;
							selections[tower.size - 1]++;
							selectionSize++;
						} else {
							tower.owner = null;
							selections[tower.size - 1]--;
							selectionSize--;
						}
						selectionOkay = selectionSize == Constants.SELECTION_SIZE;
						repaint();
						break;
					}
				}
			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {

			if (selectionPressed) {

				int x = e.getX();
				int y = e.getY();
				selectionPressed = false;

				if (x > selectButton[0] && x < selectButton[0] + selectButton[2]) {
					if (y > selectButton[1] && y < selectButton[1] + selectButton[3]) {

						FrameRenderer.this.removeMouseListener(this);
						sendMove(new SelectMove(selections));

					}

				}
				repaint();
			}

		}

	};

	private final MouseAdapter buildMouseAdapter = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {

			requestFocusInWindow();
			if (e.getButton() == MouseEvent.BUTTON1) {
				int x = e.getX();
				int y = e.getY();

				for (TowerData tower : sensetiveSegments) {
					if (inner(x, y, tower.xs, tower.ys)) {
						selectedSegment = tower;
						ox = tower.x;
						oy = tower.y;
						dx = x - ox;
						dy = y - oy;

						for (BuildMove move : gameState.getPossibleMoves()) {
							if (move.size == tower.size) {
								TowerData data = cityTowers[move.city][move.slot];
								sensetiveTowers.add(data);
								data.highlited = true;
							}
						}

						updateBuffer = true;
						repaint();
						break;
					}
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {

			if (selectedSegment != null) {
				selectedSegment.moveTo(e.getX() - dx, e.getY() - dy);

				TowerData oldSelectedTower = selectedTower;
				selectedTower = null;
				for (TowerData tower : sensetiveTowers) {
					if (inner(selectedSegment.xs[1], selectedSegment.ys[1], tower.xs, tower.ys)) {
						if (tower.slot > 0) {
							TowerData clipper = cityTowers[tower.city][tower.slot - 1];
							if (!inner(selectedSegment.xs[1], selectedSegment.ys[1], clipper.xs, clipper.ys)) {
								selectedTower = tower;
								break;
							}
						} else {
							selectedTower = tower;
							break;
						}
					}
				}

				if (selectedTower != oldSelectedTower) {
					updateBuffer = true;
				}
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			if (e.getButton() == MouseEvent.BUTTON1) {
				if (selectedSegment != null) {
					sensetiveTowers.clear();
					for (int i = 0; i < CITIES; i++) {
						for (int j = 0; j < SLOTS; j++) {
							cityTowers[i][j].highlited = false;
						}
					}
					if (selectedTower != null) {
						TowerData data = selectedTower;
						droppedSegment = selectedSegment;
						selectedTower = null;
						selectedSegment = null;
						sendMove(new BuildMove(data.city, data.slot, droppedSegment.size));
					} else {
						selectedSegment.moveTo(ox, oy);
						selectedSegment = null;
					}
					updateBuffer = true;
					repaint();
				}
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

	private KeyListener keyListener = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {

			System.out.println("--------------1");
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				System.out.println("--------------2");

				new RenderConfigurationDialog(FrameRenderer.this);
				updateBuffer = true;
				repaint();

			}
		}
	};

	private class Point {
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	class TowerData {

		int[] xs;
		int[] ys;
		int innerX;
		int innerY;
		boolean highlited;
		PlayerColor owner = null;
		int size;
		int x, y;
		int slot;
		int city;
		int diff;

		public TowerData(int size) {
			highlited = false;
			this.size = size;
		}

		public TowerData(int size, int city, int slot) {
			highlited = false;
			this.size = size;
			this.city = city;
			this.slot = slot;
		}

		public void moveTo(int x, int y) {
			rebuild(x, y, size);
		}

		public void resize(int size) {
			rebuild(x, y, size);
		}

		public void rebuild(int x, int y, int size) {
			int heigth = size * TOWER_STORIE_HEIGTH + 1;
			xs = new int[6];
			ys = new int[6];
			this.size = size;
			this.x = x;
			this.y = y;

			xs[0] = x;
			xs[1] = x + TOWER_LEFT_WIDTH;
			xs[2] = x + TOWER_TOTAL_WIDTH;
			xs[3] = x + TOWER_TOTAL_WIDTH;
			xs[4] = x + TOWER_RIGHT_WIDTH;
			xs[5] = x;

			ys[0] = y - TOWER_LEFT_HEIGTH;
			ys[1] = y;
			ys[2] = y - TOWER_RIGHT_HEIGTH;
			ys[3] = y - TOWER_RIGHT_HEIGTH - heigth;
			ys[4] = y - TOWER_LEFT_HEIGTH - TOWER_RIGHT_HEIGTH - heigth;
			ys[5] = y - TOWER_LEFT_HEIGTH - heigth;

			innerX = x + TOWER_LEFT_WIDTH;
			innerY = y - heigth;

		}

	}

	public FrameRenderer() {

		updateBuffer = true;
		bgImage = loadImage("resource/game/manhattanbg.jpeg");
		progressIcon = loadImage("resource/game/kelle.png");
		selections = new int[Constants.MAX_SEGMENT_SIZE];
		selectTowers = new LinkedList<TowerData>();
		redSegments = new LinkedList<TowerData>();
		blueSegments = new LinkedList<TowerData>();
		sensetiveTowers = new LinkedList<TowerData>();
		cityTowers = new TowerData[CITIES][SLOTS];
		for (int i = 0; i < CITIES; i++) {
			for (int j = 0; j < SLOTS; j++) {
				cityTowers[i][j] = new TowerData(0, i, j);
			}
		}

		setMinimumSize(new Dimension(2 * CARDS_PER_PLAYER * (CARD_WIDTH + STUFF_GAP) + 2 * SELECTION_SIZE
				* (TOWER_TOTAL_WIDTH + STUFF_GAP), 600));

		setDoubleBuffered(true);
		addComponentListener(componentListener);
		addKeyListener(keyListener);
		setFocusable(true);
		requestFocusInWindow();

		RenderConfiguration.loadSettings();

		resizeBoard();
		repaint();

	}

	public void updateGameState(GameState gameState) {

		if (this.gameState != null && currentMoveType == MoveType.BUILD) {
			int turnDiff = gameState.getTurn() - this.gameState.getTurn();

			Move move = gameState.getLastMove();
			if (move != null && turnDiff == 1) {
				moveSegment(gameState);
			}
		}

		// aktuellen spielstand sichern
		this.gameState = gameState;
		this.currentMoveType = gameState.getCurrentMoveType();
		currentPlayer = gameState.getCurrentPlayer().getPlayerColor();
		currentPlayerColor = getPlayerColor(currentPlayer);
		updateBuffer = true;

		selectedSegment = null;
		droppedSegment = null;
		// removeMouseListener(buildMouseAdapter);
		// removeMouseListener(selectMouseAdapter);
		// removeMouseMotionListener(buildMouseAdapter);

		highestSegments[0] = gameState.getRedPlayer().getHighestCurrentSegment();
		highestSegments[1] = gameState.getRedPlayer().getHighestSegment();
		highestSegments[2] = gameState.getBluePlayer().getHighestCurrentSegment();
		highestSegments[3] = gameState.getBluePlayer().getHighestSegment();

		Comparator<Card> cardComp = new Comparator<Card>() {
			public int compare(Card o1, Card o2) {
				return o1.slot == o2.slot ? 0 : (o1.slot > o2.slot ? 1 : -1);
			}
		};

		Collections.sort(gameState.getRedPlayer().getCards(), cardComp);
		Collections.sort(gameState.getBluePlayer().getCards(), cardComp);

		gameEnded = gameState.gameEnded();

		if (currentMoveType == MoveType.SELECT) {
			// daten vorbereiten
			selectionOkay = false;
			selectionPressed = false;
			selectionSize = 0;
			for (int i = 0; i < Constants.MAX_SEGMENT_SIZE; i++) {
				selections[i] = 0;
			}
			createSelectDialog();
			// addMouseListener(selectMouseAdapter);
		} else {
			if (currentPlayer == PlayerColor.RED) {
				sensetiveSegments = redSegments;
			} else {
				sensetiveSegments = blueSegments;
			}
			// addMouseListener(buildMouseAdapter);
			// addMouseMotionListener(buildMouseAdapter);
		}

		if (gameState.gameEnded()) {
			gameEnded = true;
			currentPlayer = gameState.winner();
		}

		createPlayerSegments();
		updateCityTowers();
		repaint();

	}

	private synchronized void moveSegment(final GameState gameState) {

		final int FPS = 30;

		setEnabled(false);
		final BuildMove move = (BuildMove) gameState.getLastMove();
		final TowerData targetTower = cityTowers[move.city][move.slot];

		// TowerData selectedSegment = null;
		if (droppedSegment == null) {
			updateBuffer = true;
			for (int i = sensetiveSegments.size() - 1; i >= 0; i--) {
				if (sensetiveSegments.get(i).size == move.size) {
					selectedSegment = sensetiveSegments.get(i);
					break;
				}
			}
		} else {
			selectedSegment = droppedSegment;
		}

		final Point p = new Point(selectedSegment.x, selectedSegment.y);
		final Point q = new Point(targetTower.innerX - TOWER_LEFT_WIDTH, targetTower.innerY);

		if (OPTIONS[MOVEMENT]) {

			double pixelPerFrame = ((double) getWidth()) / (1.5 * FPS);
			double dist = Math.sqrt(Math.pow(p.x - q.x, 2) + Math.pow(p.y - q.y, 2));

			final int frames = (int) Math.ceil(dist / pixelPerFrame);
			final Point o = new Point(p.x, p.y);
			final Point dP = new Point(q.x - p.x, q.y - p.y);

			long start = System.currentTimeMillis();
			int h = selectedSegment.size * TOWER_STORIE_HEIGTH + TOWER_LEFT_HEIGTH + TOWER_RIGHT_HEIGTH + 10;
			for (int frame = 0; frame < frames; frame++) {

				int oldx = selectedSegment.xs[0] - 5;
				int oldy = selectedSegment.ys[4] - 5;

				p.x = o.x + (int) ((double) (frame * dP.x) / (double) frames);
				p.y = o.y + (int) ((double) (frame * dP.y) / (double) frames);
				selectedSegment.moveTo(p.x, p.y);

				// invalidate();
				// getParent().repaint();

				repaint(oldx, oldy, TOWER_TOTAL_WIDTH + 10, h);
				repaint(selectedSegment.xs[0] - 5, selectedSegment.ys[4] - 5, TOWER_TOTAL_WIDTH + 10, h);

				synchronized (LOCK) {
					LOCK.notify();
				}

				try {
					long duration = start + (frame + 1) * (1000 / FPS) - System.currentTimeMillis();
					Thread.sleep(duration > 0 ? duration : 0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

		/* zum schluss richtig positionieren */
		selectedSegment.moveTo(q.x, q.y);
		setEnabled(true);

	}

	public synchronized void requestMove(final int turn) {
		turnToAnswer = turn;

		if (currentMoveType == MoveType.SELECT) {
			addMouseListener(selectMouseAdapter);
		} else {
			addMouseListener(buildMouseAdapter);
			addMouseMotionListener(buildMouseAdapter);
		}
	}

	private boolean myTurn() {
		return turnToAnswer == gameState.getTurn();
	}

	private synchronized void sendMove(final Move move) {

		removeMouseListener(buildMouseAdapter);
		removeMouseListener(selectMouseAdapter);
		removeMouseMotionListener(buildMouseAdapter);

		if (myTurn() && !gameEnded) {
			RenderFacade.getInstance().sendMove(move);
			turnToAnswer = -1;
		}
	}

	private void updateCityTowers() {

		for (Tower tower : gameState.getTowers()) {
			TowerData data = cityTowers[tower.city][tower.slot];
			data.resize(tower.getHeight());
			data.owner = tower.getOwner();
			data.diff = Math.abs(tower.getRedParts() - tower.getBlueParts());
		}

	}

	private void recreateCityTowers() {

		int y = getHeight() - BORDER_SIZE - STATUS_HEIGTH - 25;
		int cityWidth = TOWER_LEFT_WIDTH + SLOTS * (TOWER_RIGHT_WIDTH + DIAGONAL_GAP) - DIAGONAL_GAP;
		int citiesWidth = CITIES * (cityWidth + CITY_GAP) - CITY_GAP;
		int x = (getWidth() - SIDE_BAR_WIDTH - citiesWidth) / 2;

		for (int i = 0; i < CITIES; i++) {
			int y2 = y;
			for (int j = 0; j < SLOTS; j++) {
				cityTowers[i][j].moveTo(x, y2);
				x += TOWER_RIGHT_WIDTH + DIAGONAL_GAP;
				y2 -= TOWER_RIGHT_HEIGTH + DIAGONAL_GAP;
			}
			x += TOWER_LEFT_WIDTH + CITY_GAP - DIAGONAL_GAP;
		}

	}

	private void resizeBoard() {

		int width = getWidth() - 2 * BORDER_SIZE;
		int heigth = getHeight() - 2 * BORDER_SIZE - PROGRESS_BAR_HEIGTH;

		fontX = getWidth() - SIDE_BAR_WIDTH;

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

		recreateCityTowers();
		recreatePlayerSegments();
		if (currentMoveType == MoveType.SELECT) {
			recreateSelectDialog();
		}

		System.gc();
		updateBuffer = true;
		repaint();
	}

	private void createSelectDialog() {

		synchronized (selectTowers) {

			selectTowers.clear();
			differentSegments = 0;
			differentSegmentsAccN = 0;
			mostSegments = 0;
			Player player = gameState.getCurrentPlayer();
			List<Segment> segments = player.getSegments();
			for (Segment segment : segments) {
				int retained = segment.getRetained();

				// hilfsdaten fuer menue berechnen
				if (retained > 0) {
					differentSegments++;
					differentSegmentsAccN += segment.size;
					if (retained > mostSegments) {
						mostSegments = retained;
					}
				}

				// tuerme fuer selektionsmenue einmalig erstellen
				selectionOkay = player.getRetainedSegmentCount() == Constants.SELECTION_SIZE;
				for (int i = 0; i < retained; i++) {
					TowerData data = new TowerData(segment.size);
					// bei letztem menue vorselektieren
					if (selectionOkay) {
						data.owner = currentPlayer;
						selections[segment.size - 1]++;
						selectionSize++;
					}
					selectTowers.add(data);
				}
			}

			recreateSelectDialog();
		}
	}

	private void recreateSelectDialog() {

		synchronized (selectTowers) {

			// dialoggroesse bestimmen
			selectHeight = 2 * GAP_SIZE + 2 * fmH2.getHeight();
			selectHeight += differentSegments * (GAP_TOWER_TOP + GAP_TOWER_BOTTOM + 2 * GAP_SIZE);
			selectHeight += differentSegmentsAccN * TOWER_STORIE_HEIGTH;
			selectWidth = Math.max(MIN_DIALOG_SIZE, mostSegments * (DIST_TOWER_SELECT + TOWER_TOTAL_WIDTH));

			selectX = (getWidth() - SIDE_BAR_WIDTH - selectWidth) / 2;
			selectY = (getHeight() - STATUS_HEIGTH - selectHeight) / 2;

			// basispunkte bestimmen
			int x = 0;
			int y = selectY + fmH2.getHeight();
			List<Segment> segments = gameState.getCurrentPlayer().getSegments();
			int listOffset = 0;
			for (Segment segment : segments) {
				int retained = segment.getRetained();
				if (retained > 0) {
					y += 2 * GAP_SIZE + GAP_TOWER_TOP + GAP_TOWER_BOTTOM + segment.size * TOWER_STORIE_HEIGTH;
					x = (getWidth() - SIDE_BAR_WIDTH - retained * (TOWER_TOTAL_WIDTH + DIST_TOWER_SELECT) + DIST_TOWER_SELECT) / 2;
					for (int i = listOffset; i < listOffset + retained; i++) {
						selectTowers.get(i).moveTo(x, y);
						x += TOWER_TOTAL_WIDTH + DIST_TOWER_SELECT;
					}
					listOffset += retained;
				}
			}

			selectButton = new int[] { (getWidth() - SIDE_BAR_WIDTH - 200) / 2,
					selectY + selectHeight - GAP_SIZE - fmH2.getHeight(), 200, fmH2.getHeight() };

		}

	}

	private void createPlayerSegments() {

		// spieler rot
		Player player = gameState.getRedPlayer();
		synchronized (redSegments) {
			redSegments.clear();
			for (int i = MAX_SEGMENT_SIZE; i > 0; i--) {
				int usable = player.getSegment(i).getUsable();
				for (int j = 0; j < usable; j++) {
					TowerData tower = new TowerData(i);
					tower.owner = PlayerColor.RED;
					redSegments.add(tower);
				}
			}
		}

		// spieler blau
		player = gameState.getBluePlayer();
		synchronized (blueSegments) {
			blueSegments.clear();
			for (int i = MAX_SEGMENT_SIZE; i > 0; i--) {
				int usable = player.getSegment(i).getUsable();
				for (int j = 0; j < usable; j++) {
					TowerData tower = new TowerData(i);
					tower.owner = PlayerColor.BLUE;
					blueSegments.add(tower);
				}
			}
		}

		recreatePlayerSegments();

	}

	private void recreatePlayerSegments() {

		// spieler rot
		int x = BORDER_SIZE + STUFF_GAP + CARDS_PER_PLAYER * (CARD_WIDTH + STUFF_GAP);
		int y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP;

		synchronized (redSegments) {
			for (TowerData tower : redSegments) {
				if (tower != selectedSegment) {
					tower.moveTo(x, y);
				}
				x += TOWER_TOTAL_WIDTH + STUFF_GAP;
			}
		}

		// spieler blau
		x = getWidth() - BORDER_SIZE - STUFF_GAP - CARDS_PER_PLAYER * (CARD_WIDTH + STUFF_GAP)
				- TOWER_TOTAL_WIDTH;
		y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP;

		synchronized (blueSegments) {
			for (TowerData tower : blueSegments) {
				if (tower != selectedSegment) {
					tower.moveTo(x, y);
				}
				x -= TOWER_TOTAL_WIDTH + STUFF_GAP;
			}
		}

	}

	private int getHighestSegment(PlayerColor color, boolean overall) {
		int ptr = color == PlayerColor.RED ? 0 : 2;
		ptr += overall ? 1 : 0;
		return highestSegments[ptr];
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		if (updateBuffer) {
			fillBuffer();
		}

		g2.drawImage(buffer, 0, 0, getWidth(), getHeight(), this);

		if (gameState != null) {
			paintDynamicComponents(g2);
		}

		if (myTurn() && currentMoveType == MoveType.SELECT) {
			paintSelectDialog(g2);
		}

		if (gameEnded) {
			paintEndMessage(g2);
		}

		// bmFrames++;
		// // repainted = true;
		synchronized (LOCK) {
			LOCK.notify();
		}

	}

	private void fillBuffer() {

		int imageType = OPTIONS[TRANSPARANCY] ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_BGR;
		buffer = new BufferedImage(getWidth(), getHeight(), imageType);
		Graphics2D g2 = (Graphics2D) buffer.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		paintStaticComponents(g2);
		if (gameState != null) {
			// printGameStatus(g2);
			paintSemiStaticComponents(g2);
		}

		updateBuffer = false;
	}

	private void paintStaticComponents(Graphics2D g2) {

		// hintergrundbild oder farbe
		if (OPTIONS[BACKGROUND] && scaledBgImage != null) {
			g2.drawImage(scaledBgImage, BORDER_SIZE, BORDER_SIZE, getWidth() - 2 * BORDER_SIZE, getHeight() - 2
					* BORDER_SIZE, this);
		} else {
			g2.setColor(new Color(186, 217, 246));
			g2.fillRect(BORDER_SIZE, BORDER_SIZE, getWidth() - 2 * BORDER_SIZE, getHeight() - 2 * BORDER_SIZE);
		}

		// fortschrittsleite, spielerinfo und seitenleiste
		g2.setColor(getTransparentColor(new Color(255, 255, 255), 192));

		// fortschrittsleite, spielerinfo hintergrund
		int heigth = PROGRESS_BAR_HEIGTH + 2 * STUFF_GAP + CARD_HEGTH + fmH3.getHeight();
		g2.fillRect(BORDER_SIZE, getHeight() - BORDER_SIZE - heigth, getWidth() - 2 * BORDER_SIZE, heigth);

		// seitenleiste, hintergrund
		g2.fillRect(getWidth() - BORDER_SIZE - SIDE_BAR_WIDTH, BORDER_SIZE, SIDE_BAR_WIDTH, getHeight() - 2
				* BORDER_SIZE - heigth);

		// seitenleiste, info
		boolean hints = OPTIONS[DEBUG_VIEW];
		int fontY = paintPlayerInfo(g2, 2 * BORDER_SIZE + 10, PlayerColor.RED, hints);
		fontY += hints ? 10 : 30;
		fontY = 25 + paintPlayerInfo(g2, fontY, PlayerColor.BLUE, hints);

		if (hints && gameState.getLastMove() != null) {
			g2.setColor(Color.BLACK);
			g2.setFont(h5);
			int fontX = getWidth() - SIDE_BAR_WIDTH;

			for (DebugHint hint : gameState.getLastMove().getHints()) {
				g2.drawString(hint.content, fontX, fontY);
				fontY += 20;
			}
		}
	}

	private int paintPlayerInfo(Graphics2D g2, int fontY, PlayerColor player, boolean small) {

		int[] stats = gameState.getPlayerStats(player);
		int p = Constants.POINTS_PER_TOWER * stats[0] + Constants.POINTS_PER_OWEND_CITY * stats[1]
				+ Constants.POINTS_PER_HIGHEST_TOWER * stats[2];

		g2.setColor(getTransparentColor(getPlayerColor(player), 174));
		String s = Integer.toString(stats[3]);
		if (small) {
			fontY += 20;
			g2.setFont(h3);
			g2.drawString(s + " + " + p, fontX, fontY);
		} else {
			fontY += 60;
			g2.setFont(h0);
			g2.drawString(s, fontX, fontY);
			if (!(gameEnded && gameState.getRound() == Constants.ROUND_LIMIT)) {
				g2.setFont(h1);
				g2.drawString("+" + p, fontX + fmH0.stringWidth(s), fontY);
			}
		}

		g2.setColor(Color.BLACK);

		fontY += 20;
		g2.setFont(h5);
		p = Constants.POINTS_PER_TOWER * stats[0];
		s = stats[0] == 1 ? "1 Turm im Spiel" : stats[0] + " Türme im Spiel";
		s += " (" + p + " Punkte)";
		g2.drawString(s, fontX, fontY);

		fontY += 20;
		p = Constants.POINTS_PER_OWEND_CITY * stats[1];
		s = stats[1] == 1 ? "1 Stadt im Besitz" : stats[1] + " Städte im Besitz";
		s += " (" + p + " Punkte)";
		g2.drawString(s, fontX, fontY);

		fontY += 20;
		if (stats[2] == 1) {
			p = Constants.POINTS_PER_HIGHEST_TOWER * stats[2];
			s = "Höchsten Turm im Besitz";
			s += " (" + p + " Punkte)";
			g2.drawString(s, fontX, fontY);
		}

		return fontY;
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
		g2.fillRoundRect(xCenter - w / 2 - 20, getHeight() / 2 - msgH - 5 - 100, w + 40, h + 15, 20, 20);

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

	private void paintSemiStaticComponents(Graphics2D g2) {

		// fortschrittsbalken
		g2.setColor(Color.BLACK);
		g2.setFont(h4);
		int left = fmH4.stringWidth("Spielfortschritt:") + BORDER_SIZE + 30;
		int right = getWidth() - left - 30;
		int fontY = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH / 2 + fmH4.getHeight() / 2 - 4;
		g2.drawString("Spielfortschritt:", BORDER_SIZE + 10, fontY);

		int round = gameState.getRound() + 1;
		String roundString = Integer.toString(gameState.getRound() + 1);
		if (round > Constants.ROUND_LIMIT) {
			roundString = Integer.toString(Constants.ROUND_LIMIT);
		}

		g2.drawString("Runde " + roundString + " von " + Constants.ROUND_LIMIT, right + 30, fontY);

		int progress = (gameState.getTurn() * (right - left)) / (2 * Constants.ROUND_LIMIT);
		int progressTop = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH + 8;

		g2.setColor(Color.GRAY);
		g2.fillRoundRect(left, progressTop, right - left, PROGRESS_BAR_HEIGTH - 16, 10, 10);

		g2.setColor(Color.GREEN);
		g2.fillRoundRect(left, progressTop, progress, PROGRESS_BAR_HEIGTH - 16, 10, 10);

		g2.setColor(Color.DARK_GRAY);
		g2.drawRoundRect(left, progressTop, right - left, PROGRESS_BAR_HEIGTH - 16, 10, 10);

		int sectionWidth = (right - left) / 4;
		g2.setStroke(stroke15);
		progressTop += 2;
		g2.drawLine(left + sectionWidth, progressTop, left + sectionWidth, progressTop + 16);
		g2.drawLine(left + 2 * sectionWidth, progressTop, left + 2 * sectionWidth, progressTop + 16);
		g2.drawLine(left + 3 * sectionWidth, progressTop, left + 3 * sectionWidth, progressTop + 16);

		// rahmen
		g2.setColor(currentPlayerColor);
		g2.fillRect(0, 0, getWidth(), BORDER_SIZE);
		g2.fillRect(0, getHeight() - BORDER_SIZE, getWidth(), BORDER_SIZE);
		g2.fillRect(0, 0, BORDER_SIZE, getHeight());
		g2.fillRect(getWidth() - BORDER_SIZE, 0, BORDER_SIZE, getHeight());

		// spielerinfo rot
		Player player = gameState.getRedPlayer();
		int x = BORDER_SIZE + STUFF_GAP;
		int y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP - CARD_HEGTH;

		for (Card card : player.getCards()) {
			paintCard(g2, x, y, card.slot);
			x += CARD_WIDTH + STUFF_GAP;
		}

		synchronized (redSegments) {
			for (TowerData tower : redSegments) {
				if (tower != selectedSegment) {
					paintTower(g2, tower);
				}
			}
		}

		g2.setFont(h3);
		y -= (STUFF_GAP + 5);
		g2.setColor(getPlayerColor(PlayerColor.RED));
		g2.drawString(player.getDisplayName(), 2 * BORDER_SIZE, y);

		// spielerinfo blau
		player = gameState.getBluePlayer();
		x = getWidth() - BORDER_SIZE - STUFF_GAP - CARD_WIDTH;
		y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP - CARD_HEGTH;

		for (Card card : player.getCards()) {
			paintCard(g2, x, y, card.slot);
			x -= CARD_WIDTH + STUFF_GAP;
		}

		synchronized (blueSegments) {
			for (TowerData tower : blueSegments) {
				if (tower != selectedSegment) {
					paintTower(g2, tower);
				}
			}
		}

		g2.setFont(h3);
		y -= (STUFF_GAP + 5);
		g2.setColor(getPlayerColor(PlayerColor.BLUE));
		int nameWidth = fmH3.stringWidth(player.getDisplayName());
		g2.drawString(player.getDisplayName(), getWidth() - 2 * BORDER_SIZE - nameWidth, y);

		// staedte
		g2.setFont(h5);
		for (int i = CITIES - 1; i >= 0; i--) {
			for (int j = SLOTS - 1; j >= 0; j--) {
				TowerData data = cityTowers[i][j];
				paintTower(g2, data, true, data == selectedTower);

				if (data.size > 0) {
					g2.setColor(Color.DARK_GRAY.darker());
					g2.fillRoundRect(data.xs[1], data.ys[1] - fmH5.getHeight() - 6, TOWER_RIGHT_WIDTH, fmH5
							.getHeight(), 8, 8);
					g2.setColor(data.diff > MAX_SEGMENT_SIZE ? Color.YELLOW : Color.WHITE);
					String s = Integer.toString(data.diff);
					g2.drawString(s, data.xs[1] + (TOWER_RIGHT_WIDTH - fmH5.stringWidth(s)) / 2, data.ys[1] - 8);
				}
			}
		}

		for (int i = 0; i < Constants.CITIES; i++) {
			// stadtnamen
			g2.setFont(h2);
			g2.setColor(Color.GREEN);

			TowerData base = cityTowers[i][0];
			int ownership = 0;
			for (int j = 0; j < Constants.SLOTS; j++) {
				if (cityTowers[i][j].owner == PlayerColor.RED) {
					ownership++;
				} else if (cityTowers[i][j].owner == PlayerColor.BLUE) {
					ownership--;
				}
			}
			PlayerColor owner = ownership > 0 ? PlayerColor.RED : (ownership < 0 ? PlayerColor.BLUE : null);
			g2.setColor(getPlayerColor(owner, true));

			g2.translate(base.xs[2] + 20, base.ys[1] + 10);
			g2.rotate(-0.785);
			g2.shear(0.42, 0);

			g2.drawString(CITY_NAMES[i], 0, 0);
			g2.setTransform(new AffineTransform());
		}

		// fortschirttsbalken, icon
		g2.drawImage(progressIcon, left + progress - PROGRESS_ICON_SIZE / 2 + 3, getHeight()
				- PROGRESS_ICON_SIZE - 3, PROGRESS_ICON_SIZE, PROGRESS_ICON_SIZE, this);

	}

	private void paintDynamicComponents(Graphics2D g2) {

		if (selectedSegment != null) {
			paintTower(g2, selectedSegment);
		}

	}

	private void paintSelectDialog(Graphics2D g2) {

		g2.setColor(getTransparentColor(new Color(255, 255, 255), 192));
		g2.fillRoundRect(selectX, selectY, selectWidth, selectHeight, 25, 25);

		String msg = SELECT_STRING;
		int msgW = fmH2.stringWidth(msg);
		int msgH = fmH2.getHeight();

		// menueueberschrift
		g2.setFont(h2);
		g2.setColor(currentPlayerColor);
		g2.drawString(msg, (getWidth() - SIDE_BAR_WIDTH - msgW) / 2, selectY + GAP_SIZE + msgH - 5);

		// tuerme zeichnen
		synchronized (selectTowers) {
			for (TowerData tower : selectTowers) {
				paintTower(g2, tower);
			}
		}

		if (selectionOkay) {

			// button
			String okay = "Abschicken";
			int okayW = fmH4.stringWidth(okay);

			g2.setColor(selectionPressed ? new Color(50, 130, 50) : new Color(40, 140, 40));
			g2.fillRoundRect(selectButton[0], selectButton[1], selectButton[2], selectButton[3], 15, 15);

			g2.setFont(h4);
			g2.setColor(Color.DARK_GRAY);
			g2.drawString(okay, (getWidth() - SIDE_BAR_WIDTH - okayW) / 2, selectY + selectHeight - GAP_SIZE
					- 10);

			g2.setStroke(stroke15);
			g2.setColor(Color.DARK_GRAY);
			g2.drawRoundRect(selectButton[0], selectButton[1], selectButton[2], selectButton[3], 15, 15);

		} else {

			String remark;
			if (selectionSize > Constants.SELECTION_SIZE) {
				int dif = selectionSize - Constants.SELECTION_SIZE;
				if (dif == 1) {
					remark = "Es muss noch 1 Bauelement abgewählt werden.";
				} else {
					remark = "Es müssen noch " + dif + " Bauelemente abgewählt werden.";
				}
			} else {
				int dif = Constants.SELECTION_SIZE - selectionSize;
				if (dif == 1) {
					remark = "Es muss noch 1 Bauelement ausgewählt werden.";
				} else {
					remark = "Es müssen noch " + dif + " Bauelemente ausgewählt werden.";
				}
			}

			g2.setFont(h4);
			int remarkW = fmH4.stringWidth(remark);
			g2.setColor(currentPlayerColor);
			g2.drawString(remark, (getWidth() - SIDE_BAR_WIDTH - remarkW) / 2, selectY + selectHeight - GAP_SIZE
					- 10);

		}

	}

	private void paintTower(Graphics2D g2, TowerData tower) {

		paintTower(g2, tower, false, false);

	}

	private void paintTower(Graphics2D g2, TowerData tower, boolean cityTower, boolean glow) {

		boolean drawCrane = OPTIONS[CRANES] && cityTower && tower.size > 0 && !gameEnded;
		drawCrane = drawCrane && tower.diff <= getHighestSegment(tower.owner.opponent(), true);
		int frameDisplacement = tower.diff * TOWER_STORIE_HEIGTH;
		if (drawCrane) {
			int craneX = (tower.xs[4] + tower.xs[5]) / 2;
			int craneY = (tower.ys[4] + tower.ys[5]) / 2;
			int craneJibLeftX = craneX - 10;
			int craneJibLeftY = craneY - 4 - frameDisplacement - 30;
			int craneJibRightX = (tower.xs[3] + tower.innerX) / 2 + 10;
			int craneJibRightY = (tower.ys[3] + tower.innerY) / 2 + 4 - frameDisplacement - 30;
			Color color = getPlayerColor(tower.owner.opponent(), true);
			boolean canMatchHeigth = tower.diff <= getHighestSegment(tower.owner.opponent(), false);
			g2.setColor(canMatchHeigth ? color.darker() : grayer(color));
			g2.setStroke(stroke40);
			g2.drawLine(craneJibLeftX, craneJibLeftY, craneJibRightX, craneJibRightY);
			g2.drawLine(craneX, craneY, craneX, craneY - frameDisplacement - 40);
		}

		g2.setColor(glow ? currentPlayerColor : getBrightPlayerColor(tower.owner, cityTower));
		g2.fillPolygon(tower.xs, tower.ys, 6);

		if (!glow) {
			int oppSize = (tower.size - tower.diff) / 2;
			if (tower.size > 1 && oppSize > 0 && cityTower) {
				int[] otherXs = new int[6];
				int[] otherYs = new int[6];
				int opponentHeigth = oppSize * TOWER_STORIE_HEIGTH;
				for (int i = 0; i < 6; i++) {
					otherXs[i] = tower.xs[i];
					otherYs[i] = tower.ys[i];
					if (i > 2) {
						otherYs[i] = tower.ys[js[i]] - opponentHeigth;
						otherXs[i] = tower.xs[js[i]];
					}
				}
				g2.setColor(getBrightPlayerColor(tower.owner.opponent(), true));
				g2.fillPolygon(otherXs, otherYs, 6);
			}
		}

		g2.setStroke(stroke20);
		g2.setColor(tower.highlited ? currentPlayerColor : Color.DARK_GRAY);
		g2.drawPolygon(tower.xs, tower.ys, 6);

		g2.setStroke(stroke10);
		int diff = TOWER_STORIE_HEIGTH;
		for (int i = 1; i < tower.size; i++) {
			g2.drawLine(tower.xs[0], tower.ys[0] - diff, tower.xs[1], tower.ys[1] - diff);
			g2.drawLine(tower.xs[1], tower.ys[1] - diff, tower.xs[2], tower.ys[2] - diff);
			diff += TOWER_STORIE_HEIGTH;
		}

		g2.setStroke(stroke20);
		g2.drawLine(tower.innerX, tower.innerY, tower.xs[1], tower.ys[1]);
		g2.drawLine(tower.innerX, tower.innerY, tower.xs[3], tower.ys[3]);
		g2.drawLine(tower.innerX, tower.innerY, tower.xs[5], tower.ys[5]);

		Stroke prevStroke = g2.getStroke();
		if (drawCrane) {
			int[] frameXs = new int[] { tower.xs[3], tower.xs[4], tower.xs[5], tower.innerX };
			int[] frameYs = new int[] { tower.ys[3], tower.ys[4], tower.ys[5], tower.innerY };
			for (int i = 0; i < 4; i++) {
				frameYs[i] -= frameDisplacement;
			}
			g2.setColor(new Color(128, 128, 128, 128));
			g2.fillPolygon(frameXs, frameYs, 4);
			g2.setColor(Color.YELLOW.darker());
			g2.setStroke(stroke30);
			g2.drawPolygon(frameXs, frameYs, 4);
			g2.setColor(Color.DARK_GRAY);
			g2.setStroke(stroke15);
			for (int i = 0; i < 4; i++) {
				g2.drawLine((tower.xs[0] + tower.xs[2]) / 2, frameYs[1] - 15, frameXs[i], frameYs[i]);

			}
		}
		g2.setStroke(prevStroke);

	}

	private void paintCard(Graphics2D g2, int x, int y, int slot) {

		g2.setStroke(stroke15);
		g2.setColor(Color.WHITE);
		g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEGTH, 10, 10);

		g2.setColor(Color.DARK_GRAY);
		g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEGTH, 10, 10);

		g2.fillRect(x + 4 + slot * 6, y + CARD_HEGTH - 12 - slot * 12, 8, 8);
		for (int i = 0; i < SLOTS; i++) {
			g2.drawRect(x + 4 + i * 6, y + CARD_HEGTH - 12 - i * 12, 8, 8);
		}

		g2.setFont(h4);
		String s = Integer.toString(slot + 1);
		int sW = fmH4.stringWidth(s);
		g2.drawString(s, x + CARD_WIDTH - 4 - sW, y + CARD_HEGTH - 4);
		g2.drawString(s, x + 4, y + fmH4.getHeight() - 2);

	}

	private boolean inner(int x, int y, int[] xs, int[] ys) {

		boolean inner = true;
		double scalar;
		double ref = 0;
		int n = xs.length;

		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			scalar = (ys[j] - ys[i]) * (x - xs[i]) + (xs[i] - xs[j]) * (y - ys[i]);

			if (i == 0) {
				ref = Math.signum(scalar);
			}

			if (Math.signum(scalar) != ref) {
				inner = false;
				break;
			}
		}

		return inner;

	}

	private Color getTransparentColor(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), OPTIONS[TRANSPARANCY] ? alpha : 255);
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

	private Color getBrightPlayerColor(PlayerColor player, boolean forced) {
		Color color;

		if (player == null || (player != currentPlayer && !forced)) {
			return Color.GRAY;
		}

		switch (player) {
		case RED:
			color = new Color(255, 60, 60);
			break;
		case BLUE:
			color = new Color(80, 80, 255);
			break;

		default:
			color = Color.GRAY;
		}

		return color;
	}

	public Color grayer(Color color) {

		double FACTOR = 0.3;
		double ROTCAF = 1.0 - FACTOR;

		int r = color.getRed();
		if (r > 128) {
			r = 128 + (int) ((r - 128) * FACTOR);
		} else {
			r = r + (int) ((128 - r) * ROTCAF);
		}

		int g = color.getGreen();
		if (g > 128) {
			g = 128 + (int) ((g - 128) * FACTOR);
		} else {
			g = g + (int) ((128 - g) * ROTCAF);
		}

		int b = color.getBlue();
		if (b > 128) {
			b = 128 + (int) ((b - 128) * FACTOR);
		} else {
			b = b + (int) ((128 - b) * ROTCAF);
		}

		return new Color(r, g, b, color.getAlpha());

	}

	public Image getImage() {
		BufferedImage img;
		img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
	}

	private static Image loadImage(String filename) {
		URL url = FrameRenderer.class.getClassLoader().getResource(filename);

		if (url == null) {
			return null;
		}
		return (new ImageIcon(url)).getImage();
	}

}
