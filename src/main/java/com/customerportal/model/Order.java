package com.customerportal.model;

import java.time.LocalDateTime;

public class Order {

    private int orderId;
    private int customerId;
    private LocalDateTime orderDate;
    private double total;
    private String status;

    public Order(
            int orderId,
            int customerId,
            LocalDateTime orderDate,
            double total, 
            String status
    ) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.total = total;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public double gettotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }
    
}
