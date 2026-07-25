package com.customerportal.model;

public class Product {

    private int productId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private int stockQuantity;

    public Product(int productId, String name, String description, double price,
                    String imageUrl, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    // Used so Product objects display nicely in ComboBoxes, etc.
    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price);
    }
}
