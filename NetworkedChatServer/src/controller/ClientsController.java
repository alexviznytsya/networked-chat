/* File: ClientsController.java
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
 *    This class is server controller that helps server 
 *    create, and manage new threads (clients connections). Also
 *    it is used in as subject in observer pattern.
 * 
 */

package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import model.BroadcastMessage;
import model.ClientModel;
import model.ServerObserver;
import model.ServerSubject;
import model.SocketMessage;
import model.ClientUpdateMessage;
import model.ClientInfoModel;
import model.ClientMessage;
import view.ServerWindow;

public class ClientsController implements ServerSubject
{
	
	// Models:
	
	// Views:
	private ServerWindow serverWindow = null;
	
	// Controllers:
	
	// Properties:
	
	private ArrayList<ClientInfoModel> activeUserList = new ArrayList<ClientInfoModel>();
	private Map<String, ClientModel> connectedClients = null;
	private Vector<ClientModel> syncedClients = null;
	private ClientInfoModel serverInfo = null;
	
	// Default Constructor:
	
	public ClientsController()
	{
		this.serverWindow = ServerWindow.getInstance();
		this.connectedClients = new HashMap<String, ClientModel>();
		this.syncedClients = new Vector<ClientModel>();
		this.serverInfo = new ClientInfoModel("Server", null);

	}
	
	// Getter methods:
	
	public ClientModel getClientByName(String username)
	{
		return this.connectedClients.get(username);
	}
	
	public boolean isUsernameValid(String username)
	{
		return !this.connectedClients.containsKey(username);
	}
	
	public ArrayList<ClientInfoModel> getActiveUserList()
	{
		return this.activeUserList;
	}
	
	// Setter methods:
	

	// Class methods:
	
	public void updateServerClientList()
	{
		this.serverWindow.clearUserList();
		for(ClientInfoModel user : activeUserList)
		{
			this.serverWindow.addUser(user.getName());
		}
		this.serverWindow.setConnectedClient(Integer.toString(activeUserList.size()));
	}
	
	public void closeAllConnections()
	{
		this.updateObservers(new BroadcastMessage(this.serverInfo, "You have been disconnected from server due to server shut down."));
		for(ClientModel client : syncedClients)
		{
			client.closeSocket();
		}
		this.activeUserList.clear();
		this.connectedClients.clear();
		this.syncedClients.clear();
		this.serverWindow.clearUserList();
	}
	
	public void newClientNotification(String clientName)
	{
		this.updateObservers(new BroadcastMessage(this.serverInfo, "User: " + clientName + " has joined to chat."));
	}
	
	@Override
	public void addObserver(ServerObserver observer) 
	{
		ClientModel client = (ClientModel)observer;
		this.connectedClients.put(client.getClientInfo().getName(), client);
		
		this.syncedClients.add(client);
		this.activeUserList.add(client.getClientInfo());
		
		this.updateServerClientList();
		this.serverWindow.addMessage("Client " + client.getClientInfo().getName() + " joined to chat", false);
		
		ActiveClientThread clientThread = new ActiveClientThread(client);
		new Thread(clientThread).start();
	}

	@Override
	public void removeObserver(ServerObserver observer) 
	{
		ClientModel client = (ClientModel)observer;
		
		this.connectedClients.remove(client.getClientInfo().getName());
		this.syncedClients.remove(client);
		this.activeUserList.remove(client.getClientInfo());
		
		client.closeSocket();

		this.updateServerClientList();
		serverWindow.addMessage("Client " + client.getClientInfo().getName() + " exited from chat", false);
	}

	@Override
	public void updateObservers(SocketMessage data) {
		serverWindow.addMessage("Updating clients with new data ...", false);
		for(ClientModel client : syncedClients)
		{
			client.updateObserver(data);
		}
	}

	//Inner classes:
	
	class ActiveClientThread implements Runnable
	{
		private ClientModel client = null;
		
		public ActiveClientThread(ClientModel client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			while(client.getClientSocket() != null)
			{
				try
				{
					SocketMessage data = this.client.getData();
					if(data.getMessageType().equals("ClientMessage") == true)
					{
						ClientMessage userMessage = (ClientMessage)data;
						ClientModel receipient = connectedClients.get(userMessage.getRecipientName());
						if(receipient != null)
						{
							syncedClients.get(syncedClients.indexOf(receipient)).sendData(userMessage);
							serverWindow.addMessage("Forwarding message from " + userMessage.getSenderName() + " to " + userMessage.getRecipientName(), false);
						}
						else
						{
							serverWindow.addMessage("Failed forward message from" + userMessage.getSenderName() + " because recipient is undefined", true);
						}
					} else if(data.getMessageType().equals("BroadcastMessage") == true){
						BroadcastMessage broadcastMessage = (BroadcastMessage)data;
						updateObservers(broadcastMessage);
						serverWindow.addMessage("Forwarding broadcast message from " + broadcastMessage.getSenderName() + " to all users", true);
					}
				}
				catch (Exception e)
				{
					if(client.getClientSocket() != null) 
					{
						removeObserver(client);
						updateObservers(new BroadcastMessage(serverInfo, "User \"" + client.getClientInfo().getName() + "\" has left the chat."));
						updateObservers(new ClientUpdateMessage(activeUserList));
						break;
					}
				}
			}
		}
	}

}
