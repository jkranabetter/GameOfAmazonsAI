package ubc.cosc322;

import java.util.ArrayList;

public class RandomAI {
	// constants
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	
	// fields
	int player; // holds BLACK or WHITE value so that AI can play as either team
	Board trueBoard; // pointer to true board
	Board testBoard; // clone of board to use for calculating moves
	ArrayList<ArrayList<ArrayList<Integer>>> actions; // list of moves, move = list of positions, position = list of ints
	ArrayList<ArrayList<Integer>> chosenAction; // hold RandomAI's chosen move from list of moves
	
	// constructors
	
	// create new AI to play as passed team on passed board
	public RandomAI(int player, Board board) {
		this.player = player;
		this.trueBoard = board;
		this.testBoard = new Board(this.trueBoard);
		this.actions = new ArrayList<ArrayList<ArrayList<Integer>>>();
	}
	
	// create new AI to play as passed team on new board
	public RandomAI(int player) {
		this(player, new Board());
	}
	
	// methods
	
	// update both board and testBoard to current state of passed board
	private void updateBoard(Board board) {
		this.trueBoard = board; // shouldn't be doing anything really but here just in case
		this.testBoard.clone(this.trueBoard);
	}
	
	// get every possible move this AI could make on this turn
	public ArrayList<ArrayList<ArrayList<Integer>>> getAllMoves(Board board) {
		// create list to return
		ArrayList<ArrayList<ArrayList<Integer>>> actions = new ArrayList<ArrayList<ArrayList<Integer>>>();
		// get your queens from board
		ArrayList<ArrayList<Integer>> myQueens = board.getQueens(this.player);
		// loop through queens finding action options
		for (ArrayList<Integer> queenCurrent : myQueens) {
			// get each queens direct movements
			ArrayList<ArrayList<Integer>> queenMoves = board.getDirectTiles(queenCurrent);
			// erase queen from old location on testboard
			board.setTile(Board.EMPTY, queenCurrent);
			// loop through each position to move to and find all possible arrow positions
			for (ArrayList<Integer> queenMoved : queenMoves) {
				// get all possible arrow positions for each of queen's movement options
				ArrayList<ArrayList<Integer>> arrows = board.getDirectTiles(queenMoved);
				// loop through all arrow positions
				for (ArrayList<Integer> arrow : arrows) {
					// create action for queen
					ArrayList<ArrayList<Integer>> move = new ArrayList<ArrayList<Integer>>();
					move.add(queenCurrent);
					move.add(queenMoved);
					move.add(arrow);
					// add action to list of actions
					actions.add(move);
				}
			}
			// add queen back to old location
			board.setTile(this.player, queenCurrent);
		}
		// return created list
		return actions;
	}
	
	// select an action at random from ai's list of actions and keep it in ai's chosenAction field
	public void getRandomAction() {
		// get random idx
		int randomIdx = (int) ( Math.random() * (this.actions.size()) );
		this.chosenAction = this.actions.get(randomIdx);
	}
	
	// have ai take state of board and apply its chosen action onto it (DO TURN)
	public void doAction() {
		this.actions.clear(); // clear actions from previous turn
		this.actions = this.getAllMoves(this.trueBoard);
		this.getRandomAction();
		// System.out.println(this.actions.size() + " moves available"); // TESTING
		this.trueBoard.applyAction(this.player, this.chosenAction);
	}
	
	
	
}
