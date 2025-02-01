package a117;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GuestEntryForm {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField idField;
    private JButton submitButton;

    private JList<String> guestList; // JList to display guests
    private DefaultListModel<String> guestListModel; // Model for the JList
    private JButton deleteButton;

    public GuestEntryForm() {
        // Create a new JFrame
        JFrame frame = new JFrame("Hotel Booking System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create input fields for adding guests
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Guest Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Email Address:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("ID Proof:"), gbc);
        idField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(idField, gbc);

        submitButton = new JButton("Add Guest");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(submitButton, gbc);

        // Create JList to display guests
        guestListModel = new DefaultListModel<>();
        guestList = new JList<>(guestListModel);
        JScrollPane listScrollPane = new JScrollPane(guestList);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(listScrollPane, gbc);

        // Create delete button
        deleteButton = new JButton("Delete Selected Guest");
        deleteButton.setEnabled(false); // Initially disabled
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(deleteButton, gbc);

        // Add action listener to the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String id = idField.getText();

                // Validate input fields
                if (name.isEmpty() || email.isEmpty() || id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    addGuestToDatabase(name, email, id);
                }
            }
        });

        // Add action listener to the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedGuest = guestList.getSelectedValue();
                if (selectedGuest != null) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this guest?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteGuestFromDatabase(selectedGuest);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a guest to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add ListSelectionListener to enable delete button when a guest is selected
        guestList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(guestList.getSelectedValue() != null);
            }
        });

        // Load guest names from the database
        loadGuestNames();

        // Add the main panel to the frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void loadGuestNames() {
        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String user = "root"; // Replace with your database username
        String password = ""; // Replace with your database password

        String query = "SELECT name, id_proof FROM guests"; // Retrieve both name and ID proof

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            guestListModel.clear(); // Clear the list before loading new data
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String idProof = resultSet.getString("id_proof");
                guestListModel.addElement("Name: " + name + ", ID Proof: " + idProof); // Format the display
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while loading guest names.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addGuestToDatabase(String name, String email, String id) {
        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String user = "root"; // Replace with your database username
        String password = ""; // Replace with your database password

        String query = "INSERT INTO guests (name, contact_info, id_proof) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Guest added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadGuestNames(); // Reload guest names after adding
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add guest.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while connecting to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteGuestFromDatabase(String guestInfo) {
        String[] parts = guestInfo.split(","); // Assuming guestInfo contains Name and ID Proof
        String guestName = parts[0].split(":")[1].trim(); // Extract Name

        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String user = "root"; // Replace with your database username
        String password = ""; // Replace with your database password

        String query = "DELETE FROM guests WHERE name = ?"; // Assuming name is unique

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, guestName);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Guest deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadGuestNames(); // Reload guest names after deletion
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete guest.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while connecting to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        idField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuestEntryForm());
    }
}