package com.customerportal.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class CustomerManager {

    private CustomerManager() {
    }

    public static boolean registerCustomer(String name, String email, String password, Address address) {
        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            System.err.println("Registration failed: name, email, and password are required.");
            return false;
        }

        String sql = """
        INSERT INTO customers (name, email, password, street, city, state, zip_code)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name.trim());
            pstmt.setString(2, email.trim());
            pstmt.setString(3, password);

            if (address != null) {
                pstmt.setString(4, address.getStreet());
                pstmt.setString(5, address.getCity());
                pstmt.setString(6, address.getState());
                pstmt.setString(7, address.getZipCode());
            } else {
                pstmt.setNull(4, java.sql.Types.VARCHAR);
                pstmt.setNull(5, java.sql.Types.VARCHAR);
                pstmt.setNull(6, java.sql.Types.VARCHAR);
                pstmt.setNull(7, java.sql.Types.VARCHAR);
            }

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public static Customer loginCustomer(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        String sql = "SELECT * FROM customers WHERE LOWER(email) = LOWER(?) AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim());
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String storedEmail = rs.getString("email");
                Customer customer = new Customer(id, name, storedEmail, rs.getString("password"));
                customer.setAddress(readAddress(rs));
                return customer;
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }

        return null;
    }

    public static boolean updateCustomer(Customer customer) {
        if (customer == null || customer.getName() == null || customer.getName().isBlank()
                || customer.getEmail() == null || customer.getEmail().isBlank()) {
            return false;
        }

        String sql = """
                UPDATE customers
                SET name = ?, email = ?, password = ?, street = ?, city = ?, state = ?, zip_code = ?
                WHERE id = ?
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getName().trim());
            pstmt.setString(2, customer.getEmail().trim());
            pstmt.setString(3, customer.getPassword());
            setAddressParameters(pstmt, customer.getAddress(), 4);
            pstmt.setInt(8, customer.getCustomerId());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Profile update failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean emailExists(String email) {
        String cleanedEmail = email.trim();
        String sql = "SELECT 1 FROM customers WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cleanedEmail);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Returns true if a record was found

        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            return false;
        }
    }

    public static List<Customer> getCustomers() {
        List<Customer> customerList = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");

                Customer customer = new Customer(id, name, email, password);
                customer.setAddress(readAddress(rs));
                customerList.add(customer);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customers: " + e.getMessage());
        }

        return customerList;
    }

    private static Address readAddress(ResultSet rs) throws SQLException {
        String street = rs.getString("street");
        String city = rs.getString("city");
        String state = rs.getString("state");
        String zipCode = rs.getString("zip_code");

        if (street == null && city == null && state == null && zipCode == null) {
            return null;
        }
        return new Address(rs.getInt("id"), street, city, state, zipCode);
    }

    private static void setAddressParameters(PreparedStatement pstmt, Address address, int startIndex)
            throws SQLException {
        if (address == null) {
            for (int i = 0; i < 4; i++) {
                pstmt.setNull(startIndex + i, java.sql.Types.VARCHAR);
            }
            return;
        }
        pstmt.setString(startIndex, address.getStreet());
        pstmt.setString(startIndex + 1, address.getCity());
        pstmt.setString(startIndex + 2, address.getState());
        pstmt.setString(startIndex + 3, address.getZipCode());
    }
}
