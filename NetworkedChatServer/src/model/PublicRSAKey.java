/* File: PublicRSAKey.java
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
 *    This class is container for RSA public key pair.
 * 
 */

package model;

import java.io.Serializable;
import java.math.BigInteger;

public class PublicRSAKey implements Serializable
{
	// Properties:
	
    private BigInteger n;
    private BigInteger e;

    // Default Constructor:
    
    public PublicRSAKey(BigInteger n, BigInteger e)
    {
        this.n = n;
        this.e = e;
    }

    // Getters:
    
    public BigInteger getN()
    {
        return this.n;
    }

    public BigInteger getE()
    {
        return this.e;
    }

	// Setter  methods:
	
	// Class methods:
		
	// Inner Classes:
}
