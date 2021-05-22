package ubc.cosc322;

import java.util.ArrayList;

public class Board {
	// constants 
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int ARROW = 3;
	public static final int OUTOFBOUNDS = 4;
	
	// fields
	private int[][] tiles; // tiles[xPos][yPos], 0 row&col are set to 0(EMPTY) but cant be accessed
	
	// constructors
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
	// create board with state of passed board
	public Board(Board board) {
		this();
		this.clone(board);
	}
	
	// methods
	public void setTile(int newOccupent, int x, int y) {
		// check for invalid tile
		if (x<1 || x>10 || y<1 || y>10) {
			// System.out.println("Can't set this tile");
			return;
		}
		this.tiles[x][y] = newOccupent;
	}
	public void setTile(int newOccupant, ArrayList<Integer> position) {
		this.setTile(newOccupant, position.get(1), position.get(0));
	}
	
	public int getTile(int x, int y) {
		// check for invalid tile
		if (x<1 || x>10 || y<1 || y>10) {
			// System.out.println("Can't get this tile");
			return Board.OUTOFBOUNDS;
		}
		// return tile value
		return this.tiles[x][y];
	}
	public int getTile(ArrayList<Integer> position) {
		return this.getTile(position.get(1),position.get(0));
	}
	
	public void clone(Board original) {
		for (int j=0; j<11; j++) {
			for (int i=0; i<11; i++) {
				this.setTile(original.getTile(i,j), i, j);
			}
		}
	}
	
	public int checkWin() {
		// loop through all queens, checking to see if all can't move
		
		// variables
		int winner = 0; // 0==no win, 1==black win, 2==white win, no win by default
		
		// get black queens
		ArrayList<ArrayList<Integer>> queens = this.getQueens(Board.BLACK);
		
		// loop through black queens checking for movement
		for (ArrayList<Integer> queen : queens) {
			if ( !(this.getMovementOptions(queen).isEmpty()) ) {
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
			if ( !(this.getMovementOptions(queen).isEmpty()) ) {
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
	
	public ArrayList<ArrayList<Integer>> getQueens(int team) {
		// create list of queens to return
		ArrayList<ArrayList<Integer>> queens = new ArrayList<ArrayList<Integer>>();
		
		// loop through each position on board
		for (int j=1; j<11; j++) {
			for (int i=1; i<11; i++) {
				if (this.getTile(i,j)==team) {
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
	
	public ArrayList<ArrayList<Integer>> getMovementOptions(ArrayList<Integer> initialPosition) {
		// create list to hold all movement options
		ArrayList<ArrayList<Integer>> options = new ArrayList<ArrayList<Integer>>();
		
		// attempt 2 at looping through all visible options for movement
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
	
	public void applyMove(int team, ArrayList<ArrayList<Integer>> move) {
		// split move up into it's 3 parts
		ArrayList<Integer> queenCurrent = move.get(0);
		ArrayList<Integer> queenMoved = move.get(1);
		ArrayList<Integer> arrow = move.get(2);
		
		// check validity of move 
		if ( !(this.validateMove(team, queenCurrent, queenMoved, arrow)) ) {
			System.out.println("Move not valid. You lose");
			// return early without applying  invalid move
			return;
		}
		
		// make changes to board using move inputs
		this.setTile(Board.EMPTY, queenCurrent);
		this.setTile(team, queenMoved);
		this.setTile(Board.ARROW, arrow);
	}
	
	public boolean validateMove(int team, 
								ArrayList<Integer> queenCurrent, 
								ArrayList<Integer> queenMoved, 
								ArrayList<Integer> arrow) {
		// create clone of board to use for testing purposes
		Board temp = new Board(this);
		
		// check if queenCurrent holds your queen
		if (temp.getTile(queenCurrent)!=team) {
			// invalid move
			System.out.println("No queen to move");
			return false;
		}
		
		// check if currentQueen can move to queenMoved
		ArrayList<ArrayList<Integer>> options = temp.getMovementOptions(queenCurrent);
		boolean valid = false;
		for (ArrayList<Integer> option : options) {
			if ( option.get(0)==queenMoved.get(0) && option.get(1)==queenMoved.get(1) ) {
				// valid move
				valid = true;
				break;
			}
		}
		if (valid==false) {
			// move not contained in valid moves list
			System.out.println("Queen can't move there");
			return false;
		}
		
		// move queen on temp board
		temp.setTile(Board.EMPTY, queenCurrent);
		temp.setTile(team, queenMoved);
		
		// check if arrow is reachable from queenMoved
		ArrayList<ArrayList<Integer>> arrows = temp.getMovementOptions(queenMoved);
		valid = false;
		for (ArrayList<Integer> option : arrows) {
			if ( option.get(0)==arrow.get(0) && option.get(1)==arrow.get(1) ) {
				// valid move
				valid = true;
				break;
			}
		}
		if (valid==false) {
			// move not contained in valid moves list
			System.out.println("Arrow can't be thrown there");
			return false;
		}
		
		// nothing ticked false -> valid move
		return true;

	}
	
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
