package ubc.cosc322;

import java.util.ArrayList;

public class Board_v2 {
	//-- CONSTANTS --//
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int ARROW = 3;
	public static final int OUTOFBOUNDS = 4;
	
//	public static final int GATEWAY = -1; // region tile value, empty with certain adjacent walls
//	public static final int WALL = -2; // region tile value , arrow or out of bounds
	
	//-- FIELDS --//
	/**
	 * 2d positional dataframe for board
	 * indices are [row][col]
	 * 0 row and 0 col are ignored
	 */
	protected int[][] tiles;
	
	int turnCount;
	
//	protected int[][] regionTiles; // holds identifier of region this tile belongs to, -1 for arrows
//	ArrayList<Region> regions;
	
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
		
		// initialize turn count
		this.turnCount = 0;
		
//		// create lists for region identifiers and regions themselves
//		this.regionTiles = new int[11][11];
//		this.regions = new ArrayList<Region>();
//		// get positions of all tiles and put in list
//		ArrayList<ArrayList<Integer>> positions = new ArrayList<ArrayList<Integer>>();
//		for (int row=1; row<11; row++) {
//			for (int col=1; col<11; col++) {
//				ArrayList<Integer> position = new ArrayList<Integer>();
//				position.add(row);
//				position.add(col);
//				positions.add(position);
//			}
//		}
//		// make new region in regions list with passed tiles
//		this.addRegion(positions);
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
//		this.regionTiles = new int[11][11];
//		this.regions = new ArrayList<Region>();
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
//				this.setTileRegion(original.getTileRegion(row,col), row, col);
			}
		}
