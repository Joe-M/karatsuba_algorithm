/*************************

Integer Multiplication using the standard multiplication algorithm and Karatsuba's algorithm for fast multiplication of large integers

Summary:
---------------------------
Binary numbers are read in via a text file. Each number is held in a char array which is completely unnecessary but was necessary at the time.
Because of this, there are a lot of array to arraylist and arraylist to array translations for better array manipulation.
Hence the 2 methods, "arrayToList" and "listToArray" that are used more frequently then I'd like.

The text file should specifie the number of multiplications as the first integer, lets say "n".

Then "n" pairs of binary numbers will be listed, each beginning with an integer that specifies the binary number's length.

Printed to the console will be the result of a long muliplication algorithm, and then Karatsubas algrothim (both results should be the same for every pair of binary numbers)
----------------------------

***************************/


import java.io.*;
import java.util.*;
import java.io.File;


public class Multiplication {


	static char[] longMult(char[] num1, char[] num2){
		//Long Muliplication

		//We're going to make a bunch of binary numbers (num2.length to be exact) that we're going to add together at the end.
		char[][] allSumOperands = new char[num2.length][];

		for(int i = num2.length-1; i>=0; i--){
			//This look goes through every digit in the second binary number. After multiplying every digit in the first number by that second number,
			//This will come up with one of n(num2.lngth) binary numbers that will be added together.
			ArrayList<Character> sumOperand = new ArrayList<Character>();
			char currentMulitplierDigit = num2[i];

			if(currentMulitplierDigit == '0'){
				//The current multiplier is 0, meaning that when we muliplty all digits in the 1st binary number by currentMulitplierDigit, result will be 0
				//So we shouldn't even waste time going through the multiplication process for that digit, or even adding it to the sum for that matter
				continue;

			} else {

				for(int x =num1.length-1; x>=0; x--){
					//This loop "multiplies" every bianry digit in the 1st binary number by a number (ith digit) in the first binary number
					if(num1[x] == '1'){
						sumOperand.add(0,'1');
					}else{
						sumOperand.add(0,'0');
					}

				}
			}
			
			//After we have another operand from multiplying, we need to add 0s to it depending on what digit we are on in the first loop / second number.
			int zerosToAdd = num2.length - 1 - i;
			while(zerosToAdd !=0){
				sumOperand.add('0');
				zerosToAdd--;
			}

			//Now add the computed product to the sum array. But first, translate back to array
			char[] sumOperandArray = new char[sumOperand.size()];
			for (int h=0;h<sumOperand.size();h++ ) {
				sumOperandArray[h] = sumOperand.get(h);
			}
			allSumOperands[i] = sumOperandArray;

		}

		//No that we have all the product results, add them all up
		char[] sum = add(allSumOperands, false);
		return sum;
	}




	//Pre-condition: Uses an array of char arrays (aka and array of binary numbers). 
	//				 Char arrays list binary numbers IN ORDER. So binary number 1011 in a char array
	//				 has char[0] = 1, char[1] = 0, char[2] = 1, char[3] = 1
	static char[] add(char[][] numbers, boolean forSubtraction){

		//forSubtraction is for if we should drop the overflow bit in the case that we are adding a 2 complement's to compute a subtraction

		ArrayList<Character> sum = new ArrayList<Character>();
		//Using an array list so the length is not restricted
		//Initialize sum to all 0s

		int i=0;
		// There can be null values in the arrary before actual bianry numbers exist. 
		//This loop finds the point in the array where the first value exists
		if(numbers[i] == null){
			while(numbers[i] == null){
				i++;

			}
		}

		//This loop populates the sum list
		for (int p = 0; p < numbers[i].length;p++){
			sum.add('0');
		}

		for( i=0;i<numbers.length;i++){
			//loop through all the numbers we're adding, and add them to sum
			char[] currentOperandArray = numbers[i];
			if(currentOperandArray == null){
				continue;
			}
		
			//currentOperandArray = the current operand we're adding. Next, convert to arraylist to adjust length if necessary
			ArrayList<Character> currentOperand = new ArrayList<Character>();
			for(char c : currentOperandArray){
				currentOperand.add(c);
			}
			//currentOperand will hold the current number we are adding to the sum. It's just an arrayList version of the current binary number

			//its possible the sum will be longer then the sum is currently. To make things easier, extend the currentOp if so
			if(currentOperand.size() < sum.size()){
				int numberOfZerosToAdd = sum.size() - currentOperand.size();
				//Add 0's to the front of the shorter binary number so they're the same length
				while(numberOfZerosToAdd >0){
					currentOperand.add(0,'0');
					numberOfZerosToAdd--;
				}
			}else if(currentOperand.size() > sum.size()){
				//it's also possible one binary number is longer than the current sum.
				int numberOfZerosToAdd = currentOperand.size() - sum.size();
				//Add 0's to the front of the shorter binary number so they're the same length
				while(numberOfZerosToAdd >0){
					sum.add(0,'0');
					numberOfZerosToAdd--;
				}
			}

			char overflow = '0';
			for (int h = currentOperand.size()-1; h>=0;h--){
				//This loop goes through every binary digit and adds it to the sum

				if(currentOperand.get(h) == '1' && sum.get(h) == '1'){
					//case: 1 + 1
					if(overflow == '1'){
						sum.set(h,'1');
					}else{
						sum.set(h,'0');
						overflow = '1';
					}

				}else if (currentOperand.get(h) == '0' && sum.get(h) == '0'){
					//case: 0 + 0
					if(overflow == '1'){
						sum.set(h,'1');
						overflow = '0';
					}else{
						sum.set(h,'0');
					}

				}else{
					//case: 1 + 0
					if (overflow == '1') {
						sum.set(h,'0');
						overflow = '1';
					}else{
						sum.set(h,'1');
						
					}
				}

			}
			//Adding is over. If there is still a one being carried over in the overflow, add it
			if (overflow == '1' && !forSubtraction) {
				sum.add(0, '1');
			}


		}

		//Now translate the sum arraylist back to array
		char[] sumArray = new char[sum.size()];
		for (int x=0;x<sum.size();x++ ) {
			sumArray[x] = sum.get(x);
		}

		return sumArray;
	} 





