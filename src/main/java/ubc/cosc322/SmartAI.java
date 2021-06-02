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
	
	ArrayList<ArrayList<Integer>> isolatedQueens; // holds list of positions of queens unable to reach opponent
	double totalActionsWeight, tileOwnershipWeight; // heuristic weights
	
	
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
		if (this.player==Board_v2.BLACK) {
			this.turnCount = -2;
		} 
		else {
			this.turnCount = -1;
		}
		// set turn duration
		this.turnDuration = 28; // gives 2 seconds to get out of multiple layers of loops
		// initialize isolated queens to empty
		this.isolatedQueens = new ArrayList<ArrayList<Integer>>();
	}

	//-- GENERAL METHODS --//
	@Override
	public ArrayList<ArrayList<Integer>> getAction() {
		// do turn start parameter updates
		this.turnStartTime = System.nanoTime(); // initialize turn start time
		this.turnCount += 2; // increment turn counter
		
//		//-- UNCOMMENT BELOW WHEN QUEEN ISOLATION TECHNIQUE COMPLETELY IMPLEMENTED --//
//		// check if isolatedQueens list filled
//		if (this.isolatedQueens.size()>=4) {
//			// do longest path algorithm
//			ArrayList<ArrayList<Integer>> action = this.findLongestPath(this.isolatedQueens);
//			return action;
//		}
//		//-- UNCOMMENT ABOVE WHEN QUEEN ISOLATION TECHNIQUE COMPLETELY IMPLEMENTED --//
		
		// update heuristic weights for scoreBoard formula -> temporary form currently
		if (turnCount>=0) {
			totalActionsWeight = 1;
			tileOwnershipWeight = 0;
		}
		else if (turnCount>=25) {
			totalActionsWeight = 0.5;
			tileOwnershipWeight = 0.5;
		}
		else if (turnCount>=50) {
			totalActionsWeight = 0;
			tileOwnershipWeight = 1;
		}
		
		// perform iterative minimax
		ArrayList<ArrayList<Integer>> action = this.iterativeMinimax(searchDepth, this.trueBoard);
		
		// return best action from iterative minimax
		return action;
		
	}
	
	/**
	 * uncomment lines for queen isolation technique
	 */
	@Override
	public ArrayList<ArrayList<ArrayList<Integer>>> getAllActions(int player, Board_v2 board) {
		// copy board to not accidentally change it
		board = new Board_v2(board);
		// create list to store actions
		ArrayList<ArrayList<ArrayList<Integer>>> actions = new ArrayList<ArrayList<ArrayList<Integer>>>();
		// get list of passed player's queens
		ArrayList<ArrayList<Integer>> queens = board.getQueens(player);
		// loop through potential queenCurrent positions
		for (ArrayList<Integer> queenCurrent : queens) {
			
//			//-- UNCOMMENT BELOW WHEN QUEEN ISOLATION TECHNIQUE COMPLETELY IMPLEMENTED --//
//			// check if queen on isolation list ie can be skipped
//			if (this.isOnIsolationList(queenCurrent)) {
//				// switch to next queen without bothering to get moves of this one
//				continue;
//			}
//			//-- UNCOMMENT ABOVE WHEN QUEEN ISOLATION TECHNIQUE COMPLETELY IMPLEMENTED --//
			
			// get list of directly reachable empty tiles from queenCurrent
			ArrayList<ArrayList<Integer>> queenMoves = board.getDirectEmptyTiles(queenCurrent);
			// erase queenCurrent from its position
			board.setTile(Board_v2.EMPTY, queenCurrent);
			// loop through potential queenMoved positions
			for (ArrayList<Integer> queenMoved : queenMoves) {
				// get list of directly reachable empty tiles from queenMoved
				ArrayList<ArrayList<Integer>> arrows = board.getDirectEmptyTiles(queenMoved);
				// loop through potential arrow positions
				for (ArrayList<Integer> arrow : arrows) {
					// create action
					ArrayList<ArrayList<Integer>> action = new ArrayList<ArrayList<Integer>>();
					action.add(queenCurrent);
					action.add(queenMoved);
					action.add(arrow);
					// add action to list
					actions.add(action);
				}
			}
			// add queenCurrent back to board
			board.setTile(player, queenCurrent);
		}
		// return list of actions
		return actions;
	}
	
	public boolean checkEndTurn() {
		// check if end of turn yet
		if ((System.nanoTime()-this.turnStartTime)/(int)(Math.pow(10, 9)) > this.turnDuration) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Use heuristic functions to determine a score for the passed board
	 * will need to be updated for future heuristic functions
	 * Example Formula : score() = weight*totalActions() + weight*controllingRegion() + weight*trappingOpponents()
	 * @param board board to give score to
	 * @return score for passed board
	 */
	public int scoreBoard(Board_v2 board) {
		// determine which heuristic functions need to be called
		int score = 0;
		if (totalActionsWeight>0 && tileOwnershipWeight>0) {
			score = (int) ( this.totalActionsWeight * this.totalActionsHeuristic(board) + this.tileOwnershipWeight * this.tileOwnershipHeuristic(board) );
		}
		else if (totalActionsWeight>0) {
			score = (int) (this.totalActionsWeight * this.totalActionsHeuristic(board));
		}
		else if (tileOwnershipWeight>0) {
			score = (int) (this.tileOwnershipWeight * this.tileOwnershipHeuristic(board));
		}
		
		// System.out.println("Gave score of " + score);
		return score;
	}

	
	//-- MINIMAX ALGORITHM --//
	
	/**
	 * calls minimax searches with increasingly larger depths until turn timer cuts it off
	 * @param initialDepth
	 * @param board
	 * @return best action found 
	 */
	public ArrayList<ArrayList<Integer>> iterativeMinimax(int initialDepth, Board_v2 board) {
		// create pointer to best action
		ArrayList<ArrayList<Integer>> bestAction = new ArrayList<ArrayList<Integer>>();
		// perform iterative minimax search 
		for (int i=initialDepth; true; i++) {
			// check if end of turn yet
			if (this.checkEndTurn()) { break; }
			// begin iteration
			System.out.println("Beginning iteration " + i); // testing
			// do minimax search to depth i
			ArrayList<ArrayList<Integer>> action = this.minimax(board, i);
			// check if initializing bestAction
			if (bestAction.isEmpty()) {
				bestAction = action;
			}
			// check if updating to same action -> can stop b/c confirmed best action
			else if (action.get(0).equals(bestAction.get(0)) && action.get(1).equals(bestAction.get(1)) && action.get(2).equals(bestAction.get(2))) {
				break;
			}
			// check if new action is better
			else if (action.get(3).get(0)>=bestAction.get(3).get(0)) {
				System.out.println("Updating best action"); // testing
				bestAction = action;
			}
		}
		// remove score from action list
		bestAction.remove(bestAction.size()-1);
		// return action 
		return bestAction;
	}
	
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
			if (this.checkEndTurn()) { break; }
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
			if (this.checkEndTurn()) {
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
			if (this.checkEndTurn()) {
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
	 * is very slow and inefficient
	 * doesnt have to loop through each tile every time
	 * only first time, then can run recursive function at queenCurrent, queenMoved, and adjacent to arrow
	 * will have to run with opponent action too though
	 * @param board
	 * @return
	 */
	public int tileOwnershipHeuristic(Board_v2 board) {
		// create score holder
		int score = 0; // increase for close player, decrease for close opponent
		// loop through board
		for (int row=1; row<11; row++) {
			// check if end of turn yet
			if (this.checkEndTurn()) { break; }
			for (int col=1; col<11; col++) {
				// check if end of turn yet
				if (this.checkEndTurn()) { break; }
				// check if empty playable tile
				if (board.getTile(row, col)==Board_v2.EMPTY) {
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
			if (this.checkEndTurn()) { break; }
			// check if empty tile
			if (board.getTile(row,col)==Board_v2.EMPTY) {
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
			if (this.checkEndTurn()) { break; }
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
	
	public int tileOwnershipHeuristic_v2(Board_v2 board) {
		// create array to hold which tiles belong to which player
		int[][] tiles = new int[11][11];
		// loop through tiles whos ownership have not yet been found
		for (int row=1; row<11; row++) {
			// check if end of turn yet
			if (this.checkEndTurn()) { break; }
			for (int col=1; col<11; col++) {
				// check if end of turn yet
				if (this.checkEndTurn()) { break; }
				// check if tile ownership already found
				if (tiles[row][col]==0) {
					// skip
					continue;
				}
				// get ownership of tile by recursing into it
				ArrayList<Integer> position = new ArrayList<Integer>();
				position.add(row);
				position.add(col);
				tiles[row][col] = this.tileOwnershipHeuristicRecurse_v2(tiles, board, position, 1);
			}
		}
		return 0; // temp
	}
	
	/**
	 * get ownership of passed tile and recurse into reachable tiles getting their closests
	 * @param tiles 2d array to place ownership value into for each tile
	 * @param board
	 * @param position
	 * @param depth current depth may not be needed if gonna count upwards from back
	 * @return distance to closest queen, positive if player's, negative if opponents, 0 if neutral
	 */
	public int tileOwnershipHeuristicRecurse_v2(int[][] tiles, Board_v2 board, ArrayList<Integer> position, int depth) {
		// loop through reachable tiles looking for queens reachable in one turn
		// determine if queen(s) is(are) reachable and return +1, -1, or 0
		// get closest queens to each of the reachable tiles
		// determine closest queen to this tile and return it
		
		return 0; // temp
	}
	
	
	//-- ISOLATED QUEENS TECHNIQUE METHODS --//
	/**
	 * check for queens who dont need to contribute to search tree rn
	 * can just find longest path
	 * will need to change getActions loop to skip isolated queens
	 */
	public void findIsolatedQueens(Board_v2 board, int player) {
		// get all player's queens on trueBoard
		ArrayList<ArrayList<Integer>> queens = board.getQueens(player);
		// loop through queens
		for (ArrayList<Integer> queen : queens) {
			// check if disconnected from all opponent queens
			if (this.isIsolated(board, queen)) {
				// add isolated queen to list
				this.isolatedQueens.add(queen);
			}
		}
	}
	
	/**
	 * INCOMPLETE METHOD
	 * determine if passed queen is isolated from all opponent queens
	 * @param board
	 * @param queen
	 * @return
	 */
	public boolean isIsolated(Board_v2 board, ArrayList<Integer> queen) {
		// determine if queen can reach an opponent queen
		
		// temporary return
		return false;
	}
	
	/**
	 * determine if passed queen has been found to be isolated
	 * @param queen
	 * @return
	 */
	public boolean isOnIsolationList(ArrayList<Integer> queen) {
		for (ArrayList<Integer> isolated: this.isolatedQueens) {
			if (isolated.get(0).equals(queen.get(0)) && isolated.get(1).equals(queen.get(1))) {
				// matches list item
				return true;
			}
		}
		// otherwise
		return false;
	}
	
	/**
	 * INCOMPLETE METHOD
	 * find first action for longest path 
	 * @param queens
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> findLongestPath(ArrayList<ArrayList<Integer>> queens) {
		// find size of room for first isolated queen with at least 1 action left 
		// perform minimax at depth of room size, stop when found route all the way to depth size, score using depth of board
		return null;
	}
	
	
	//-- EXTRA METHODS --//
	/**
	 * TESTING FUNCTION, USED TO PIT OWN AI AGAINST ITSELF WITH DIFFERENT STATS
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
	
}
