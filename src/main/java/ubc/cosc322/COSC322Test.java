
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
    
    Board board;
    RandomAI ai;
 
	
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
            // System.out.println("action start");
//            this.gamegui.updateGameState(msgDetails);
            
            // create ai as black
            this.board = new Board();
            this.ai = new RandomAI(Board.BLACK);
            ArrayList<ArrayList<Integer>> action = ai.doAction();
            ArrayList<Integer> queenCurrent = action.get(0);
            ArrayList<Integer> queenMoved = action.get(1);
            ArrayList<Integer> arrow = action.get(2);
            this.gameClient.sendMoveMessage(queenCurrent, queenMoved, arrow);
            this.gamegui.updateGameState(queenCurrent, queenMoved, arrow);
            
        }
        else if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_MOVE)) {
            // System.out.println("action move");
//            this.gamegui.updateGameState( (java.util.ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR), 
//                                            (java.util.ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT), 
//                                            (java.util.ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS) );
            
            // if ai not created, create ai as white
            if (this.ai==null) {
            	this.board = new Board();
                this.ai = new RandomAI(Board.WHITE);
            }
            // read in opponent action to our board
            ArrayList<ArrayList<Integer>> action = new ArrayList<ArrayList<Integer>>();
            action.add( (ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR) );
            action.add( (ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT) );
            action.add( (ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.ARROW_POS) );
            board.applyAction((this.ai.player==Board.BLACK)?(Board.WHITE):(Board.BLACK), action);
            
            // determine and send our action back to server
            action = ai.doAction();
            ArrayList<Integer> queenCurrent = action.get(0);
            ArrayList<Integer> queenMoved = action.get(1);
            ArrayList<Integer> arrow = action.get(2);
            this.gameClient.sendMoveMessage(queenCurrent, queenMoved, arrow);
            this.gamegui.updateGameState(queenCurrent, queenMoved, arrow);
            
            //these commented lines below were from what TA was showing in lab
            //ArrayList<Integer>queenToMove = new ArrayList<Integer>;
			//queenToMove.add(row);
			//queenToMove.add(col);
			//this.gameClient.sendMoveMessage(queenToMove, wheretoMove, whereToThrow);
			////now we tell the gui about the above move
			//this.gamegui.updateGameState(queenToMove, wheretoMove, whereToThrow);
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
