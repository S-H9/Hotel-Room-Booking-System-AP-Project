package omar1;
import javax.swing.*;
import java.awt.event.*;
	import java.sql.*;

	public class HotelBookingSystem  {
	    // Database Connection
	    private static Connection connect() {
	    	String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
	    	String username = "root";
	        String password = "";
	        Connection conn = null;
	        try {
	            conn =DriverManager.getConnection(url, username, password);
	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(null, "Database Connection Failed: " + e.getMessage());
	        }
	        return conn;
	    }

	    // GUI Components
	    public static void main(String[] args) {
	        JFrame frame = new JFrame("Hotel Room Booking System");
	        frame.setSize(500, 400);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        JPanel panel = new JPanel();
	        frame.add(panel);
	        placeComponents(panel);

	        frame.setVisible(true);
	    }

	    private static void placeComponents(JPanel panel) {
	        panel.setLayout(null);

	        // Labels
	        JLabel guestLabel = new JLabel("Guest Name:");
	        guestLabel.setBounds(10, 20, 100, 25);
	        panel.add(guestLabel);

	        JLabel roomLabel = new JLabel("Room Number:");
	        roomLabel.setBounds(10, 50, 100, 25);
	        panel.add(roomLabel);

	        JLabel checkInLabel = new JLabel("Check-In Date:");
	        checkInLabel.setBounds(10, 80, 100, 25);
	        panel.add(checkInLabel);

	        JLabel checkOutLabel = new JLabel("Check-Out Date:");
	        checkOutLabel.setBounds(10, 110, 100, 25);
	        panel.add(checkOutLabel);

	        // Text Fields
	        JTextField guestText = new JTextField(20);
	        guestText.setBounds(120, 20, 200, 25);
	        panel.add(guestText);

	        JTextField roomText = new JTextField(20);
	        roomText.setBounds(120, 50, 200, 25);
	        panel.add(roomText);

	        JTextField checkInText = new JTextField(20);
	        checkInText.setBounds(120, 80, 200, 25);
	        panel.add(checkInText);

	        JTextField checkOutText = new JTextField(20);
	        checkOutText.setBounds(120, 110, 200, 25);
	        panel.add(checkOutText);

	        // Buttons
	        JButton addButton = new JButton("Add Booking");
	        addButton.setBounds(10, 150, 150, 25);
	        panel.add(addButton);

	        JButton viewButton = new JButton("View Bookings");
	        viewButton.setBounds(170, 150, 150, 25);
	        panel.add(viewButton);

	        // Add Booking Action
	        addButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                String guestName = guestText.getText();
	                String roomNumber = roomText.getText();
	                String checkInDate = checkInText.getText();
	                String checkOutDate = checkOutText.getText();

	                if (guestName.isEmpty() || roomNumber.isEmpty() || checkInDate.isEmpty() || checkOutDate.isEmpty()) {
	                    JOptionPane.showMessageDialog(null, "Please fill all fields.");
	                } else {
	                    try (Connection conn = connect();
	                         PreparedStatement pstmt = conn.prepareStatement(
	                                 "INSERT INTO booking(guest_id, room_id, check_in_date, check_out_date) VALUES (?, ?, ?, ?)")) {
	                        pstmt.setString(1, guestName);
//	                        pstmt.setString(2, "N/A"); // Placeholder for contact
	                        pstmt.setString(2, roomNumber);
	                        pstmt.setString(3, checkInDate);
	                        pstmt.setString(4, checkOutDate);
	                        pstmt.executeUpdate();
	                        JOptionPane.showMessageDialog(null, "Booking Added Successfully!");
	                    } catch (SQLException ex) {
	                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
	                    }
	                }
	            }
	        });

	        // View Bookings Action
	        viewButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                try (Connection conn = connect();
	                     Statement stmt = conn.createStatement();
	                     ResultSet rs = stmt.executeQuery("SELECT * FROM booking")) {
	                    StringBuilder bookings = new StringBuilder();
	                    while (rs.next()) {
	                        bookings.append("ID: ").append(rs.getInt("guest_id"))
//	                                .append(", Guest: ").append(rs.getString("guest_name"))
	                                .append(", Room: ").append(rs.getString("room_id"))
	                                .append(", Check-In: ").append(rs.getString("check_in_date"))
	                                .append(", Check-Out: ").append(rs.getString("check_out_date"))
	                                .append("\n");
	                    }
	                    JOptionPane.showMessageDialog(null, bookings.toString(), "Bookings", JOptionPane.INFORMATION_MESSAGE);
	                } catch (SQLException ex) {
	                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
	                }
	            }
	        });
	    }
	}



 


