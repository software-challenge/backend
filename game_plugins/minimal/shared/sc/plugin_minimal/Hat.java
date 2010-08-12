package sc.plugin_minimal;

import java.util.HashMap;
import java.util.Map;

public class Hat {

	private static int nextIndex = 0;

	public final int index;

	private HatSize size;
	private boolean preGolden;
	private boolean golden;

	private  Node target;
	public final Player owner;

	private Node node;

	private int gold;

	public Hat(Node start, Node target, Player owner) {
		this.target = target;
		this.owner = owner;
		this.node = start;
		start.addHat(this);
		//owner.addHat(this);

		size = new HatSize();
		size.addSize(owner.getColor());
		
		index = nextIndex++;
		golden = false;

	}

	public HatSize getSize() {
		return size;
	}

	public boolean isGolden() {
		return golden;
	}
	
	public boolean isPreGolden() {
		return preGolden;
	}

	public Node getCurrentNode() {
		return node;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node target) {
		node = target;
	}

	public void setPreGolden(boolean golden) {
		this.preGolden = golden;

	}
	
	public void setGolden(boolean golden) {
		this.golden = golden;

	}

	public int getGold() {
		return gold;
	}

	public void addGold(int gold) {
		this.gold += gold;
	}
	
	public Node getTarget() {
		return target;
	}


	public void setTarget(Node target) {
		this.target = target;
	}


	public void kill() {
		node.removeHat(this);
		//owner.removeHat(this);

	}

	public Map<Node,Integer> getReachableNodes() {

		Map<Node,Integer> map = new HashMap<Node, Integer>();
//		for (Integer i : owner.getDices()) {
//			for(Node node1 : this.node.getNeighbours(i))
//				map.put(node1, i);
//		}
			
		return map;

	}

	public Map<Node,Integer> getValideMoves() {

		Map<Node,Integer> nodes = new HashMap<Node, Integer>(getReachableNodes());
		for (Node rNode : getReachableNodes().keySet()) {
			switch (rNode.getNodeType()) {
		

			case SAVE:
				// kein anderer hut, es sei den selbst golden
				if(!golden && rNode.getHats().size() !=0){
					nodes.remove(rNode);
				}
				
			case NORM:
				// kein eigener hut
				for (Hat hat : rNode.getHats()) {
					if (hat.owner == owner) {
						nodes.remove(rNode);
						break;
					}
				}
				break;
				
			case BASE0: 
			case BASE1:
				// richtiges ziel
				if(rNode != target){
					nodes.remove(rNode);
				}
				break;


			}
		}

		return nodes;
	}



}