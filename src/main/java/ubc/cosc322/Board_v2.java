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
	
	int turnCount;
	
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
		
		// initialize turn count
		this.turnCount = 0;
		
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
		// make new region in regions list with passed tiles
		this.makeNewRegion(positions);
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
		// update regions with action
		// System.out.println("Beginning region changes");
		this.investigateArrow(arrow);
		this.updateRegions();
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
		else if (this.regions.size()<10) {
			output += "  -   -   -   -   -   -   -   -   -   -  \n";
			for (int row=10; row>=1; row--) {
				output += "|";
				for (int col=1; col<11; col++) {
					switch (this.tiles[row][col]) {
					case Board_v2.EMPTY: 
						switch (this.regionTiles[row][col]) {
						case Board_v2.GATEWAY: 
							output += " ' "; 
							break;
						default: 
							output += " " + this.regionTiles[row][col] + " "; 
						}
						break;
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
		else {
			output += "  --   --   --   --   --   --   --   --   --   --  \n";
			for (int row=10; row>=1; row--) {
				output += "|";
				for (int col=1; col<11; col++) {
					switch (this.tiles[row][col]) {
					case Board_v2.EMPTY: 
						switch (this.regionTiles[row][col]) {
						case Board_v2.GATEWAY: 
							output += " '  "; 
							break;
						default: 
							output += " " + this.regionTiles[row][col]; 
							// add trailing spaces depending on size of tile region id
							output += (this.regionTiles[row][col]<10)? ("  "):(" ");
						}
						break;
					case Board_v2.BLACK: output += " B  "; break;
					case Board_v2.WHITE: output += " W  "; break;
					case Board_v2.ARROW: output += " *  "; break;
					}
					output += "|";
				}
				// output += " " + row + "\n";
				output += "\n";
				output += "  --   --   --   --   --   --   --   --   --   --  \n";
			}
			// output += "  1   2   3   4   5   6   7   8   9   10 \n";
			return output;
		}

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
		if (this.getTile(row,col)==Board_v2.OUTOFBOUNDS) {
			// return wall value, tile isn't tied to any region
			return Board_v2.WALL;
		}
		// return region id
		return this.regionTiles[row][col];
	}
	public int getRegionTile(ArrayList<Integer> position) {
		return this.getRegionTile(position.get(0),position.get(1));
	}
	/**
	 * Set passed tile position to regionId if not out of bounds or arrow
	 * @param regionId
	 * @param row
	 * @param col
	 */
	public void setRegionTile(int regionId, int row, int col) {
		// check if out of bounds 
		if (this.getTile(row,col)==Board_v2.OUTOFBOUNDS) {
			// do nothing
			return;
		}
		// check if arrow
		if (this.getTile(row,col)==Board_v2.ARROW) {
			// set to wall value
			this.regionTiles[row][col] = Board_v2.WALL;
		}
		// set tile to region id
		this.regionTiles[row][col] = regionId;
	}
	public void setRegionTile(int regionId, ArrayList<Integer> position) {
		this.setRegionTile(regionId, position.get(0),position.get(1));
	}
	
	/**
	 * get region passed position is sitting in
	 * @param position
	 * @return
	 */
	public Region getRegion(ArrayList<Integer> position) {
		int id = this.getRegionTile(position);
		for (Region region : regions) {
			if (region.id==id) {
				return region;
			}
		}
		// if never found region, output error
		System.out.println("Never found requested region. Outputting null");
		return null;
	}
	
	/**
	 * return adjacent regions to passed gate tile
	 * @param position
	 * @return
	 */
	public ArrayList<Region> getAdjacentRegions(ArrayList<Integer> position) {
		// get sides of gate
		ArrayList<ArrayList<Integer>> sides = this.getSidesOfGate(position, new boolean[11][11]);
		// make list of regions
		ArrayList<Region> regions = new ArrayList<Region>();
		// loop through sides adding new regions to list
		for (ArrayList<Integer> side : sides) {
			int sideId = this.getRegionTile(side);
			// loop through regions list to see if it matches
			boolean inList = false;
			for (Region region : regions) {
				// check if same region as already in list
				if (region.id==sideId) {
					inList = true;
					break;
				}
			}
			// check if wasnt in list
			if (inList==false) {
				// add to list
				regions.add(this.getRegion(side));
			}
		}
		// return list of regions
		return regions;
	}

	/**
	 * Get list of regions in this board state
	 * @return list of regions
	 */
	public ArrayList<Region> getRegionsList() {
		return (ArrayList<Region>) this.regions.clone();
	}
	
	/**
	 * Make new region with passed tiles and add to regions list within board
	 * @param tiles
	 */
	public void makeNewRegion(ArrayList<ArrayList<Integer>> tiles) {
		this.regions.add( new Region(this, tiles) );
	}

	
	/**
	 * NOT FULLY COMPLETED
	 * need to add/remove region tiles to/from regions
	 * 
	 * check if arrow replaces gateway or region tile
	 * for replaces gateway, check adjacent tiles for if any are no longer gateways and add to region they not belong to
	 * for replaces region tile, check if any new gates need to be made and if these gates make new regions
	 * 
	 * Investigate how new arrow changes board regions
	 * add wall, add/remove gateways, add/remove adjacent region connections, partition region
	 * 
	 * @param arrow position of arrow thrown by action sent to applyAction() 
	 */
	public void investigateArrow(ArrayList<Integer> arrow) {
		// System.out.println("Arrow tile was " + this.getRegionTile(arrow.get(0), arrow.get(1)));
		// check if arrow position was gateway 
		if (this.getRegionTile(arrow.get(0), arrow.get(1))==Board_v2.GATEWAY) {
			// System.out.println("Arrow was a gateway");
			// update arrow position to wall on region board
			this.setRegionTile(Board_v2.WALL, arrow.get(0), arrow.get(1));
			// check if adjacent gateways are no longer gateways
			ArrayList<ArrayList<Integer>> adjacentTiles = this.getAdjacentTiles(arrow);
			for (ArrayList<Integer> tile : adjacentTiles) {
				// check if should not be gate but is
				if (this.isGate(tile)==false && this.getRegionTile(tile.get(0),tile.get(1))==Board_v2.GATEWAY) {
					// System.out.println("Adjacent gate is no longer a gate");
					// get adjacent tiles
					ArrayList<ArrayList<Integer>> adjTiles = this.getAdjacentTiles(tile);
					boolean allGates = true;
					for (ArrayList<Integer> t : adjTiles) {
						// check if region tile
						if (this.getRegionTile(t.get(0),t.get(1))!=Board_v2.GATEWAY && 
								this.getRegionTile(t.get(0),t.get(1))!=Board_v2.WALL) {
							// System.out.println("Found region to take value of");
							// should only be one type of region so can take first one 
							// set what was gate to found region
							this.setRegionTile(this.getRegionTile(t.get(0), t.get(1)), tile.get(0), tile.get(1));
							// set allGates to false so as to not create new region
							allGates = false;
							break;
						}
					}
					// check if allGates was never flipped ie no adjacent region ie needs new region
					if (allGates==true) {
						// System.out.println("Did not find region to take value of");
						// make list of just the one tile that used to be a gate
						ArrayList<ArrayList<Integer>> loneTile = new ArrayList<ArrayList<Integer>>();
						loneTile.add(tile);
						// make new region with list of 1 tile
						this.makeNewRegion(loneTile);
					}
					
				}
			}
		}
		// check if arrow position was region tile -> make new gateways
		else if (this.getRegionTile(arrow.get(0), arrow.get(1))!=Board_v2.WALL) {
			// System.out.println("Arrow was a region tile");
			// update arrow position to wall on region board
			this.setRegionTile(Board_v2.WALL, arrow.get(0), arrow.get(1));
			// check if adjacent tiles are now gates
			ArrayList<ArrayList<Integer>> adjacentTiles = this.getAdjacentTiles(arrow);
			for (ArrayList<Integer> tile : adjacentTiles) {
				// check if tile should be gateway
				if (this.isGate(tile)) {
					// set tile state to gateway
					this.setRegionTile(Board_v2.GATEWAY, tile.get(0), tile.get(1));
					// get sides to gate
					ArrayList<ArrayList<Integer>> sides = this.getSidesOfGate(tile, new boolean[11][11]);
					// check if any 2 sides are same region
					for (int i=0; i<sides.size(); i++) {
						for (int j=0; j<sides.size(); j++) {
							// ignore checking side with itself
							if(i==j) {
								continue;
							}
							// check if 2 separate sides have same region -> check for partition
							if (this.getRegionTile(sides.get(i).get(0), sides.get(i).get(1))==this.getRegionTile(sides.get(j).get(0), sides.get(j).get(1))) {
								// check if 2 sides are now disconnected and make into new region if they are
								this.partition(sides.get(i), sides.get(j));
							}
							// dont need to do anything for if sides have different regions -> updated later
						}
					}
				}
			}
		}
	}
	
	/**
	 * Get list of all adjacent positions except for out of bounds 
	 * after calling function, loop through list taking tiles that are wanted
	 * @param position
	 * @return list of up to 8 tiles on board adjacent to passed position
	 */
	public ArrayList<ArrayList<Integer>> getAdjacentTiles(ArrayList<Integer> position) {
		// create list to hold adjacent tiles
		ArrayList<ArrayList<Integer>> adjacents = new ArrayList<ArrayList<Integer>>();
		// loop through all 8 adjacent tiles
		for (int i=0; i<8; i++) {
			// get difference in position to current adjacent tile
			int dx,dy;
			switch (i) {
			case 0: dy=0; dx=1; break;
			case 1: dy=1; dx=1; break;
			case 2: dy=1; dx=0; break;
			case 3: dy=1; dx=-1; break;
			case 4: dy=0; dx=-1; break;
			case 5: dy=-1; dx=-1; break;
			case 6: dy=-1; dx=0; break;
			case 7: dy=-1; dx=1; break;
			default: dy=0; dx=0; 
			}
			if (this.getTile(position)==Board_v2.OUTOFBOUNDS) {
				// skip
				continue;
			}
			// add tile to adjacents
			ArrayList<Integer> tile = new ArrayList<Integer>();
			tile.add(position.get(0)+dy);
			tile.add(position.get(1)+dx);
			adjacents.add(tile);
		}
		// return list of 8 tiles
		return adjacents;
	}
	
	/**
	 * Determine if passed tile position qualifies as a gateway
	 * use switch statement with all the different possible gateways and see if it matches
	 * @param position
	 * @return true if passed position is a gateway
	 */
	public boolean isGate(ArrayList<Integer> position) {
		// check if position is not region tile ie cant be made into gate
		if (this.getTile(position)==Board_v2.OUTOFBOUNDS || this.getRegionTile(position.get(0), position.get(1))==Board_v2.WALL) {
			// cant make into gate
			return false;
		}
		// look at adjacent tiles and save which ones are walls
		ArrayList<ArrayList<Integer>> adjacents = this.getAdjacentTiles(position);
		// create array to hold adjacent walls
		boolean[] walls = new boolean[8];
		for (int i=0; i<adjacents.size(); i++) {
			// check if tile is wall
			if (this.getRegionTile(adjacents.get(i).get(0),adjacents.get(i).get(1))==Board_v2.WALL) {
				walls[i] = true;
			}
		}
		// switch through the different gateway types and return true once it matches one
		// checks if walls are in right spot, and sides of gate have at least one opening
		if (walls[0] && walls[4] && (walls[1]==false || walls[2]==false || walls[3]==false) && (walls[5]==false || walls[6]==false || walls[7]==false)) {
			// up down gate
			return true;
		}
		else if (walls[2] && walls[6] && (walls[3]==false || walls[4]==false || walls[5]==false) && (walls[7]==false || walls[0]==false || walls[1]==false)) {
			// left right gate
			return true;
		}
		else if (walls[0] && walls[2] && (walls[1]==false) && (walls[3]==false || walls[4]==false || walls[5]==false || walls[6]==false || walls[7]==false)) {
			// up,right gate
			return true;
		}
		else if (walls[2] && walls[4] && (walls[3]==false) && (walls[5]==false || walls[6]==false || walls[7]==false || walls[0]==false || walls[1]==false)) {
			// up,left gate
			return true;
		}
		else if (walls[4] && walls[6] && (walls[5]==false) && (walls[7]==false || walls[0]==false || walls[1]==false || walls[2]==false || walls[3]==false)) {
			// down,left gate
			return true;
		}
		else if (walls[6] && walls[0] && (walls[7]==false) && (walls[1]==false || walls[2]==false || walls[3]==false || walls[4]==false || walls[5]==false)) {
			// down right gate
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * get position of a tile on each side of gate
	 * the position is whatever first position is found in a set
	 * recurse through gates to get to sides
	 * loop through gate adjacent tiles (not walls)
	 * shouldn't have duplicates b/c of checked array
	 * @param gate current gate being checked
	 * @return list of positions disconnected from each other
	 */
	public ArrayList<ArrayList<Integer>> getSidesOfGate(ArrayList<Integer> gate, boolean[][] checked) {
		// mark gate as checked
		checked[gate.get(0)][gate.get(1)] = true;
		// make list to hold sides
		ArrayList<ArrayList<Integer>> sides = new ArrayList<ArrayList<Integer>>();
		// create var to hold idx of start of current side, -1 when not working on side yet
		int sideStart = -1;
		// get adjacent tiles
		ArrayList<ArrayList<Integer>> adjacents = this.getAdjacentTiles(gate);
		// loop through adjacent tiles
		for (int i=0; i<8; i++) {
			// check if outofbounds
			if (this.getTile(adjacents.get(i))==Board_v2.OUTOFBOUNDS) {
				// skip
				continue;
			}
			// check if gate
			if (this.getRegionTile(adjacents.get(i).get(0), adjacents.get(i).get(1))==Board_v2.GATEWAY) {
				// if building side, stop and send it to list
				if (sideStart>=0) {
					// add saved side position to list
					sides.add(adjacents.get(sideStart));
					// revert sideStart
					sideStart = -1;
				}
				// check if already checked
				if (checked[adjacents.get(i).get(0)][adjacents.get(i).get(1)]) {
					// skip since already looked into
					continue;
				}
				// get sides from this gate by recursing into it
				ArrayList<ArrayList<Integer>> newSides = this.getSidesOfGate(adjacents.get(i), checked);
				// add sides to list
				for (ArrayList<Integer> s : newSides) {
					sides.add(s);
				}
			}
			// check if wall tile
			else if (this.getRegionTile(adjacents.get(i).get(0), adjacents.get(i).get(1))==Board_v2.WALL) {
				// if building side, stop and send it to list
				if (sideStart>=0) {
					// add saved side position to list
					sides.add(adjacents.get(sideStart));
					// revert sideStart
					sideStart = -1;
				}
			}
			// check if region tile
			else {
				// check if already started side
				if (sideStart>=0) {
					// multi tiled side, already gave it a position
				}
				// check if not started side
				if (sideStart<0) {
					// start building side
					sideStart = i;
				}
				// check if on last loop and working on side
				if (i>=7 && sideStart>=0) {
					// check if first tile was region
					if (this.getRegionTile(adjacents.get(i).get(0), adjacents.get(i).get(1))!=Board_v2.GATEWAY &&
							this.getRegionTile(adjacents.get(i).get(0), adjacents.get(i).get(1))!=Board_v2.WALL) {
						// current side already counted in first side -> dont add
					}
					else {
						// current side not already counted in first side
						sides.add(adjacents.get(sideStart));
					}
				}
			}
		}
		// return list of positions all pertaining to different sides of gate
		return sides;
	}
	
	/**
	 * take 2 sides that have same region and see if they are connected ie should stay one region
	 * @param side1
	 * @param side2
	 */
	public void partition(ArrayList<Integer> side1, ArrayList<Integer> side2) {
		// get all connected region tiles from side 1
		ArrayList<ArrayList<Integer>> side1Tiles = this.getConnectedRegionTiles(side1, side2, new boolean[11][11]);
		// check if last element is the side2 position
		if (side1Tiles.get(side1Tiles.size()-1).equals(side2)) {
			// 2 sides are connected -> dont make new region
		}
		else {
			// 2 sides are disconnected but same region -> make new region
			this.makeNewRegion(side1Tiles);
		}
	}
	
	/**
	 * get list of connected region tiles
	 * shoudln't be duplicates because of checked array
	 * last position will either be the goal or be the last possible tile
	 * @param tile
	 * @param goal
	 * @param checked
	 * @return list of connected tiles from current
	 */
	public ArrayList<ArrayList<Integer>> getConnectedRegionTiles(ArrayList<Integer> tile, ArrayList<Integer> goal, boolean[][] checked) {
		// mark tile as checked
		checked[tile.get(0)][tile.get(1)] = true;
		// create list to store tiles
		ArrayList<ArrayList<Integer>> regionTiles = new ArrayList<ArrayList<Integer>>();
		// add current tile to list
		regionTiles.add(tile);
		// get adjacent tiles
		ArrayList<ArrayList<Integer>> adjacents = this.getAdjacentTiles(tile);
		// loop through adjacent tiles
		for (int i=0; i<8; i++) {
			// check if outofbounds
			if (this.getTile(adjacents.get(i))==Board_v2.OUTOFBOUNDS) {
				// skip
				continue;
			}
			// check if goal state found -> don't do anymore additions -> exit
			if (goal!=null && checked[goal.get(0)][goal.get(1)]) {
				return regionTiles;
			}
			// check if gate or wall
			if (this.getRegionTile(adjacents.get(i).get(0), adjacents.get(i).get(1))==Board_v2.GATEWAY ||
				this.getRegionTile(adjacents.get(i).get(0), adjacents.get(i).get(1))==Board_v2.WALL ) {
				// dont add this tile, dont recurse into this tile
				continue;
			}
			// check if region tile
			else {
				// check if goal state
				if (goal!=null && adjacents.get(i).equals(goal)) {
					// add goal state to list and checked and break out to not waste time
					regionTiles.add(goal);
					checked[goal.get(0)][goal.get(1)] = true;
					break;
				}
				// check if tile already checked
				if (checked[adjacents.get(i).get(0)][adjacents.get(i).get(1)]) {
					// this tile already done
					continue;
				}
				// get list from tile
				ArrayList<ArrayList<Integer>> foundTiles = this.getConnectedRegionTiles(adjacents.get(i), goal, checked);
				for (ArrayList<Integer> t : foundTiles) {
					regionTiles.add(t);
				}
			}
		}
		// return list of regionTiles found
		return regionTiles;
	}
	
	
	/**
	 * Used in region.updateGatewayTiles() to get all region gateways
	 * @param gate
	 * @param checked
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> getConnectedGateways(ArrayList<Integer> gate, boolean[][] checked) {
		// mark tile as checked
		checked[gate.get(0)][gate.get(1)] = true;
		// create list to store tiles
		ArrayList<ArrayList<Integer>> gateways = new ArrayList<ArrayList<Integer>>();
		// add current tile to list
		gateways.add(gate);
		// get adjacent tiles
		ArrayList<ArrayList<Integer>> adjacents = this.getAdjacentTiles(gate);
		// loop through adjacent tiles
		for (int i=0; i<8; i++) {
			// check if outofbounds
			if (this.getTile(adjacents.get(i))==Board_v2.OUTOFBOUNDS) {
				// skip
				continue;
			}
			// check if tile already checked
			if (checked[adjacents.get(i).get(0)][adjacents.get(i).get(1)]) {
				// this tile already done
				continue;
			}
			// check if gate
			if (this.getRegionTile(adjacents.get(i).get(0),adjacents.get(i).get(1))==Board_v2.GATEWAY) {
				// get list from tile
				ArrayList<ArrayList<Integer>> foundTiles = this.getConnectedGateways(adjacents.get(i),checked);
				for (ArrayList<Integer> t : foundTiles) {
					gateways.add(t);
				}
			}
		}
		// return list of gateways found
		return gateways;
	}
	
	/**
	 * Outputs version of board without queens, looking at regions
	 * arrows are walls(W) and all other spaces are distinguished by their region id 
	 * only used in testing
	 * toString method now updated to show region of tiles with no queens on them
	 * @return
	 */
	public String regionsToString() {
		String output = "\nState of Board: \n";
		output += "  -   -   -   -   -   -   -   -   -   -  \n";
		for (int row=10; row>=1; row--) {
			output += "|";
			for (int col=1; col<11; col++) {
				switch (this.regionTiles[row][col]) {
				case Board_v2.WALL: output += " W "; break;
				case Board_v2.GATEWAY: output += " G "; break;
				default: output += " " + this.regionTiles[row][col] + " "; break;
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
	
	public void updateRegions() {
		// String output = "";
		// loop through regions on board
		for (int i=0; i<regions.size(); i++) {
			// update region with current state of board
			regions.get(i).update(this);
			// check if empty region
			if (regions.get(i).regionTiles.size()<1) {
				// System.out.println("Region "+regions.get(i).id+" is empty");
				// regions.remove(i); -> shouldnt remove
			}
			// output += "Region:"+region.id+" has size "+region.size+", ";
		}
		// System.out.println(output);
	}
	
}
	
