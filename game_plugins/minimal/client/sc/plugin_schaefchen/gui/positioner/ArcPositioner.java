package sc.plugin_schaefchen.gui.positioner;

import sc.plugin_schaefchen.GUINode;

public class ArcPositioner implements Positioner {

	private int x1, x2, y1, y2;
	private final GUINode node;

	private int[] xs;
	private int[] ys;
	private int n = 3;

	public ArcPositioner(GUINode node) {
		this.node = node;
		createScaledPositions(n);
	}

	@Override
	public void scale(int size) {

		x1 = node.getScaledXs()[1];
		y1 = node.getScaledYs()[1];

		x2 = node.getScaledXs()[4];
		y2 = node.getScaledYs()[4];

		createScaledPositions(n);

	}

	private void createScaledPositions(int n) {
		this.n = n;
		xs = new int[n];
		ys = new int[n];

		for (int i = 0; i < n; i++) {
			xs[i] = x1 + (2 * i + 1) * (x2 - x1) / (2 * n);
			ys[i] = y1 + (2 * i + 1) * (y2 - y1) / (2 * n);
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
