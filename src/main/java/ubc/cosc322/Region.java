package ubc.cosc322;

import java.util.ArrayList;

public class Region {
	//-- FIELDS --//
	static int nextIdentifier = 0;
	int id; // on region version of board, tiles in this region will have this as their value
	int size; // holds number of board tiles belonging to this region, including gateway tiles
	ArrayList<ArrayList<Integer>> regionTiles; // holds position on board of tiles belonging to this region
	ArrayList<ArrayList<Integer>> gatewayTiles; // holds position on board of tiles acting as gateways in this region
	
	
	//-- CONSTRUCTORS --//
	/**
	 * 
	 * @param board use to find gateways and adjacent regions
	 * @param regionTiles
	 */
	public Region(Board_v2 board, ArrayList<ArrayList<Integer>> regionTiles, ArrayList<ArrayList<Integer>> gatewayTiles) {
		// set identifier then increment nextIdentifier for next new region
		this.id = Region.nextIdentifier++;
		// create regionTiles out of passed region tiles 
		this.regionTiles = regionTiles;
		// set tiles on board to this region id
		for (ArrayList<Integer> position : this.regionTiles) {
			board.setRegionTile(this.id, position.get(0), position.get(1));
		}
		// add passed gateway tiles to field -> empty for first region
		this.gatewayTiles = gatewayTiles;
		// initialize size
		this.updateSize();
		
		// use gateways to find adjacent regions
		// pass adjacent regions?
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
	
	
	

}
