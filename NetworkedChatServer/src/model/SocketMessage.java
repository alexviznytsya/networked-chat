/* File: SocketMessage.java
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
 *     This abstract class is generic message container for all future
 *     concrete classes that will be used for server-client communication.
 * 
 */

package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class SocketMessage implements Serializable
{
	// Properties: 
	
	private ClientInfoModel sender = null;
	private String messageType = null;
	private String timeStamp = null;
	
	// Default constructor:
	
	public SocketMessage(ClientInfoModel sender, String messageType)
	{
		this.sender = sender;
		this.messageType = messageType;
		
		SimpleDateFormat dateAndTime = new SimpleDateFormat ("MM/dd/yyyy hh:mm:ss");
		this.timeStamp = dateAndTime.format(new Date());
	}
	
	// Getter methods:
	
	public ClientInfoModel getSenderInfo()
	{
		return this.sender;
	}
	
	public String getSenderName()
	{
		return this.sender.getName();
	}
	
	public String getMessageType() {
		return this.messageType;
	}
	
	public PublicRSAKey getSenderPublicKey() {
		return this.sender.getPublicKey();
	}
	
	public String getTimeStamp()
	{
		return this.timeStamp;
	}
	
	// Setter  methods:
	
	// Class methods:
	
	// Inner Classes:
}
