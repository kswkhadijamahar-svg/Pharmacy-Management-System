package pharmacymanagementsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * SalesForm - Manages medicine sales in the pharmacy
 * Allows selling medicines and viewing sales history
 */
public class SalesForm extends JFrame {

    // UI Components
    JTextField txtQuantity, txtTotalPrice;
    JButton btnSell, btnClear;
    JTable table;
    DefaultTableModel model;
    JComboBox<String> cmbMedicines;

    /**
     * Constructor - Creates the Sales Management window
     */
    public SalesForm() {
        // Set window properties
        setTitle("Sales Management");
        setSize(800, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(173, 216, 230));

        // Title Label
        JLabel lblTitle = new JLabel("SALES MANAGEMENT");
        lblTitle.setBounds(280, 10, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle);

        // Medicine dropdown label
        JLabel lblMedicine = new JLabel("Select Medicine:");
        lblMedicine.setBounds(20, 60, 120, 25);
        add(lblMedicine);

        // Dropdown to select medicine
        cmbMedicines = new JComboBox<>();
        cmbMedicines.setBounds(140, 60, 200, 25);
        add(cmbMedicines);
        // Load medicines from database into dropdown
        loadMedicines();

        // Quantity Label and Field
        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setBounds(20, 100, 120, 25);
        add(lblQuantity);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(140, 100, 200, 25);
        add(txtQuantity);

        // Total Price Label and Field (auto calculated)
        JLabel lblTotal = new JLabel("Total Price:");
        lblTotal.setBounds(20, 140, 120, 25);
        add(lblTotal);

        txtTotalPrice = new JTextField();
        txtTotalPrice.setBounds(140, 140, 200, 25);
        // Make total price field read only
        txtTotalPrice.setEditable(false);
        add(txtTotalPrice);

        // Sell Button - Completes the sale
        btnSell = new JButton("Sell");
        btnSell.setBounds(80, 190, 100, 35);
        btnSell.setBackground(new Color(0, 153, 76));
        btnSell.setForeground(Color.WHITE);
        btnSell.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnSell);

        // Clear Button - Clears all fields
        btnClear = new JButton("Clear");
        btnClear.setBounds(200, 190, 100, 35);
        btnClear.setBackground(new Color(204, 0, 0));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnClear);

        // Table to display sales history
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Medicine");
        model.addColumn("Quantity Sold");
        model.addColumn("Total Price");
        model.addColumn("Sale Date");

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(380, 50, 390, 450);
        add(scrollPane);

        // Load existing sales from database
        loadSales();

        // Button action listeners
        btnSell.addActionListener(e -> sellMedicine());
        btnClear.addActionListener(e -> clearFields());

        // When medicine is selected calculate price automatically
        cmbMedicines.addActionListener(e -> calculatePrice());

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
     * loadMedicines() - Loads all medicines into dropdown
     */
    void loadMedicines() {
        try {
            Connection con = getConnection();
            // Get all medicine names from database
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT name FROM medicines");
            while (rs.next()) {
                // Add each medicine to dropdown
                cmbMedicines.addItem(rs.getString("name"));
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    /**
     * calculatePrice() - Automatically calculates total price
     * based on selected medicine and quantity
     */
    void calculatePrice() {
        try {
            String medicineName = (String) cmbMedicines.getSelectedItem();
            if (medicineName == null) return;
            Connection con = getConnection();
            // Get price of selected medicine
            PreparedStatement ps = con.prepareStatement(
                "SELECT price FROM medicines WHERE name=?");
            ps.setString(1, medicineName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double price = rs.getDouble("price");
                // Calculate total price = price x quantity
                if (!txtQuantity.getText().isEmpty()) {
                    int qty = Integer.parseInt(txtQuantity.getText());
                    txtTotalPrice.setText(String.valueOf(price * qty));
                }
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    /**
     * sellMedicine() - Processes the sale of a medicine
     * Updates stock and saves sale record to database
     */
    void sellMedicine() {
        // Validate quantity field
        if (txtQuantity.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter quantity!");
            return;
        }
        try {
            String medicineName = (String) cmbMedicines.getSelectedItem();
            int quantity = Integer.parseInt(txtQuantity.getText());
            Connection con = getConnection();

            // Check current stock of selected medicine
            PreparedStatement ps = con.prepareStatement(
                "SELECT id, price, quantity FROM medicines WHERE name=?");
            ps.setString(1, medicineName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int medicineId = rs.getInt("id");
                double price = rs.getDouble("price");
                int currentQty = rs.getInt("quantity");

                // Check if enough stock is available
                if (quantity > currentQty) {
                    JOptionPane.showMessageDialog(null,
                        "Not enough stock! Available: " + currentQty);
                    return;
                }

                // Calculate total price
                double totalPrice = price * quantity;

                // Insert sale record into database
                PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO sales (medicine_id, medicine_name, quantity_sold, total_price, sale_date) VALUES (?,?,?,?,?)");
                ps2.setInt(1, medicineId);
                ps2.setString(2, medicineName);
                ps2.setInt(3, quantity);
                ps2.setDouble(4, totalPrice);
                // Set today's date as sale date
                ps2.setString(5, LocalDate.now().toString());
                ps2.executeUpdate();

                // Update medicine stock in database
                PreparedStatement ps3 = con.prepareStatement(
                    "UPDATE medicines SET quantity=? WHERE id=?");
                ps3.setInt(1, currentQty - quantity);
                ps3.setInt(2, medicineId);
                ps3.executeUpdate();

                JOptionPane.showMessageDialog(null, "Sale Completed Successfully!");
                // Refresh sales table
                loadSales();
                clearFields();
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    /**
     * loadSales() - Loads all sales records from database into table
     */
    void loadSales() {
        // Clear existing table data
        model.setRowCount(0);
        try {
            Connection con = getConnection();
            // Get all sales from database
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM sales");
            while (rs.next()) {
                // Add each sale to table
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("medicine_name"),
                    rs.getInt("quantity_sold"),
                    rs.getDouble("total_price"),
                    rs.getString("sale_date")
                });
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    /**
     * clearFields() - Clears all input fields
     */
    void clearFields() {
        txtQuantity.setText("");
        txtTotalPrice.setText("");
    }
}