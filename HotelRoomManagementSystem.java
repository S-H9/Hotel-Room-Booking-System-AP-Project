package APFinalProject;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class HotelRoomManagementSystem {

	
	
	
	
	public static void main(String[] args) {
		
		initializeDatabase();
		
		JFrame frame = new JFrame("Hotel Room Booking System");
		frame.setSize(1050, 770);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		
		RoomManagement rooms = new RoomManagement();
		GuestMangement guests = new GuestMangement();
		BookingManagement booking = new  BookingManagement();
		Reporting report = new Reporting();
		
		JTabbedPane hotel = new JTabbedPane();

		JTabbedPane reportTab = new JTabbedPane();
		JTabbedPane roomsTab = new JTabbedPane();
		JTabbedPane guestTab = new JTabbedPane();
		JTabbedPane bookTab = new JTabbedPane();
		
//		rooms.
		
		
		hotel.addTab("Room Management",rooms.createMainPanel());
		hotel.addTab("Guest Management",guests.createGuestPanel());
		hotel.addTab("Booking Management", booking);

		report.ReportingTab(hotel);;

		
		frame.add(hotel);
//		frame.add(guestTab);
//		frame.add(bookTab);
//		frame.add(reportTab);

		
		
		frame.setVisible(true);
		
		
		
	}
	
	
	private static void initializeDatabase() {
	    String url = "jdbc:mysql://localhost:3306/";
	    String username = "root";
	    String password = "";
	    
	    try {
	        // First, create database if it doesn't exist
	        try (Connection conn = DriverManager.getConnection(url, username, password);
	             Statement stmt = conn.createStatement()) {
	            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS hotelbookingsystem");
	        }
	        
	        // Now connect to the database and create tables
	        url = "jdbc:mysql://localhost:3306/hotelbookingsystem";
	        try (Connection conn = DriverManager.getConnection(url, username, password);
	             Statement stmt = conn.createStatement()) {
	            
	            // Create rooms table
	            stmt.executeUpdate(
	                "CREATE TABLE IF NOT EXISTS rooms (" +
	                "room_id int(11) NOT NULL AUTO_INCREMENT," +
	                "room_number varchar(10) DEFAULT NULL," +
	                "room_type varchar(20) DEFAULT NULL," +
	                "price decimal(10,2) DEFAULT NULL," +
	                "availability tinyint(1) DEFAULT NULL," +
	                "PRIMARY KEY (room_id)" +
	                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci"
	            );
	            
	            // Create guests table
	            stmt.executeUpdate(
	                "CREATE TABLE IF NOT EXISTS guests (" +
	                "guest_id int(11) NOT NULL AUTO_INCREMENT," +
	                "name varchar(255) DEFAULT NULL," +
	                "contact_info varchar(255) DEFAULT NULL," +
	                "id_proof varchar(255) DEFAULT NULL," +
	                "PRIMARY KEY (guest_id)" +
	                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci"
	            );
	            
	            // Create booking table
	            stmt.executeUpdate(
	                "CREATE TABLE IF NOT EXISTS booking (" +
	                "booking_id int(11) NOT NULL AUTO_INCREMENT," +
	                "room_id int(11) DEFAULT NULL," +
	                "guest_id int(11) DEFAULT NULL," +
	                "check_in_date date DEFAULT NULL," +
	                "check_out_date date DEFAULT NULL," +
	                "PRIMARY KEY (booking_id)," +
	                "KEY guest_id_index_booking (guest_id)," +
	                "CONSTRAINT booking_ibfk_1 FOREIGN KEY (guest_id) REFERENCES guests (guest_id) " +
	                "ON DELETE CASCADE ON UPDATE CASCADE" +
	                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci"
	            );
	            
	            // Insert sample data
	            stmt.executeUpdate(
	                "INSERT IGNORE INTO rooms (room_id, room_number, room_type, price, availability) " +
	                "VALUES (1, '101', 'Single', 100.00, 0), (2, '102', 'Double', 200.00, 1)"
	            );
	            
	            stmt.executeUpdate(
	                "INSERT IGNORE INTO guests (guest_id, name, contact_info, id_proof) " +
	                "VALUES (2, 'bou', 'a@a.a', '1112')"
	            );
	            
	            stmt.executeUpdate(
	                "INSERT IGNORE INTO booking (booking_id, room_id, guest_id, check_in_date, check_out_date) " +
	                "VALUES (12, 101, 2, '1111-11-11', '1111-12-12'), " +
	                "(13, 101, 2, '1444-12-01', '1444-12-06')"
	            );
	            
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error initializing database: " + e.getMessage());
	    }
	}
	
	
}