	//Pre-condition: Takes in only 2 binary numbers for the sake of simplicity, because things can get complicated because the order that the numbers are subtracted
	//				 actually matters, unlike in addition. num2 will be subtracted from num1. Will calclutae num1-num2
	static char[] subtract(char[] num1, char[] num2){

		//Before doing anything, make sure both numbers are the same size. If not, add dem zeros
		ArrayList<Character> num1List = arrayToList(num1);
		ArrayList<Character> num2List = arrayToList(num2);
		if (num1.length>num2.length) {
			int zerosToAdd = num1.length - num2.length;
			while(zerosToAdd != 0){
				num2List.add(0,'0');
				zerosToAdd--;
			}
		}else if(num1.length< num2.length){
			int zerosToAdd = num2.length-num1.length;
			while(zerosToAdd != 0){
				num1List.add(0,'0');
				zerosToAdd--;
			}

		}

		num1 = listToArray(num1List);
		num2 = listToArray(num2List);

		//First, translate num2 to a negative 2's compliment number
		for(int w =0;w<num2.length;w++){
			if (num2[w] == '0') {
				num2[w] = '1';
			}else
				num2[w] = '0';
		}

		char[][] allSumOperands = new char[2][];
		//Make an array that includes num2 and '1'
		allSumOperands[0] = num2;
		char[] one = {'1'};
		allSumOperands[1] = one;
		num2 = add(allSumOperands, true);


		//Now that num2 is negative, we can just add num1 and num2
		char[][] bothNumbersArray = {num1,num2};
		return add(bothNumbersArray, true);

	}

