package sc.plugin2011.gui.positioner;

import sc.plugin2011.GUINode;

public class SquarePositioner implements Positioner {

	private double r;
	private final GUINode node;

	private int size;
	private int[] xs;
	private int[] ys;
	private int n = 3;

	public SquarePositioner(GUINode node) {
		this.node = node;
		createScaledPositions(n);

	}

	public void scale(int size) {
		this.size = size;
		r = 1.0 / 2.0 * Math.sqrt(Math.pow(node.getCenterX() - node.getXs()[0],
				2)
				+ Math.pow(node.getCenterY() - node.getYs()[0], 2));
		createScaledPositions(n);

	}

	private void createScaledPositions(int n) {
		this.n = n;
		this.xs = new int[n];
		this.ys = new int[n];

		for (int i = 0; i < n; i++) {
			this.xs[i] = node.getScaledCenterX()
					+ (int) ((((n > 1) ? (size * r * Math.cos(2.0 * i * Math.PI
							/ n)) : 0)));
			this.ys[i] = node.getScaledCenterY()
					+ (int) ((((n > 1) ? (size * r * Math.sin(2.0 * i * Math.PI
							/ n)) : 0)));
		}
	}

	@Override
	public int[] scaledXs(int n) {
		if (n != this.n) {
			createScaledPositions(n);
		}
		return xs.clone();
	}

	@Override
	public int[] scaledYs(int n) {
		if (n != this.n) {
			createScaledPositions(n);
		}
		return ys.clone();
	}

}