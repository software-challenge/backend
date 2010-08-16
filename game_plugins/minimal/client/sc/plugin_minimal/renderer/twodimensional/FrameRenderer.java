/**
 * 
 */
package sc.plugin_minimal.renderer.twodimensional;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import sc.plugin_minimal.Board;
import sc.plugin_minimal.Sheep;
import sc.plugin_minimal.Node;
import sc.plugin_minimal.PlayerColor;
import sc.plugin_minimal.IGameHandler;
import sc.plugin_minimal.Move;
import sc.plugin_minimal.Player;
import sc.plugin_minimal.renderer.IRenderer;
import sc.plugin_minimal.util.Constants;
import sc.shared.GameResult;

/**
 * @author ffi, sca, tkra
 * 
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JPanel implements IRenderer, MouseListener,
		MouseMotionListener {
	// local instances of current players and board
	private Player currentPlayer;
	private Player nextPlayer;
	private Board board;

	// We also have a game handler to know whats going on
	private IGameHandler handler;

	// Am I only observing? Then don't show any controls or such
	private boolean onlyObserving;

	// Am I currently visible?
	private boolean showing;
	private boolean myturn;
	private int round;

	public FrameRenderer(final IGameHandler handler, final boolean onlyObserving) {
		this.handler = handler;
		this.onlyObserving = onlyObserving;

		createInitFrame();
	}

	public void shown() {
		showing = true;
	}

	public void hidden() {
		showing = false;
	}

	/**
	 * Create the GUI the first time Add all GUI elements to the panel here
	 */
	private void createInitFrame() {
		setDoubleBuffered(true);

		// image = new ImageIcon("bg3.jpeg").getImage();
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);
		// this.nodes = nodes;
		hatMap = new HashMap<Sheep, Point>(7);

		createHatMap();
		repaint();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				if (showing) {
					repaint();
				}
			}
		});
	}

	/**
	 * Player got updated, let the gui react to it
	 */
	@Override
	public void updatePlayer(final Player currentPlayer, final Player nextPlayer) {
		this.currentPlayer = currentPlayer;
		this.nextPlayer = nextPlayer;
		repaint();
	}

	/**
	 * Board got updated, update local things
	 */
	@Override
	public void updateBoard(Board board, int round) {
		if (this.board != board) {
			this.board = board;
		}
		this.round = round;
		repaint();
	}

	/**
	 * Create Image of the current gui view
	 */
	@Override
	public Image getImage() {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
	}

	/**
	 * Send given move (using the game handler)
	 * 
	 * @param move
	 */
	private void sendMove(Move move) {
		if (myturn) {
			handler.sendAction(move);
			myturn = false;
		}
	}

	/**
	 * Move is requested
	 */
	@Override
	public void requestMove() {
		// For this example, only recognize that it's our turn
		// We don't need to disable the move button because it will be invisible
		// until next turn anyway
		myturn = true;
	}

	/**
	 * Game is over, print out game result
	 */
	@Override
	public void gameEnded(final GameResult data, PlayerColor color,
			String errorMessage) {
	}

	/**
	 * An error (in most cases client failure) happened
	 */
	@Override
	public void gameError(String errorMessage) {
	}

	/**
	 * Obviously it was planned to implement internet games and chat
	 * functionality for this server. Maybe we will do that later, for now it's
	 * unused.
	 */
	@Override
	public void updateChat(String chatMsg) {
	}

	private Font hdice = new Font(getFont().getName(), Font.PLAIN, 33);

	private FontMetrics fm = getFontMetrics(getFont());

	private List<Node> nodes = new LinkedList<Node>();

	private Node currentNode;
	private Dimension dim;
	private int mousey;
	private int mousex;
	private Map<Node, Integer> currentNeighbours;
	private Sheep currentHat;

	// private final Image image;

	private Move move;

	private class Point {
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private Map<Sheep, Point> hatMap;
	private int size;

	private int xBorder;
	private int yBorder;

	private void createHatMap() {

		hatMap.clear();
		for (Node node : nodes) {
			List<Sheep> hats = node.getSheeps();
			int n = hats.size(), i = 0;
			int[] hatXs = node.getScaledXPositions(n);
			int[] hatYs = node.getScaledYPositions(n);
			for (Sheep hat : node.getSheeps()) {
				hatMap.put(hat, new Point(hatXs[i], hatYs[i]));
				i++;
			}

		}

	}

	@Override
	public void paint(Graphics g) {

		if (board != null) {
			nodes = board.getNodes();
		}

		if (!getSize().equals(dim)) {

			int width = getWidth() - Constants.STATS_WIDTH - 2
					* Constants.BORDER_SIZE;
			int heigth = getHeight() - 2 * Constants.BORDER_SIZE;

			size = Math.min(width, heigth);
			xBorder = (width - size) / 2 + Constants.BORDER_SIZE;
			yBorder = (heigth - size) / 2 + Constants.BORDER_SIZE;

			dim = getSize();
			for (Node node : nodes) {
				node.scale(size, xBorder, yBorder);
			}

		}

		createHatMap();

		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics metrics = getFontMetrics(getFont());

		// g2.drawImage(image, xBorder, yBorder, size, size, this);

		for (Node node : nodes) {
			switch (node.getNodeType()) {

			case GRASS:
				if (node.getFlowers() != 0) {
					int k = 10;
					String gold = Integer.toString(node.getFlowers());
					g2.setColor(Color.YELLOW);
					g2.fillOval(node.getScaledCenterX() - k, node
							.getScaledCenterY()
							- k, 2 * k, 2 * k);
					g2.setColor(Color.YELLOW.darker().darker().darker());
					g2.drawOval(node.getScaledCenterX() - k, node
							.getScaledCenterY()
							- k, 2 * k, 2 * k);
					g2.drawString(gold, node.getScaledCenterX()
							- metrics.stringWidth(gold) / 2, node
							.getScaledCenterY()
							+ metrics.getHeight() / 2 - 2);
				}
				break;

			case HOME1:
				g2.setColor(new Color(255, 32, 32, 128));
				g2.fillPolygon(node.getScaledXs(), node.getScaledYs(), node
						.size());
				break;

			case HOME2:
				g2.setColor(new Color(32, 32, 255, 128));
				g2.fillPolygon(node.getScaledXs(), node.getScaledYs(), node
						.size());
				break;

			case FENCE:
				g2.setColor(new Color(32, 192, 32, 128));
				g2.fillPolygon(node.getScaledXs(), node.getScaledYs(), node
						.size());
				break;
			}

		}

		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(2f));
		for (Node node : nodes) {
			g2.drawPolygon(node.getScaledXs(), node.getScaledYs(), node.size());

		}

		if (currentHat != null) {
			Color c = currentHat.getPlayerColor().getColor();

			if (currentNode != null && currentHat.owner == currentPlayer
					&& currentNeighbours.keySet().contains(currentNode)) {
				g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
						128));
				g2.fillPolygon(currentNode.getScaledXs(), currentNode
						.getScaledYs(), currentNode.size());
			}

			g2.setColor(c);
			if (currentNeighbours != null
					&& currentHat.getPlayerColor() != PlayerColor.NOPLAYER) {
				for (Node node : currentNeighbours.keySet()) {
					g2.setStroke(new BasicStroke(3.5f));
					g2.drawPolygon(node.getScaledXs(), node.getScaledYs(), node
							.size());
				}
			}
		}

		// huete malen
		g2.setFont(getFont());
		for (Sheep hat : hatMap.keySet()) {
			if (hat != currentHat) {
				drawHat(g2, hat);
			}
		}

		drawHat(g2, currentHat);

	}

	private void drawHat(Graphics2D g2, Sheep hat) {

		if (hat != null) {

			Point p = hatMap.get(hat);
			if (currentHat != null && hat == currentHat) {
				p.x = mousex;
				p.y = mousey;
			}

			g2.setColor(hat.hasSharpSheepdog() ? Color.YELLOW.darker().darker()
					: Color.BLACK);
			g2.fillArc(p.x - 15, p.y - 15, 30, 30, 0, 360);

			Node target = hat.getTarget();
			if (target != null) {
				double phiR = Math.atan((float) (p.x - target
						.getScaledCenterX())
						/ (p.y - target.getScaledCenterY()));
				phiR += ((p.y >= target.getScaledCenterY()) ? 1 : -1) * Math.PI
						/ 2;

				double phiD = 2 * Math.PI * 9 * phiR;
				g2.setStroke(new BasicStroke(5f));
				g2.fillArc(p.x - 20, p.y - 20, 40, 40, (int) (phiD - 10), 20);
			}

			g2.setColor(hat.getPlayerColor().getColor());
			g2.fillArc(p.x - 12, p.y - 12, 24, 24, 0, 360);

			g2.setColor(Color.BLACK);
			String gold = Integer.toString(hat.getFlowers());
			String size = Integer.toString(hat.getSize().getSize());
			if (currentHat != null && hat.index == currentHat.index) {
				String s1 = hat.getSize().toString() + " Hütchen", s2 = gold
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

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {

		move = null;

		int x = e.getX();
		int y = e.getY();

		for (Sheep hat : hatMap.keySet()) {
			Point p = hatMap.get(hat);
			if (Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)) < 12) {
				currentHat = hat;
				currentNeighbours = hat.getValideMoves();
				repaint();
				break;
			}

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (currentHat != null) {
			if (currentHat != null && currentNode != null && move == null) {
				if (currentHat.getPlayerColor() != PlayerColor.NOPLAYER
						&& currentHat.owner == currentPlayer
						&& currentNeighbours.keySet().contains(currentNode)) {
					move = new Move(currentHat, currentNode);
					sendMove(move);
				}
			}

			currentHat = null;
			currentNode = null;
			currentNeighbours = null;
			repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (currentHat != null) {
			currentNode = null;

			if (currentHat.owner == currentPlayer) {
				for (Node node : currentNeighbours.keySet()) {
					if (node.inner(e.getX(), e.getY())) {
						currentNode = node;
						break;
					}
				}
			}

			mousex = e.getX();
			mousey = e.getY();

			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		mousex = e.getX();
		mousey = e.getY();

	}

	public Move getMove() {
		return move;
	}

}
