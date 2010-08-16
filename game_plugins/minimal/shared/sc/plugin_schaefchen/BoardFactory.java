package sc.plugin_schaefchen;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import sc.plugin_schaefchen.renderer.positioner.ArcPositioner;
import sc.plugin_schaefchen.renderer.positioner.BasePositioner;
import sc.plugin_schaefchen.renderer.positioner.Positioner;
import sc.plugin_schaefchen.renderer.positioner.RectPositioner;
import sc.plugin_schaefchen.renderer.positioner.SquarePositioner;
import sc.plugin_schaefchen.util.Constants;

/**
 * generiert das spielfeld als gerichteten graphen, befuellt felder mit talern
 * und setzt initiale huete
 * 
 * @author tkra
 * */
public class BoardFactory {

	private static class FactoryNode {

		public final int index;

		private int flowers;
		// anzahl und lage der eckpunkte
		private final int n;
		private final double[] xs;
		private final double[] ys;

		// lage des zentrums
		private double centerX;
		private double centerY;
		Class<? extends Positioner> positioner;

		// menge der benachbarten spielfelder
		private final Set<Integer> neighbours;

		// typ dieses spielfeldes
		private NodeType type;

		private int counterPart;

		public FactoryNode(double[] xs, double[] ys, int n, int index) {
			this.n = n;
			this.xs = xs;
			this.ys = ys;
			this.index = index;

			type = NodeType.GRASS;
			neighbours = new HashSet<Integer>();
			
			//mittelpunkt berechnen
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
		 * verbindet zwei gegebene spielfelder als nachbarn
		 * 
		 * @param node1
		 * @param node2
		 */
		public static void couple(final FactoryNode node1,
				final FactoryNode node2) {
			node1.addNeighbour(node2.index);
			node2.addNeighbour(node1.index);
		}

		/**
		 * fuegt diesem spielfeld einen nachbar hinzu
		 */
		public void addNeighbour(final int other) {
			neighbours.add(other);
		}

		/**
		 * liefert die menge der nachbarn dieses spielfeldes
		 */
		public Set<Integer> getNeighbours() {
			return neighbours;
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
		 * liefert das ggf. vorhandene gegenstueck zu diesem spielfeld
		 */
		public int getCounterPart() {
			return counterPart;
		}

		/**
		 * setzt das gegenstueck zu diesem spielfeld
		 */
		public void setCounterPart(int counterPart) {
			this.counterPart = counterPart;
		}

	}

	private static class Index {
		private int index = 0;

		public int next() {
			return index++;
		}
	}

	public static List<Node> createNodes() {

		List<FactoryNode> factoryNodes = createFactoryNodes();
		List<Node> nodes = new ArrayList<Node>(factoryNodes.size());

		for (FactoryNode factoryNode : factoryNodes) {
			Node node = new Node(factoryNode.getNodeType(), factoryNode
					.getCounterPart(), factoryNode.index);
			node.addFlowers(factoryNode.getFlowers());
			for (Integer n : factoryNode.getNeighbours()) {
				node.addNeighbour(n);
			}
			nodes.add(node);
		}

		return nodes;

	}

	public static List<GUINode> createGUINodes() {

		List<FactoryNode> factoryNodes = createFactoryNodes();
		List<GUINode> nodes = new ArrayList<GUINode>(factoryNodes.size());

		for (FactoryNode factoryNode : factoryNodes) {
			GUINode guiNode = new GUINode(factoryNode.xs, factoryNode.ys,
					factoryNode.centerX, factoryNode.centerY, factoryNode.n,
					factoryNode.index, factoryNode.getNodeType());

			try {
				// FIXME: scheint ein wenig umstaendlicher weg zu sein
				Positioner positioner = factoryNode.positioner.getConstructor(
						GUINode.class).newInstance(guiNode);
				guiNode.setPositioner(positioner);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			nodes.add(guiNode);
		}

		return nodes;

	}

	private static List<FactoryNode> createFactoryNodes() {

		Index index = new Index();
		List<FactoryNode> nodes = new LinkedList<FactoryNode>();

		FactoryNode center = new FactoryNode(new double[] { -1, 1, 1, -1 },
				new double[] { -1, -1, 1, 1 }, 4, index.next());
		center.setPositioner(SquarePositioner.class);
		center.setNodeType(NodeType.DOGHOUSE);

		FactoryNode top = new FactoryNode(new double[] { -1, 1, 1, -1 },
				new double[] { -1, -1, 1, 1 }, 4, index.next());
		top.setPositioner(SquarePositioner.class);
		top.translate(0, -7);

		FactoryNode bottom = new FactoryNode(new double[] { -1, 1, 1, -1 },
				new double[] { -1, -1, 1, 1 }, 4, index.next());
		bottom.setPositioner(SquarePositioner.class);
		bottom.translate(0, 7);

		FactoryNode left = new FactoryNode(new double[] { -1, 1, 1, -1 },
				new double[] { -1, -1, 1, 1 }, 4, index.next());
		left.setPositioner(SquarePositioner.class);
		left.translate(-7, 0);

		FactoryNode right = new FactoryNode(new double[] { -1, 1, 1, -1 },
				new double[] { -1, -1, 1, 1 }, 4, index.next());
		right.setPositioner(SquarePositioner.class);
		right.translate(7, 0);

		nodes.add(center);
		nodes.add(top);
		nodes.add(bottom);
		nodes.add(left);
		nodes.add(right);

		int straightLength = Constants.STRAIGHT_LENGTH;
		int straightSaveDist = Constants.STRAIGHT_SAVE_DIST;
		int arcSize = Constants.ARC_LENGTH;
		int arcSaveDist = Constants.ARC_SAVE_DIST;
		int arcSaveDist2 = Constants.ARC_SAVE_DIST2;

		boolean arcSave = Constants.USE_ARC_SAVE;
		boolean straightSave = Constants.USE_STRAIGHT_SAVE;

		List<FactoryNode> centerTop = getStraightLink(1, 1, 6, -1,
				straightLength, index);
		nodes.addAll(centerTop);
		if (straightSave)
			centerTop.get(straightSaveDist).setNodeType(NodeType.FENCE);
		FactoryNode.couple(center, centerTop.get(0));
		FactoryNode.couple(centerTop.get(straightLength - 1), top);
		for (FactoryNode node : centerTop) {
			node.rotate(3 * Math.PI / 2);
		}

		List<FactoryNode> centerBottom = getStraightLink(1, 1, 6, -1,
				straightLength, index);
		nodes.addAll(centerBottom);
		if (straightSave)
			centerBottom.get(straightSaveDist).setNodeType(NodeType.FENCE);
		FactoryNode.couple(center, centerBottom.get(0));
		FactoryNode.couple(centerBottom.get(straightLength - 1), bottom);
		for (FactoryNode node : centerBottom) {
			node.rotate(Math.PI / 2);
		}

		List<FactoryNode> centerLeft = getStraightLink(1, 1, 6, -1,
				straightLength, index);
		nodes.addAll(centerLeft);
		if (straightSave)
			centerLeft.get(straightSaveDist).setNodeType(NodeType.FENCE);
		FactoryNode.couple(center, centerLeft.get(0));
		FactoryNode.couple(centerLeft.get(straightLength - 1), left);
		for (FactoryNode node : centerLeft) {
			node.rotate(Math.PI);
		}

		List<FactoryNode> centerRight = getStraightLink(1, 1, 6, -1,
				straightLength, index);
		nodes.addAll(centerRight);
		FactoryNode.couple(center, centerRight.get(0));
		FactoryNode.couple(centerRight.get(straightLength - 1), right);
		if (straightSave)
			centerRight.get(straightSaveDist).setNodeType(NodeType.FENCE);

		List<FactoryNode> rightBottom = getArcLink(1, 1, 5, 7, 0, Math.PI / 2,
				arcSize, index);
		nodes.addAll(rightBottom);
		if (arcSave) {
			rightBottom.get(arcSaveDist).setNodeType(NodeType.FENCE);
			rightBottom.get(arcSaveDist2).setNodeType(NodeType.FENCE);
		}
		FactoryNode.couple(right, rightBottom.get(0));
		FactoryNode.couple(rightBottom.get(arcSize - 1), bottom);

		List<FactoryNode> bottomLeft = getArcLink(1, 1, 5, 7, 0, Math.PI / 2,
				arcSize, index);
		nodes.addAll(bottomLeft);
		if (arcSave) {
			bottomLeft.get(arcSaveDist).setNodeType(NodeType.FENCE);
			bottomLeft.get(arcSaveDist2).setNodeType(NodeType.FENCE);
		}
		FactoryNode.couple(bottom, bottomLeft.get(0));
		FactoryNode.couple(bottomLeft.get(arcSize - 1), left);
		for (FactoryNode node : bottomLeft) {
			node.rotate(Math.PI / 2);
		}

		List<FactoryNode> leftTop = getArcLink(1, 1, 5, 7, 0, Math.PI / 2,
				arcSize, index);
		nodes.addAll(leftTop);
		if (arcSave) {
			leftTop.get(arcSaveDist2).setNodeType(NodeType.FENCE);
			leftTop.get(arcSaveDist).setNodeType(NodeType.FENCE);
		}
		FactoryNode.couple(left, leftTop.get(0));
		FactoryNode.couple(leftTop.get(arcSize - 1), top);
		for (FactoryNode node : leftTop) {
			node.rotate(Math.PI);
		}

		List<FactoryNode> topRight = getArcLink(1, 1, 5, 7, 0, Math.PI / 2,
				arcSize, index);
		nodes.addAll(topRight);
		if (arcSave) {
			topRight.get(arcSaveDist).setNodeType(NodeType.FENCE);
			topRight.get(arcSaveDist2).setNodeType(NodeType.FENCE);
		}
		FactoryNode.couple(top, topRight.get(0));
		FactoryNode.couple(topRight.get(arcSize - 1), right);
		for (FactoryNode node : topRight) {
			node.rotate(3 * Math.PI / 2);
		}

		FactoryNode rightBase = getBaseNode(index);
		nodes.add(rightBase);
		rightBase.setPositioner(BasePositioner.class);
		rightBase.setNodeType(NodeType.HOME2);
		FactoryNode.couple(right, rightBase);
		rightBase.translate(8, 0);

		FactoryNode bottomBase = getBaseNode(index);
		nodes.add(bottomBase);
		bottomBase.setPositioner(BasePositioner.class);
		bottomBase.setNodeType(NodeType.HOME1);
		FactoryNode.couple(bottom, bottomBase);
		bottomBase.translate(8, 0);
		bottomBase.rotate(Math.PI / 2);

		FactoryNode leftBase = getBaseNode(index);
		nodes.add(leftBase);
		leftBase.setPositioner(BasePositioner.class);
		leftBase.setNodeType(NodeType.HOME2);
		FactoryNode.couple(left, leftBase);
		leftBase.translate(8, 0);
		leftBase.rotate(Math.PI);

		FactoryNode topBase = getBaseNode(index);
		nodes.add(topBase);
		topBase.setPositioner(BasePositioner.class);
		topBase.setNodeType(NodeType.HOME1);
		FactoryNode.couple(top, topBase);
		topBase.translate(8, 0);
		topBase.rotate(3 * Math.PI / 2);

		for (FactoryNode node : nodes) {
			node.rotate(Math.PI / 4);
		}

		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		for (FactoryNode node : nodes) {
			if (node.minX() < minX)
				minX = node.minX();
			if (node.minY() < minY)
				minY = node.minY();
		}

		for (FactoryNode node : nodes) {
			node.translate(-minX, -minY);
		}

		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		for (FactoryNode node : nodes) {
			if (node.maxX() > maxX)
				maxX = node.maxX();
			if (node.maxY() > maxY)
				maxY = node.maxY();
		}

		for (FactoryNode node : nodes) {
			node.scale(1 / Math.max(maxX, maxY));
		}

		topBase.setCounterPart(bottomBase.index);
		bottomBase.setCounterPart(topBase.index);
		leftBase.setCounterPart(rightBase.index);
		rightBase.setCounterPart(leftBase.index);

		int gold = 0, min = Constants.MIN_GOLD;
		int max = Constants.MAX_GOLD, total = Constants.TOTAL_GOLD;
		List<FactoryNode> goldenNodes = new ArrayList<FactoryNode>(nodes.size());
		for (FactoryNode node : nodes) {
			if (node.getNodeType() == NodeType.GRASS && node != center) {
				goldenNodes.add(node);
				node.addFlowers(min);
				gold += min;
			}
		}

		Random rand = new Random();
		while (gold < total && goldenNodes.size() > 0) {
			FactoryNode n = goldenNodes.get(rand.nextInt(goldenNodes.size()));
			n.addFlowers(1);

			if (n.getFlowers() == max) {
				goldenNodes.remove(n);
			}

			gold++;
		}

		return nodes;

	}

	private static FactoryNode getBaseNode(final Index index) {

		int n = 25;
		double[] xs = new double[n];
		double[] ys = new double[n];

		double phi = Math.PI / 6;
		double phi_cover = 2 * Math.PI - 2 * phi;
		double phi_incr = phi_cover / (n - 1);

		double dx = 2 * Math.cos(phi);

		for (int i = 0; i < n; i++) {
			xs[i] = dx - 2 * Math.cos(phi);
			ys[i] = 2 * Math.sin(phi);
			phi += phi_incr;
		}

		return new FactoryNode(xs, ys, n, index.next());

	}

	private static List<FactoryNode> getStraightLink(double x1, double y1,
			double x2, double y2, int n, final Index index) {

		double xa, xb;
		double x_incr = (x2 - x1) / n;
		List<FactoryNode> nodes = new ArrayList<FactoryNode>(n);

		for (int i = 0; i < n; i++) {
			xa = x1 + i * x_incr;
			xb = x1 + (i + 1) * x_incr;

			if (i == -1)
				xb = x2;

			FactoryNode node = new FactoryNode(new double[] { xa, xb, xb, xa },
					new double[] { y1, y1, y2, y2 }, 4, index.next());
			nodes.add(node);
			node.setPositioner(RectPositioner.class);

		}

		for (int i = 1; i < n; i++) {
			FactoryNode.couple(nodes.get(i - 1), nodes.get(i));
		}

		return nodes;
	}

	private static List<FactoryNode> getArcLink(double center_x,
			double center_y, double r_i, double r_a, double phi_start,
			double phi_end, int n, final Index index) {

		double phi_incr = Math.abs(phi_start - phi_end) / (2 * n);
		double phi, phi2, phi3;
		List<FactoryNode> nodes = new ArrayList<FactoryNode>(n);

		for (int i = 0; i < n; i++) {

			phi = phi_start + (2 * i) * phi_incr;
			phi2 = phi + phi_incr;
			phi3 = phi2 + phi_incr;

			if (i == n - 1) {
				phi3 = phi_end;
			}

			FactoryNode node = new FactoryNode(new double[] {
					(center_x + r_i * Math.cos(phi)),
					(center_x + r_i * Math.cos(phi2)),
					(center_x + r_i * Math.cos(phi3)),
					(center_x + r_a * Math.cos(phi3)),
					(center_x + r_a * Math.cos(phi2)),
					(center_x + r_a * Math.cos(phi)) }, new double[] {
					(center_y + r_i * Math.sin(phi)),
					(center_y + r_i * Math.sin(phi2)),
					(center_y + r_i * Math.sin(phi3)),
					(center_y + r_a * Math.sin(phi3)),
					(center_y + r_a * Math.sin(phi2)),
					(center_y + r_a * Math.sin(phi)) }, 6, index.next());

			nodes.add(node);
			node.setPositioner(ArcPositioner.class);

		}

		for (int i = 1; i < n; i++) {
			FactoryNode.couple(nodes.get(i - 1), nodes.get(i));
		}

		return nodes;

	}

}