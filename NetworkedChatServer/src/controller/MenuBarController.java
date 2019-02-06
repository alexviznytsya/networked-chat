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
 *    This class is collection of implemented ActionListener classes
 *    that are used for server menu item actions.
 * 
 */

package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ServerLogModel;
import view.ServerWindow;

public class MenuBarController 
{	
	//Models:
	private ServerLogModel serverLogModel = null;
	
	// Views:
	
	private ServerWindow serverWindow = null;
	
	// Controllers:
	
	// Default Constructor:
	
	public MenuBarController()
	{
		this.serverWindow = ServerWindow.getInstance();
		this.serverLogModel = new ServerLogModel();
		
		this.initializeMenu();
	}
	
	// Getter methods:
	
	
	// Setter methods:
	
	
	// Class methods:
	private void initializeMenu() 
	{
		this.serverWindow.addMenuItemsActionListener("saveServerLog", new SaveServerLogMenu());
		this.serverWindow.addMenuItemsActionListener("exit", new ExitMenu());
		this.serverWindow.addMenuItemsActionListener("help", new HelpMenu());
		this.serverWindow.addMenuItemsActionListener("about", new AboutMenu());
	}
	
	// Inner classes:
	class ExitMenu implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			System.exit(0);
		}
	}
	
	class SaveServerLogMenu implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("Server Logs", "txt");
            fileChooser.setFileFilter(fileNameExtensionFilter);
            int returnValue = fileChooser.showSaveDialog(serverWindow.getContentPane());
            if(returnValue == JFileChooser.APPROVE_OPTION) 
            {
                serverLogModel.stroreServerLog(fileChooser.getSelectedFile(), serverWindow.getHTMLDocument());
            }
		}
	}
	
	class HelpMenu implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String message = "To start server:\n" +
					"Enter port number in \"Server Port\" field, or select random checkbox to select\n" +
					"first available open port, and press \"Start Server\" button to star chat server.\n\n" +
					"To stop server:\n" +
					"Press \"Stop Server\" button, and server will notify all connected users that server\n" +
					"will be offline, disconects all users, and stop itself.\n\n";
	        String title = "Help";
	        JOptionPane.showMessageDialog(serverWindow.getContentPane(), message, title, JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	class AboutMenu implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String message = "Networked Chat - Server\n" +
					"with RSA Encryption/Decryption\n\n" +
					"Authors:\n\n" +
	                "Alex Viznytsya\n\n\n" +
	                "Sean Martinelli\n\n\n" +
	                "12/07/2017\n\n\n";
	        String title = "About";
	        JOptionPane.showMessageDialog(serverWindow.getContentPane(), message, title, JOptionPane.PLAIN_MESSAGE);
		}
	}
}
