import java.util.Scanner;

public class TreeSort { 
    public static void main(String[] args) {

	DictBinTree tree = new DictBinTree();
	
	Scanner sc = new Scanner(System.in);
	while (sc.hasNextInt()) {
	    tree.insert(sc.nextInt());
	
	    
       }
    tree.search(10);
    System.out.println(tree.orderedTraversal());

	} 
}
