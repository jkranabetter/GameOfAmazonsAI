package ubc.cosc322;

public class Main {
	
	public static void main(String[] args) {
		// create board
		Board board = new Board();
		
		// create AI
		RandomAI aiBlack = new RandomAI(Board.BLACK, board);
		RandomAI aiWhite = new RandomAI(Board.WHITE, board);
		
		// print initial board state
		System.out.println(board);
		
		// create player tracker
		int player = Board.BLACK; // switches from black to white and back untill win
		int turnCount = 0; // count iterations
		
		// timing fields
		long start, end, duration;
		
		// loop through gameplay
		while ( board.checkLose(player)==0 ) {
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
				aiBlack.makeAction(board);
			}
			else {
				aiWhite.makeAction(board);
			}
			// get turn end time
			end = System.nanoTime();
			// calculate turn duration
			duration = end - start;
			System.out.println("AI took " + (duration) + " nanoseconds to make decision.");
			// display new board
			System.out.println(board);
			// switch player
			player = (player==Board.BLACK) ? (Board.WHITE) : (Board.BLACK);
		}
		
		// declare winner
		if (player==Board.BLACK) {
			System.out.println("Black loses. White wins.");
		}
		else if (player==Board.WHITE){
			System.out.println("White loses. Black wins.");
		}
		else {
			System.out.println("Somehow nobody wins");
		}
		
//		// TESTING
//		for (int i=0; i<3; i++) {
//			aiBlack.makeMove(board);
//			System.out.println(board);
//		}
		

	}

}
