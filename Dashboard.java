package pharmacymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dashboard - Main menu of the Pharmacy Management System
 * Shows different buttons based on user role (Admin/User)
 */
public class Dashboard extends JFrame {

    // User role (Admin or User)
    String role;

    /**
     * Constructor - Creates the Dashboard window
     * @param role - Role of logged in user (Admin/User)
     */
    public Dashboard(String role) {
        this.role = role;

        // Set window properties
        setTitle("Pharmacy Management System - Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Set background color
        getContentPane().setBackground(new Color(173, 216, 230));

        // Title Label
        JLabel lblWelcome = new JLabel("PHARMACY MANAGEMENT SYSTEM");
        lblWelcome.setBounds(130, 20, 400, 30);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        lblWelcome.setForeground(new Color(0, 102, 204));
        add(lblWelcome);

        // Role Label
        JLabel lblRole = new JLabel("Logged in as: " + role);
        lblRole.setBounds(220, 55, 200, 25);
        lblRole.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lblRole);

        // Medicines Button
        JButton btnMedicines = new JButton("Medicines");
        btnMedicines.setBounds(80, 120, 150, 50);
        btnMedicines.setBackground(new Color(0, 153, 76));
        btnMedicines.setForeground(Color.WHITE);
        btnMedicines.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnMedicines);

        // Sales Button
        JButton btnSales = new JButton("Sales");
        btnSales.setBounds(280, 120, 150, 50);
        btnSales.setBackground(new Color(0, 102, 204));
        btnSales.setForeground(Color.WHITE);
        btnSales.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnSales);

        // Reports Button - Only visible to Admin
        JButton btnReports = new JButton("Reports");
        btnReports.setBounds(80, 220, 150, 50);
        btnReports.setBackground(new Color(204, 102, 0));
        btnReports.setForeground(Color.WHITE);
        btnReports.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnReports);

        // Logout Button
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(280, 220, 150, 50);
        btnLogout.setBackground(new Color(204, 0, 0));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnLogout);

        // Check role and restrict access
        if(role.equals("Admin")) {
            // Admin can see everything
            btnMedicines.setEnabled(true);
            btnReports.setEnabled(true);
        } else {
            // User can only see Sales
            // Disable Medicines button for User
            btnMedicines.setEnabled(false);
            btnMedicines.setBackground(Color.GRAY);
            btnMedicines.setText("Medicines (Admin Only)");
            // Disable Reports button for User
            btnReports.setEnabled(false);
            btnReports.setBackground(Color.GRAY);
            btnReports.setText("Reports (Admin Only)");
        }

        // Medicines button click
        btnMedicines.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MedicineForm();
            }
        });

        // Sales button click
        btnSales.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SalesForm();
            }
        });

        // Reports button click
        btnReports.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ReportsForm();
            }
        });

        // Logout button click
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginForm();
            }
        });

        // Make window visible
        setVisible(true);
    }
}