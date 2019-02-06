/* File: ServerController.java
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
 *    This class creates and run server. When server is started,
 *    it listens for every new client that wants to connect to it
 *    check all connection requirements and if everything is OK, it 
 *    creates new tread to for that particular client and wait for 
 *    new connection.
 * 
 */

package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import model.ClientModel;
import model.HandshakeMessage;
import model.SocketMessage;
import model.ClientUpdateMessage;
import view.ServerWindow;

public class ServerController 
{
	// Models:
	
	
	// Views:
	private ServerWindow serverWindow = null;
	
	// Controllers:
	private ClientsController connectionController = null;
	private MenuBarController menuBarController = null;
	
	// Properties:
	private ServerSocket serverSocket = null;
	private String serverIP = null;
	private int serverPort = -1;
	
	// Default constructor:
	public ServerController()
	{
		this.serverWindow = ServerWindow.getInstance();
		this.menuBarController = new MenuBarController();
		this.serverWindow.addBtnStartServerActionListener(new StartServer());
		this.serverWindow.addBtnStopServerActionListener(new StopServer());
		this.connectionController = new ClientsController();
		
		this.initializeServer();
	}
	
	// Getter methods:
	
	public int getServerPort() 
	{
		if(this.serverSocket != null) 
		{
			return serverSocket.getLocalPort();
		} 
		else 
		{
			return 0;
		}
	}
	
	// Setter methods:
	
	
	// Class methods:
	
	private void initializeServer() 
	{
		InetAddress hostIPAddress = null;
		
		try 
		{
			hostIPAddress = InetAddress.getLocalHost();
		} 
		catch (UnknownHostException e) 
		{
			System.err.println("Cannot find host: " + e.getMessage());
		}
		
		this.serverWindow.setServerIPField(hostIPAddress.getHostAddress());
		this.serverIP = hostIPAddress.getHostAddress();
		
		this.serverWindow.setVisible(true);
	}
	
	
	// Inner classes:
	
	class StartServer implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event) 
		{
			if(serverWindow.isInputFieldsValid() == true) 
			{
				serverPort = Integer.parseInt(serverWindow.getServerPortField());
				serverWindow.setServerInputState(false);
					
				try 
				{
					serverSocket = new ServerSocket(serverPort);
					serverPort = getServerPort();
					serverWindow.setServerPortField(serverPort);
					
					new Thread(new WaitForClientsThread()).start();
					
					serverWindow.addMessage("Server has been started", false);
				} 
				catch (IOException e) 
				{
					System.err.println("Server: Cannot create new server socket: " + e.getMessage());
				}
				
				serverWindow.setStatusMessage("Listening connections on IP: " + serverIP + ":" + serverPort + " ...");
			}
			else 
			{
				serverWindow.setStatusMessage("Please check IP address and port for your server ...");
			}
			
		}
		
	}
	
	class StopServer implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event) 
		{
			connectionController.closeAllConnections();
			
			if(serverSocket != null) 
			{
				try 
				{
					serverSocket.close();
					serverSocket = null;
					serverWindow.addMessage("Server has been stopped", true);
				} 
				catch (IOException e) 
				{
					System.err.println("Server: Cannot close server socket >" + e.getMessage());
				}
			}
		
			serverWindow.setStatusMessage("Server has been stopped ...");
			serverWindow.setServerInputState(true);
		}
	}
	
	class WaitForClientsThread implements Runnable
	{
		@Override
		public void run() 
		{
			while(serverSocket != null)
			{
				try 
				{
					serverWindow.addMessage("Listening for new connection ...", false);
					
					ClientModel client = new ClientModel();
					client.setClientSocket(serverSocket.accept());
					client.setConnection();
					
					SocketMessage data = client.getData();
					
					if(data.getMessageType().equals("HandshakeMessage") == true)
					{
						serverWindow.addMessage("Received handshake from " + data.getSenderName(), false);
						
						HandshakeMessage connectionMessage = (HandshakeMessage)data;
						
						if(connectionController.isUsernameValid(connectionMessage.getSenderName()) == true)
						{
							connectionMessage.setValidName(true);
							
							client.setUserInfo(connectionMessage.getSenderInfo());
							client.sendData(data);
							
							connectionController.addObserver(client);
							connectionController.newClientNotification(connectionMessage.getSenderName());
							connectionController.updateObservers(new ClientUpdateMessage(connectionController.getActiveUserList()));
						}
						else
						{
							connectionMessage.setValidName(false);
							
							client.sendData(data);
							client.closeSocket();
							
							serverWindow.addMessage("Client with  " + data.getSenderName() + " already exists. Notifying client ..." , false);
						}
					}
				} 
				catch (IOException e) 
				{ 
					
				} 		
			}
		}
	}
}