	static char[] karatsuba(char[] num1Array, char[] num2Array){

		//First thing: make ArrayLists of each number
		
		ArrayList<Character> num1 = new ArrayList<Character>();
		for(char c : num1Array){
			num1.add(c);
		}
		ArrayList<Character> num2 = new ArrayList<Character>();
		for(char c : num2Array){
			num2.add(c);
		}

		//Now check make sure both binary numbers have the same # of bits
		if (num1.size()>num2.size()) {
			int numberOfZerosToAdd = num1.size()-num2.size();
			while(numberOfZerosToAdd !=0){
				num2.add(0,'0');
				numberOfZerosToAdd--;
			}
			
		}else if(num1.size()<num2.size()){
			int numberOfZerosToAdd = num2.size()-num1.size();
			while(numberOfZerosToAdd !=0){
				num1.add(0,'0');
				numberOfZerosToAdd--;
			}
			
		}

		//Now we need to make sure the bit length of each number is a power of 2. Power of 2s will only have one '1' bit in the binary number
		int powerOfTwo = 1;
		while(powerOfTwo<=num1.size()){
			if(powerOfTwo == num1.size()){
				//Num1 is a bit size of power of 2. So is num2, since they are the same length.
				break; 
			}
			powerOfTwo = powerOfTwo*2;
		}
		if (powerOfTwo>num1.size()) {
			//If we didn't find a power of two that matched the bit size, we're gonna have to add 0s
			powerOfTwo = powerOfTwo - num1.size();
			//Reuce powerOfTwo to the amount of 0s we'll add
			while(powerOfTwo !=0){
				num1.add(0,'0');
				num2.add(0,'0');
				powerOfTwo--;
			}
		}
		int size = num1.size();
		

		//The to binary numbers are the same length, each a length of a power of 2. Now the multiplication actually begins.
		if (size == 1) {
			//Base case: If the length of each binary number is one bit, we do literal muliplication
			if(num1.get(0) == '0' || num2.get(0) == '0'){
				char[] zero = {'0'};
				return zero;
			}else{
				char[] one = {'1'};
				return one;
			}
		}

		ArrayList<Character> a1 = new ArrayList<Character>(num1.subList(0,size/2));
		ArrayList<Character> a0 = new ArrayList<Character>(num1.subList(size/2,size));
		ArrayList<Character> b1 = new ArrayList<Character>(num2.subList(0,size/2));
		ArrayList<Character> b0 = new ArrayList<Character>(num2.subList(size/2,size));

		char[] c2 = karatsuba(listToArray(a1),listToArray(b1));
		char[] c0 = karatsuba(listToArray(a0), listToArray(b0));

		
		char[] c1 = karatsuba(add(arrayOfBinaryArrays( listToArray(a1), listToArray(a0)), false), add(arrayOfBinaryArrays( listToArray(b1), listToArray(b0)),false));
		

		c1 = subtract(c1, add(arrayOfBinaryArrays( c2,c0),false));



		//And now we'll tack on the 0s to each binary number for "c", instead of wasting time multiplying in the "c" assignment
		int zeros = size;

		ArrayList<Character> c2List = arrayToList(c2);
		while(zeros !=0){
			c2List.add('0');
			zeros--;
		}
		c2 = listToArray(c2List);

		zeros = size/2;
		ArrayList<Character> c1List = arrayToList(c1);
				

		while(zeros != 0){
			c1List.add('0');
			zeros--;
		}
		c1 = listToArray(c1List);

		//Now we can calculate c and return it

		return add(arrayOfBinaryArrays(c2,c1,c0), false);

	}


	static char[] listToArray(ArrayList<Character> list){
		char[] array = new char[list.size()];
		for(int u=0;u<list.size();u++){
			array[u] = list.get(u);
		}
		return array;
	}


	static ArrayList<Character> arrayToList(char[] array){

		ArrayList<Character> list = new ArrayList<Character>();
		for(char c : array){
				list.add(c);
			}

			return list;
	}


	//Pre-condition: Can ONLY use 3 arrays, nor more no less. Because laziness
	static char[][] arrayOfBinaryArrays(char[] num1, char[] num2, char[] num3){
		//This takes in arrays of bits (binary numbers) and makes an array of them
		char[][] binaryArray = {num1, num2, num3};


		return binaryArray;
	}
	//Pre-condition: This method is the same as the above metod, but only takes n binary numbers.
		public static char[][] arrayOfBinaryArrays(char[] num1, char[] num2){
		//This takes in arrays of bits (binary numbers) and makes an array of them
		char[][] binaryA = {num1,num2};

		return binaryA;
	}



	public static void printBinary(char[] num){
		num = trimBinaryNumber(num);
		for(int i=0; i< num.length;i++){
			System.out.printf("%c", num[i]);
		}
		System.out.printf("\n");
	}


	//Post-condition: The fancy muliplication algorithm doesn't trim off excess 0's at the front of the binary number. This does that.
	static char[] trimBinaryNumber(char[] num){
		ArrayList<Character> numList = arrayToList(num);
		while(numList.get(0) == '0'){
			numList.remove(0);
		}
		return listToArray(numList);

	}





	public static void main(String [] args) throws Exception{

		Scanner sc = new Scanner(new File("mult.txt"));
		int numberOfMultiplications = sc.nextInt();

		while(numberOfMultiplications != 0){
			//Loop for each multiplication

			int lengthOfFirstNum = sc.nextInt();
			char[] num1 = new char[lengthOfFirstNum];
			String number = sc.next();
			for (int y=0;y<lengthOfFirstNum ;y++ ) {
				num1[y] = number.charAt(y);
			}


			int lengthOfSecondNum = sc.nextInt();
			char[] num2 = new char[lengthOfSecondNum];
			String number2 = sc.next();
			for (int y=0;y<lengthOfSecondNum ;y++ ) {
				num2[y] = number2.charAt(y);
			}

			//num1 and num2 hold the 2 binary numbers

			char[] product1 = longMult(num1, num2);
			//Long muliplication
			printBinary(product1);

			char[] prodcut2 = karatsuba(num1,num2);
			//karatsuba muliplication
			printBinary(prodcut2);

			numberOfMultiplications--;
		}


	}
	
}


