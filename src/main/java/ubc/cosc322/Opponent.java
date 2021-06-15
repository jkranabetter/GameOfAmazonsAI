package ubc.cosc322;

public class Opponent extends SmartAI {
	public Opponent(int player, Board_v2 board) {
		super(player,board);
	}
	@Override
	public int scoreBoard(Board_v2 board) {
		// determine which heuristic functions need to be called
		int score = 0;
		
		score = this.totalActionsHeuristic(board);
		// score = this.tileOwnershipHeuristic_v5(board);
		// score = (int) ( 1 * this.totalActionsHeuristic(board) + 100 * this.tileOwnershipHeuristic_v5(board) );
		
		// System.out.println("Gave score of " + score);
		return score;
	}
}
