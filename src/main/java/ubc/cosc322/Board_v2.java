package ubc.cosc322;

import java.util.ArrayList;

public class Board_v2 {
	//-- CONSTANTS --//
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int ARROW = 3;
	public static final int OUTOFBOUNDS = 4;
	
	public static final int GATEWAY = -1; // region tile value, empty with certain adjacent walls
	public static final int WALL = -2; // region tile value , arrow or out of bounds
	
	//-- FIELDS --//
	/**
	 * 2d positional dataframe for board
	 * indices are [row][col]
	 * 0 row and 0 col are ignored
	 */
	protected int[][] tiles;
	
	protected int[][] regionTiles; // holds identifier of region this tile belongs to, -1 for arrows
	ArrayList<Region> regions;
	
	//-- CONSTRUCTORS --//
	/**
	 * Create board with manual inputted queen locations
	 */
	public Board_v2() {
		// initialize tiles to empty -> loop not technically needed since Board_v2.EMPTY==0
		this.tiles = new int[11][11];
		for (int j=0; j<11; j++) {
			for (int i=0; i<11; i++) {
				this.setTile(Board_v2.EMPTY, i, j);
			}
		}
		// manually add starting positions of queens to tiles
		this.setTile(Board_v2.BLACK, 10, 4);
		this.setTile(Board_v2.BLACK, 10, 7);
		this.setTile(Board_v2.BLACK, 7, 1);
		this.setTile(Board_v2.BLACK, 7, 10);
		this.setTile(Board_v2.WHITE, 1, 4);
		this.setTile(Board_v2.WHITE, 1, 7);
		this.setTile(Board_v2.WHITE, 4, 1);
		this.setTile(Board_v2.WHITE, 4, 10);
		
		// create lists for region identifiers and regions themselves
		this.regionTiles = new int[11][11];
		this.regions = new ArrayList<Region>();
		// get positions of all tiles and put in list
		ArrayList<ArrayList<Integer>> positions = new ArrayList<ArrayList<Integer>>();
		for (int row=1; row<11; row++) {
			for (int col=1; col<11; col++) {
				ArrayList<Integer> position = new ArrayList<Integer>();
				position.add(row);
				position.add(col);
				positions.add(position);
			}
		}
		// add to regions list new region passing tiles to put in and empty list of gateways
		this.regions.add( new Region(this, positions, new ArrayList<ArrayList<Integer>>()) );
	}
	
	/**
	 * Create board from server message details in game action start
	 * NOT IMPLEMENTED but turns out not needed
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
		this.regionTiles = new int[11][11];
		this.regions = new ArrayList<Region>();
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
			return Board_v2.OUTOFBOUNDS;
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
		// copy tile values and region values
		for (int row=1; row<11; row++) {
			for (int col=1; col<11; col++) {
				this.setTile(original.getTile(row,col), row, col);
				this.setRegionTile(original.getRegionTile(row,col), row, col);
			}
		}
		// copy regions
		this.regions = original.getRegionsList();
		
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
		for (int count=0, dx=1, dy=1, row=position.get(0)+dy, col=position.get(1)+dx; count<8; row+=dy, col+=dx) {
			// check if empty tile
			if (this.getTile(row,col)==Board_v2.EMPTY) {
				// add tile to list
				ArrayList<Integer> newTile = new ArrayList<Integer>();
				newTile.add(row);
				newTile.add(col); 
				directTiles.add(newTile); 
				// continue to next tile in line
				continue;
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
		if (this.getTile(queenMoved)!=Board_v2.EMPTY) {
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
		this.setTile(Board_v2.EMPTY, queenCurrent);
		// check if arrow is empty
		if (this.getTile(arrow)!=Board_v2.EMPTY) {
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
		this.setTile(Board_v2.EMPTY, queenCurrent);
		this.setTile(player, queenMoved);
		this.setTile(Board_v2.ARROW, arrow);
		// update regions with action
		this.updateRegions(arrow);
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
		if (player==Board_v2.BLACK) {
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
				case Board_v2.EMPTY: output += "   "; break;
				case Board_v2.BLACK: output += " B "; break;
				case Board_v2.WHITE: output += " W "; break;
				case Board_v2.ARROW: output += " A "; break;
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
	
	
	
	//-- REGION METHODS --//
	
	/**
	 * Get id of region containing passed tile
	 * @param row
	 * @param col
	 * @return region id otherwise WALL value(-2)
	 */
	public int getRegionTile(int row, int col) {
		// check if tile is invalid or an arrow
		if (this.getTile(row,col)==Board_v2.OUTOFBOUNDS || this.getTile(row,col)==Board_v2.ARROW) {
			// return wall value, tile isn't tied to any region
			return Board_v2.WALL;
		}
		// return region id
		return this.regionTiles[row][col];
	}
	/**
	 * Set passed tile position to regionId if not out of bounds or arrow
	 * @param regionId
	 * @param row
	 * @param col
	 */
	public void setRegionTile(int regionId, int row, int col) {
		// check if out of bounds 
		if (this.getTile(row,col)==Board_v2.ARROW || this.getTile(row,col)==Board_v2.OUTOFBOUNDS) {
			// do nothing
			return;
		}
		// check if arrow
		if (this.getTile(row,col)==Board_v2.ARROW) {
			// set only to wall value
			this.setRegionTile(Board_v2.WALL, row, col);
		}
		// set tile to region id
		this.regionTiles[row][col] = regionId;
	}

	/**
	 * Get list of regions in this board state
	 * @return list of regions
	 */
	public ArrayList<Region> getRegionsList() {
		return (ArrayList<Region>) this.regions.clone();
	}
	
	/**
	 * Determine if passed tile position qualifies as a gateway
	 * use switch statement with all the different possible gateways and see if it matches
	 * @param position
	 * @return true if passed position is a gateway
	 */
	public boolean isGateway(int row, int col) {
		// look at adjacent tiles and save which ones are walls
		// switch through the different gateway types and return true once it matches one
		// return false as default
		return false; // temp
	}
	
	/**
	 * Update board's regions
	 * add wall, add/remove gateways, add/remove adjacent region connections, partition regions
	 * @param arrow position of arrow thrown by action sent to applyAction() 
	 */
	public void updateRegions(ArrayList<Integer> arrow) {
		// update arrow position to wall on region board
		// check adjacent tiles to throw arrow for newly created gateways
		// for each new gateway, add it to region(s) it touches (directly or through adjacent gates)
		// if both sides of gate are same region, recurse to see if partition can be made
	}
	
	
	
	
	
	
	
	
	
	/**
	 * REDOING
	 * when player places arrow, check adjacent tiles for if theyve become gateways
	 * @param arrow position to check adjacent tiles of for gateways
	 */
	public void checkForNewGateways(ArrayList<Integer> arrow) {
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
			// check adjacent tile for gateway
			if (this.isGateway(arrow.get(0)+j, arrow.get(1)+k)) {
				// change region board value
				this.setRegionTile(Board_v2.GATEWAY, arrow.get(0)+j, arrow.get(1)+k);
				// check for new region -> USE RECURSIVE TECHNIQUE TO LOOP AROUND GATE
				this.checkForRegionPartition(arrow.get(0)+j, arrow.get(1)+k);
			}
		}
	}
	/**
	 * REDOING -> WELL RENAMING AND POTENTIALLY REDESCRIBING
	 * call recursive function to loop through adjacent tiles
	 * stop early if other side of gateway is found
	 * if never found, then use found tiles to create new region
	 * @param gatewayRow
	 * @param gatewayCol
	 */
	public void checkForRegionPartition(int gatewayRow, int gatewayCol) {
		
	}
	
	
}
