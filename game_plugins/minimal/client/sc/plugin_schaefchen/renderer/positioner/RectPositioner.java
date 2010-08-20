package sc.plugin_schaefchen.renderer.positioner;

import sc.plugin_schaefchen.GUINode;

public class RectPositioner implements Positioner {

	private int x1, x2, y1, y2;
	private final GUINode node;

	private int[] xs;
	private int[] ys;
	private int n = 3;

	public RectPositioner(GUINode node) {
		this.node = node;
		createScaledPositions(n);
	}

	public void scale(int size) {

		x1 = (node.getScaledXs()[0] + node.getScaledXs()[1]) / 2;
		y1 = (node.getScaledYs()[0] + node.getScaledYs()[1]) / 2;

		x2 = (node.getScaledXs()[2] + node.getScaledXs()[3]) / 2;
		y2 = (node.getScaledYs()[2] + node.getScaledYs()[3]) / 2;

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