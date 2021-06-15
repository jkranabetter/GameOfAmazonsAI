
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
	Player ai; // changed to v2


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
			
			// version 2 code
			
			// create board for player ai to use
			this.board = new Board_v2();
			// determine which color is assigned to player
			if ( this.userName.equalsIgnoreCase( (String)( msgDetails.get(AmazonsGameMessage.PLAYER_BLACK) ) ) ) {
				// create black ai
				System.out.println("Creating Black AI");
				this.ai = new SmartAI(Board_v2.BLACK, this.board);
				// determine black first move
				System.out.println("Determining Black AI initial action");
				ArrayList<ArrayList<Integer>> action = ai.getAction();
				ArrayList<Integer> queenCurrent = action.get(0);
				ArrayList<Integer> queenMoved = action.get(1);
				ArrayList<Integer> arrow = action.get(2);
				// send action to 3 places
				System.out.println("Sending Black AI initial action");
				board.applyAction(this.ai.getColorInt(), queenCurrent, queenMoved, arrow);
				board.outputActionToConsole(queenCurrent, queenMoved, arrow);
				System.out.println(board);
				System.out.println("Waiting for opponent initial action");
				this.gameClient.sendMoveMessage(queenCurrent, queenMoved, arrow);
				this.gamegui.updateGameState(queenCurrent, queenMoved, arrow);
			}
			else {
				// create white ai
				System.out.println("Creating White AI");
				this.ai = new SmartAI(Board_v2.WHITE, this.board);
				System.out.println("Waiting for opponent initial action");
			}
			
		}
		else if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_MOVE)) {
			System.out.println("action move");
			
			// version 2 code
			
			// output turn count
			System.out.println("\nTurn: "+board.turnCount);
			
			// read in opponent action to board and gamegui
			System.out.println("Opponent is " + board.getPlayerColorString((ai.getColorInt()==Board_v2.BLACK)?Board_v2.WHITE:Board_v2.BLACK));
			
			System.out.println("Reading in action from opponent");
			ArrayList<Integer> queenCurrent = (ArrayList<Integer>)( msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR) );
			ArrayList<Integer> queenMoved = (ArrayList<Integer>)( msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT) );
			ArrayList<Integer> arrow = (ArrayList<Integer>)( msgDetails.get(AmazonsGameMessage.ARROW_POS) );
			
			// apply opponent action to board
			board.applyAction(((this.ai.getColorInt()==Board_v2.BLACK)?Board_v2.WHITE:Board_v2.BLACK), queenCurrent, queenMoved, arrow);
			board.outputActionToConsole(queenCurrent, queenMoved, arrow);
			System.out.println(board);
			this.gamegui.updateGameState(queenCurrent, queenMoved, arrow);
			
			// determine if player has lost
			if (board.checkLose(this.ai.getColorInt())==false) {
				// output turn count
				System.out.println("\nTurn: "+board.turnCount);
				// determine action
				System.out.println("Player is " + board.getPlayerColorString(ai.getColorInt()));
				System.out.println("Determining player action");
				ArrayList<ArrayList<Integer>> action = ai.getAction();
				queenCurrent = action.get(0);
				queenMoved = action.get(1);
				arrow = action.get(2);
				// send action to 3 places
				System.out.println("Sending player action");
				board.applyAction(this.ai.getColorInt(), queenCurrent, queenMoved, arrow);
				board.outputActionToConsole(queenCurrent, queenMoved, arrow);
				System.out.println(board);
				System.out.println("Waiting for opponent action");
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
