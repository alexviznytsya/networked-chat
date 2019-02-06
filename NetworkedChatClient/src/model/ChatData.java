/* File: ChatData.java
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
 *     This class is data holder for entire chat application. 
 * 
 */

package model;

import java.util.ArrayList;

public class ChatData
{
	private static ChatData chatDataInstance = null;
	private ArrayList<ClientInfoModel> clientList;
	private ClientInfoModel clientInfo = null;
	private String username;

	// private constructor to implement Singleton Pattern
	private ChatData()
	{
		clientList = new ArrayList<ClientInfoModel>();
		username = "Unknown";
	}

	//
	// Return the current instance of this class. If an instance
	// has not been instantiated yet, instantiate one.
	//
	public static ChatData getInstance() 
	{
		if(chatDataInstance == null)
		{
			synchronized(ChatData.class)
			{
				if(ChatData.chatDataInstance == null)
					return ChatData.chatDataInstance = new ChatData();
				else
					return ChatData.chatDataInstance;
			}
		} 
		else 
		{
			return ChatData.chatDataInstance;
		}
	}

	//
	// Return the current client list containing all of the users
	// currently connected to the server.
	//
	public ArrayList<ClientInfoModel> getClientList()
	{
		return this.clientList;
	}

	//
	// Return the username currently associated with this client.
	//
	public String getUsername()
	{
		return username;
	}

	//
	// Return the public key of the specified user.
	//
	public PublicRSAKey getClientsPublicKey(String client)
	{
		//Search client list for user and return their public key
		for(ClientInfoModel user : clientList)
			if(user.getName().equals(client))
				return user.getPublicKey();

		return null; //Client not in client list
	}

	//
	// Setter functions for data members
	//
	public void setClientList(ArrayList<ClientInfoModel> clientList) {
		this.clientList = clientList;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setClientInfo(String username, PublicRSAKey publicRSAKey) {
		this.clientInfo = new ClientInfoModel(username, publicRSAKey);
	}
}
