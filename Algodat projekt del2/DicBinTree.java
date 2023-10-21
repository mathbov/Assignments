public class DictBinTree implements Dict{

	BinNode root;

	public DictBinTree(){
	
		root = null;


	}
	private int searchHelper(BinNode currentNode int key){

		if (currentNode == null){

		return false;
		}

	if (currentNode.getKey() = key{
		return true;
	}
		if (gey < currentNode.getKey()){
			return searchHelper(currentNode.getLeft(), key);
		}
		else if{
			return searchHelper(currentNode.getRight(), key);
		}
		}
	}
	}

	public boolean search(int key){
		return (searchHelper(root, key));


	}

	public void insert(int key){
		BinNode z = new BinNode(key);
		By = null;
		x = root;
		while (x != null){
			y = x;
			if (z.getKey() < x.getKey()){
				x = x.getLeft();
			else x = x.getRight();
			}
		}
		if (y == null){
			root = z;
		}
		else if (z.getKey() < y.getKey()){
			y.setLeft(z);
		}
		else y.setRight(z);
		}

	}
	public ArrayList orderedTraversal(){
		ArrayList<Integer> list = new ArrayList<>()
		orderedTraversalRecursive(tree, root);

	}

	public ArrayList orderedTraversalRecursive(DictBinTree tree, BinNode currentNode){
		if (currentNode != null){
			orderedTraversalRecursive(tree, currentNode.getLeft);
			list.add(currentNode.getKey())
			orderedTraversalRecursive(tree, currentNode.getRight);
			


		}

	}


}