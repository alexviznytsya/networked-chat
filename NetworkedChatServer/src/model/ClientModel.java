/* File: ClientModel.java
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
 *    This class is client connection. When client establishes
 *    connection with server this instance of client is created.
 *    So, server can read and write objects to connected client
 *    using Sockets.
 * 
 */

package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientModel implements ServerObserver
{
	// Properties:
	
	private ClientInfoModel clientInfo = null;
	private Socket clientSocket = null;
	private ObjectInputStream dataIn = null;
	private ObjectOutputStream dataOut = null;
	
	// Default constructor:
	
	public ClientModel()
	{

	}
	
	// Getter methods:
	
	public ClientInfoModel getClientInfo()
	{
		return this.clientInfo;
	}
	
	public ObjectInputStream getInputStream()
	{
		return this.dataIn;
	}
	
	public ObjectOutputStream getOutputStream()
	{
		return this.dataOut;
	}
	
	public Socket getClientSocket() 
	{
		return this.clientSocket;
	}
	
	// Setter methods:
	
	public void setUserInfo(ClientInfoModel clientInfo)
	{
		this.clientInfo = clientInfo;
	}
	
	public void setClientSocket(Socket socket)
	{
		this.clientSocket = socket;
	}
	
	
	private boolean setDataIn() 
	{
		try 
		{
			this.dataIn = new ObjectInputStream(this.clientSocket.getInputStream());
			return true;
		} 
		catch (IOException e) 
		{
			System.err.println("Client " + this.clientInfo.getName() + " : Cannot instantiate ObjectInputStream > " + e.getMessage());
			return false;
		} 
	}
	
	private boolean setDataOut() 
	{
		try 
		{
			this.dataOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
			return true;
		}
		catch (IOException e) 
		{
			System.err.println("Client " + this.clientInfo.getName() + " : Cannot instantiate ObjectOutputStream > " + e.getMessage());
			return false;
		} 
	}
	
	// Class methods:
	
	public void setConnection() 
	{
		this.setDataOut();
		this.setDataIn();
	}
	
	public SocketMessage getData() 
	{
		SocketMessage data = null;
		try 
		{
			data = (SocketMessage)dataIn.readObject();
		} 
		catch (IOException | ClassNotFoundException e)
		{
			
		}
		return data;
	}
	
	public void sendData(SocketMessage data) 
	{
		try 
		{
			this.dataOut.writeObject(data);
			this.dataOut.flush();
			this.dataOut.reset();
		} catch (IOException e) {
			
		}
	}
	
	public void closeSocket() 
	{
		try 
		{
			if(this.clientSocket != null) 
			{
				this.clientSocket.close();
				this.clientSocket = null;
			}
			
		} 
		catch (IOException e) 
		{
			System.err.println("Client " + this.clientInfo.getName() + ": Cannot close socket > " + e.getMessage());
		}
	}

	@Override
	public void updateObserver(SocketMessage data) 
	{
		this.sendData(data);
	}
	
	// Inner Classes:

	
}
