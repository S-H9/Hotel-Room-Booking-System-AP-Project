import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class Reporting {
    
    public static void ReportingTab(JTabbedPane tabbedPane) {
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
                updateReportingData(statisticsTable, totalRoomsCard, unavailableRoomsCard, 
                                 availableRoomsCard);
            }
        });

        // Initial data load
        updateReportingData(statisticsTable, totalRoomsCard, unavailableRoomsCard, 
                          availableRoomsCard);

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
}