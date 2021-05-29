package ubc.cosc322;

import java.util.ArrayList;

public class SmartAI extends Player {
	/**
	 * Inherited from Player class:
	 * player: for player int(Board.BLACK or Board.WHITE)
	 * trueBoard: for access to true board
	 * getColorInt(): returns player field
	 * getAllActions(int player, Board_v2 board): returns list of valid actions... 
	 * ... for given board state
	 * 
	 */
	//-- FIELDS --//
	
	
	//-- CONSTRUCTORS --//
	/**
	 * Create Ai with passed color and with connection to passed board
	 * @param player
	 * @param board
	 */
	public SmartAI(int player, Board_v2 board) {
		super(player,board);
	}

	//-- METHODS --//
	@Override
	public ArrayList<ArrayList<Integer>> getAction() {
		// call minimax function on trueBoard with specified depth
		return this.minimax(trueBoard, 3);
	}
	
	public ArrayList<ArrayList<Integer>> minimax(Board_v2 board, int depth) {
		// get all actions
		ArrayList<ArrayList<ArrayList<Integer>>> actions = this.getAllActions(player, board);
		// create action and score variables to hold best action and score found
		ArrayList<ArrayList<Integer>> bestAction = actions.get(0);
		int bestScore = -999;
		// loop through each action
		for (int i=0; i<actions.size(); i++) {
			// clone passed board
			Board_v2 nextBoard = new Board_v2(board);
			// apply action to cloned board 
			nextBoard.applyAction(this.player, actions.get(i).get(0), actions.get(i).get(1), actions.get(i).get(2));
			// get score from applied action
			int score = this.maxValue(nextBoard, depth);
			// check if action score is better than current best
			if (score>bestScore) {
				// update bestAction and bestScore to current action and score
				bestAction = actions.get(i);
				bestScore = score;
			}
			else if (score==bestScore) {
				// choose random OR score each board state OR just do/don't update
			}
		}
		// return chosen action
		return bestAction;
	}
	
	public int maxValue(Board_v2 board, int depth) {
		// check if reached max depth -> score current state of board and return it
		
		// loop through actions getting next row of tree
		
		// return the max valued action
		
		return 0; // TEMPORARY
	}
	public int minValue(Board_v2 board, int depth) {
		// check if reached max depth -> score current state of board and return it
		
		// loop through actions getting next row of tree
		
		// return the min valued action
		
		return 0; // TEMPORARY
	}
	
	

}
