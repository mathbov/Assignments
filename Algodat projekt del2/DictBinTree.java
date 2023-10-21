// Authors:
// Mathias Bovtrup (mabov13)
// Isabell Seyfried (issey21)

import java.util.ArrayList;

public class DictBinTree implements Dict{

	public BinNode root;

	public DictBinTree(){
		root = null;
	}
	//SearchHelper is a recursive search function that searches for a given key in
	// in the tree.
	private boolean searchHelper(BinNode currentNode, int key){

	//First we check if the tree is empty or we have reached the end.
		if (currentNode == null){
		System.out.println(key + " has not been found");
		return false;
		}

	//Then we checks if we found the key at the node we have reached.
		if (currentNode.key == key){
		System.out.println(key + " has been found");
		return true;
		}

	//Else we performs a recursive call to either the left or right side of the tree
	// depending on wether the key is less or larger than the current.	
		if (key < currentNode.key){
			return searchHelper(currentNode.left, key);
		}
		else{
			return searchHelper(currentNode.right, key);
		}

	}
		
	
	
	
	//The search function from the interface that the user see, uses our recursive searchHelper method.
	public boolean search(int key){
		return (searchHelper(root, key));


	}

	//To insert a key into the tree we use the algorithm from the book.
	public void insert(int key){
		BinNode y = null;
		BinNode x = root;

	//At the start of our class we set root to be null so if the tree is empty we dont come into this while loop.
		while (x != null){
			y = x; 	//We set y to be the x we are currently looking at to find a place to insert the new node.
			

			if (key < x.key){
				x = x.left; // If the key we are inserting are less than the current key we contiune in the left tree or subtree.
			}
			else{ 
				x = x.right; //Otherwise we go to the rigth side.
			}
		}
	//If the tree is empty we set the root of the tree to be the node we are inserting.
		if (y == null){
			root = new BinNode(key);
		}
	//When we reach this part we checks again if the key inserting is to be a left or right child of the y node which is the node we reached in the while loop.
		else if (key < y.key){
			y.left = new BinNode(key);
		}
		else {
			y.right = new BinNode(key);
		}
		
	
		
	}
	// The orderTraversal method from the interfaces creates an ArrayList to to be used by our recursive orderTraversalRecursive method which adds
	// keys of the nodes to the ArrayList in sorted order.
	public ArrayList<Integer> orderedTraversal(){
		ArrayList<Integer> list = new ArrayList<>();
		orderedTraversalRecursive(list, root);
		return list;

	}
//Our orderedTraversalRecursive function takes the ArrayList from before and a node to perform the inorder walk from. By default this is the root, so we
// go through the entire tree.
	public void orderedTraversalRecursive( ArrayList<Integer> list, BinNode currentNode){
		if (currentNode != null){
			orderedTraversalRecursive(list, currentNode.left); //Recursive call to get all the left nodes of the tree.
			list.add(currentNode.key); //Adds the currentnode to the ArrayList
			orderedTraversalRecursive(list, currentNode.right); //Recursive call to get all the right nodes of the tree.



		}

	}
	//Our BinNode class is an inner class to create nodes as BinNodes out of our keys we get from the user input.
	private static class BinNode{

	//The attributes are the nodes key, its right and left child.
	
		private int key;
		private BinNode right;
		private BinNode left;

	// Our constructer sets the key to be this key and when we create a new node both childs are set to be null untill a node is inserted as their child.
		public BinNode(int key){
			this.key = key;
			right =  null;
			left = null;

	}


}

}