package ubc.cosc322;

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
	
	// methods
	public void setTile(int newOccupent, int x, int y) {
		// check for invalid tile
		if (x<1 || x>11 || y<1 || y>11) {
			// System.out.println("Can't set this tile");
			return;
		}
		this.tiles[x][y] = newOccupent;
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
	
	public Board clone() {
		return this.clone();
	}
	
	public boolean checkWin() {
		// dont know how to check this yet
		return false;
	}
	
	public int[][] getQueens(int team) {
		int[][] queens = new int[4][2];
		int queensIdx = 0;
		for (int i=1; i<11; i++) {
			for (int j=1; j<11; j++) {
				if (this.tiles[i][j]==team) {
					queens[queensIdx][0] = i;
					queens[queensIdx++][1] = j;
				}
				// get out of loop early
				if (queensIdx>=4) { break; }
			}
			// get out of loop early
			if (queensIdx>=4) { break; }
		}
		return queens;
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
