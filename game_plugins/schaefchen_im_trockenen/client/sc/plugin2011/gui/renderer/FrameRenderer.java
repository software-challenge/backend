/**
 * 
 */
package sc.plugin2011.gui.renderer;

import static sc.plugin2011.gui.renderer.RenderConfiguration.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import sc.plugin2011.BoardFactory;
import sc.plugin2011.DebugHint;
import sc.plugin2011.Die;
import sc.plugin2011.DogState;
import sc.plugin2011.Flower;
import sc.plugin2011.GUINode;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.NodeType;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.Sheep;
import sc.plugin2011.util.Constants;

/**
 * @author tkra, ffi
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JPanel {

	private static final Color PLAYER2_COLOR = Color.BLUE;
	private static final Color PLAYER1_COLOR = Color.RED;

	private static final Font h0 = new Font("Helvetica", Font.BOLD, 73);
	private static final Font h1 = new Font("Helvetica", Font.BOLD, 27);
	private static final Font h2 = new Font("Helvetica", Font.BOLD, 20);
	private static final Font h3 = new Font("Helvetica", Font.BOLD, 14);
	private static final Font h4 = new Font("Helvetica", Font.PLAIN, 14);
	private static final Font hSheep = new Font("Helvetica", Font.PLAIN, 10);
	private final FontMetrics fmH0 = getFontMetrics(h0);
	private final FontMetrics fmH1 = getFontMetrics(h1);
	// private final FontMetrics fmH2 = getFontMetrics(h2);
	private final FontMetrics fmH3 = getFontMetrics(h3);
	private final FontMetrics fmH4 = getFontMetrics(h4);
	private final FontMetrics fmSheep = getFontMetrics(hSheep);

	private final static int BORDER_SIZE = 5;
	private static final String TITLE = "Schäfchen im Trockenen";
	private static final int ICON_SIZE = 34;
	private static final int SHEEP_SIZE = 44;
	private static final int PROGRESS_SHEEP_SIZE = 60;
	private static final int DOG_SIZE = 50;
	private static final int PROGRESS_BAR_HEIGTH = 36;
	private final int STATS_WIDTH = getFontMetrics(h1).stringWidth(TITLE) + 4
			* BORDER_SIZE;

	// local instances of current players and gameState
	private PlayerColor currentPlayer;
	private GameState gameState;

	private boolean gameEnded;

	private GUINode[] guiNodes;

	private boolean highliteNode;
	private int currentNode;
	private int mouseY;
	private int mouseX;
	private int dropedX;
	private int dropedY;

	private Set<Integer> currentNeighbours;
	private boolean dropedSheep;
	private boolean draggedSheep;
	private int currentSheep;

	private final Image bgBoard;
	private Image scaledBgBoard;

	private final Image sheepIcon;
	private final Image progessSheepIcon;
	private final Image dogIcon;
	private final Image mushroomIcon;
	private final Image flower1Icon;
	private final Image flower2Icon;

	private Map<Integer, Point> sheepMap;
	private int size;

	private int xBorder;
	private int yBorder;

	private boolean config;

	private class Point {
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private ComponentListener componentListener = new ComponentAdapter() {

		@Override
		public void componentResized(ComponentEvent e) {
			resizeBoard();
			repaint();
		}

	};

	private MouseAdapter dragMouseAdapter = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {

			mouseX = e.getX();
			mouseY = e.getY();
			requestFocusInWindow();
			for (Integer sheepIndex : sheepMap.keySet()) {
				Sheep sheep = gameState.getSheep(sheepIndex);
				Point p = sheepMap.get(sheepIndex);
				if (Math.sqrt(Math.pow(p.x - mouseX, 2)
						+ Math.pow(p.y - mouseY, 2)) < 20
						&& sheep.owner != null) {

					draggedSheep = true;
					currentSheep = sheep.index;
					highliteSheep = true;
					currentNeighbours = gameState
							.getValidReacheableNodes(currentSheep);

					updateBuffer = true;
					repaint();
					break;
				}

			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {

			mouseX = 0;
			mouseY = 0;
			if (highliteSheep && highliteNode && !gameEnded) {
				Sheep sheep = gameState.getSheep(currentSheep);
				if (sheep.owner != null && sheep.owner == currentPlayer
						&& currentNeighbours.contains(currentNode)) {

					// wenn zug abgeschickt werden soll aktuelles schaf
					// nicht zurueckfallen lassen
					if (myTurn()) {
						Point p = sheepMap.get(currentSheep);
						p.x = e.getX();
						p.y = e.getY();
						dropedX = e.getX();
						dropedY = e.getY();
						dropedSheep = true;
						sendMove(new Move(currentSheep, currentNode));
					}
				}
			}

			draggedSheep = false;
			highliteSheep = false;
			highliteNode = false;
			currentNeighbours = null;
			updateBuffer = true;
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {

			mouseX = e.getX();
			mouseY = e.getY();
			highliteNode = false;
			if (highliteSheep) {

				Sheep sheep = gameState.getSheep(currentSheep);
				if (sheep.owner == currentPlayer) {
					for (Integer node : currentNeighbours) {
						if (guiNodes[node].inner(e.getX(), e.getY())) {
							currentNode = node;
							highliteNode = true;
							break;
						}
					}
				}
				repaint();
			}

		}

	};

	private final Object LOCK = new Object();
	private boolean bmShow = false;
	private int bmFrames;
	private int bmFrameRate;
	private long bmLastSecond;
	private double bmPhi = 0;
	private final Runnable bmThread = new Runnable() {

		@Override
		public void run() {
			bmShow = false;
			bmFrames = 0;
			while (OPTIONS[BENCHMARK]) {

				long now = System.currentTimeMillis();
				if (now >= bmLastSecond + 1000) {
					bmFrameRate = bmFrames;
					bmFrames = 0;
					bmLastSecond = now;
					bmShow = true;
				}

				bmPhi += (2 * Math.PI) / size;
				if (bmPhi >= 2 * Math.PI) {
					bmPhi = 0;
				}

				try {
					FrameRenderer.this.validate();
					FrameRenderer.this.repaint();
					synchronized (LOCK) {
						LOCK.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private MouseAdapter configMouseAdapter = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {

			int x = e.getX();
			int y = e.getY();
			requestFocusInWindow();
			if (x >= configX && x <= configX + 20 && y >= configY
					&& y <= configY + 25 * OPTIONS.length) {
				y -= configY;
				if (y % 25 <= 20) {
					int i = y / 25;
					OPTIONS[i] = !OPTIONS[i];
					if (i == CURVED_SHAPES) {
						GUINode.setSimple(!OPTIONS[CURVED_SHAPES]);
					} else if (i == BENCHMARK) {

						bmFrameRate = 0;
						bmPhi = 0;
						bmLastSecond = System.currentTimeMillis();
						Thread t = new Thread(bmThread);
						t.start();
					} else if (i == PROGRESS_BAR) {
						resizeBoard();
					}
					RenderConfiguration.saveSettings();
					updateBuffer = true;
					repaint();
				}
			}

		}

	};
	private KeyListener keyListener = new KeyAdapter() {

		@Override
		public void keyTyped(KeyEvent e) {

			if (e.getKeyChar() == KeyEvent.VK_SPACE) {
				config = !config;
				valideView();
			}

		}

	};
	private int configY;
	private int configX;
	private BufferedImage buffer;
	private boolean updateBuffer;
	private int fontY;
	private int fontX;
	private boolean highliteSheep;
	private int turnToAnswer;

	// private boolean repainted;

	public FrameRenderer() {
		bgBoard = loadImage("resource/game/boden_wiese3.png");

		sheepIcon = loadImage("resource/game/sheep.png");
		progessSheepIcon = loadImage("resource/game/medsheep.png");
		dogIcon = loadImage("resource/game/dog.png");
		mushroomIcon = loadImage("resource/game/mushroom.png");
		flower1Icon = loadImage("resource/game/flower1.png");
		flower2Icon = loadImage("resource/game/flower2.png");

		highliteNode = false;
		sheepMap = new HashMap<Integer, Point>(4 * Constants.SHEEPS_AT_HOME + 1);
		List<GUINode> guiNodeList = BoardFactory.createGUINodes();
		guiNodes = new GUINode[guiNodeList.size()];
		for (GUINode node : guiNodeList) {
			guiNodes[node.index] = node;
		}

		setDoubleBuffered(true);
		addComponentListener(componentListener);
		addKeyListener(keyListener);
		setMouseListeners();
		setFocusable(true);
		requestFocusInWindow();

		RenderConfiguration.loadSettings();

		resizeBoard();
		repaint();

	}

	private void valideView() {

		gameEnded = false;
		OPTIONS[BENCHMARK] = false;
		highliteSheep = false;
		highliteNode = false;
		currentNeighbours = null;
		mouseX = 0;
		mouseY = 0;
		setMouseListeners();
		repaint();

	}

	private void setMouseListeners() {

		// alte listener entfernen
		for (MouseListener l : getMouseListeners()) {
			removeMouseListener(l);
		}
		for (MouseMotionListener l : getMouseMotionListeners()) {
			removeMouseMotionListener(l);
		}

		if (config) {
			addMouseListener(configMouseAdapter);
		} else {
			addMouseListener(dragMouseAdapter);
			addMouseMotionListener(dragMouseAdapter);
		}

	}

	public void updateGameState(GameState gameState) {

		if (this.gameState != null) {
			int turnDiff = gameState.getRound() - this.gameState.getRound();
			Move move = gameState.getLastMove();
			if (OPTIONS[SHEEP_MOVEMENT]
					&& move != null
					&& gameState.getCurrentPlayer() != this.gameState
							.getCurrentPlayer() && turnDiff >= 0
					&& turnDiff <= 1) {
				moveSheep(gameState);
			}
		}

		dropedSheep = false;
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer().getPlayerColor();
		createSheepMap();

		valideView();

		if (gameState.gameEnded()) {
			gameEnded = true;
			currentPlayer = gameState.winner();
		}

		updateBuffer = true;
		repaint();

	}

	private synchronized void moveSheep(final GameState gameState) {

		createSheepMap();
		Move move = gameState.getLastMove();
		Sheep sheep = gameState.getSheep(move.sheep);
		highliteSheep = true;
		highliteNode = false;
		updateBuffer = true;
		currentSheep = sheep.index;
		Point p = sheepMap.get(sheep.index);
		if (dropedSheep) {
			p.x = dropedX;
			p.y = dropedY;
		}
		Point q = new Point(guiNodes[move.target].getScaledCenterX(),
				guiNodes[move.target].getScaledCenterY());

		double pixelPerFrame = (0.6 * (double) size) / (1 * 30);
		double dist = Math
				.sqrt(Math.pow(p.x - q.x, 2) + Math.pow(p.y - q.y, 2));

		final int frames = (int) Math.ceil(dist / pixelPerFrame);
		final Point o = new Point(p.x, p.y);
		final Point dP = new Point(q.x - p.x, q.y - p.y);

		for (int frame = 0; frame < frames; frame++) {

			p.x = o.x + (int) ((double) (frame * dP.x) / (double) frames);
			p.y = o.y + (int) ((double) (frame * dP.y) / (double) frames);

			invalidate();
			getParent().repaint();

			try {
				Thread.sleep(1000 / 30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public Image getImage() {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
	}

	public synchronized void requestMove(int turn) {
		turnToAnswer = turn;
	}

	private synchronized void sendMove(Move move) {
		if (myTurn() && !gameEnded) {
			RenderFacade.getInstance().sendMove(move);
			turnToAnswer = -1;
		}
	}

	private void createSheepMap() {

		sheepMap.clear();
		if (gameState != null) {
			Map<Integer, Set<Sheep>> map = new HashMap<Integer, Set<Sheep>>();
			for (Sheep sheep : gameState.getSheeps()) {
				if (!map.containsKey(sheep.getNode())) {
					map.put(sheep.getNode(), new HashSet<Sheep>());
				}
				Set<Sheep> set = map.get(sheep.getNode());
				set.add(sheep);
			}

			for (Integer node : map.keySet()) {
				Set<Sheep> set = map.get(node);

				int n = set.size(), i = 0;
				int[] hatXs = guiNodes[node].getScaledXPositions(n);
				int[] hatYs = guiNodes[node].getScaledYPositions(n);
				for (Sheep sheep : set) {
					sheepMap.put(sheep.index, new Point(hatXs[i], hatYs[i]));
					i++;
				}
			}
		}
	}

	private void resizeBoard() {

		int width = getWidth() - STATS_WIDTH - 2 * BORDER_SIZE;
		int heigth = getHeight() - 2 * BORDER_SIZE
				- (OPTIONS[PROGRESS_BAR] ? PROGRESS_BAR_HEIGTH : 0);

		size = Math.min(width, heigth);
		xBorder = (width - size) / 2 + BORDER_SIZE;
		yBorder = (heigth - size) / 2 + BORDER_SIZE;

		fontX = getWidth() - STATS_WIDTH + BORDER_SIZE;

		for (GUINode guiNode : guiNodes) {
			guiNode.scale(size, xBorder, yBorder);
		}

		if (width > 0 && heigth > 0) {
			MediaTracker tracker = new MediaTracker(this);
			// scaledBgBoard = bgBoard.getScaledInstance(400, -1,
			// Image.SCALE_AREA_AVERAGING);

			scaledBgBoard = new BufferedImage(width, heigth,
					BufferedImage.TYPE_3BYTE_BGR);
			scaledBgBoard.getGraphics().drawImage(bgBoard, 0, 0, width, heigth,
					this);
			tracker.addImage(scaledBgBoard, 0);
			try {
				tracker.waitForID(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.gc();
		createSheepMap();
		updateBuffer = true;
		repaint();
	}

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

		if (config) {
			drawConfigMessage(g2);
		} else if (gameEnded) {
			drawEndMessage(g2);
		}

		bmFrames++;
		// repainted = true;
		synchronized (LOCK) {
			LOCK.notifyAll();
		}

	}

	private void fillBuffer() {

		int imageType = OPTIONS[TRANSPARANCY] ? BufferedImage.TYPE_INT_ARGB
				: BufferedImage.TYPE_INT_BGR;
		buffer = new BufferedImage(getWidth(), getHeight(), imageType);
		Graphics2D g2 = (Graphics2D) buffer.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		// hintergrundbild
		if (OPTIONS[BACKGROUND] && scaledBgBoard != null) {
			g2.drawImage(scaledBgBoard, BORDER_SIZE, BORDER_SIZE, getWidth()
					- 2 * BORDER_SIZE, getHeight() - 2 * BORDER_SIZE, this);
		} else {
			g2.setColor(new Color(76, 119, 43));
			g2.fillRect(BORDER_SIZE, BORDER_SIZE, getWidth() - 2 * BORDER_SIZE,
					getHeight() - 2 * BORDER_SIZE);
		}

		// seiitenleiste
		g2.setColor(getTransparentColor(new Color(200, 240, 200), 160));
		g2.fillRect(getWidth() - BORDER_SIZE - STATS_WIDTH, BORDER_SIZE,
				STATS_WIDTH, getHeight() - 2 * BORDER_SIZE);

		// fortschrittsleite
		if (OPTIONS[PROGRESS_BAR]) {
			g2.fillRect(BORDER_SIZE, getHeight() - BORDER_SIZE
					- PROGRESS_BAR_HEIGTH, getWidth() - 2 * BORDER_SIZE
					- STATS_WIDTH, PROGRESS_BAR_HEIGTH);
		}

		paintStaticComponents(g2);
		if (gameState != null) {
			printGameStatus(g2);
			paintSemiStaticComponents(g2);
		}

	}

	private void paintStaticComponents(Graphics2D g2) {

		// flaechig gefuellte spielfelder zeichnen
		for (GUINode guiNode : guiNodes) {

			NodeType type = guiNode.getNodeType();
			if (type != NodeType.GRASS) {

				Color c;
				if (type == NodeType.HOME1) {
					c = new Color(226, 32, 32);
				} else if (type == NodeType.HOME2) {
					c = new Color(32, 32, 226);
				} else {
					c = new Color(32, 192, 32);
				}

				g2.setColor(getTransparentColor(c, 192));
				g2.fillPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(),
						guiNode.size());
			}
		}

		// rahmen fuer jedes spielfeld zeichnen
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(2f));
		for (GUINode guiNode : guiNodes) {
			g2.drawPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(),
					guiNode.size());

		}

		updateBuffer = false;

	}

	private void paintSemiStaticComponents(Graphics2D g2) {

		// fortschrittsbalken
		if (OPTIONS[PROGRESS_BAR]) {
			g2.setColor(Color.BLACK);
			g2.setFont(h3);
			int left = fmH3.stringWidth("Spielfortschritt:") + BORDER_SIZE + 30;
			int right = getWidth() - left - 30;
			int fontY = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH / 2
					+ fmH3.getHeight() / 2 - 4;
			g2.drawString("Spielfortschritt:", BORDER_SIZE + 10, fontY);

			int round = gameState.getRound() + 1;
			String roundString = Integer.toString(gameState.getRound() + 1);
			if (round > Constants.ROUND_LIMIT) {
				roundString = Integer.toString(Constants.ROUND_LIMIT);
			}
			g2.drawString(roundString, fontX, fontY);

			g2.drawString("Runde " + roundString + " von 30",
					right + 30, fontY);

			int progress = (gameState.getTurn() * (right - left))
					/ (2 * Constants.ROUND_LIMIT);

			if (OPTIONS[CURVED_SHAPES]) {
				g2.setColor(Color.GRAY);
				g2.fillRoundRect(left, getHeight() - BORDER_SIZE
						- PROGRESS_BAR_HEIGTH + 8, right - left,
						PROGRESS_BAR_HEIGTH - 16, 10, 10);

				g2.setColor(Color.GREEN);
				g2.fillRoundRect(left, getHeight() - BORDER_SIZE
						- PROGRESS_BAR_HEIGTH + 8, progress,
						PROGRESS_BAR_HEIGTH - 16, 10, 10);

				g2.setColor(Color.DARK_GRAY);
				g2.drawRoundRect(left, getHeight() - BORDER_SIZE
						- PROGRESS_BAR_HEIGTH + 8, right - left,
						PROGRESS_BAR_HEIGTH - 16, 10, 10);
			} else {
				g2.setColor(Color.GRAY);
				g2.fillRect(left, getHeight() - BORDER_SIZE
						- PROGRESS_BAR_HEIGTH + 8, right - left,
						PROGRESS_BAR_HEIGTH - 16);

				g2.setColor(Color.GREEN);
				g2.fillRect(left, getHeight() - BORDER_SIZE
						- PROGRESS_BAR_HEIGTH + 8, progress,
						PROGRESS_BAR_HEIGTH - 16);

			}

			g2.drawImage(progessSheepIcon, left + progress
					- PROGRESS_SHEEP_SIZE / 2, getHeight()
					- PROGRESS_SHEEP_SIZE, PROGRESS_SHEEP_SIZE,
					PROGRESS_SHEEP_SIZE, this);

		}

		// rahmen
		g2.setColor(getPlayerColor(currentPlayer));
		g2.fillRect(0, 0, getWidth(), BORDER_SIZE);
		g2.fillRect(0, getHeight() - BORDER_SIZE, getWidth(), BORDER_SIZE);

		g2.fillRect(0, 0, BORDER_SIZE, getHeight());
		g2.fillRect(getWidth() - BORDER_SIZE, 0, BORDER_SIZE, getHeight());

		// blumen zeichnen
		for (Flower flower : gameState.getAllFlowers()) {
			GUINode guiNode = guiNodes[flower.node];

			switch (flower.amount) {

			case -1:
				g2.drawImage(mushroomIcon, guiNode.getScaledCenterX()
						- ICON_SIZE / 2, guiNode.getScaledCenterY() - ICON_SIZE
						/ 2, ICON_SIZE, ICON_SIZE, this);
				break;

			case 1:
				g2.drawImage(flower1Icon, guiNode.getScaledCenterX()
						- ICON_SIZE / 2, guiNode.getScaledCenterY() - ICON_SIZE
						/ 2, ICON_SIZE, ICON_SIZE, this);
				break;

			case 2:
				g2.drawImage(flower2Icon, guiNode.getScaledCenterX()
						- ICON_SIZE / 2, guiNode.getScaledCenterY() - ICON_SIZE
						/ 2, ICON_SIZE, ICON_SIZE, this);
				break;

			default:
			}

		}

		// knotenindizes
		for (GUINode guiNode : guiNodes) {
			if (OPTIONS[NODE_INDICES]) {

				int x = guiNode.getScaledCenterX();
				int y = guiNode.getScaledCenterY();
				g2.setFont(hSheep);
				String index = Integer.toString(guiNode.index);
				int indexW = fmSheep.stringWidth(index);
				int indexH = fmSheep.getHeight();

				if (OPTIONS[CURVED_SHAPES]) {
					g2.setColor(Color.GRAY);
					g2.fillRoundRect(x - indexW / 2 - 4, y - indexH / 2 - 2,
							indexW + 8, indexH, 8, 8);

					g2.setColor(Color.DARK_GRAY);
					g2.setStroke(new BasicStroke(1.5f));
					g2.drawRoundRect(x - indexW / 2 - 4, y - indexH / 2 - 2,
							indexW + 8, indexH, 8, 8);
				} else {
					g2.setColor(Color.GRAY);
					g2.fillRect(x - indexW / 2 - 4, y - indexH / 2 - 2,
							indexW + 8, indexH);
				}

				g2.setColor(Color.WHITE);
				g2.drawString(index, x - indexW / 2, y - indexH / 2 - 1
						+ indexH - 3);

			}
		}

		// highlights fuer erreichbare knoten zeichnen
		if (highliteSheep) {
			Sheep sheep = gameState.getSheep(currentSheep);
			g2.setColor(sheep.owner == currentPlayer ? getPlayerColor(
					sheep.owner, true) : Color.BLACK);
			if (currentNeighbours != null && sheep.owner != null) {
				for (Integer n : currentNeighbours) {
					GUINode guiNode = guiNodes[n];
					g2.setStroke(new BasicStroke(3.5f));
					g2.drawPolygon(guiNode.getScaledXs(),
							guiNode.getScaledYs(), guiNode.size());
				}
			}
		}

		List<Sheep> sortedSheeps = new LinkedList<Sheep>(gameState.getSheeps());
		Collections.sort(sortedSheeps, new Comparator<Sheep>() {

			@Override
			public int compare(Sheep o1, Sheep o2) {
				return sheepMap.get(o1.index).y < sheepMap.get(o2.index).y ? 1
						: -1;
			}

		});

		// schafe malen
		g2.setFont(getFont());
		if (sheepMap != null) {
			for (Sheep sheep : sortedSheeps) {
				if (sheep.index != currentSheep) {
					drawSheep(g2, sheep, false);
				}
			}
		}

	}

	private boolean myTurn() {
		return turnToAnswer == gameState.getTurn();
	}

	private void paintDynamicComponents(Graphics2D g2) {

		// highlight fuer gewaehlten knoten zeichnen
		Sheep sheep = gameState.getSheep(currentSheep);
		if (highliteSheep) {
			Color c = getPlayerColor(sheep.owner);
			if (highliteNode && myTurn()) {

				g2.setColor(getTransparentColor(c, 128));
				GUINode currentGUINode = guiNodes[currentNode];
				g2.fillPolygon(currentGUINode.getScaledXs(), currentGUINode
						.getScaledYs(), currentGUINode.size());

				g2.setColor(c);
				g2.setStroke(new BasicStroke(3.5f));
				g2.drawPolygon(currentGUINode.getScaledXs(), currentGUINode
						.getScaledYs(), currentGUINode.size());
			}

		}

		drawSheep(g2, sheep, highliteSheep);

	}

	private void drawConfigMessage(Graphics2D g2) {

		String msg = "Einstellungen";

		int msgW = fmH1.stringWidth(msg);
		int msgH = fmH1.getHeight();
		int xCenter = xBorder + size / 2;

		int w = msgW;
		int h = msgH + 10 + 25 * OPTION_NAMES.length;
		for (int i = 0; i < OPTION_NAMES.length; i++) {
			w = Math.max(w, fmH4.stringWidth(OPTION_NAMES[i]) + 25);
		}

		g2.setColor(getTransparentColor(new Color(255, 255, 255), 192));
		if (OPTIONS[CURVED_SHAPES]) {
			g2.fillRoundRect(xCenter - w / 2 - 20, (getHeight() - h) / 2 - 5,
					w + 40, h + 10, 20, 20);
		} else {
			g2.fillRect(xCenter - w / 2 - 20, (getHeight() - h) / 2 - 5,
					w + 40, h + 10);
		}

		h = (getHeight() - h) / 2 + fmH1.getHeight() - 3;
		g2.setFont(h1);
		g2.setColor(Color.BLACK);
		g2.drawString(msg, xCenter - msgW / 2, h);

		h += 10;
		g2.setFont(h4);
		configX = 5 + xCenter - w / 2;
		configY = h;
		for (int i = 0; i < OPTION_NAMES.length; i++) {
			if (OPTIONS[CURVED_SHAPES]) {
				if (OPTIONS[i]) {
					g2.setColor(Color.GRAY);
					g2.fillRoundRect(configX, h, 20, 20, 10, 10);
				}
				g2.setColor(Color.DARK_GRAY);
				g2.drawRoundRect(configX, h, 20, 20, 10, 10);

			} else {
				if (OPTIONS[i]) {
					g2.setColor(Color.GRAY);
					g2.fillRect(configX, h, 20, 20);
				}
				g2.setColor(Color.DARK_GRAY);
				g2.drawRect(configX, h, 20, 20);
			}

			g2.setColor(Color.BLACK);
			String option = OPTION_NAMES[i];
			if (i == BENCHMARK && OPTIONS[BENCHMARK] && bmShow) {
				if (bmFrameRate < 30) {
					g2.setColor(Color.RED);
				}
				option += " @ " + bmFrameRate + " fps";
			}
			g2.drawString(option, configX + 25, h + fmH4.getHeight());
			h += 25;

		}

	}

	private void drawEndMessage(Graphics2D g2) {
		String msg = "Das Spiel ist zu Ende!";

		PlayerColor winner = gameState.winner();
		if (winner == PlayerColor.RED) {
			msg = gameState.getPlayerNames()[0] + " hat gewonnen!";
		} else if (winner == PlayerColor.BLUE) {
			msg = gameState.getPlayerNames()[1] + " hat gewonnen!";
		}

		String info = gameState.winningReason();
		int delim = info.indexOf("\\n");
		String info1 = info.substring(0, delim);
		String info2 = info.substring(delim + 2);

		int msgW = fmH1.stringWidth(msg);
		int msgH = fmH1.getHeight();
		int info1W = fmH3.stringWidth(info1);
		int info2W = fmH3.stringWidth(info2);
		int infoW = Math.max(info1W, info2W);
		int infoH = 2 * fmH3.getHeight() + 3;
		int w = Math.max(msgW, infoW);
		int h = msgH + infoH;
		int xCenter = xBorder + size / 2;

		g2.setColor(getTransparentColor(new Color(255, 255, 255), 192));

		if (OPTIONS[CURVED_SHAPES]) {
			g2.fillRoundRect(xCenter - w / 2 - 20, getHeight() / 2 - msgH - 5,
					w + 40, h + 10, 20, 20);
		} else {
			g2.fillRect(xCenter - w / 2 - 20, getHeight() / 2 - msgH - 5,
					w + 40, h + 10);
		}

		h = getHeight() / 2 - 5;
		g2.setFont(h1);
		g2.setColor(getPlayerColor(winner, true));
		g2.drawString(msg, xCenter - msgW / 2, h);

		h += msgH - 10;
		g2.setFont(h3);
		g2.setColor(Color.BLACK);
		g2.drawString(info1, xCenter - info1W / 2, h);

		h += 20;
		g2.drawString(info2, xCenter - info2W / 2, h);

	}

	private void printGameStatus(Graphics2D g2) {

		fontY = 42;

		g2.setFont(h1);
		g2.setColor(Color.BLACK);
		g2.drawString(TITLE, fontX, fontY);

		int flowers = gameState.getTotalFlowerAmount();
		String type = flowers < 0 ? "Fliegenpilze" : "Blumen";
		if (Math.abs(flowers) == 1) {
			type = type.substring(0, type.length() - 1);
		}

		fontY += 20;
		g2.setFont(h4);
		g2.drawString(Math.abs(flowers) + " " + type + " auf dem Spielfeld",
				fontX, fontY);

		if (!OPTIONS[PROGRESS_BAR]) {
			fontY += 20;
			int round = gameState.getRound() + 1;
			String roundString = "Das Rundenlimit wurde erreicht.";
			if (round <= Constants.ROUND_LIMIT) {
				roundString = "Runde " + (gameState.getRound() + 1) + " von "
						+ Constants.ROUND_LIMIT + " Runden.";
			}
			g2.drawString(roundString, fontX, fontY);
		}

		fontY += 35;
		g2.setFont(h2);
		String name = gameState.getPlayerNames()[0];
		g2.setColor(getPlayerColor(PlayerColor.RED));

		g2.drawString(name, fontX, fontY);
		g2.setColor(Color.BLACK);
		if (OPTIONS[DEBUG_VIEW]) {
			fontY = drawSmallPlayerInfo(g2, fontY, fontX, PlayerColor.RED);
		} else {
			fontY = drawPlayerInfo(g2, fontY, fontX, PlayerColor.RED);
		}

		fontY += 35;
		g2.setFont(h2);
		name = gameState.getPlayerNames()[1];
		g2.setColor(getPlayerColor(PlayerColor.BLUE));

		g2.drawString(name, fontX, fontY);
		g2.setColor(Color.BLACK);
		if (OPTIONS[DEBUG_VIEW]) {
			fontY = drawSmallPlayerInfo(g2, fontY, fontX, PlayerColor.BLUE);
		} else {
			fontY = drawPlayerInfo(g2, fontY, fontX, PlayerColor.BLUE);
		}
		int i = 0;
		fontY += 15;
		if (OPTIONS[CURVED_SHAPES]) {
			for (Die dice : gameState.getDice()) {
				drawDie(g2, fontX + 60 * i++, fontY, dice.value);
			}
		} else {
			for (Die dice : gameState.getDice()) {
				drawSimpleDie(g2, fontX + 60 * i++, fontY, dice.value);
			}
		}

		fontY += 50;
		g2.setColor(Color.BLACK);
		g2.setFont(h4);
		if (OPTIONS[DEBUG_VIEW]) {
			Move lastMove = gameState.getLastMove();
			if (lastMove != null) {
				fontY += 25;
				g2.drawString("Letzter Zug: Schaf #" + lastMove.sheep
						+ " auf Feld #" + lastMove.target, fontX, fontY);

				fontY += 5;
				for (DebugHint hint : lastMove.getHints()) {
					fontY += 20;
					g2.drawString(hint.content, fontX, fontY);
				}

			}

		}

		int fontY2 = fontY;
		g2.setColor(Color.BLACK);
		g2.setFont(h4);
		if (!OPTIONS[DEBUG_VIEW] && highliteSheep) {
			Sheep sheep = gameState.getSheep(currentSheep);
			int ownSheeps = sheep.getSize(sheep.owner);
			int opponentSheeps = sheep.getSize(sheep.owner.opponent());
			flowers = sheep.getFlowers();

			fontY2 += 25;
			g2.drawString(ownSheeps + " eigene" + (ownSheeps == 1 ? "s" : "")
					+ " Schaf" + (ownSheeps == 1 ? "" : "e"), fontX, fontY2);

			fontY2 += 20;
			g2.drawString(opponentSheeps + " gegnerische"
					+ (opponentSheeps == 1 ? "s" : "") + " Schaf"
					+ (opponentSheeps == 1 ? "" : "e"), fontX, fontY2);

			type = flowers < 0 ? "Fliegenpilze" : "Blumen";
			if (Math.abs(flowers) == 1) {
				type = type.substring(0, type.length() - 1);
			}

			fontY2 += 20;
			g2.drawString(Math.abs(flowers) + " " + type + " eingesammelt",
					fontX, fontY2);

			if (sheep.getDogState() == DogState.PASSIVE) {
				fontY2 += 25;
				g2.drawString("Der Schäferhund ist passiv", fontX, fontY2);
			} else if (sheep.getDogState() == DogState.ACTIVE) {
				fontY2 += 20;
				g2.drawString("Der Schäferhund ist aktiv", fontX, fontY2);
			}

		}

		fontY2 = getHeight() - BORDER_SIZE - 5;
		if (OPTIONS[PROGRESS_BAR]) {
			fontY2 -= PROGRESS_BAR_HEIGTH;
		}
		int fontX2 = getWidth() - BORDER_SIZE - 5;
		fontX2 -= fmSheep.stringWidth("Leertaste für Einstellungen");
		g2.setFont(hSheep);
		g2.setColor(Color.DARK_GRAY);
		if (hasFocus()) {
			g2.drawString("Leertaste für Einstellungen", fontX2, fontY2);
		}

	}

	private void drawDie(Graphics2D g2, int x, int y, int value) {

		g2.setColor(Color.GRAY);
		g2.fillRoundRect(x, y, 50, 50, 15, 15);

		g2.setColor(Color.DARK_GRAY);
		g2.drawRoundRect(x, y, 50, 50, 15, 15);

		g2.setColor(Color.WHITE);
		switch (value) {
		case 5:
			g2.fillArc(x + 37 - 4, y + 13 - 4, 8, 8, 0, 360);
			g2.fillArc(x + 13 - 4, y + 37 - 4, 8, 8, 0, 360);

		case 3:
			g2.fillArc(x + 13 - 4, y + 13 - 4, 8, 8, 0, 360);
			g2.fillArc(x + 37 - 4, y + 37 - 4, 8, 8, 0, 360);

		case 1:
			g2.fillArc(x + 25 - 4, y + 25 - 4, 8, 8, 0, 360);
			break;

		case 6:
			g2.fillArc(x + 37 - 4, y + 25 - 4, 8, 8, 0, 360);
			g2.fillArc(x + 13 - 4, y + 25 - 4, 8, 8, 0, 360);

		case 4:
			g2.fillArc(x + 37 - 4, y + 13 - 4, 8, 8, 0, 360);
			g2.fillArc(x + 13 - 4, y + 37 - 4, 8, 8, 0, 360);

		case 2:
			g2.fillArc(x + 13 - 4, y + 13 - 4, 8, 8, 0, 360);
			g2.fillArc(x + 37 - 4, y + 37 - 4, 8, 8, 0, 360);
			break;

		default:
			break;
		}

	}

	private void drawSimpleDie(Graphics2D g2, int x, int y, int value) {

		g2.setColor(Color.GRAY);
		g2.fillRect(x, y, 50, 50);

		g2.setColor(Color.WHITE);
		switch (value) {
		case 5:
			g2.fillRect(x + 37 - 4, y + 13 - 4, 8, 8);
			g2.fillRect(x + 13 - 4, y + 37 - 4, 8, 8);

		case 3:
			g2.fillRect(x + 13 - 4, y + 13 - 4, 8, 8);
			g2.fillRect(x + 37 - 4, y + 37 - 4, 8, 8);

		case 1:
			g2.fillRect(x + 25 - 4, y + 25 - 4, 8, 8);
			break;

		case 6:
			g2.fillRect(x + 37 - 4, y + 25 - 4, 8, 8);
			g2.fillRect(x + 13 - 4, y + 25 - 4, 8, 8);

		case 4:
			g2.fillRect(x + 37 - 4, y + 13 - 4, 8, 8);
			g2.fillRect(x + 13 - 4, y + 37 - 4, 8, 8);

		case 2:
			g2.fillRect(x + 13 - 4, y + 13 - 4, 8, 8);
			g2.fillRect(x + 37 - 4, y + 37 - 4, 8, 8);
			break;

		default:
			break;
		}

	}

	private int drawPlayerInfo(Graphics2D g2, int fontY, int fontX,
			PlayerColor player) {

		int[] stats = gameState.getPlayerStats(player);

		fontY += 20;
		g2.setFont(h4);
		g2.drawString(stats[1] + " Schafe im Spiel", fontX, fontY);

		fontY += 20;
		g2.drawString(stats[2] + " Schafe gefangen", fontX, fontY);

		fontY += 20;
		g2.drawString(stats[3] + " Schafe gestohlen", fontX, fontY);

		fontY += 25;
		String type = (stats[4] < 0 ? "Fliegenpilze" : "Blumen");
		if (Math.abs(stats[4]) == 1) {
			type = type.substring(0, type.length() - 1);
		}
		g2.drawString(Math.abs(stats[4]) + " " + type + " gesammelt", fontX,
				fontY);

		fontY += 20;
		type = (stats[5] < 0 ? "Fliegenpilze" : "Blumen");
		if (Math.abs(stats[5]) == 1) {
			type = type.substring(0, type.length() - 1);
		}
		g2.drawString(Math.abs(stats[5]) + " " + type + " gefressen", fontX,
				fontY);

		// fontY += 25;
		g2.setFont(h0);
		g2.setColor(getTransparentColor(getPlayerColor(player), 174));
		g2.drawString(Integer.toString(stats[6]), getWidth() - 2 * BORDER_SIZE
				- fmH0.stringWidth(Integer.toString(stats[6])), fontY);

		return fontY;

	}

	private int drawSmallPlayerInfo(Graphics2D g2, int fontY, int fontX,
			PlayerColor player) {

		int dFontX = STATS_WIDTH / 8;
		int[] stats = gameState.getPlayerStats(player);

		fontY += 20;
		g2.setFont(h4);
		g2.drawString(Integer.toString(stats[1]), fontX, fontY);

		fontX += dFontX;
		g2.drawString(Integer.toString(stats[2]), fontX, fontY);

		fontX += dFontX;
		g2.drawString(Integer.toString(stats[3]), fontX, fontY);

		fontX += dFontX;
		g2.drawString(Integer.toString(stats[4]), fontX, fontY);

		fontX += dFontX;
		g2.drawString(Integer.toString(stats[5]), fontX, fontY);

		fontX += dFontX;
		g2.setFont(h3);
		g2.drawString(stats[6] + " Punkte", fontX, fontY);

		return fontY;

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
			color = PLAYER1_COLOR;
			break;
		case BLUE:
			color = PLAYER2_COLOR;
			break;

		default:
			color = Color.DARK_GRAY;
		}

		return color;
	}

	private void drawSheep(Graphics2D g2, Sheep sheep, boolean highlight) {

		if (sheep != null) {

			Point p = sheepMap.get(sheep.index);
			// System.out.println(p);

			// FIXME: warum kann p == null auftreten?
			// if (OPTIONS[BENCHMARK]) {
			// int dX = guiNodes[0].getScaledCenterX();
			// int dY = guiNodes[0].getScaledCenterY();
			// double cosPhi = Math.cos(-bmPhi);
			// double sinPhi = Math.sin(-bmPhi);
			// int x = (int) ((p.x - dX) * cosPhi - (p.y - dY) * sinPhi);
			// int y = (int) ((p.x - dX) * sinPhi + (p.y - dY) * cosPhi);
			// p = new Point(dX + x, dY + y);
			// } else
			//				

			if (draggedSheep && highlight) {
				p = new Point(mouseX, mouseY);
			}
			int spread = (sheep.getDogState() != null)
					&& (sheep.getSize(PlayerColor.RED)
							+ sheep.getSize(PlayerColor.BLUE) > 0) ? 5 : 0;

			int n = 4;
			int s = Math.min(42, size / 26);
			if (highlight) {
				s += 10;
			}
			int[] pointerXs = new int[] { s + 10, -(s - 10), -(s / 2 - 10),
					-(s - 10) };
			int[] pointerYs = new int[] { 0, (s - 5), 0, -(s - 5) };

			GUINode target = guiNodes[sheep.getTarget()];
			if (sheep.owner != null) {
				double phiR = Math.atan((float) (p.x - target
						.getScaledCenterX())
						/ (p.y - target.getScaledCenterY()));
				phiR += ((p.y >= target.getScaledCenterY()) ? 1 : -1) * Math.PI
						/ 2;

				int x, y;
				double cosPhi = Math.cos(-phiR);
				double sinPhi = Math.sin(-phiR);
				for (int i = 0; i < n; i++) {
					x = (int) (pointerXs[i] * cosPhi - pointerYs[i] * sinPhi);
					y = (int) (pointerXs[i] * sinPhi + pointerYs[i] * cosPhi);
					pointerXs[i] = x + p.x;
					pointerYs[i] = y + p.y;
				}

				g2.setColor(getPlayerColor(sheep.owner));
				g2.fillPolygon(pointerXs, pointerYs, n);
				g2.setColor(Color.BLACK);
				g2.setStroke(new BasicStroke(2f));
				g2.drawPolygon(pointerXs, pointerYs, n);

			}

			if (sheep.getDogState() == DogState.PASSIVE) {
				g2.drawImage(dogIcon, p.x + spread - DOG_SIZE / 2, p.y
						- DOG_SIZE / 2, DOG_SIZE, DOG_SIZE, this);
			}

			if (sheep.owner != null) {
				g2.drawImage(sheepIcon, p.x - spread - SHEEP_SIZE / 2, p.y
						- SHEEP_SIZE / 2, SHEEP_SIZE, SHEEP_SIZE, this);
			}

			if (sheep.getDogState() == DogState.ACTIVE) {
				g2.drawImage(dogIcon, p.x + spread - DOG_SIZE / 2, p.y
						- DOG_SIZE / 2, DOG_SIZE, DOG_SIZE, this);
			}

			if (OPTIONS[SHEEP_INDICES]) {

				g2.setFont(h4);
				String index = Integer.toString(sheep.index);
				int indexW = fmH4.stringWidth(index);
				int indexH = fmH4.getHeight();

				if (OPTIONS[CURVED_SHAPES]) {
					g2.setColor(Color.GRAY);
					g2.fillRoundRect(p.x - indexW / 2 - 4, p.y - 10,
							indexW + 8, indexH, 8, 8);

					g2.setColor(Color.DARK_GRAY);
					g2.setStroke(new BasicStroke(1.5f));
					g2.drawRoundRect(p.x - indexW / 2 - 4, p.y - 10,
							indexW + 8, indexH, 8, 8);
				} else {
					g2.setColor(Color.GRAY);
					g2.fillRect(p.x - indexW / 2 - 4, p.y - 10, indexW + 8,
							indexH);
				}

				g2.setColor(Color.WHITE);
				g2.drawString(index, p.x - indexW / 2, p.y - 10 + indexH - 3);

			}

			if (sheep.owner != null) {
				int ownSheeps = sheep.getSize(sheep.owner);
				int opponentSheeps = sheep.getSize(sheep.owner.opponent());
				int flowers = sheep.getFlowers();

				g2.setFont(hSheep);
				String stat = ownSheeps + ", " + opponentSheeps + ", "
						+ flowers;
				int statsW = fmSheep.stringWidth(stat);
				int statsH = fmSheep.getHeight();

				if (OPTIONS[CURVED_SHAPES]) {
					g2.setColor(getPlayerColor(sheep.owner));
					g2.fillRoundRect(p.x - statsW / 2 - 4, p.y + 10,
							statsW + 8, statsH, 8, 8);

					g2.setColor(Color.BLACK);
					g2.setStroke(new BasicStroke(1.5f));
					g2.drawRoundRect(p.x - statsW / 2 - 4, p.y + 10,
							statsW + 8, statsH, 8, 8);
				} else {
					g2.setColor(getPlayerColor(sheep.owner));
					g2.fillRect(p.x - statsW / 2 - 4, p.y + 10, statsW + 8,
							statsH);
				}

				g2
						.setColor(sheep.getDogState() == DogState.ACTIVE ? Color.YELLOW
								: Color.WHITE);
				g2.drawString(stat, p.x - statsW / 2, p.y + 10 + statsH - 3);
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

}
