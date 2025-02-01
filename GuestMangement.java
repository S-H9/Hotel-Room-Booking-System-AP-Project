package APFinalProject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GuestMangement {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField idField;
    private JButton submitButton;
    private JList<String> guestList;
    private DefaultListModel<String> guestListModel;
    private JButton deleteButton;
    private JButton editButton;

    public JPanel createGuestPanel() {
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create form panel for input fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Guest"));
        GridBagConstraints gbc = createGridBagConstraints();

        // Add input fields with labels
        addFormField(formPanel, "Guest Name:", nameField = new JTextField(20), gbc, 0);
        addFormField(formPanel, "Email Address:", emailField = new JTextField(20), gbc, 1);
        addFormField(formPanel, "ID Proof:", idField = new JTextField(20), gbc, 2);

        // Create and add submit button
        submitButton = new JButton("Add Guest");
        submitButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(submitButton, gbc);

        // Create guest list panel
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBorder(BorderFactory.createTitledBorder("Guest List"));
        
        guestListModel = new DefaultListModel<>();
        guestList = new JList<>(guestListModel);
        guestList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guestList.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(guestList);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel for delete and edit buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        
        // Create delete button
        deleteButton = new JButton("Delete Selected Guest");
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false);
        buttonPanel.add(deleteButton);

        // Create edit button
        editButton = new JButton("Edit Guest");
        editButton.setFocusPainted(false);
        editButton.setEnabled(false);
        buttonPanel.add(editButton);

        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(listPanel, BorderLayout.CENTER);

        // Add action listeners
        setupActionListeners();

        // Load existing guests
        loadGuestNames();

        return mainPanel;
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addFormField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int gridy) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 25));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.weightx = 0.0;
    }

    private void setupActionListeners() {
        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String id = idField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || id.isEmpty()) {
                showError("Please fill in all fields.");
                return;
            }
            addGuestToDatabase(name, email, id);
        });

        deleteButton.addActionListener(e -> {
            String selectedGuest = guestList.getSelectedValue();
            if (selectedGuest != null) {
                int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this guest?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteGuestFromDatabase(selectedGuest);
                }
            }
        });

        editButton.addActionListener(e -> {
            String selectedGuest = guestList.getSelectedValue();
            if (selectedGuest != null) {
                showEditDialog(selectedGuest);
            }
        });

        guestList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = guestList.getSelectedValue() != null;
                deleteButton.setEnabled(hasSelection);
                editButton.setEnabled(hasSelection);
            }
        });
    }

    private void showEditDialog(String guestInfo) {
        String name = guestInfo.split(",")[0].split(":")[1].trim();
        String id = guestInfo.split(",")[1].split(":")[1].trim();
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(guestList), "Edit Guest", true);
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = createGridBagConstraints();
        
        JTextField editNameField = new JTextField(name, 20);
        JTextField editEmailField = new JTextField(20);
        JTextField editIdField = new JTextField(id, 20);
        
        // Load email from database
        loadGuestDetails(name, id, editEmailField);
        
        addFormField(editPanel, "Guest Name:", editNameField, gbc, 0);
        addFormField(editPanel, "Email Address:", editEmailField, gbc, 1);
        addFormField(editPanel, "ID Proof:", editIdField, gbc, 2);
        
        JButton updateButton = new JButton("Update");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        editPanel.add(updateButton, gbc);
        
        updateButton.addActionListener(ev -> {
            String newName = editNameField.getText().trim();
            String newEmail = editEmailField.getText().trim();
            String newId = editIdField.getText().trim();
            
            if (newName.isEmpty() || newEmail.isEmpty() || newId.isEmpty()) {
                showError("Please fill in all fields.");
                return;
            }
            
            updateGuestInDatabase(name, id, newName, newEmail, newId);
            dialog.dispose();
        });
        
        dialog.add(editPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(guestList);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void loadGuestDetails(String name, String id, JTextField emailField) {
        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String user = "root";
        String password = "";
        
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = connection.prepareStatement(
                 "SELECT contact_info FROM guests WHERE name = ? AND id_proof = ?")) {
            
            stmt.setString(1, name);
            stmt.setString(2, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    emailField.setText(rs.getString("contact_info"));
                }
            }
        } catch (SQLException e) {
            showError("Failed to load guest details: " + e.getMessage());
        }
    }

    private void loadGuestNames() {
        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name, id_proof FROM guests")) {

            guestListModel.clear();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String idProof = resultSet.getString("id_proof");
                guestListModel.addElement(String.format("Name: %s, ID: %s", name, idProof));
            }
        } catch (SQLException e) {
            showError("Failed to load guest list: " + e.getMessage());
        }
    }

    private void addGuestToDatabase(String name, String email, String id) {
        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = connection.prepareStatement(
                 "INSERT INTO guests (name, contact_info, id_proof) VALUES (?, ?, ?)")) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, id);
            
            if (stmt.executeUpdate() > 0) {
                showSuccess("Guest added successfully");
                clearFields();
                loadGuestNames();
            }
        } catch (SQLException e) {
            showError("Failed to add guest: " + e.getMessage());
        }
    }

    private void updateGuestInDatabase(String oldName, String oldId, String newName, String newEmail, String newId) {
        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String user = "root";
        String password = "";
        
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = connection.prepareStatement(
                 "UPDATE guests SET name = ?, contact_info = ?, id_proof = ? WHERE name = ? AND id_proof = ?")) {
            
            stmt.setString(1, newName);
            stmt.setString(2, newEmail);
            stmt.setString(3, newId);
            stmt.setString(4, oldName);
            stmt.setString(5, oldId);
            
            if (stmt.executeUpdate() > 0) {
                showSuccess("Guest updated successfully");
                loadGuestNames();
            }
        } catch (SQLException e) {
            showError("Failed to update guest: " + e.getMessage());
        }
    }

    private void deleteGuestFromDatabase(String guestInfo) {
        String name = guestInfo.split(",")[0].split(":")[1].trim();
        String url = "jdbc:mysql://localhost:3306/HotelBookingSystem";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM guests WHERE name = ?")) {
            
            stmt.setString(1, name);
            
            if (stmt.executeUpdate() > 0) {
                showSuccess("Guest deleted successfully");
                loadGuestNames();
            }
        } catch (SQLException e) {
            showError("Failed to delete guest: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        idField.setText("");
        nameField.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}