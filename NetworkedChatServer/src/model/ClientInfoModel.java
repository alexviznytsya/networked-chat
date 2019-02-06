/* File: ClientInfoModel.java
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
 *    This class is container for client information,
 *    such as name and public RSA key pair. With this class every client
 *    can be stored in any array like container and used for encrypting and 
 *    sending messages.
 * 
 */

package model;

import java.io.Serializable;

public class ClientInfoModel implements Serializable
{
	// Properties:
	
    private String name;
    private PublicRSAKey publicKey;

    // Default constructor:
    
    public ClientInfoModel(String name, PublicRSAKey publicKey)
    {
        this.name = name;
        this.publicKey = publicKey;
    }

    // Getters:
    
    public String getName()
    {
        return name;
    }

    public PublicRSAKey getPublicKey()
    {
        return publicKey;
    }
    
    // Setter methods:
    
    // Class methods:
    
    // Inner classes:
}