//		// copy regions
//		this.regions = original.regions;
		// copy turn count
		this.turnCount = original.turnCount;
		
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
//		// update regions with action
//		// System.out.println("Beginning region changes");
//		this.investigateArrow(arrow);
//		this.updateRegions();
		// increment turn counter
		this.turnCount++;
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
		boolean showingRegions = false; // change to true if wanting to show region ids
		// check if not needing double digits
		String output = "\nState of Board: \n";
		if (showingRegions==false) {
			output += "  -   -   -   -   -   -   -   -   -   -  \n";
			for (int row=10; row>=1; row--) {
				output += "|";
				for (int col=1; col<11; col++) {
					switch (this.tiles[row][col]) {
					case Board_v2.EMPTY: output += "   "; break;
					case Board_v2.BLACK: output += " B "; break;
					case Board_v2.WHITE: output += " W "; break;
					case Board_v2.ARROW: output += " * "; break;
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
//		else if (this.regions.size()<10) {
//			output += "  -   -   -   -   -   -   -   -   -   -  \n";
//			for (int row=10; row>=1; row--) {
//				output += "|";
//				for (int col=1; col<11; col++) {
//					switch (this.tiles[row][col]) {
//					case Board_v2.EMPTY: 
//						switch (this.regionTiles[row][col]) {
//						case Board_v2.GATEWAY: 
//							output += " ' "; 
//							break;
//						default: 
//							output += " " + this.regionTiles[row][col] + " "; 
//						}
//						break;
//					case Board_v2.BLACK: output += " B "; break;
//					case Board_v2.WHITE: output += " W "; break;
//					case Board_v2.ARROW: output += " * "; break;
//					}
//					output += "|";
//				}
//				// output += " " + row + "\n";
//				output += "\n";
//				output += "  -   -   -   -   -   -   -   -   -   -  \n";
//			}
//			// output += "  1   2   3   4   5   6   7   8   9   10 \n";
//			return output;
//		}
//		else {
//			output += "  --   --   --   --   --   --   --   --   --   --  \n";
//			for (int row=10; row>=1; row--) {
//				output += "|";
//				for (int col=1; col<11; col++) {
//					switch (this.tiles[row][col]) {
//					case Board_v2.EMPTY: 
//						switch (this.regionTiles[row][col]) {
//						case Board_v2.GATEWAY: 
//							output += " '  "; 
//							break;
//						default: 
//							output += " " + this.regionTiles[row][col]; 
//							// add trailing spaces depending on size of tile region id
//							output += (this.regionTiles[row][col]<10)? ("  "):(" ");
//						}
//						break;
//					case Board_v2.BLACK: output += " B  "; break;
//					case Board_v2.WHITE: output += " W  "; break;
//					case Board_v2.ARROW: output += " *  "; break;
//					}
//					output += "|";
//				}
//				// output += " " + row + "\n";
//				output += "\n";
//				output += "  --   --   --   --   --   --   --   --   --   --  \n";
//			}
//			// output += "  1   2   3   4   5   6   7   8   9   10 \n";
//			return output;
//		}
		return output;
	}
	
	
//	//-- REGION METHODS ATTEMPT 2 --//
//	/**
//	 * Get Region passed tile belongs to, otherwise gate or wall
//	 * @param row
//	 * @param col
//	 * @return id of tile's region
//	 */
//	public int getTileRegion(int row, int col) {
//		// check if out of bounds
//		if (this.getTile(row,col)==Board_v2.OUTOFBOUNDS) {
//			return Board_v2.WALL;
//		}
//		// check if arrow
//		if (this.getTile(row,col)==Board_v2.ARROW) {
//			return Board_v2.WALL;
//		}
//		// check if gate
//		if (this.tiles[row][col]==Board_v2.GATEWAY) {
//			return Board_v2.GATEWAY;
//		}
//		// else return region id
//		return this.regionTiles[row][col];
//	}
//	public int getTileRegion(ArrayList<Integer> position) {
//		return this.getTileRegion(position.get(0), position.get(1));
//	}
//	
//	/**
//	 * Set region of passed tile to passed regionId(id/gate/wall)
//	 * @param row
//	 * @param col
//	 */
//	public void setTileRegion(int regionId, int row, int col) {
//		// check if out of bounds
//		if (this.getTile(row,col)==Board_v2.OUTOFBOUNDS) {
//			// cant set out of bounds tile
//			return;
//		}
//		// check if arrow
//		if (this.getTile(row,col)==Board_v2.ARROW) {
//			// can only set arrow to wall
//			this.regionTiles[row][col] = Board_v2.WALL;
//			return;
//		}
//		// else set to regionId
//		this.regionTiles[row][col] = regionId;
//	}
//	public void setTileRegion(int regionId, ArrayList<Integer> position) {
//		this.setTileRegion(regionId, position.get(0), position.get(1));
//	}
//	
//	/**
//	 * Get regions(s) of passed tile
//	 * @param position
//	 * @return tile's region if region tile, touching regions if gateway, empty list otherwise
//	 */
//	public ArrayList<Region> getRegion(ArrayList<Integer> position) {
//		// make list to hold regions
//		ArrayList<Region> regions = new ArrayList<Region>();
//		// check if out of bounds
//		if (this.getTile(position)==Board_v2.OUTOFBOUNDS) {
//			// no regions for out of bounds tile
//			return regions;
//		}
//		// check if arrow
//		if (this.getTile(position)==Board_v2.ARROW) {
//			// no regions for arrows
//			return regions;
//		}
//		// check if gate
//		if (this.getTile(position)==Board_v2.GATEWAY) {
//			// get gate sides
//			ArrayList<ArrayList<Integer>> sides = this.getGateSides(position);
//			// loop through sides
//			for (ArrayList<Integer> side : sides) {
//				// get region of side
//				Region newRegion = this.getRegion(side).get(0);
//				// check if region has already been added to list
//				boolean added = false;
//				for (Region region : regions) {
//					if (region.id==newRegion.id) {
//						// already added
//						added = true;
//						break;
//					}
//				}
//				if (added==false) {
//					// not found in list, add now
//					regions.add(newRegion);
//				}
//			}
//			// return list of regions
//			return regions;
//		}
//		// else is region tile
//		// get id from tile
//		int regionId = this.getTileRegion(position);
//		// loop through regions for matching id
//		for (Region region : this.regions) {
//			if (region.id==regionId) {
//				// found matching region
//				regions.add(region);
//				// return list of 1 region
//				return regions;
//			}
//		}
//		// else tile has unidentified region -> return empty list
//		return regions; 
//	}
//	public ArrayList<Region> getRegion(int row, int col) {
//		ArrayList<Integer> position = new ArrayList<Integer>();
//		position.add(row);
//		position.add(col);
//		return this.getRegion(position);
//	}
//	
//	/**
//	 * Create region out of passed tiles and add to board's list
//	 * @param tiles
//	 */
//	public void addRegion(ArrayList<ArrayList<Integer>> tiles) {
//		this.regions.add( new Region(new Board_v2(this), tiles));
//	}
//	
//	/**
//	 * Update tile region data due to new arrow on board
//	 * not completed yet
//	 * @param arrow
//	 */
//	public void investigateArrow(ArrayList<Integer> arrow) {
//		// check if arrow position was gate
//		if (this.getTileRegion(arrow)==Board_v2.GATEWAY) {
//			// update board region value to wall
//			this.setTileRegion(Board_v2.WALL, arrow);
//			// get adjacent tiles
//			ArrayList<ArrayList<Integer>> adjacentTiles= this.getAdjacentTiles(arrow);
//			// loop through adjacent tiles
//			for (ArrayList<Integer> adjacentTile : adjacentTiles) {
//				// check if is gate but shouldnt be
//				if (this.isGate(adjacentTile)==false && this.getTileRegion(arrow)==Board_v2.GATEWAY) {
//					// get gate's adjacent tiles
//					ArrayList<ArrayList<Integer>> gateAdjacents = this.getAdjacentTiles(adjacentTile);
//					// loop through gate adjacents
//					boolean allGates = true;
//					for (ArrayList<Integer> gateAdjacent : gateAdjacents) {
//						if (this.isRegionTile(gateAdjacent)) {
//							allGates = false;
//							// take region id from this tile for gate
//							this.setTileRegion(this.getTileRegion(gateAdjacent), adjacentTile);
//							break;
//						}
//					}
//					// check if all gates
//					if (allGates) {
//						// make new region 1 tile big out of what was gate
//						ArrayList<ArrayList<Integer>> tiles = new ArrayList<ArrayList<Integer>>();
//						tiles.add(adjacentTile);
//						this.addRegion(tiles);
//					}
//				}
//			}
//		}
//		// else is region tile
//		else {
//			// update board region value to wall
//			this.setTileRegion(Board_v2.WALL, arrow);
//			// get adjacent tiles
//			ArrayList<ArrayList<Integer>> adjacentTiles= this.getAdjacentTiles(arrow);
//			// loop through adjacent tiles
//			for (ArrayList<Integer> adjacentTile : adjacentTiles) {
//				// check if gate
//				if (this.isGate(adjacentTile)) {
//					// set to gate on board
//					this.setTileRegion(Board_v2.GATEWAY, adjacentTile);
//					// get sides of gate
//					ArrayList<ArrayList<Integer>> sides = this.getGateSides(adjacentTile);
//					// loop through sides
//					for (ArrayList<Integer> side1 : sides) {
//						// loop through sides to compare
//						for (ArrayList<Integer> side2 : sides) {
//							// check if same side
//							if (side1.equals(side2)) {
//								// skip
//								continue;
//							}
//							// check if sides are same region
//							if (this.getTileRegion(side1)==this.getTileRegion(side2)) {
//								// partition into new region if not connected
//								this.partition(side1, side2);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Determine if passed tile qualifies as a gate on the board
//	 * @param position
//	 * @return true if should be gate
//	 */
//	public boolean isGate(ArrayList<Integer> position) {
//		// check if passed position is out of bounds
//		if (this.getTile(position)==Board_v2.OUTOFBOUNDS) {
//			return false;
//		}
//		// check if passed position is arrow
//		if (this.getTile(position)==Board_v2.ARROW) {
//			return false;
//		}
//		// initialize boolean array to hold whether adjacent is wall or not
//		boolean[] walls = new boolean[8];
//		// get list of adjacent tiles
//		ArrayList<ArrayList<Integer>> adjacentTiles = this.getAdjacentTiles(position);
//		// loop through adjacent tiles
//		for (int i=0; i<adjacentTiles.size(); i++) {
//			// check if adjacent tile is a wall
//			if (this.getTileRegion(adjacentTiles.get(i))==Board_v2.WALL) {
//				// mark adjacent position as wall
//				walls[i] = true;
//			}
//		}
//		// check if position with adjacent walls matches form of gate
//		// 2 walls in correct position, as well as at least 1 non wall tile on both sides
//		
//		// check if horizantal gate
//		if (walls[0] && walls[4] && (walls[1]==false || walls[2]==false || walls[3]==false) && (walls[5]==false || walls[6]==false || walls[7]==false)) {
//			// up down gate
//			return true;
//		}
//		// check if vertical gate
//		else if (walls[2] && walls[6] && (walls[3]==false || walls[4]==false || walls[5]==false) && (walls[7]==false || walls[0]==false || walls[1]==false)) {
//			// left right gate
//			return true;
//		}
//		// check if top right gate
//		else if (walls[0] && walls[2] && (walls[1]==false) && (walls[3]==false || walls[4]==false || walls[5]==false || walls[6]==false || walls[7]==false)) {
//			// up,right gate
//			return true;
//		}
//		// check if top left gate
//		else if (walls[2] && walls[4] && (walls[3]==false) && (walls[5]==false || walls[6]==false || walls[7]==false || walls[0]==false || walls[1]==false)) {
//			// up,left gate
//			return true;
//		}
//		// check if bottom left gate
//		else if (walls[4] && walls[6] && (walls[5]==false) && (walls[7]==false || walls[0]==false || walls[1]==false || walls[2]==false || walls[3]==false)) {
//			// down,left gate
//			return true;
//		}
//		// check if bottom right gate
//		else if (walls[6] && walls[0] && (walls[7]==false) && (walls[1]==false || walls[2]==false || walls[3]==false || walls[4]==false || walls[5]==false)) {
//			// down right gate
//			return true;
//		}
//		// no gate forms flagged ie not gate
//		else {
//			return false;
//		}
//	}
//	
//	/**
//	 * Determine if passed tile belongs to region (ie not gate or wall)
//	 * @param position
//	 * @return
//	 */
//	public boolean isRegionTile(ArrayList<Integer> position) {
//		if (this.getTileRegion(position)!=Board_v2.WALL && this.getTileRegion(position)!=Board_v2.GATEWAY) {
//			return true;
//		}
//		else {
//			return false;
//		}
//	}
//	
//	/**
//	 * Recurse through adjacent gates to get list of all adjacent region tiles
//	 * will work differently than original
//	 * @param gate
//	 * @return
//	 */
//	public ArrayList<ArrayList<Integer>> getGateSides(ArrayList<Integer> position) {
//		// make list to return
//		ArrayList<ArrayList<Integer>> sides = new ArrayList<ArrayList<Integer>>();
//		// get connected gates
//		ArrayList<ArrayList<Integer>> gates = this.getConnectedGates(position,new boolean[11][11]);
//		// loop through and get each touching region
//		for (ArrayList<Integer> gate : gates) {
//			// get adjacent tiles to gate
//			ArrayList<ArrayList<Integer>> adjacentTiles = this.getAdjacentTiles(gate);
//			// loop through adjacent tiles
//			for (ArrayList<Integer> adjacentTile : adjacentTiles) {
//				// check if adjacent tile is region tile
//				if (this.isRegionTile(adjacentTile)) {
//					// check if position has already been added to list
//					boolean added = false;
//					for (ArrayList<Integer> side : sides) {
//						// check if adjacentTile matches side in list
//						if (side.equals(adjacentTile)) {
//							// position already listed, dont add again
//							added = true;
//						}
//					}
//					if (added==false) {
//						// not found in list, add now
//						sides.add(adjacentTile);
//					}
//				}
//			}
//		}
//		// return filled list
//		return sides; 
//	}
//	
//	/**
//	 * Recurse through adjacent region tiles from side1 to side2 and make new region if disconnected
//	 * @param side1
//	 * @param side2
//	 */
//	public void partition(ArrayList<Integer> side1, ArrayList<Integer> side2) {
//		// check if both sides part of same region
//		if (this.getTileRegion(side1)!=this.getTileRegion(side2)) {
//			// dont do anything
//			return;
//		}
//		// get all connected region tiles of side1
//		ArrayList<ArrayList<Integer>> region1Tiles = this.getConnectedRegionTiles(side1,new boolean[11][11]);
//		// create boolean to hold if found
//		boolean foundSide2 = false;
//		// loop through connected tiles
//		for (ArrayList<Integer> region1Tile : region1Tiles) {
//			// check if side2 found in connected tiles
//			if (region1Tile.equals(side2)) {
//				// side 2 found
//				foundSide2 = true;
//				break;
//			}
//		}
//		// check if side2 was not found
//		if (foundSide2==false) {
//			// create new region out of found tiles
//			this.addRegion(region1Tiles);
//		}
//	}
//	
//	/**
//	 * Recurse through adjacent region tiles, 
//	 * not completed
//	 * @param position
//	 * @param checked 11x11 array holding which positions on board have already been recursed into
//	 * @return list of region tiles found
//	 */
//	public ArrayList<ArrayList<Integer>> getConnectedRegionTiles(ArrayList<Integer> position, boolean[][] checked) {
//		// mark current tile as checked
//		checked[position.get(0)][position.get(1)] = true;
//		// create list to hold connected tiles
//		ArrayList<ArrayList<Integer>> connectedTiles = new ArrayList<ArrayList<Integer>>();
//		// check if not region tile
//		if (this.isRegionTile(position)==false) {
//			// stop recursing and dont add current tile to list
//			return connectedTiles;
//		}
//		// else is region tile
//		connectedTiles.add(position);
//		// get adjacent tiles
//		ArrayList<ArrayList<Integer>> adjacentTiles = this.getAdjacentTiles(position);
//		// loop through adjacent tiles
//		for (ArrayList<Integer> adjacentTile : adjacentTiles) {
//			// check if tile in bounds
//			if (this.getTile(adjacentTile)==Board_v2.OUTOFBOUNDS) {
//				// skip cause not a real tile on board
//				continue;
//			}
//			// check if adjacent tile is already checked
//			if (checked[adjacentTile.get(0)][adjacentTile.get(1)]) {
//				// dont recurse into adjacent tile that has already been done
//				continue;
//			}
//			// recurse into adjacent tile adding all its found tiles to list
//			connectedTiles.addAll(this.getConnectedRegionTiles(adjacentTile,checked));
//		}
//		// return filled list
//		return connectedTiles;
//	}
//	
//	/**
//	 * Recurse through adjacent gate tiles 
//	 * @param position
//	 * @param checked
//	 * @return list of gate tiles found
//	 */
//	public ArrayList<ArrayList<Integer>> getConnectedGates(ArrayList<Integer> position, boolean[][] checked) {
//		// mark current tile as checked
//		checked[position.get(0)][position.get(1)] = true;
//		// create list to hold connected tiles
//		ArrayList<ArrayList<Integer>> connectedTiles = new ArrayList<ArrayList<Integer>>();
//		// check if not region tile
//		if (this.isGate(position)==false) {
//			// stop recursing and dont add current tile to list
//			return connectedTiles;
//		}
//		// else is region tile
//		connectedTiles.add(position);
//		// get adjacent tiles
//		ArrayList<ArrayList<Integer>> adjacentTiles = this.getAdjacentTiles(position);
//		// loop through adjacent tiles
//		for (ArrayList<Integer> adjacentTile : adjacentTiles) {
//			// check if tile in bounds
//			if (this.getTile(adjacentTile)==Board_v2.OUTOFBOUNDS) {
//				// skip cause not a real tile on board
//				continue;
//			}
//			// check if adjacent tile is already checked
//			if (checked[adjacentTile.get(0)][adjacentTile.get(1)]) {
//				// dont recurse into adjacent tile that has already been done
//				continue;
//			}
//			// recurse into adjacent tile adding all its found tiles to list
//			connectedTiles.addAll(this.getConnectedGates(adjacentTile,checked));
//		}
//		// return filled list
//		return connectedTiles;
//	}
//	
//	/**
//	 * Loop through each region in board's list and run the update function
//	 */
//	public void updateRegions() {
//		for (int i=0; i<regions.size(); i++) {
//			regions.get(i).update(this);
//			if (regions.get(i).getSize()<=0) {
//				regions.remove(i--);
//			}
//		}
//	}
//	
//	//-- REGION SUPPORT METHODS, not region specific but used in region methods --//
//	/**
//	 * Get list of 8 adjacent tiles to passed tile
//	 * include all tiles, including out of bounds
//	 * @param position
//	 * @return list of 8 positions surrounding passed position
//	 */
//	public ArrayList<ArrayList<Integer>> getAdjacentTiles(ArrayList<Integer> position) {
//		// create list to hold adjacent tiles
//		ArrayList<ArrayList<Integer>> adjacentTiles = new ArrayList<ArrayList<Integer>>();
//		// loop through 8 adjacent positions
//		for (int i=0; i<8; i++) {
//			// initialize position differences
//			int dx = 0, dy = 0;
//			// select values for dx and dy to access the different tiles
//			switch (i) {
//			case 0: dy=0; dx=1; break;
//			case 1: dy=1; dx=1; break;
//			case 2: dy=1; dx=0; break;
//			case 3: dy=1; dx=-1; break;
//			case 4: dy=0; dx=-1; break;
//			case 5: dy=-1; dx=-1; break;
//			case 6: dy=-1; dx=0; break;
//			case 7: dy=-1; dx=1; break;
//			default: dy=0; dx=0; 
//			}
//			// add posiiton to list
//			ArrayList<Integer> adjacentTile = new ArrayList<Integer>();
//			adjacentTile.add(position.get(0)+dy);
//			adjacentTile.add(position.get(1)+dx);
//			adjacentTiles.add(adjacentTile);
//		}
//		// return filled list of 8 adjacent tiles
//		return adjacentTiles; 
//	}

	
}
	
