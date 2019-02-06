/* File: ServerWindow.java
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
 *     This Singleton class is GUI for chat server application. 
 * 
 */

package view;

import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

public class ServerWindow extends JFrame 
{

	// Properties:
	
	private static volatile ServerWindow serverWindowInstance = null;	
	private final Map<String, JMenuItem> menuItems = new HashMap<String, JMenuItem>();
	private JTextField tfServerIP = new JTextField();
	private JTextField tfServerPort = new JTextField();
	private JCheckBox cbRandomPort = new JCheckBox();
	private JButton btnStartServer = new JButton();
	private JButton btnStopServer = new JButton();
	private DefaultListModel<String> userListModel = new DefaultListModel<String>();
	private JList<String> clientList = new JList<String>(userListModel);
	private JTextPane serverMessagesTextPane = new JTextPane();
	private JLabel lblStatusMessage = new JLabel();
	private HTMLEditorKit editorKit = new HTMLEditorKit();
	private HTMLDocument document = new HTMLDocument();
	private JLabel lblNumConnectedClients = new JLabel();
	
	// Default constructor:
	
	private ServerWindow() 
	{
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowExit());
		setBounds(100, 100, 600, 505);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		// Set up menu area:
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmSaveServerLog = new JMenuItem("Save Server Log");
		this.menuItems.put("saveServerLog", mntmSaveServerLog);
		mnFile.add(mntmSaveServerLog);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		this.menuItems.put("exit", mntmExit);
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		this.menuItems.put("help", mntmHelp);
		mnHelp.add(mntmHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		this.menuItems.put("about", mntmAbout);
		mnHelp.add(mntmAbout);
		
		// Set up server settings area:
		
		JPanel serverSettingsPanel = new JPanel();
		serverSettingsPanel.setBounds(5, 5, 270, 90);
		serverSettingsPanel.setLayout(null);
		contentPane.add(serverSettingsPanel);
		
		JLabel serverIPLabel = new JLabel("Server IP:");
		serverIPLabel.setBounds(5, 5, 85, 15);
		serverSettingsPanel.add(serverIPLabel);
		
		JLabel serverPortLabel = new JLabel("Server Port:");
		serverPortLabel.setBounds(5, 35, 85, 15);
		serverSettingsPanel.add(serverPortLabel);
		tfServerIP.setEditable(false);
		
		this.tfServerIP.setBounds(85, 0, 180, 25);
		serverSettingsPanel.add(this.tfServerIP);
		
		this.tfServerPort.setBounds(85, 30, 180, 25);
		serverSettingsPanel.add(tfServerPort);
		
		this.cbRandomPort.setText("Select Random Port");
		this.cbRandomPort.setBounds(80, 60, 155, 25);
		this.cbRandomPort.addActionListener(new CheckBox());
		serverSettingsPanel.add(this.cbRandomPort);
		
		// Set up server controls area:
		
		JPanel serverControlPanel = new JPanel();
		serverControlPanel.setBounds(5, 100, 270, 40);
		contentPane.add(serverControlPanel);
		
		this.btnStartServer.setText("Start Server");
		serverControlPanel.add(this.btnStartServer);
		
		this.btnStopServer.setText("Stop Server");
		this.btnStopServer.setEnabled(false);
		serverControlPanel.add(this.btnStopServer);
		
		// Set up connected clients area:
		
		JPanel connectedClientsPanel = new JPanel();
		connectedClientsPanel.setBounds(290, 5, 304, 135);
		connectedClientsPanel.setLayout(null);
		contentPane.add(connectedClientsPanel);
		
		JScrollPane connectedClientsScrollPane = new JScrollPane();
		connectedClientsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		connectedClientsScrollPane.setBounds(0, 0, 304, 135);
		connectedClientsScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		connectedClientsScrollPane.getVerticalScrollBar().addAdjustmentListener(new AutoScroll(connectedClientsScrollPane));
		connectedClientsPanel.add(connectedClientsScrollPane);
		
		this.clientList.setBackground(SystemColor.window);
		this.clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		connectedClientsScrollPane.setViewportView(this.clientList);
		
		JPanel clientPanel = new JPanel();
		clientPanel.setBorder(null);
		JLabel connectedClientsLabel = new JLabel("Conected Clients:");
		clientPanel.add(connectedClientsLabel);
		connectedClientsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		connectedClientsLabel.setOpaque(true);
		this.lblNumConnectedClients.setText("0");
		clientPanel.add(this.lblNumConnectedClients);
		connectedClientsScrollPane.setColumnHeaderView(clientPanel);
		
		// Set up server messages area:
		
		JPanel serverMessagesPanel = new JPanel();
		serverMessagesPanel.setBounds(5, 150, 589, 275);
		serverMessagesPanel.setLayout(null);
		contentPane.add(serverMessagesPanel);
		
		JScrollPane serverMesagesScrollPanel = new JScrollPane();
		serverMesagesScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		serverMesagesScrollPanel.setBounds(0, 0, 589, 275);
		serverMesagesScrollPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		serverMesagesScrollPanel.getVerticalScrollBar().addAdjustmentListener(new AutoScroll(serverMesagesScrollPanel));
		serverMessagesPanel.add(serverMesagesScrollPanel);
		
		this.serverMessagesTextPane.setEditable(false);
		this.serverMessagesTextPane.setBackground(SystemColor.window);
		this.serverMessagesTextPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.serverMessagesTextPane.setContentType("text/html");
		this.serverMessagesTextPane.setEditorKit(editorKit);
		this.serverMessagesTextPane.setDocument(document);
		serverMesagesScrollPanel.setViewportView(this.serverMessagesTextPane);
		
		JPanel messagesPanel = new JPanel();
		serverMesagesScrollPanel.setColumnHeaderView(messagesPanel);
		
		JLabel serverMessagesLabel = new JLabel("Server Messages:");
		messagesPanel.add(serverMessagesLabel);
		serverMessagesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		serverMessagesLabel.setOpaque(true);
		
		// Set up server status bar: 
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBackground(Color.WHITE);
		FlowLayout fl_statusPanel = (FlowLayout) statusPanel.getLayout();
		fl_statusPanel.setAlignment(FlowLayout.LEFT);
		statusPanel.setBounds(0, 430, 600, 30);
		contentPane.add(statusPanel);
		
		JLabel serverStatusLabel = new JLabel("Server status:");
		statusPanel.add(serverStatusLabel);
		
		lblStatusMessage.setText("server is not running. Plese set IP and Port to start chat server.");
		statusPanel.add(lblStatusMessage);
		
	}
	
