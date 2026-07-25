package com.customerportal.model;

public enum DeliveryOption {

    STANDARD("Standard Delivery (3-5 business days)", 5.99),
    EXPRESS("Express Delivery (1-2 business days)", 12.99),
    PICKUP("Store Pickup (free)", 0.00);

    private final String label;
    private final double fee;

    DeliveryOption(String label, double fee) {
        this.label = label;
        this.fee = fee;
    }

    public double getFee() {
        return fee;
    }

    @Override
    public String toString() {
        return label;
    }
}
