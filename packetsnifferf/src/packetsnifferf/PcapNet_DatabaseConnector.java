package packetsnifferf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class PcapNet_DatabaseConnector extends JFrame {
    /**
	 * 
	 */
	public String fileName = "CaptureData.txt";                   // file save from txt
	private static final long serialVersionUID = -9063047328278846825L;   //auto generated
	private JTextField usernameField, passwordField, databaseField;
    private JButton connectButton, createDatabaseButton;

    public PcapNet_DatabaseConnector() {                           //ui ux for the database
    	setResizable(false);
        setTitle("Database Connection");
        setSize(316, 233);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 0, 140, 41);
        usernameField = new JTextField();
        usernameField.setBounds(150, 0, 150, 41);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 41, 140, 41);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 41, 150, 41);
        JLabel databaseLabel = new JLabel("Database:");
        databaseLabel.setBounds(10, 82, 140, 41);
        databaseField = new JTextField();
        databaseField.setBounds(150, 82, 150, 41);

        connectButton = new JButton("Connect");
        connectButton.setBounds(0, 123, 150, 41);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {           //call the button for function
                try {
					connectToDatabase();
				} catch (PcapNet_CustomExeption e1) {
					
					e1.printStackTrace();
				}
            }
        });

        createDatabaseButton = new JButton("Create Database");              //create ui
        createDatabaseButton.setBounds(150, 123, 150, 41);
        createDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createDatabase();
            }
        });
        getContentPane().setLayout(null);

        getContentPane().add(usernameLabel);
        getContentPane().add(usernameField);
        getContentPane().add(passwordLabel);
        getContentPane().add(passwordField);
        getContentPane().add(databaseLabel);
        getContentPane().add(databaseField);
        getContentPane().add(connectButton);
        getContentPane().add(createDatabaseButton);
        
        JButton B_QUIT = new JButton("Quit");
        B_QUIT.setBounds(107, 165, 89, 23);
        getContentPane().add(B_QUIT);
        B_QUIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        setVisible(true);
    }

    public void connectToDatabase() throws PcapNet_CustomExeption{   // connects to database and custom exception throw
        String username = usernameField.getText();
        String password = passwordField.getText();         //input
        String database = databaseField.getText();

        try {          //ERROR HANDLE
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database, username, password);   //link to database
            JOptionPane.showMessageDialog(this, "Connected to database successfully!");
            createTables(connection);
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {     //takes input
            	
            	 
                // Connect to the database using DatabaseGUI
            	 //if (connection != null) {
                     String line;
                     while ((line = br.readLine()) != null) {
                    	 System.out.println(line);
                         if (line.contains("UDP")) {                   // from text to sql
                             processUDPPacket(line, connection);
                         } else if (line.contains("ARP REQUEST")) {
                             processARPRequest(line, connection);           //UDP and ARP are connection type
                         }
                         else
                         {
                        	 processELSEPacket(line, connection);
                         }
                     }
                // }
             } catch (IOException | SQLException e) {      //input and output time if throws error
                 e.printStackTrace();
             } finally {
                 if (connection != null) {
                     try {
                         connection.close();
                     } catch (SQLException ex) {
                         ex.printStackTrace();
                     }
                 }
             }
        } catch (SQLException ex) {
        	
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + ex.getMessage());
            throw new PcapNet_CustomExeption("Failed to connect to database: " + ex.getMessage());//custom exception
        }
}
    private void createDatabase() {                         
        String username = usernameField.getText();
        String password = passwordField.getText();
        String database = databaseField.getText();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", username, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE " + database);
            JOptionPane.showMessageDialog(this, "Database created successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to create database: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PcapNet_DatabaseConnector();
            }
        });
    }
    private void createTables(Connection connection) throws SQLException {
        String createCapturedPacketsTable = "CREATE TABLE IF NOT EXISTS captured_packets (timestamp VARCHAR(255) , source_ip VARCHAR(255), destination_ip VARCHAR(255), protocol VARCHAR(255))";
        String createARPRequestsTable = "CREATE TABLE IF NOT EXISTS arp_requests (source_mac VARCHAR(255), source_ip VARCHAR(255), destination_mac VARCHAR(255), destination_ip VARCHAR(255))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createCapturedPacketsTable);
            statement.executeUpdate(createARPRequestsTable);
        }
    }
    private void processUDPPacket(String line, Connection connection) throws SQLException {
        String[] parts = line.split(" ");
        if (parts.length >= 8) {
            String timestamp = parts[0].split(":")[0];
            String sourceIP = parts[1].split("->")[0];
            String destinationIP = parts[1].split("->")[1];
            String protocol = parts[2];
            System.out.println(timestamp);
            /*String restOfData = ""; // Collecting the rest of the data into a single string
            for (int i = 8; i < parts.length; i++) {
                restOfData += parts[i] + " ";
            }
			*/
            String sql = "INSERT INTO captured_packets (timestamp, source_ip, destination_ip, protocol) VALUES (?, ?, ?, ?)";   //statement
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, timestamp);
            statement.setString(2, sourceIP);
            statement.setString(3, destinationIP);
            statement.setString(4, protocol);
            //statement.setString(5, restOfData.trim()); // Trimming to remove extra whitespace
            statement.executeUpdate();
            statement.close();
        }
    }

    private void processARPRequest(String line, Connection connection) throws SQLException {
        String[] parts = line.split(" ");
        if (parts.length >= 7) {
            String sourceMAC = parts[2];
            String sourceIP = parts[3].split("\\(|\\)")[1];
            String destinationMAC = parts[5];
            String destinationIP = parts[6].split("\\(|\\)")[1];

            /*String restOfData = ""; // Collecting the rest of the data into a single string
            for (int i = 7; i < parts.length; i++) {
                restOfData += parts[i] + " ";
            }
			*/
            String sql = "INSERT INTO arp_requests (source_mac, source_ip, destination_mac, destination_ip) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, sourceMAC);
            statement.setString(2, sourceIP);
            statement.setString(3, destinationMAC);
            statement.setString(4, destinationIP);
            //statement.setString(5, restOfData.trim()); // Trimming to remove extra whitespace
            statement.executeUpdate();
            statement.close();
        }
    }
    private void processELSEPacket(String line, Connection connection) throws SQLException {
        String[] parts = line.split(" ");
        if (parts.length >= 8) {
            String timestamp = parts[0].split(":")[0];
            String sourceIP = parts[1].split("->")[0];
            String destinationIP = parts[1].split("->")[1];
            String protocol = parts[2];
            System.out.println(timestamp);
           /* String restOfData = ""; // Collecting the rest of the data into a single string
            for (int i = 8; i < parts.length; i++) {
                restOfData += parts[i] + " ";
            }
			*/
            String sql = "INSERT INTO captured_packets (timestamp, source_ip, destination_ip, protocol) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, timestamp);
            statement.setString(2, sourceIP);
            statement.setString(3, destinationIP);
            statement.setString(4, protocol);
           // statement.setString(5, restOfData.trim()); // Trimming to remove extra whitespace
            statement.executeUpdate();
            statement.close();
        }
    }
    

}