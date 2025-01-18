import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class AddRooms2 {

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

                // Database connection variables
                String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
                String username = "root";
                String password = "";

                // SQL query to insert data
                String sql = "INSERT INTO rooms (room_number, room_type, price, availability) VALUES (?, ?, ?, ?)";

                try (Connection connection = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    // Set parameters
                    statement.setString(1, roomNumber);
                    statement.setString(2, roomType);
                    statement.setString(3, price);
                    statement.setBoolean(4, availability);

                    // Execute the query
                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Room details added successfully!");
                    }

                    // Refresh table data
                    refreshTableData(roomsTable);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        // Initial data load
        refreshTableData(roomsTable);

        // Add panel to tab
        tabbedPane.addTab("Add Room", addRoomsPanel);
    }

    public static void SearchRoomTab(JTabbedPane tabbedPane) {
        JPanel searchPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        searchPanel.setLayout(layout);

        // Room Type Filter
        JLabel typeLabel = new JLabel("Room Type:");
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"All", "Single", "Double", "Suite"});
        layout.putConstraint(SpringLayout.WEST, typeLabel, 20, SpringLayout.WEST, searchPanel);
        layout.putConstraint(SpringLayout.NORTH, typeLabel, 20, SpringLayout.NORTH, searchPanel);
        layout.putConstraint(SpringLayout.WEST, typeComboBox, 150, SpringLayout.WEST, searchPanel);
        layout.putConstraint(SpringLayout.NORTH, typeComboBox, 20, SpringLayout.NORTH, searchPanel);

        searchPanel.add(typeLabel);
        searchPanel.add(typeComboBox);

        // Price Range Filter
        JLabel priceRangeLabel = new JLabel("Price Range:");
        JTextField minPriceField = new JTextField(8);
        JLabel toLabel = new JLabel("to");
        JTextField maxPriceField = new JTextField(8);

        layout.putConstraint(SpringLayout.WEST, priceRangeLabel, 20, SpringLayout.WEST, searchPanel);
        layout.putConstraint(SpringLayout.NORTH, priceRangeLabel, 20, SpringLayout.SOUTH, typeComboBox);
        layout.putConstraint(SpringLayout.WEST, minPriceField, 150, SpringLayout.WEST, searchPanel);
        layout.putConstraint(SpringLayout.NORTH, minPriceField, 20, SpringLayout.SOUTH, typeComboBox);
        layout.putConstraint(SpringLayout.WEST, toLabel, 10, SpringLayout.EAST, minPriceField);
        layout.putConstraint(SpringLayout.NORTH, toLabel, 20, SpringLayout.SOUTH, typeComboBox);
        layout.putConstraint(SpringLayout.WEST, maxPriceField, 10, SpringLayout.EAST, toLabel);
        layout.putConstraint(SpringLayout.NORTH, maxPriceField, 20, SpringLayout.SOUTH, typeComboBox);

        searchPanel.add(priceRangeLabel);
        searchPanel.add(minPriceField);
        searchPanel.add(toLabel);
        searchPanel.add(maxPriceField);

        // Availability Filter
        JLabel availabilityLabel = new JLabel("Availability:");
        JComboBox<String> availabilityComboBox = new JComboBox<>(new String[]{"All", "Available", "Not Available"});

        layout.putConstraint(SpringLayout.WEST, availabilityLabel, 20, SpringLayout.WEST, searchPanel);
        layout.putConstraint(SpringLayout.NORTH, availabilityLabel, 20, SpringLayout.SOUTH, minPriceField);
        layout.putConstraint(SpringLayout.WEST, availabilityComboBox, 150, SpringLayout.WEST, searchPanel);
        layout.putConstraint(SpringLayout.NORTH, availabilityComboBox, 20, SpringLayout.SOUTH, minPriceField);

        searchPanel.add(availabilityLabel);
        searchPanel.add(availabilityComboBox);

        // Search Button
        JButton searchButton = new JButton("Search");
        layout.putConstraint(SpringLayout.WEST, searchButton, 20, SpringLayout.WEST, searchPanel);
        layout.putConstraint(SpringLayout.NORTH, searchButton, 30, SpringLayout.SOUTH, availabilityComboBox);

        searchPanel.add(searchButton);

        // Results Table
        JTable resultsTable = new JTable(new DefaultTableModel(
                new Object[]{"Room Number", "Room Type", "Price", "Availability"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        layout.putConstraint(SpringLayout.WEST, scrollPane, 20, SpringLayout.WEST, searchPanel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 30, SpringLayout.SOUTH, searchButton);
        layout.putConstraint(SpringLayout.EAST, scrollPane, -20, SpringLayout.EAST, searchPanel);
        layout.putConstraint(SpringLayout.SOUTH, scrollPane, -20, SpringLayout.SOUTH, searchPanel);

        searchPanel.add(scrollPane);

        // Search Button Action Listener
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) typeComboBox.getSelectedItem();
                String minPrice = minPriceField.getText();
                String maxPrice = maxPriceField.getText();
                String availability = (String) availabilityComboBox.getSelectedItem();

                // Build SQL query based on filters
                StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM rooms WHERE 1=1");
                
                if (!selectedType.equals("All")) {
                    sqlBuilder.append(" AND room_type = ?");
                }
                if (!minPrice.isEmpty()) {
                    sqlBuilder.append(" AND price >= ?");
                }
                if (!maxPrice.isEmpty()) {
                    sqlBuilder.append(" AND price <= ?");
                }
                if (!availability.equals("All")) {
                    sqlBuilder.append(" AND availability = ?");
                }

                String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
                String username = "root";
                String password = "";

                try (Connection connection = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString())) {

                    int paramIndex = 1;
                    if (!selectedType.equals("All")) {
                        statement.setString(paramIndex++, selectedType);
                    }
                    if (!minPrice.isEmpty()) {
                        statement.setDouble(paramIndex++, Double.parseDouble(minPrice));
                    }
                    if (!maxPrice.isEmpty()) {
                        statement.setDouble(paramIndex++, Double.parseDouble(maxPrice));
                    }
                    if (!availability.equals("All")) {
                        statement.setBoolean(paramIndex, availability.equals("Available"));
                    }

                    ResultSet resultSet = statement.executeQuery();
                    DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
                    model.setRowCount(0);

                    while (resultSet.next()) {
                        String roomNumber = resultSet.getString("room_number");
                        String roomType = resultSet.getString("room_type");
                        String price = resultSet.getString("price");
                        String isAvailable = resultSet.getBoolean("availability") ? "Available" : "Not Available";
                        model.addRow(new Object[]{roomNumber, roomType, price, isAvailable});
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error searching rooms: " + ex.getMessage());
                }
            }
        });

        // Add panel to tab
        tabbedPane.addTab("Search Rooms", searchPanel);
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
                // Create main frame
                JFrame frame = new JFrame("Hotel Management System");
                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Create JTabbedPane
                JTabbedPane tabbedPane = new JTabbedPane();

                // Add "Add Room" tab
                AddRoomTab(tabbedPane);
                
                // Add "Search Room" tab
                SearchRoomTab(tabbedPane);

                // Add tabbedPane to frame
                frame.getContentPane().add(tabbedPane);
                frame.setVisible(true);
            }
        });
    }
}