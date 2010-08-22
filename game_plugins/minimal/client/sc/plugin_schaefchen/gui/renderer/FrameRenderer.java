/**
 * 
 */
package sc.plugin_schaefchen.gui.renderer;

import java.awt.BasicStroke;
import static sc.plugin_schaefchen.gui.renderer.RenderConfiguration.*;

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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import sc.plugin_schaefchen.BoardFactory;
import sc.plugin_schaefchen.Die;
import sc.plugin_schaefchen.DogState;
import sc.plugin_schaefchen.Flower;
import sc.plugin_schaefchen.GUINode;
import sc.plugin_schaefchen.GameState;
import sc.plugin_schaefchen.IGameHandler;
import sc.plugin_schaefchen.Move;
import sc.plugin_schaefchen.NodeType;
import sc.plugin_schaefchen.Player;
import sc.plugin_schaefchen.PlayerColor;
import sc.plugin_schaefchen.Sheep;
import sc.plugin_schaefchen.util.Constants;
import sc.shared.GameResult;

/**
 * @author ffi, sca, tkra
 * 
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JPanel implements IRenderer {

	private static final Color PLAYER2_COLOR = Color.BLUE;
	private static final Color PLAYER1_COLOR = Color.RED;

	private static final Font h1 = new Font("Helvetica", Font.BOLD, 25);
	private static final Font h2 = new Font("Helvetica", Font.BOLD, 20);
	private static final Font h3 = new Font("Helvetica", Font.BOLD, 14);
	private static final Font h4 = new Font("Helvetica", Font.PLAIN, 14);
	private static final Font hSheep = new Font("Helvetica", Font.PLAIN, 10);
	private final FontMetrics fmH1 = getFontMetrics(h1);
	// private final FontMetrics fmH2 = getFontMetrics(h2);
	private final FontMetrics fmH3 = getFontMetrics(h3);
	private final FontMetrics fmH4 = getFontMetrics(h4);
	private final FontMetrics mfSheep = getFontMetrics(hSheep);

	private final static int BORDER_SIZE = 5;
	private static final String TITLE = "Schäfchen im Trockenen";
	private static final int ICON_SIZE = 34;
	private static final int SHEEP_SIZE = 44;
	private static final int DOG_SIZE = 50;
	private final int STATS_WIDTH = getFontMetrics(h1).stringWidth(TITLE) + 4
			* BORDER_SIZE;

	// local instances of current players and gameState
	private PlayerColor currentPlayer;
	private GameState gameState;

	// We also have a game handler to know whats going on
	private IGameHandler handler;

	private boolean onlyObserving;

	// Am I currently visible?
	private boolean showing;
	private boolean myturn;

	private boolean ended;
	private String endErrorMsg;
	private PlayerColor endColor;

	private GUINode[] guiNodes;

	private boolean highliteNode;
	private int currentNode;
	private int mousey;
	private int mousex;
	// private int dx, dy;
	private Set<Integer> currentNeighbours;
	private Sheep currentSheep;

	private final Image bgBoard;
	private Image scaledBgBoard;

	private final Image sheepIcon;
	private final Image dogIcon;
	private final Image mushroomIcon;
	private final Image flower1Icon;
	private final Image flower2Icon;

	private Map<Sheep, Point> sheepMap;
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

			mousex = e.getX();
			mousey = e.getY();
			requestFocusInWindow();
			for (Sheep sheep : sheepMap.keySet()) {
				Point p = sheepMap.get(sheep);
				if (Math.sqrt(Math.pow(p.x - mousex, 2)
						+ Math.pow(p.y - mousey, 2)) < 20
						&& sheep.owner != null) {
					currentSheep = sheep;
					currentNeighbours = gameState.getValidReachableNodes(
							sheep.index).keySet();

					repaint();
					break;
				}

			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {

			mousex = 0;
			mousey = 0;
			if (currentSheep != null && highliteNode && !ended) {
				if (currentSheep.owner != null
						&& currentSheep.owner == currentPlayer
						&& currentNeighbours.contains(currentNode)) {

					// wenn zug abgeschickt werden soll aktuelln hut
					// nicht zurueckfallen lassen
					if (!onlyObserving) {
						Point p = sheepMap.get(currentSheep);
						p.x = e.getX();
						p.y = e.getY();
						sendMove(new Move(currentSheep.index, currentNode));
					}
				}
			}

			currentSheep = null;
			highliteNode = false;
			currentNeighbours = null;
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {

			mousex = e.getX();
			mousey = e.getY();
			highliteNode = false;
			if (currentSheep != null) {

				if (currentSheep.owner == currentPlayer) {
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
					if (i == SIMEPLE_SHAPES) {
						GUINode.setSimple(OPTIONS[SIMEPLE_SHAPES]);
					}
					RenderConfiguration.saveSettings();
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

	public FrameRenderer(final IGameHandler handler, final boolean onlyObserving) {
		this.handler = handler;
		this.onlyObserving = onlyObserving;

		bgBoard = RendererUtil.getImage("resource/game/boden_wiese3.png");

		sheepIcon = RendererUtil.getImage("resource/game/sheep.png");
		dogIcon = RendererUtil.getImage("resource/game/dog.png");
		mushroomIcon = RendererUtil.getImage("resource/game/mushroom.png");
		flower1Icon = RendererUtil.getImage("resource/game/flower1.png");
		flower2Icon = RendererUtil.getImage("resource/game/flower2.png");

		highliteNode = false;
		sheepMap = new HashMap<Sheep, Point>(4 * Constants.SHEEPS_AT_HOME + 1);
		List<GUINode> guiNodeList = BoardFactory.guiNodes;
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

		currentSheep = null;
		highliteNode = false;
		currentNeighbours = null;
		mousex = 0;
		mousey = 0;
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
			/*
			 * if (OPTIONS[DRAG_N_DROP]) { addMouseListener(dragMouseAdapter);
			 * addMouseMotionListener(dragMouseAdapter); } else {
			 */addMouseListener(dragMouseAdapter);
			addMouseMotionListener(dragMouseAdapter);
			// }
		}

	}

	@Override
	public void shown() {
		showing = true;
	}

	@Override
	public void hidden() {
		showing = false;
	}

	@Override
	public void updatePlayer(final Player currentPlayer, final Player nextPlayer) {
		this.currentPlayer = currentPlayer.getPlayerColor();
		repaint();
	}

	@Override
	public void updateGameState(GameState gameState) {
		this.gameState = gameState;

		createSheepMap();
		currentPlayer = gameState.getCurrentPlayer().getPlayerColor();
		config = false;
		ended = false;
		valideView();
	}

	@Override
	public Image getImage() {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
	}

	@Override
	public void requestMove() {
		myturn = true;
	}

	@Override
	public void gameEnded(final GameResult data, PlayerColor color,
			String errorMessage) {
		ended = true;
		endErrorMsg = errorMessage;
		endColor = null;
		if (data != null) {
			if (data.getScores().get(0).getValues().get(0).equals(
					new BigDecimal(1))) {
				endColor = PlayerColor.PLAYER1;
			} else if (data.getScores().get(1).getValues().get(0).equals(
					new BigDecimal(1))) {
				endColor = PlayerColor.PLAYER2;
			}
		}
		repaint();
	}

	@Override
	public void gameError(String errorMessage) {
		ended = true;
		endErrorMsg = errorMessage == null ? "Es ist ein unbekannter Fehler aufgetreten."
				: errorMessage;
		endColor = null;
		repaint();
	}

	@Override
	public void updateChat(String chatMsg) {

	}

	private void sendMove(Move move) {
		if (myturn && !ended && !onlyObserving) {
			handler.sendAction(move);
			myturn = false;
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
					sheepMap.put(sheep, new Point(hatXs[i], hatYs[i]));
					i++;
				}
			}
		}
	}

	private void resizeBoard() {

		int width = getWidth() - STATS_WIDTH - 2 * BORDER_SIZE;
		int heigth = getHeight() - 2 * BORDER_SIZE;

		size = Math.min(width, heigth);
		xBorder = (width - size) / 2 + BORDER_SIZE;
		yBorder = (heigth - size) / 2 + BORDER_SIZE;

		for (GUINode guiNode : guiNodes) {
			guiNode.scale(size, xBorder, yBorder);
		}

		MediaTracker tracker = new MediaTracker(this);
		scaledBgBoard = bgBoard.getScaledInstance(400, -1, Image.SCALE_SMOOTH);
		tracker.addImage(scaledBgBoard, 0);
		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		createSheepMap();
		repaint();
	}

	@Override
	public void paint(Graphics g) {

		if (!showing) {
			return;
		}

		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		// hintergrundbild
		if (OPTIONS[BACKGROUND]) {
			g2.drawImage(scaledBgBoard, BORDER_SIZE, BORDER_SIZE, getWidth()
					- 2 * BORDER_SIZE, getHeight() - 2 * BORDER_SIZE, this);
		} else {
			g2.setColor(new Color(76, 119, 43));
			g2.fillRect(BORDER_SIZE, BORDER_SIZE, getWidth() - 2 * BORDER_SIZE,
					getHeight() - 2 * BORDER_SIZE);
		}

		// seiitenleiste
		g2.setColor(getTransparentColor(new Color(200, 240, 200), 160));
		g2.fillRect(getWidth() - BORDER_SIZE - STATS_WIDTH, 0, STATS_WIDTH
				+ BORDER_SIZE, getHeight());

		paintStaticComponents(g2);
		if (gameState != null) {
			printGameStatus(g2);
			paintDynamicComponents(g2);
		}

		if (config) {
			drawConfigMessage(g2);
		} else if (ended) {
			drawEndMessage(g2);
		}

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
		if (OPTIONS[SIMEPLE_SHAPES]) {
			g2.fillRect(xCenter - w / 2 - 20, (getHeight() - h) / 2 - 5,
					w + 40, h + 10);
		} else {
			g2.fillRoundRect(xCenter - w / 2 - 20, (getHeight() - h) / 2 - 5,
					w + 40, h + 10, 20, 20);
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
			if (OPTIONS[SIMEPLE_SHAPES]) {
				if (OPTIONS[i]) {
					g2.setColor(Color.GRAY);
					g2.fillRect(configX, h, 20, 20);
				}
				g2.setColor(Color.DARK_GRAY);
				g2.drawRect(configX, h, 20, 20);
			} else {
				if (OPTIONS[i]) {
					g2.setColor(Color.GRAY);
					g2.fillRoundRect(configX, h, 20, 20, 10, 10);
				}
				g2.setColor(Color.DARK_GRAY);
				g2.drawRoundRect(configX, h, 20, 20, 10, 10);

			}
			g2.setColor(Color.BLACK);
			g2.drawString(OPTION_NAMES[i], configX + 25, h + fmH4.getHeight());
			h += 25;
		}

	}

	private void drawEndMessage(Graphics2D g2) {
		String msg = "Das Spiel ist zu Ende";
		if (endColor == PlayerColor.PLAYER1) {
			msg = gameState.getPlayerNames()[0] + " hat gewonnen!";
		} else if (endColor == PlayerColor.PLAYER2) {
			msg = gameState.getPlayerNames()[1] + " hat gewonnen!";
		}
		String info = endErrorMsg != null ? endErrorMsg
				: "Herzlichen Glückwunsch!";

		int msgW = fmH1.stringWidth(msg);
		int msgH = fmH1.getHeight();
		int infoW = fmH3.stringWidth(info);
		int infoH = fmH3.getHeight();
		int w = Math.max(msgW, infoW);
		int h = msgH + infoH;
		int xCenter = xBorder + size / 2;

		g2.setColor(getTransparentColor(new Color(255, 255, 255), 192));

		if (OPTIONS[SIMEPLE_SHAPES]) {
			g2.fillRect(xCenter - w / 2 - 20, getHeight() / 2 - msgH - 5,
					w + 40, h + 10);
		} else {
			g2.fillRoundRect(xCenter - w / 2 - 20, getHeight() / 2 - msgH - 5,
					w + 40, h + 10, 20, 20);
		}

		h = getHeight() / 2 - 5;
		g2.setFont(h1);
		g2.setColor(getPlayerColor(endColor));
		g2.drawString(msg, xCenter - msgW / 2, h);

		h += msgH - 10;
		g2.setFont(h3);
		g2.setColor(Color.BLACK);
		g2.drawString(info, xCenter - infoW / 2, h);

	}

	private void paintStaticComponents(Graphics2D g2) {

		// hintergrundbild zeichnen
		// g2.drawImage(bgBoard, xBorder, yBorder, size, size, this);

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

	}

	private void paintDynamicComponents(Graphics2D g2) {

		// rahmen
		g2.setColor(currentPlayer == null ? Color.LIGHT_GRAY
				: getPlayerColor(currentPlayer));
		g2.fillRect(0, 0, getWidth(), BORDER_SIZE);
		g2.fillRect(0, getHeight() - BORDER_SIZE, getWidth(), BORDER_SIZE);

		g2.fillRect(0, 0, BORDER_SIZE, getHeight());
		g2.fillRect(getWidth() - BORDER_SIZE, 0, BORDER_SIZE, getHeight());

		for (Flower flower : gameState.getFlowers()) {
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

		// highlights fuer erreichbare knoten zeichnen
		if (currentSheep != null) {
			Color c = currentSheep.owner == currentPlayer ? getPlayerColor(currentSheep.owner)
					: Color.BLACK;

			if (highliteNode && myturn && !onlyObserving
					&& currentSheep.owner == currentPlayer
					&& currentNeighbours.contains(currentNode)) {

				g2.setColor(getTransparentColor(c, 128));
				GUINode currentGUINode = guiNodes[currentNode];
				g2.fillPolygon(currentGUINode.getScaledXs(), currentGUINode
						.getScaledYs(), currentGUINode.size());
			}

			g2.setColor(c);
			if (currentNeighbours != null && currentSheep.owner != null) {
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
				return sheepMap.get(o1).y < sheepMap.get(o2).y ? 1 : -1;
			}

		});

		// huete malen
		g2.setFont(getFont());
		if (sheepMap != null) {
			for (Sheep sheep : sortedSheeps) {
				if (sheep != currentSheep) {
					drawSheep(g2, sheep, false);
				}
			}
		}

		drawSheep(g2, currentSheep, true);

	}

	private void printGameStatus(Graphics2D g2) {

		int fontX = getWidth() - STATS_WIDTH + BORDER_SIZE;
		int fontY = 42;

		g2.setFont(h1);
		g2.setColor(Color.BLACK);
		g2.drawString(TITLE, fontX, fontY);

		fontY += 20;
		g2.setFont(h4);
		g2.drawString(
				gameState.getTotalFlowers() + " Blumen auf dem Spielfeld",
				fontX, fontY);

		fontY += 20;
		g2.drawString("Runde " + (gameState.getTurn() + 1) + " von "
				+ Constants.TURN_LIMIT + " Runden", fontX, fontY);

		fontY += 35;
		g2.setFont(h2);
		String name = gameState.getPlayerNames()[0];
		g2.setColor(PLAYER1_COLOR);
		if (currentPlayer == PlayerColor.PLAYER1) {
			name += " ist am Zug";
		}
		g2.drawString(name, fontX, fontY);
		g2.setColor(Color.BLACK);
		fontY = drawPlayerInfo(g2, fontY, fontX, PlayerColor.PLAYER1);

		fontY += 35;
		g2.setFont(h2);
		name = gameState.getPlayerNames()[1];
		g2.setColor(PLAYER2_COLOR);
		if (currentPlayer == PlayerColor.PLAYER2) {
			name += " ist am Zug";
		}
		g2.drawString(name, fontX, fontY);
		g2.setColor(Color.BLACK);
		fontY = drawPlayerInfo(g2, fontY, fontX, PlayerColor.PLAYER2);

		int i = 0;
		fontY += 15;
		if (OPTIONS[SIMEPLE_SHAPES]) {
			for (Die dice : gameState.getDice()) {
				drawSimpleDie(g2, fontX + 60 * i++, fontY, dice.value);
			}
		} else {
			for (Die dice : gameState.getDice()) {
				drawDie(g2, fontX + 60 * i++, fontY, dice.value);
			}
		}

		fontY += 50;
		g2.setColor(Color.BLACK);
		g2.setFont(h4);
		if (currentSheep != null && currentSheep.owner != null) {
			int ownSheeps = currentSheep.getSize(currentSheep.owner);
			int opponentSheeps = currentSheep.getSize(currentSheep.owner
					.oponent());
			int flowers = currentSheep.getFlowers();

			fontY += 25;
			g2.drawString(ownSheeps + " eigene" + (ownSheeps == 1 ? "s" : "")
					+ " Schaf" + (ownSheeps == 1 ? "" : "e"), fontX, fontY);

			fontY += 20;
			g2.drawString(opponentSheeps + " gegnerische"
					+ (opponentSheeps == 1 ? "s" : "") + " Schaf"
					+ (opponentSheeps == 1 ? "" : "e"), fontX, fontY);

			fontY += 20;
			g2.drawString(flowers + " eingesammelte Blume"
					+ (Math.abs(flowers) != 1 ? "n" : ""), fontX, fontY);

			if (currentSheep.getDogState() == DogState.PASSIVE) {
				fontY += 25;
				g2.drawString("Der Schäferhund ist passiv", fontX, fontY);
			} else if (currentSheep.getDogState() == DogState.ACTIVE) {
				fontY += 20;
				g2.drawString("Der Schäferhund ist aktiv", fontX, fontY);
			}

		}

		if (hasFocus()) {
			fontY = getHeight() - BORDER_SIZE - 5;
			g2.setFont(hSheep);
			g2.setColor(Color.DARK_GRAY);
			g2.drawString("Leertaste für Einstellungen", fontX, fontY);
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

		int[] stats = gameState.getGameStats(player);

		fontY += 20;
		g2.setFont(h4);
		g2.drawString(stats[1] + " Schafe im Spiel", fontX, fontY);

		fontY += 20;
		g2.drawString(stats[2] + " Schafe gestohlen", fontX, fontY);

		fontY += 20;
		g2.drawString(stats[3] + " Schafe gefangen", fontX, fontY);

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

		fontY += 25;
		g2.setFont(h3);
		g2.drawString(stats[6] + " Punkte", fontX, fontY);

		return fontY;

	}

	private Color getTransparentColor(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(),
				OPTIONS[TRANSPARANCY] ? alpha : 255);
	}

	private Color getPlayerColor(PlayerColor player) {
		Color color;

		switch (player) {
		case PLAYER1:
			color = PLAYER1_COLOR;
			break;
		case PLAYER2:
			color = PLAYER2_COLOR;
			break;

		default:
			color = Color.DARK_GRAY;
		}

		return color;
	}

	private void drawSheep(Graphics2D g2, Sheep sheep, boolean highlight) {

		if (sheep != null) {

			Point p = sheepMap.get(sheep);
			// FIXME: warum kann p == null auftreten?
			if ((currentSheep != null && sheep == currentSheep) || p == null) {
				p = new Point(mousex, mousey);
			}

			int spread = (sheep.getDogState() != null)
					&& (sheep.getSize(PlayerColor.PLAYER1)
							+ sheep.getSize(PlayerColor.PLAYER2) > 0) ? 5 : 0;

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

				g2
						.setColor(sheep.owner == currentPlayer ? getPlayerColor(sheep.owner)
								: Color.DARK_GRAY);
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

			if (sheep.owner != null) {
				int ownSheeps = sheep.getSize(sheep.owner);
				int opponentSheeps = sheep.getSize(sheep.owner.oponent());
				int flowers = sheep.getFlowers();

				g2.setFont(hSheep);
				String stat = ownSheeps + ", " + opponentSheeps + ", "
						+ flowers;
				int statsW = mfSheep.stringWidth(stat);
				int statsH = mfSheep.getHeight();

				if (OPTIONS[SIMEPLE_SHAPES]) {
					g2
							.setColor(sheep.owner == currentPlayer ? getPlayerColor(sheep.owner)
									: Color.DARK_GRAY);
					g2.fillRect(p.x - statsW / 2 - 4, p.y + 10, statsW + 8,
							statsH);
				} else {
					g2
							.setColor(sheep.owner == currentPlayer ? getPlayerColor(sheep.owner)
									: Color.DARK_GRAY);
					g2.fillRoundRect(p.x - statsW / 2 - 4, p.y + 10,
							statsW + 8, statsH, 8, 8);

					g2.setColor(Color.BLACK);
					g2.setStroke(new BasicStroke(1.5f));
					g2.drawRoundRect(p.x - statsW / 2 - 4, p.y + 10,
							statsW + 8, statsH, 8, 8);
				}

				g2
						.setColor(sheep.getDogState() == DogState.ACTIVE ? Color.YELLOW
								: Color.WHITE);
				g2.drawString(stat, p.x - statsW / 2, p.y + 10 + statsH - 3);
			}
		}

	}

}
