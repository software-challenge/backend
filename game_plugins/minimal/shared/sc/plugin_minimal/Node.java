package sc.plugin_minimal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sc.plugin_minimal.renderer.positioner.Positioner;

public class Node {

	private static int nextIndex = 0;
	
	private static int totalGold = 0;

	private Positioner positioner;

	private final int n;
	private final double[] xs;
	private final double[] ys;

	private double centerX;
	private double centerY;

	private final int[] scaledXs;
	private final int[] scaledYs;

	private int scaledCenterX;
	private int scaledCenterY;

	private final Set<Node> neighbours;
	private final List<Hat> hats;
	
	private Node counterPart;
	

	public Node getCounterPart() {
		return counterPart;
	}

	public void setCounterPart(Node counterPart) {
		this.counterPart = counterPart;
	}

	public final int index;
	private NodeType type;
	private int gold;

	public Node(double[] xs, double[] ys, int n) {

		this.n = n;
		this.xs = new double[n];
		this.ys = new double[n];

		scaledXs = new int[n];
		scaledYs = new int[n];

		for (int i = 0; i < n; i++) {
			this.xs[i] = xs[i];
			this.ys[i] = ys[i];
		}

		double a = 0.0;
		double cx = 0.0;
		double cy = 0.0;
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			a += xs[i] * ys[j] - xs[j] * ys[i];
			cx += (xs[i] + xs[j]) * (xs[i] * ys[j] - xs[j] * ys[i]);
			cy += (ys[i] + ys[j]) * (xs[i] * ys[j] - xs[j] * ys[i]);
		}
		centerX = cx / (3 * a);
		centerY = cy / (3 * a);

		scale(100, 100, 10);

		neighbours = new HashSet<Node>();
		hats = new LinkedList<Hat>();

		index = nextIndex++;
		type = NodeType.NORM;



	}

	public void setPositioner(Positioner positioner) {
		this.positioner = positioner;
	}

	public void translate(double x, double y) {

		for (int i = 0; i < n; i++) {
			xs[i] += x;
			ys[i] += y;
		}
		centerX += x;
		centerY += y;

	}

	public void scale(double f) {

		for (int i = 0; i < n; i++) {
			xs[i] *= f;
			ys[i] *= f;
		}

		centerX *= f;
		centerY *= f;

	}
	
	
	public void rotate(double phi) {
		double cosPhi = Math.cos(phi);
		double sinPhi = Math.sin(phi);
		double x, y;

		for (int i = 0; i < n; i++) {
			x = xs[i] * cosPhi - ys[i] * sinPhi;
			y = xs[i] * sinPhi + ys[i] * cosPhi;
			xs[i] = x;
			ys[i] = y;
		}

		x = centerX * cosPhi - centerY * sinPhi;
		y = centerX * sinPhi + centerY * cosPhi;
		centerX = x;
		centerY = y;

	}
	
	
	public double maxX() {

		double max = Double.MIN_VALUE;
		for (int i = 0; i < n; i++) {
			if (xs[i] > max) {
				max = xs[i];
			}
		}
		return max;
	}

	public double maxY() {

		double max = Double.MIN_VALUE;
		for (int i = 0; i < n; i++) {
			if (ys[i] > max) {
				max = ys[i];
			}
		}
		return max;
	}

	public double minX() {

		double min = Double.MAX_VALUE;
		for (int i = 0; i < n; i++) {
			if (xs[i] < min) {
				min = xs[i];
			}
		}
		return min;
	}

	public double minY() {

		double min = Double.MAX_VALUE;
		for (int i = 0; i < n; i++) {
			if (ys[i] < min) {
				min = ys[i];
			}
		}
		return min;
	}

	public boolean inner(double x, double y) {

		boolean inner = true;

		for (int i = 0; i < 4; i++) {
			int j = (i + 1) % 4;
			inner = inner
					&& (ys[j] - ys[i]) * (x - xs[i]) + (xs[i] - xs[j])
							* (y - ys[i]) < 0;
		}

		return inner;

	}

	public int size() {
		return n;
	}

	public double[] getXs() {
		return xs.clone();
	}

	public double[] getYs() {
		return ys.clone();

	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public void scale(int size, int xBorder, int yBorder) {


		int size2 = size / 50;
		size -= 2*size2;
		xBorder += size2;
		yBorder += size2;

		for (int i = 0; i < n; i++) {
			scaledXs[i] = xBorder + (int) (xs[i] * size);
			scaledYs[i] = yBorder + (int) (ys[i] * size);

		}

		scaledCenterX = xBorder + (int) (centerX * size);
		scaledCenterY = yBorder + (int) (centerY * size);
		if (positioner != null)
			positioner.scale(size);

	}

	public int[] getScaledXs() {
		return scaledXs.clone();
	}

	public int[] getScaledYs() {
		return scaledYs.clone();

	}

	public int getScaledCenterX() {
		return scaledCenterX;
	}

	public int getScaledCenterY() {
		return scaledCenterY;
	}

	public int[] getScaledXPositions(int n) {
		return positioner.scaledXs(n);
	}

	public int[] getScaledYPositions(int n) {
		return positioner.scaledYs(n);
	}

	public boolean inner(int x, int y) {

		boolean inner = true;
		double scalar;
		double ref = 0;

		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			scalar = (scaledYs[j] - scaledYs[i]) * (x - scaledXs[i])
					+ (scaledXs[i] - scaledXs[j]) * (y - scaledYs[i]);

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

	public static void couple(Node node1, Node node2) {
		node1.addNeighbour(node2);
		node2.addNeighbour(node1);
	}

	public void addNeighbour(Node other) {
		neighbours.add(other);
	}

	public Set<Node> getNeighbours() {
		return new HashSet<Node>(neighbours);
	}

	public Set<Node> getNeighbours(int dist) {

		Set<Node> set = new HashSet<Node>();
		if (dist == 0) {
			set.add(this);
		} else {
			for (Node node : neighbours) {
				node.addNeighbours(Math.abs(dist) - 1, this, set);
			}
		}

		return set;

	}

	private void addNeighbours(int dist, Node origin, Set<Node> set) {
		if (dist == 0) {
			set.add(this);
		} else if (dist > 0) {
			for (Node node : neighbours) {
				if (node != origin) {
					node.addNeighbours(dist - 1, this, set);
				}
			}
		}

	}

	public void setNodeType(NodeType type) {
		this.type = type;
	}

	public NodeType getNodeType() {
		return type;
	}

	public void addGold(int gold) {
		this.gold += gold;
		totalGold += gold;
	}

	public int getGold() {
		return gold;
	}
	
	public static int getTotalGold() {
		return totalGold;
	}

	public void addHat(Hat hat) {
		hats.add(hat);
	}
	
	public void removeHat(Hat hat) {
		hats.remove(hat);
	}
	
	public List<Hat> getHats() {
		return new LinkedList<Hat>(hats);
	}

	public static void reset() {
		totalGold = 0;
		
	}






}