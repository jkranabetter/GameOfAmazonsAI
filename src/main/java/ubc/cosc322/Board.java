package ubc.cosc322;

import java.util.ArrayList;

public class Board {
	//-- CONSTANTS --//
	public static final int EMPTY = 0; // empty space
	public static final int BLACK = 1; // black queen/player
	public static final int WHITE = 2; // white queen/player
	public static final int ARROW = 3; // arrow
	public static final int OUTOFBOUNDS = 4; // invalid value
	
	//-- FIELDS --//
	
	private int[][] tiles; // 2D array to hold value of each tile, [row][col], 0 row and col are ignored
	
	//-- CONSTRUCTORS --//
	
	/**
	 * create board with default initial state
	 */
	public Board() {
		this.tiles = new int[11][11];
		for (int j=0; j<11; j++) {
			for (int i=0; i<11; i++) {
				this.setTile(Board.EMPTY, j, i);
			}
		}
		// add initial queen positions
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
	 * create new board with state of passed board
	 * @param original board to copy state of
	 */
	public Board(Board original) {
		this();
		this.clone(original);
	}
	
	//-- METHODS --//
	
	/**
	 * update this board to state of passed board
	 * @param original board to copy state of
	 */
	public void clone(Board original) {
		for (int j=0; j<11; j++) {
			for (int i=0; i<11; i++) {
				this.setTile(original.getTile(j,i), j, i);
			}
		}
	}
	
	/**
	 * set state of specified tile
	 * @param newOccupant state to set tile to
	 * @param row row of tile
	 * @param col col of tile
	 */
	public void setTile(int newOccupant, int row, int col) {
		// check for invalid tile
		if (row<1 || row>10 || col<1 || col>10) {
			// System.out.println("Can't set this tile");
			return;
		}
		// set tile to new value
		this.tiles[row][col] = newOccupant;
	}
	
	/**
	 * set state of specified tile
	 * @param newOccupant state to set tile to
	 * @param position arrayList containing row and col of tile
	 */
	public void setTile(int newOccupant, ArrayList<Integer> position) {
		this.setTile(newOccupant, position.get(0), position.get(1));
	}
	
	/**
	 * get state of specified tile
	 * @param row row of tile
	 * @param col col of tile
	 * @return value of tile
	 */
	public int getTile(int row, int col) {
		// check for invalid tile
		if (row<1 || row>10 || col<1 || col>10) {
			// System.out.println("Can't get this tile");
			return Board.OUTOFBOUNDS;
		}
		// return tile value
		return this.tiles[row][col];
	}
	
	/**
	 * get state of specified tile
	 * @param position arrayList containing row and col of tile
	 * @return value of tile
	 */
	public int getTile(ArrayList<Integer> position) {
		return this.getTile(position.get(0),position.get(1));
	}
	
	/**
	 * determine if passed player has no moves left(ie lost game)
	 * @param player to check 
	 * @return 0 not lost, passed player if passed player lost
	 */
	public int checkLose(int player) {
		// get queens of passed player
		ArrayList<ArrayList<Integer>> queens = this.getQueens(player);
		
		// loop through each queen
		for (ArrayList<Integer> queen : queens) {
			// get all directly accessible tiles for each queen
			// if were to use unique method could skip finding more than one space
			ArrayList<ArrayList<Integer>> directTiles = this.getDirectTiles(queen); 
			
			// check if at least one option available to player
			if ( !(directTiles.isEmpty()) ) {
				return 0;
			}
		}
		
		// no tiles to move to therefore player loses
		return player;
	}	
	
	/**
	 * determine if either player has won the game
	 * has been replaced by checkLose(player) method
	 * @return 0 if no win, Board.WHITE if white wins, Board.BLACK if black wins
	 */
	public int checkWin() {
		// loop through all queens, checking to see if all can't move
		
		// variables
		int winner = 0; // 0==no win, 1==black win, 2==white win, no win by default
		
		// get black queens
		ArrayList<ArrayList<Integer>> queens = this.getQueens(Board.BLACK);
		
		// loop through black queens checking for movement
		for (ArrayList<Integer> queen : queens) {
			if ( !(this.getDirectTiles(queen).isEmpty()) ) {
				// System.out.println("Black can move"); // TESTING
				// this queen can move to at least one spot
				winner = Board.BLACK;
				// no need to check other queens
				break;
			}
		}
		
		// get white queens
		queens = this.getQueens(Board.WHITE);
		
		// loop through white queens checking for movement
		for (ArrayList<Integer> queen : queens) {
			if ( !(this.getDirectTiles(queen).isEmpty()) ) {
				// this queen can move to at least one spot
				// System.out.println("White can move"); // TESTING
				if (winner==0) {
					// only white can move
					winner = Board.WHITE; 
				}
				else if (winner==Board.BLACK) { 
					// both can move
					winner = 0; 
				}
				// no need to check other queens
				break;
			}
		}
		
		// return winner value
		return winner;
	}
	
	/**
	 * get a list of positions of passed players queens
	 * @param player colour of queens to get positions of
	 * @return list of positions of queens (length == 4)
	 */
	public ArrayList<ArrayList<Integer>> getQueens(int player) {
		// create list of queens to return
		ArrayList<ArrayList<Integer>> queens = new ArrayList<ArrayList<Integer>>();
		
		// loop through each position on board
		for (int j=1; j<11; j++) {
			for (int i=1; i<11; i++) {
				if (this.getTile(j,i)==player) {
					// add queen to list from current position
					ArrayList<Integer> newQueen = new ArrayList<Integer>(); // create queen to add
					newQueen.add(j); // add row
					newQueen.add(i); // add col
					queens.add(newQueen); // add queen to list
				}
				if (queens.size()>=4) { break; } // quick escape, all queens accounted for
			}
			if (queens.size()>=4) { break; } // quick escape, all queens accounted for
		}
		
		// return list of queens
		return queens;
	}
	
	/**
	 * get list of all directly reachable tiles from passed position
	 * @param initialPosition reference position to use for search
	 * @return list of directly reachable tiles
	 */
	public ArrayList<ArrayList<Integer>> getDirectTiles(ArrayList<Integer> initialPosition) {
		// create list to hold all direct tiles
		ArrayList<ArrayList<Integer>> directTiles = new ArrayList<ArrayList<Integer>>();
		
		// loop through all directly reachable tiles
		for (int count=0, dx=1, dy=0, j=initialPosition.get(0)+dy, i=initialPosition.get(1)+dx; count<8; j+=dy, i+=dx) {
			// check for empty tile
			if (this.getTile(j,i)==Board.EMPTY) {
				// add tile to list
				ArrayList<Integer> newTile = new ArrayList<Integer>();
				newTile.add(j);
				newTile.add(i); 
				directTiles.add(newTile); 
				
				// continue to next tile in line
				continue;
			}
			// switch direction (change dx and dy)
			switch (count++) {
			case 0: dx=1; dy=1; break;
			case 1: dx=0; dy=1; break;
			case 2: dx=-1; dy=1; break;
			case 3: dx=-1; dy=0; break;
			case 4: dx=-1; dy=-1; break;
			case 5: dx=0; dy=-1; break;
			case 6: dx=1; dy=-1; break;
			case 7: dx=0; dy=0; break; // not necessary b/c leaving loop 
			}
			
			// reset to start of new line (change i and j)
			i = initialPosition.get(1);
			j = initialPosition.get(0);
		}
		
		// return list of directTiles
		return directTiles;
	}
	 
	/**
	 * update board with passed action
	 * @param player player doing action
	 * @param action list of positions representing player action
	 */
	public void applyAction(int player, ArrayList<ArrayList<Integer>> action) {
		// split action up into it's 3 parts
		ArrayList<Integer> queenCurrent = action.get(0);
		ArrayList<Integer> queenMoved = action.get(1);
		ArrayList<Integer> arrow = action.get(2);
		
		// check validity of action 
		if ( !(this.validateAction(player, queenCurrent, queenMoved, arrow)) ) {
			switch (player) {
			case Board.BLACK: System.out.println("Black action invalid."); break;
			case Board.WHITE: System.out.println("White action invalid."); break;
			}
			// return early without applying invalid move
			return;
		}
		
		// make changes to board using move inputs
		this.setTile(Board.EMPTY, queenCurrent);
		this.setTile(player, queenMoved);
		this.setTile(Board.ARROW, arrow);
	}
	
	/**
	 * check validity of passed action by passed player
	 * @param player player doing action
	 * @param queenCurrent current position of queen to move
	 * @param queenMoved position to move queen to
	 * @param arrow position to throw arrow
	 * @return true if valid, false if invalid
	 */
	public boolean validateAction(	int player, 
									ArrayList<Integer> queenCurrent, 
									ArrayList<Integer> queenMoved, 
									ArrayList<Integer> arrow	) {
		// create clone of board to use for testing purposes
		Board temp = new Board(this);
		
		// check if queenCurrent holds your queen
		if (temp.getTile(queenCurrent)!=player) {
			// invalid action
			System.out.println("Your queen is not currently there.");
			return false;
		}
		
		// check if currentQueen can move to queenMoved
		ArrayList<ArrayList<Integer>> options = temp.getDirectTiles(queenCurrent);
		boolean valid = false;
		for (ArrayList<Integer> option : options) {
			if ( option.get(0)==queenMoved.get(0) && option.get(1)==queenMoved.get(1) ) {
				// valid move
				valid = true;
				break;
			}
		}
		if (valid==false) {
			// action not contained in valid moves list
			System.out.println("Queen can't be moved there.");
			return false;
		}
		
		// move queen on temp board
		temp.setTile(Board.EMPTY, queenCurrent);
		temp.setTile(player, queenMoved);
		
		// check if arrow is reachable from queenMoved
		ArrayList<ArrayList<Integer>> arrows = temp.getDirectTiles(queenMoved);
		valid = false;
		for (ArrayList<Integer> option : arrows) {
			if ( option.get(0)==arrow.get(0) && option.get(1)==arrow.get(1) ) {
				// valid move
				valid = true;
				break;
			}
		}
		if (valid==false) {
			// action not contained in valid actions list
			System.out.println("Arrow can't be thrown there.");
			return false;
		}
		
		// nothing ticked false -> valid move
		return true;
	}
	
	/**
	 * output visual representation of current board state
	 */
	public String toString() {
		String output = "\nState of Board: \n";
		output += "  -   -   -   -   -   -   -   -   -   -  \n";
		for (int j=10; j>=1; j--) {
			output += "|";
			for (int i=1; i<11; i++) {
				switch (this.tiles[j][i]) {
				case Board.EMPTY: output += "   "; break;
				case Board.BLACK: output += " B "; break;
				case Board.WHITE: output += " W "; break;
				case Board.ARROW: output += " A "; break;
				}
				output += "|";
			}
			output += "\n";
			output += "  -   -   -   -   -   -   -   -   -   -  \n";
		}
		return output;
	}
}
