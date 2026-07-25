package com.customerportal.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class OrderManager {

    private static final double TAX_RATE = 0.0825;

    private OrderManager() {
    }

    /**
     * Looks up a discount code and returns its percentage off (e.g. 0.10 for 10%).
     * Returns 0.0 if the code is blank or not found/active.
     */
    public static double validateDiscountCode(String code) {
        if (code == null || code.isBlank()) {
            return 0.0;
        }

        String sql = "SELECT percentage FROM discount_codes WHERE UPPER(code) = UPPER(?) AND active = 1";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code.trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("percentage");
            }

        } catch (SQLException e) {
            System.err.println("Error validating discount code: " + e.getMessage());
        }

        return 0.0;
    }

    /**
     * Completes checkout: computes tax/discount/delivery totals, inserts one row
     * into `orders` and one row per cart line into `order_items` (in a single
     * transaction), then clears the cart. Returns the created Order, or null
     * if checkout failed (e.g. empty cart or a database error).
     */
    public static Order checkout(int customerId, ShoppingCart cart, DeliveryOption deliveryOption,
                                  String discountCode) {
        if (cart == null || cart.isEmpty() || deliveryOption == null) {
            return null;
        }

        double subtotal = cart.getSubtotal();
        double discountPercentage = validateDiscountCode(discountCode);
        double discountAmount = subtotal * discountPercentage;
        double taxableAmount = subtotal - discountAmount;
        double tax = taxableAmount * TAX_RATE;
        double deliveryFee = deliveryOption.getFee();
        double total = taxableAmount + tax + deliveryFee;

        String insertOrderSql = """
                INSERT INTO orders
                    (customer_id, order_date, subtotal, tax, discount_amount, delivery_option, delivery_fee, total, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String insertItemSql = """
                INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
                VALUES (?, ?, ?, ?, ?)
                """;

        Connection conn = null;
        LocalDateTime orderDateTime = LocalDateTime.now();
        String orderDateFormatted = orderDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            int orderId;

            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, customerId);
                pstmt.setString(2, orderDateFormatted);
                pstmt.setDouble(3, subtotal);
                pstmt.setDouble(4, tax);
                pstmt.setDouble(5, discountAmount);
                pstmt.setString(6, deliveryOption.name());
                pstmt.setDouble(7, deliveryFee);
                pstmt.setDouble(8, total);
                pstmt.setString(9, "PLACED");
                pstmt.executeUpdate();

                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        orderId = keys.getInt(1);
                    } else {
                        throw new SQLException("Could not retrieve generated order id.");
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertItemSql)) {
                for (CartItem item : cart.getItems()) {
                    pstmt.setInt(1, orderId);
                    pstmt.setInt(2, item.getProduct().getProductId());
                    pstmt.setString(3, item.getProduct().getName());
                    pstmt.setInt(4, item.getQuantity());
                    pstmt.setDouble(5, item.getProduct().getPrice());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();

            Order order = new Order(orderId, customerId, orderDateTime, total, "PLACED");

            cart.clear();
            return order;

        } catch (SQLException e) {
            System.err.println("Checkout failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            return null;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

}
