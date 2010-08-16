package sc.plugin_minimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sc.plugin_minimal.renderer.positioner.ArcPositioner;
import sc.plugin_minimal.renderer.positioner.BasePositioner;
import sc.plugin_minimal.renderer.positioner.RectPositioner;
import sc.plugin_minimal.renderer.positioner.SquarePositioner;
import sc.plugin_minimal.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * generiert die spielfelder
 * @author tkra
 * */
@XStreamAlias(value = "minimal:boardfactory")
public class BoardFactory {

	public static final List<Node> createNodes(final Board board) {
		
		List<Node> nodes = new ArrayList<Node>(81);

		Node center = new Node(new double[] { -1, 1, 1, -1 }, new double[] {
				-1, -1, 1, 1 }, 4, board);
		center.setPositioner(new SquarePositioner(center));

		Node top = new Node(new double[] { -1, 1, 1, -1 }, new double[] { -1,
				-1, 1, 1 }, 4, board);
		top.setPositioner(new SquarePositioner(top));
		top.translate(0, -7);

		Node bottom = new Node(new double[] { -1, 1, 1, -1 }, new double[] {
				-1, -1, 1, 1 }, 4, board);
		bottom.setPositioner(new SquarePositioner(bottom));
		bottom.translate(0, 7);

		Node left = new Node(new double[] { -1, 1, 1, -1 }, new double[] { -1,
				-1, 1, 1 }, 4, board);
		left.setPositioner(new SquarePositioner(left));
		left.translate(-7, 0);

		Node right = new Node(new double[] { -1, 1, 1, -1 }, new double[] { -1,
				-1, 1, 1 }, 4, board);
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

		List<Node> centerTop = getStraightLink(1, 1, 6, -1, straightLength, board);
		nodes.addAll(centerTop);
		if (straightSave)
			centerTop.get(straightSaveDist).setNodeType(NodeType.FENCE);
		Node.couple(center, centerTop.get(0));
		Node.couple(centerTop.get(straightLength - 1), top);
		for (Node node : centerTop) {
			node.rotate(3 * Math.PI / 2);
		}

		List<Node> centerBottom = getStraightLink(1, 1, 6, -1, straightLength, board);
		nodes.addAll(centerBottom);
		if (straightSave)
			centerBottom.get(straightSaveDist).setNodeType(NodeType.FENCE);
		Node.couple(center, centerBottom.get(0));
		Node.couple(centerBottom.get(straightLength - 1), bottom);
		for (Node node : centerBottom) {
			node.rotate(Math.PI / 2);
		}

		List<Node> centerLeft = getStraightLink(1, 1, 6, -1, straightLength, board);
		nodes.addAll(centerLeft);
		if (straightSave)
			centerLeft.get(straightSaveDist).setNodeType(NodeType.FENCE);
		Node.couple(center, centerLeft.get(0));
		Node.couple(centerLeft.get(straightLength - 1), left);
		for (Node node : centerLeft) {
			node.rotate(Math.PI);
		}

		List<Node> centerRight = getStraightLink(1, 1, 6, -1, straightLength, board);
		nodes.addAll(centerRight);
		Node.couple(center, centerRight.get(0));
		Node.couple(centerRight.get(straightLength - 1), right);
		if (straightSave)
			centerRight.get(straightSaveDist).setNodeType(NodeType.FENCE);

		List<Node> rightBottom = getArcLink(1, 1, 5, 7, 0, Math.PI / 2, arcSize, board);
		nodes.addAll(rightBottom);
		if (arcSave) {
			rightBottom.get(arcSaveDist).setNodeType(NodeType.FENCE);
			rightBottom.get(arcSaveDist2).setNodeType(NodeType.FENCE);
		}
		Node.couple(right, rightBottom.get(0));
		Node.couple(rightBottom.get(arcSize - 1), bottom);

		List<Node> bottomLeft = getArcLink(1, 1, 5, 7, 0, Math.PI / 2, arcSize, board);
		nodes.addAll(bottomLeft);
		if (arcSave) {
			bottomLeft.get(arcSaveDist).setNodeType(NodeType.FENCE);
			bottomLeft.get(arcSaveDist2).setNodeType(NodeType.FENCE);
		}
		Node.couple(bottom, bottomLeft.get(0));
		Node.couple(bottomLeft.get(arcSize - 1), left);
		for (Node node : bottomLeft) {
			node.rotate(Math.PI / 2);
		}

		List<Node> leftTop = getArcLink(1, 1, 5, 7, 0, Math.PI / 2, arcSize, board);
		nodes.addAll(leftTop);
		if (arcSave) {
			leftTop.get(arcSaveDist2).setNodeType(NodeType.FENCE);
			leftTop.get(arcSaveDist).setNodeType(NodeType.FENCE);
		}
		Node.couple(left, leftTop.get(0));
		Node.couple(leftTop.get(arcSize - 1), top);
		for (Node node : leftTop) {
			node.rotate(Math.PI);
		}

		List<Node> topRight = getArcLink(1, 1, 5, 7, 0, Math.PI / 2, arcSize, board);
		nodes.addAll(topRight);
		if (arcSave) {
			topRight.get(arcSaveDist).setNodeType(NodeType.FENCE);
			topRight.get(arcSaveDist2).setNodeType(NodeType.FENCE);
		}
		Node.couple(top, topRight.get(0));
		Node.couple(topRight.get(arcSize - 1), right);
		for (Node node : topRight) {
			node.rotate(3 * Math.PI / 2);
		}

		Node rightBase = getBaseNode(board);
		nodes.add(rightBase);
		rightBase.setPositioner(new BasePositioner(rightBase));
		rightBase.setNodeType(NodeType.HOME2);
		Node.couple(right, rightBase);
		rightBase.translate(8, 0);

		Node bottomBase = getBaseNode(board);
		nodes.add(bottomBase);
		bottomBase.setPositioner(new BasePositioner(bottomBase));
		bottomBase.setNodeType(NodeType.HOME1);
		Node.couple(bottom, bottomBase);
		bottomBase.translate(8, 0);
		bottomBase.rotate(Math.PI / 2);

		Node leftBase = getBaseNode(board);
		nodes.add(leftBase);
		leftBase.setPositioner(new BasePositioner(leftBase));
		leftBase.setNodeType(NodeType.HOME2);
		Node.couple(left, leftBase);
		leftBase.translate(8, 0);
		leftBase.rotate(Math.PI);

		Node topBase = getBaseNode(board);
		nodes.add(topBase);
		topBase.setPositioner(new BasePositioner(topBase));
		topBase.setNodeType(NodeType.HOME1);
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

		Sheep goldenhat = new Sheep(center, null, null);
		if (Constants.PRE_GOLDEN_RULE) {
			goldenhat.setSheepdog(true);
		} else {
			goldenhat.setSharpSheepdog(true);
		}

		int gold = 0, min = Constants.MIN_GOLD;
		int max = Constants.MAX_GOLD, total = Constants.TOTAL_GOLD;
		List<Node> goldenNodes = new ArrayList<Node>(nodes.size());
		for (Node node : nodes) {
			if (node.getNodeType() == NodeType.GRASS && node != center) {
				goldenNodes.add(node);
				node.addFlowers(min);
				gold += min;
			}
		}

		Random rand = new Random();
		while (gold < total && goldenNodes.size() > 0) {
			Node n = goldenNodes.get(rand.nextInt(goldenNodes.size()));
			n.addFlowers(1);

			if (n.getFlowers() == max) {
				goldenNodes.remove(n);
			}

			gold++;
		}
		
		return nodes;

	}

