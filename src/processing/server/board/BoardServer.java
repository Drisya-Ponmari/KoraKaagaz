package processing.server.board;

import java.io.IOException;

import networking.CommunicatorFactory;
import processing.*;
import processing.utility.*;

/**
 * This contains the main function for the Board Server. Jar file for 
 * the Board Server will be made using this as the main starting file and 
 * this jar file will be used while starting a new Board server. So this file 
 * will be the first one to start when a Board Server will start.
 * 
 * @author Himanshu Jain
 * @reviewer Ahmed Zaheer Dadarkar
 *
 */

public class BoardServer {

	public static void main(String[] args) {
		
		/**
		 * While starting a new Board Server the port on which it should start 
		 * the communicator is given as the first argument, so we are first 
		 * extracting that.
		 */
		Port serverPort = new Port(Integer.parseInt(args[0]));
		
		/**
		 * Get a new communicator from the networking module giving the same port
		 * number received as the argument.
		 */
		ClientBoardState.communicator = CommunicatorFactory.getCommunicator(
				serverPort.port
		);
		
		/**
		 * Start the communicator by calling the start function of the communicator
		 * given above. It will now continuously listen on the port given as the input
		 * while creating the communicator.
		 */
		ClientBoardState.communicator.start();
		
		// set the Board Server's port as the port on which it is listening
		ClientBoardState.serverPort = serverPort;
		
		/**
		 * While starting a new Board Server, the Board ID is given as the second argument
		 * so extract it and save it in the Client Board State.
		 */
		ClientBoardState.boardId = new BoardId(args[1]);
		
		/**
		 * If the board existed before then it will have some persistence data which needs to
		 * be loaded again, so while starting the new Board Server the persistence file is
		 * passed as the third argument.
		 */
		String persistence = args[2];
		
		/**
		 * If the board never existed before i.e the board is a new board then the persistence 
		 * will be null, so we need not to do anything.
		 */
		if(persistence != null) {
			
			try {
				/**
				 * Deserialize the persistence string and cast it the BoardState
				 * and save this old persistence state in the maps of ClientBoardState.
				 */
				ClientBoardState.maps = (BoardState)Serialize.deSerialize(
						persistence
				);
			} catch (ClassNotFoundException e) {
				// log the exception
			} catch (IOException e) {
				// log the exception
			}
		}
		
		/**
		 * As soon as the client receives the port on which their requested Board's server
		 * is running they will make a request to the server asking for the previous 
		 * BoardState, so the server need to subscribe for thus request using the same 
		 * identifier the client is using to send BoardState Request.
		 */
		ClientBoardState.communicator.subscribeForNotifications(
				"BoardStateRequest", 
				new BoardStateRequestHandler()
		);
		
		/**
		 * As any client will make change on the board, it will pass it as the BoardObject
		 * to the server to broadcast it to the other clients, so the server need to subscribe
		 * for this request using the same identifier as the client is using to send the
		 * BoardObject.
		 */
		ClientBoardState.communicator.subscribeForNotifications(
				"ObjectBroadcast", 
				new ObjectBroadcastHandler()
		);
		
		/**
		 * When the client want to stop the connection it needs to tell the server
		 * so the client will send a message with identifier StopConnection and message as
		 * their userID, so the server need to subscribe for this message to receive.
		 */
		ClientBoardState.communicator.subscribeForNotifications(
				"StopConnection", 
				new ObjectBroadcastHandler()
		);
	}

}