	// Getter methods:
	
	public static ServerWindow getInstance() 
	{
		if(ServerWindow.serverWindowInstance == null) 
		{
			synchronized(ServerWindow.class)
			{
				if(ServerWindow.serverWindowInstance == null)
				{
					return ServerWindow.serverWindowInstance = new ServerWindow();
				} 
				else 
				{
					return ServerWindow.serverWindowInstance;
				}
			}
		}
		else 
		{
			return ServerWindow.serverWindowInstance;
		}
	}
	
	public String getServerIPField() 
	{
		return this.tfServerIP.getText();
	}
	
	public String getServerPortField() 
	{
		return this.tfServerPort.getText();
	}
	
	public String getHTMLDocument() 
	{
		try 
		{
			return this.document.getText(0, this.document.getLength());
		} 
		catch (BadLocationException e) 
		{
			return null;
		}
	}
	
	// Setter methods:
	
	public void setServerIPField(String IP) 
	{
		this.tfServerIP.setText(IP);
	}
	
	public void setServerPortField(int port) 
	{
		this.tfServerPort.setText(Integer.toString(port));
	}
	
	public void setStatusMessage(String message)
	{
		this.lblStatusMessage.setText(message);
	}
	
	public void setConnectedClient(String num)
	{
		this.lblNumConnectedClients.setText(num);
	}
	
	// Class methods:
	
	public void addMenuItemsActionListener(String mnName, ActionListener actlsn) 
	{
		this.menuItems.get(mnName).addActionListener(actlsn);
	}
	
	public void addBtnStartServerActionListener(ActionListener actlsn)
	{
		this.btnStartServer.addActionListener(actlsn);
	}
	
	public void addBtnStopServerActionListener(ActionListener actlsn)
	{
		this.btnStopServer.addActionListener(actlsn);
	}
	
	// Enable and disable server control buttons and fields:
	
	public void setServerInputState(boolean state) 
	{
		this.tfServerIP.setEnabled(state);
		this.tfServerPort.setEnabled(state);
		this.cbRandomPort.setEnabled(state);
		this.btnStartServer.setEnabled(state);
		this.btnStopServer.setEnabled(!state);
	}
	
	// Check if user filled all required fields before server start:
	
	public boolean isInputFieldsValid() 
	{
		if(this.cbRandomPort.isSelected() == true)
		{
			this.tfServerPort.setText("0");
			return true;
		} 
		else 
		{
			if(this.tfServerPort.getText().length() >= 1) 
			{
				return true;
			} 
			else 
			{
				return false;
			}
		}
	}
	
	// Erase user list text field:
	
	public void clearUserList()
	{
		this.userListModel.removeAllElements();
		this.lblNumConnectedClients.setText("0");
	}

	public void addUser(String user)
	{
		this.userListModel.addElement(user);
	}
	
	// Print message to server log text field:
	
	public synchronized void addMessage(String message, boolean important)
	{
		
		Date date = new Date( );
		SimpleDateFormat dateAndTime = new SimpleDateFormat ("MM/dd/yyyy hh:mm:ss");
		String formatedMessage = null;
		if(important == true)
		{
			formatedMessage = "<div style=\"color: red; font-weight: bold;\">" + dateAndTime.format(date) + " > " + message + "</div>";
		} 
		else 
		{
			formatedMessage = dateAndTime.format(date) + " > " + message + "<br>";
		}
		try {
				editorKit.insertHTML(document, this.document.getLength(), formatedMessage, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			System.err.println("Cannot add new message: " + e.getMessage());
		}
		
		
	}
	
	// Inner classes:
	
	class WindowExit extends WindowAdapter
	{
		@Override
	    public void windowClosing(WindowEvent e)
		{
	        int confirm = JOptionPane.showOptionDialog(ServerWindow.getInstance().getContentPane(), 
	        											 "Are You Sure to Close Networked Chat Server?", 
	        											 "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
	        											 JOptionPane.QUESTION_MESSAGE, null, null, null);
	        if (confirm == 0) 
	        {
	        		System.exit(0);
	        }
	    }
	}
	
	class CheckBox implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox chBox = (JCheckBox)e.getSource();
			if(chBox.isSelected() == true) {
				tfServerPort.setEnabled(false);
			}
			else 
			{
				tfServerPort.setEnabled(true);
			}
			
		}
		
	}
	
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
		
			this.previousValue = e.getAdjustable().getValue();
			this.previousMaximum	= this.currentMaximum;	
		}
    }
}
