package com.learnreactivespring;

import com.learnreactivespring.router.TreeNodeTraversal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

@SpringBootApplication
public class LearnReactivespringApplication {

	public static void main(String[] args) {

//		lexicalProblem();
		new TreeNodeTraversal().findDiagonalSum();

		findPairs(new int[] {1,5,7,-1}, 6);

		SpringApplication.run(LearnReactivespringApplication.class, args);
	}

	private static void lexicalProblem() {
		Scanner sc = new Scanner(System.in).useDelimiter("\n");
		Integer numberOfTestCases = 1;//sc.nextInt();

		for (int i = 0; i < numberOfTestCases; i++) {
//			String[] inputData = sc.next().split(" ");
			int textLength = 8;//Integer.parseInt(inputData[0]);
			int pairsCount = 4;//Integer.parseInt(inputData[1]);

			char[] sCharArray = "abagfiab".toCharArray();//sc.next().toCharArray();
			char[] tCharArray = "cbacbcda".toCharArray();//sc.next().toCharArray();

			for (int pair = 0; pair < pairsCount; pair++) {
				String[] pairs = sc.next().split(" ");

				int value1 = Integer.parseInt(pairs[0]);
				int value2 = Integer.parseInt(pairs[1]);

				if(tCharArray[value1] > tCharArray[value2]) {
					//Swap
					char temp = tCharArray[value1];
					tCharArray[value1] = tCharArray[value2];
					tCharArray[value2] = temp;
				}
			}

			for (int j = 0; j < textLength; j++) {
				if (sCharArray[j] > tCharArray[j]) {
					//swap
					char temp = sCharArray[j];
					sCharArray[j] = tCharArray[j];
					tCharArray[j] = temp;
				}
			}

			System.out.println(new String(sCharArray));
		}
	}

	private void chessKnightProblem() {
		Scanner sc = new Scanner(System.in).useDelimiter("\n");
		Integer numberOfTestCases = 1;//sc.nextInt();
		int[] xMoves = {2, 1, -1, -2, -2, -1, 1, 2};
		int[] yMoves = {1, 2, 2, 1, -1, -2, -2, -1};

		for (int i = 0; i < numberOfTestCases; i++) {
//			String[] matrixSize = sc.next().split(" ");
			int rows = 8;//Integer.parseInt(matrixSize[0]);
			int columns = 8;// Integer.parseInt(matrixSize[1]);
//			char[][] chessBoard = new char[rows][columns];

//			char[][] chessBoard = new char[][] {
//					{'n','_','_'},
//					{'_','_','_'},
//					{'_','_','n'}
//			};

//			char[][] chessBoard = new char[][] {
//					{'-','_','_','_','_'},
//					{'-','_','n','_','_'},
//					{'-','_','n','_','_'},
//					{'-','_','n','_','_'},
//					{'-','_','_','_','_'},
//			};

			char[][] chessBoard = new char[][] {
					{'_','_','_','_','_','_','_','_'},
					{'_','_','_','_','_','n','_','_'},
					{'_','n','n','_','n','n','_','_'},
					{'_','_','n','_','_','_','_','_'},
					{'_','_','_','_','_','n','_','_'},
					{'_','_','n','n','_','n','n','_'},
					{'_','_','n','_','_','_','_','_'},
					{'_','_','_','_','_','_','_','_'},
			};

			int attackPositions = 0;

			int totalKnights = 0;
			for (int row = 0; row < chessBoard.length; row++) {
				for (int col = 0; col < chessBoard[row].length; col++) {
					if (chessBoard[row][col] == 'n') {
						totalKnights++;
						for (int j = 0; j < 8; j++) {
							int xMove = row + xMoves[j];
							int yMove = col + yMoves[j];

							if (xMove >= 0 && yMove >= 0 && xMove < rows && yMove < columns
									&& chessBoard[xMove][yMove] != 'n' && chessBoard[xMove][yMove] != 'm') {
								attackPositions++;
								chessBoard[xMove][yMove] = 'm';
							}
						}
					}
				}
			}

			System.out.println((rows * columns) - attackPositions - totalKnights);
		}

		sc.close();
	}

	public static void findPairs(int[] inputArray, int givenSum) {
		int totalPairs = 0;
		List<Integer> foundItems = newArrayList();
		for(int i=0 ;i<inputArray.length ; i++ ) {
			int temp = givenSum - inputArray[i];
			if(foundItems.contains((Integer)temp)) {
				totalPairs++;
			}
			foundItems.add(inputArray[i]);
		}

		System.out.println(totalPairs);
	}


}
