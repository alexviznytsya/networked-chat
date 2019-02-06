/* File: HandshakeMessage.java
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
 *    used as handshake between server and client chat applications.
 * 
 */

package model;

public class HandshakeMessage extends SocketMessage
{
	// Properties:
	
    private boolean validName = false;

    // Default constructor:
    
    public HandshakeMessage(ClientInfoModel sender)
    {
        super(sender, "HandshakeMessage");
    }

    // Getter methods:
    
    public boolean isValidName() 
    {
    		return this.validName; 
    	}

    // Setter methods:
    
    public void setValidName(boolean value)
    {
        this.validName = value;
    }
}
