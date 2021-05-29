package ubc.cosc322;

import java.util.ArrayList;

public class Board_v2 {
	//-- CONSTANTS --//
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int ARROW = 3;
	public static final int OUTOFBOUNDS = 4;
	
	//-- FIELDS --//
	/**
	 * 2d positional dataframe for board
	 * indices are [row][col]
	 * 0 row and 0 col are ignored
	 */
	protected int[][] tiles;
	
	//-- CONSTRUCTORS --//
	/**
	 * Create board with manual inputted queen locations
	 */
	public Board_v2() {
		this.tiles = new int[11][11];
		for (int j=0; j<11; j++) {
			for (int i=0; i<11; i++) {
				this.setTile(Board.EMPTY, i, j);
			}
		}
		this.setTile(Board.BLACK, 10, 4);
		this.setTile(Board.BLACK, 10, 7);
		this.setTile(Board.BLACK, 7, 1);
		this.setTile(Board.BLACK, 7, 10);
		this.setTile(Board.WHITE, 1, 4);
		this.setTile(Board.WHITE, 1, 7);
		this.setTile(Board.WHITE, 4, 1);
		this.setTile(Board.WHITE, 4, 10);
	}
	
	/**
	 * Create board from server message details in game action start
	 * @param gameState list recieved from server message details 
	 */
	public Board_v2(ArrayList<Integer> gameState) {
		this.tiles = new int[11][11];
	}
	
	/**
	 * Create board as copy of passed board
	 * @param original board to copy state of
	 */
	public Board_v2(Board_v2 original) {
		this.tiles = new int[11][11];
		this.clone(original);
	}
	
	//-- METHODS --//
	/**
	 * Set tile to new state 
	 * @param newState state to set tile to
	 * @param row row of tile to set
	 * @param col col of tile to set
	 */
	public void setTile(int newState, int row, int col) {
		// check for invalid position
		if (row<1 || row>10 || col<1 || col>10) {
			return;
		}
		// set specified tile
		this.tiles[row][col] = newState;
	}
	
	/**
	 * Set tile to new state 
	 * @param newState state to set tile to
	 * @param position position of tile to set
	 */
	public void setTile(int newState, ArrayList<Integer> position) {
		this.setTile(newState, position.get(0), position.get(1));
	}
	
	/**
	 * Get state of tile
	 * @param row row of tile to get
	 * @param col col of tile to get
	 * @return
	 */
	public int getTile(int row, int col) {
		// check for invalid tile
		if (row<1 || row>10 || col<1 || col>10) {
			return Board.OUTOFBOUNDS;
		}
		// return specified tile
		return this.tiles[row][col];
	}
	
	/**
	 * Get state of tile
	 * @param position position of tile to get
	 * @return
	 */
	public int getTile(ArrayList<Integer> position) {
		return this.getTile(position.get(0), position.get(1));
	}
	
	/**
	 * Set tiles to states of passed board
	 * @param original board to clone
	 */
	public void clone(Board_v2 original) {
		for (int row=1; row<11; row++) {
			for (int col=1; col<11; col++) {
				this.setTile(original.getTile(row,col), row, col);
			}
		}
	}
	
	/**
	 * Get list of positions of each queen belonging to passed player
	 * @param player player owning queens to find
	 * @return list of queen positions
	 */
	public ArrayList<ArrayList<Integer>> getQueens(int player) {
		// create list to store queens
		ArrayList<ArrayList<Integer>> queens = new ArrayList<ArrayList<Integer>>();
		// loop through each tile on board
		for (int row=1; row<11; row++) {
			for (int col=1; col<11; col++) {
				// check if passed player's queen
				if (this.getTile(row,col)==player) {
					// create queen
					ArrayList<Integer> queen = new ArrayList<Integer>();
					queen.add(row);
					queen.add(col);
					// add queen to list
					queens.add(queen);
				}
			}
		}
		// return list of found queens
		return queens;
	}
	
	/**
	 * Get list of directly reachable empty tiles from passed position
	 * @param position position to start search at
	 * @return list of positions that can be reached
	 */
	public ArrayList<ArrayList<Integer>> getDirectEmptyTiles(ArrayList<Integer> position) {
		// create list to store empty tiles
		ArrayList<ArrayList<Integer>> directTiles = new ArrayList<ArrayList<Integer>>();
		// loop through directly reachable empty tiles along straight lines
		for (int count=0, dx=1, dy=0, row=position.get(0)+dy, col=position.get(1)+dx; count<8; row+=dy, col+=dx) {
			// check if empty tile
			if (this.getTile(row,col)==Board.EMPTY) {
				// add tile to list
				ArrayList<Integer> newTile = new ArrayList<Integer>();
				newTile.add(row);
				newTile.add(col); 
				directTiles.add(newTile); 
				// continue to next tile in line
				continue;
			}
			// define new line
			switch (count++) {
			case 0: dx=1; dy=1; break;
			case 1: dx=0; dy=1; break;
			case 2: dx=-1; dy=1; break;
			case 3: dx=-1; dy=0; break;
			case 4: dx=-1; dy=-1; break;
			case 5: dx=0; dy=-1; break;
			case 6: dx=1; dy=-1; break;
			case 7: dx=0; dy=0; break;
			}
			// reset to start of line
			row = position.get(0);
			col = position.get(1);
		}
		// return list of empty tiles
		return directTiles;
	}
	
