package com.customerportal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the customer's in-progress cart. This is kept in memory for the
 * current session; it becomes persistent rows in `orders` / `order_items`
 * only once checkout completes (see OrderManager.checkout).
 */
public class ShoppingCart {

    private final List<CartItem> items = new ArrayList<>();

    public void addItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return;
        }

        Optional<CartItem> existing = findItem(product.getProductId());

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public void updateQuantity(int productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(productId);
            return;
        }
        findItem(productId).ifPresent(item -> item.setQuantity(newQuantity));
    }

    public void removeItem(int productId) {
        items.removeIf(item -> item.getProduct().getProductId() == productId);
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    private Optional<CartItem> findItem(int productId) {
        return items.stream()
                .filter(item -> item.getProduct().getProductId() == productId)
                .findFirst();
    }
}
