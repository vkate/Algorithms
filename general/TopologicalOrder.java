import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * @author Vamsi Katepalli
 * program that visits all permutations of n distinct objects,
	numbered 1..n, that satisfy a given set of precedence constraints.  A
	precedence constraint (a,b) means that we are interested in only those
	permutations in which a appears before b.
	
	To implement, algorithm written by Y.L Varol and D. Rotem 
	to generate all topological sorting arrangements is used.
 */


public class Topological_Sort {
	
	static int n;static int v;
	static List<Pair> constraintlist;
	static int count;
	
	/**
	 * @param args
	 * Main method.
	 */
	public static void main(String[] args) {
		try{
			//Using scanner and take input n and v.
			Scanner input = new Scanner(new BufferedInputStream(System.in));
			n = input.nextInt();
			int c = input.nextInt();
			v = input.nextInt();

			int countCheck = 0;
			constraintlist = new ArrayList<Pair>();
			int a =0;int b =0;
			while(input.nextLine()!=null){
				a = input.nextInt();
				b = input.nextInt();
				constraintlist.add(new Pair(a,b));
				countCheck++;
				if(c==countCheck)
					break;
			}
			int[] inputArray = getPartialTopologicalOrder(n);
			int[] constraintArrayR = new int[n+1]; 
			for(int i=0;i<inputArray.length;i++){
				constraintArrayR[inputArray[i]]=i;
			}
			input.close();
			
			//calling the function with the input array.
			//value index 0 is kept 0. 
			int[] A = new int[n+2];
			int[] B = new int[n+2];
			for(int i = 0;i<n;i++){
				A[i+1] = i+1;
				B[i+1] = i+1;
			}
		 
			long currentTime = System.currentTimeMillis();
			getTotalPermutations(A, B,inputArray,constraintArrayR);
			System.out.println(count+" "+(System.currentTimeMillis()-currentTime));
			
			
		}catch(NumberFormatException numberFormatException){
			System.out.println("Please give integers in the input");
			numberFormatException.printStackTrace();
		}
		catch(ArrayIndexOutOfBoundsException boundsException){
			System.out.println("There is some issue in the input. Please enter in the format \"n v\" ");
			boundsException.printStackTrace();
		}
		catch(Exception exception)
		{
			System.out.println("There is some issue exception: "+exception.getMessage());
			exception.printStackTrace();
		}
	}

	static boolean flag = true;
	
	/**
	 * @param A input array where permutation is stored
	 * @param B inverse array of A
	 * @param k integer used in recursion.
	 * @param j integer value used in function
	 * @param linteger value used in function
	 * @return boolean
	 * This is the main function of this problem. It is the implementation of
	 * algorithm written by Y.L Varol and D. Rotem.
	 * a. Initially we start with an array 1..n, and we visit the array.
	 * b. We mark the n and n-1 of A and check if there is 
	 *    a constraint. If it is not a constraint,we swap the elements and move
	 *    on to next two elements.
	 * c. If we find a constraint, we backtrack to the 
	 *    array till the last time we got constraint or first step. Now we mark
	 *    n-1 and n-2 and repeat the steps. But this algorithm works if constraints
	 * 	  are in increasing order and input is in increasing order. To work with random constraints,
	 * 	  we convert the problem analogous to increasing order by mapping the input array to 
	 *    increasing sequence Ex: 2 1 3 4 5 to 1 2 3 4 5. Also constraints are checked in similar way. 
	 */
	private static void getTotalPermutations(int[] P,int[] LOC,int[] inputArr,int[] inputConstrPair){
		visit(P, v,inputArr);
		int i = 1;int k =0;int k1=0;int obj_k=0;int obj_k1=0;
		while(i<n){
			k = LOC[i];
			k1 = k+1;
			obj_k=P[k];
			obj_k1=P[k1];
			if(checkConstraint(i,obj_k1,inputConstrPair)){
				for(int l=k;l>=i+1;l--){
					P[l] = P[l-1];					
				}
				P[i] = obj_k;
				LOC[i]=i;
				i = i+1;
			}else{
				P[k]= obj_k1;
				P[k1] =obj_k;
				LOC[i] = k1;
				i = 1;
				visit(P, v, inputArr);
			}
		}
	}
	
	
	/**
	 * @param l first element
	 * @param k second element
	 * @return boolean
	 * This function is to check if constraint exists between l and k.
	 * Also constraint between 0 and k is true.
	 */
	private static boolean checkConstraint(int l, int k,int[] inputArr) {
		
		if(l==0 || k ==0 || k==l)
			return true;
		else{
			for(Pair pair:constraintlist){
				if((inputArr[pair.getFirstInt()]==l && inputArr[pair.getSecondInt()] == k))
					return true;
			}
		}
		return false;
	}

