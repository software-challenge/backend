package sc.plugin2011;

import sc.plugin2011.gui.positioner.Positioner;

/**
 * 
 * geometrische informationen ueber ein spielfeld
 * 
 * @author tkra
 * 
 */

public final class GUINode {

	static boolean simple = false;

	// ein zur form passender positionierungsalgorithmus fuer
	// auf diesem feld liegende objekte
	private Positioner positioner;

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

	// diskrete lage skalierte eckpunkte
	private final int[] scaledXs;
	private final int[] scaledYs;

	// diskrete lage skalierte eckpunkte
	private final int[] scaledSimpleXs;
	private final int[] scaledSimpleYs;

	// diskrete lage des skalierten zentrums
	private int scaledCenterX;
	private int scaledCenterY;

	// typ dieses spielfeldes
	private NodeType type;

	// eikndeutige nummer dieses spielfeldes
	public final int index;

	public static void setSimple(boolean simple) {
		GUINode.simple = simple;
	}

	public GUINode(final double[] xs, final double[] ys, int n, double centerX,
			double centerY, final double[] simpleXs, final double[] simpleYs,
			int simpleN, int index, NodeType type) {

		this.n = n;
		this.simpleN = simpleN;
		this.index = index;
		this.xs = xs.clone();
		this.ys = ys.clone();

		this.simpleXs = simpleXs.clone();
		this.simpleYs = simpleYs.clone();
		this.type = type;

		scaledXs = new int[n];
		scaledYs = new int[n];

		scaledSimpleXs = new int[n];
		scaledSimpleYs = new int[n];

		this.centerX = centerX;
		this.centerY = centerY;

		scale(100, 100, 10);
	}

	/*
	 * setzt den positionierungsalgorithmus
	 */
	public void setPositioner(final Positioner positioner) {
		this.positioner = positioner;
	}

	/*
	 * liefert die anzahl der eckpunkte deses spielfeldes
	 */
	public int size() {
		return simple ? simpleN : n;
	}

	/*
	 * liefert die x-werte der lagen der eckpunkte im kontinuierlichen
	 * koordinatenbereich
	 */
	public double[] getXs() {
		return xs;
	}

	/*
	 * liefert die y-werte der lagen der eckpunkte im kontinuierlichen
	 * koordinatenbereich
	 */
	public double[] getYs() {
		return ys;

	}

	/*
	 * liefert den x-werte der lage des mittelpunktes im kontinuierlichen
	 * koordinatenbereich
	 */
	public double getCenterX() {
		return centerX;
	}

	/*
	 * liefert den y-werte der lage des mittelpunktes im kontinuierlichen
	 * koordinatenbereich
	 */
	public double getCenterY() {
		return centerY;
	}

	/*
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

		for (int i = 0; i < simpleN; i++) {
			scaledSimpleXs[i] = xBorder + (int) (simpleXs[i] * size);
			scaledSimpleYs[i] = yBorder + (int) (simpleYs[i] * size);
		}

		scaledCenterX = xBorder + (int) (centerX * size);
		scaledCenterY = yBorder + (int) (centerY * size);
		if (positioner != null)
			positioner.scale(size);

	}

	/*
	 * liefert die x-werte der lagen der eckpunkte im diskreten
	 * koordinatenbereich
	 */
	public int[] getScaledXs() {
		return simple ? scaledSimpleXs : scaledXs;
	}

	/*
	 * liefert die y-werte der lagen der eckpunkte im diskreten
	 * koordinatenbereich
	 */
	public int[] getScaledYs() {
		return simple ? scaledSimpleYs : scaledYs;

	}

	/*
	 * liefert den x-wert der lagen des mittelpunktes im diskreten
	 * koordinatenbereich
	 */
	public int getScaledCenterX() {
		return scaledCenterX;
	}

	/*
	 * liefert den y-wert der lagen des mittelpunktes im diskreten
	 * koordinatenbereich
	 */
	public int getScaledCenterY() {
		return scaledCenterY;
	}

	/*
	 * liefert die x-wert der lagen der mittelpunkte von n objekten die auf
	 * diesem spielfeld positioniert werden sollen im diskreten
	 * koordinatenbereich
	 */
	public int[] getScaledXPositions(int n) {
		return positioner.scaledXs(n);
	}

	/*
	 * liefert die y-wert der lagen der mittelpunkte von n objekten die auf
	 * diesem spielfeld positioniert werden sollen im diskreten
	 * koordinatenbereich
	 */
	public int[] getScaledYPositions(int n) {
		return positioner.scaledYs(n);
	}

	/*
	 * prueft ob ein im diskreten koordinatenbereich gegebener püunkt innerhalb
	 * dieses spielfeldes ist
	 */
	public boolean inner(int x, int y) {

		boolean inner = true;
		double scalar;
		double ref = 0;

		if (simple) {
			for (int i = 0; i < simpleN; i++) {
				int j = (i + 1) % simpleN;
				scalar = (scaledSimpleYs[j] - scaledSimpleYs[i])
						* (x - scaledSimpleXs[i])
						+ (scaledSimpleXs[i] - scaledSimpleXs[j])
						* (y - scaledSimpleYs[i]);

				if (i == 0) {
					ref = Math.signum(scalar);
				}

				if (Math.signum(scalar) != ref) {
					inner = false;
					break;
				}
			}

			return inner;

		} else {
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
	}

	/*
	 * liefert den spielfeldtyp dieses spielfeldes
	 */
	public NodeType getNodeType() {
		return type;
	}
}