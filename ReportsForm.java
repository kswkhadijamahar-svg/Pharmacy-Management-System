package pharmacymanagementsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * ReportsForm - Generates reports for the pharmacy
 * Shows Sales Report and Low Stock Report
 */
public class ReportsForm extends JFrame {

    // UI Components
    JTable table;
    DefaultTableModel model;
    JLabel lblTotalSales, lblLowStock;

    /**
     * Constructor - Creates the Reports window
     */
    public ReportsForm() {
        // Set window properties
        setTitle("Reports");
        setSize(800, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(173, 216, 230));

        // Title Label
        JLabel lblTitle = new JLabel("REPORTS");
        lblTitle.setBounds(350, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle);

        // Sales Report Button
        JButton btnSalesReport = new JButton("Sales Report");
        btnSalesReport.setBounds(50, 60, 150, 35);
        btnSalesReport.setBackground(new Color(0, 102, 204));
        btnSalesReport.setForeground(Color.WHITE);
        btnSalesReport.setFont(new Font("Arial", Font.BOLD, 12));
        add(btnSalesReport);

        // Low Stock Report Button
        JButton btnLowStock = new JButton("Low Stock Report");
        btnLowStock.setBounds(220, 60, 160, 35);
        btnLowStock.setBackground(new Color(204, 0, 0));
        btnLowStock.setForeground(Color.WHITE);
        btnLowStock.setFont(new Font("Arial", Font.BOLD, 12));
        add(btnLowStock);

        // Label to show total sales amount
        lblTotalSales = new JLabel("Total Sales: ");
        lblTotalSales.setBounds(50, 110, 300, 25);
        lblTotalSales.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblTotalSales);

        // Label to show low stock count
        lblLowStock = new JLabel("");
        lblLowStock.setBounds(50, 140, 300, 25);
        lblLowStock.setFont(new Font("Arial", Font.BOLD, 14));
        lblLowStock.setForeground(Color.RED);
        add(lblLowStock);

        // Table to display report data
        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 180, 700, 300);
        add(scrollPane);

        // Sales Report button click
        btnSalesReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Show sales report
                showSalesReport();
            }
        });

        // Low Stock Report button click
        btnLowStock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Show low stock report
                showLowStockReport();
            }
        });

        // Make window visible
        setVisible(true);
    }

    /**
     * getConnection() - Creates and returns database connection
     */
    Connection getConnection() throws Exception {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/pharmacy_db", "root", "Khadija@3187");
    }

    /**
     * showSalesReport() - Displays all sales records
     * and calculates total sales amount
     */
    void showSalesReport() {
        // Clear existing table data
        model.setRowCount(0);
        model.setColumnCount(0);

        // Add table columns
        model.addColumn("ID");
        model.addColumn("Medicine");
        model.addColumn("Quantity Sold");
        model.addColumn("Total Price");
        model.addColumn("Sale Date");

        try {
            Connection con = getConnection();
            // Get all sales from database
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM sales");
            double total = 0;
            while (rs.next()) {
                // Add each sale to table
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("medicine_name"),
                    rs.getInt("quantity_sold"),
                    rs.getDouble("total_price"),
                    rs.getString("sale_date")
                });
                // Calculate total sales amount
                total += rs.getDouble("total_price");
            }
            // Show total sales amount
            lblTotalSales.setText("Total Sales: Rs. " + total);
            lblLowStock.setText("");
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    /**
     * showLowStockReport() - Displays medicines with low stock
     * Shows medicines with quantity less than 50
     */
    void showLowStockReport() {
        // Clear existing table data
        model.setRowCount(0);
        model.setColumnCount(0);

        // Add table columns
        model.addColumn("ID");
        model.addColumn("Medicine Name");
        model.addColumn("Category");
        model.addColumn("Quantity");

        try {
            Connection con = getConnection();
            // Get medicines with quantity less than 50
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT * FROM medicines WHERE quantity < 50");
            int count = 0;
            while (rs.next()) {
                // Add each low stock medicine to table
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("quantity")
                });
                count++;
            }
            // Show count of low stock medicines
            lblLowStock.setText("Low Stock Medicines: " + count);
            lblTotalSales.setText("");
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}