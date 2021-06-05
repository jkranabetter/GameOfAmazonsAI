package ubc.cosc322;

import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) {
		// version 2 
		
		// create board
		Board_v2 board = new Board_v2();
		
		// create ai
		Player aiBlack = new RandomAI_v2(Board_v2.BLACK, board);
		Player aiWhite = new RandomAI_v2(Board_v2.WHITE, board);
		
		// print initial board state
		System.out.println(board);
		
		// create player tracker
		int player = Board_v2.BLACK; // switches from black to white and back untill win
		int turnCount = 0; // count iterations
		
		// timing fields
		long start, end, duration;
		
		// loop through gameplay
		while ( board.checkLose(player)==false ) {
			// display turn count
			System.out.println("Start turn " + turnCount++);
			// display colour moving
			switch (player) {
			case Board_v2.BLACK: System.out.println("Black is moving..."); break;
			case Board_v2.WHITE: System.out.println("White is moving..."); break;
			}
			// get turn start time
			start = System.nanoTime();
			// have ai make move
			if (player==Board_v2.BLACK) {
				ArrayList<ArrayList<Integer>> action = aiBlack.getAction();
				ArrayList<Integer> queenCurrent = action.get(0);
				ArrayList<Integer> queenMoved = action.get(1);
				ArrayList<Integer> arrow = action.get(2);
				board.applyAction(player, queenCurrent, queenMoved, arrow);
				board.outputActionToConsole(queenCurrent, queenMoved, arrow);
			}
			else {
				ArrayList<ArrayList<Integer>> action = aiWhite.getAction();
				ArrayList<Integer> queenCurrent = action.get(0);
				ArrayList<Integer> queenMoved = action.get(1);
				ArrayList<Integer> arrow = action.get(2);
				board.applyAction(player, queenCurrent, queenMoved, arrow);
				board.outputActionToConsole(queenCurrent, queenMoved, arrow);
			}
			// get turn end time
			end = System.nanoTime();
			// calculate turn duration in seconds
			duration = (end - start) / (int)(Math.pow(10, 9));
			System.out.println("AI took " + (duration) + " seconds to make decision.");
			// display new board
			System.out.println(board);
			// switch player
			player = (player==Board_v2.BLACK) ? (Board_v2.WHITE) : (Board_v2.BLACK);
			// break; // TESTING
		}
		
		// declare winner
		if (player==Board_v2.BLACK) {
			System.out.println("Black unable to move. White wins.");
		}
		else if (player==Board_v2.WHITE){
			System.out.println("White unable to move. Black wins.");
		}
		else {
			System.out.println("Somehow nobody wins");
		}
		
//		// testing regions
//		
//		// create board
//		Board_v2 board = new Board_v2();
//		
//		// create ai
//		Player aiBlack = new RandomAI_v2(Board_v2.BLACK, board);
//		Player aiWhite = new RandomAI_v2(Board_v2.WHITE, board);
//		
//		// print initial board state
//		System.out.println(board.regionsToString());
//		
//		// create player tracker
//		int player = Board_v2.BLACK; // switches from black to white and back untill win
//		int turnCount = 0; // count iterations
//		
//		// timing fields
//		long start, end, duration;
//		
//		// loop through gameplay
//		while ( board.checkLose(player)==false ) {
//			// display turn count
//			System.out.println("Start turn " + turnCount++);
//			// display colour moving
//			switch (player) {
//			case Board_v2.BLACK: System.out.println("Black is moving..."); break;
//			case Board_v2.WHITE: System.out.println("White is moving..."); break;
//			}
//			// get turn start time
//			start = System.nanoTime();
//			// have ai make move
//			if (player==Board_v2.BLACK) {
//				ArrayList<ArrayList<Integer>> action = aiBlack.getAction();
//				ArrayList<Integer> queenCurrent = action.get(0);
//				ArrayList<Integer> queenMoved = action.get(1);
//				ArrayList<Integer> arrow = action.get(2);
//				board.applyAction(player, queenCurrent, queenMoved, arrow);
//				board.outputActionToConsole(queenCurrent, queenMoved, arrow);
//			}
//			else {
//				ArrayList<ArrayList<Integer>> action = aiWhite.getAction();
//				ArrayList<Integer> queenCurrent = action.get(0);
//				ArrayList<Integer> queenMoved = action.get(1);
//				ArrayList<Integer> arrow = action.get(2);
//				board.applyAction(player, queenCurrent, queenMoved, arrow);
//				board.outputActionToConsole(queenCurrent, queenMoved, arrow);
//			}
//			// get turn end time
//			end = System.nanoTime();
//			// calculate turn duration in seconds
//			duration = (end - start) / (int)(Math.pow(10, 9));
//			System.out.println("AI took " + (duration) + " seconds to make decision.");
//			// display new board
//			System.out.println(board.regionsToString());
//			// switch player
//			player = (player==Board_v2.BLACK) ? (Board_v2.WHITE) : (Board_v2.BLACK);
//			// break; // TESTING
//		}
//		
//		// declare winner
//		if (player==Board_v2.BLACK) {
//			System.out.println("Black unable to move. White wins.");
//		}
//		else if (player==Board_v2.WHITE){
//			System.out.println("White unable to move. Black wins.");
//		}
//		else {
//			System.out.println("Somehow nobody wins");
//		}
	}

}
