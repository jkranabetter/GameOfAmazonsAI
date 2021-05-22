package ubc.cosc322;

import java.util.ArrayList;

public class RandomAI {
	// constants
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	
	// fields
	int team; // holds BLACK or WHITE value for differentiation
	Board board; // connection to true board
	Board testBoard; // clone of board to use for calculating moves
	ArrayList<ArrayList<ArrayList<Integer>>> moves; // list of moves, move = list of positions, position = list of ints
	
	// constructors
	public RandomAI(int team, Board board) {
		this.team = team;
		this.board = board;
		this.testBoard = new Board(this.board);
		this.moves = new ArrayList<ArrayList<ArrayList<Integer>>>();
	}
	public RandomAI(int team) {
		this(team, new Board());
	}
	
	// methods
	public void updateBoard(Board board) {
		this.board = board;
		this.testBoard.clone(this.board);
	}
	
	public void getAllMoves() {
		// erase old move options
		this.moves = new ArrayList<ArrayList<ArrayList<Integer>>>();
		// get your queens from board
		ArrayList<ArrayList<Integer>> myQueens = board.getQueens(this.team);
		// loop through queens finding movement options
		for (ArrayList<Integer> queenCurrent : myQueens) {
			// get each queens direct movements
			ArrayList<ArrayList<Integer>> queenMoves = board.getMovementOptions(queenCurrent);
			// loop through each position to move to and find all possible arrow positions
			for (ArrayList<Integer> queenMoved : queenMoves) {
				// get all possible arrow positions for each of queen's movement options
				ArrayList<ArrayList<Integer>> arrows = board.getMovementOptions(queenMoved);
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
		}
	}
	
	public ArrayList<ArrayList<Integer>> getRandomMove() {
		// get random idx
		int randomIdx = (int) Math.random()*(this.moves.size());
		return this.moves.get(randomIdx);
	}
	
	public ArrayList<ArrayList<Integer>> makeMove(Board board) {
		this.updateBoard(board);
		this.getAllMoves();
		return this.getRandomMove();
	}
	
	
	
}
