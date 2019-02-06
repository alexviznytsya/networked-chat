/* File: ClientMessage.java
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
 *    used send encrypted messages between clients.
 * 
 */

package model;

import java.math.BigInteger;
import java.util.ArrayList;

public class ClientMessage extends SocketMessage
{
	// Properties:
	
    private ClientInfoModel recipient;
    private ArrayList<BigInteger> message;

    // Default constructor:
    public ClientMessage(ClientInfoModel sender, ClientInfoModel recipient, ArrayList<BigInteger> message)
    {
        super(sender, "ClientMessage");
        this.recipient = recipient;
        this.message = message;
    }

    // Getter methods:
    public String getRecipientName()
    {
        return this.recipient.getName();
    }

    public ArrayList<BigInteger> getMessage()
    {
        return this.message;
    }

}
