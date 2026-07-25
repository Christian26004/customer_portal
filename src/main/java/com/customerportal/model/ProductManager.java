package com.customerportal.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Handles read access to the products table.
public final class ProductManager {

    private ProductManager() {
    }

    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }

        return products;
    }

    public static Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching product: " + e.getMessage());
        }

        return null;
    }

    private static Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getString("image_url"),
                rs.getInt("stock_quantity")
        );
    }
}
