package com.customerportal.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class OrderDAO {

    private OrderDAO() {
    }

    public static List<Order> getOrdersForCustomer(int customerId) {
        List<Order> orders = new ArrayList<>();

        String sql = """
                SELECT order_id,
                       customer_id,
                       order_date,
                       total,
                       status
                FROM orders
                WHERE customer_id = ?
                ORDER BY order_date DESC
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, customerId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Order order = new Order(
                            results.getInt("order_id"),
                            results.getInt("customer_id"),
                            LocalDateTime.parse(
                                    results.getString("order_date")
                            ),
                            results.getDouble("total"),
                            results.getString("status")
                    );

                    orders.add(order);
                }
            }

        } catch (SQLException exception) {
            System.err.println(
                    "Could not load order history: "
                            + exception.getMessage()
            );
        }

        return orders;
    }
}