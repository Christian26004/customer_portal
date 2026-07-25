package com.customerportal.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:customer_portal.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initializeDatabase() {
        String createCustomersTable = """
        CREATE TABLE IF NOT EXISTS customers (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            street TEXT,
            city TEXT,
            state TEXT,
            zip_code TEXT
        );
        """;

        String createProductsTable = """
        CREATE TABLE IF NOT EXISTS products (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            description TEXT,
            price REAL NOT NULL,
            image_url TEXT,
            stock_quantity INTEGER NOT NULL DEFAULT 0
        );
        """;

        String createDiscountCodesTable = """
        CREATE TABLE IF NOT EXISTS discount_codes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            code TEXT UNIQUE NOT NULL,
            percentage REAL NOT NULL,
            active INTEGER NOT NULL DEFAULT 1
        );
        """;

        String createOrdersTable = """
        CREATE TABLE IF NOT EXISTS orders (
            order_id INTEGER PRIMARY KEY AUTOINCREMENT,
            customer_id INTEGER NOT NULL,
            order_date TEXT NOT NULL,
            subtotal REAL NOT NULL,
            tax REAL NOT NULL,
            discount_amount REAL NOT NULL DEFAULT 0,
            delivery_option TEXT NOT NULL,
            delivery_fee REAL NOT NULL DEFAULT 0,
            total REAL NOT NULL,
            status TEXT NOT NULL,
            FOREIGN KEY (customer_id) REFERENCES customers(id)
        );
        """;

        String createOrderItemsTable = """
        CREATE TABLE IF NOT EXISTS order_items (
            order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
            order_id INTEGER NOT NULL,
            product_id INTEGER NOT NULL,
            product_name TEXT,
            quantity INTEGER NOT NULL,
            price REAL NOT NULL,
            FOREIGN KEY (order_id) REFERENCES orders(order_id)
        );
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createCustomersTable);
            stmt.execute(createProductsTable);
            stmt.execute(createDiscountCodesTable);
            stmt.execute(createOrdersTable);
            stmt.execute(createOrderItemsTable);

            seedProductsIfEmpty(conn);
            seedDiscountCodesIfEmpty(conn);

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    private static void seedProductsIfEmpty(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) AS total FROM products";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt("total") > 0) {
                return;
            }
        }

        String insertSql = """
            INSERT INTO products (name, description, price, image_url, stock_quantity)
            VALUES (?, ?, ?, ?, ?)
            """;

        Object[][] sampleProducts = {
                {"Gala Apples (3 lb bag)", "Sweet and crisp apples.", 4.49, "", 120},
                {"Bananas (1 lb)", "Fresh yellow bananas.", 0.59, "", 200},
                {"Whole Milk (1 gal)", "Vitamin D whole milk.", 3.79, "", 80},
                {"Large Eggs (dozen)", "Grade A large eggs.", 3.29, "", 100},
                {"Sourdough Bread", "Bakery-fresh sourdough loaf.", 4.99, "", 60},
                {"Boneless Chicken Breast (1 lb)", "Fresh, boneless and skinless.", 5.99, "", 75},
                {"Jasmine Rice (5 lb bag)", "Fragrant long-grain rice.", 6.99, "", 90},
                {"Ground Coffee (12 oz)", "Medium roast ground coffee.", 8.49, "", 65}
        };

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Object[] product : sampleProducts) {
                pstmt.setString(1, (String) product[0]);
                pstmt.setString(2, (String) product[1]);
                pstmt.setDouble(3, (Double) product[2]);
                pstmt.setString(4, (String) product[3]);
                pstmt.setInt(5, (Integer) product[4]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private static void seedDiscountCodesIfEmpty(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) AS total FROM discount_codes";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt("total") > 0) {
                return;
            }
        }

        String insertSql = "INSERT INTO discount_codes (code, percentage, active) VALUES (?, ?, 1)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, "WELCOME5");
            pstmt.setDouble(2, 0.05);
            pstmt.addBatch();

            pstmt.setString(1, "SAVE10");
            pstmt.setDouble(2, 0.10);
            pstmt.addBatch();

            pstmt.executeBatch();
        }
    }
}
