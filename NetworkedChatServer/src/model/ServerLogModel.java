/* File: ServerLogModel.java
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
 *    This class is used to save server logs to text file.
 * 
 */

package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class ServerLogModel 
{
	// Properties:
	
	// Default Constructor:
	
	public ServerLogModel() 
	{
		
	}
	
	// Save server log history to .txt file: 
	
	public void stroreServerLog(File file, String serverLog) 
	{
        try 
        {
            BufferedWriter bufferedWriter = null;
            if(file.getName().contains(".txt")) 
            {
                bufferedWriter = new BufferedWriter(new FileWriter(file));
            } else {
               bufferedWriter = new BufferedWriter(new FileWriter(file + ".txt"));
            }
            
            bufferedWriter.write(serverLog);
            bufferedWriter.close();
     
        } 
        catch (Exception e) 
        {
            System.err.format("Error occured during '%s' file writing.", file.toString());
        }
    }
	
	// Setter  methods:
	
	// Class methods:
		
	// Inner Classes:
}
