package ubc.cosc322;

import java.util.ArrayList;

public class Board {
	// constant values for tile to hold
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int ARROW = 3;
	public static final int OUTOFBOUNDS = 4;
	
	// 2d array holding integers representing what is occupying each tile
	private int[][] tiles; // tiles[xPos][yPos], 0 row&col are set to 0(EMPTY) but cant be accessed
	
	// constructors
	
	// create board with default game setup
	public Board() {
		this.tiles = new int[11][11];
		for (int j=0; j<11; j++) {
			for (int i=0; i<11; i++) {
				this.setTile(Board.EMPTY, i, j);
			}
		}
		// add initial queen positions
		this.setTile(Board.BLACK, 4, 10);
		this.setTile(Board.BLACK, 7, 10);
		this.setTile(Board.BLACK, 1, 7);
		this.setTile(Board.BLACK, 10, 7);
		this.setTile(Board.WHITE, 4, 1);
		this.setTile(Board.WHITE, 7, 1);
		this.setTile(Board.WHITE, 1, 4);
		this.setTile(Board.WHITE, 10, 4);
	}
	// create board by copying the layout of a passed board
	public Board(Board board) {
		this();
		this.clone(board);
	}
	
	// methods
	
	// change the value of a tile (specified by 2 separate integers) to passed newOccupant value
	public void setTile(int newOccupant, int x, int y) {
		// check for invalid tile
		if (x<1 || x>10 || y<1 || y>10) {
			// System.out.println("Can't set this tile");
			return;
		}
		this.tiles[x][y] = newOccupant;
	}
	
	// change the value of a tile (specified by arraylist of 2 integers) to passed newOccupant value
	public void setTile(int newOccupant, ArrayList<Integer> position) {
		this.setTile(newOccupant, position.get(1), position.get(0));
	}
	
	// determine value of a tile specified by 2 separate integers
	public int getTile(int x, int y) {
		// check for invalid tile
		if (x<1 || x>10 || y<1 || y>10) {
			// System.out.println("Can't get this tile");
			return Board.OUTOFBOUNDS;
		}
		// return tile value
		return this.tiles[x][y];
	}
	
	// determine value of a tile specified by arraylist of 2 integers
	public int getTile(ArrayList<Integer> position) {
		return this.getTile(position.get(1),position.get(0));
	}
	
	// copy each tile from a passed board to this board
	public void clone(Board original) {
		for (int j=0; j<11; j++) {
			for (int i=0; i<11; i++) {
				this.setTile(original.getTile(i,j), i, j);
			}
		}
	}
	
	// replacing checkWin, returns 0 if game not done, otherwise passed player returned
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
	
	// determine if either player can no longer make any more moves
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
	
	// get locations of all passed player's queens 
	public ArrayList<ArrayList<Integer>> getQueens(int player) {
		// create list of queens to return
		ArrayList<ArrayList<Integer>> queens = new ArrayList<ArrayList<Integer>>();
		
		// loop through each position on board
		for (int j=1; j<11; j++) {
			for (int i=1; i<11; i++) {
				if (this.getTile(i,j)==player) {
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
	
	// get all valid tile locations to move directly to (for queens and arrows) from passed position
	public ArrayList<ArrayList<Integer>> getDirectTiles(ArrayList<Integer> initialPosition) {
		// create list to hold all movement options
		ArrayList<ArrayList<Integer>> options = new ArrayList<ArrayList<Integer>>();
		
		// loop through all visible options for movement
		int initialX = initialPosition.get(1), initialY = initialPosition.get(0);
		for (int count=0, dx=1, dy=0, i=initialX+dx, j=initialY+dy; count<8; i+=dx, j+=dy) {
			if (this.getTile(i, j)==Board.EMPTY) {
				// add empty tile position to options and continue along current line
				ArrayList<Integer> newTile = new ArrayList<Integer>();
				newTile.add(j); // add row
				newTile.add(i); // add col
				options.add(newTile); // add to list of options
			}
			else {
				// obstacle has stopped loop
				// switch direction (change dx and dy)
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
				// set to start of line (change i and j)
				i=initialX;
				j=initialY;
			}
		}
		
		// return list of options
		return options;
	}
	
	// take passed action from AI and apply it to board 
	public void applyAction(int player, ArrayList<ArrayList<Integer>> action) {
		// split action up into it's 3 parts
		ArrayList<Integer> queenCurrent = action.get(0);
		ArrayList<Integer> queenMoved = action.get(1);
		ArrayList<Integer> arrow = action.get(2);
		
		// check validity of action 
		if ( !(this.validateAction(player, queenCurrent, queenMoved, arrow)) ) {
			System.out.println("Action not valid. You lose");
			// return early without applying invalid move
			return;
		}
		
		// make changes to board using move inputs
		this.setTile(Board.EMPTY, queenCurrent);
		this.setTile(player, queenMoved);
		this.setTile(Board.ARROW, arrow);
	}
	
	// check validity of a player's chosen action before applying it
	public boolean validateAction(int player, 
								ArrayList<Integer> queenCurrent, 
								ArrayList<Integer> queenMoved, 
								ArrayList<Integer> arrow) {
		// create clone of board to use for testing purposes
		Board temp = new Board(this);
		
		// check if queenCurrent holds your queen
		if (temp.getTile(queenCurrent)!=player) {
			// invalid action
			System.out.println("No queen to move");
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
			System.out.println("Queen can't move there");
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
			System.out.println("Arrow can't be thrown there");
			return false;
		}
		
		// nothing ticked false -> valid move
		return true;

	}
	
	// output board as string so that it can be printed to console/file
	public String toString() {
		String output = "\nCurrent Board: \n";
		output += "  -   -   -   -   -   -   -   -   -   -  \n";
		for (int j=10; j>=1; j--) {
			output += "|";
			for (int i=1; i<11; i++) {
				switch (this.tiles[i][j]) {
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
