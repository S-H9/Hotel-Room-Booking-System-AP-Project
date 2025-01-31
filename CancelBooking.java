package AP_Project;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CancelBooking {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cancel Room Booking");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel searchLabel = new JLabel("Search Booking:");
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel Booking");
        cancelButton.setEnabled(false);

        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(cancelButton);

        String[] columns = {"Booking ID", "Guest Name", "Room Number", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchBookings(searchField.getText(), model));

        cancelButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int bookingID = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel this booking?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    cancelBooking(bookingID);
                    model.setValueAt("Canceled", selectedRow, 3);
                    JOptionPane.showMessageDialog(frame, "Booking Canceled Successfully.");
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> cancelButton.setEnabled(table.getSelectedRow() != -1));

        frame.setVisible(true);
    }

    private static void searchBookings(String searchTerm, DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT booking_id, guest_name, room_number, status FROM bookings WHERE booking_id LIKE ? OR guest_name LIKE ? OR room_number LIKE ?")) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            stmt.setString(3, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("booking_id"), rs.getString("guest_name"), rs.getString("room_number"), rs.getString("status")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching data from database.");
        }
    }

    private static void cancelBooking(int bookingID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("UPDATE bookings SET status = 'Canceled' WHERE booking_id = ?")) {
            stmt.setInt(1, bookingID);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating booking status.");
        }
    }
}
