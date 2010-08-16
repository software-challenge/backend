package sc.plugin_minimal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.plugin_minimal.renderer.positioner.Positioner;

/**
 * ein spielfeld. als geometrische figur und als logisches element
 * 
 * @author tkra
 * 
 */

@XStreamAlias(value = "minimal:node")
public final class Node {

	private static int nextIndex = 0;

	// ein zur form passender positionierungsalgorithmus fuer
	// auf diesem feld liegende objekte
	private Positioner positioner;

	// anzahl und lage der eckpunkte
	private final int n;
	private final double[] xs;
	private final double[] ys;

	// lage des zentrums
	private double centerX;
	private double centerY;

	// diskrete lage skalierte eckpunkte
	private final int[] scaledXs;
	private final int[] scaledYs;

	// diskrete lage des skalierten zentrums
	private int scaledCenterX;
	private int scaledCenterY;

	// menge der benachbarten spielfelder
	private final Set<Node> neighbours;

	// liste der auf diesem spielfeld stehenden schafe
	private final List<Sheep> sheeps;

	// ggf. vorghandeses gegenstueck zu diesem feld
	// wird bei den heimatfeldern benoetigt um einem schaf sein ziel mitzuteilen
	private Node counterPart;

	// eikndeutige nummer dieses spielfeldes
	public final int index;

	// typ dieses spielfeldes
	private NodeType type;

	// anzahl an blumen die auf diesem spielfeld sind
	private int flowers;

	// referenz auf zugehoeriges spielbrett
	private Board board;

	public Node(final double[] xs, final double[] ys, int n, final  Board board) {

		this.n = n;
		this.board = board;
		this.xs = new double[n];
		this.ys = new double[n];

		scaledXs = new int[n];
		scaledYs = new int[n];

		for (int i = 0; i < n; i++) {
			this.xs[i] = xs[i];
			this.ys[i] = ys[i];
		}

		// mittelpunkt bestimmen
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
		sheeps = new LinkedList<Sheep>();

		index = nextIndex++;
		type = NodeType.GRASS;

	}

	/**
	 * setzt den positionierungsalgorithmus
	 */
	public void setPositioner(final Positioner positioner) {
		this.positioner = positioner;
	}

	/**
	 * verschiebung im kontinuierlichen koordinatenbereich
	 */
	public void translate(double x, double y) {

		for (int i = 0; i < n; i++) {
			xs[i] += x;
			ys[i] += y;
		}
		centerX += x;
		centerY += y;

	}

	/**
	 * skalierung im kontinuierlichen koordinatenbereich
	 */
	public void scale(double f) {

		for (int i = 0; i < n; i++) {
			xs[i] *= f;
			ys[i] *= f;
		}

		centerX *= f;
		centerY *= f;

	}

	/**
	 * rotation im kontinuierlichen koordinatenbereich
	 */
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

	/**
	 * maximaler x-wert im kontinuierlichen koordinatenbereich
	 */
	public double maxX() {

		double max = Double.MIN_VALUE;
		for (int i = 0; i < n; i++) {
			if (xs[i] > max) {
				max = xs[i];
			}
		}
		return max;
	}

	/**
	 * maximaler y-wert im kontinuierlichen koordinatenbereich
	 */
	public double maxY() {

		double max = Double.MIN_VALUE;
		for (int i = 0; i < n; i++) {
			if (ys[i] > max) {
				max = ys[i];
			}
		}
		return max;
	}

	/**
	 * minimaler x-wert im kontinuierlichen koordinatenbereich
	 */
	public double minX() {

		double min = Double.MAX_VALUE;
		for (int i = 0; i < n; i++) {
			if (xs[i] < min) {
				min = xs[i];
			}
		}
		return min;
	}

	/**
	 * minimaler y-wert im kontinuierlichen koordinatenbereich
	 */
	public double minY() {

		double min = Double.MAX_VALUE;
		for (int i = 0; i < n; i++) {
			if (ys[i] < min) {
				min = ys[i];
			}
		}
		return min;
	}

	/**
	 * prueft ob ein im kontinuierlichen koordinatenbereich gegebener püunkt
	 * innerhalb dieses spielfeldes ist
	 */
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

	/**
	 * liefert die anzahl der eckpunkte deses spielfeldes
	 */
	public int size() {
		return n;
	}

	/**
	 * liefert die x-werte der lagen der eckpunkte im kontinuierlichen
	 * koordinatenbereich
	 */
	public double[] getXs() {
		return xs.clone();
	}

	/**
	 * liefert die y-werte der lagen der eckpunkte im kontinuierlichen
	 * koordinatenbereich
	 */
	public double[] getYs() {
		return ys.clone();

	}

