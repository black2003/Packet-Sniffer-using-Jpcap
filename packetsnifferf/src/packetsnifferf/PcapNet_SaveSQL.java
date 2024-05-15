package packetsnifferf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PcapNet_SaveSQL 
{
    public void SavSQL() 
    {
        String fileName = "CaptureData.txt";
        
        // Database connection details
        //String username = "your_username";
        //String password = "your_password";
        //String database = "your_database";
        PcapNet_DatabaseConnector databaseGUI = new PcapNet_DatabaseConnector();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            // Connect to the database using DatabaseGUI
        	
            //Connection connection = databaseGUI.connectToDatabase();

            if (connection != null) {
                String line;
                while ((line = br.readLine()) != null) {
                    // Your packet processing logic here
                	if (line.contains("UDP")) {
                        // Parse UDP packet information
                        String[] parts = line.split(" ");
                        String timestamp = parts[0].split(":")[0];
                        String sourceIP = parts[1].split("->")[0];
                        String destinationIP = parts[1].split("->")[1];
                        String protocol = parts[7];

                        // Insert UDP packet data into the database
                        String sql = "INSERT INTO captured_packets (timestamp, source_ip, destination_ip, protocol) VALUES (?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, timestamp);
                        statement.setString(2, sourceIP);
                        statement.setString(3, destinationIP);
                        statement.setString(4, protocol);
                        statement.executeUpdate();
                        statement.close();
                    } else if (line.contains("ARP REQUEST")) {
                        // Parse ARP request information
                        String[] parts = line.split(" ");
                        String sourceMAC = parts[2];
                        String sourceIP = parts[3].split("\\(|\\)")[1];
                        String destinationMAC = parts[5];
                        String destinationIP = parts[6].split("\\(|\\)")[1];

                        // Insert ARP request data into the database
                        String sql = "INSERT INTO arp_requests (source_mac, source_ip, destination_mac, destination_ip) VALUES (?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, sourceMAC);
                        statement.setString(2, sourceIP);
                        statement.setString(3, destinationMAC);
                        statement.setString(4, destinationIP);
                        statement.executeUpdate();
                        statement.close();
                    }
                }

                // Close the connection after processing packets
                connection.close();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}