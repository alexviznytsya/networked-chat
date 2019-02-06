/* File: ServerSubject.java
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
 *     This interface is subject interface for observer pattern.
 *     In case of this server, server will be subject and will
 *     notify all observers (chat clients) with any changes. 
 * 
 */

package model;

public interface ServerSubject 
{
	// Class methods:
	
	public void addObserver(ServerObserver observer);
	public void removeObserver(ServerObserver observer);
	public void updateObservers(SocketMessage data);
}
