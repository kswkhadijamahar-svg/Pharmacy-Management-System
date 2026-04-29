package pharmacymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * LoginForm - Handles user authentication
 * Allows Admin and User to login to the system
 */
public class LoginForm extends JFrame {
    
    // UI Components
    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnLogin;
    JLabel lblTitle, lblUsername, lblPassword;

    /**
     * Constructor - Creates the Login Form window
     */
    public LoginForm() {
        // Set window title
        setTitle("Pharmacy Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Center the window on screen
        setLocationRelativeTo(null);
        setLayout(null);

        // Title Label
        lblTitle = new JLabel("PHARMACY MANAGEMENT SYSTEM");
        lblTitle.setBounds(50, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblTitle);

        // Username Label and Field
        lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 80, 100, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(160, 80, 180, 25);
        add(txtUsername);

        // Password Label and Field
        lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 120, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(160, 120, 180, 25);
        add(txtPassword);

        // Login Button
        btnLogin = new JButton("LOGIN");
        btnLogin.setBounds(150, 170, 100, 30);
        add(btnLogin);

        // Login button click event
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call login method when button is clicked
                login();
            }
        });

        // Make window visible
        setVisible(true);
    }

    /**
     * login() - Validates username and password from database
     * Opens Dashboard if login is successful
     */
    void login() {
        // Get username and password from fields
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        // Validate empty fields
        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter username and password!");
            return;
        }

        try {
            // Connect to database
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/pharmacy_db", "root", "Khadija@3187");
            
            // Query to check username and password
            String query = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            // If user found in database
            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(null, "Welcome " + username + "!");
                // Open Dashboard with user role
                new Dashboard(role);
                // Close login form
                dispose();
            } else {
                // Invalid credentials
                JOptionPane.showMessageDialog(null, "Invalid username or password!");
            }
            con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    /**
     * main() - Entry point of the application
     */
    public static void main(String[] args) {
        // Start the application with Login Form
        new LoginForm();
    }
}

