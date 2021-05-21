package ubc.cosc322;

public class Board {
	// constants 
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int ARROW = 3;
	
	// fields
	private int[][] tiles;
	
	// constructors
	public Board() {
		this.tiles = new int[11][11];
		for (int i=1; i<11; i++) {
			for (int j=1; j<11; j++) {
				this.tiles[i][j] = Board.EMPTY;
			}
		}
	}
	
	// methods
	public void setTile(int newOccupent, int xPos, int yPos) {
		this.tiles[xPos][yPos] = newOccupent;
	}
	
	public int getTile(int x, int y) {
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
		for (int i=1; i<11; i++) {
			for (int j=1; j<11; j++) {
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
