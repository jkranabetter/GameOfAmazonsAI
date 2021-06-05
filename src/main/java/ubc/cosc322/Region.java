package ubc.cosc322;

import java.util.ArrayList;

public class Region {
	//-- FIELDS --//
	static int nextIdentifier = 0;
	int id; // on region version of board, tiles in this region will have this as their value
	int size; // holds number of board tiles belonging to this region, including gateway tiles
	ArrayList<ArrayList<Integer>> regionTiles; // holds position on board of tiles belonging to this region
	ArrayList<ArrayList<Integer>> gatewayTiles; // holds position on board of tiles acting as gateways in this region
	ArrayList<Integer> adjacentRegions; // holds id of all regions adjacent to this one (with only gateways btw)
	ArrayList<Integer> regionConnections; // holds number of gates connect to each region, same length as adjacentRegions
	
	//-- CONSTRUCTORS --//
	/**
	 * 
	 * @param board use to find gateways and adjacent regions
	 * @param regionTiles
	 */
	public Region(Board_v2 board, ArrayList<ArrayList<Integer>> regionTiles) {
		System.out.println("Region being made");
		// set identifier then increment nextIdentifier for next new region
		this.id = Region.nextIdentifier++;
		// create regionTiles out of passed region tiles 
		this.regionTiles = regionTiles;
		// set tiles on board to this region id
		for (ArrayList<Integer> position : this.regionTiles) {
			board.setRegionTile(this.id, position.get(0), position.get(1));
		}
		// confirm region tiles and get gateway tiles as well as adjacent regions info
		this.updateRegionTiles(board);
		this.updateGatewayTiles(board);
		this.updateAdjacentRegions(board);
		
		// initialize size
		this.updateSize();
	}
	
	//-- METHODS --//
	/**
	 * Get id of this region
	 * @return id of region
	 */
	public int getRegionId() {
		return this.id;
	}
	
	/**
	 * Update size field to actual current size
	 */
	public void updateSize() {
		this.size = regionTiles.size() + gatewayTiles.size();
	}
	
	// need methods to add gateways, add adjacent regions, 
	// remove gateways, remove tiles (convert to walls)
	// get number of adjacent regions 
	// encompassing method for partitioning subset of region into a new one
	
	/**
	 * loop through region tiles list until found one that is still part of this region on board
	 * recurse from here to find all other tiles in this region
	 * set these tiles board region values to this id
	 * @param board
	 */
	public void updateRegionTiles(Board_v2 board) {
		// create position to save first position
		ArrayList<Integer> startTile = new ArrayList<Integer>();
		// create array of checked values for recursing
		boolean[][] checked = new boolean[11][11];
		// loop through current region tiles until first one that is still region tile on board
		for (int i=0; i<this.regionTiles.size(); i++) {
			// check if value on board matches this region
			if (board.getRegionTile(regionTiles.get(i).get(0), regionTiles.get(i).get(1))==this.id) {
				// get row and col of first tile that matches board
				startTile.add(regionTiles.get(i).get(0));
				startTile.add(regionTiles.get(i).get(1));
				// recurse on board from start tile to get all the others, use null goal
				this.regionTiles = board.getConnectedRegionTiles(startTile, null, checked);
				// loop through updated region tiles list
				for (ArrayList<Integer> tile : regionTiles) {
					// set value on region board to this id
					board.setRegionTile(id, tile.get(0), tile.get(1));
				}
				// break out of loop as soon as hit first matching region
				break;
			}
		}



	}
	
	/**
	 * loop through region tiles list finding adjacent gateways to region on board
	 * recurse through gateways to find ones not directly touching a region tile
	 * take sides of gates as well to find adjacent regions to this one, add to list
	 * @param board
	 */
	public void updateGatewayTiles(Board_v2 board) {
		// loop through region tiles to find adjacent gateway tiles and recurse through them
		
		// reset gateway list
		this.gatewayTiles = new ArrayList<ArrayList<Integer>>();
		// create array of checked values for recursing
		boolean[][] checked = new boolean[11][11];
		// loop through region tiles
		for (ArrayList<Integer> regionTile : regionTiles) {
			// get adjacent tiles on board
			ArrayList<ArrayList<Integer>> adjacentTiles = board.getAdjacentTiles(regionTile);
			// loop through adjacent tiles
			for (ArrayList<Integer> adjacentTile : adjacentTiles) {
				// check if gate
				if (board.getRegionTile(adjacentTile.get(0), adjacentTile.get(1))==Board_v2.GATEWAY) {
					// check if already checked
					if (checked[adjacentTile.get(0)][adjacentTile.get(1)]) {
						// this tile already done
						continue;
					}
					// get list from tile
					ArrayList<ArrayList<Integer>> foundTiles = board.getConnectedGateways(adjacentTile,checked);
					for (ArrayList<Integer> t : foundTiles) {
						// add each gate to list of gateways in region
						gatewayTiles.add(t);
					}
					
				}
			}
		}
	}
	
	/**
	 * update adjacent regions list
	 * passing checked list into getSides means it wont loop through all gates since some will ...
	 * ... be done through recursion, shoudlnt have any repeats
	 * @param board access sides of gateways positions then look on board to see what their id is
	 */
	public void updateAdjacentRegions(Board_v2 board) {
		// reset adjacent regions list and adjacent regions connections list
		this.adjacentRegions = new ArrayList<Integer>();
		this.regionConnections = new ArrayList<Integer>();
		// create array of checked values for recursing
		boolean[][] checked = new boolean[11][11];
		// loop through gateway tiles
		for (ArrayList<Integer> gate : gatewayTiles) {
			// get sides to gate
			ArrayList<ArrayList<Integer>> sides = board.getSidesOfGate(gate, checked);
			// loop through sides of gate
			for (ArrayList<Integer> side : sides) {
				// check if this region
				if (board.getRegionTile(side.get(0), side.get(1))==id) {
					// ignore this tells nothing
					continue;
				}
				// else must be another region
				// loop through regions 
				boolean existingRegion = false;
				for (int i=0; i<adjacentRegions.size(); i++) {
					// check if region matches region on list
					if (board.getRegionTile(side.get(0), side.get(1))==adjacentRegions.get(i)) {
						// mark region as existent
						existingRegion = true;
						// increment connections to this region
						regionConnections.set(i, regionConnections.get(i)+1);
						break;
					}
				}
				// check if region wasnt found
				if (existingRegion==false) {
					// add region to list with 1 connection
					adjacentRegions.add(board.getRegionTile(side.get(0), side.get(1)));
					regionConnections.add(1);
				}
			}
		}
	}
	
	public void update(Board_v2 board) {
		// update region tiles
		this.updateRegionTiles(board);
		// update gateway tiles
		this.updateGatewayTiles(board);
		// update adjacent regions 
		this.updateRegionTiles(board);
		// update size
		this.updateSize();
	}
	
	

}
