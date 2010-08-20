/**
 * 
 */
package sc.plugin_schaefchen.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
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
import sc.plugin_schaefchen.GameState;
import sc.plugin_schaefchen.GUINode;
import sc.plugin_schaefchen.IGameHandler;
import sc.plugin_schaefchen.Move;
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

	private static final Color HOME1_COLOR = new Color(255, 32, 32, 128);
	private static final Color HOME2_COLOR = new Color(32, 32, 255, 128);
	private static final Color FENCE_COLOR = new Color(32, 192, 32, 128);

	private static final Color PLAYER2_COLOR = Color.BLUE;
	private static final Color PLAYER1_COLOR = Color.RED;

	private static final Font h1 = new Font("Helvetica", Font.BOLD, 25);
	private static final Font h2 = new Font("Helvetica", Font.BOLD, 20);
	private static final Font h3 = new Font("Helvetica", Font.BOLD, 14);
	private static final Font h4 = new Font("Helvetica", Font.PLAIN, 14);
	private static final Font hSheep = new Font("Helvetica", Font.PLAIN, 10);
	private static final Font hDice = new Font("Helvetica", Font.BOLD, 33);

	private final FontMetrics fmH1 = getFontMetrics(h1);
	// private final FontMetrics fmH2 = getFontMetrics(h2);
	private final FontMetrics fmH3 = getFontMetrics(h3);
	// private final FontMetrics fmH4 = getFontMetrics(h4);
	private final FontMetrics mfSheep = getFontMetrics(hSheep);
	private final FontMetrics mfDice = getFontMetrics(hDice);

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
	private int turn;

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

	private final Image sheepIcon;
	private final Image dogIcon;
	private final Image mushroomIcon;
	private final Image flower1Icon;
	private final Image flower2Icon;

	private Map<Sheep, Point> sheepMap;
	private int size;

	private int xBorder;
	private int yBorder;

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

	private MouseListener mouseListener = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {

			mousex = e.getX();
			mousey = e.getY();
			for (Sheep sheep : sheepMap.keySet()) {
				Point p = sheepMap.get(sheep);
				if (Math.sqrt(Math.pow(p.x - mousex, 2) + Math.pow(p.y - mousey, 2)) < 20
						&& !sheep.owner.equals(PlayerColor.NOPLAYER)) {
					currentSheep = sheep;
					if (!sheep.owner.equals(PlayerColor.NOPLAYER)) {
						currentNeighbours = gameState.getValidReachableNodes(
								sheep.index).keySet();
					}
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
				if (currentSheep.owner != PlayerColor.NOPLAYER
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

	};

	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {

		@Override
		public void mouseDragged(MouseEvent e) {

			mousex = e.getX();
			mousey = e.getY();
			highliteNode = false;
			if (currentSheep != null) {

				if (currentSheep.owner.equals(currentPlayer)) {
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
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseMotionListener);
		addComponentListener(componentListener);
		setFocusable(true);

		resizeBoard();
		repaint();

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
		this.turn = gameState.getTurn();
		this.currentPlayer = gameState.getCurrentPlayer().getPlayerColor();
		this.ended = false;
		repaint();
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
		endColor = PlayerColor.NOPLAYER;
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
		endColor = PlayerColor.NOPLAYER;
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

		createSheepMap();
	}

	@Override
	public void paint(Graphics g) {

		if (!showing) {
			return;
		}

		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(currentPlayer == null ? Color.LIGHT_GRAY
				: getPlayerColor(currentPlayer));
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.drawImage(bgBoard, BORDER_SIZE, BORDER_SIZE, getWidth() - 2
				* BORDER_SIZE, getHeight() - 2 * BORDER_SIZE, this);

		g2.setColor(new Color(200, 240, 200, 160));
		g2.fillRect(getWidth() - BORDER_SIZE - STATS_WIDTH, BORDER_SIZE,
				STATS_WIDTH, getHeight() - 2 * BORDER_SIZE);

		paintStaticComponents(g2);
		if (gameState != null) {
			printGameStatus(g2);
			paintDynamicComponents(g2);
		}

		if (ended) {
			String msg;
			switch (endColor) {
			case PLAYER1:
				msg = gameState.getPlayerNames()[0] + " hat gewonnen!";
				break;
			case PLAYER2:
				msg = gameState.getPlayerNames()[1] + " hat gewonnen!";
				break;
			default:
				msg = "Das Spiel ist zu Ende";
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

			g2.setColor(new Color(255, 255, 255, 160));
			g2.fillRoundRect(xCenter - w / 2 - 20, getHeight() / 2 - msgH - 5,
					w + 40, h + 10, 20, 20);

			h = getHeight() / 2 - 5;
			g2.setFont(h1);
			g2.setColor(getPlayerColor(endColor));
			g2.drawString(msg, xCenter - msgW / 2, h);

			h += msgH - 10;
			g2.setFont(h3);
			g2.setColor(Color.BLACK);
			g2.drawString(info, xCenter - infoW / 2, h);

		}

	}

	private void paintStaticComponents(Graphics2D g2) {

		// hintergrundbild zeichnen
		// g2.drawImage(bgBoard, xBorder, yBorder, size, size, this);

		// flaechig gefuellte spielfelder zeichnen
		for (GUINode guiNode : guiNodes) {
			switch (guiNode.getNodeType()) {

			case GRASS:
				break;

			case HOME1:
				g2.setColor(HOME1_COLOR);
				g2.fillPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(),
						guiNode.size());
				break;

			case HOME2:
				g2.setColor(HOME2_COLOR);
				g2.fillPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(),
						guiNode.size());
				break;

			case SAVE:
				g2.setColor(FENCE_COLOR);
				g2.fillPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(),
						guiNode.size());
				break;
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
			Color c = currentSheep.owner.equals(currentPlayer) ? getPlayerColor(currentSheep.owner)
					: Color.BLACK;

			if (highliteNode && myturn && !onlyObserving
					&& currentSheep.owner == currentPlayer
					&& currentNeighbours.contains(currentNode)) {
				g2.setColor(getTransparentColor(c));
				GUINode currentGUINode = guiNodes[currentNode];
				g2.fillPolygon(currentGUINode.getScaledXs(), currentGUINode
						.getScaledYs(), currentGUINode.size());
			}

			g2.setColor(c);
			if (currentNeighbours != null
					&& currentSheep.owner != PlayerColor.NOPLAYER) {
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
		g2.drawString("Runde " + (turn + 1) + " von "
				+ (Constants.TURN_LIMIT - 1), fontX, fontY);

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
		g2.setFont(hDice);
		for (Die dice : gameState.getDice()) {
			g2.setColor(Color.GRAY);
			g2.fillRoundRect(fontX + 60 * i, fontY, 50, 50, 15, 15);
			g2.setColor(getPlayerColor(currentPlayer));
			String value = Integer.toString(dice.value);
			g2.drawString(value, fontX + 23 + 60 * i
					- mfDice.stringWidth(value) / 2, fontY - 2
					+ mfDice.getHeight());

			i++;
		}

		if (currentSheep != null
				&& !currentSheep.owner.equals(PlayerColor.NOPLAYER)) {
			int ownSheeps = currentSheep.getSize(currentSheep.owner);
			int opponentSheeps = currentSheep.getSize(currentSheep.owner
					.oponent());
			int flowers = currentSheep.getFlowers();

			fontY += 75;
			g2.setColor(Color.BLACK);
			g2.setFont(h4);
			g2.drawString(ownSheeps + " eigene" + (ownSheeps == 1 ? "s" : "")
					+ " Schaf" + (ownSheeps == 1 ? "" : "e"), fontX, fontY);

			fontY += 20;
			g2.drawString(opponentSheeps + " gegnerische"
					+ (opponentSheeps == 1 ? "s" : "") + " Schaf"
					+ (opponentSheeps == 1 ? "" : "e"), fontX, fontY);

			fontY += 20;
			g2.drawString(flowers + " eingesammelte Blume"
					+ (Math.abs(flowers) != 1 ? "n" : ""), fontX, fontY);

			if (currentSheep.getDogState() != DogState.NONE) {
				fontY += 25;
				g2.drawString("In Begleitung des Schäferhundes", fontX, fontY);
			}
			if (currentSheep.getDogState() == DogState.ACTIVE) {
				fontY += 20;
				g2.drawString("Der Schäferhund ist aktiv", fontX, fontY);
			}

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

	private Color getTransparentColor(Color c) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 128);
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

			int spread = (sheep.getDogState() != DogState.NONE)
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
			if (!sheep.owner.equals(PlayerColor.NOPLAYER)) {
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
						.setColor(sheep.owner.equals(currentPlayer) ? getPlayerColor(sheep.owner)
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

			if (!sheep.owner.equals(PlayerColor.NOPLAYER)) {
				g2.drawImage(sheepIcon, p.x - spread - SHEEP_SIZE / 2, p.y
						- SHEEP_SIZE / 2, SHEEP_SIZE, SHEEP_SIZE, this);
			}

			if (sheep.getDogState() == DogState.ACTIVE) {
				g2.drawImage(dogIcon, p.x + spread - DOG_SIZE / 2, p.y
						- DOG_SIZE / 2, DOG_SIZE, DOG_SIZE, this);
			}

			if (!sheep.owner.equals(PlayerColor.NOPLAYER)) {
				int ownSheeps = sheep.getSize(sheep.owner);
				int opponentSheeps = sheep.getSize(sheep.owner.oponent());
				int flowers = sheep.getFlowers();

				g2.setFont(hSheep);
				String stat = ownSheeps + ", " + opponentSheeps + ", "
						+ flowers;
				int statsW = mfSheep.stringWidth(stat);
				int statsH = mfSheep.getHeight();

				g2
						.setColor(sheep.owner.equals(currentPlayer) ? getPlayerColor(sheep.owner)
								: Color.DARK_GRAY);
				g2.fillRoundRect(p.x - statsW / 2 - 4, p.y + 10, statsW + 8,
						statsH, 8, 8);

				g2.setColor(Color.BLACK);
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawRoundRect(p.x - statsW / 2 - 4, p.y + 10, statsW + 8,
						statsH, 8, 8);

				g2
						.setColor(sheep.getDogState() == DogState.ACTIVE ? Color.YELLOW
								: Color.WHITE);
				g2.drawString(stat, p.x - statsW / 2, p.y + 10 + statsH - 3);
			}
		}

	}

}
