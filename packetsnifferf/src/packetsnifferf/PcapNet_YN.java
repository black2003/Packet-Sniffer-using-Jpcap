package packetsnifferf;

import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class PcapNet_YN extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JLabel lblNewLabel = new JLabel("Do You Want To Save To Database ?");
	JButton btnNewButton = new JButton("YES");
	JButton btnNewButton_1 = new JButton("NO");
	/**
	 * Launch the application.
	 */
	
	public PcapNet_YN() {
        super("Database Connection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(359, 135);
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        Display();
        
    }
	
	public void Display() {
		
		contentPane.setLayout(null);
		
		
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 323, 14);
		contentPane.add(lblNewLabel);
		
		
		btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectToDatabase();
            }
        });
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNewButton.setBounds(10, 59, 89, 23);
		contentPane.add(btnNewButton);
		
		
		btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNewButton_1.setBounds(244, 59, 89, 23);
		contentPane.add(btnNewButton_1);
	}
	 private void connectToDatabase() {
	        // Your database connection code here
	        // This method will be called when the "YES" button is pressed
	        System.out.println("Connecting to database...");
	        PcapNet_DatabaseConnector Dtcntr = new PcapNet_DatabaseConnector();
	    }

	 private void closeWindow() {
	        // This method will be called when the "NO" button is pressed
	        System.out.println("Closing window...");
	        dispose(); // Close the window
	    }
}
