package PageReplacementAlgorithms;

import java.util.*;
import java.util.stream.IntStream;

public class PageReplacementAlgorithms {
    
    public static int framesNum; //no of memory frames
    public static int refString[]; //refrence string
    public static int ne;
    
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Random rand = new Random();
        
        //---------------------------------------------------------------
        
        do{
            System.out.println("Enter Number of Frames: ");
            framesNum = s.nextInt();
        }
        while( framesNum <= 0 );
        
        do{
            System.out.println("Enter the length of Reference String: ");
            ne = s.nextInt();
        }
        while( ne <= 0 );
        
        refString = new int[ne];
        // refString = new int[]{7,0,1,2,0,3,0,4,2,3,0,3,2,1,2,0,1,7,0,1};         // use RefStringNumber = 20 and FramesNumber = 3 
        // refString = new int[]{0, 1, 3, 6, 2, 4, 5, 2, 5, 0, 3, 1, 2, 5, 4, 1, 0};   // use RefStringNumber = 17 and FramesNumber = 4 
        
  
        //---------------------------------------------------------------
        
        // Initialize refString with random values
        for (int i = 0; i < refString.length; i++) {
          refString[i] = rand.nextInt(100);   
          // refString[i] = rand.nextInt(refString.length);   
        }
     
        //---------------------------------------------------------------
         
        firstInFirstOut();
        
        leastRecentlyUsed();
        
        leastFrequentlyUsed();
        
        secondChance();
        
        enhancedSecondChance();
        
