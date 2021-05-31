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
	long turnStartTime, turnDuration;
	int searchDepth, turnCount, heuristicType;
	
	
	//-- CONSTRUCTORS --//
	/**
	 * Create Ai with passed color and with connection to passed board
	 * @param player
	 * @param board
	 */
	public SmartAI(int player, Board_v2 board) {
		// create base fields
		super(player,board);
		// initialize turn counter, will be incremented to starting value on first turn
		if (this.player==Board.BLACK) {
			this.turnCount = -2;
		} 
		else {
			this.turnCount = -1;
		}
		// set turn duration
		this.turnDuration = 28; // gives 2 seconds to get out of multiple layers of loops
	}

	//-- GENERAL METHODS --//
	@Override
	public ArrayList<ArrayList<Integer>> getAction() {
		// initialize turn start time
		this.turnStartTime = System.nanoTime();
		// increment turn counter
		this.turnCount += 2;
		
		// perform minimax search at set level
		ArrayList<ArrayList<Integer>> bestAction = this.minimax(trueBoard, searchDepth);
		
		// perform iterative minimax search with any extra time
		for (int i=searchDepth+1; true; i++) {
			// check if end of turn yet
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnDuration) {
				break;
			}
			// begin iteration
			System.out.println("Beginning iteration " + i);
			// do minimax search to depth i
			ArrayList<ArrayList<Integer>> action = this.minimax(trueBoard, i);
			// check if updating to same action -> can stop b/c confirmed best action
			if (action.get(0).equals(bestAction.get(0)) && action.get(1).equals(bestAction.get(1)) && action.get(2).equals(bestAction.get(2))) {
				break;
			}
			// check if new action is better
			if (action.get(3).get(0)>=bestAction.get(3).get(0)) {
				System.out.println("Updating score");
				bestAction = action;
			}
		}
		// remove score from action list
		bestAction.remove(bestAction.size()-1);
		// return action 
		return bestAction;
		
	}
	
	/**
	 * Initialize ai fields that dictate how it searches as well as initialize turn counter
	 * use to change parameters of 2 smart ai in main file so as to compare and contrast
	 * @param turnDuration time on turn at which ai should begin exiting out of loops to end turn
	 * @param searchDepth depth for minimax algorithm to stop searching at
	 */
	public void changeAIFields(int searchDepth, int heuristicType) {
		// set search depth
		this.searchDepth = searchDepth;
		// set heuristic type
		this.heuristicType = heuristicType;
	}
	
	//-- MINIMAX ALGORITHM --//
	
	/**
	 * First layer of minimax algorithm
	 * loops through all actions that can be taken this turn finding the best scored action
	 * returns the best action instead of the best score (as in maxValue())
	 * @param board board to get actions from
	 * @param depth depth of tree to search
	 * @return best action for ai to take with corresponding score appended to end as length 1 list
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
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnDuration) {
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
		System.out.println("Best action has score of " + bestScore);
		// append score onto end of action
		ArrayList<Integer> appendingScore = new ArrayList<Integer>();
		appendingScore.add(bestScore);
		bestAction.add(appendingScore);
		// return chosen action with score appended onto it as length 1 list
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
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnDuration) {
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
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnDuration) {
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
	
	/**
	 * Use heuristic functions to determine a score for the passed board
	 * @param board board to give score to
	 * @return score for board
	 */
	public int scoreBoard(Board_v2 board) {
		int score;
		switch (this.heuristicType) {
		case 0: score = this.totalActionsHeuristic(board); break;
		case 1: score = this.tileOwnershipHeuristic(board); break;
		default: score = 0;
		}
		// System.out.println("Gave score of " + score);
		return score;
	}
	
	//-- HEURISTICS FUNCTIONS --//
	
	/**
	 * Heuristic that compares players total available actions to opponent
	 * out performs tileOwnershipHeuristic()
	 * @param board board to find actions on
	 * @return difference between player total actions and opponent total actions 
	 */
	public int totalActionsHeuristic(Board_v2 board) {
		ArrayList<ArrayList<ArrayList<Integer>>> playerActions = this.getAllActions(player, board);
		ArrayList<ArrayList<ArrayList<Integer>>> opponentActions = this.getAllActions(opponent, board);
		return playerActions.size()-opponentActions.size();
	}
	
	/**
	 * Heuristic that compares number of tiles with closer player queens to tiles with closer opponent queen
	 * loses to totalActionsHeuristic()
	 * @param board
	 * @return
	 */
	public int tileOwnershipHeuristic(Board_v2 board) {
		// create score holder
		int score = 0; // increase for close player, decrease for close opponent
		// loop through board
		for (int row=1; row<11; row++) {
			// check if end of turn yet
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnDuration) {
				break;
			}
			for (int col=1; col<11; col++) {
				// check if end of turn yet
				if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnDuration) {
					break;
				}
				// check if empty playable tile
				if (board.getTile(row, col)==Board.EMPTY) {
					// define tile position
					ArrayList<Integer> position = new ArrayList<Integer>();
					position.add(row);
					position.add(col);
					// use recursive function to find depth of closest queen
					int depth = this.tileOwnershipHeuristicRecurse(board,position,new boolean[11][11],1);
					// check if player or opponent queen
					if (depth>0) {
						// increment score if current tile is closer to player queen
						score += 1;
					}
					else if (depth<0) {
						// decrement score if current tile is closer to opponent queen
						score -= 1;
					}
				}
			}
		}
		// return determined score
		return score;
	}
	
	/**
	 * 
	 * @param board
	 * @param position
	 * @param checkedTiles boolean array holding if tiles have been recursed into already
	 * @param currentDepth absolute depth of heuristic search
	 * @return depth of closest queen, positive if player queen, negative if opponent queen, 0 if both equal
	 */
	public int tileOwnershipHeuristicRecurse(Board_v2 board, 
												ArrayList<Integer> position, 
												boolean[][] checkedTiles,
												int currentDepth) {
		// add current tile to checkedTiles
		checkedTiles[position.get(0)][position.get(1)] = true;
		// create boolean variables to hold whether queens were seen
		boolean playerQueen = false, opponentQueen = false;
		// create list to store empty tiles
		ArrayList<ArrayList<Integer>> directTiles = new ArrayList<ArrayList<Integer>>();
		
		// loop through directly reachable empty tiles along straight lines looking for queens
		for (int count=0, dx=1, dy=1, row=position.get(0)+dy, col=position.get(1)+dx; count<8; row+=dy, col+=dx) {
			// check if end of turn yet
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnDuration) {
				break;
			}
			// check if empty tile
			if (board.getTile(row,col)==Board.EMPTY) {
				// add tile to list
				ArrayList<Integer> newTile = new ArrayList<Integer>();
				newTile.add(row);
				newTile.add(col); 
				directTiles.add(newTile); 
				// continue to next tile in line
				continue;
			}
			// check if player queen tile
			else if (board.getTile(row,col)==player) {
				playerQueen = true;
			}
			// check if opponent queen tile
			else if (board.getTile(row,col)==opponent) {
				opponentQueen = true;
			}
			// define new line -> updated order of lines, doing diagonals first
			switch (count++) {
			case 0: dx=-1; dy=1; break;
			case 1: dx=-1; dy=-1; break;
			case 2: dx=1; dy=-1; break;
			case 3: dx=1; dy=0; break;
			case 4: dx=0; dy=1; break;
			case 5: dx=-1; dy=0; break;
			case 6: dx=0; dy=-1; break;
			case 7: dx=1; dy=1; break;
			}
			// reset to start of line
			row = position.get(0);
			col = position.get(1);
		}
		
		// determine tile ownership without recursion
		if (playerQueen==true && opponentQueen==true) {
			// neutral tile
			return 0;
		}
		else if (playerQueen==true) {
			// player tile
			return currentDepth;
		}
		else if (playerQueen==false) {
			// opponent tile
			return -currentDepth;
		}
		
		// define list to hold recursive scores
		ArrayList<Integer> queenDistances = new ArrayList<Integer>();
		// loop through direct tiles to recurse into
		for (int i=0; i<directTiles.size(); i++) {
			// check if end of turn yet
			if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnDuration) {
				break;
			}
			// check if already been checked
			if (checkedTiles[directTiles.get(i).get(0)][directTiles.get(i).get(1)]) {
				// already recursed into tile, dont repeat
				continue;
			}
			// recurse into tile to get depth of closest queen and add to list
			queenDistances.add( this.tileOwnershipHeuristicRecurse(board, directTiles.get(i), checkedTiles, ++currentDepth) );
			
		}
		
		// finding minimum variables
		int smallestDepth = Integer.MAX_VALUE; // hold depth of closest queen
		boolean smallestDepthTie = false; // is true when player and opponent queen equally close
		// loop through scores to determine closest queen
		for (int i=0; i<queenDistances.size(); i++) {
			// check if current is strictly closer
			if (Math.abs(queenDistances.get(i)) < Math.abs(smallestDepth)) {
				smallestDepth = queenDistances.get(i);
				smallestDepthTie = false;
			}
			// check if current is equally close but of opposite color
			else if (Math.abs(queenDistances.get(i)) == Math.abs(smallestDepth) && queenDistances.get(i)-smallestDepth==0) {
				smallestDepth = Math.abs(queenDistances.get(i));
				smallestDepthTie = true;
			}
		}
		
		// check if 2 queens equally close
		if (smallestDepthTie==true) {
			return 0; // neutral tile
		}
		// return closest queen value including pos and neg
		return smallestDepth;
	}
	
	
}
