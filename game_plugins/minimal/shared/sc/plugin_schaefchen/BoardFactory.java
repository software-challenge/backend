package sc.plugin_schaefchen;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import sc.plugin_schaefchen.gui.positioner.ArcPositioner;
import sc.plugin_schaefchen.gui.positioner.BasePositioner;
import sc.plugin_schaefchen.gui.positioner.Positioner;
import sc.plugin_schaefchen.gui.positioner.RectPositioner;
import sc.plugin_schaefchen.gui.positioner.SquarePositioner;
import sc.plugin_schaefchen.util.Constants;

/**
 * generiert das spielfeld als gerichteten graphen, befuellt felder mit talern
 * und setzt initiale huete
 * 
 * @author tkra
 * */
public class BoardFactory {

	private static final List<FactoryNode> factoryNodes;
	public static final List<Node> nodes;

	static {
		factoryNodes = createFactoryNodes();
		nodes = createNodes();

		for (Node node : nodes) {
			for (int i = 2; i <= Constants.DIE_SIZE; i++) {
				for (Integer neighbour : getNeighbours(node, i)) {
					node.addNeighbour(neighbour, i);
				}
			}
		}

	}

	private static class Index {
		private int index = 0;

		public int next() {
			return index++;
		}
	}

	private static List<Node> createNodes() {

		List<FactoryNode> factoryNodes = createFactoryNodes();
		List<Node> nodes = new ArrayList<Node>(factoryNodes.size());

		for (FactoryNode factoryNode : factoryNodes) {
			Node node = new Node(factoryNode.getNodeType(), factoryNode
					.getCounterPart(), factoryNode.index);
			for (Integer n : factoryNode.getNeighbours()) {
				node.addNeighbour(n, 1);
			}
			nodes.add(node);
		}

		return nodes;

	}

	/*
	 * liefert die menge der (indirekten) nachbar dieses spielfeldes im abstand
	 * dist
	 */
	private static Set<Integer> getNeighbours(Node node, int dist) {
		Set<Integer> set = new HashSet<Integer>();
		if (dist == 0) {
			set.add(node.index);
		} else {
			for (Integer n : node.getNeighbours()) {
				addNeighbours(nodes.get(n), node, Math.abs(dist) - 1, set);
			}
		}

		return set;
	}

	/*
	 * fuegt einen menge von spielfeldern ihre (indirekten) nachbarn im abstand
	 * dist hinzu. ohne den aufrufenden knoten origin
	 */
	private static void addNeighbours(Node node, Node origin, int dist,
			final Set<Integer> set) {
		if (dist == 0) {
			set.add(node.index);
		} else {
			for (Integer n : node.getNeighbours()) {
				if (nodes.get(n) != origin) {
					addNeighbours(nodes.get(n), node, dist - 1, set);
				}
			}
		}

	}

