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
 *     This class handles interactions between the
 *     ConnectionWindow, ChatData, and EncryptedMessageBuilder.
 *
 */

package controller;

import model.*;
import view.ClientWindow;
import view.ConnectionWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;


public class ConnectionController
{
	private ConnectionWindow connectionWindow;
	private ClientWindow clientWindow;
	private ChatData chatData;
	private EncryptedMessageBuilder encryptedMessageBuilder;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ChatClient chatClient;

	public ConnectionController() {

		this.connectionWindow = ConnectionWindow.getInstance();
		this.clientWindow = ClientWindow.getInstance();
		this.chatData = ChatData.getInstance();
		this.encryptedMessageBuilder = EncryptedMessageBuilder.getInstance();

		socket = null;
		out = null;
		in = null;
		chatClient = null;

		//Set up connection and client windows
		this.connectionWindow.addBtnConnectActionListener(new ConnectButton());
		this.connectionWindow.addBtnDisconnectActionListener(new DisconnectButton());
		connectionWindow.addGenerateRSACheckBoxListener(new generateRSACheckBoxHandler());
		this.clientWindow.addMenuActionListener("closeConnection", new DisconnectButton());
	}

	//
	// Perform all of the operation necessary to set up the client
	// to send and receive message.
	//
	private void startClientThread()
	{
		//Set p and q values for encryption
		if(!setPQValues()) {
			connectionUnsuccessful();
			return;
		}

		//Set up the clients information in the model
		this.chatData.setClientInfo(this.chatData.getUsername(), this.encryptedMessageBuilder.getPublicKey());

		//Start the thread that will handle the socket
		chatClient = new ChatClient();
		Thread chatClientThread = new Thread(chatClient);
		chatClientThread.start();
	}

	//
	// Set the p and q values that will be used for the RSA encryption.
	// The user can either provide their own values, or specify that
	// they would like the program to generate the values.
	//
	private boolean setPQValues()
	{
		//Let the user set the values
		if(!connectionWindow.generateRSAValuesIsSelected())
		{
			//Get values from view
			long p = connectionWindow.getPValue();
			long q = connectionWindow.getQValue();


			//Make sure the values are not the same
			if(p == q) {
				connectionWindow.setStatusLabel("p and q must have different values");
				connectionWindow.setVisible(true);
				return false;
			}

			//Set values
			int updateStatus = encryptedMessageBuilder.setPQValues(p, q);

			//Check for errors when update p and q
			if (updateStatus == 0) {
				connectionWindow.setStatusLabel("The product of P and Q must be");
				connectionWindow.setStatusLabelTwo("at least 72,057,594,037,927,936");
				connectionWindow.setVisible(true);
				return false;
			} else if (updateStatus == -1) {
				connectionWindow.setStatusLabel("P is not a prime number");
				connectionWindow.setVisible(true);
				return false;
			} else if (updateStatus == -2) {
				connectionWindow.setStatusLabel("Q is not a prime number");
				connectionWindow.setVisible(true);
				return false;
			}
		}
		else //Auto generate values
		{
			try
			{
				encryptedMessageBuilder.generate_PQ_Values();
			}
			catch (TimeoutException e)
			{
				connectionWindow.setStatusLabel("Product greater than 72,057,594,037,927,936 not");
				connectionWindow.setStatusLabelTwo("found in primeNumbers.rsc. Please check file.");
				connectionWindow.setVisible(true);
				return false;
			}
			catch (FileNotFoundException e)
			{
				connectionWindow.setStatusLabel("primeNumbers.rsc cannot be found.");
				connectionWindow.setVisible(true);
				return false;
			}
		}

		return true;
	}

	//
	// Handle a connection message from the server and make sure
	// the name the user specified is valid.
	//
	private void handleConnectionMessage(HandshakeMessage message)
	{
		//Check if the name the user specified is valid
		if(message.isValidName()) {
			chatData.setUsername(message.getSenderName());
			clientWindow.setTitle("Networked Chat - " + message.getSenderName());
			connectionSuccessful();
		} else {
			disconnectFromServer();
			clientWindow.setStatusMessage("");
			connectionWindow.setVisible(true);
			connectionWindow.setStatusLabel("Name already in use");
		}
	}

	//
	// Update model and view to reflect the new list of clients
	// sent by the server.
	//
	private void handleUpdateClientsMessage(ClientUpdateMessage message)
	{
		ArrayList<ClientInfoModel> clientList = message.getClientList();

		//update model
		chatData.setClientList(clientList);

		//Clear the old user list from clientWindow
		clientWindow.clearUserList();

		//Add the new user list to clientWindow
		for(ClientInfoModel user : clientList)
			if(!user.getName().equals(chatData.getUsername()))
				clientWindow.addUser(user.getName());
	}

	//
	// Read a message sent by another client and update the view
	// to show this message.
	//
	private void handleClientMessage(ClientMessage encryptedMessage)
	{
		String message = encryptedMessageBuilder.decryptMessage(encryptedMessage.getMessage());
		clientWindow.addReceivedMessage(encryptedMessage.getSenderName(), encryptedMessage.getTimeStamp(), message);
		clientWindow.setStatusMessage("Direct message received from " + encryptedMessage.getSenderName() +
		" (" + encryptedMessage.getTimeStamp() + ")");
	}

	//
	// Read a broadcast message sent by another client or the server and
	// update the view to show this message.
	//
	private void handleBroadcastMessage(BroadcastMessage message)
	{
		//If the message is not from the user who sent it add it to chat window
		if(!message.getSenderName().equals(chatData.getUsername()))
			clientWindow.addReceivedMessage(message.getSenderName() + " (broadcast)",
					message.getTimeStamp(), message.getUnencryptedMessage());
	}

