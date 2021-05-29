
package ubc.cosc322;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

	private GameClient gameClient = null; 
	private BaseGameGUI gamegui = null;

	private String userName = null;
	private String passwd = null;

	Board_v2 board; // changed to v2
	RandomAI_v2 ai; // changed to v2


	/**
	 * The main method
	 * @param args for name and passwd (current, any string would work)
	 */
	public static void main(String[] args) {				 
		COSC322Test player = new COSC322Test(args[0], args[1]);

		if(player.getGameGUI() == null) {
			player.Go();
		}
		else {
			BaseGameGUI.sys_setup();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					player.Go();
				}
			});
		}
	}

	/**
	 * Any name and passwd 
	 * @param userName
	 * @param passwd
	 */
	public COSC322Test(String userName, String passwd) {
		this.userName = userName;
		this.passwd = passwd;

		//To make a GUI-based player, create an instance of BaseGameGUI
		//and implement the method getGameGUI() accordingly
		this.gamegui = new BaseGameGUI(this);
	}



	@Override
	public void onLogin() {
		System.out.println("Congratualations!!! "
				+ "I am called because the server indicated that the login is successfully");
		System.out.println("The next step is to find a room and join it: "
				+ "the gameClient instance created in my constructor knows how!"); 

		System.out.println(gameClient.getRoomList());

		//    	//warm up #1
		//    	List<Room> x = gameClient.getRoomList();
		//
		//    	gameClient.joinRoom(x.get(0).getName());

		// warmup 2
		userName = gameClient.getUserName();
		if(gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}    	

	}

	//if you get game actionstart you are first player to move (black)																																																																										
	@Override
	public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		//This method will be called by the GameClient when it receives a game-related message
		//from the server.

		//For a detailed description of the message types and format, 
		//see the method GamePlayer.handleGameMessage() in the game-client-api document. 

		// warmup 2 - print out a message from the room
		if (messageType.equalsIgnoreCase(GameMessage.GAME_STATE_BOARD)) {
			// System.out.println("state board");
			this.gamegui.setGameState( (java.util.ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE) );
		}
		else if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_START)) {
			System.out.println("action start");
			//            this.gamegui.updateGameState(msgDetails);

//			// create ai as black
//			if (this.userName.equalsIgnoreCase((String)(msgDetails.get(AmazonsGameMessage.PLAYER_BLACK)))) {
//				this.board = new Board();
//				this.ai = new RandomAI(Board.BLACK);
//				System.out.println("Black AI created.");
//				ArrayList<ArrayList<Integer>> action = ai.doAction();
//				ArrayList<Integer> queenCurrent = action.get(0);
//				ArrayList<Integer> queenMoved = action.get(1);
//				ArrayList<Integer> arrow = action.get(2);
//				System.out.println("Black AI is sending action to server now.");
//				this.gameClient.sendMoveMessage(queenCurrent, queenMoved, arrow);
//				this.gamegui.updateGameState(queenCurrent, queenMoved, arrow);
//			}
//			else {
//				this.board = new Board();
//				this.ai = new RandomAI(Board.WHITE);
//				System.out.println("White AI created.");
//			}
			
			// version 2 code
			
			
			// create board for player ai to use
			this.board = new Board_v2();
			// create black ai or white ai
			if ( this.userName.equalsIgnoreCase( (String)( msgDetails.get(AmazonsGameMessage.PLAYER_BLACK) ) ) ) {
				// create black ai
				System.out.println("Creating Black AI");
				this.ai = new RandomAI_v2(Board.BLACK, this.board);
				// determine black first move
				System.out.println("Determining Black AI initial action");
				ArrayList<ArrayList<Integer>> action = ai.getAction();
				ArrayList<Integer> queenCurrent = action.get(0);
				ArrayList<Integer> queenMoved = action.get(1);
				ArrayList<Integer> arrow = action.get(2);
				// send action to 3 places
				System.out.println("Sending Black AI initial action");
				board.applyAction(this.ai.getColor(), queenCurrent, queenMoved, arrow);
				System.out.println(board);
				this.gameClient.sendMoveMessage(queenCurrent, queenMoved, arrow);
				this.gamegui.updateGameState(queenCurrent, queenMoved, arrow);
			}
			else {
				// create white ai
				System.out.println("Creating White AI");
				this.ai = new RandomAI_v2(Board.WHITE, this.board);
			}
			
		}
		else if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_MOVE)) {
			// System.out.println("action move");
			//            this.gamegui.updateGameState( (java.util.ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR), 
			//                                            (java.util.ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT), 
			//                                            (java.util.ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS) );

//			// read in opponent action to our board
//			System.out.println("Reading in action from opponent.");
//			ArrayList<ArrayList<Integer>> action = new ArrayList<ArrayList<Integer>>();
//			action.add( (ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR) );
//			action.add( (ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT) );
//			action.add( (ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.ARROW_POS) );
//
//			board.applyAction((this.ai.player==Board.BLACK)?(Board.WHITE):(Board.BLACK), action);
//			System.out.println("Board updated with opponent action.");
//			
//			System.out.println(board);
//			this.gamegui.updateGameState(action.get(0), action.get(1), action.get(2));
//
//			// check if we lost
//			System.out.println("Checking if game lost before deciding action.");
//			if (board.checkLose(this.ai.player)==this.ai.player) {
//				System.out.println("Player has lost");
//				return true;
//			}
//			System.out.println("Game not lost. Beginning player action.");
//
//			// determine and send our action back to server
//			action = ai.doAction();
//			ArrayList<Integer> queenCurrent = action.get(0);
//			ArrayList<Integer> queenMoved = action.get(1);
//			ArrayList<Integer> arrow = action.get(2);
//
//			long time = System.nanoTime();
//			while ((System.nanoTime()-time)<=1000000000) {
//
//			}
//			System.out.println("Sending player action to server now.");
//
//			System.out.println(board);
//			this.gameClient.sendMoveMessage(queenCurrent, queenMoved, arrow);
//			this.gamegui.updateGameState(queenCurrent, queenMoved, arrow);

			//these commented lines below were from what TA was showing in lab
			//ArrayList<Integer>queenToMove = new ArrayList<Integer>;
			//queenToMove.add(row);
			//queenToMove.add(col);
			//this.gameClient.sendMoveMessage(queenToMove, wheretoMove, whereToThrow);
			////now we tell the gui about the above move
			//this.gamegui.updateGameState(queenToMove, wheretoMove, whereToThrow);
			
			// version 2 code
			
			// read in opponent action to board and gamegui
			System.out.println("Opponent is " + board.getPlayerColor((ai.getColor()==Board.BLACK)?Board.WHITE:Board.BLACK));
			System.out.println("Reading in action from opponent");
			ArrayList<Integer> queenCurrent = (ArrayList<Integer>)( msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR) );
			ArrayList<Integer> queenMoved = (ArrayList<Integer>)( msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT) );
			ArrayList<Integer> arrow = (ArrayList<Integer>)( msgDetails.get(AmazonsGameMessage.ARROW_POS) );
			board.applyAction((this.ai.getColor()==Board.BLACK)?Board.WHITE:Board.BLACK, queenCurrent, queenMoved, arrow);
			System.out.println(board);
			this.gamegui.updateGameState(queenCurrent, queenMoved, arrow);
			// determine if player has lost
			if (board.checkLose(this.ai.getColor())==false) {
				// determine action
				System.out.println("Player is " + board.getPlayerColor(ai.getColor()));
				System.out.println("Determining player action");
				ArrayList<ArrayList<Integer>> action = ai.getAction();
				queenCurrent = action.get(0);
				queenMoved = action.get(1);
				arrow = action.get(2);
				// send action to 3 places
				System.out.println("Sending player action");
				board.applyAction(this.ai.getColor(), queenCurrent, queenMoved, arrow);
				System.out.println(board);
				this.gameClient.sendMoveMessage(queenCurrent, queenMoved, arrow);
				this.gamegui.updateGameState(queenCurrent, queenMoved, arrow);
			}
		}
		return true;
	}

	@Override
	public String userName() {
		return userName;
	}

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub
		return  this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		gameClient = new GameClient(userName, passwd, this);			
	}


}//end of class
