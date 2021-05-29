package ubc.cosc322;

import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) {
		// version 2 
		
		// create board
		Board_v2 board = new Board_v2();
		
		// create ai
		Player aiBlack = new SmartAI(Board.BLACK, board);
		Player aiWhite = new SmartAI(Board.WHITE, board);
		
		// print initial board state
		System.out.println(board);
		
		// create player tracker
		int player = Board.BLACK; // switches from black to white and back untill win
		int turnCount = 0; // count iterations
		
		// timing fields
		long start, end, duration;
		
		// loop through gameplay
		while ( board.checkLose(player)==false ) {
			// display turn count
			System.out.println("Start turn " + turnCount++);
			// display colour moving
			switch (player) {
			case Board.BLACK: System.out.println("Black is moving..."); break;
			case Board.WHITE: System.out.println("White is moving..."); break;
			}
			// get turn start time
			start = System.nanoTime();
			// have ai make move
			if (player==Board.BLACK) {
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
			player = (player==Board.BLACK) ? (Board.WHITE) : (Board.BLACK);
			// break; // TEMPORARY
		}
		
		// declare winner
		if (player==Board.BLACK) {
			System.out.println("Black unable to move. White wins.");
		}
		else if (player==Board.WHITE){
			System.out.println("White unable to move. Black wins.");
		}
		else {
			System.out.println("Somehow nobody wins");
		}
	}

}
