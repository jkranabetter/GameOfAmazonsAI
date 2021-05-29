package ubc.cosc322;

import java.util.ArrayList;

public class RandomAI_v2 extends Player {
	//-- CONSTRUCTORS --//
	/**
	 * Create Ai with passed color and with connection to passed board
	 * @param player
	 * @param board
	 */
	public RandomAI_v2(int player, Board_v2 board) {
		super(player, board);
	}
	
	//-- METHODS --//	
	/**
	 * Get a random action from a passed list of actions
	 * @param actions to choose from
	 * @return action in form [queenCurrent, queenMoved, arrow]
	 */
	public ArrayList<ArrayList<Integer>> getRandomAction(ArrayList<ArrayList<ArrayList<Integer>>> actions) {
		return actions.get( (int)( Math.random()*actions.size() ) );
	}
	
	/**
	 * Get action from this AI
	 * This method is called by COSC322TEST 
	 * @return action in form [queenCurrent, queenMoved, arrow]
	 */
	public ArrayList<ArrayList<Integer>> getAction() {
		ArrayList<ArrayList<ArrayList<Integer>>> actions = this.getAllActions(this.player, this.trueBoard);
		return this.getRandomAction(actions);
	}

}
