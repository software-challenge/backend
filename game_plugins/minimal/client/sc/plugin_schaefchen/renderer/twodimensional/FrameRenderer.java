/**
 * 
 */
package sc.plugin_schaefchen.renderer.twodimensional;

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
import java.util.HashSet;
import java.util.LinkedList;
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
import sc.plugin_schaefchen.renderer.IRenderer;
import sc.plugin_schaefchen.util.Constants;
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
	private int test;

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
		
		List<GUINode> guiNodeList = BoardFactory.createGUINodes();
		guiNodes = new GUINode[guiNodeList.size()];
		for (GUINode node : guiNodeList) {
			guiNodes[node.index] = node;
		}

		test = guiNodeList.size();
		
		// image = new ImageIcon("bg3.jpeg").getImage();
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);
		// this.nodes = nodes;
		sheepMap = new HashMap<Sheep, Point>(7);

//		createHatMap();
		repaint();
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
	private FontMetrics mfdice = getFontMetrics(hdice);

	private FontMetrics fm = getFontMetrics(getFont());

	private GUINode[] guiNodes;

	private Node currentNode;
	private Dimension dim;
	private int mousey;
	private int mousex;
	private Set<Integer> currentNeighbours;
	private Sheep currentSheep;

	// private final Image image;

	private Move move;

	private class Point {
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private Map<Sheep, Point> sheepMap;
	private int size;

	private int xBorder;
	private int yBorder;
	private List<Node> nodes;

	private synchronized void createSheepMap() {

		sheepMap.clear();
		Map<Integer,Set<Sheep>> map = new HashMap<Integer, Set<Sheep>>();
		for (Sheep sheep : board.getSheeps()){
			if(!map.containsKey(sheep.getNode())){
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

	@Override
	public void paint(Graphics g) {

		if (board != null) {
			nodes = new LinkedList<Node>();
			for (Node node : board.getNodes()) {
				nodes.add(node);
			}
		}

		// TODO: resize erkennen
		if (true) {

			int width = getWidth() - Constants.STATS_WIDTH - 2
					* Constants.BORDER_SIZE;
			int heigth = getHeight() - 2 * Constants.BORDER_SIZE;

			size = Math.min(width, heigth);
			xBorder = (width - size) / 2 + Constants.BORDER_SIZE;
			yBorder = (heigth - size) / 2 + Constants.BORDER_SIZE;

			dim = getSize();
			for (GUINode guiNode : guiNodes) {
				guiNode.scale(size, xBorder, yBorder);
			}

		}

	//	createHatMap();

		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		FontMetrics metrics = getFontMetrics(getFont());
		// g2.drawImage(image, xBorder, yBorder, size, size, this);

		
		
		for (GUINode guiNode : guiNodes) {
			switch (guiNode.getNodeType()) {

			case GRASS:
				break;

			case HOME1:
				g2.setColor(new Color(255, 32, 32, 128));
				g2.fillPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(), guiNode
						.size());
				break;

			case HOME2:
				g2.setColor(new Color(32, 32, 255, 128));
				g2.fillPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(), guiNode
						.size());
				break;

			case FENCE:
				g2.setColor(new Color(32, 192, 32, 128));
				g2.fillPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(), guiNode
						.size());
				break;
			}

		}

		// index von jedem spielfeld zeichnen
		for (GUINode guiNode : guiNodes) {
			g2.drawString(guiNode.index + "", guiNode.getScaledCenterX(), guiNode
					.getScaledCenterY());
		}

		
		// rahmen fuer jedes spielfeld zeichnen
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(2f));
		for (GUINode guiNode : guiNodes) {
			g2.drawPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(), guiNode.size());

		}
		
		// taler fuer jeden node zeichnen
		if(board != null){
			for (Node node : board.getNodes()){
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

		if (currentSheep != null) {
			Color c = currentSheep.owner.getColor();

			if (currentNode != null && currentSheep.owner == currentPlayer.getPlayerColor()
					&& currentNeighbours.contains(currentNode.index)) {
				g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
						128));
				GUINode currentGUINode = guiNodes[currentNode.index];
				g2.fillPolygon(currentGUINode.getScaledXs(), currentGUINode
						.getScaledYs(), currentGUINode.size());
			}

			g2.setColor(c);
			if (currentNeighbours != null
					&& currentSheep.getPlayerColor() != PlayerColor.NOPLAYER) {
				for (Integer n : currentNeighbours) {
					GUINode guiNode = guiNodes[n];
					g2.setStroke(new BasicStroke(3.5f));
					g2.drawPolygon(guiNode.getScaledXs(), guiNode.getScaledYs(), guiNode
							.size());
				}
			}
		}

		int fontX = getWidth() - Constants.STATS_WIDTH + Constants.BORDER_SIZE;
		int fontY = 42;

		if(board != null){
		int i = 0;
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
		fontY += 100;
		fontY += 25;
		g2.drawString(guiNodes.length + "", fontX, fontY);
		fontY += 25;
		g2.drawString(test + "", fontX, fontY);

		// huete malen
     	g2.setFont(getFont());
			if(board!=null){
				createSheepMap();
				for (Sheep sheep : board.getSheeps()){
					if(sheep != currentSheep){
					drawSheep(g2, sheep);
					}
				}
			}
		
		drawSheep(g2, currentSheep);

	}

	private void drawSheep(Graphics2D g2, Sheep sheep) {

		if (sheep != null) {

			Point p;
			synchronized (sheepMap) {
				p = sheepMap.get(sheep);					
			}
			if (currentSheep != null && sheep == currentSheep) {
				p.x = mousex;
				p.y = mousey;
			}

			g2.setColor(sheep.hasSharpSheepdog() ? Color.YELLOW.darker().darker()
					: Color.BLACK);
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

			g2.setColor(sheep.owner.getColor());
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

		synchronized (sheepMap){
		for (Sheep sheep : sheepMap.keySet()) {
			Point p = sheepMap.get(sheep);
			if (Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)) < 12) {
				currentSheep = sheep;
				if(!sheep.owner.equals(PlayerColor.NOPLAYER)){
				currentNeighbours = board.getValideMoves(sheep.index).keySet();
				}
				repaint();
				break;
			}
		}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (currentSheep != null) {
			if (currentSheep != null && currentNode != null && move == null) {
				if (currentSheep.getPlayerColor() != PlayerColor.NOPLAYER
						&& currentSheep.owner == currentPlayer.getPlayerColor()
						&& currentNeighbours.contains(currentNode.index)) {
					move = new Move(currentSheep.index, currentNode.index);
					sendMove(move);
				}
			}

			currentSheep = null;
			currentNode = null;
			currentNeighbours = null;
			repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (currentSheep != null) {
		currentNode = null;

		if (currentSheep.owner.equals(
				currentPlayer.getPlayerColor())) {
			for (Integer n : currentNeighbours) {
				Node node = board.getNode(n);
				if (guiNodes[n].inner(e.getX(), e.getY())) {
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
