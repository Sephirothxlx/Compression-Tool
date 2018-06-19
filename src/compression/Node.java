package compression;

public class Node {
	private int weight;
	private Byte data;
	private Node parent;
	private Node leftChild;
	private Node rightChild;
	private boolean isLeaf=false;

	public Node(Byte data, int weight) {
		this.data = data;
		this.weight = weight;
	}
	
	public Byte getData(){
		return this.data;
	}
	
	public int getWeight(){
		return this.weight;
	}
	
	public Node getParent() {
		return this.parent;
	}

	public void setParent(Node n) {
		this.parent = n;
	}

	public Node getLeft() {
		return this.leftChild;
	}

	public void setLeft(Node n) {
		this.leftChild = n;
	}

	public Node getRight() {
		return this.rightChild;
	}

	public void setRight(Node n) {
		this.rightChild = n;
	}
	
	public boolean getIsLeaf(){
		return this.isLeaf;
	}
	
	public void setIsLeaf(boolean x){
		this.isLeaf=x;
	}
}
