package a117;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuestEntryForm {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField idField;
    private JButton submitButton;

    public GuestEntryForm() {
        // Create a new JFrame
        JFrame frame = new JFrame("Hotel Booking System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create the Guest Entry Panel
        JPanel guestEntryPanel = createGuestEntryPanel();
        tabbedPane.addTab("Add Guest", guestEntryPanel);

        // You can add more tabs here for other functionalities
        // JPanel viewGuestsPanel = createViewGuestsPanel();
        // tabbedPane.addTab("View Guests", viewGuestsPanel);

        // Add the tabbed pane to the frame
        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private JPanel createGuestEntryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        // Create input fields
        JLabel nameLabel = new JLabel("Guest Name:");
        nameField = new JTextField();
        
        JLabel emailLabel = new JLabel("Email Address:");
        emailField = new JTextField();
        
        JLabel idLabel = new JLabel("ID Proof:");
        idField = new JTextField();
        
        submitButton = new JButton("Add Guest");
        
        // Add components to the panel
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(idLabel);
        panel.add(idField);
        panel.add(submitButton);

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

        return panel;
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
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add guest.", "Error", JOptionPane.ERROR_MESSAGE);
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