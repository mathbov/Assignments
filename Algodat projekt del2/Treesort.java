// Authors:
// Mathias Bovtrup (mabov13)
// Isabell Seyfried (issey21)

import java.util.Scanner;

// Our Treesort is a client class that creates a new empty tree and takes input from the user and inserts those the tree by calling the methods from our DictBinTree classs.
public class Treesort { 
    public static void main(String[] args) {

	DictBinTree tree = new DictBinTree();
	
	Scanner sc = new Scanner(System.in);
	while (sc.hasNextInt()) {
	    tree.insert(sc.nextInt());
	
	    
       }
//At last it prints the ArrayList of keys in sorted order.
    System.out.println(tree.orderedTraversal());

	} 
}
