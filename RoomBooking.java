package AP_Project;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.sql.*;

public class RoomBooking {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Room Booking");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField guestNameField = new JTextField();
        JTextField checkInField = new JTextField();
        JTextField checkOutField = new JTextField();
        JComboBox<String> roomSelection = new JComboBox<>();
        JLabel totalPriceLabel = new JLabel("Total Price: $0.00");
        JButton bookButton = new JButton("Book Room");

        panel.add(new JLabel("Guest Name:"));
        panel.add(guestNameField);
        panel.add(new JLabel("Check-In Date (YYYY-MM-DD):"));
        panel.add(checkInField);
        panel.add(new JLabel("Check-Out Date (YYYY-MM-DD):"));
        panel.add(checkOutField);
        panel.add(new JLabel("Select Room:"));
        panel.add(roomSelection);
        panel.add(totalPriceLabel);
        panel.add(new JLabel());
        panel.add(bookButton);

        frame.add(panel, BorderLayout.NORTH);
        loadAvailableRooms(roomSelection);

        bookButton.addActionListener((ActionEvent e) -> {
            String guestName = guestNameField.getText();
            String checkIn = checkInField.getText();
            String checkOut = checkOutField.getText();
            String roomNumber = (String) roomSelection.getSelectedItem();

            if (guestName.isEmpty() || checkIn.isEmpty() || checkOut.isEmpty() || roomNumber == null) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            bookRoom(guestName, checkIn, checkOut, roomNumber);
        });

        frame.setVisible(true);
    }

    private static void loadAvailableRooms(JComboBox<String> roomSelection) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT room_number FROM rooms WHERE status = 'Available'");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roomSelection.addItem(rs.getString("room_number"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading available rooms.");
        }
    }

    private static void bookRoom(String guestName, String checkIn, String checkOut, String roomNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO bookings (guest_name, check_in_date, check_out_date, room_number, status) VALUES (?, ?, ?, ?, 'Booked')");
             PreparedStatement updateRoom = conn.prepareStatement("UPDATE rooms SET status = 'Booked' WHERE room_number = ?")) {
            
            stmt.setString(1, guestName);
            stmt.setString(2, checkIn);
            stmt.setString(3, checkOut);
            stmt.setString(4, roomNumber);
            stmt.executeUpdate();
            
            updateRoom.setString(1, roomNumber);
            updateRoom.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Room booked successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error booking room.");
        }
    }
}
