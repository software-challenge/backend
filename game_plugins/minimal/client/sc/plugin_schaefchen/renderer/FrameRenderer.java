/**
 * 
 */
package sc.plugin_schaefchen.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import sc.plugin_schaefchen.Board;
import sc.plugin_schaefchen.BoardFactory;
import sc.plugin_schaefchen.GUINode;
import sc.plugin_schaefchen.IGameHandler;
import sc.plugin_schaefchen.Move;
import sc.plugin_schaefchen.Node;
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

	private final static int BORDER_SIZE = 5;
	private static final String TITLE = "Schäfchen ins Trockene";
	private final int STATS_WIDTH = getFontMetrics(h1).stringWidth(TITLE) + 2
			* BORDER_SIZE;

	// local instances of current players and board
	private Player currentPlayer;
	private Board board;

	// We also have a game handler to know whats going on
	private IGameHandler handler;

	// Am I only observing? Then don't show any controls or such
	private boolean onlyObserving;

	// Am I currently visible?
	private boolean showing;
	private boolean myturn;
	private int round;

	private Font hdice = new Font(getFont().getName(), Font.PLAIN, 33);
	private FontMetrics mfdice = getFontMetrics(hdice);

	private FontMetrics fm = getFontMetrics(getFont());

	private GUINode[] guiNodes;

	private boolean highliteNode;
	private int currentNode;
	private int mousey;
	private int mousex;
	private Set<Integer> currentNeighbours;
	private Sheep currentSheep;

	// private final Image image;

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

			if (!onlyObserving) {

				int x = e.getX();
				int y = e.getY();

				for (Sheep sheep : sheepMap.keySet()) {
					Point p = sheepMap.get(sheep);
					if (Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)) < 12) {
						currentSheep = sheep;
						if (!sheep.owner.equals(PlayerColor.NOPLAYER)) {
							currentNeighbours = board.getValidReachableNodes(
									sheep.index).keySet();
						}
						repaint();
						break;
					}

				}

			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (!onlyObserving) {

				if (currentSheep != null) {
					if (currentSheep != null && highliteNode) {
						if (currentSheep.getPlayerColor() != PlayerColor.NOPLAYER
								&& currentSheep.owner == currentPlayer
										.getPlayerColor()
								&& currentNeighbours.contains(currentNode)) {

							if (myturn) {
								// wenn zug abgeschickt werden soll aktuelln hut
								// nicht zurueckfallen lassen
								Point p = sheepMap.get(currentSheep);
								p.x = mousex;
								p.y = mousey;
								sendMove(new Move(currentSheep.index,
										currentNode));
							}
						}
					}

					currentSheep = null;
					highliteNode = false;
					currentNeighbours = null;
					repaint();
				}
			}
		}
	};

	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!onlyObserving) {
				if (currentSheep != null) {

					if (currentSheep.owner.equals(currentPlayer
							.getPlayerColor())) {
						for (Integer n : currentNeighbours) {
							Node node = board.getNode(n);
							if (guiNodes[n].inner(e.getX(), e.getY())) {
								currentNode = node.index;
								highliteNode = true;
								break;
							}
						}
					}

					mousex = e.getX();
					mousey = e.getY();

					repaint();
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {

			mousex = e.getX();
			mousey = e.getY();

		}
	};

	public FrameRenderer(final IGameHandler handler, final boolean onlyObserving) {
		this.handler = handler;
		this.onlyObserving = onlyObserving;
		// image = new ImageIcon("bg3.jpeg").getImage();

		highliteNode = false;
		sheepMap = new HashMap<Sheep, Point>(4 * Constants.SHEEPS_AT_HOME + 1);
		List<GUINode> guiNodeList = BoardFactory.createGUINodes();
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
		this.currentPlayer = currentPlayer;
		repaint();
	}

	@Override
	public void updateBoard(Board board, int round) {
		if (this.board != board) {
			this.board = board;
		}

		createSheepMap();
		this.round = round;
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
	}

	@Override
	public void gameError(String errorMessage) {
	}

	@Override
	public void updateChat(String chatMsg) {
	}

	private void sendMove(Move move) {
		if (myturn) {
			handler.sendAction(move);
			myturn = false;
		}
	}

	private void createSheepMap() {

		sheepMap.clear();
		if (board != null) {
			Map<Integer, Set<Sheep>> map = new HashMap<Integer, Set<Sheep>>();
			for (Sheep sheep : board.getSheeps()) {
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

		g2.setColor(getPlayerColor(currentPlayer.getPlayerColor()));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(getBackground());
		g2.fillRect(BORDER_SIZE, BORDER_SIZE, getWidth() - 2 * BORDER_SIZE,
				getHeight() - 2 * BORDER_SIZE);

		paintStaticComponents(g2);
		if (board != null) {
			paintDynamicComponents(g2);
			printGameStatus(g2);
		}

	}

	private void paintStaticComponents(Graphics2D g2) {

		// hintergrundbil zeichnen
		// g2.drawImage(image, xBorder, yBorder, size, size, this);

		// flaechig gefuellte spielfelder zeichnen
		for (GUINode guiNode : guiNodes) {
			switch (guiNode.getNodeType()) {

			case DOGHOUSE:
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

			case FENCE:
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

		// TODO: icons zeichnen
		// blumen fuer jeden node zeichnen
		FontMetrics metrics = getFontMetrics(getFont());
		if (board != null) {
			for (Node node : board.getNodes()) {
				GUINode guiNode = guiNodes[node.index];
				if (node.getFlowers() != 0) {
					int k = 10;
					String gold = Integer.toString(node.getFlowers());
					g2.setColor(Color.YELLOW);
					g2.fillOval(guiNode.getScaledCenterX() - k, guiNode
							.getScaledCenterY()
							- k, 2 * k, 2 * k);
					g2.setColor(Color.YELLOW.darker().darker().darker());
					g2.drawOval(guiNode.getScaledCenterX() - k, guiNode
							.getScaledCenterY()
							- k, 2 * k, 2 * k);
					g2.drawString(gold, guiNode.getScaledCenterX()
							- metrics.stringWidth(gold) / 2, guiNode
							.getScaledCenterY()
							+ metrics.getHeight() / 2 - 2);
				}

			}
		}

		// highlights fuer erreichbare knoten zeichnen
		if (currentSheep != null && myturn) {
			Color c = getPlayerColor(currentSheep.owner);

			if (highliteNode
					&& currentSheep.owner == currentPlayer.getPlayerColor()
					&& currentNeighbours.contains(currentNode)) {
				g2.setColor(getTransparentColor(c));
				GUINode currentGUINode = guiNodes[currentNode];
				g2.fillPolygon(currentGUINode.getScaledXs(), currentGUINode
						.getScaledYs(), currentGUINode.size());
			}

			g2.setColor(c);
			if (currentNeighbours != null
					&& currentSheep.getPlayerColor() != PlayerColor.NOPLAYER) {
				for (Integer n : currentNeighbours) {
					GUINode guiNode = guiNodes[n];
					g2.setStroke(new BasicStroke(3.5f));
					g2.drawPolygon(guiNode.getScaledXs(),
							guiNode.getScaledYs(), guiNode.size());
				}
			}
		}

		// huete malen
		g2.setFont(getFont());
		if (sheepMap != null) {
			for (Sheep sheep : board.getSheeps()) {
				if (sheep != currentSheep) {
					drawSheep(g2, sheep);
				}
			}
		}

		drawSheep(g2, currentSheep);

	}

	private void printGameStatus(Graphics2D g2) {

		int fontX = getWidth() - STATS_WIDTH - BORDER_SIZE;
		int fontY = 42;

		g2.setFont(h1);
		g2.setColor(Color.BLACK);
		g2.drawString(TITLE, fontX, fontY);

		fontY += 20;
		g2.setFont(h4);
		g2.drawString(board.getTotalFlowers() + " Blumen auf dem Spielfeld",
				fontX, fontY);

		fontY += 20;
		g2.drawString("Runde " + (round + 1) + " von " + Constants.TURN_LIMIT,
				fontX, fontY);

		fontY += 35;
		g2.setFont(h2);
		String name = "Spieler 1";
		g2.setColor(PLAYER1_COLOR);
		if (currentPlayer.getPlayerColor() == PlayerColor.PLAYER1) {
			name += " ist am Zug";
		}
		g2.drawString(name, fontX, fontY);
		g2.setColor(Color.BLACK);
		fontY = drawPlayerInfo(g2, fontY, fontX, PlayerColor.PLAYER1);

		fontY += 35;
		g2.setFont(h2);
		name = "Spieler 2";
		g2.setColor(PLAYER2_COLOR);
		if (currentPlayer.getPlayerColor() == PlayerColor.PLAYER2) {
			name += " ist am Zug";
		}
		g2.drawString(name, fontX, fontY);
		g2.setColor(Color.BLACK);
		fontY = drawPlayerInfo(g2, fontY, fontX, PlayerColor.PLAYER2);

		int i = 0;
		fontY += 10;
		g2.setFont(hdice);
		for (Integer dice : board.getDice()) {
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRoundRect(fontX + 60 * i, fontY, 50, 50, 15, 15);
			g2.setColor(Color.DARK_GRAY);
			String value = dice.toString();
			g2.drawString(value, fontX + 25 + 60 * i
					- mfdice.stringWidth(value) / 2, fontY - 7
					+ mfdice.getHeight());
			i++;
		}

	}

	private int drawPlayerInfo(Graphics2D g2, int fontY, int fontX,
			PlayerColor player) {

		int[] stats = board.getGameStats(player);

		fontY += 20;
		g2.setFont(h4);
		g2.drawString(stats[0] + " Schafe im Spiel", fontX, fontY);

		fontY += 20;
		g2.drawString(stats[1] + " Schafe gefangen", fontX, fontY);

		fontY += 20;
		g2.drawString(stats[2] + " Schafe gesichert", fontX, fontY);

		fontY += 25;
		g2.drawString(stats[3] + " Blumen gesammelt", fontX, fontY);

		fontY += 20;
		g2.drawString(stats[4] + " Blumen gesichert", fontX, fontY);

		fontY += 25;
		g2.setFont(h3);
		g2.drawString(stats[5] + " Punkte", fontX, fontY);

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
		case NOPLAYER:
			color = Color.YELLOW.darker();
			break;
		default:
			color = Color.GRAY;
		}

		return color;
	}

	private void drawSheep(Graphics2D g2, Sheep sheep) {

		if (sheep != null) {

			Point p;

			p = sheepMap.get(sheep);

			if (currentSheep != null && sheep == currentSheep) {
				p = new Point(mousex, mousey);
			}

			g2.setColor(sheep.hasSharpSheepdog() ? Color.YELLOW.darker()
					.darker() : Color.BLACK);
			g2.fillArc(p.x - 15, p.y - 15, 30, 30, 0, 360);

			GUINode target = guiNodes[sheep.getTarget()];
			if (!sheep.owner.equals(PlayerColor.NOPLAYER)) {
				double phiR = Math.atan((float) (p.x - target
						.getScaledCenterX())
						/ (p.y - target.getScaledCenterY()));
				phiR += ((p.y >= target.getScaledCenterY()) ? 1 : -1) * Math.PI
						/ 2;

				double phiD = 2 * Math.PI * 9 * phiR;
				g2.setStroke(new BasicStroke(5f));
				g2.fillArc(p.x - 20, p.y - 20, 40, 40, (int) (phiD - 10), 20);
			}

			g2.setColor(getPlayerColor(sheep.owner));
			g2.fillArc(p.x - 12, p.y - 12, 24, 24, 0, 360);

			g2.setColor(Color.BLACK);
			String gold = Integer.toString(sheep.getFlowers());
			String size = Integer.toString(sheep.getSize().getSize());
			if (currentSheep != null && sheep.index == currentSheep.index) {
				String s1 = sheep.getSize().toString() + " Hütchen", s2 = gold
						+ " Taler";
				g2.drawString(s1, p.x - fm.stringWidth(s1) / 2, p.y + 35);
				g2.drawString(s2, p.x - fm.stringWidth(s2) / 2, p.y + 35
						+ fm.getHeight());

			} else {
				String s = size + "/" + gold;
				g2.drawString(s, p.x - fm.stringWidth(s) / 2, p.y
						+ fm.getHeight() / 2 - 3);
			}

		}

	}

}
