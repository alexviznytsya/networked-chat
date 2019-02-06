/* File: ClientUpdateMessage.java
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
 *    This class is concrete class from SocketMessage class, and 
 *    used to update connected client list for every client.
 * 
 */

package model;

import java.util.ArrayList;

public class ClientUpdateMessage extends SocketMessage
{
	// Properties:
	
    private ArrayList<ClientInfoModel> clientList = null;

    // Default constructor:
    
    public ClientUpdateMessage(ArrayList<ClientInfoModel> clientList)
    {
    		super(null, "ClientUpdateMessage");
        this.clientList = clientList;
    }

    // Getter methods:

    public ArrayList<ClientInfoModel> getClientList()
    {
        return clientList;
    }
    
    // Setter methods:
	
	// Class methods:
    
    // Inner classes:
}
