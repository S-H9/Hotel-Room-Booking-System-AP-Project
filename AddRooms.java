package a;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class AddRooms {

    public static void AddRoomTab(JTabbedPane tabbedPane) {
        // Create panel with SpringLayout
        JPanel addRoomsPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        addRoomsPanel.setLayout(layout);

        // Room Number
        JLabel roomNumberLabel = new JLabel("Room Number:");
        JTextField roomNumberField = new JTextField(20);
        layout.putConstraint(SpringLayout.WEST, roomNumberLabel, 20, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, roomNumberLabel, 20, SpringLayout.NORTH, addRoomsPanel);
        layout.putConstraint(SpringLayout.WEST, roomNumberField, 150, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, roomNumberField, 20, SpringLayout.NORTH, addRoomsPanel);

        addRoomsPanel.add(roomNumberLabel);
        addRoomsPanel.add(roomNumberField);

        // Room Type
        JLabel roomTypeLabel = new JLabel("Room Type:");
        JComboBox<String> roomTypeComboBox = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        layout.putConstraint(SpringLayout.WEST, roomTypeLabel, 20, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, roomTypeLabel, 20, SpringLayout.SOUTH, roomNumberField);
        layout.putConstraint(SpringLayout.WEST, roomTypeComboBox, 150, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, roomTypeComboBox, 20, SpringLayout.SOUTH, roomNumberField);

        addRoomsPanel.add(roomTypeLabel);
        addRoomsPanel.add(roomTypeComboBox);

        // Price
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(20);
        layout.putConstraint(SpringLayout.WEST, priceLabel, 20, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, priceLabel, 20, SpringLayout.SOUTH, roomTypeComboBox);
        layout.putConstraint(SpringLayout.WEST, priceField, 150, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, priceField, 20, SpringLayout.SOUTH, roomTypeComboBox);

        addRoomsPanel.add(priceLabel);
        addRoomsPanel.add(priceField);

        // Availability
        JLabel availabilityLabel = new JLabel("Availability:");
        JRadioButton availableYes = new JRadioButton("True");
        JRadioButton availableNo = new JRadioButton("False");
        ButtonGroup availabilityGroup = new ButtonGroup();
        availabilityGroup.add(availableYes);
        availabilityGroup.add(availableNo);

        layout.putConstraint(SpringLayout.WEST, availabilityLabel, 20, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, availabilityLabel, 20, SpringLayout.SOUTH, priceField);
        layout.putConstraint(SpringLayout.WEST, availableYes, 150, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, availableYes, 20, SpringLayout.SOUTH, priceField);
        layout.putConstraint(SpringLayout.WEST, availableNo, 220, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, availableNo, 20, SpringLayout.SOUTH, priceField);

        addRoomsPanel.add(availabilityLabel);
        addRoomsPanel.add(availableYes);
        addRoomsPanel.add(availableNo);

        // Submit Button
        JButton submitButton = new JButton("Submit");
        layout.putConstraint(SpringLayout.WEST, submitButton, 20, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, submitButton, 30, SpringLayout.SOUTH, availableYes);

        addRoomsPanel.add(submitButton);

        // Delete Button
        JButton deleteButton = new JButton("Delete");
        layout.putConstraint(SpringLayout.WEST, deleteButton, 120, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, deleteButton, 30, SpringLayout.SOUTH, availableYes);

        addRoomsPanel.add(deleteButton);

        // Delete All Button
        JButton deleteAllButton = new JButton("Delete All");
        layout.putConstraint(SpringLayout.WEST, deleteAllButton, 220, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, deleteAllButton, 30, SpringLayout.SOUTH, availableYes);

        addRoomsPanel.add(deleteAllButton);

        // Table to display data
        JTable roomsTable = new JTable(new DefaultTableModel(
                new Object[]{"Room Number", "Room Type", "Price", "Availability"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        layout.putConstraint(SpringLayout.WEST, scrollPane, 20, SpringLayout.WEST, addRoomsPanel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 30, SpringLayout.SOUTH, submitButton);
        layout.putConstraint(SpringLayout.EAST, scrollPane, -20, SpringLayout.EAST, addRoomsPanel);
        layout.putConstraint(SpringLayout.SOUTH, scrollPane, -20, SpringLayout.SOUTH, addRoomsPanel);

        addRoomsPanel.add(scrollPane);

        // Add action listener to the Submit button
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String roomNumber = roomNumberField.getText();
                String roomType = (String) roomTypeComboBox.getSelectedItem();
                String price = priceField.getText();
                Boolean availability = availableYes.isSelected();

                String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
                String username = "root";
                String password = "";

                String sql = "INSERT INTO rooms (room_number, room_type, price, availability) VALUES (?, ?, ?, ?)";

                try (Connection connection = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setString(1, roomNumber);
                    statement.setString(2, roomType);
                    statement.setString(3, price);
                    statement.setBoolean(4, availability);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Room details added successfully!");
                    }

                    refreshTableData(roomsTable);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        // Add action listener to the Delete button
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = roomsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a room to delete.");
                    return;
                }

                String roomNumber = (String) roomsTable.getValueAt(selectedRow, 0);

                String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
                String username = "root";
                String password = "";

                String sql = "DELETE FROM rooms WHERE room_number = ?";

                try (Connection connection = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setString(1, roomNumber);

                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(null, "Room deleted successfully!");
                    }

                    refreshTableData(roomsTable);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        // Add action listener to the Delete All button
        deleteAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete all rooms?",
                        "Confirm Delete All",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
                    String username = "root";
                    String password = "";

                    String sql = "DELETE FROM rooms";

                    try (Connection connection = DriverManager.getConnection(url, username, password);
                         PreparedStatement statement = connection.prepareStatement(sql)) {

                        int rowsDeleted = statement.executeUpdate();
                        JOptionPane.showMessageDialog(null, rowsDeleted + " room(s) deleted successfully!");

                        refreshTableData(roomsTable);

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        // Initial data load
        refreshTableData(roomsTable);

        // Add panel to tab
        tabbedPane.addTab("Add Room", addRoomsPanel);
    }

    public static void refreshTableData(JTable table) {
        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String username = "root";
        String password = "";
        String sql = "SELECT room_number, room_type, price, availability FROM rooms";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                String roomNumber = resultSet.getString("room_number");
                String roomType = resultSet.getString("room_type");
                String price = resultSet.getString("price");
                String availability = resultSet.getBoolean("availability") ? "True" : "False";
                model.addRow(new Object[]{roomNumber, roomType, price, availability});
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Hotel Management System");
                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JTabbedPane tabbedPane = new JTabbedPane();

                AddRoomTab(tabbedPane);

                JPanel otherTabPanel = new JPanel();
                otherTabPanel.add(new JLabel("Other Functionality"));
                tabbedPane.addTab("Other Tab", otherTabPanel);

                frame.getContentPane().add(tabbedPane);
                frame.setVisible(true);
            }
        });
    }
}
