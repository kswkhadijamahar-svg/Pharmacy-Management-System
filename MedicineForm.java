package pharmacymanagementsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * MedicineForm - Manages medicines in the pharmacy
 * Supports Add, Update, Delete, Search operations (CRUD)
 */
public class MedicineForm extends JFrame {

    // UI Components
    JTextField txtName, txtCategory, txtPrice, txtQuantity, txtExpiry, txtSearch;
    JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;
    JTable table;
    DefaultTableModel model;

    /**
     * Constructor - Creates the Medicine Management window
     */
    public MedicineForm() {
        // Set window properties
        setTitle("Medicine Management");
        setSize(800, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(173, 216, 230));

        // Title Label
        JLabel lblTitle = new JLabel("MEDICINE MANAGEMENT");
        lblTitle.setBounds(280, 10, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle);

        // Medicine Name Label and Field
        JLabel lblName = new JLabel("Medicine Name:");
        lblName.setBounds(20, 60, 120, 25);
        add(lblName);
        txtName = new JTextField();
        txtName.setBounds(140, 60, 150, 25);
        add(txtName);

        // Category Label and Field
        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setBounds(20, 100, 120, 25);
        add(lblCategory);
        txtCategory = new JTextField();
        txtCategory.setBounds(140, 100, 150, 25);
        add(txtCategory);

        // Price Label and Field
        JLabel lblPrice = new JLabel("Price:");
        lblPrice.setBounds(20, 140, 120, 25);
        add(lblPrice);
        txtPrice = new JTextField();
        txtPrice.setBounds(140, 140, 150, 25);
        add(txtPrice);

        // Quantity Label and Field
        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setBounds(20, 180, 120, 25);
        add(lblQuantity);
        txtQuantity = new JTextField();
        txtQuantity.setBounds(140, 180, 150, 25);
        add(txtQuantity);

        // Expiry Date Label and Field
        JLabel lblExpiry = new JLabel("Expiry Date:");
        lblExpiry.setBounds(20, 220, 120, 25);
        add(lblExpiry);
        txtExpiry = new JTextField("YYYY-MM-DD");
        txtExpiry.setBounds(140, 220, 150, 25);
        add(txtExpiry);

        // Add Button - Adds new medicine
        btnAdd = new JButton("Add");
        btnAdd.setBounds(20, 270, 80, 30);
        btnAdd.setBackground(new Color(0, 153, 76));
        btnAdd.setForeground(Color.WHITE);
        add(btnAdd);

        // Update Button - Updates selected medicine
        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(110, 270, 80, 30);
        btnUpdate.setBackground(new Color(0, 102, 204));
        btnUpdate.setForeground(Color.WHITE);
        add(btnUpdate);

        // Delete Button - Deletes selected medicine
        btnDelete = new JButton("Delete");
        btnDelete.setBounds(200, 270, 80, 30);
        btnDelete.setBackground(new Color(204, 0, 0));
        btnDelete.setForeground(Color.WHITE);
        add(btnDelete);

        // Clear Button - Clears all fields
        btnClear = new JButton("Clear");
        btnClear.setBounds(290, 270, 80, 30);
        btnClear.setBackground(new Color(204, 102, 0));
        btnClear.setForeground(Color.WHITE);
        add(btnClear);

        // Search Label and Field
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setBounds(20, 320, 60, 25);
        add(lblSearch);
        txtSearch = new JTextField();
        txtSearch.setBounds(80, 320, 150, 25);
        add(txtSearch);

        // Search Button - Searches medicine by name
        btnSearch = new JButton("Search");
        btnSearch.setBounds(240, 320, 80, 25);
        btnSearch.setBackground(new Color(0, 102, 204));
        btnSearch.setForeground(Color.WHITE);
        add(btnSearch);

        // Table to display medicines
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Category");
        model.addColumn("Price");
        model.addColumn("Quantity");
        model.addColumn("Expiry Date");

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(380, 50, 390, 450);
        add(scrollPane);

        // Load all medicines from database
        loadMedicines();

        // Button action listeners
        btnAdd.addActionListener(e -> addMedicine());
        btnUpdate.addActionListener(e -> updateMedicine());
        btnDelete.addActionListener(e -> deleteMedicine());
        btnClear.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> searchMedicine());

        // Table row click - fills fields with selected medicine data
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                txtName.setText(model.getValueAt(row, 1).toString());
                txtCategory.setText(model.getValueAt(row, 2).toString());
                txtPrice.setText(model.getValueAt(row, 3).toString());
                txtQuantity.setText(model.getValueAt(row, 4).toString());
                txtExpiry.setText(model.getValueAt(row, 5).toString());
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
     * loadMedicines() - Loads all medicines from database into table
     */
    void loadMedicines() {
        // Clear existing table data
        model.setRowCount(0);
        try {
            Connection con = getConnection();
            // Get all medicines from database
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM medicines");
            while (rs.next()) {
                // Add each medicine to table
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("expiry_date")
                });
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    /**
     * addMedicine() - Adds a new medicine to the database
     */
    void addMedicine() {
        // Validate required fields
        if(txtName.getText().isEmpty() || txtPrice.getText().isEmpty() 
                || txtQuantity.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all required fields!");
            return;
        }
        try {
            Connection con = getConnection();
            // Insert new medicine into database
            String query = "INSERT INTO medicines (name, category, price, quantity, expiry_date) VALUES (?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, txtName.getText());
            ps.setString(2, txtCategory.getText());
            ps.setDouble(3, Double.parseDouble(txtPrice.getText()));
            ps.setInt(4, Integer.parseInt(txtQuantity.getText()));
            ps.setString(5, txtExpiry.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medicine Added Successfully!");
            // Refresh table
            loadMedicines();
            clearFields();
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    /**
     * updateMedicine() - Updates selected medicine in database
     */
    void updateMedicine() {
        // Check if a medicine is selected
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(null, "Please select a medicine to update!");
            return;
        }
        try {
            Connection con = getConnection();
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            // Update medicine in database
            String query = "UPDATE medicines SET name=?, category=?, price=?, quantity=?, expiry_date=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, txtName.getText());
            ps.setString(2, txtCategory.getText());
            ps.setDouble(3, Double.parseDouble(txtPrice.getText()));
            ps.setInt(4, Integer.parseInt(txtQuantity.getText()));
            ps.setString(5, txtExpiry.getText());
            ps.setInt(6, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medicine Updated Successfully!");
            // Refresh table
            loadMedicines();
            clearFields();
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    /**
     * deleteMedicine() - Deletes selected medicine from database
     */
    void deleteMedicine() {
        // Check if a medicine is selected
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(null, "Please select a medicine to delete!");
            return;
        }
        // Ask for confirmation before deleting
        int confirm = JOptionPane.showConfirmDialog(null, 
                "Are you sure you want to delete this medicine?");
        if(confirm == 0) {
            try {
                Connection con = getConnection();
                int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                // Delete medicine from database
                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM medicines WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Medicine Deleted Successfully!");
                // Refresh table
                loadMedicines();
                clearFields();
                con.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        }
    }

    /**
     * searchMedicine() - Searches medicines by name
     */
    void searchMedicine() {
        // Clear existing table data
        model.setRowCount(0);
        try {
            Connection con = getConnection();
            // Search medicine by name using LIKE query
            String query = "SELECT * FROM medicines WHERE name LIKE ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, "%" + txtSearch.getText() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("expiry_date")
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
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        txtExpiry.setText("YYYY-MM-DD");
        txtSearch.setText("");
    }
}