	/**
	 * Get validity of passed action
	 * @param player player performing action
	 * @param queenCurrent position of queen to move
	 * @param queenMoved position for queen to move to
	 * @param arrow position to throw arrow
	 * @return true if passed action is valid, false if invalid action
	 */
	public boolean getActionValidity(	int player, 
										ArrayList<Integer> queenCurrent, 
										ArrayList<Integer> queenMoved, 
										ArrayList<Integer> arrow) {
		// check if queenCurrent is player's queen
		if (this.getTile(queenCurrent)!=player) {
			System.out.println(this.getPlayerColorString(player) + "'s queen not at " + queenCurrent);
			return false;
		}
		// check if queenMoved is empty
		if (this.getTile(queenMoved)!=Board.EMPTY) {
			System.out.println(this.getPlayerColorString(player) + " cannot move queen to " + queenMoved);
			return false;
		}
		// check if queenMoved is reachable by queenCurrent
		boolean flag = false;
		ArrayList<ArrayList<Integer>> tiles = this.getDirectEmptyTiles(queenCurrent);
		for (ArrayList<Integer> tile : tiles) {
			if (tile.get(0)==queenMoved.get(0) && tile.get(1)==queenMoved.get(1)) {
				flag = true;
			}
		}
		if (flag==false) {
			System.out.println(this.getPlayerColorString(player) + "'s queen cannot reach " + queenMoved);
			return false;
		}
		// move queen out of queen current
		this.setTile(Board.EMPTY, queenCurrent);
		// check if arrow is empty
		if (this.getTile(arrow)!=Board.EMPTY) {
			// reset queen to current position before returning
			this.setTile(player, queenCurrent);
			System.out.println(this.getPlayerColorString(player) + "'s arrow can not be thrown to " + arrow);
			return false;
		}
		// check if arrow is reachable from queenMoved
		flag = false;
		tiles = this.getDirectEmptyTiles(queenMoved);
		for (ArrayList<Integer> tile : tiles) {
			if (tile.get(0)==arrow.get(0) && tile.get(1)==arrow.get(1)) {
				flag = true;
			}
		}
		if (flag==false) {
			// reset queen to current position before returning
			this.setTile(player, queenCurrent);
			System.out.println(this.getPlayerColorString(player) + "'s arrow cannot reach " + arrow);
			return false;
		}
		// reset queen to current position
		this.setTile(player, queenCurrent);
		// return true if no false flags ticked
		// System.out.println(this.getPlayerColorString(player) + "'s action is valid.");
		return true;
	}
	
	/**
	 * Check if passed player has lost
	 * This is called by COSC322TEST file before having ai determine new action
	 * @param player player to check 
	 * @return true if passed player has lost, false otherwise
	 */
	public boolean checkLose(int player) {
		// get passed player's queens
		ArrayList<ArrayList<Integer>> queens = this.getQueens(player);
		// loop through queens
		for (ArrayList<Integer> queen : queens) {
			// get directly reachable empty tiles for current queen
			ArrayList<ArrayList<Integer>> directTiles = this.getDirectEmptyTiles(queen);
			// check if at least one tile can be reached
			if (directTiles.isEmpty()==false) {
				// at least 1 queen can move to at least 1 tile
				System.out.println(this.getPlayerColorString(player) + " can still move.");
				return false;
			}
		}
		// no queen could move therefore passed player loses
		System.out.println(this.getPlayerColorString(player) + " can no longer move.");
		return true;
	}
	
	/**
	 * Update board with passed action
	 * This is called by COSC322TEST file to update our version of board
	 * @param player player performing action
	 * @param queenCurrent position of queen to move
	 * @param queenMoved position for queen to move to
	 * @param arrow position to throw arrow
	 */
	public void applyAction(int player,
							ArrayList<Integer> queenCurrent, 
							ArrayList<Integer> queenMoved, 
							ArrayList<Integer> arrow) {
		// check validity of action
		if ( this.getActionValidity(player, queenCurrent, queenMoved, arrow)==false ) {
			System.out.println(this.getPlayerColorString(player) + "'s action is invalid.");
		}
		// update board with action
		this.setTile(Board.EMPTY, queenCurrent);
		this.setTile(player, queenMoved);
		this.setTile(Board.ARROW, arrow);
	}
	
	public void outputActionToConsole(ArrayList<Integer> queenCurrent, 
										ArrayList<Integer> queenMoved, 
										ArrayList<Integer> arrow) {
		System.out.println("Moved queen from " + queenCurrent + " to " + queenMoved + " and threw arrow to " + arrow);
	}
	
	/**
	 * get color of player in string format
	 * @param player player color in int format
	 * @return player color in string format
	 */
	public String getPlayerColorString(int player) {
		if (player==Board.BLACK) {
			return "Black";
		}
		else {
			return "White";
		}
	}
	
	/**
	 * Get representation of board in string format
	 */
	public String toString() {
		String output = "\nState of Board: \n";
		output += "  -   -   -   -   -   -   -   -   -   -  \n";
		for (int row=10; row>=1; row--) {
			output += "|";
			for (int col=1; col<11; col++) {
				switch (this.tiles[row][col]) {
				case Board.EMPTY: output += "   "; break;
				case Board.BLACK: output += " B "; break;
				case Board.WHITE: output += " W "; break;
				case Board.ARROW: output += " A "; break;
				}
				output += "|";
			}
			// output += " " + row + "\n";
			output += "\n";
			output += "  -   -   -   -   -   -   -   -   -   -  \n";
		}
		// output += "  1   2   3   4   5   6   7   8   9   10 \n";
		return output;
	}
}
