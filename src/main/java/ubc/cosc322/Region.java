package ubc.cosc322;

import java.util.ArrayList;

public class Region {
//	//-- FIELDS --//
//	// static int nextIdentifier = 1;
//	int id; // on region version of board, tiles in this region will have this as their value
//	// int size; // holds number of board tiles belonging to this region, including gateway tiles
//	ArrayList<ArrayList<Integer>> regionTiles; // holds position on board of tiles belonging to this region
//	ArrayList<ArrayList<Integer>> gatewayTiles; // holds position on board of tiles acting as gateways in this region
//	ArrayList<Region> adjacentRegions; // holds id of all regions adjacent to this one (with only gateways btw)
//	// ArrayList<Integer> regionConnections; // holds number of gates connect to each region, same length as adjacentRegions
//	// int blackQueens, whiteQueens; // holds number of queens in region
//	
//	//-- CONSTRUCTORS --//
//	/**
//	 * 
//	 * @param board use to find gateways and adjacent regions
//	 * @param regionTiles
//	 */
//	public Region(Board_v2 board, ArrayList<ArrayList<Integer>> regionTiles) {
////		System.out.println("Region " + Region.nextIdentifier + " is being created");
//		// set identifier then increment nextIdentifier for next new region
//		// this.id = Region.nextIdentifier++;
//		this.id = board.regions.size();
//		// create regionTiles out of passed region tiles 
//		this.regionTiles = regionTiles;
//		// set tiles on board to this region id
//		for (ArrayList<Integer> position : this.regionTiles) {
//			board.setTileRegion(this.id, position.get(0), position.get(1));
//		}
//		// set region statistics
//		this.update(board);
//		
////		// output initial 
////		System.out.println("Region "+this.id+" has an initial size of "+this.size+".");
////		System.out.println("That includes "+regionTiles.size()+ " region tiles, and "+gatewayTiles.size()+" gateway tiles.");
////		for (int i=0; i<adjacentRegions.size() && i<regionConnections.size(); i++) {
////			System.out.println("It has "+regionConnections.get(i)+" connections to Region "+adjacentRegions.get(i));
////		}
//	}
//	
//	//-- METHODS --//
//	/**
//	 * Update region statistics
//	 * region tiles, region gates, adjacent regions 
//	 * @param board
//	 */
//	public void update(Board_v2 board) {
//		this.updateTiles(board);
//		this.updateRegionGates(board);
//		// this.updateNeighbourRegions(board);
//	}
//	
//	/**
//	 * Loop through region tiles to remove from region or to add adjacent tiles
//	 * boolean array inRegion meant to allow quick access to checking if tile is already in list
//	 * @param board
//	 */
//	public void updateTiles(Board_v2 board) {
////		// create array to hold quick access to whether tiles are in region
////		boolean[][] inRegion = new boolean[11][11];
//		// create list of tiles to potentially add to region
//		ArrayList<ArrayList<Integer>> newRegionTiles = new ArrayList<ArrayList<Integer>>();
//		// loop through region tiles
//		for (int i=0; i<regionTiles.size(); i++) {
//			// check if tile is no longer a part of regions 
//			if (board.getTileRegion(regionTiles.get(i))!=this.id) {
//				// remove from list and decrement i in order to not skip next tile
//				regionTiles.remove(i--); 
//			}
//			// check if tile is still part of region on board
//			else if (board.getTileRegion(regionTiles.get(i))==this.id) {
//				// mark on board
//				board.setTileRegion(this.id, regionTiles.get(i));
////				// mark in boolean array
////				inRegion[regionTiles.get(i).get(0)][regionTiles.get(i).get(1)] = true;
//				// get adjacent tiles and add to list
//				ArrayList<ArrayList<Integer>> adjacentTiles = new ArrayList<ArrayList<Integer>>();
//				// loop through adjacent tiles
//				for (ArrayList<Integer> adjacentTile : adjacentTiles) {
//					// check if adjacent tile matches this id
//					if (board.getTileRegion(adjacentTile)==this.id) {
//						// add to new region tiles list to potentially add later
//						newRegionTiles.add(adjacentTile);
//					}
//				}
//			}
//		}
//		// loop through new region tiles
//		for (ArrayList<Integer> newTile : newRegionTiles) {
//			// determine if already seen in list
//			// create boolean to hold whether its already in list or not
//			boolean inList = false;
//			// loop through region tiles
//			for (ArrayList<Integer> regionTile : regionTiles) {
//				// check if new tile matches tile in list
//				if (newTile.equals(regionTile)) {
//					// flag as seen in list
//					inList = true;
//					break;
//				}
//			}
//			// check if not seen in list
//			if (inList==false) {
//				// add to end of list
//				regionTiles.add(newTile);
//				// mark on board
//				board.setTileRegion(this.id, newTile);
//			}
//		}
//	}
//	
//	/**
//	 * Loop through region tiles finding region gates in adjacent spaces
//	 * @param board
//	 */
//	public void updateRegionGates(Board_v2 board) {
//		// clear out region gates
//		this.gatewayTiles = new ArrayList<ArrayList<Integer>>();
//		// loop through region tiles
//		for (ArrayList<Integer> regionTile : regionTiles) {
//			// get adjacent tiles
//			ArrayList<ArrayList<Integer>> adjacentTiles = board.getAdjacentTiles(regionTile);
//			// loop through adjacent tiles
//			for (ArrayList<Integer> adjacentTile : adjacentTiles) {
//				// check if adjacent tile is gate
//				if (board.getTileRegion(adjacentTile)==Board_v2.GATEWAY) {
//					// get list of connected gates
//					ArrayList<ArrayList<Integer>> connectedGates = board.getConnectedGates(adjacentTile, new boolean[11][11]);
//					// loop through potentially new gates
//					for (ArrayList<Integer> connectedGate : connectedGates) {
//						// determine if already in list of gateway tiles
//						boolean inList = false;
//						for (ArrayList<Integer> gate : gatewayTiles) {
//							// check if same position
//							if (gate.equals(connectedGate)) {
//								// connectedGate already in list
//								inList = true;
//								break;
//							}
//						}
//						// check if not in list
//						if (inList==false) {
//							// add to list
//							gatewayTiles.add(connectedGate);
//						}
//					}
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Loop through region gates finding adjacent region tiles to add their regions to list
//	 * NOT WORKING
//	 * @param board
//	 */
//	public void updateNeighbourRegions(Board_v2 board) {
//		// clear adjacent regions list 
//		this.adjacentRegions = new ArrayList<Region>();
//		// loop through gateway tiles
//		for (ArrayList<Integer> gate : gatewayTiles) {
//			// get sides of gate
//			ArrayList<ArrayList<Integer>> sides = board.getGateSides(gate);
//			// loop through sides
//			for (ArrayList<Integer> side : sides) {
//				// get region
//				Region region = board.getRegion(side).get(0);
//				// check if this region
//				if (region.id==this.id) {
//					// ignore side
//					continue;
//				}
//				// else is a different region
//				// check if region already in regions list
//				boolean inList = false;
//				for (Region adjacentRegion : adjacentRegions) {
//					// check if same region id
//					if (adjacentRegion.id==region.id) {
//						inList = true;
//						break;
//					}
//				}
//				// check if not in list
//				if (inList==false) {
//					// add region to list
//					adjacentRegions.add(region);
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Get number of passed player's queens that are in this region or its gates
//	 * @param player
//	 * @return
//	 */
//	public int getQueensCount(Board_v2 board, int player) {
//		// var to track number of queens
//		int count = 0;
//		// loop through region tiles
//		for (ArrayList<Integer> tile : regionTiles) {
//			if (board.getTile(tile)==player) {
//				count++;
//			}
//		}
//		// loop through gateways
//		for (ArrayList<Integer> tile : gatewayTiles) {
//			if (board.getTile(tile)==player) {
//				count++;
//			}
//		}
//		// return count of found queens
//		return count;
//	}
//	
//	/**
//	 * Get size of region, currently ignoring number of gates all together
//	 * @return
//	 */
//	public int getSize() {
//		return regionTiles.size();
//	}
	
	

}