	/**
	 * @param A input array
	 * @param count
	 * @param verbose
	 * @return long
	 * This function prints the array after it has one permutation.
	 */
	static long visit(int[] A,int verbose,int[] inputArr)
	{
	   if (verbose > 0) {
	      for(int i = 0; i < n+1; i++) {
			 if (A[i] > 0) System.out.print(inputArr[A[i]]+ " " );
		  }
	      System.out.println();
	   }
	   return count++;
	}

	
	/**
	 * This method gets a permutation for the given set of constraints.
	 * It uses Algorithm T in section 2.2.3 of Art of Computer programming book.
	 * This algorithm first creates a model for the input data in a particular order,
	 * where it maps the constraint associated like a chain and has a counter about
	 * how many times the element is repeated in constraints.
	 * Ex: if constraints are (1 3), (2 4), (1 5)then the model is below:
	 * 			1  2  3  4  5
	 * count	0  0  1  1  1
	 * links	3  4  
	 * 			5
	 * 
	 * After this step we output initially the values with count 0 and while we output the value,
	 * we decrease the count of corresponding element which it is linked to and if it is 0,
	 * we output the value.
	 * 
	 * @param n number of elements
	 * @return a partial topological sort with given constraints.
	 */
	static int[] getPartialTopologicalOrder(int n){
		//We use count Array and array of linkClass elements.
		int[] countArray = new int[n+1];
		linkClass[] tempArray = new linkClass[n+1];
		
		//We implement queue behaviour using two arrays.
		int[] outputArray = new int[n+2];
		int[] outputArrayTemp = new int[n+1];
		
		//Iterate through constraint pairs and create the initial memory model
		for(Pair pair:constraintlist){
			countArray[pair.getSecondInt()] = countArray[pair.getSecondInt()] + 1;
			linkClass classtemp = new linkClass();
			classtemp.succussor = pair.getSecondInt();
			classtemp.next = tempArray[pair.getFirstInt()];
			tempArray[pair.getFirstInt()] = classtemp;			
		}
		//initialize the output array with the values for which count is 0.
		int j = 1;
		for(int i=1;i<countArray.length;i++){
			if(countArray[i]==0){
				outputArrayTemp[j++] = i;
				
			}
		}
		
		//iterate through the values for which count is 0 and decrement the count 
		//for which the value is linked.
		for(int i=1;i<outputArrayTemp.length;i++){
			if(outputArrayTemp[i] !=0){
				outputArray[i]=outputArrayTemp[i];
				outputArrayTemp[i]=0;
				linkClass var =  tempArray[outputArray[i]];
				if(var == null)
					continue;
				else{
					while(var!=null){
						countArray[var.succussor]=countArray[var.succussor]-1;
						if(countArray[var.succussor]==0){
							outputArrayTemp[j++]=var.succussor;
						}
						var = var.next;
					}
				}
			}
		}
		return outputArray;
	}
}

//linkClass used to get partial order permutation.
class linkClass{
	int succussor;
	linkClass next;
	@Override
	public String toString() {
		return "linkClass [succussor=" + succussor + ", next=" + next + "]";
	}
	
}

/**
 * @author Vamsi Katepalli
 * This class is used to store the pairs of constraints.
 */
class Pair{
	private int firstInt;
	private int secondInt;
	public int getFirstInt() {
		return firstInt;
	}
	public void setFirstInt(int firstInt) {
		this.firstInt = firstInt;
	}
	public int getSecondInt() {
		return secondInt;
	}
	public void setSecondInt(int secondInt) {
		this.secondInt = secondInt;
	}
	public Pair(int firstInt, int secondInt) {
		super();
		this.firstInt = firstInt;
		this.secondInt = secondInt;
	}
	
}



