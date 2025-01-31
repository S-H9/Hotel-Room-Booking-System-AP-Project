package omar1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HotelBookingSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/HotelBookingSystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelBookingSystem::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Hotel Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500); // تكبير حجم النافذة قليلاً

        // إنشاء Panel لتحديد موقع الجدول
        JPanel panel = new JPanel();
        panel.setLayout(null); // استخدام تنسيق يدوي لتحديد الموقع بدقة

        // إنشاء جدول الحجز
        String[] columnNames = {"ID", "Room", "Guest", "Check-in", "Check-out"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        // تكبير حجم الجدول قليلاً
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 80, 500, 300); // تكبير العرض والارتفاع

        // تحميل بيانات الحجز
        loadBookingData(tableModel);

        // إضافة الجدول إلى الـ Panel
        panel.add(scrollPane);

        // ترتيب العناصر في الواجهة
        frame.add(panel);

        frame.setVisible(true);
    }

    private static void loadBookingData(DefaultTableModel tableModel) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT booking_id, room_id, guest_id, check_in_date, check_out_date FROM booking")) {

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                int roomId = rs.getInt("room_id");
                int guestId = rs.getInt("guest_id");
                String checkInDate = rs.getString("check_in_date");
                String checkOutDate = rs.getString("check_out_date");

                tableModel.addRow(new Object[]{bookingId, roomId, guestId, checkInDate, checkOutDate});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }
}