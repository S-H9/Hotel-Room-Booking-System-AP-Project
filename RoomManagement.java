import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class RoomManagement {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Room Management");
                frame.setSize(1000, 700);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Create tabbed pane
                JTabbedPane tabbedPane = new JTabbedPane();
                
                // Add Room Management tab
                tabbedPane.addTab("Room Management", createMainPanel());
                
                // Add Reporting tab
                createReportingTab(tabbedPane);
                
                frame.getContentPane().add(tabbedPane);
                frame.setVisible(true);
            }
        });
    }

    private static void createReportingTab(JTabbedPane tabbedPane) {
        JPanel reportingPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        reportingPanel.setLayout(layout);

        // Create summary cards panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create metric cards
        JPanel totalRoomsCard = createMetricCard("Total Rooms", "0");
        JPanel unavailableRoomsCard = createMetricCard("Unavailable", "0");
        JPanel availableRoomsCard = createMetricCard("Available", "0");

        summaryPanel.add(totalRoomsCard);
        summaryPanel.add(unavailableRoomsCard);
        summaryPanel.add(availableRoomsCard);

        // Position summary panel
        layout.putConstraint(SpringLayout.WEST, summaryPanel, 20, SpringLayout.WEST, reportingPanel);
        layout.putConstraint(SpringLayout.EAST, summaryPanel, -20, SpringLayout.EAST, reportingPanel);
        layout.putConstraint(SpringLayout.NORTH, summaryPanel, 20, SpringLayout.NORTH, reportingPanel);
        reportingPanel.add(summaryPanel);

        // Create refresh button
        JButton refreshButton = new JButton("Refresh Data");
        layout.putConstraint(SpringLayout.WEST, refreshButton, 20, SpringLayout.WEST, reportingPanel);
        layout.putConstraint(SpringLayout.NORTH, refreshButton, 20, SpringLayout.SOUTH, summaryPanel);
        reportingPanel.add(refreshButton);

        // Create detailed statistics table
        JTable statisticsTable = new JTable(new DefaultTableModel(
                new Object[]{"Room Type", "Total Rooms", "Unavailable", "Available", "Occupancy Rate"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(statisticsTable);
        
        // Position table
        layout.putConstraint(SpringLayout.WEST, scrollPane, 20, SpringLayout.WEST, reportingPanel);
        layout.putConstraint(SpringLayout.EAST, scrollPane, -20, SpringLayout.EAST, reportingPanel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 20, SpringLayout.SOUTH, refreshButton);
        layout.putConstraint(SpringLayout.SOUTH, scrollPane, -20, SpringLayout.SOUTH, reportingPanel);
        reportingPanel.add(scrollPane);

        // Add refresh button action listener
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateReportingData(statisticsTable, totalRoomsCard, unavailableRoomsCard, availableRoomsCard);
            }
        });

        // Initial data load
        updateReportingData(statisticsTable, totalRoomsCard, unavailableRoomsCard, availableRoomsCard);

        // Add panel to tab
        tabbedPane.addTab("Reporting", reportingPanel);
    }

    private static JPanel createMetricCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 24));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private static void updateReportingData(JTable table, JPanel totalRoomsCard,
                                          JPanel unavailableRoomsCard, JPanel availableRoomsCard) {
        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Update summary cards
            String summaryQuery = "SELECT " +
                "COUNT(*) as total_rooms, " +
                "SUM(CASE WHEN availability = false THEN 1 ELSE 0 END) as unavailable_rooms, " +
                "SUM(CASE WHEN availability = true THEN 1 ELSE 0 END) as available_rooms " +
                "FROM rooms";

            try (PreparedStatement stmt = connection.prepareStatement(summaryQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    int totalRooms = rs.getInt("total_rooms");
                    int unavailableRooms = rs.getInt("unavailable_rooms");
                    int availableRooms = rs.getInt("available_rooms");
                    
                    // Update card values
                    updateCardValue(totalRoomsCard, String.valueOf(totalRooms));
                    updateCardValue(unavailableRoomsCard, String.valueOf(unavailableRooms));
                    updateCardValue(availableRoomsCard, String.valueOf(availableRooms));
                }
            }

            // Update detailed statistics table
            String detailedQuery = "SELECT " +
                "room_type, " +
                "COUNT(*) as total_rooms, " +
                "SUM(CASE WHEN availability = false THEN 1 ELSE 0 END) as unavailable, " +
                "SUM(CASE WHEN availability = true THEN 1 ELSE 0 END) as available, " +
                "ROUND((SUM(CASE WHEN availability = false THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2) as occupancy_rate " +
                "FROM rooms GROUP BY room_type";

            try (PreparedStatement stmt = connection.prepareStatement(detailedQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("room_type"),
                        rs.getInt("total_rooms"),
                        rs.getInt("unavailable"),
                        rs.getInt("available"),
                        rs.getDouble("occupancy_rate") + "%"
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating reporting data: " + ex.getMessage());
        }
    }

    private static void updateCardValue(JPanel card, String value) {
        Component[] components = card.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getFont().getSize() == 24) {
                    label.setText(value);
                    break;
                }
            }
        }
    }

    private static JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        mainPanel.setLayout(layout);

        // Create two sub-panels for add and search functionality
        JPanel addPanel = new JPanel();
        JPanel searchPanel = new JPanel();
        
        // Add panel title borders
        addPanel.setBorder(BorderFactory.createTitledBorder("Add/Delete Rooms"));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Rooms"));

        // Set layouts for sub-panels
        SpringLayout addLayout = new SpringLayout();
        SpringLayout searchLayout = new SpringLayout();
        addPanel.setLayout(addLayout);
        searchPanel.setLayout(searchLayout);

        // Position the panels side by side
        layout.putConstraint(SpringLayout.WEST, addPanel, 10, SpringLayout.WEST, mainPanel);
        layout.putConstraint(SpringLayout.NORTH, addPanel, 10, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.WEST, searchPanel, 10, SpringLayout.EAST, addPanel);
        layout.putConstraint(SpringLayout.NORTH, searchPanel, 10, SpringLayout.NORTH, mainPanel);

        // Set panel sizes
        addPanel.setPreferredSize(new Dimension(480, 650));
        searchPanel.setPreferredSize(new Dimension(480, 650));

        // Add Room Components
        JLabel roomNumberLabel = new JLabel("Room Number:");
        JTextField roomNumberField = new JTextField(15);
        JLabel roomTypeLabel = new JLabel("Room Type:");
        JComboBox<String> roomTypeComboBox = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(15);
        JLabel availabilityLabel = new JLabel("Availability:");
        JRadioButton availableYes = new JRadioButton("True");
        JRadioButton availableNo = new JRadioButton("False");
        ButtonGroup availabilityGroup = new ButtonGroup();
        availabilityGroup.add(availableYes);
        availabilityGroup.add(availableNo);

        // Position Add Room components
        addLayout.putConstraint(SpringLayout.WEST, roomNumberLabel, 20, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, roomNumberLabel, 30, SpringLayout.NORTH, addPanel);
        addLayout.putConstraint(SpringLayout.WEST, roomNumberField, 150, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, roomNumberField, 30, SpringLayout.NORTH, addPanel);

        addLayout.putConstraint(SpringLayout.WEST, roomTypeLabel, 20, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, roomTypeLabel, 30, SpringLayout.SOUTH, roomNumberField);
        addLayout.putConstraint(SpringLayout.WEST, roomTypeComboBox, 150, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, roomTypeComboBox, 30, SpringLayout.SOUTH, roomNumberField);

        addLayout.putConstraint(SpringLayout.WEST, priceLabel, 20, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, priceLabel, 30, SpringLayout.SOUTH, roomTypeComboBox);
        addLayout.putConstraint(SpringLayout.WEST, priceField, 150, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, priceField, 30, SpringLayout.SOUTH, roomTypeComboBox);

        addLayout.putConstraint(SpringLayout.WEST, availabilityLabel, 20, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, availabilityLabel, 30, SpringLayout.SOUTH, priceField);
        addLayout.putConstraint(SpringLayout.WEST, availableYes, 150, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, availableYes, 30, SpringLayout.SOUTH, priceField);
        addLayout.putConstraint(SpringLayout.WEST, availableNo, 220, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, availableNo, 30, SpringLayout.SOUTH, priceField);

        // Add components to addPanel
        addPanel.add(roomNumberLabel);
        addPanel.add(roomNumberField);
        addPanel.add(roomTypeLabel);
        addPanel.add(roomTypeComboBox);
        addPanel.add(priceLabel);
        addPanel.add(priceField);
        addPanel.add(availabilityLabel);
        addPanel.add(availableYes);
        addPanel.add(availableNo);

        // Add action buttons
        JButton submitButton = new JButton("Submit");
        JButton deleteButton = new JButton("Delete Selected");
        JButton deleteAllButton = new JButton("Delete All");

        addLayout.putConstraint(SpringLayout.WEST, submitButton, 20, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, submitButton, 30, SpringLayout.SOUTH, availableYes);
        addLayout.putConstraint(SpringLayout.WEST, deleteButton, 120, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, deleteButton, 30, SpringLayout.SOUTH, availableYes);
        addLayout.putConstraint(SpringLayout.WEST, deleteAllButton, 260, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, deleteAllButton, 30, SpringLayout.SOUTH, availableYes);

        addPanel.add(submitButton);
        addPanel.add(deleteButton);
        addPanel.add(deleteAllButton);

        // Search Components
        JLabel searchTypeLabel = new JLabel("Room Type:");
        JComboBox<String> searchTypeComboBox = new JComboBox<>(new String[]{"All", "Single", "Double", "Suite"});
        JLabel priceRangeLabel = new JLabel("Price Range:");
        JTextField minPriceField = new JTextField(8);
        JLabel toLabel = new JLabel("to");
        JTextField maxPriceField = new JTextField(8);
        JLabel searchAvailabilityLabel = new JLabel("Availability:");
        JComboBox<String> availabilityComboBox = new JComboBox<>(new String[]{"All", "Available", "Not Available"});
        JButton searchButton = new JButton("Search");

        // Position Search components
        searchLayout.putConstraint(SpringLayout.WEST, searchTypeLabel, 20, SpringLayout.WEST, searchPanel);
        searchLayout.putConstraint(SpringLayout.NORTH, searchTypeLabel, 30, SpringLayout.NORTH, searchPanel);
        searchLayout.putConstraint(SpringLayout.WEST, searchTypeComboBox, 150, SpringLayout.WEST, searchPanel);
        searchLayout.putConstraint(SpringLayout.NORTH, searchTypeComboBox, 30, SpringLayout.NORTH, searchPanel);

        searchLayout.putConstraint(SpringLayout.WEST, priceRangeLabel, 20, SpringLayout.WEST, searchPanel);
        searchLayout.putConstraint(SpringLayout.NORTH, priceRangeLabel, 30, SpringLayout.SOUTH, searchTypeComboBox);
        searchLayout.putConstraint(SpringLayout.WEST, minPriceField, 150, SpringLayout.WEST, searchPanel);
        searchLayout.putConstraint(SpringLayout.NORTH, minPriceField, 30, SpringLayout.SOUTH, searchTypeComboBox);
        searchLayout.putConstraint(SpringLayout.WEST, toLabel, 10, SpringLayout.EAST, minPriceField);
        searchLayout.putConstraint(SpringLayout.NORTH, toLabel, 30, SpringLayout.SOUTH, searchTypeComboBox);
        searchLayout.putConstraint(SpringLayout.WEST, maxPriceField, 10, SpringLayout.EAST, toLabel);
        searchLayout.putConstraint(SpringLayout.NORTH, maxPriceField, 30, SpringLayout.SOUTH, searchTypeComboBox);

        searchLayout.putConstraint(SpringLayout.WEST, searchAvailabilityLabel, 20, SpringLayout.WEST, searchPanel);
        searchLayout.putConstraint(SpringLayout.NORTH, searchAvailabilityLabel, 30, SpringLayout.SOUTH, minPriceField);
        searchLayout.putConstraint(SpringLayout.WEST, availabilityComboBox, 150, SpringLayout.WEST, searchPanel);
        searchLayout.putConstraint(SpringLayout.NORTH, availabilityComboBox, 30, SpringLayout.SOUTH, minPriceField);

        searchLayout.putConstraint(SpringLayout.WEST, searchButton, 20, SpringLayout.WEST, searchPanel);
        searchLayout.putConstraint(SpringLayout.NORTH, searchButton, 30, SpringLayout.SOUTH, availabilityComboBox);

        // Add components to searchPanel
        searchPanel.add(searchTypeLabel);
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(priceRangeLabel);
        searchPanel.add(minPriceField);
        searchPanel.add(toLabel);
        searchPanel.add(maxPriceField);
        searchPanel.add(searchAvailabilityLabel);
        searchPanel.add(availabilityComboBox);
        searchPanel.add(searchButton);

        // Create tables for both panels
        JTable roomsTable = new JTable(new DefaultTableModel(
                new Object[]{"Room Number", "Room Type", "Price", "Availability"}, 0
        ));
        JScrollPane addScrollPane = new JScrollPane(roomsTable);
        addLayout.putConstraint(SpringLayout.WEST, addScrollPane, 20, SpringLayout.WEST, addPanel);
        addLayout.putConstraint(SpringLayout.NORTH, addScrollPane, 30, SpringLayout.SOUTH, submitButton);
        addLayout.putConstraint(SpringLayout.EAST, addScrollPane, -20, SpringLayout.EAST, addPanel);
        addLayout.putConstraint(SpringLayout.SOUTH, addScrollPane, -20, SpringLayout.SOUTH, addPanel);
        addPanel.add(addScrollPane);

        JTable searchResultsTable = new JTable(new DefaultTableModel(
                new Object[]{"Room Number", "Room Type", "Price", "Availability"}, 0
        ));
        JScrollPane searchScrollPane = new JScrollPane(searchResultsTable);
        searchLayout.putConstraint(SpringLayout.WEST, searchScrollPane, 20, SpringLayout.WEST, searchPanel);
        searchLayout.putConstraint(SpringLayout.NORTH, searchScrollPane, 30, SpringLayout.SOUTH, searchButton);
        searchLayout.putConstraint(SpringLayout.EAST, searchScrollPane, -20, SpringLayout.EAST, searchPanel);
        searchLayout.putConstraint(SpringLayout.SOUTH, searchScrollPane, -20, SpringLayout.SOUTH, searchPanel);
        searchPanel.add(searchScrollPane);

        // Add action listeners
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
                        refreshTableData(roomsTable);
                        refreshTableData(searchResultsTable);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

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
                        refreshTableData(roomsTable);
                        refreshTableData(searchResultsTable);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

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
                        refreshTableData(searchResultsTable);

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) searchTypeComboBox.getSelectedItem();
                String minPrice = minPriceField.getText();
                String maxPrice = maxPriceField.getText();
                String availability = (String) availabilityComboBox.getSelectedItem();

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
                    DefaultTableModel model = (DefaultTableModel) searchResultsTable.getModel();
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

        // Initial data load
        refreshTableData(roomsTable);
        refreshTableData(searchResultsTable);

        // Add panels to main panel
        mainPanel.add(addPanel);
        mainPanel.add(searchPanel);

        return mainPanel;
    }

    private static void refreshTableData(JTable table) {
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
                String availability = resultSet.getBoolean("availability") ? "Available" : "Not Available";
                model.addRow(new Object[]{roomNumber, roomType, price, availability});
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + ex.getMessage());
        }
    }
}