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
		int player = Board.BLACK; // switches from black to white to black till win
		int turnCount = 0; // count iterations
		
		// loop through gameplay
		while ( board.checkWin()==0 ) {
			// display turn count
			System.out.println("Start turn " + turnCount++);
			// display colour moving
			switch (player) {
			case Board.BLACK: System.out.println("Black is moving..."); break;
			case Board.WHITE: System.out.println("White is moving..."); break;
			}
			// have ai make move
			if (player==Board.BLACK) {
				aiBlack.makeMove(board);
			}
			else {
				aiWhite.makeMove(board);
			}
			// display new board
			System.out.println(board);
			// switch player
			player = (player==Board.BLACK) ? (Board.WHITE) : (Board.BLACK);
		}
		
		// declare winner
		if (board.checkWin()==Board.BLACK) {
			System.out.println("Black wins");
		}
		else if (board.checkWin()==Board.WHITE){
			System.out.println("White wins");
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
