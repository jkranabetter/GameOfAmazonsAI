package ubc.cosc322;

import java.util.ArrayList;

public abstract class Player {
	//-- FIELDS --//
	int player, opponent;
	Board_v2 trueBoard;
	
	//-- CONSTRUCTORS --//
	/**
	 * Create Ai with passed color and with connection to passed board
	 * @param player
	 * @param board
	 */
	public Player(int playerInt, Board_v2 board) {
		this.player = playerInt;
		this.opponent = (this.player==Board_v2.BLACK)?Board_v2.WHITE:Board_v2.BLACK;
		trueBoard = board;
	}
	
	//-- METHODS --//
	public int getColorInt() {
		return this.player;
	}
	
	/**
	 * Get all possible actions passed player can make on passed board
	 * @param player
	 * @param board
	 * @return list of actions in form [queenCurrent, queenMoved, arrow]
	 */
	public ArrayList<ArrayList<ArrayList<Integer>>> getAllActions(int player, Board_v2 board) {
		// copy board to not accidentally change it
		board = new Board_v2(board);
		// create list to store actions
		ArrayList<ArrayList<ArrayList<Integer>>> actions = new ArrayList<ArrayList<ArrayList<Integer>>>();
		// get list of passed player's queens
		ArrayList<ArrayList<Integer>> queens = board.getQueens(player);
		// loop through potential queenCurrent positions
		for (ArrayList<Integer> queenCurrent : queens) {
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
	 * Is called by COSC322TEST to get action to send to server
	 * must be implemented
	 * @return action to send to server
	 */
	public abstract ArrayList<ArrayList<Integer>> getAction();
	
}
