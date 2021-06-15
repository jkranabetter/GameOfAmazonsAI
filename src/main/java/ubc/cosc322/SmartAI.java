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
	
	int searchDepth;
	
	ArrayList<ArrayList<Integer>> isolatedQueens; // holds queens unable to reach opponent
	ArrayList<ArrayList<Integer>> trappedQueens; // holds queens who can no longer move
	boolean inFinalPhase; // flipped to true when all queens are isolated
	
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
		// set turn duration
		this.turnDuration = 28; // gives 2 seconds to get out of multiple layers of loops
		// set search depth
		this.searchDepth = 1; // depth of 0 and 1 are same, dont set to 0
		// initialize isolated queens and trapped queens to empty
		this.isolatedQueens = new ArrayList<ArrayList<Integer>>();
		this.trappedQueens = new ArrayList<ArrayList<Integer>>();
		this.inFinalPhase = false;
	}

	//-- GENERAL METHODS --//
	@Override
	public ArrayList<ArrayList<Integer>> getAction() {
		// initialize turn start time
		this.turnStartTime = System.nanoTime(); 
		// turn count moved to board class
		this.findIsolatedOrTrappedQueens(trueBoard, this.player); // update isolated and trapped queens lists
		
		//-- UNCOMMENT BELOW WHEN QUEEN ISOLATION TECHNIQUE COMPLETELY IMPLEMENTED --//
		// check if isolatedQueens list filled
		if (this.isolatedQueens.size()+this.trappedQueens.size()>=4) {
			// trigger final phase
			System.out.println("All queens isolated or trapped");
			this.inFinalPhase = true;
			// do longest path algorithm
			ArrayList<ArrayList<Integer>> action = this.findLongestPath(this.isolatedQueens);
			return action;
		}
		//-- UNCOMMENT ABOVE WHEN QUEEN ISOLATION TECHNIQUE COMPLETELY IMPLEMENTED --//
		

		
		// determine initial search depth (numbers based on 30 second turns
		int actionsSize = this.getAllActions(player, trueBoard).size();
		if (actionsSize<10) {
			System.out.println("Not bothering with iterative search");
			return this.minimax(trueBoard, 5);
		}
		else if (actionsSize<100) {
			System.out.println("Initial Search depth set to 3");
			searchDepth = 3;
		}
		else if (actionsSize<300) {
			System.out.println("Initial Search depth set to 2");
			searchDepth = 2;
		}
		else if (actionsSize>=300) {
			System.out.println("Initial Search depth set to 1");
			searchDepth = 1;
		}
		
//		// for 10 second turns
//		searchDepth = 1;
		
		// perform iterative minimax
		ArrayList<ArrayList<Integer>> action = this.iterativeMinimax(searchDepth, this.trueBoard);
		
		// return best action from iterative minimax
		return action;
		
	}
	
	/**
	 * uncomment lines for queen isolation technique -> done
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
			
			//-- UNCOMMENT BELOW WHEN QUEEN ISOLATION TECHNIQUE COMPLETELY IMPLEMENTED --//
			// check if queen on isolation/trapped lists ie can be skipped unless on final phase
			if ( this.inFinalPhase==false && (this.isOnIsolationList(queenCurrent) || this.isOnTrappedList(queenCurrent)) ) {
				// switch to next queen without bothering to get moves of this one
				continue;
			}
			//-- UNCOMMENT ABOVE WHEN QUEEN ISOLATION TECHNIQUE COMPLETELY IMPLEMENTED --//
			
			
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
	
	/**
	 * Check if run out of time
	 * @return
	 */
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
		
		// check if all queens isolated on trueBoard
		if (this.isolatedQueens.size()+this.trappedQueens.size()>=4) {
			// score board by total actions
			score = this.totalActionsHeuristic(board);
			return score;
		}
		
		// testing out combo of heuristics with different weights
		// score = (int) ( 1 * this.totalActionsHeuristic(board) + 100 * this.tileOwnershipHeuristic_v5(board) );
		
		// best working scoring 
		if (board.turnCount<15) {
			// phase 1
			score = (int) ( 0 * this.totalActionsHeuristic(board) + 100 * this.tileOwnershipHeuristic_v5(board) );
		}
		else if (board.turnCount<30) {
			// phase 2
			score = (int) ( 0.5 * this.totalActionsHeuristic(board) + 100 * this.tileOwnershipHeuristic_v5(board) );
		}
		else {
			// phase 3 
			score = (int) ( 1 * this.totalActionsHeuristic(board) + 100 * this.tileOwnershipHeuristic_v5(board) );
		}
		
		
		// uncomment one
		// score = this.totalActionsHeuristic(board);
		// score = this.regionsHeuristic_v4(board);
		// score = this.tileOwnershipHeuristic_v5(board); // best one by itself
		
		
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
//			// check if updating to same action -> can stop b/c confirmed best action
//			else if (action.get(0).equals(bestAction.get(0)) && action.get(1).equals(bestAction.get(1)) && action.get(2).equals(bestAction.get(2))) {
//				System.out.println("Confirmed action, exiting early"); // testing
//				break;
//			}
			// check if new action is better
			else if (action.get(3).get(0)>=bestAction.get(3).get(0)) {
				// System.out.println("Updating best action"); // testing
				bestAction = action;
			}
		}
		System.out.println("Best action has score "+bestAction.get(bestAction.size()-1));
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
	 * Rank board state by number of tiles that player has easier access to versus the opponent
	 * tiles equally easily accessible are neutral tiles and do not contribute to overall score
	 * Loop through each empty tile on board
	 * Starting with current empty tile, loop through reachable tiles adding to queue until queen is found
	 * At the start of each row of tree, add 3 element to initial position to know when to start next row
	 * when queen found, finish row to make sure opponent queen is not also justas close (ie neutral tile)
	 * @param board
	 * @return
	 */
	public int tileOwnershipHeuristic_v5(Board_v2 board) {
		// create score to return
		int score = 0;
		// loop through each tile on board
		for (int row=1; row<11; row++) {
			for (int col=1; col<11; col++) {
				// check if tile is empty
				if (board.getTile(row,col)==Board_v2.EMPTY) {
					// System.out.println("Tile "+row+","+col+" is being looked at");
					// create booleans to hold whether player and opponent queens have been found
					boolean playerFound = false, opponentFound = false;
					// create array to hold if tiles are already in queue/analysed
					boolean[][] queued = new boolean[11][11];
					// create queue of positions
					ArrayList<ArrayList<Integer>> queue = new ArrayList<ArrayList<Integer>>();
					// convert current position to list format
					ArrayList<Integer> start = new ArrayList<Integer>();
					start.add(row);
					start.add(col);
					// initialize queue with current position
					queue.add(start);
					// mark first position as first in a row by adding 1 to end of position coords
					queue.get(0).add(1);
					// loop through queue until empty or broken out of by having found queen(s)
					while (queue.size()>0) {
						// dequeue first element
						ArrayList<Integer> position = queue.remove(0);
						// check if element is first in row of tree
						boolean firstElement = false;
						if (position.size()>=3) {
							// System.out.println("Found first element in row of tree");
							// change firtElement to true
							firstElement = true;
							// check if queen found in previous row of tree
							if (playerFound || opponentFound) {
								// System.out.println("Queen found in previous row of tree");
								// exit tree before beginning new row
								break;
							}
						}
						// check if dequeued is queen
						if (board.getTile(position)==Board_v2.BLACK || board.getTile(position)==Board_v2.WHITE) {
							// check if player queen found
							if (board.getTile(position)==player) {
								// System.out.println("Player queen found");
								// player queen found
								playerFound = true;
							}
							// check if opponent queen found
							else if (board.getTile(position)==opponent) {
								// System.out.println("Opponent queen found");
								// opponent queen found
								opponentFound = true;
							}
							// check if both teams have equally close queen
							if (playerFound && opponentFound) {
								// tile is neutral, can break out ofqueue loop
								break;
							}
						}
						// get reachable tiles by current, including any queens
						ArrayList<ArrayList<Integer>> reachableTiles = this.getDirectEmptyAndQueenTiles(board, position);
						// System.out.println(reachableTiles.size()+" reachable tiles to "+position);
						// loop through list of reachable tiles
						for (ArrayList<Integer> reachableTile : reachableTiles) {
							// check if tile has already been added to queue before
							if (queued[reachableTile.get(0)][reachableTile.get(1)]) {
								// dont add to queue again
								continue;
							}
							// check if this tile is first to be added from first element in row
							if (firstElement) {
								// mark as first element in row before adding
								reachableTile.add(1);
								// uncheck first element
								firstElement = false;
							}
							// add new tile to queue to look at later
							queue.add(reachableTile);
							// mark tile as queued
							queued[reachableTile.get(0)][reachableTile.get(1)] = true;
						}
					}
					// determine tile effect on score based on playerFound and opponentFound values
					if (playerFound && opponentFound) {
						// neutral tile, both colors equal distance away
						// System.out.println("Tile "+row+","+col+" is neutral");
					}
					else if (playerFound) {
						// player tile, player queen closer
						// System.out.println("Tile "+row+","+col+" belongs to "+player);
						score++;
					}
					else if (opponentFound) {
						// opponent tile, opponent queen closer
						// System.out.println("Tile "+row+","+col+" belongs to "+opponent);
						score--;
					}
					else {
						// neutral tile, neither queen can access tile
						// System.out.println("Tile "+row+","+col+" is cut off from all queens");
					}
				}
			}
		}
		
		// System.out.println("Total Score = "+score);
		return score;
	}
	
	/**
	 * Get list of tiles directly reachable by passed position
	 * Include all empty tiles, as well as queens that block path 
	 * @param board
	 * @param position
	 * @return list of accessible positions
	 */
	public ArrayList<ArrayList<Integer>> getDirectEmptyAndQueenTiles(Board_v2 board, ArrayList<Integer> position) {
		// create list to store empty tiles
		ArrayList<ArrayList<Integer>> directTiles = new ArrayList<ArrayList<Integer>>();
		// loop through directly reachable empty tiles along straight lines
		for (int count=0, dx=1, dy=1, row=position.get(0)+dy, col=position.get(1)+dx; count<8; row+=dy, col+=dx) {
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
			// check if found queen
			if (board.getTile(row,col)==Board_v2.BLACK || board.getTile(row,col)==Board_v2.WHITE) {
				// add queen to list before starting new line
				ArrayList<Integer> newTile = new ArrayList<Integer>();
				newTile.add(row);
				newTile.add(col); 
				directTiles.add(newTile); 
				// System.out.println("Queen at "+newTile+" added to reachable tiles");
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
		// return list of empty tiles
		return directTiles;
	}
	
//	/**
//	 * Heuristic that compares number of tiles with closer player queens to tiles with closer opponent queen
//	 * loses to totalActionsHeuristic() 
//	 * is very slow and inefficient
//	 * doesnt have to loop through each tile every time
//	 * only first time, then can run recursive function at queenCurrent, queenMoved, and adjacent to arrow
//	 * will have to run with opponent action too though
//	 * @param board
//	 * @return
//	 */
//	public int tileOwnershipHeuristic(Board_v2 board) {
//		// create score holder
//		int score = 0; // increase for close player, decrease for close opponent
//		// loop through board
//		for (int row=1; row<11; row++) {
//			// check if end of turn yet
//			if (this.checkEndTurn()) { break; }
//			for (int col=1; col<11; col++) {
//				// check if end of turn yet
//				if (this.checkEndTurn()) { break; }
//				// check if empty playable tile
//				if (board.getTile(row, col)==Board_v2.EMPTY) {
//					// define tile position
//					ArrayList<Integer> position = new ArrayList<Integer>();
//					position.add(row);
//					position.add(col);
//					// use recursive function to find depth of closest queen
//					int depth = this.tileOwnershipHeuristicRecurse(board,position,new boolean[11][11],1);
//					// check if player or opponent queen
//					if (depth>0) {
//						// increment score if current tile is closer to player queen
//						score += 1;
//					}
//					else if (depth<0) {
//						// decrement score if current tile is closer to opponent queen
//						score -= 1;
//					}
//				}
//			}
//		}
//		// return determined score
//		return score;
//	}
//	
//	/**
//	 * Use with above heuristic
//	 * @param board
//	 * @param position
//	 * @param checkedTiles boolean array holding if tiles have been recursed into already
//	 * @param currentDepth absolute depth of heuristic search
//	 * @return depth of closest queen, positive if player queen, negative if opponent queen, 0 if both equal
//	 */
//	public int tileOwnershipHeuristicRecurse(Board_v2 board, 
//												ArrayList<Integer> position, 
//												boolean[][] checkedTiles,
//												int currentDepth) {
//		// add current tile to checkedTiles
//		checkedTiles[position.get(0)][position.get(1)] = true;
//		// create boolean variables to hold whether queens were seen
//		boolean playerQueen = false, opponentQueen = false;
//		// create list to store empty tiles
//		ArrayList<ArrayList<Integer>> directTiles = new ArrayList<ArrayList<Integer>>();
//		
//		// loop through directly reachable empty tiles along straight lines looking for queens
//		for (int count=0, dx=1, dy=1, row=position.get(0)+dy, col=position.get(1)+dx; count<8; row+=dy, col+=dx) {
//			// check if end of turn yet
//			if (this.checkEndTurn()) { break; }
//			// check if empty tile
//			if (board.getTile(row,col)==Board_v2.EMPTY) {
//				// add tile to list
//				ArrayList<Integer> newTile = new ArrayList<Integer>();
//				newTile.add(row);
//				newTile.add(col); 
//				directTiles.add(newTile); 
//				// continue to next tile in line
//				continue;
//			}
//			// check if player queen tile
//			else if (board.getTile(row,col)==player) {
//				playerQueen = true;
//			}
//			// check if opponent queen tile
//			else if (board.getTile(row,col)==opponent) {
//				opponentQueen = true;
//			}
//			// define new line -> updated order of lines, doing diagonals first
//			switch (count++) {
//			case 0: dx=-1; dy=1; break;
//			case 1: dx=-1; dy=-1; break;
//			case 2: dx=1; dy=-1; break;
//			case 3: dx=1; dy=0; break;
//			case 4: dx=0; dy=1; break;
//			case 5: dx=-1; dy=0; break;
//			case 6: dx=0; dy=-1; break;
//			case 7: dx=1; dy=1; break;
//			}
//			// reset to start of line
//			row = position.get(0);
//			col = position.get(1);
//		}
//		
//		// determine tile ownership without recursion
//		if (playerQueen==true && opponentQueen==true) {
//			// neutral tile
//			return 0;
//		}
//		else if (playerQueen==true) {
//			// player tile
//			return currentDepth;
//		}
//		else if (playerQueen==false) {
//			// opponent tile
//			return -currentDepth;
//		}
//		
//		// define list to hold recursive scores
//		ArrayList<Integer> queenDistances = new ArrayList<Integer>();
//		// loop through direct tiles to recurse into
//		for (int i=0; i<directTiles.size(); i++) {
//			// check if end of turn yet
//			if (this.checkEndTurn()) { break; }
//			// check if already been checked
//			if (checkedTiles[directTiles.get(i).get(0)][directTiles.get(i).get(1)]) {
//				// already recursed into tile, dont repeat
//				continue;
//			}
//			// recurse into tile to get depth of closest queen and add to list
//			queenDistances.add( this.tileOwnershipHeuristicRecurse(board, directTiles.get(i), checkedTiles, ++currentDepth) );
//			
//		}
//		
//		// finding minimum variables
//		int smallestDepth = Integer.MAX_VALUE; // hold depth of closest queen
//		boolean smallestDepthTie = false; // is true when player and opponent queen equally close
//		// loop through scores to determine closest queen
//		for (int i=0; i<queenDistances.size(); i++) {
//			// check if current is strictly closer
//			if (Math.abs(queenDistances.get(i)) < Math.abs(smallestDepth)) {
//				smallestDepth = queenDistances.get(i);
//				smallestDepthTie = false;
//			}
//			// check if current is equally close but of opposite color
//			else if (Math.abs(queenDistances.get(i)) == Math.abs(smallestDepth) && queenDistances.get(i)-smallestDepth==0) {
//				smallestDepth = Math.abs(queenDistances.get(i));
//				smallestDepthTie = true;
//			}
//		}
//		
//		// check if 2 queens equally close
//		if (smallestDepthTie==true) {
//			return 0; // neutral tile
//		}
//		// return closest queen value including pos and neg
//		return smallestDepth;
//	}
//	
//	/**
//	 * NOT IN USE but has better structure than previous version which is slow -> complete recursive function at some point
//	 * @param board
//	 * @return
//	 */
//	public int tileOwnershipHeuristic_v2(Board_v2 board) {
//		// create array to hold which tiles belong to which player
//		int[][] tiles = new int[11][11];
//		// create holder of total board score
//		int score = 0;
//		// loop through tiles whos ownership have not yet been found
//		for (int row=1; row<11; row++) {
//			// check if end of turn yet
//			if (this.checkEndTurn()) { break; }
//			for (int col=1; col<11; col++) {
//				// check if end of turn yet
//				if (this.checkEndTurn()) { break; }
//				// check if tile ownership potentially not found (0 is default but also neutral tile)
//				if (tiles[row][col]==0) {
//					// get ownership of tile by recursing into it
//					ArrayList<Integer> position = new ArrayList<Integer>();
//					position.add(row);
//					position.add(col);
//					tiles[row][col] = this.tileOwnershipHeuristicRecurse_v2(tiles, board, position, 1);
//				}
//				// increment or decrement total score of board
//				if (tiles[row][col]>0) {
//					score++;
//				}
//				else if (tiles[row][col]<0) {
//					score--;
//				}
//			}
//		}
//		// return total board score
//		return score;
//	}
//	
//	/**
//	 * get ownership of passed tile and recurse into reachable tiles getting their closests
//	 * NOT COMPLETED
//	 * @param tiles 2d array to place ownership value into for each tile
//	 * @param board
//	 * @param position
//	 * @param depth current depth may not be needed if gonna count upwards from back
//	 * @return distance to closest queen, positive if player's, negative if opponents, 0 if neutral
//	 */
//	public int tileOwnershipHeuristicRecurse_v2(int[][] tiles, Board_v2 board, ArrayList<Integer> position, int depth) {
//		// loop through reachable tiles looking for queens reachable in one turn
//		// determine if queen(s) is(are) reachable and return +1, -1, or 0
//		// get closest queens to each of the reachable tiles
//		// determine closest queen to this tile and return it
//		
//		return 0; // temp
//	}
//	
//	public int tileOwnershipHeuristic_v3(Board_v2 board) {
//		// make var to hold score
//		int score = 0;
////		// make array to hold checked tiles -> not using at this point
////		boolean[][] checked = new boolean[11][11];
//		// loop through tiles
//		for (int row=1; row<11; row++) {
//			for (int col=1; col<11; col++) {
////				// check if already checked
////				if (checked[row][col]) {
////					// skip since already done
////					continue;
////				}
//				// find closest queen to position, add to score if player, take if opponent
//				ArrayList<Integer> position = new ArrayList<Integer>();
//				position.add(row);
//				position.add(col);
//				int closestQueen = this.getClosestQueen(board, position, new boolean[11][11], new int[11][11], 1);
//				if (closestQueen>0) { 
//					score += 1; 
//				}
//				else if (closestQueen<0) {
//					score -= 1;
//				}
//				System.out.println("Tile "+row+","+col+"'s score is "+closestQueen+" and board score is "+score);
//			}
//		}
//		// return total found score
//		return score; 
//	}
//	
//	/**
//	 * get depth of closest queen, positive if player, negative if opponent
//	 * used inside tileOwnershipHeuristic_v3()
//	 * @param board
//	 * @param row
//	 * @param col
//	 * @param checked
//	 * @return positive depth if player closer, negative if opponent closer, 0 if neither closer
//	 */
//	public int getClosestQueen(Board_v2 board, ArrayList<Integer> position, boolean[][] checked, int[][] checkedDepth, int depth) {
//		// check if already checked
////		if (checked[position.get(0)][position.get(1)]) {
////			System.out.println("Already recursed into this tile");
////			return 0;
////		}
//		// add current tile to checked array
//		checked[position.get(0)][position.get(1)] = true;
//		checkedDepth[position.get(0)][position.get(1)] = depth;
//		// check if current tile is terminal node
//		if (board.getTile(position)==player) {
//			System.out.println("Found player and returning "+depth);
//			return depth;
//		}
//		else if (board.getTile(position)==opponent) {
//			System.out.println("Found opponent and returning "+(-depth));
//			return -depth;
//		}
//		else if (board.getTile(position)==Board_v2.ARROW) {
//			return 0;
//		}
//		// get directly reachable tiles including queens
//		ArrayList<ArrayList<Integer>> directTiles = this.getDirectTiles(board, position);
//		// System.out.println("Size of direct tiles is "+directTiles.size());
//		// initialize closest players and opponents to furthest away possible
//		int closestPlayer = Integer.MAX_VALUE, closestOpponent = Integer.MIN_VALUE;
//		// loop through directly reachable tiles from current position
//		for (ArrayList<Integer> tile : directTiles) {
//			// check if already checked tile at shorter depth
//			if (checked[tile.get(0)][tile.get(1)]==true) {
//				// skip recursing into this tile
//				continue;
//			}
//			// recurse into this tile
//			int score = this.getClosestQueen(board, tile, checked, checkedDepth, depth+1);
//			// check if new closest player queen
//			if (score>0 && score<closestPlayer) {
//				// System.out.println("Player score updating to "+score);
//				closestPlayer = score;
//			}
//			// check if new closest opponent
//			else if (score<0 && score>closestOpponent) {
//				// System.out.println("Opponent score updating to "+score);
//				closestOpponent = score;
//			}
//		}
//		// compare closest player and opponent
//		if (closestPlayer==Integer.MAX_VALUE && closestOpponent==Integer.MIN_VALUE) {
//			// no players found
//			return 0;
//		}
//		else if (closestPlayer!=Integer.MAX_VALUE && Math.abs(closestPlayer)<Math.abs(closestOpponent)) {
//			// player closer
//			return closestPlayer;
//		}
//		else if (closestOpponent!=Integer.MIN_VALUE && Math.abs(closestOpponent)<Math.abs(closestPlayer)) {
//			// opponent closer
//			return closestOpponent;
//		}
//		else {
//			// both are equal distance
//			return 0;
//		}
//	}
//	
//	public ArrayList<ArrayList<Integer>> getDirectTiles(Board_v2 board, ArrayList<Integer> position) {
//		// create list to store empty tiles
//		ArrayList<ArrayList<Integer>> directTiles = new ArrayList<ArrayList<Integer>>();
//		// loop through directly reachable empty tiles along straight lines
//		for (int count=0, dx=1, dy=1, row=position.get(0)+dy, col=position.get(1)+dx; count<8; row+=dy, col+=dx) {
//			// check if empty tile
//			if (board.getTile(row,col)==Board_v2.EMPTY) {
//				// add tile to list
//				ArrayList<Integer> newTile = new ArrayList<Integer>();
//				newTile.add(row);
//				newTile.add(col); 
//				directTiles.add(newTile); 
//				// continue to next tile in line
//				continue;
//			}
//			// check if queen before moving to next line
//			if (board.getTile(row,col)!=Board_v2.OUTOFBOUNDS && board.getTile(row,col)!=Board_v2.ARROW) {
//				// add tile to list
//				ArrayList<Integer> newTile = new ArrayList<Integer>();
//				newTile.add(row);
//				newTile.add(col); 
//				directTiles.add(newTile);
//			}
//			// define new line -> updated order of lines, doing diagonals first
//			switch (count++) {
//			case 0: dx=-1; dy=1; break;
//			case 1: dx=-1; dy=-1; break;
//			case 2: dx=1; dy=-1; break;
//			case 3: dx=1; dy=0; break;
//			case 4: dx=0; dy=1; break;
//			case 5: dx=-1; dy=0; break;
//			case 6: dx=0; dy=-1; break;
//			case 7: dx=1; dy=1; break;
//			}
//			// reset to start of line
//			row = position.get(0);
//			col = position.get(1);
//		}
//		// return list of empty tiles
//		return directTiles;
//	}
//	
//	/**
//	 * Final attempt before mock tournament
//	 * Use adjacent tiles method to closest queen
//	 * Loop through rows of tree until queen found
//	 * finish row to check if there are other queens at same depth
//	 * 
//	 * currently is just adding score for first queen found, so if 2 queens are equal distances away then 
//	 * @param board
//	 * @return
//	 */
//	public int tileOwnershipHeuristic_v4(Board_v2 board) {
//		// var to hold total score
//		int totalScore = 0;
//		// loop through each space on board
//		for (int row=1; row<11; row++) {
//			for (int col=1; col<11; col++) {
//				// System.out.println("Analysing tile "+row+","+col);
//				// create position of closest queen
//				ArrayList<Integer> closestQueen = null;
//				// create queue 
//				ArrayList<ArrayList<Integer>> queue = new ArrayList<ArrayList<Integer>>();
//				// create array to hold if tiles are already checked
//				boolean[][] checked = new boolean[11][11];
//				// initialize row counter
//				int rowIdx = 0, nodeCount = 0;
//				// create bool for if queen is found -> use to finish row of tree then leave
//				boolean queenFound = false;
//				// add current tile to initialize queue
//				ArrayList<Integer> initialPosition = new ArrayList<Integer>();
//				initialPosition.add(row);
//				initialPosition.add(col);
//				queue.add(initialPosition);
//				// loop through queue (breadth first search) 
//				while (queue.size()>0) {
//					// System.out.println("Queue size is "+queue.size());
//					// remove first node from queue to look at
//					ArrayList<Integer> position = queue.remove(0);
//					// System.out.println("Looking at tile "+position);
//					// check if outofbounds or arrow
//					if (board.getTile(position)==Board_v2.OUTOFBOUNDS || board.getTile(position)==Board_v2.ARROW) {
//						// move to next item in list
//						continue;
//					}
//					// check if queen
//					else if (board.getTile(position)==Board_v2.BLACK || board.getTile(position)==Board_v2.WHITE) {
//						// check if queen already found this row
//						if (queenFound) {
//							// check if queens are different color
//							if (board.getTile(closestQueen)!=board.getTile(position)) {
//								// 2 queens are equally close to tile -> neither is closer
//								closestQueen = null;
//								break;
//							}
//						}
//						// update closestqueen to first found in row
//						closestQueen = position;
//						// mark that queen was found
//						queenFound = true;
//					}
//					// check if already checked
//					else if (checked[position.get(0)][position.get(1)]) {
//						// dont look into
//						continue;
//					}
//					// mark current tile as checked
//					checked[position.get(0)][position.get(1)] = true;
//					// get adjacent tiles to current tile
//					ArrayList<ArrayList<Integer>> adjacentTiles = board.getAdjacentTiles(position);
//					// loop through adjacent tiles
//					for (ArrayList<Integer> adjacentTile : adjacentTiles) {
//						// add all adjacent tiles to queue
//						queue.add(adjacentTile);
//					}
//					// increment tracker vars
//					nodeCount++;
//					// check if entering next row of tree
//					if (nodeCount>=4*((int)Math.pow(rowIdx+1, 2)-(int)Math.pow(rowIdx, 2)-1)) {
//						// check if queen found-> dont start next row
//						if (queenFound) {
//							break;
//						}
//						// reset trackers for next row
//						rowIdx++;
//						nodeCount = 0;
//					}
//				}
//				// determine which color closest queen is and increase/decrease score accordingly
//				if (closestQueen==null) {
//					// neither color queen
//					// System.out.println("Tile "+row+","+col+" is closer to neither queen");
//				}
//				else if (board.getTile(closestQueen)==player) {
//					totalScore++;
//					// System.out.println("Tile "+row+","+col+" is closest to "+board.getPlayerColorString(player));
//				}
//				else if (board.getTile(closestQueen)==opponent) {
//					totalScore--;
//					// System.out.println("Tile "+row+","+col+" is closest to "+board.getPlayerColorString(opponent));
//				}
//				// System.out.println("Total score is now "+totalScore);
//			}
//		}
//		// return total score
//		// System.out.println("Total score found = "+totalScore);
//		return totalScore;
//	}
//	

	
	
	//-- ISOLATED QUEENS TECHNIQUE METHODS --//
	/**
	 * check for queens who dont need to contribute to search tree rn
	 * can just find longest path
	 * will need to change getActions loop to skip isolated queens
	 */
	public void findIsolatedOrTrappedQueens(Board_v2 board, int player) {
		// reset trapped and isolated queens list
		this.trappedQueens = new ArrayList<ArrayList<Integer>>();
		this.isolatedQueens = new ArrayList<ArrayList<Integer>>();
		// get all player's queens on trueBoard
		ArrayList<ArrayList<Integer>> queens = board.getQueens(player);
		// loop through queens
		for (ArrayList<Integer> queen : queens) {
			// check if trapped queen
			if (this.isTrapped(board, queen)) {
				// add trapped queen to list
				this.trappedQueens.add(queen);
			}
			// check if disconnected from all opponent queens (only if not already trapped)
			else if (this.isIsolated(board, queen)) {
				// add isolated queen to list
				this.isolatedQueens.add(queen);
			}
		}
	}
	
	/**
	 * determine if passed queen is isolated from all opponent queens
	 * @param board
	 * @param queen
	 * @return
	 */
	public boolean isIsolated(Board_v2 board, ArrayList<Integer> queen) {
		// get target colour = opponent layer
		int opponent = (board.getTile(queen)==Board_v2.WHITE)?Board_v2.BLACK:Board_v2.WHITE;
		// determine if queen can reach an opponent queen
		boolean opponentFound = this.recurseAdjacentTilesForTarget(board, new boolean[11][11], opponent, queen.get(0), queen.get(1));
		if (opponentFound==false) {
			System.out.println("Queen at " + queen + " is isolated");
		}
		return !opponentFound;
	}
	
	/**
	 * Recurse tiles from input row and col to find target tile state
	 * @param board board to look on
	 * @param recursed tells when positions have already been recursed into
	 * @param target tile type to check for 
	 * @param position
	 * @return true if target found, false otherwise
	 */
	public boolean recurseAdjacentTilesForTarget(Board_v2 board, boolean[][] recursed, int target, int row, int col) {
		// add tile to checked array
		recursed[row][col] = true;
		// check if target -> return true, stop recursing
		if (board.getTile(row,col)==target) {
			return true;
		}
		// check if arrow (stop recursing)
		if (board.getTile(row,col)==Board_v2.ARROW) {
			return false;
		}
		// check all 8 adjacent tiles including diagonals
		for (int i=0; i<8; i++) {
			int j, k;
			switch (i) {
			case 0: j=0; k=1; break;
			case 1: j=1; k=1; break;
			case 2: j=1; k=0; break;
			case 3: j=1; k=-1; break;
			case 4: j=0; k=-1; break;
			case 5: j=-1; k=-1; break;
			case 6: j=-1; k=0; break;
			case 7: j=-1; k=1; break;
			default: j=0; k=0; 
			}
			// check if invalid position to recurse into
			if (board.getTile(row+j, col+k)==Board_v2.OUTOFBOUNDS) {
				// skip out of bounds position
				continue;
			}
			// check if already recursed
			if (recursed[row+j][col+k]==true) {
				// skip already recursed position
				continue;
			}
			// recurse into adjacent tile
			if (this.recurseAdjacentTilesForTarget(board, recursed, target, row+j, col+k)) {
				// adjacent tile found target, 
				return true;
			}
		}
		// no adjacent tile ticked true, therefore false
		return false; 
	}
	
	/**
	 * determine if passed queen has already been found to be isolated
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
	 * NEED TO FIX
	 * find first action for longest path 
	 * not at all the best way to do it
	 * has not been tested, but could probably use an actual longest path algorithm
	 * called from main getAction function
	 * @param queens list of isolated queens, look at first one
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> findLongestPath(ArrayList<ArrayList<Integer>> queens) {
		// find size of room
		// int size = this.recurseAdjacentTilesForSize(trueBoard, new boolean[11][11], player, queens.get(0).get(0), queens.get(0).get(1));
		// perform minimax to size
		return this.minimax(trueBoard, 4);
		// return this.iterativeMinimax(2, trueBoard);
	}
	
	/**
	 * gets size of isolated room
	 * NOT IN USE
	 * could probably use one of the Regions functions to do this
	 * @param board
	 * @param recursed
	 * @param player
	 * @param row
	 * @param col
	 * @return
	 */
	public int recurseAdjacentTilesForSize(Board_v2 board, boolean[][] recursed, int player, int row, int col) {
		// add tile to checked array
		recursed[row][col] = true;
		// create base score to add to
		int score = 0;
		// check if arrow (stop recursing)
		if (board.getTile(row,col)==Board_v2.ARROW || board.getTile(row,col)==player) {
			return 0;
		}
		// check if queen (
		// check all 8 adjacent tiles including diagonals
		for (int i=0; i<8; i++) {
			int j, k;
			switch (i) {
			case 0: j=0; k=1; break;
			case 1: j=1; k=1; break;
			case 2: j=1; k=0; break;
			case 3: j=1; k=-1; break;
			case 4: j=0; k=-1; break;
			case 5: j=-1; k=-1; break;
			case 6: j=-1; k=0; break;
			case 7: j=-1; k=1; break;
			default: j=0; k=0; 
			}
			// check if invalid position to recurse into
			if (board.getTile(row+j, col+k)==Board_v2.OUTOFBOUNDS) {
				// skip out of bounds position
				continue;
			}
			// check if already recursed
			if (recursed[row+j][col+k]==true) {
				// skip already recursed position
				continue;
			}
			// recurse into adjacent tile adding to total score
			score += this.recurseAdjacentTilesForSize(board, recursed, player, row+j, col+k);
			
		}
		// return new score 
		return score;
		
	}
	
	/**
	 * determine whether passed queen has no actions left
	 * @param board
	 * @param queen
	 * @return
	 */
	public boolean isTrapped(Board_v2 board, ArrayList<Integer> queen) {
		// check if any tiles to move to
		if (board.getDirectEmptyTiles(queen).isEmpty()) {
			System.out.println("Queen at "+queen+" is trapped");
			return true;
		}
		return false;
	}
	
	/**
	 * determine if passed queen has been previously found to have no actions left
	 * @param queen
	 * @return
	 */
	public boolean isOnTrappedList(ArrayList<Integer> queen) {
		for (ArrayList<Integer> trapped: this.trappedQueens) {
			if (trapped.get(0).equals(queen.get(0)) && trapped.get(1).equals(queen.get(1))) {
				// matches list item
				return true;
			}
		}
		// otherwise
		return false;
	}
	

	//-- REGIONS HEURISTICS --//
	
	/**
	 * Score passed board based on where queens are in relation to regions
	 * For each player queen on region tile:
	 * + size of region
	 * + for each opponent queen in same region divide by region size
	 * - for each other player queen in same region divide by region size
	 * + for each gateway in region 
	 * + for each adjacent region
	 * For each player queen on gateway tile:
	 * +in gateway (look at both regions
	 * +opponent in region with less connections and smaller size
	 * -player in same position
	 * - for each other player in same gateways
	 * 
	 * connections are not working correctly, do not rely heavily on this
	 * errors probably come from finding sides of gate
	 * 
	 * @param board
	 * @return
	 */
//	public int regionsHeuristic(Board_v2 board) {
//		// create total score to return
//		double totalScore = 0;
//		// get positions of each player and opponent
//		ArrayList<ArrayList<Integer>> players = board.getQueens(player);
//		ArrayList<ArrayList<Integer>> opponents = board.getQueens(opponent);
//		// loop through each player mainly adding to score
//		for (ArrayList<Integer> queen : players) {
//			double score = 0;
//			// check if in gateway
//			if (board.getRegionTile(queen)==Board_v2.GATEWAY) {
//				// get adjacent regions
//				ArrayList<Region> regions = board.getAdjacentRegions(queen);
//				// loop through adjacent regions
//				for (Region region1 : regions) {
//					// compare to other regions in list
//					for (Region region2 : regions) {
//						// skip if comparing to same region
//						if (region1.id==region2.id) {
//							continue;
//						}
//						// check if region1 is smaller region
//						if (region1.size<region2.size) {
//							// increase score for opponents here
//							score += region1.getQueenCount(opponent) / (region1.size==0?1:region1.size);
//						}
//						// check if region1 is larger region
//						else if (region1.size>region2.size) {
//							// decrease score for multiple connections btw regions
//							score -= (region1.getConnectionsCount(region2.id) - 1) / (region1.size==0?1:region1.size);
//						}
//						
//					}
//				}
//			}
//			// otherwise is in region tile
//			else {
//				// score based on size of region, others in region, connections
//				Region region = board.getRegion(queen); // how to access current region
//				score = region.size; // initialize score to size of region
//				score -= ( region.getQueenCount(player) - 1 ) / (region.size==0?1:region.size); // decrease score for any extra queens in region
//				score += region.getQueenCount(opponent) / (region.size==0?1:region.size); // increase score for opponents in region
//				// loop through adjacent regions
//				for (int i=0; i<region.adjacentRegions.size(); i++) {
//					score += region.regionConnections.get(i) / (region.size==0?1:region.size); // add connections to score
//				}
//			}
//			// add score to total score
//			totalScore += score;
//		}
//		// loop through each opponent mainly deducting from score
//		for (ArrayList<Integer> queen : opponents) {
//			double score = 0;
//			// check if in gateway
//			if (board.getRegionTile(queen)==Board_v2.GATEWAY) {
//				// get adjacent regions
//				ArrayList<Region> regions = board.getAdjacentRegions(queen);
//				// loop through adjacent regions
//				for (Region region1 : regions) {
//					// compare to other regions in list
//					for (Region region2 : regions) {
//						// skip if comparing to same region
//						if (region1.id==region2.id) {
//							continue;
//						}
//						// check if region1 is smaller region
//						if (region1.size<region2.size) {
//							// increase score for opponents here
//							score += region1.getQueenCount(player) / (region1.size==0?1:region1.size);
//						}
//						// check if region1 is larger region
//						else if (region1.size>region2.size) {
//							// decrease score for multiple connections btw regions
//							score -= (region1.getConnectionsCount(region2.id) - 1) / (region1.size==0?1:region1.size);
//						}
//						
//					}
//				}
//			}
//			// otherwise is in region tile
//			else {
//				// score based on size of region, others in region, connections
//				Region region = board.getRegion(queen); // how to access current region
//				// score = region.size; // initialize score to size of region -> not doing this?
//				score -= ( region.getQueenCount(opponent) - 1 ) / (region.size==0?1:region.size); // decrease score for any extra queens in region
//				score += region.getQueenCount(player) / (region.size==0?1:region.size); // increase score for opponents in region
//				// loop through adjacent regions
//				for (int i=0; i<region.adjacentRegions.size(); i++) {
//					score += region.regionConnections.get(i) / (region.size==0?1:region.size); // add connections to score
//				}
//			}
//			// add score to total score
//			totalScore += score;
//		}
//		// return double score
//		return (int) totalScore;
//	}
//
//	public int regionsHeuristic_v2(Board_v2 board) {
//		// create total score to return
//		int totalScore = 0;
//		// get positions of each player and opponent
//		ArrayList<ArrayList<Integer>> players = board.getQueens(player);
//		ArrayList<ArrayList<Integer>> opponents = board.getQueens(opponent);
//		// loop through each player mainly adding to score
//		for (ArrayList<Integer> queen : players) {
//			double score = 0;
//			// check if in gateway
//			if (board.getRegionTile(queen)==Board_v2.GATEWAY) {
//				// plus if opponents in smaller regions
//				// minus if opponents in larger regions
//				// plus if no other connections
//				// use with tile ownership heuristic
//				
//				// get adjacent regions
//				ArrayList<Region> regions = board.getAdjacentRegions(queen);
//				// loop through adjacent regions
//				for (Region region1 : regions) {
//					// loop through adjacent regions again to compare to others
//					for (Region region2 : regions) {
//						// check if smaller region
//						if (region1.regionTiles.size()<region2.regionTiles.size()) {
//							// increase score for opponents in smaller region
//							score += region1.getQueenCount(opponent);
//							score -= region1.getQueenCount(player);
//						}
//						// check if larger region
//						else if (region1.regionTiles.size()>region2.regionTiles.size()) {
//							// increase score for players in larger region
//							score += region1.getQueenCount(player);
//							score -= region1.getQueenCount(opponent);
//						}
//					}
//					// check if connects to one other region only
//					if (region1.adjacentRegions.size()<2) {
//						// increase score for blocking off only entrance to region
//						score += region1.size;
//					}
//				}
//			}
//			// otherwise is in region tile
//			else {
//				// plus for bigger region
//				// minus for extra player queens (small deduction)
//				// use with total actions heuristic
//				// minus for opponent queens
//				
//				
//				// get access to region queen is residing in
//				Region region = board.getRegion(queen); 
//				// increase score for size of region
//				score += region.size;
//				// decrease score for more than 2 queens in region
//				score -= (region.getQueenCount(player)-2); // 1Q=+1,2Q=0,3Q=-1,4Q=-2
//				// decrease score for opponent queens in region
//				score -= region.getQueenCount(opponent);
//			}
//			// add to total score
//			totalScore += score;
//		}
//		// loop through each opponent mainly deducting from score
//		for (ArrayList<Integer> queen : opponents) {
//			double score = 0;
//			// check if in gateway
//			if (board.getRegionTile(queen)==Board_v2.GATEWAY) {
//				// get adjacent regions
//				ArrayList<Region> regions = board.getAdjacentRegions(queen);
//				// loop through adjacent regions
//				for (Region region1 : regions) {
//					// loop through adjacent regions again to compare to others
//					for (Region region2 : regions) {
//						// check if smaller region
//						if (region1.regionTiles.size()<region2.regionTiles.size()) {
//							// increase score for opponents in smaller region
//							score += region1.getQueenCount(player);
//							score -= region1.getQueenCount(opponent);
//						}
//						// check if larger region
//						else if (region1.regionTiles.size()>region2.regionTiles.size()) {
//							// increase score for players in larger region
//							score += region1.getQueenCount(opponent);
//							score -= region1.getQueenCount(player);
//						}
//					}
//					// check if in sole set of gates to region
//					if (region1.adjacentRegions.size()<2) {
//						// increase score for blocking off only entrance to region
//						score += region1.size;
//					}
//				}
//			}
//			// otherwise is in region tile
//			else {
//				// get access to region queen is residing in
//				Region region = board.getRegion(queen); 
//				// increase score for size of region
//				score += region.size;
//				// decrease score for more than 2 queens in region
//				score -= (region.getQueenCount(opponent)-2); // 1Q=+1,2Q=0,3Q=-1,4Q=-2
//				// decrease score for opponent queens in region
//				score -= region.getQueenCount(player);
//			}
//			// take from total score
//			totalScore -= score;
//		}
//		// return total score
//		return totalScore;
//	}
//	
//	public int regionsHeuristic_v3(Board_v2 board) {
//		int totalScore = 0;
//		// loop through board regions
//		for (Region region : board.regions) {
//			// add to score size of region for each queen, subtract size for each opponent
//			totalScore += region.size * (region.getQueenCount(player) - region.getQueenCount(opponent));
//		}
//		// System.out.println("Giving score of "+totalScore);
//		return totalScore;
//	}
	
//	/**
//	 * Score board based on regions
//	 * @param board
//	 * @return
//	 */
//	public int regionsHeuristic_v4(Board_v2 board) {
//		int score = 0;
//		// loop through regions on board
//		for (Region region : board.regions) {
//			score += ( region.getQueensCount(board, player) * region.getSize() );
//			score -= ( region.getQueensCount(board, opponent) * region.getSize() );
//		}
//		return score; // temp
//	}
}
