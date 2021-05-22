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
	private int[][] tiles; // tiles[xPos][yPos], 0 row&col are set to 0(EMPTY) but never used
	
	// constructors
	public Board() {
		this.tiles = new int[11][11];
		for (int j=0; j<11; j++) {
			for (int i=0; i<11; i++) {
				this.tiles[i][j] = Board.EMPTY;
			}
		}
	}
	// create board with state of passed board
	public Board(Board board) {
		this();
		this.clone(board);
	}
	
	// methods
	public void setTile(int newOccupent, int x, int y) {
		// check for invalid tile
		if (x<1 || x>11 || y<1 || y>11) {
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
		if (x<1 || x>11 || y<1 || y>11) {
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
				if (this.tiles[i][j]==team) {
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
		
		// loop from position along straight lines with direction dx,dy until obstacle, do so for each direction
		for (int dx=1, dy=0, i=initialPosition.get(1)+dx, j=initialPosition.get(0)+dy; true; i+=dx, j+=dy) {
			if (this.getTile(i, j)==Board.EMPTY) {
				// add empty tile position to options and continue along current line
				ArrayList<Integer> newTile = new ArrayList<Integer>();
				newTile.add(j); // add row
				newTile.add(i); // add col
				options.add(newTile); // add to list of options
				continue;
			}
			else {
				// change dx and/or dy to rotate line CCW
				if (dy==0) { 
					dy = dx; 
				}
				else if (dx==dy) { 
					dx = 0; 
				}
				else if (dx==0) { 
					dx = -dy; 
				}
				else if (dx!=dy && dx!=0 && dy!=0) {
					dy = 0;
				}
				// leave loop if changed dx,dy back to initial direction
				if (dx==1 && dy==0) {
					break;
				}
				// revert i and j back to starting values to begin new line
				i = initialPosition.get(1) + dx;
				j = initialPosition.get(0) + dy;
			}
		}
		
		// return list of options
		return options;
		
		// rough work for dx and dy:
		// dx and dy values for each direction iteration when moving CCW
		// dx: 1, 1, 0,-1,-1,-1, 0, 1
		// dy: 0, 1, 1, 1, 0,-1,-1,-1
	}
	
	public void applyMove(int team, ArrayList<ArrayList<Integer>> move) {
		// split move up into it's 3 parts
		ArrayList<Integer> queenCurrent = move.get(0);
		ArrayList<Integer> queenMoved = move.get(1);
		ArrayList<Integer> arrow = move.get(2);
		
		// check validity of move - EMPTY METHOD ASSUMES TRUE
		if ( !(this.validateMove(queenCurrent, queenMoved, arrow)) ) {
			// System.out.println("Move not valid. You lose");
			// return early without applying move
			return;
		}
		
		// make changes to board using move inputs
		this.setTile(Board.EMPTY, queenCurrent);
		this.setTile(team, queenMoved);
		this.setTile(Board.ARROW, arrow);
	}
	
	// INCOMPLETE
	/* 
	 * things to check for validity:
	 * queenCurrent holds your queen
	 * queenMoved is reachable from queenCurrent
	 * arrow is reachable from queenMoved
	 * 
	 */
	public boolean validateMove(ArrayList<Integer> queenCurrent, 
								ArrayList<Integer> queenMoved, 
								ArrayList<Integer> arrow) {
		// temporarily empty method - ADD LATER
		return true;

	}
	
	public String toString() {
		String output = "Current Board: \n";
		for (int j=11; j>=1; j--) {
			for (int i=1; i<11; i++) {
				switch (this.tiles[i][j]) {
				case Board.EMPTY: output += "   "; break;
				case Board.BLACK: output += " B "; break;
				case Board.WHITE: output += " W "; break;
				case Board.ARROW: output += " A "; break;
				}
			}
			output += "\n";
		}
		return output;
	}
}
