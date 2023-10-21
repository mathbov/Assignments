import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Task 1:
 * This program reads a file as input and encodes it through Huffman-code.
 */


//Step 1:
    //Scan input file + create an array for the frequence of the bytes.

//Step 2:
    //Run the Huffman algorithm with the array as input.


public class Encode {

    //Attributes:
    private static int[] frequency = new int[256]; //create an array for the frequence occurrences.
    private static BitOutputStream bitOut;
    private static FileOutputStream output;
    private static FileInputStream input;
    private static String originalFile;
    private static String compressedFile;
    private static PQ heap;
    private static int size;
    private static int modBytes;


    private static String[] codeString = new String[256];


    //Step 1:
    //Scan input file + create an array for the frequencies of the "bytes"
    private static void fileReader(){
        
        try{
            input = new FileInputStream(originalFile);
            int bit;
            while((bit = input.read()) !=-1){
                frequency[bit] += 1;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        } 
    }

    //
    private static void bitReader(){
        
        try{
            input = new FileInputStream(originalFile);
            int bit;
            while((bit = input.read()) !=-1){
                
                String tempString = codeString[bit];

                for (int i = 0; i < tempString.length(); i++){
                    int tempInt = tempString.charAt(i)=='0' ? 0:1; 
                    bitOut.writeBit(tempInt);
                }
            }
            bitOut.close();
        }
        catch(Exception e){
            e.printStackTrace();
        } 
    }

    /**
     * 
     */
    private static void frequencyOutput(){
        
        try{
            output = new FileOutputStream(compressedFile);
    
        } catch (Exception e) {
            e.printStackTrace();
        }

        bitOut = new BitOutputStream(output);

        for(int i = 0; i < frequency.length; i++){
            try {
                bitOut.writeInt(frequency[i]); //+i?
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method for calculating the accumulated frequencies
     */
    private static void countBytes(){
        int bytes = 0;
        for(int i = 0; i < frequency.length; i++){
            //bytes is defined by multiplying the frequencies with the length of the code.
            bytes = (bytes + (frequency[i] * codeString[i].length()));
        }
        //
        int modBytes = bytes % 8;
    }



    //Huffman 
    
    private static void heapBuild(){
        for( int i = 0; i < frequency.length; i++){
            heap.insert(new Element(frequency[i], i));
            size +=1;
        }
    }

    
   /**
    * Private innerclass.
    * HuffNode for the tree/huffman algorithm 
    */
    private static class HuffNode {
        private HuffNode right;
        private HuffNode left;
        private Element e ;

        
        //Constructor
        public HuffNode(Element e){
            left = null;
            right = null;
            this.e = e;
            
        }
    }

    /**
     * Huffman-like algorithm using huffNodes construct af huffman-tree
     */
    private static void huffmanAlgorithm(){

        while(size > 1){
            HuffNode parent = new HuffNode(null);
            Element left = heap.extractMin();
            Element right = heap.extractMin();

            //Left node
            if(left.getData() instanceof HuffNode){
                //typecasting to get a HuffNode
                parent.left = (HuffNode) left.getData();
            }
            else{
                parent.left = new HuffNode(left);
            }

            //Right node
            if(right.getData() instanceof HuffNode){
                //typecasting to get a HuffNode
                parent.right = (HuffNode) right.getData();
            }
            else{
                parent.right = new HuffNode(right);
            }

            //defining the parent-heaps key and data.
            parent.e = new Element(left.getKey() + right.getKey(),parent);

            //Inserting parent into the heap.
            heap.insert(parent.e);

            //sizing one down after 
            size = size - 1;
        }

    }

    private static void inOrderTraversal(HuffNode currentnode, String currentCode){

        if (currentnode != null){ //currentnode.e.getData() != null
            inOrderTraversal(currentnode.left, currentCode + "0" );
            
            if (currentnode.left == null){//if it's a leaf = no children
                codeString[(int) currentnode.e.getData()] = currentCode;
            }
            
            inOrderTraversal(currentnode.right, currentCode + "1" );
        }
    }

    //Aux for inOrderTraversal
    private static void auxTraversal(){

        Element element = heap.extractMin();
        HuffNode root = (HuffNode) element.getData();
        String currentCode = "";


        inOrderTraversal(root, currentCode);
    }


    public static void main(String[] args){

        originalFile = args[0]; //".txt"; //
        compressedFile = args[1]; //hvis du vil teste, kan du skrive dokumentet her direkte, husk .txt
        
        heap = new PQHeap();
    
    
        fileReader();
        
        //writeInt
        frequencyOutput();


        heapBuild();
        huffmanAlgorithm();

        
        auxTraversal();

        for( int i = 0; i < codeString.length; i++){
            System.out.println(codeString[i]);
        }


        //writeBit
        bitReader();


    }
}

    