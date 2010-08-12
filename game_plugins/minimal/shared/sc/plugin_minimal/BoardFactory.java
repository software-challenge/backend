package sc.plugin_minimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.plugin_minimal.renderer.positioner.ArcPositioner;
import sc.plugin_minimal.renderer.positioner.BasePositioner;
import sc.plugin_minimal.renderer.positioner.RectPositioner;
import sc.plugin_minimal.renderer.positioner.SquarePositioner;
import sc.plugin_minimal.util.Constants;
import sc.plugin_minimal.Node;

/**
 * generiert das spielfeld als gerichteten graphen, befuellt felder mit talern und setzt initiale huete
 * @author tkra
 * */
@XStreamAlias(value = "minimal:boardfactory")
public class BoardFactory {

	public static  List<Node> createNodes() {
		
		List<Node> nodes = new ArrayList<Node>(81);

		Node center = new Node(new double[] { -1, 1, 1, -1 }, new double[] {
				-1, -1, 1, 1 }, 4);
		center.setPositioner(new SquarePositioner(center));

		Node top = new Node(new double[] { -1, 1, 1, -1 }, new double[] { -1,
				-1, 1, 1 }, 4);
		top.setPositioner(new SquarePositioner(top));
		top.translate(0, -7);

		Node bottom = new Node(new double[] { -1, 1, 1, -1 }, new double[] {
				-1, -1, 1, 1 }, 4);
		bottom.setPositioner(new SquarePositioner(bottom));
		bottom.translate(0, 7);

		Node left = new Node(new double[] { -1, 1, 1, -1 }, new double[] { -1,
				-1, 1, 1 }, 4);
		left.setPositioner(new SquarePositioner(left));
		left.translate(-7, 0);

		Node right = new Node(new double[] { -1, 1, 1, -1 }, new double[] { -1,
				-1, 1, 1 }, 4);
		right.setPositioner(new SquarePositioner(right));
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
		int arcSaveDist2 =Constants.ARC_SAVE_DIST2;

		boolean arcSave = Constants.USE_ARC_SAVE;
		boolean straightSave = Constants.USE_STRAIGHT_SAVE;

		List<Node> centerTop = getStraightLink(1, 1, 6, -1, straightLength);
		nodes.addAll(centerTop);
		if (straightSave)
			centerTop.get(straightSaveDist).setNodeType(NodeType.SAVE);
		Node.couple(center, centerTop.get(0));
		Node.couple(centerTop.get(straightLength - 1), top);
		for (Node node : centerTop) {
			node.rotate(3 * Math.PI / 2);
		}

		List<Node> centerBottom = getStraightLink(1, 1, 6, -1, straightLength);
		nodes.addAll(centerBottom);
		if (straightSave)
			centerBottom.get(straightSaveDist).setNodeType(NodeType.SAVE);
		Node.couple(center, centerBottom.get(0));
		Node.couple(centerBottom.get(straightLength - 1), bottom);
		for (Node node : centerBottom) {
			node.rotate(Math.PI / 2);
		}

		List<Node> centerLeft = getStraightLink(1, 1, 6, -1, straightLength);
		nodes.addAll(centerLeft);
		if (straightSave)
			centerLeft.get(straightSaveDist).setNodeType(NodeType.SAVE);
		Node.couple(center, centerLeft.get(0));
		Node.couple(centerLeft.get(straightLength - 1), left);
		for (Node node : centerLeft) {
			node.rotate(Math.PI);
		}

		List<Node> centerRight = getStraightLink(1, 1, 6, -1, straightLength);
		nodes.addAll(centerRight);
		Node.couple(center, centerRight.get(0));
		Node.couple(centerRight.get(straightLength - 1), right);
		if (straightSave)
			centerRight.get(straightSaveDist).setNodeType(NodeType.SAVE);

		List<Node> rightBottom = getArcLink(1, 1, 5, 7, 0, Math.PI / 2, arcSize);
		nodes.addAll(rightBottom);
		if (arcSave) {
			rightBottom.get(arcSaveDist).setNodeType(NodeType.SAVE);
			rightBottom.get(arcSaveDist2).setNodeType(NodeType.SAVE);
		}
		Node.couple(right, rightBottom.get(0));
		Node.couple(rightBottom.get(arcSize - 1), bottom);

		List<Node> bottomLeft = getArcLink(1, 1, 5, 7, 0, Math.PI / 2, arcSize);
		nodes.addAll(bottomLeft);
		if (arcSave) {
			bottomLeft.get(arcSaveDist).setNodeType(NodeType.SAVE);
			bottomLeft.get(arcSaveDist2).setNodeType(NodeType.SAVE);
		}
		Node.couple(bottom, bottomLeft.get(0));
		Node.couple(bottomLeft.get(arcSize - 1), left);
		for (Node node : bottomLeft) {
			node.rotate(Math.PI / 2);
		}

		List<Node> leftTop = getArcLink(1, 1, 5, 7, 0, Math.PI / 2, arcSize);
		nodes.addAll(leftTop);
		if (arcSave) {
			leftTop.get(arcSaveDist2).setNodeType(NodeType.SAVE);
			leftTop.get(arcSaveDist).setNodeType(NodeType.SAVE);
		}
		Node.couple(left, leftTop.get(0));
		Node.couple(leftTop.get(arcSize - 1), top);
		for (Node node : leftTop) {
			node.rotate(Math.PI);
		}

		List<Node> topRight = getArcLink(1, 1, 5, 7, 0, Math.PI / 2, arcSize);
		nodes.addAll(topRight);
		if (arcSave) {
			topRight.get(arcSaveDist).setNodeType(NodeType.SAVE);
			topRight.get(arcSaveDist2).setNodeType(NodeType.SAVE);
		}
		Node.couple(top, topRight.get(0));
		Node.couple(topRight.get(arcSize - 1), right);
		for (Node node : topRight) {
			node.rotate(3 * Math.PI / 2);
		}

		Node rightBase = getBaseNode();
		nodes.add(rightBase);
		rightBase.setPositioner(new BasePositioner(rightBase));
		rightBase.setNodeType(NodeType.BASE1);
		Node.couple(right, rightBase);
		rightBase.translate(8, 0);

		Node bottomBase = getBaseNode();
		nodes.add(bottomBase);
		bottomBase.setPositioner(new BasePositioner(bottomBase));
		bottomBase.setNodeType(NodeType.BASE0);
		Node.couple(bottom, bottomBase);
		bottomBase.translate(8, 0);
		bottomBase.rotate(Math.PI / 2);

		Node leftBase = getBaseNode();
		nodes.add(leftBase);
		leftBase.setPositioner(new BasePositioner(leftBase));
		leftBase.setNodeType(NodeType.BASE1);
		Node.couple(left, leftBase);
		leftBase.translate(8, 0);
		leftBase.rotate(Math.PI);

		Node topBase = getBaseNode();
		nodes.add(topBase);
		topBase.setPositioner(new BasePositioner(topBase));
		topBase.setNodeType(NodeType.BASE0);
		Node.couple(top, topBase);
		topBase.translate(8, 0);
		topBase.rotate(3 * Math.PI / 2);

		for (Node node : nodes) {
			node.rotate(Math.PI / 4);
		}

		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		for (Node node : nodes) {
			if (node.minX() < minX)
				minX = node.minX();
			if (node.minY() < minY)
				minY = node.minY();
		}

		for (Node node : nodes) {
			node.translate(-minX, -minY);
		}

		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		for (Node node : nodes) {
			if (node.maxX() > maxX)
				maxX = node.maxX();
			if (node.maxY() > maxY)
				maxY = node.maxY();
		}

		for (Node node : nodes) {
			node.scale(1 / Math.max(maxX, maxY));
		}

		topBase.setCounterPart(bottomBase);
		bottomBase.setCounterPart(topBase);
		leftBase.setCounterPart(rightBase);
		rightBase.setCounterPart(leftBase);
//		for (int i = 0; i < JConfigPanel.getHatsInBase1(); i++) {
//			new Hat(leftBase, rightBase, Player.PLAYER1);
//			new Hat(topBase, bottomBase, Player.PLAYER0);
//		}
//
//		for (int i = 0; i < JConfigPanel.getHatsInBase2(); i++) {
//			new Hat(rightBase, leftBase, Player.PLAYER1);
//			new Hat(bottomBase, topBase, Player.PLAYER0);
//		}
//
//		Hat goldenhat = new Hat(center, null, Player.NOPLAYER);
//		if (JConfigPanel.preGoldenRule()) {
//			goldenhat.setPreGolden(true);
//		} else {
//			goldenhat.setGolden(true);
//		}

		int gold = 0, min = Constants.MIN_GOLD;
		int max = Constants.MAX_GOLD, total = Constants.TOTAL_GOLD;
		List<Node> goldenNodes = new ArrayList<Node>(nodes.size());
		for (Node node : nodes) {
			if (node.getNodeType() == NodeType.NORM && node != center) {
				goldenNodes.add(node);
				node.addGold(min);
				gold += min;
			}
		}

		Random rand = new Random();
		while (gold < total && goldenNodes.size() > 0) {
			Node n = goldenNodes.get(rand.nextInt(goldenNodes.size()));
			n.addGold(1);

			if (n.getGold() == max) {
				goldenNodes.remove(n);
			}

			gold++;
		}
		
		return nodes;

	}

