package sc.plugin_schaefchen;

import java.util.HashSet;
import java.util.Set;

import sc.plugin_schaefchen.gui.positioner.Positioner;

/*
 * knoten-hilfsklasse fuer die boardfactory
 * @author tkra
 *
 */
public final class FactoryNode {
	// index des spielfeldes
	public final int index;

	// anzahl und lage der eckpunkte
	private final int n;
	private final double[] xs;
	private final double[] ys;

	private final int simpleN;
	private final double[] simpleXs;
	private final double[] simpleYs;

	// lage des zentrums
	private double centerX;
	private double centerY;

	// positionierungsalgorithmus fuer elemente auf diesem spielfeld
	Class<? extends Positioner> positioner;

	// menge der benachbarten spielfelder
	private final Set<Integer> neighbours;

	// typ dieses spielfeldes
	private NodeType type;

	// ggf velinktes gegenstueck
	private int counterPart;

	// anzahl blumen auf diesem spielfeld
	private boolean flowers;

	private int sheepAmount;


	public FactoryNode(double[] xs, double[] ys, int n, int index) {
		this(xs, ys, n, xs.clone(), ys.clone(), n, index);
	}
	
	public FactoryNode(double[] xs, double[] ys, int n, double[] simpleXs,
			double[] simpleYs, int simpleN, int index) {
		this.n = n;
		this.simpleN = simpleN;
		this.xs = xs;
		this.ys = ys;
		this.simpleXs = simpleXs;
		this.simpleYs = simpleYs;
		this.index = index;

		type = NodeType.GRASS;
		neighbours = new HashSet<Integer>();

		// mittelpunkt berechnen
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
	}

	void setPositioner(Class<? extends Positioner> positioner) {
		this.positioner = positioner;
	}

	public double[] getXs() {
		return xs;
	}

	public double[] getYs() {
		return ys;
	}

	public double[] getSimpleXs() {
		return simpleXs;
	}

	public double[] getSimpleYs() {
		return simpleYs;
	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	/*
	 * verschiebung im kontinuierlichen koordinatenbereich
	 */
	public void translate(double x, double y) {

		for (int i = 0; i < n; i++) {
			xs[i] += x;
			ys[i] += y;
		}

		for (int i = 0; i < simpleN; i++) {
			simpleXs[i] += x;
			simpleYs[i] += y;
		}

		centerX += x;
		centerY += y;

	}

	/*
	 * skalierung im kontinuierlichen koordinatenbereich
	 */
	public void scale(double f) {

		for (int i = 0; i < n; i++) {
			xs[i] *= f;
			ys[i] *= f;
		}

		for (int i = 0; i < simpleN; i++) {
			simpleXs[i] *= f;
			simpleYs[i] *= f;
		}

		centerX *= f;
		centerY *= f;

	}

	/*
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

		for (int i = 0; i < simpleN; i++) {
			x = simpleXs[i] * cosPhi - simpleYs[i] * sinPhi;
			y = simpleXs[i] * sinPhi + simpleYs[i] * cosPhi;
			simpleXs[i] = x;
			simpleYs[i] = y;
		}

		x = centerX * cosPhi - centerY * sinPhi;
		y = centerX * sinPhi + centerY * cosPhi;
		centerX = x;
		centerY = y;

	}

	/*
	 * maximaler x-wert im kontinuierlichen koordinatenbereich
	 */
	public double maxX() {

		double max = Double.MIN_VALUE;
		for (int i = 0; i < getN(); i++) {
			if (xs[i] > max) {
				max = xs[i];
			}
		}
		return max;
	}

	/*
	 * maximaler y-wert im kontinuierlichen koordinatenbereich
	 */
	public double maxY() {

		double max = Double.MIN_VALUE;
		for (int i = 0; i < getN(); i++) {
			if (ys[i] > max) {
				max = ys[i];
			}
		}
		return max;
	}

	/*
	 * minimaler x-wert im kontinuierlichen koordinatenbereich
	 */
	public double minX() {

		double min = Double.MAX_VALUE;
		for (int i = 0; i < getN(); i++) {
			if (xs[i] < min) {
				min = xs[i];
			}
		}
		return min;
	}

	/*
	 * minimaler y-wert im kontinuierlichen koordinatenbereich
	 */
	public double minY() {

		double min = Double.MAX_VALUE;
		for (int i = 0; i < getN(); i++) {
			if (ys[i] < min) {
				min = ys[i];
			}
		}
		return min;
	}

	/*
	 * verbindet zwei gegebene spielfelder als nachbarn
	 * 
	 * @param node1
	 * @param node2
	 */
	public static void couple(final FactoryNode node1, final FactoryNode node2) {
		node1.addNeighbour(node2.index);
		node2.addNeighbour(node1.index);
	}

	/*
	 * fuegt diesem spielfeld einen nachbar hinzu
	 */
	public void addNeighbour(final int other) {
		neighbours.add(other);
	}

	/*
	 * liefert die menge der nachbarn dieses spielfeldes
	 */
	public Set<Integer> getNeighbours() {
		return neighbours;
	}

	/*
	 * setzt den spielfeldzyp fuer dieses spielfeld
	 */
	public void setNodeType(final NodeType type) {
		this.type = type;
	}

	/*
	 * liefert den spielfeldtyp dieses spielfeldes
	 */
	public NodeType getNodeType() {
		return type;
	}

	/*
	 * setzt die anzahl an bluemn
	 */
	public void setFlowers(boolean b) {
		this.flowers = b;

	}

	/*
	 * liefert die anzahl der blumen auf diesem spielfeld
	 */
	public boolean hasFlowers() {
		return flowers;
	}

	/*
	 * liefert das ggf. vorhandene gegenstueck zu diesem spielfeld
	 */
	public int getCounterPart() {
		return counterPart;
	}

	/*
	 * setzt das gegenstueck zu diesem spielfeld
	 */
	public void setCounterPart(int counterPart) {
		this.counterPart = counterPart;
	}

	public int getN() {
		return n;
	}

	public int getSimpleN() {
		return simpleN;
	}

	public void setSheeps(int amount) {
		this.sheepAmount = amount;
	}

	public int getSheeps() {
		return sheepAmount;
	}

}