	private static final Node getBaseNode(final Board board) {

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

		return new Node(xs, ys, n, board);

	}

	private static final List<Node> getStraightLink(double x1, double y1, double x2,
			double y2, int n, final Board board) {

		double xa, xb;
		double x_incr = (x2 - x1) / n;
		List<Node> nodes = new ArrayList<Node>(n);

		for (int i = 0; i < n; i++) {
			xa = x1 + i * x_incr;
			xb = x1 + (i + 1) * x_incr;

			if (i == -1)
				xb = x2;

			Node node = new Node(new double[] { xa, xb, xb, xa }, new double[] {
					y1, y1, y2, y2 }, 4, board);
			nodes.add(node);
			node.setPositioner(new RectPositioner(node));

		}

		for (int i = 1; i < n; i++) {
			Node.couple(nodes.get(i - 1), nodes.get(i));
		}

		return nodes;
	}

	private static final List<Node> getArcLink(double center_x, double center_y, double r_i,
			double r_a, double phi_start, double phi_end, int n, final Board board) {

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
					(center_y + r_a * Math.sin(phi)) }, 6, board);

			nodes.add(node);
			node.setPositioner(new ArcPositioner(node));

		}

		for (int i = 1; i < n; i++) {
			Node.couple(nodes.get(i - 1), nodes.get(i));
		}

		return nodes;

	}

}