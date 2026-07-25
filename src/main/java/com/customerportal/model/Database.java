package com.customerportal.model;

import java.sql.Connection;
import java.sql.DriverManager;
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

    String createOrdersTable = """
        CREATE TABLE IF NOT EXISTS orders (
            order_id INTEGER PRIMARY KEY AUTOINCREMENT,
            customer_id INTEGER NOT NULL,
            order_date TEXT NOT NULL,
            total REAL NOT NULL,
            status TEXT NOT NULL,
            FOREIGN KEY (customer_id)
                REFERENCES customers(id)
                ON DELETE CASCADE
        );
        """;

    String createOrderItemsTable = """
        CREATE TABLE IF NOT EXISTS order_items (
            order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
            order_id INTEGER NOT NULL,
            product_id INTEGER NOT NULL,
            quantity INTEGER NOT NULL,
            price REAL NOT NULL,
            FOREIGN KEY (order_id)
                REFERENCES orders(order_id)
                ON DELETE CASCADE
        );
        """;

    try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement()
    ) {
        stmt.execute(createCustomersTable);
        stmt.execute(createOrdersTable);
        stmt.execute(createOrderItemsTable);

        System.out.println("Database tables initialized.");

    } catch (SQLException e) {
        System.err.println(
                "Database initialization failed: " + e.getMessage()
        );
    }
}
}
