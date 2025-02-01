package APFinalProject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class BookingManagement extends JPanel {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/HotelBookingSystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private JComboBox<String> roomSelection;
    private JTextField checkOutField, guestNameField, checkInField;
    private DefaultTableModel tableModel;
    private JTable table;

    public BookingManagement() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        guestNameField = new JTextField();
        checkInField = new JTextField();
        checkOutField = new JTextField();
        roomSelection = new JComboBox<>();
        JButton bookButton = new JButton("Book Room");
        JButton cancelButton = new JButton("Cancel Booking");

        inputPanel.add(new JLabel("Guest Name:"));
        inputPanel.add(guestNameField);
        inputPanel.add(new JLabel("Check-In Date (YYYY-MM-DD):"));
        inputPanel.add(checkInField);
        inputPanel.add(new JLabel("Check-Out Date (YYYY-MM-DD):"));
        inputPanel.add(checkOutField);
        inputPanel.add(new JLabel("Select Room:"));
        inputPanel.add(roomSelection);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(cancelButton);
        buttonPanel.add(bookButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        loadAvailableRooms();

        bookButton.addActionListener(this::bookRoom);
        cancelButton.addActionListener(this::cancelBooking);

        String[] columnNames = {"ID", "Room", "Guest", "Check-in", "Check-out"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        loadBookingData();
    }

    private void loadAvailableRooms() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT room_number FROM rooms WHERE availability = 1");
             ResultSet rs = stmt.executeQuery()) {

            roomSelection.removeAllItems();
            while (rs.next()) {
                roomSelection.addItem(rs.getString("room_number"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading available rooms.");
        }
    }

    private void bookRoom(ActionEvent e) {
        String guestName = guestNameField.getText();
        String checkIn = checkInField.getText();
        String checkOut = checkOutField.getText();
        String roomNumber = (String) roomSelection.getSelectedItem();

        if (guestName.isEmpty() || checkIn.isEmpty() || checkOut.isEmpty() || roomNumber == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO booking (guest_id, check_in_date, check_out_date, room_id) VALUES (?, ?, ?, ?)");
             PreparedStatement updateRoom = conn.prepareStatement("UPDATE rooms SET availability = 0 WHERE room_number = ?")) {

            stmt.setString(1, guestName);
            stmt.setString(2, checkIn);
            stmt.setString(3, checkOut);
            stmt.setString(4, roomNumber);
            stmt.executeUpdate();

            updateRoom.setString(1, roomNumber);
            updateRoom.executeUpdate();

            JOptionPane.showMessageDialog(this, "Room booked successfully!");
            refreshPage();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error booking room.");
        }
    }

    private void cancelBooking(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String roomNumber = tableModel.getValueAt(selectedRow, 1).toString();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement deleteBooking = conn.prepareStatement("DELETE FROM booking WHERE room_id = ?");
             PreparedStatement updateRoom = conn.prepareStatement("UPDATE rooms SET availability = 1 WHERE room_number = ?")) {

            deleteBooking.setString(1, roomNumber);
            deleteBooking.executeUpdate();

            updateRoom.setString(1, roomNumber);
            updateRoom.executeUpdate();

            JOptionPane.showMessageDialog(this, "Booking canceled successfully!");
            refreshPage();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error canceling booking.");
        }
    }

    private void refreshPage() {
        roomSelection.removeAllItems();
        loadAvailableRooms();
        guestNameField.setText("");
        checkInField.setText("");
        checkOutField.setText("");
        loadBookingData();
    }

    private void loadBookingData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT booking_id, room_id, guest_id, check_in_date, check_out_date FROM booking")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("booking_id"), rs.getInt("room_id"), rs.getInt("guest_id"), rs.getString("check_in_date"), rs.getString("check_out_date")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }
}
