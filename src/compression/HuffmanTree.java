package compression;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class HuffmanTree {
	private Node root;
	private HashMap<Byte, String> hfCode0 = new HashMap();

	public HuffmanTree() {

	}

	public Node createTree(ArrayList<Node> n) {
		if (n.size() == 0)
			return null;
		//按照频率大小构建优先队列
		PriorityQueue<Node> pq = new PriorityQueue<Node>(n.size(), new Comparator<Node>() {
			public int compare(Node n0, Node n1) {
				return n0.getWeight() - n1.getWeight();
			}
		});
		for (int i = 0; i < n.size(); i++) {
			if (n.get(i) != null) {
				if (n.get(i).getWeight() > 0) {
					n.get(i).setIsLeaf(true);
					pq.add(n.get(i));
				}
			}
		}
		//构建huffmantree
		while (pq.size() > 1) {
			Node x = pq.poll();
			Node y = pq.poll();
			Node parent = new Node(null, x.getWeight() + y.getWeight());
			x.setParent(parent);
			y.setParent(parent);
			parent.setLeft(x);
			parent.setRight(y);
			this.root = parent;
			pq.add(parent);
		}
		return this.root;
	}
	
	//构建HashMap，方便查询
	public void createCodeMap(Node n) {
		if (n != null) {
			if (n.getIsLeaf()) {
				String code = "";
				Node temp = n;
				while (temp.getParent() != null) {
					if (temp.getParent().getLeft() == temp) {
						code = "0" + code;
					} else if (temp.getParent().getRight() == temp) {
						code = "1" + code;
					}
					temp = temp.getParent();
				}
				hfCode0.put(n.getData(), code);
			}
			createCodeMap(n.getLeft());
			createCodeMap(n.getRight());
		}
	}

	public HashMap<Byte, String> getHfCode0() {
		return this.hfCode0;
	}
}
