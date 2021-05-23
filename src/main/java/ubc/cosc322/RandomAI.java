package ubc.cosc322;

import java.util.ArrayList;

public class RandomAI {
	// constants
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	
	// fields
	int team; // holds BLACK or WHITE value so that AI can play as either team
	Board board; // pointer to true board
	Board testBoard; // clone of board to use for calculating moves
	ArrayList<ArrayList<ArrayList<Integer>>> moves; // list of moves, move = list of positions, position = list of ints
	ArrayList<ArrayList<Integer>> chosenMove; // hold RandomAI's chosen move from list of moves
	
	// constructors
	
	// create new AI to play as passed team on passed board
	public RandomAI(int team, Board board) {
		this.team = team;
		this.board = board;
		this.testBoard = new Board(this.board);
		this.moves = new ArrayList<ArrayList<ArrayList<Integer>>>();
	}
	
	// create new AI to play as passed team on new board
	public RandomAI(int team) {
		this(team, new Board());
	}
	
	// methods
	
	// update both board and testBoard to current state of passed board
	public void updateBoard(Board board) {
		this.board = board; // shouldn't be doing anything really but here just in case
		this.testBoard.clone(this.board);
	}
	
	// get every possible move this AI could make on this turn
	public void getAllMoves() {
		// get your queens from board
		ArrayList<ArrayList<Integer>> myQueens = testBoard.getQueens(this.team);
		// loop through queens finding movement options
		for (ArrayList<Integer> queenCurrent : myQueens) {
			// get each queens direct movements
			ArrayList<ArrayList<Integer>> queenMoves = testBoard.getMovementOptions(queenCurrent);
			// erase queen from old location on testboard
			testBoard.setTile(Board.EMPTY, queenCurrent);
			// loop through each position to move to and find all possible arrow positions
			for (ArrayList<Integer> queenMoved : queenMoves) {
				// get all possible arrow positions for each of queen's movement options
				ArrayList<ArrayList<Integer>> arrows = testBoard.getMovementOptions(queenMoved);
				// loop through all arrow positions
				for (ArrayList<Integer> arrow : arrows) {
					// create move for queen
					ArrayList<ArrayList<Integer>> move = new ArrayList<ArrayList<Integer>>();
					move.add(queenCurrent);
					move.add(queenMoved);
					move.add(arrow);
					// add move to list of moves
					this.moves.add(move);
				}
			}
			// add queen back to old location
			testBoard.setTile(this.team, queenCurrent);
		}
	}
	
	// select a move at random from ai's list of moves and keep it in ai's chosenMove field
	public void getRandomMove() {
		// get random idx
		int randomIdx = (int) ( Math.random() * (this.moves.size()) );
		this.chosenMove = this.moves.get(randomIdx);
	}
	
	// have ai take state of board and apply its chosen move onto it (DO TURN)
	public void makeMove(Board board) {
		this.updateBoard(board);
		this.moves.clear(); // clear moves from previous turn
		this.getAllMoves();
		this.getRandomMove();
		System.out.println(this.moves.size() + " moves available"); // TESTING
		board.applyMove(this.team, this.chosenMove);
	}
	
	
	
}
