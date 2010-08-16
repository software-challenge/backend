package sc.plugin_minimal;

import sc.plugin_minimal.renderer.positioner.Positioner;


/**
 * ein spielfeld. als geometrische figur und als logisches element
 * 
 * @author tkra
 * 
 */

public final class GUINode {

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
	
	// typ dieses spielfeldes
	private NodeType type;

	// eikndeutige nummer dieses spielfeldes
	public final int index;

	public GUINode(final double[] xs, final double[] ys, double centerX,
			double centerY, int n, int index, NodeType type) {

		this.n = n;
		this.index = index;
		this.xs = new double[n];
		this.ys = new double[n];
		this.type = type;
		
		scaledXs = new int[n];
		scaledYs = new int[n];

		for (int i = 0; i < n; i++) {
			this.xs[i] = xs[i];
			this.ys[i] = ys[i];
		}

		this.centerX = centerX;
		this.centerY = centerY;

		scale(100, 100, 10);
	}

	/**
	 * setzt den positionierungsalgorithmus
	 */
	public void setPositioner(final Positioner positioner) {
		this.positioner = positioner;
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
	 * liefert den spielfeldtyp dieses spielfeldes
	 */
	public NodeType getNodeType() {
		return type;
	}
}