	public static List<GUINode> createGUINodes() {

		List<FactoryNode> factoryNodes = createFactoryNodes();
		List<GUINode> nodes = new ArrayList<GUINode>(factoryNodes.size());

		for (FactoryNode fNode : factoryNodes) {
			GUINode guiNode = new GUINode(fNode.getXs(), fNode.getYs(), fNode
					.getN(), fNode.getCenterX(), fNode.getCenterY(), fNode
					.getSimpleXs(), fNode.getSimpleYs(), fNode.getSimpleN(),
					fNode.index, fNode.getNodeType());

			try {
				// TODO: scheint ein wenig umstaendlicher weg zu sein
				Positioner positioner = fNode.positioner.getConstructor(
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

	protected static List<Flower> createFlowers() {

		Map<FactoryNode, Integer> nodeMap = new HashMap<FactoryNode, Integer>();
		List<FactoryNode> nodeList = new LinkedList<FactoryNode>();
		for (FactoryNode node : factoryNodes) {
			if (node.hasFlowers()) {
				nodeMap.put(node, 0);
				nodeList.add(node);
			}
		}

		Random rand = new SecureRandom();
		for (int i = 0; i < Constants.NODES_WITH_MUSHROOMS; i++) {
			int r = rand.nextInt(nodeList.size());
			FactoryNode node = nodeList.get(r);
			nodeMap.put(node, -1);
			nodeList.remove(r);
		}

		int max = Constants.MAX_FLOWERS;
		int total = Constants.TOTAL_FLOWERS;
		int flowers = -Constants.NODES_WITH_MUSHROOMS;
		while (flowers < total && nodeList.size() > 0) {
			int r = rand.nextInt(nodeList.size());
			FactoryNode node = nodeList.get(r);

			int f = nodeMap.get(node) + 1;
			nodeMap.put(node, f);
			if (f == max) {
				nodeList.remove(node);
			}

			flowers++;
		}

		List<Flower> flowerList = new LinkedList<Flower>();
		for (FactoryNode node : nodeMap.keySet()) {
			int f = nodeMap.get(node);
			if (f != 0) {
				flowerList.add(new Flower(node.index, f));
			}
		}

		Collections.sort(flowerList, new Comparator<Flower>() {
			@Override
			public int compare(Flower o1, Flower o2) {
				return o1.node > o2.node ? 1 : -1;
			}
		});

		return flowerList;
	}

	protected static List<Sheep> createSheeps() {

		List<Sheep> sheepList = new LinkedList<Sheep>();
		for (FactoryNode node : factoryNodes) {
			PlayerColor owner = null;
			if (node.getNodeType() == NodeType.HOME1) {
				owner = PlayerColor.PLAYER1;
			} else if (node.getNodeType() == NodeType.HOME2) {
				owner = PlayerColor.PLAYER2;
			}
			for (int i = 0; i < node.getSheeps(); i++) {
				Sheep sheep = new Sheep(node.index, node.getCounterPart(),
						owner);
				if (node.getNodeType() == NodeType.GRASS) {
					sheep.setDogState(DogState.PASSIVE);
				}
				sheepList.add(sheep);
			}
		}

		return sheepList;
	}

	private static List<FactoryNode> createFactoryNodes() {

		Index index = new Index();
		List<FactoryNode> nodes = new LinkedList<FactoryNode>();

		FactoryNode center = new FactoryNode(new double[] { -1, 1, 1, -1 },
				new double[] { -1, -1, 1, 1 }, 4, index.next());
		center.setPositioner(SquarePositioner.class);
		center.setSheeps(1);

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
			centerTop.get(straightSaveDist).setNodeType(NodeType.SAVE);
		FactoryNode.couple(center, centerTop.get(0));
		FactoryNode.couple(centerTop.get(straightLength - 1), top);
		for (FactoryNode node : centerTop) {
			node.rotate(3 * Math.PI / 2);
		}

		List<FactoryNode> centerBottom = getStraightLink(1, 1, 6, -1,
				straightLength, index);
		nodes.addAll(centerBottom);
		if (straightSave)
			centerBottom.get(straightSaveDist).setNodeType(NodeType.SAVE);
		FactoryNode.couple(center, centerBottom.get(0));
		FactoryNode.couple(centerBottom.get(straightLength - 1), bottom);
		for (FactoryNode node : centerBottom) {
			node.rotate(Math.PI / 2);
		}

		List<FactoryNode> centerLeft = getStraightLink(1, 1, 6, -1,
				straightLength, index);
		nodes.addAll(centerLeft);
		if (straightSave)
			centerLeft.get(straightSaveDist).setNodeType(NodeType.SAVE);
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
			centerRight.get(straightSaveDist).setNodeType(NodeType.SAVE);

		List<FactoryNode> rightBottom = getArcLink(1, 1, 5, 7, 0, Math.PI / 2,
				arcSize, index);
		nodes.addAll(rightBottom);
		if (arcSave) {
			rightBottom.get(arcSaveDist).setNodeType(NodeType.SAVE);
			rightBottom.get(arcSaveDist2).setNodeType(NodeType.SAVE);
		}
		FactoryNode.couple(right, rightBottom.get(0));
		FactoryNode.couple(rightBottom.get(arcSize - 1), bottom);

		List<FactoryNode> bottomLeft = getArcLink(1, 1, 5, 7, 0, Math.PI / 2,
				arcSize, index);
		nodes.addAll(bottomLeft);
		if (arcSave) {
			bottomLeft.get(arcSaveDist).setNodeType(NodeType.SAVE);
			bottomLeft.get(arcSaveDist2).setNodeType(NodeType.SAVE);
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
			leftTop.get(arcSaveDist2).setNodeType(NodeType.SAVE);
			leftTop.get(arcSaveDist).setNodeType(NodeType.SAVE);
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
			topRight.get(arcSaveDist).setNodeType(NodeType.SAVE);
			topRight.get(arcSaveDist2).setNodeType(NodeType.SAVE);
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

		topBase.setSheeps(Constants.SHEEPS_AT_HOME);
		bottomBase.setSheeps(Constants.SHEEPS_AT_HOME);
		leftBase.setSheeps(Constants.SHEEPS_AT_HOME);
		rightBase.setSheeps(Constants.SHEEPS_AT_HOME);

		for (FactoryNode node : nodes) {
			if (node.getNodeType() == NodeType.GRASS && node != center) {
				node.setFlowers(true);
			}
		}

		// nach knotenindex sortieren
		 Collections.sort(nodes, new Comparator<FactoryNode>() {
			@Override
			public int compare(FactoryNode o1, FactoryNode o2) {
				return o1.index > o2.index ? 1 : -1;
			}
		});

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

		int m = 9;
		double[] simpleXs = new double[n];
		double[] simpleYs = new double[n];

		phi = Math.PI / 6;
		phi_cover = 2 * Math.PI - 2 * phi;
		phi_incr = phi_cover / (m - 1);
		dx = 2 * Math.cos(phi);

		for (int i = 0; i < m; i++) {
			simpleXs[i] = dx - 2 * Math.cos(phi);
			simpleYs[i] = 2 * Math.sin(phi);
			phi += phi_incr;
		}

		return new FactoryNode(xs, ys, n, simpleXs, simpleYs, m, index.next());

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
					(center_y + r_a * Math.sin(phi)) }, 6, new double[] {
					(center_x + r_i * Math.cos(phi)),
					(center_x + r_i * Math.cos(phi3)),
					(center_x + r_a * Math.cos(phi3)),
					(center_x + r_a * Math.cos(phi)) }, new double[] {
					(center_y + r_i * Math.sin(phi)),
					(center_y + r_i * Math.sin(phi3)),
					(center_y + r_a * Math.sin(phi3)),
					(center_y + r_a * Math.sin(phi)) }, 4, index.next());

			nodes.add(node);
			node.setPositioner(ArcPositioner.class);

		}

		for (int i = 1; i < n; i++) {
			FactoryNode.couple(nodes.get(i - 1), nodes.get(i));
		}

		return nodes;

	}

}