/* File: ClientController.java
 *
 * Authors:
 *     Alex Viznytsya
 *     Sean Martinelli
 *
 *
 * Date:
 *     12/07/2017
 * 
 * Class description:
 *     This class is main controller that instantiates other controllers,
 *     and setup entire application.
 * 
 */

package controller;

import model.*;
import view.ClientWindow;
import view.ConnectionWindow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;

public class ClientController 
{
	// Models:
	private ChatData chatData;
	
	// Views:
	private ClientWindow clientWindow;
	private ConnectionWindow connectionWindow;

	// Controllers:
	private ConnectionController connectionController;
	private MenuBarController menuBarController;

	private EncryptedMessageBuilder encryptedMessageBuilder;

	// Default constructor:
	public ClientController()
	{
		this.clientWindow = ClientWindow.getInstance();
		this.connectionWindow = ConnectionWindow.getInstance();
		this.chatData = ChatData.getInstance();
		this.connectionController = new ConnectionController();
		this.menuBarController = new MenuBarController();
		this.clientWindow.addSendButtonActionListener(new SendMessageHandler());
		this.encryptedMessageBuilder = EncryptedMessageBuilder.getInstance();
	}

	//
	// Start the controller and display the GUIs to begin the program
	//
	public void start()
	{
		this.clientWindow.setVisible(true);
		this.connectionWindow.setLocationRelativeTo(this.clientWindow);
		this.connectionWindow.setVisible(true);
	}

	//
	// Send an encrypted message to the specified user.
	//
	private void sendUserMessage(String sender, String recipient, String message)
	{
		//Encrypt message
		PublicRSAKey recipientPublicKey = chatData.getClientsPublicKey(recipient);
		ArrayList<BigInteger> encryptedMessage = encryptedMessageBuilder.encryptMessage(message, recipientPublicKey);

		//Send message
		ClientMessage clientMessage = new ClientMessage(new ClientInfoModel(sender, encryptedMessageBuilder.getPublicKey()),
				new ClientInfoModel(recipient, recipientPublicKey), encryptedMessage);
		connectionController.sendMessage(clientMessage);
		clientWindow.addSentMessage(recipient, clientMessage.getTimeStamp(), message, false);
	}

	//
	// Send an unencrypted message to all users.
	//
	private void sendBroadcastMessage(String sender, String message)
	{
		BroadcastMessage broadcastMessage = new BroadcastMessage(new ClientInfoModel(sender, null), message);
		connectionController.sendMessage(broadcastMessage);
		clientWindow.addSentMessage("all users", broadcastMessage.getTimeStamp(), message, true);
		clientWindow.setBroadcastCheckBoxState(false);
	}

	//
	// This class is responsible handling the action of the user pressing
	// the send button.
	//
	private class SendMessageHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String recipient = clientWindow.getRecipient();

			//Make sure a user is selected if it is not a broadcast message
			if(recipient == null && !clientWindow.getBroadcastCheckboxState()) {
				clientWindow.setStatusMessage("Please select a user to send your message to.");
				return;
			}

			String message = clientWindow.getSendMessage();

			//Make sure message is not blank
			if(message.isEmpty()) {
				clientWindow.setStatusMessage("Please enter a message to send.");
				return;
			}

			String sender = chatData.getUsername();

			//Send Message
			if(clientWindow.getBroadcastCheckboxState())
				sendBroadcastMessage(sender, message);
			else
				sendUserMessage(sender, recipient, message);

			clientWindow.setStatusMessage("Message sent");
		}
	}
}
