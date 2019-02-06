/* File: ClientWindow.java
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
 *     This class is GUI for chat client application. 
 * 
 */

package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class ClientWindow  extends JFrame
{
	private static ClientWindow clientWindowInstatnce = null;

	private Map<String, JMenuItem> menuItems;
	private JTextPane chatMessagesTextPane;
	private DefaultListModel<String> userListModel;
	private JList<String> chatUserList;
	private JButton btnSendMessage;
	private JTextField txfldSendMessage;
	private JLabel lbStatusMessage;
	private JCheckBox broadcastCheckBox;
	private HTMLEditorKit editorKit;
	private HTMLDocument document;
	private JPanel contentPane;
	private Color backgroundColor;

	//Constructor is private to implement the Singleton Pattern
	private ClientWindow()
	{
		SetCrossPlatformLookAndFeel();

		//Initialize data members
		menuItems = new HashMap<String, JMenuItem>();
		chatMessagesTextPane = new JTextPane();
		userListModel = new DefaultListModel<String>();
		chatUserList = new JList<String>(userListModel);
		btnSendMessage = new JButton();
		txfldSendMessage = new JTextField();
		lbStatusMessage = new JLabel();
		editorKit = new HTMLEditorKit();
		document = new HTMLDocument();

		backgroundColor = Color.white;

		setFrameParameters();

		//Setup sub-components
		setupMenuComponents();
		setupMessageWindowComponents();
		setupUserListComponent();
		setupSendMessageComponent();
		setupStatusBarComponent();
	}

	//
	// Return the current instance of ClientWindow.
	// If there is not one, instantiate one.
	//
	public static ClientWindow getInstance()
	{
		if(clientWindowInstatnce == null) 
		{
			synchronized(ClientWindow.class)
			{
				if(clientWindowInstatnce == null)
					return ClientWindow.clientWindowInstatnce = new ClientWindow();
				else
					return ClientWindow.clientWindowInstatnce;
			}
		}
		else
		{
			return ClientWindow.clientWindowInstatnce;
		}
	}

	//
	// Return the HTMLDocument associated with the message window
	//
	public String getHTMLDocument() {
		try {
			return this.document.getText(0, this.document.getLength());
		} catch (BadLocationException e) {
			return null;
		}
	}

	//
	// Returns the string that is currently in the send message text field.
	// The text field is cleared before the method returns.
	//
	public String getSendMessage()
	{
		String message = this.txfldSendMessage.getText();
		this.txfldSendMessage.setText("");
		return message;
	}

	//
	// Returns name of the client the user current has selected in the user list.
	//
	public String getRecipient()
	{
		return chatUserList.getSelectedValue();
	}

	//
	// Returns the current state of the broadcast checkbox
	//
	public boolean getBroadcastCheckboxState()
	{
		return broadcastCheckBox.isSelected();
	}

	//
	// Sets the status bar label at the bottom of the window to the
	// specified message.
	//
	public void setStatusMessage(String message)
	{
		this.lbStatusMessage.setText(message);
	}

	//
	// Add an ActionListener to the specified menu item.
	//
	public void addMenuActionListener(String mnName, ActionListener actlsn)
	{
		this.menuItems.get(mnName).addActionListener(actlsn);

	}

	//
	// Set the state of the broadcast message checkbox
	//
	public void setBroadcastCheckBoxState(boolean state)
	{
		broadcastCheckBox.setSelected(state);
	}

	//
	// Add an action listener to the send button
	//
	public void addSendButtonActionListener(ActionListener actlsn)
	{
		this.btnSendMessage.addActionListener(actlsn);
	}

	//
	// Set components of the window to reflect that there is no
	// longer a connection to the server.
	//
	public void disconnected()
	{
		btnSendMessage.setEnabled(false);
		txfldSendMessage.setEnabled(false);
		broadcastCheckBox.setEnabled(false);
		menuItems.get("closeConnection").setEnabled(false);
		txfldSendMessage.setText("");
		setTitle("Networked Chat");
		clearUserList();
		setStatusMessage("Disconnected from server.");
	}

	//
	// Set components of the window to reflect that there is
	// a connection to the server.
	//
	public void connected()
	{
		chatMessagesTextPane.setText(null);
		btnSendMessage.setEnabled(true);
		txfldSendMessage.setEnabled(true);
		broadcastCheckBox.setEnabled(true);
		menuItems.get("closeConnection").setEnabled(true);
	}

	//
	// Remove all users from the user list.
	//
	public void clearUserList()
	{
		userListModel.removeAllElements();
	}

	//
	// Add a user to the user list.
	//
	public void addUser(String user)
	{
		userListModel.addElement(user);
	}

	//
	// Add a received message to the message window.  This includes the sender
	// and a time stamp.
	//
	public synchronized void addReceivedMessage(String from, String timeStamp, String message)
	{
		try {
			editorKit.insertHTML(document, document.getLength(), "<br><b>" + timeStamp + " > From "
					+ from + ":</b>", 0, 0, null);
			editorKit.insertHTML(document, document.getLength(), "<p>" + message + "</p>",
					0, 0, null);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	//
	// Add a received message to the message window.  This includes the recipient name
	// and a time stamp.
	//
	public synchronized void addSentMessage(String recipient, String timeStamp, String message,  boolean isBroadcast)
	{
		String color;

		//Set the color based on whether or not the message is a broadcast
		if(isBroadcast)
			color = "red";
		else
			color = "blue";

		//Display message
		try {
			editorKit.insertHTML(document, document.getLength(), "<br><b style=\"color:" + color + "\"> " +
					timeStamp + " > To " + recipient + ":</b>", 0, 0, null);
			editorKit.insertHTML(document, document.getLength(), "<p style=\"color:" + color + "\">" +
					message + "</p>", 0, 0, null);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	//
	//Make sure the look of the program is the same on different platforms
	//
	private void SetCrossPlatformLookAndFeel()
	{
		// Set cross-platform look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception e) {
			// Just continue using default look and feel
		}
	}

	//
	// Set up the menu items that make up the menu bar
	//
	private void setupMenuComponents()
	{
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mnitmSaveConversation = new JMenuItem("Save chat to file");
		this.menuItems.put("save2file", mnitmSaveConversation);
		mnFile.add(mnitmSaveConversation);

		JMenuItem mnitmExit = new JMenuItem("Exit");
		this.menuItems.put("exit", mnitmExit);
		mnFile.add(mnitmExit);

		JMenu mnConnection = new JMenu("Connection");
		menuBar.add(mnConnection);

		JMenuItem mnitmOpenConnection = new JMenuItem("Open Connection");
		this.menuItems.put("openConnection", mnitmOpenConnection);
		mnConnection.add(mnitmOpenConnection);

		JMenuItem mnitmCloseConnection = new JMenuItem("Close Connection");
		mnitmCloseConnection.setEnabled(false);
		this.menuItems.put("closeConnection", mnitmCloseConnection);
		mnConnection.add(mnitmCloseConnection);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mnitmHelp = new JMenuItem("Help");
		this.menuItems.put("help", mnitmHelp);
		mnHelp.add(mnitmHelp);

		JMenuItem mnitmAbout = new JMenuItem("About");
		this.menuItems.put("about", mnitmAbout);
		mnHelp.add(mnitmAbout);
	}

	//
	// Set up the components that display the messages the user
	// has sent and received.
	//
	private void setupMessageWindowComponents()
	{
		//Crate main panel
		JPanel chatMessagesPanel = new JPanel();
		chatMessagesPanel.setBounds(0, 0, 685, 680);
		chatMessagesPanel.setLayout(null);
		contentPane.add(chatMessagesPanel);

		//Create scroll pane to display message
		JScrollPane chatMessageScrollPane = new JScrollPane();
		chatMessageScrollPane.setSize(675, 670);
		chatMessageScrollPane.setLocation(5, 5);
		chatMessageScrollPane.setViewportBorder(null);
		chatMessageScrollPane.setBorder(null);
		chatMessageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatMessageScrollPane.getVerticalScrollBar().addAdjustmentListener(new AutoScroll(chatMessageScrollPane));
		chatMessagesPanel.add(chatMessageScrollPane);

		//Create heading label
		JLabel lbChatMessages = new JLabel("Chat Messages:");
		lbChatMessages.setOpaque(true);
		lbChatMessages.setBackground(backgroundColor);
		chatMessageScrollPane.setColumnHeaderView(lbChatMessages);

		this.chatMessagesTextPane.setBackground(backgroundColor);
		this.chatMessagesTextPane.setEditable(false);
		this.chatMessagesTextPane.setContentType("text/html");
		this.chatMessagesTextPane.setEditorKit(editorKit);
		this.chatMessagesTextPane.setDocument(document);
		chatMessageScrollPane.setViewportView(this.chatMessagesTextPane);
	}

	//
	// Set up the components that allow the user to see and select other users that are
	// currently connected to the server
	//
	private void setupUserListComponent()
	{
		//Crate main panel
		JPanel chatUsersPanel = new JPanel();
		chatUsersPanel.setBounds(685, 0, 315, 730);
		chatUsersPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
		chatUsersPanel.setLayout(null);
		contentPane.add(chatUsersPanel);

		//Create scroll panel to display list of current users
		JScrollPane usersScrollPane = new JScrollPane();
		usersScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		usersScrollPane.setBounds(5, 5, 305, 670);
		usersScrollPane.setViewportBorder(null);
		usersScrollPane.setBorder(null);
		usersScrollPane.getVerticalScrollBar().addAdjustmentListener(new AutoScroll(usersScrollPane));
		chatUsersPanel.add(usersScrollPane);

		//Create checkbox that will allow users to send broadcast message
		broadcastCheckBox = new JCheckBox("Send unencrypted message to all users");
		broadcastCheckBox.setBounds(15,695,270,25);
		broadcastCheckBox.setFont(new Font("Arial", Font.PLAIN, 13));
		broadcastCheckBox.setFocusable(false);
		broadcastCheckBox.setEnabled(false);
		chatUsersPanel.add(broadcastCheckBox);

		//Create user list heading label
		JLabel lbChatUsers = new JLabel("Other Chat Users:");
		lbChatUsers.setOpaque(true);
		lbChatUsers.setBackground(backgroundColor);
		lbChatUsers.setHorizontalAlignment(SwingConstants.CENTER);
		usersScrollPane.setColumnHeaderView(lbChatUsers);

		this.chatUserList.setBackground(backgroundColor);
		this.chatUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.chatUserList.setCursor(new Cursor(Cursor.HAND_CURSOR));
		usersScrollPane.setViewportView(chatUserList);
	}

	//
	// Set up the components that will allow the user to send a message
	//
	private void setupSendMessageComponent()
	{
		//Main panel
		JPanel sendMessagePanel = new JPanel();
		sendMessagePanel.setLayout(null);
		sendMessagePanel.setBounds(0, 680, 685, 50);
		sendMessagePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		contentPane.add(sendMessagePanel);

		//Area label
		JLabel lbSendMessage = new JLabel("Message:");
		lbSendMessage.setBounds(5, 15, 60, 15);
		sendMessagePanel.add(lbSendMessage);

		//message to send text area
		txfldSendMessage.setBounds(65, 11, 495, 26);
		txfldSendMessage.setEnabled(false);
		sendMessagePanel.add(txfldSendMessage);

		//Send Button
		this.btnSendMessage.setText("Send");
		this.btnSendMessage.setEnabled(false);
		this.btnSendMessage.setBounds(560, 10, 120, 30);
		sendMessagePanel.add(this.btnSendMessage);
		getRootPane().setDefaultButton(btnSendMessage);
	}

	//
	// Setup a status bar that will allow the program to alert the user to
	// status changes.
	//
	private void setupStatusBarComponent()
	{
		//main panel
		JPanel statusPanel = new JPanel();
		FlowLayout fl_statusPanel = (FlowLayout) statusPanel.getLayout();
		fl_statusPanel.setAlignment(FlowLayout.LEFT);
		statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		statusPanel.setBackground(Color.WHITE);
		statusPanel.setBounds(0, 730, 1000, 35);
		contentPane.add(statusPanel);

		//Status label
		JLabel lbStatus = new JLabel("Chat status:");
		statusPanel.add(lbStatus);

		this.lbStatusMessage.setText("Please select Connection > Open Connection");
		statusPanel.add(lbStatusMessage);
	}

	//
	// Set parameters associated with the windows main JFrame
	//
	private void setFrameParameters()
	{
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Networked Chat");
		addWindowListener(new WindowExit());
		setBounds(100, 100, 1000, 805);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
	}

	//
	// Control the window exit functionality.  The user is prompted to make sure
	// they actually want to quit.
	//
	class WindowExit extends WindowAdapter
	{
		@Override
	    public void windowClosing(WindowEvent e)
		{
	        int confirm = JOptionPane.showOptionDialog(ClientWindow.getInstance().getContentPane(),
					"Are You Sure to Close Networked Chat?",
					"Exit Confirmation", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null);
	        if (confirm == 0)
	        {
	        		System.exit(0);
	        }
	    }
	}

	//
	// This class controls the scrolling functionality associated with the message window
	// and user list window.
	//
	class AutoScroll implements AdjustmentListener {  
		
		private JScrollPane scrollPane = null;
		private boolean lockScroll = false; 
		private int currentValue = 0; 
		private int previousValue = 0;
		private int currentMaximum = 0;
		private int previousMaximum = 0;
		
		public AutoScroll(JScrollPane scrollPane)
		{
			this.scrollPane = scrollPane;
		}
		
		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {  
        		
			this.currentValue = e.getAdjustable().getValue();
			this.currentMaximum = e.getAdjustable().getMaximum();

			boolean valueChanged = (previousValue != currentValue);
			boolean maximumChanged = (previousMaximum != currentMaximum);

			//
			// Check state of scrollPane
			//

			if((this.currentValue + e.getAdjustable().getVisibleAmount()) == e.getAdjustable().getMaximum())
			{
				this.lockScroll = false;
			}
			
			if (valueChanged == true && maximumChanged == false)
			{
				this.scrollPane.getVerticalScrollBar().removeAdjustmentListener(this);
				this.lockScroll = true;
				e.getAdjustable().setValue(this.currentValue);
				this.scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
			}

			if (valueChanged == false && maximumChanged == true && lockScroll == false)
			{
				this.scrollPane.getVerticalScrollBar().removeAdjustmentListener(this);
				e.getAdjustable().setValue(e.getAdjustable().getMaximum() - e.getAdjustable().getVisibleAmount());
				this.scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
			}

			//Update previous values
			this.previousValue = e.getAdjustable().getValue();
			this.previousMaximum	= this.currentMaximum;	
			
		}
    }
}
