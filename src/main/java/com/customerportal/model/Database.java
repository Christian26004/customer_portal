package com.customerportal.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static final String DB_URL =
            "jdbc:sqlite:customer_portal.db";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection =
                DriverManager.getConnection(DB_URL);

        try (Statement statement =
                     connection.createStatement()) {

            statement.execute("PRAGMA foreign_keys = ON;");
        }

        return connection;
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
                    product_name TEXT,
                    quantity INTEGER NOT NULL,
                    price REAL NOT NULL,
                    FOREIGN KEY (order_id)
                        REFERENCES orders(order_id)
                        ON DELETE CASCADE,
                    FOREIGN KEY (product_id)
                        REFERENCES products(id)
                );
                """;

        try (
                Connection connection = getConnection();
                Statement statement =
                        connection.createStatement()
        ) {
            statement.execute(createCustomersTable);
            statement.execute(createProductsTable);
            statement.execute(createDiscountCodesTable);
            statement.execute(createOrdersTable);
            statement.execute(createOrderItemsTable);

            seedProductsIfEmpty(connection);
            seedDiscountCodesIfEmpty(connection);

            System.out.println("Database tables initialized.");

        } catch (SQLException exception) {
            System.err.println(
                    "Database initialization failed: "
                            + exception.getMessage()
            );
            exception.printStackTrace();
        }
    }

    private static void seedProductsIfEmpty(
            Connection connection
    ) throws SQLException {

        String insertSql = """
                INSERT INTO products (
                    name,
                    description,
                    price,
                    image_url,
                    stock_quantity
                )
                SELECT ?, ?, ?, ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM products WHERE name = ?
                )
                """;

        Object[][] sampleProducts = {
                {
                        "Gala Apples (3 lb bag)",
                        "Sweet and crisp apples.",
                        4.49,
                        "",
                        120
                },
                {
                        "Bananas (1 lb)",
                        "Fresh yellow bananas.",
                        0.59,
                        "",
                        200
                },
                {
                        "Whole Milk (1 gal)",
                        "Vitamin D whole milk.",
                        3.79,
                        "",
                        80
                },
                {
                        "Large Eggs (dozen)",
                        "Grade A large eggs.",
                        3.29,
                        "",
                        100
                },
                {
                        "Sourdough Bread",
                        "Bakery-fresh sourdough loaf.",
                        4.99,
                        "",
                        60
                },
                {
                        "Boneless Chicken Breast (1 lb)",
                        "Fresh, boneless and skinless.",
                        5.99,
                        "",
                        75
                },
                {
                        "Jasmine Rice (5 lb bag)",
                        "Fragrant long-grain rice.",
                        6.99,
                        "",
                        90
                },
                {
                        "Ground Coffee (12 oz)",
                        "Medium roast ground coffee.",
                        8.49,
                        "",
                        65
                },
                {
                        "Cheddar Cheese (8 oz)",
                        "Sharp cheddar cheese block.",
                        3.99,
                        "",
                        70
                },
                {
                        "Orange Juice (52 oz)",
                        "Fresh-tasting 100% orange juice.",
                        4.29,
                        "",
                        85
                },
                {
                        "Pasta (16 oz)",
                        "Classic durum wheat pasta.",
                        1.89,
                        "",
                        140
                },
                {
                        "Tomato Sauce (24 oz)",
                        "Rich tomato basil pasta sauce.",
                        2.49,
                        "",
                        110
                },
                {
                        "Peanut Butter (16 oz)",
                        "Smooth roasted peanut butter.",
                        3.49,
                        "",
                        95
                },
                {
                        "Granola Bars (6 count)",
                        "Oat and honey snack bars.",
                        3.79,
                        "",
                        75
                },
                {
                        "Frozen Mixed Vegetables (12 oz)",
                        "A convenient blend of frozen vegetables.",
                        2.99,
                        "",
                        90
                },
                {
                        "Bottled Water (24 pack)",
                        "Purified drinking water.",
                        5.99,
                        "",
                        100
                }
        };

        try (PreparedStatement statement =
                     connection.prepareStatement(insertSql)) {

            for (Object[] product : sampleProducts) {
                statement.setString(
                        1,
                        (String) product[0]
                );
                statement.setString(
                        2,
                        (String) product[1]
                );
                statement.setDouble(
                        3,
                        (Double) product[2]
                );
                statement.setString(
                        4,
                        (String) product[3]
                );
                statement.setInt(
                        5,
                        (Integer) product[4]
                );
                statement.setString(6, (String) product[0]);

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    private static void seedDiscountCodesIfEmpty(
            Connection connection
    ) throws SQLException {

        String countSql =
                "SELECT COUNT(*) AS total "
                        + "FROM discount_codes";

        try (
                Statement statement =
                        connection.createStatement();
                ResultSet results =
                        statement.executeQuery(countSql)
        ) {
            if (results.next()
                    && results.getInt("total") > 0) {
                return;
            }
        }

        String insertSql = """
                INSERT INTO discount_codes (
                    code,
                    percentage,
                    active
                )
                VALUES (?, ?, 1)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(insertSql)) {

            statement.setString(1, "WELCOME5");
            statement.setDouble(2, 0.05);
            statement.addBatch();

            statement.setString(1, "SAVE10");
            statement.setDouble(2, 0.10);
            statement.addBatch();

            statement.executeBatch();
        }
    }
}
