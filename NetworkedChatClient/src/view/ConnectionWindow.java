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
 *     This class is GUI for chat Connection Window
 *     that allows the user to connect to a server.
 *
 */

package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ConnectionWindow extends JFrame
{
	private static ConnectionWindow connectionWindowInstance = null;
	
	private JTextField tfUserName;
	private JTextField tfServerIP;
	private JTextField tfServerPort;
	private JTextField pValueTextField;
	private JTextField qValueTextField;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JLabel statusLabel;
	private JLabel statusLabelTwo;
	private JCheckBox generateRSAValuesCheckBox;
	private JPanel connectionPanel;

	//Private constructor to implement the Singleton Pattern
	private ConnectionWindow() 
	{
		SetCrossPlatformLookAndFeel();

		//Initialize data members
		tfUserName = new JTextField();
		tfServerIP = new JTextField();
		tfServerPort = new JTextField();
		pValueTextField = new JTextField();
		qValueTextField = new JTextField();
		btnConnect = new JButton();
		btnDisconnect = new JButton();
		statusLabel = new JLabel();
		statusLabelTwo = new JLabel();
		generateRSAValuesCheckBox = new JCheckBox();

		setFrameParameters();

		createConnectionPanel();

		//Add sub-components to connectionPanel
		addConnectionPanelLabels();
		addConnectionPanelTxtFields();
		addConnectionPanelButtons();
		addConnectionPanelStatusLabel();
	}

	//
	// Return the current instance of ConnectionWindow.
	// If there is not one, instantiate one.
	//
	public static ConnectionWindow getInstance() 
	{
		if(connectionWindowInstance == null) 
		{
			synchronized(ConnectionWindow.class)
			{
				if(connectionWindowInstance == null)
					return ConnectionWindow.connectionWindowInstance = new ConnectionWindow();
				else
					return ConnectionWindow.connectionWindowInstance;
			}
		} 
		else 
		{
			return ConnectionWindow.connectionWindowInstance;
		}
	}

	//
	// Set the state of certain components on the window
	//
	public void setInputState(boolean state) {
		this.tfUserName.setEnabled(state);
		this.tfServerIP.setEnabled(state);
		this.tfServerPort.setEnabled(state);
		this.btnConnect.setEnabled(state);
		this.btnDisconnect.setEnabled(!state);		
	}

	//
	// Set the status labels to the specified message
	//
	public void setStatusLabel(String message) {
		this.statusLabel.setText(message);
		statusLabelTwo.setText("");
	}

	public void setStatusLabelTwo(String message) { this.statusLabelTwo.setText(message); }

	//
	// remove the current error message from the status label.
	//
	public void clearStatusLabel()
	{
		this.statusLabel.setText("");
		this.statusLabelTwo.setText("");
	}
	
	//
	// Add an action listener to the connect button
	//
	public void addBtnConnectActionListener(ActionListener listener)
	{
		this.btnConnect.addActionListener(listener);
	}

	//
	// Add an action listener to the disconnect button
	//
	public void addBtnDisconnectActionListener(ActionListener listener)
	{
		this.btnDisconnect.addActionListener(listener);
	}

	//
	// Add an action listener the generate RSA checkbox
	//
	public void addGenerateRSACheckBoxListener(ActionListener listener)
	{
		generateRSAValuesCheckBox.addActionListener(listener);
	}

	//
	// Set the state of the p and q input fields
	//
	public void setRSAInputEnabled(boolean value)
	{
		pValueTextField.setEnabled(value);
		qValueTextField.setEnabled(value);
	}

	//
	// Returns the status of the RSA checkbox
	//
	public boolean generateRSAValuesIsSelected()
	{
		return generateRSAValuesCheckBox.isSelected();
	}

	//Make sure the look of the program is the same on different platforms
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
	// Set parameters associated with the windows main JFrame
	//
	private void setFrameParameters()
	{
		setType(Type.POPUP);
		setResizable(false);
		setAlwaysOnTop(true);

		setTitle("Chat Connection");
		setBounds(0,0, 400, 460);
		getContentPane().setLayout(null);
	}

	//
	// Add the labels that go with the text fields to the connection panel.
	//
	private void addConnectionPanelLabels()
	{
		JLabel lblServerIp = new JLabel("Server IP:");
		lblServerIp.setBounds(17, 51, 90, 16);
		connectionPanel.add(lblServerIp);

		JLabel lblServerPort = new JLabel("Server Port:");
		lblServerPort.setBounds(17, 81, 90, 16);
		connectionPanel.add(lblServerPort);

		JLabel lblYourName = new JLabel("Your Name:");
		lblYourName.setBounds(17, 17, 90, 22);
		connectionPanel.add(lblYourName);

		JLabel RSA_HeadingLabel_1 = new JLabel("Select two prime numbers with a product greater");
		RSA_HeadingLabel_1.setBounds(17,145,350,16);
		connectionPanel.add(RSA_HeadingLabel_1);

		JLabel RSA_HeadingLabel_2 = new JLabel("than 72,057,594,037,927,936 for the RSA encryption.");
		RSA_HeadingLabel_2.setBounds(17,162,350,16);
		connectionPanel.add(RSA_HeadingLabel_2);

		JLabel pValueLabel = new JLabel("p Value:");
		pValueLabel.setBounds(17,200,90,16);
		connectionPanel.add(pValueLabel);

		JLabel qValueLabel = new JLabel("q Value:");
		qValueLabel.setBounds(17,230,90,16);
		connectionPanel.add(qValueLabel);
	}

	//
	// Add the text fields to collect information from the user
	// to the connection panel.
	//
	private void addConnectionPanelTxtFields()
	{
		tfUserName.setBounds(108, 15, 233, 26);
		connectionPanel.add(tfUserName);

		tfServerIP.setBounds(108, 46, 233, 26);
		connectionPanel.add(tfServerIP);

		tfServerPort.setBounds(108, 76, 64, 26);
		tfServerPort.setColumns(5);
		connectionPanel.add(tfServerPort);

		pValueTextField.setBounds(108, 195, 125, 26);
		connectionPanel.add(pValueTextField);

		qValueTextField.setBounds(108, 225, 125, 26);
		connectionPanel.add(qValueTextField);

		generateRSAValuesCheckBox.setText("Auto generate values");
		generateRSAValuesCheckBox.setBounds(17,260,250,25);
		generateRSAValuesCheckBox.setFocusable(false);
		connectionPanel.add(generateRSAValuesCheckBox);
	}

	//
	// Add the connect and disconnect buttons to the connection pane.
	// The disconnect button starts in a disabled state.
	//
	private void addConnectionPanelButtons()
	{
		btnConnect.setText("Connect");
		btnConnect.setBounds(67, 305, 117, 29);
		connectionPanel.add(btnConnect);

		btnDisconnect.setText("Disconnect");
		btnDisconnect.setBounds(196, 305, 117, 29);
		btnDisconnect.setEnabled(false);
		connectionPanel.add(btnDisconnect);
	}

	//
	// Add a status label to the connection panel to display error message.
	//
	private void addConnectionPanelStatusLabel()
	{
		statusLabel.setBounds(0, 5, 400, 20);
		statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setForeground(Color.red);
		getContentPane().add(statusLabel);

		statusLabelTwo.setBounds(0, 25, 400, 20);
		statusLabelTwo.setFont(new Font("Arial", Font.BOLD, 12));
		statusLabelTwo.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabelTwo.setForeground(Color.red);
		getContentPane().add(statusLabelTwo);
	}

	//
	// Create a connection panel that will hold all of the components that
	// allow the user to connect to a server.
	//
	private void createConnectionPanel()
	{
		connectionPanel = new JPanel();
		connectionPanel.setBounds(20, 45, 350, 340);
		getContentPane().add(connectionPanel);
		connectionPanel.setLayout(null);
	}

	//
	// Getter methods:
	//

	public String getUserName()
	{
		return this.tfUserName.getText();
	}

	public String getServerIP()
	{
		return this.tfServerIP.getText();
	}

	public int getServerPort()
	{
		return Integer.parseInt(this.tfServerPort.getText());
	}

	public long getPValue() {
		try {
			return Long.parseLong(pValueTextField.getText());
		} catch (Exception e) {
			return 0;
		}
	}

	public long getQValue()
	{
		try {
			return Long.parseLong(qValueTextField.getText());
		} catch (Exception e) {
			return 0;
		}
	}

}
