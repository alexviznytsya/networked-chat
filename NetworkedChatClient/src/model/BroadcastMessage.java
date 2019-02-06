/* File: BroadcastMessage.java
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
 *    used as unencrypted message for all connected clients.
 * 
 */

package model;


public class BroadcastMessage extends SocketMessage
{
	// Properties:
	
	private String unencryptedMessage = null;
	
	
	// Default constructor:
	public BroadcastMessage(ClientInfoModel sender, String unencryptedMessage)
	{
		super(sender, "BroadcastMessage");
		this.unencryptedMessage = unencryptedMessage;
	}
	
	// Getters:
	
	public String getUnencryptedMessage() 
	{
		return this.unencryptedMessage;
	}

}
