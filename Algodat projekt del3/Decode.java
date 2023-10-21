import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Task 2:
 */
public class Decode {
    
    //Attributes 
    private static BitInputStream bitIn;
    private static BitOutputStream bitOut;
    private static FileInputStream input;
    private static FileOutputStream output;

    private static String compressedFile;
    private static String originalFile; 
    private static int[] frequency = new int[256]; //create an array for the frequence occurrences.
    private static String[] codeString = new String[256];
    private static PQ heap; //PQ or PQHeap
    private static int size;
    private static int orgininalSize;

    
    //(step 1)
    /**
     * Reading the frequency from the the inputfile using readInt()
     */
    private static void frequencyReader(){
        try{
            input = new FileInputStream(compressedFile);
            //Defining a new BitInputStream which takes a FileInputStream as input
            bitIn = new BitInputStream(input);
            int bit;
            while((bit = bitIn.readInt()) !=-1){
                for( int i = 0; i < frequency.length; i++){
                    frequency[i] = bit;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        } 
    }

    
    /**
     * Reading the bits from the the inputfile (compressed file) using readBit()
     */
    private static void bitReader(){

        try{
            //Defininf a new FileInputStream using the compressedFile as input
            input = new FileInputStream(compressedFile);
            //Defining a new BitInputStream which takes a FileInputStream as input
            bitIn = new BitInputStream(input);
            int bit;
            while((bit = bitIn.readBit()) !=-1){ //Should this include the frequncy array?
                for(int i = 0; i < orgininalSize; i++){//Should stop at int sum value 
                    bitOut.writeBit(bit);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        } 
    }


    /**
     *  
     */
    private static void outputWriter(){
        
        try{
            output = new FileOutputStream(originalFile);
    
        } catch (Exception e) {
            e.printStackTrace();
        }

        bitOut = new BitOutputStream(output);

        for(int i = 0; i < frequency.length; i++){
            try {
                output.write(orgininalSize); 
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





    /**
     * Method for calculating the accumulated frequencies
    */
    private static void accumulatedFreq(){
        int bytes = 0;
        for(int i = 0; i < frequency.length; i++){
            //bytes is defined by multiplying the frequencies with the length of the code.
            bytes = (bytes + (frequency[i] * codeString[i].length()));
        }
        orgininalSize = bytes;
        
        // int modBytes = bytes % 8; 
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
     * 
     */
    private static void heapBuild(){
        for( int i = 0; i < frequency.length; i++){
            heap.insert(new Element(frequency[i], i));
            size = size + 1;
        }
    }


    /**
     * Huffman algorithm like  in the encode class
     */
    private static void decodeHuffman(){
        
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

            size = size - 1;
        }
        
        //HERE OR IN TRAVERSAL?
        
        //Alter way of getting the sum of frequencies by extracting the root (where all values have accumilated)
        //AND re-insering it
        
        //Element root = heap.extractMin();
        // Safe the value as sum somewhere
        //orgininalSize = (int) root.getData();
            
        //insert it back into the heap after the sum has been taken
        //heap.insert(root);
        
    }



    private static void inOrderTraversal(HuffNode currentNode, String currentCode){

        if (currentNode != null){ //currentNode.e.getData() != null
            inOrderTraversal(currentNode.left, currentCode + "0" );
            
            if (currentNode.left == null){//if it's a leaf = no children
                codeString[(int) currentNode.e.getData()] = currentCode;
                //cast currentCode to int for write():
                //int leafValue = ((int) currentCode.getData());
               /** 
                try {
                    output.write(codeString[(int) currentNode.e.getData()]);

                } catch (IOException e) {
                    e.printStackTrace();
                } //Skal det være her eller skal cC bruges i en output metode?
                //codeString[orgininalSize ] = output.write(orgininalSize);
                */

                //Call to outputWriter method??
                outputWriter();
            }
            
            inOrderTraversal(currentNode.right, currentCode + "1" );
        }
    }

    //Aux for inOrderTraversal
    private static void auxTraversal(){

        Element e = heap.extractMin();
        HuffNode root = (HuffNode) e.getData();
        String currentCode = "";

        while(size > 1){
            inOrderTraversal(root, currentCode);
        }
        /** 
         * Test stuff for extrating root
         * ((PQ) root).extractMin();
         * heap.insert(root.e);
        */
    }

    public static void main(String[] args) {
       
        originalFile = args[0]; //".txt"; //
        compressedFile = "onebyte.txt";//args[1]; //hvis du vil teste, kan du skrive dokumentet her direkte, husk .txt
        
        heap = new PQHeap();
    
        //Read the frequency
        frequencyReader();
        //summinf the freq
        accumulatedFreq();

        //Huffman algorithm
        heapBuild();
        decodeHuffman(); //root extraction is here, should it be in the traversal??

        //Traversal
        auxTraversal();
        for( int i = 0; i < codeString.length; i++){
            System.out.println(codeString[i]);
        }

        bitReader();

        //double call cause it's in 
        outputWriter();
        


    }
}
