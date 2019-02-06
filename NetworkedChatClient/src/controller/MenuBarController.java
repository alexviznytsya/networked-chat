/* File: MenuBarController.java
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
 *     This class adds control functionality to application's menu. 
 * 
 */

package controller;

import view.ClientWindow;
import view.ConnectionWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class MenuBarController 
{
	// Views:
	private ClientWindow clientWindow = null;
	private ConnectionWindow connectionWindow = null;

	public MenuBarController()
	{
		this.clientWindow = ClientWindow.getInstance();
		this.connectionWindow = ConnectionWindow.getInstance();

		//Set up action listeners
		this.clientWindow.addMenuActionListener("save2file", new SaveChatMenu());
		this.clientWindow.addMenuActionListener("exit", new MenuExit());
		this.clientWindow.addMenuActionListener("openConnection", new OpenConnection());
		this.clientWindow.addMenuActionListener("help", new HelpMenu());
		this.clientWindow.addMenuActionListener("about", new AboutMenu());
	}

	//
	// Allow the user to save the current chat to a text file
	//
	class SaveChatMenu implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			File saveToFile = null;
			FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("Chat Logs", "txt");

			//Create JFileChooser
			JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(fileNameExtensionFilter);

            int returnValue = fileChooser.showSaveDialog(clientWindow.getContentPane());

            if(returnValue == JFileChooser.APPROVE_OPTION) {
				try
				{
					saveToFile = fileChooser.getSelectedFile();
					BufferedWriter bufferedWriter = null;

					//Check file extension
					if(saveToFile.getName().contains(".txt")) {
						System.out.println(saveToFile.getName());
						bufferedWriter = new BufferedWriter(new FileWriter(saveToFile));
					} else {
						bufferedWriter = new BufferedWriter(new FileWriter(saveToFile + ".txt"));
					}

					//Write to file
					bufferedWriter.write(clientWindow.getHTMLDocument());
					bufferedWriter.close();
              
				} catch (Exception ex) {
					System.err.format("Error occurred during file writing.");
				}
            }
		}
	}

	//
	// Exit the program
	//
	class MenuExit implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event) 
		{
			System.exit(0);
		}
	}

	//
	// Display the connectionWindow to allow the user to connect to a server.
	//
	class OpenConnection implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event) 
		{
			connectionWindow.setVisible(true);
		}
	}

	//
	// Display information about how to use the program.
	//
	class HelpMenu implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String message = "Select \"Connection > Open Connection\" from the menu bar to establish\n" +
					"a connection with a server. This will display a dialog box prompting you to select\n" +
					"a name and enter the address of the server you would like to connect to.\n" +
					"You can also specify p and q values to be used for the RSA encryption, or select \n" +
					"\"Auto generate values\" to let the program choose for you.\n\n" +
					"Once you have connected to a server, select a user to send an encrypted message to from\n" +
					"the panel on the right side of the window.  You can also send an unencrypted message to\n" +
					"all users by checking the \"Send unencrypted message to all users\" checkbox.\n\n" +
					"To disconnect from the server, select \"Connection > Close Connection\" from the menu bar.\n";
	        String title = "How to use Networked Chat";
	        JOptionPane.showMessageDialog(clientWindow.getContentPane(), message, title, JOptionPane.PLAIN_MESSAGE);
		}
	}

	//
	// Display information about the program.
	//
	class AboutMenu implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String message = "Networked Chat - Client\n" +
					"with RSA Encryption/Decryption\n\n" +
					"Authors:\n\n" +
	                "Sean Martinelli\n\n" +
	                "Alex Viznytsya\n\n" +
	                "12/07/2017\n\n\n";
	        String title = "About";
	        JOptionPane.showMessageDialog(clientWindow.getContentPane(), message, title, JOptionPane.PLAIN_MESSAGE);
		}
	}
}