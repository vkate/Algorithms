import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Vamsi Katepalli
 * 
 */

/**
 * program that visits all permutations/combinations of k objects
 * 
   

   Sample input:
   4 2 2

   Sample output:
   1 2
   1 3
   1 4
   2 1
   2 3
   2 4
   3 1
   3 2
   3 4
   4 1
   4 2
   4 3
   12 0
   
 */

public class Permutations_Combinations {

	
	/**
	 *Global variable for number of integers. 
	 */
	public static int n;
	
	/**
	 * @param args
	 * @throws IOException 
	 * Main method
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		String input;
		try{
		while((input = bf.readLine()) != null){
			String[] inputArray = input.split(" ");
			runProgram(Integer.parseInt(inputArray[0]),Integer.parseInt(inputArray[1]),Integer.parseInt(inputArray[2]));
		}
		}catch(NumberFormatException numberFormatException){
			System.out.println("Please give integers in the input");
		}
		catch(ArrayIndexOutOfBoundsException boundsException){
			System.out.println("There is some issue in the input. Please enter in the format \"n k v\" ");
		}
		catch(Exception exception)
		{
			System.out.println("There is some issue exception: "+exception.getMessage());
			exception.printStackTrace();
		}
	}
	
	/**
	 * @param inputSize
	 * @param option
	 * @return boolean (true or false)
	 * This method validates the input range for this problem.
	 * 3<n<1000 and 0<v<4.
	 */
	private static boolean validateInput(int inputSize, int option) {
		if(inputSize < 3 || inputSize > 1000 || option <0 || option>3)
			return false;
		else
			return true;
	}
	
	
	/**
	 * @param inputSize 
	 * @param k number of combinations
	 * @param v verbose input
	 * Starting point of the problem to initialize values 
	 * and call permutaion or combination based on verbose input
	 */
	static void runProgram(int inputSize,int k,int v){
		n = inputSize;
		int[] A = new int[n];
		int[] B = new int[n];
		
		if(!validateInput(n, v)){
			System.out.println("Please enter correct values for n and v");
			return;
		}
		
		long count=0;
		long a = System.currentTimeMillis();
		if(v == 0 || v == 2)
			count = permute(A,B,n,k,v,count);
		else
			count = combinations(A,n-1,k,v,count);
		System.out.println(count + " "+ (System.currentTimeMillis()-a));
	}
	
	/**
	 * @param A Array used to store combinations
	 * @param i number of elements
	 * @param k number of combinations
	 * @param v verbose input
	 * @param count
	 * @return count of combinations after visit.
	 * 
	 * This method is called recursively and check for combinations.
	 * If there is combination we want to visit, we insert 1 in the 
	 * input array and 0  for which we don't want to add.
	 * While visiting the array, we print indexes of array for which A[i]=1.
	 */
	private static long combinations(int[] A, int i,int k,int v, long count) {
		if(k==0)
			count = visitCombinations(A, count, v);
		else if(i==-1 && k > 0)
			return count;
		else
		{
			A[i]=1;
			count = combinations(A, i-1, k-1, v,count);
			A[i]=0;
			count = combinations(A, i-1, k, v,count);
		}
		return count;
	}

	/**
	 * @param A Array which is used to store permutation
	 * @param B Array used to visit permutation
	 * @param i number of elements 
	 * @param k number of permutations 
	 * @param v verbose value
	 * @param count of permutations.
	 * @return count of permutations after visit.
	 * This method is used recursively to visit the permutations.
	 * We use auxillary array B to store the current permutation
	 * and visit the permutation when the level of recursion is 0.
	 */
	static long permute(int[] A,int[] B,int i,int k, int v,long count){
		if(k==0)
			count = visitPermutations(B,count,v);
		else{
			for(int j=0;j<=n-1;j++){
				if(A[j] == 0){
					A[j]=j+1;
					B[n-k] = A[j];
					count = permute(A,B,i,k-1,v,count);
					A[j]=0;
				}
			}
		}
		return count;
	}
	
	
	/**
	 * @param A: input array
	 * @param count: long value to increase count
	 * @param verbose: verbose input value to print.
	 * @return count of current values
	 * 
	 * This method is used to visit a permutation 
	 * when it is inserted in the array.
	 */
	static long visitPermutations(int[] A,long count,int verbose)
	{
	   if (verbose > 0) {
	      for(int i = 0; i < n; i++) {
			 if (A[i] > 0) System.out.print(A[i]+ " " );
		  }
	      System.out.println();
	   }
	   return count+1;
	}
	
	/**
	 * @param A: input array
	 * @param count: long value to increase count
	 * @param verbose: verbose input value to print.
	 * @return count of current values.
	 * 
	 * This method is used to visit a combination 
	 * when it is inserted in the array.
	 */
	static long visitCombinations(int[] A,long count,int verbose)
	{
	   if (verbose == 3) {
	      for(int i = 0; i < n; i++) {
			 if (A[i] == 1) System.out.print((i+1)+ " " );
		  }
	      System.out.println();
	   }
	   return count+1;
	}

}
