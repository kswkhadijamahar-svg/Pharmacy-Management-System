package pharmacymanagementsystem;
import java.sql.*;

public class PharmacyManagementSystem {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/pharmacy_db", "root", "Khadija@3187");
            System.out.println("Database Connected Successfully!");
            con.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}