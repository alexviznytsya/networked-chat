/* File: SeerverObserver.java
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
 *     This interface is observer template for all future
 *     observers (chat clients) that needs to be notified 
 *     by observer (chat server) if any changes will occurred.
 * 
 */

package model;

public interface ServerObserver
{
	// Class methods:
	
	public void updateObserver(SocketMessage data);
}