	//
	// Determine the type of message received from the server and
	// pass it along for further processing.
	//
	private void processMessage(SocketMessage message)
	{
		if(message.getMessageType().equals("HandshakeMessage"))
		{
			HandshakeMessage connectionMessage = (HandshakeMessage)message;
			handleConnectionMessage(connectionMessage);
		}
		else if(message.getMessageType().equals("ClientUpdateMessage"))
		{
			ClientUpdateMessage updateClientsMessage = (ClientUpdateMessage) message;
			handleUpdateClientsMessage(updateClientsMessage);
		}
		else if(message.getMessageType().equals("ClientMessage"))
		{
			ClientMessage userMessage = (ClientMessage)message;
			handleClientMessage(userMessage);
		}
		else if(message.getMessageType().equals("BroadcastMessage"))
		{
			BroadcastMessage broadcastMessage = (BroadcastMessage)message;
			handleBroadcastMessage(broadcastMessage);
		}
	}

	//
	// Update the GUIs to show that the connection was successful.
	//
	private void connectionSuccessful()
	{
		connectionWindow.setInputState(false);
		connectionWindow.setVisible(false);
		connectionWindow.clearStatusLabel();
		clientWindow.setStatusMessage("Connected to server as " + chatData.getUsername());
		clientWindow.connected();
	}

	//
	// Update the GUIs to show that the connection was unsuccessful.
	//
	private void connectionUnsuccessful()
	{
		connectionWindow.setVisible(true);
		clientWindow.setStatusMessage("Connection unsuccessful.");
	}

	//
	// Stop the chatClient thread to disconnect from the server.
	//
	private void disconnectFromServer()
	{
		chatClient.stopClient();

		try {
			socket.close();
			out.close();
			in.close();

			socket = null;
			out = null;
			in = null;
		} catch (Exception e) {
			System.err.println("Cannot close socket.");
		}

		connectionWindow.setInputState(true);
		connectionWindow.setVisible(false);
		clientWindow.disconnected();
	}

	//
	//Send a message to the server
	//
	void sendMessage(Object message)
	{
		try {
			out.writeObject(message);
			out.flush();
			out.reset();
		} catch (IOException e) {
			System.err.println("Cannot send message to server: " + e.toString());
		}
	}

	//
	// Attempt to connect to the server when the user presses the connect button.
	//
	class ConnectButton implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event) 
		{
			connectionWindow.setVisible(false);
			clientWindow.setStatusMessage("Connecting to server...");

			startClientThread();
		}
	}

	//
	// Disconnect from the server when the user presses the disconnect button.
	//
	class DisconnectButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			disconnectFromServer();
		}
	}

	//
	// Update the RSA input boxes on the view when the user toggles
	// the generateRSACheckBox.
	//
	class generateRSACheckBoxHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JCheckBox generateRSACheckBox = (JCheckBox)e.getSource();

			if(generateRSACheckBox.isSelected())
				connectionWindow.setRSAInputEnabled(false);
			else
				connectionWindow.setRSAInputEnabled(true);
		}
	}

	//
	// This class will run in its own thread to receive message from the server.
	//
	private class ChatClient implements Runnable
	{
		private volatile boolean shouldRun;

		ChatClient()
		{
			shouldRun = true;
		}

		public void run()
		{
			try
			{
				//Get IP address and port number from the user
				String ipAddress = connectionWindow.getServerIP();
				int portNum = connectionWindow.getServerPort();

				socket = new Socket();

				//Connect to the server
				try {
					socket.connect(new InetSocketAddress(ipAddress, portNum), 5000);
					out = new ObjectOutputStream(socket.getOutputStream());
					in = new ObjectInputStream(socket.getInputStream());

					//Send a message to the server to make sure the username is ok
					sendMessage(new HandshakeMessage(new ClientInfoModel(connectionWindow.getUserName(),
							encryptedMessageBuilder.getPublicKey())));

				//Handle exceptions related to connecting to the server
				} catch (Exception e) {
					handleException(e, ipAddress, portNum);
				}

				//Handle exceptions related to the port number
			} catch (Exception e) {
				handleException(e, null, 0);
			}

			//Make sure the socket is connected before proceeding
			if(socket == null || !socket.isConnected()) {
				connectionUnsuccessful();
				return;
			}

			while(shouldRun)
				readMessage(); 
		}

		//Read a message when one arrives from the server
		private void readMessage()
		{
			try {
				processMessage((SocketMessage)in.readObject());
			}
			catch (Exception ex)
			{
				clientWindow.setStatusMessage("Lost connection with server");
				shouldRun = false;
				if(socket != null)
					disconnectFromServer();
			}
		}

		//
		// Allow the thread to break out of the while loop and end.
		//
		void stopClient() {
			shouldRun = false;
		}

		//
		// Display information to the user about exceptions generated while
		// connecting to the server
		//
		private void handleException(Exception exception, String ipAddress, int portNum)
		{
			if (exception instanceof SocketTimeoutException)
				connectionWindow.setStatusLabel("Cannot connect to server at " + ipAddress +
						" on port " + portNum);

			else if (exception instanceof ConnectException)
				connectionWindow.setStatusLabel("Connection refused by " + ipAddress +
						" on port " + portNum);

			else if (exception instanceof UnknownHostException)
				connectionWindow.setStatusLabel("Invalid IP address.");

			else if (exception instanceof  NumberFormatException || exception instanceof IllegalArgumentException)
				connectionWindow.setStatusLabel("Invalid port number.");

			else if (exception instanceof IOException)
				connectionWindow.setStatusLabel("Could not connect to server.");

		}
	}
}
