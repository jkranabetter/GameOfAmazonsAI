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
	long turnStartTime, turnExitTime;
	int searchDepth;
	
	
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
		// initialize turn start time
		this.turnStartTime = System.nanoTime();
		this.turnExitTime = 28;
		this.searchDepth = 3;
		// call minimax function on trueBoard with specified depth
		return this.minimax(trueBoard, searchDepth);
	}
	
	/**
	 * First layer of minimax algorithm
	 * loops through all actions that can be taken this turn finding the best scored action
	 * returns the best action instead of the best score (like in maxValue())
	 * @param board board to get actions from
	 * @param depth depth of tree to search
	 * @return best action for ai to take
	 */
	public ArrayList<ArrayList<Integer>> minimax(Board_v2 board, int depth) {
		// System.out.println("Beginning minimax algorithm with depth " + depth);
		// get all actions
		ArrayList<ArrayList<ArrayList<Integer>>> actions = this.getAllActions(player, board);
		// create action and score variables to hold best action and score found
		ArrayList<ArrayList<Integer>> bestAction = actions.get(0);
		int bestScore = Integer.MIN_VALUE;
		// loop through each action
		for (int i=0; i<actions.size(); i++) {
			// check if end of turn yet
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnExitTime) {
				break;
			}
			// clone passed board
			Board_v2 nextBoard = new Board_v2(board);
			// apply action to cloned board 
			nextBoard.applyAction(this.player, actions.get(i).get(0), actions.get(i).get(1), actions.get(i).get(2));
			// recurse into opponent turn to get score
			int score = this.minValue(nextBoard, depth-1, bestScore);
			// check if action score is strictly better than current best
			if (score>bestScore) {
				// update bestAction and bestScore to current action and score
				bestAction = actions.get(i);
				bestScore = score;
				// System.out.println("Best score updated in minimax to " + bestScore);
			}
			else if (score==bestScore) {
				// choose random OR score each board state OR just do/don't update
			}
		}
		System.out.println("Chosen action has score of " + bestScore);
		// return chosen action
		return bestAction;
	}
	
	/**
	 * Get the player's best action's score for passed board
	 * @param board to analyze
	 * @param depth depth of tree to stop recursing at
	 * @return best possible score
	 */
	public int maxValue(Board_v2 board, int depth, int beta) {
		// System.out.println("Searching for max value at depth of " + depth);
		// check if reached max depth -> score current state of board and return it
		if (depth<=0) {
			// System.out.println("Reached max depth, scoring board");
			// score current board and return terminal score
			return this.scoreBoard(board);
		}
		// get all player actions
		ArrayList<ArrayList<ArrayList<Integer>>> actions = this.getAllActions(player, board);
		// check if no player options -> lose
		if (actions.isEmpty()) {
			// System.out.println("Reached losing state");
			// no way to recurse on this board state
			return this.scoreBoard(board);
		}
		// initialize best score to worse possible
		int bestScore = Integer.MIN_VALUE;
		// loop through player actions
		for (int i=0; i<actions.size(); i++) {
			// check if end of turn yet
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnExitTime) {
				// check if passing MIN_VALUE score
				if (bestScore==Integer.MIN_VALUE) {
					return this.scoreBoard(board);
				}
				break;
			}
			// clone passed board
			Board_v2 nextBoard = new Board_v2(board);
			// apply action to cloned board 
			nextBoard.applyAction(this.player, actions.get(i).get(0), actions.get(i).get(1), actions.get(i).get(2));
			// recurse into opponent's turn to get score
			int score = this.minValue(nextBoard, depth-1, bestScore);
			// check if action score is strictly better than current best
			if (score>bestScore) {
				// update bestScore to current score
				bestScore = score;
				// System.out.println("Best score updated at depth of " + depth + " to " + bestScore);
			}
			else if (score==bestScore) {
				// choose random OR score each board state OR just do/don't update
			}
			// check if bestScore is bigger than beta
			if (bestScore>beta) {
				// System.out.println("Beta smaller, skipping nodes");
				// other children from this node will have no affect on parent node
				break;
			}
		}
		// return the max valued action
		return bestScore;
	}
	
	/**
	 * Get the opponents best action's score (lowest score) for passed board
	 * @param board board to analyze
	 * @param depth depth to stop recursing at 
	 * @return worst possible score 
	 */
	public int minValue(Board_v2 board, int depth, int alpha) {
		// System.out.println("Searching for min value at depth of " + depth);
		// check if reached max depth -> score current state of board and return it
		if (depth<=0) {
			// System.out.println("Reached max depth, scoring board");
			// score current board and return terminal score
			return this.scoreBoard(board);
		}
		// get all opponent actions
		ArrayList<ArrayList<ArrayList<Integer>>> actions = this.getAllActions(opponent, board);
		// check if no opponent options -> win
		if (actions.isEmpty()) {
			// System.out.println("Reached winning state");
			// no way to recurse on this board state
			return this.scoreBoard(board);
		}
		// initialize worst score to best possible
		int worstScore = Integer.MAX_VALUE;
		// loop through player actions
		for (int i=0; i<actions.size(); i++) {
			// check if end of turn yet
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnExitTime) {
				// check if passing MAX_VALUE score
				if (worstScore==Integer.MAX_VALUE) {
					return this.scoreBoard(board);
				}
				break;
			}
			// clone passed board
			Board_v2 nextBoard = new Board_v2(board);
			// apply action to cloned board 
			nextBoard.applyAction(opponent, actions.get(i).get(0), actions.get(i).get(1), actions.get(i).get(2));
			// recurse into player's's turn to get score
			int score = this.maxValue(nextBoard, depth-1, worstScore);
			// check if action score is strictly worse than current worse
			if (score<worstScore) {
				// update worstScore to current score
				worstScore = score;
				// System.out.println("Worst score updated at depth of " + depth + " to " + worstScore);
			}
			else if (score==worstScore) {
				// choose random OR score each board state OR just do/don't update
			}
			// check if worstScore is smaller than alpha
			if (worstScore<alpha) {
				// System.out.println("Alpha larger, skipping nodes");
				// other children from this node will have no affect on parent node
				break;
			}
		}
		// return the min valued action
		return worstScore;
	}
	public int scoreBoard(Board_v2 board) {
		// using total queens actions heuristic
		int score = this.totalQueensActionsHeuristic(board);
		// System.out.println("Gave score of " + score);
		return score;
	}
	
	public int totalQueensActionsHeuristic(Board_v2 board) {
		// compare number of actions between player and opponent and return difference as score
		ArrayList<ArrayList<ArrayList<Integer>>> playerActions = this.getAllActions(player, board);
		ArrayList<ArrayList<ArrayList<Integer>>> opponentActions = this.getAllActions(opponent, board);
		return playerActions.size()-opponentActions.size();
	}
	
	

}