	/**
	 * liefert den x-werte der lage des mittelpunktes im kontinuierlichen
	 * koordinatenbereich
	 */
	public double getCenterX() {
		return centerX;
	}

	/**
	 * liefert den y-werte der lage des mittelpunktes im kontinuierlichen
	 * koordinatenbereich
	 */
	public double getCenterY() {
		return centerY;
	}

	/**
	 * skalierung im diskreten koordinatenbereich
	 */
	public void scale(int size, int xBorder, int yBorder) {

		int size2 = size / 50;
		size -= 2 * size2;
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

	/**
	 * liefert die x-werte der lagen der eckpunkte im diskreten
	 * koordinatenbereich
	 */
	public int[] getScaledXs() {
		return scaledXs.clone();
	}

	/**
	 * liefert die y-werte der lagen der eckpunkte im diskreten
	 * koordinatenbereich
	 */
	public int[] getScaledYs() {
		return scaledYs.clone();

	}

	/**
	 * liefert den x-wert der lagen des mittelpunktes im diskreten
	 * koordinatenbereich
	 */
	public int getScaledCenterX() {
		return scaledCenterX;
	}

	/**
	 * liefert den y-wert der lagen des mittelpunktes im diskreten
	 * koordinatenbereich
	 */
	public int getScaledCenterY() {
		return scaledCenterY;
	}

	/**
	 * liefert die x-wert der lagen der mittelpunkte von n objekten die auf
	 * diesem spielfeld positioniert werden sollen im diskreten
	 * koordinatenbereich
	 */
	public int[] getScaledXPositions(int n) {
		return positioner.scaledXs(n);
	}

	/**
	 * liefert die y-wert der lagen der mittelpunkte von n objekten die auf
	 * diesem spielfeld positioniert werden sollen im diskreten
	 * koordinatenbereich
	 */
	public int[] getScaledYPositions(int n) {
		return positioner.scaledYs(n);
	}

	/**
	 * prueft ob ein im diskreten koordinatenbereich gegebener püunkt innerhalb
	 * dieses spielfeldes ist
	 */
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

	/**
	 * verbindet zwei gegebene spielfelder als nachbarn
	 * 
	 * @param node1
	 * @param node2
	 */
	public static void couple(final Node node1, final Node node2) {
		node1.addNeighbour(node2);
		node2.addNeighbour(node1);
	}

	/**
	 * fuegt diesem spielfeld einen nachbar hinzu
	 */
	public void addNeighbour(final Node other) {
		neighbours.add(other);
	}

	/**
	 * liefert die menge der nachbarn dieses spielfeldes
	 */
	public Set<Node> getNeighbours() {
		return new HashSet<Node>(neighbours);
	}

	/**
	 * liefert die menge der (indirekten) nachbar dieses spielfeldes im abstand
	 * dist
	 */
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

	/*
	 * fuegt einen menge von spielfeldern ihre (indirekten) nachbarn im abstand
	 * dist hinzu
	 */
	private void addNeighbours(int dist, final Node origin, final Set<Node> set) {
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

	/**
	 * setzt den spielfeldzyp fuer dieses spielfeld
	 */
	public void setNodeType(final NodeType type) {
		this.type = type;
	}

	/**
	 * liefert den spielfeldtyp dieses spielfeldes
	 */
	public NodeType getNodeType() {
		return type;
	}

	/**
	 * fuegt diesem spielfeld blumen hinzu
	 */
	public void addFlowers(int flowers) {
		this.flowers += flowers;
	}

	/**
	 * liefert die anzahl der blumen auf diesem spielfeld
	 */
	public int getFlowers() {
		return flowers;
	}

	/**
	 * setzt ein schaf auf dieses spielfeld
	 */
	public void addSheep(final Sheep sheep) {
		sheeps.add(sheep);
	}

	/**
	 * entfernt ein schaf von diesem spielfeld
	 */
	public void removeSheep(final Sheep sheep) {
		sheeps.remove(sheep);
	}

	/**
	 * liefert die lieste der sich auf diesem spielfeld befindlichen schafe
	 */
	public List<Sheep> getSheeps() {
		return new LinkedList<Sheep>(sheeps);
	}

	/**
	 * liefert das zugehoerige spielfeld
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * liefert das ggf. vorhandene gegenstueck zu diesem spielfeld
	 */
	public Node getCounterPart() {
		return counterPart;
	}

	/**
	 * setzt das gegenstueck zu diesem spielfeld
	 */
	public void setCounterPart(Node counterPart) {
		this.counterPart = counterPart;
	}

}