        optimal();
    }
    
    private static void optimal(){
        
        int memory[][];
        int inFrames[];
        int inFramesNextIndex[];
        int faults = 0;
        inFrames = new int[framesNum];
        memory = new int[refString.length][framesNum];
        
        // Frames are initially empty 
        for (int i = 0; i < framesNum; i++) { 
            inFrames[i] = -1;
        }
        
        //---------------------------------------------------------------
         
        for(int i = 0; i < refString.length; i++) {
            
            int temp = i;
            boolean contains = IntStream.of(inFrames).anyMatch(x -> x == refString[temp]);
            
            if( !contains ){
            
                inFramesNextIndex = new int[framesNum];
                faults++;

                // Get the indicies where you will call the pages in inFrames next time
                for (int j = 0; j < inFrames.length; j++) {
                    for (int k = i+1; k < refString.length; k++) {
                        if( inFrames[j] == -1 ){
                            inFramesNextIndex[j] = refString.length + 2;
                            break;
                        }
                        else if( inFrames[j] == refString[k] ){
                            inFramesNextIndex[j] = k;
                            break;
                        }
                        else{
                            inFramesNextIndex[j] = refString.length + 1;
                        }
                    }
                }


                int max = inFramesNextIndex[0];
                int maxIndex = 0;

                for(int h = 1; h < inFramesNextIndex.length; h++)
                {
                    if(inFramesNextIndex[h] > max)
                    {
                            max = inFramesNextIndex[h];
                            maxIndex = h;
                    }
                }

                inFrames[maxIndex] = refString[i];
            
            }  
            
            // Filling memory matrix to display
            for(int z = 0; z < framesNum; z++){ 
                   memory[i][z] = inFrames[z];
            }
          }
        
         //---------------------------------------------------------------
                 
        printOneColumn(memory, faults, "Optimal Algorithm: ");
    }
    
    private static void firstInFirstOut(){
        
        int memory[][];
        int inFrames[];
        int faults = 0;
        int p = 0;
        inFrames = new int[framesNum];
        memory = new int[refString.length][framesNum];
        
        //Frames are initially empty 
        for (int i = 0; i < framesNum; i++) { 
            inFrames[i] = -1;
        }
        
        //---------------------------------------------------------------
         
        for(int i = 0; i < refString.length; i++) {
            boolean check = false;
            for(int j = 0; j < framesNum; j++){
               if(inFrames[j] == refString[i])
                 {
                    check = true;
                    break;
                } 
            }
            if(check == false)
            {
             inFrames[p] = refString[i];
             faults++;
             p++;
             if(p == framesNum)
                p = 0;
            }
            
             // Filling memory matrix to display
            for(int z = 0; z < framesNum; z++){
                memory[i][z] = inFrames[z];
            }
          }
        
         //---------------------------------------------------------------
        printOneColumn(memory, faults, "FIFO Algorithm: ");
    }
    
    private static void leastRecentlyUsed(){
        int faults = 0;
        int[] time = new int[framesNum];
        int[][] mem = new int[refString.length][framesNum];
        
        for(int i=0; i<framesNum; i++)
            time[i] = -1;

        for(int j=0; j<framesNum; j++)
            mem[0][j] = -1;
        
        for(int i=0; i<refString.length; i++){
            for(int z=0; z<framesNum; z++){
                if(i != 0)
                    mem[i][z] = mem[i-1][z];
            }
            
            int j = indexOf(mem[i], refString[i]);
            
            if(j == framesNum){
               j = getMinIndex(time);
               faults++;
            }

            time[j] = i;
            mem[i][j] = refString[i];
        }
        printOneColumn(mem, faults, "LRU Algorithm: ");
    }
    
    private static void leastFrequentlyUsed(){
        int faults = 0;
        int[][] mem = new int[refString.length][framesNum];
        
        for(int j=0; j<framesNum; j++)
            mem[0][j] = -1;
        
        for(int i=0; i<refString.length; i++)
            LFUPage.intialAdd(refString[i]);
        
        for(int i=0; i<refString.length; i++){
            for(int z=0; z<framesNum; z++){
                if(i != 0)
                    mem[i][z] = mem[i-1][z];
            }
            
            int j = indexOf(mem[i], refString[i]);
            
            if(j == framesNum){
                j = LFUPage.getReplacementIndex(mem[i]);
                faults++;
            }
                
            mem[i][j] = refString[i];
            LFUPage.setTimeFreq(refString[i], i); 
        }
        printOneColumn(mem, faults, "LFU Algorithm: ");
    }
        
    private static void secondChance(){
        
        int faults = 0;
        int p = 0;
        int inFrames[][] = new int[framesNum][2]; // 2 bits inside the frame
        int memory [][] = new int[refString.length][framesNum];
        int validBt[][] = new int[refString.length][framesNum];
        
        for(int j = 0; j < framesNum; j++){
         inFrames[j][0] = -1; // initially empty
         inFrames[j][1] = 0; //initially zero
        }
        
        for(int i = 0; i < refString.length; i++){
            
            boolean check = false;
            
             for(int j = 0; j < framesNum; j++){
                 if(inFrames[j][0] == refString[i]){
                    check = true;
                    inFrames[j][1] = 1;
                    break;
                  } 
             }
            if(check == false){
                
                 while(inFrames[p][1] == 1) {
                       
                     inFrames[p][1] = 0;
                     
                     p++;
                     if(p == framesNum)
                        p = 0;
                  }
                 
                  inFrames[p][0] = refString[i];
                  inFrames[p][1] = 1;
                  
                  faults++;
                  
                  p++;
                  if(p == framesNum)
                     p = 0;
            }
            
            for(int j = 0; j < framesNum; j++){
             memory[i][j] = inFrames[j][0];
             validBt[i][j] = inFrames[j][1];
            }
       }
        printTwoColumns(memory, validBt, faults, "Second Chance Algorithm: ");
    }
        
    private static void enhancedSecondChance(){
        
        int faults = 0;
        int p = 0;
        int inFrames[][] = new int[framesNum][3]; // 3 bits inside the frame
        int memory [][] = new int[refString.length][framesNum];
        int refBit[][] = new int[refString.length][framesNum];
        int modifyBit[][] = new int[refString.length][framesNum];
        int intialModifyBit[][] = new int[refString.length][2];
        Random rand = new Random();
        
        for(int j = 0; j < framesNum; j++){
            inFrames[j][0] = -1;    // initially empty
            inFrames[j][1] = 0;     // initially zero
            inFrames[j][1] = 0;     // initially zero
        }
        
        // Make modify bit the same for repeated pages
//        for(int i = 0; i < intialModifyBit.length; i++) {
//            
//            int index = linearSearch(Arrays.copyOfRange(refString, 0, i), refString[i]);
//                   
//            if( index != -1 ){
//                intialModifyBit[i][1] = intialModifyBit[index][1];
//            }
//            else{
//                intialModifyBit[i][1] = rand.nextInt(2);
//            }
//                       
//            // System.out.print(intialModifyBit[i][1] + " ");
//        }
        
        for(int i = 0; i < intialModifyBit.length; i++) {
            
            intialModifyBit[i][1] = rand.nextInt(2);
        }
        
//        intialModifyBit = new int[][]{
//        {0,0},
//        {1,1},
//        {3,0},
//        {6,0},
//        {2,1},
//        {4,0},
//        {5,0},
//        {2,1},
//        {5,0},
//        {0,0},
//        {3,1},
//        {1,0},
//        {2,0},
//        {5,1},
//        {4,0},
//        {1,0},
//        {0,1}
//        };
        
        for(int i = 0; i < refString.length; i++){
            
            boolean check = false;
            ArrayList<Integer> passedByFrames = new ArrayList<>();
            
            for(int j = 0; j < framesNum; j++){

               if(inFrames[j][0] == refString[i]){
                  check = true;
                //  inFrames[j][1] = 1;
                  break;
                } 
            }
            
            if(check == false){
                
                faults++;
                
                int replacementPointer = enhancedSecondChance_00(inFrames, p, i, intialModifyBit, passedByFrames);
                
                if( replacementPointer == -1 ) {
                
                    replacementPointer = enhancedSecondChance_01(inFrames, p, i, intialModifyBit, passedByFrames);
                
                    // Set all by passed r-bits to zero
                    for (int j = 0; j < passedByFrames.size(); j++) {
                        inFrames[(int)passedByFrames.get(j)][1] = 0;
                    }
                    
                    if( replacementPointer == -1 ) {
                        
                        replacementPointer = enhancedSecondChance_00(inFrames, p, i, intialModifyBit, passedByFrames);

                        if( replacementPointer == -1 ) {

                            replacementPointer = enhancedSecondChance_01(inFrames, p, i, intialModifyBit, passedByFrames);
                        }
                        else{
                            p = replacementPointer;
                        }
                    }
                    else{
                        p = replacementPointer;
                    }
                }
                else{
                    p = replacementPointer;
                }
                
            }
            
            for(int j = 0; j < framesNum; j++){
                memory[i][j] = inFrames[j][0];
                refBit[i][j] = inFrames[j][1];
                modifyBit[i][j] = inFrames[j][2];
            }
       }
        printThreeColumns(memory, refBit, modifyBit, faults, "Enhanced Second Chance Algorithm: ");
    }
    
    private static int enhancedSecondChance_00(int inFrames[][], int p, int i, int intialModifyBit[][], ArrayList passedByFrames){
        
        for (int j = p; j < inFrames.length+p; j++) {

            if( inFrames[p][1] == 0 && inFrames[p][2] == 0 ) {

                inFrames[p][0] = refString[i];
                inFrames[p][1] = 1;
                inFrames[p][2] = intialModifyBit[i][1];
                
//                if(passedByFrames.contains(p)){
//                    passedByFrames.remove((Object)p);
//                }

                p++;

                if(p == framesNum)
                    p = 0;

                
                return p;
            }
            else{
                // inFrames[p][1] = 0;
                
//                if(!passedByFrames.contains(p)){
//                    passedByFrames.add(p);
//                }
                        
                p++;

                if(p == framesNum)
                    p = 0;
            }
        }
        return -1;
    }
    
    private static int enhancedSecondChance_01(int inFrames[][], int p, int i, int intialModifyBit[][], ArrayList passedByFrames){
        
        for (int j = p; j < inFrames.length+p; j++) {

            if( inFrames[p][1] == 0 && inFrames[p][2] == 1 ) {

                inFrames[p][0] = refString[i];
                inFrames[p][1] = 1;
                inFrames[p][2] = intialModifyBit[i][1];
                  
                if(passedByFrames.contains(p)){
                    passedByFrames.remove((Object)p);
                }

                p++;

                if(p == framesNum)
                    p = 0;


                return p;
            }
            else{
                // inFrames[p][1] = 0;
                
                if(!passedByFrames.contains(p)){
                    passedByFrames.add(p);
                }
                
                p++;

                if(p == framesNum)
                    p = 0;
            }
        }
        return -1;
    }
    
    private static int getMinIndex(int[] arr){
        int minIndex = 0;
        for(int i=0; i<arr.length; i++){
            if(arr[i]<arr[minIndex])
                minIndex = i;
        }
        
        return minIndex;
    }
    
    private static int indexOf(int[] mem, int x){
        int j=0;
        for(j=0; j<framesNum; j++){
            if(x == mem[j]) {
                break;
            }
        }
        return j;
    }
      
    private static int linearSearch(int arr[], int key){ 
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == key) 
                return i; 
        }
        return -1; 
    }

    private static void printOneColumn(int[][] memory, int faults,String s){
     
        System.out.println("");
        for(int i=0; i<25; i++){
              System.out.print("--");
        }
        System.out.println("");
        System.out.println("Reference String:");    
        for (int i = 0; i < refString.length; i++) {
            System.out.printf("%4d ",refString[i]);
        }
        
        System.out.println("\n"); 
        System.out.println(s);
        for(int i = 0; i < framesNum; i++){
            for(int j = 0; j < refString.length; j++)
                System.out.printf("%4d ",memory[j][i]);
            System.out.println("");
        }
        
        System.out.println("The number of Faults: " + faults);    
    }
    
    private static void printTwoColumns(int[][] memory,int[][] validBit, int faults,String s){
       
       System.out.println("");
        for(int i=0; i<25; i++){
              System.out.print("--");
        }
        System.out.println("");
        System.out.println("Reference String:");    
        for (int i = 0; i < refString.length; i++) {
            System.out.printf("%5d  ",refString[i]);
        }
        System.out.println("\n"); 
       
        System.out.println(s);
        for(int i = 0; i < framesNum; i++){
            for(int j = 0; j < refString.length; j++)
                System.out.printf("%4d %d ",memory[j][i],validBit[j][i]);
            System.out.println("");
        }
        
        System.out.println("The number of Faults: " + faults);
    }
    
    private static void printThreeColumns(int[][] memory,int[][] refBit,int[][] modifyBit, int faults,String s){
       
       System.out.println("");
        for(int i=0; i<25; i++){
              System.out.print("--");
        }
        System.out.println("");
        System.out.println("Reference String:");    
        for (int i = 0; i < refString.length; i++) {
            System.out.printf("%6d   ",refString[i]);
        }
        System.out.println("\n"); 
       
        System.out.println(s);
        for(int i = 0; i < framesNum; i++){
            for(int j = 0; j < refString.length; j++)
                System.out.printf("%4d %d %d ",memory[j][i],refBit[j][i],modifyBit[j][i]);
            System.out.println("");
        }
        
        System.out.println("The number of Faults: " + faults);
    }
}

    