	private static Node getBaseNode() {

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

		return new Node(xs, ys, n);

	}

	private static List<Node> getStraightLink(double x1, double y1, double x2,
			double y2, int n) {

		double xa, xb;
		double x_incr = (x2 - x1) / n;
		List<Node> nodes = new ArrayList<Node>(n);

		for (int i = 0; i < n; i++) {
			xa = x1 + i * x_incr;
			xb = x1 + (i + 1) * x_incr;

			if (i == -1)
				xb = x2;

			Node node = new Node(new double[] { xa, xb, xb, xa }, new double[] {
					y1, y1, y2, y2 }, 4);
			nodes.add(node);
			node.setPositioner(new RectPositioner(node));

		}

		for (int i = 1; i < n; i++) {
			Node.couple(nodes.get(i - 1), nodes.get(i));
		}

		return nodes;
	}

	private static List<Node> getArcLink(double center_x, double center_y, double r_i,
			double r_a, double phi_start, double phi_end, int n) {

		double phi_incr = Math.abs(phi_start - phi_end) / (2 * n);
		double phi, phi2, phi3;
		List<Node> nodes = new ArrayList<Node>(n);

		for (int i = 0; i < n; i++) {

			phi = phi_start + (2 * i) * phi_incr;
			phi2 = phi + phi_incr;
			phi3 = phi2 + phi_incr;

			if (i == n - 1) {
				phi3 = phi_end;
			}

			Node node = new Node(new double[] {
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
					(center_y + r_a * Math.sin(phi)) }, 6);

			nodes.add(node);
			node.setPositioner(new ArcPositioner(node));

		}

		for (int i = 1; i < n; i++) {
			Node.couple(nodes.get(i - 1), nodes.get(i));
		}

		return nodes;

	}

}