package com.customerportal.control;

import com.customerportal.Launcher;
import com.customerportal.model.Address;
import com.customerportal.model.CartItem;
import com.customerportal.model.Customer;
import com.customerportal.model.DeliveryOption;
import com.customerportal.model.Order;
import com.customerportal.model.OrderManager;
import com.customerportal.model.ShoppingCart;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CartController {

    private static final double TAX_RATE = 0.0825;

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> productColumn;

    @FXML
    private TableColumn<CartItem, String> priceColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, String> subtotalColumn;

    @FXML
    private TableColumn<CartItem, Void> removeColumn;

    @FXML
    private TextField discountCodeField;

    @FXML
    private ComboBox<DeliveryOption> deliveryComboBox;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label discountLabel;

    @FXML
    private Label taxLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Label messageLabel;

    private ShoppingCart cart;
    private Customer customer;
    private double appliedDiscountPercentage = 0.0;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.cart = customer.getShoppingCart();
        if (cartTable != null) {
            refreshCartTable();
        }
    }

    @FXML
    private void initialize() {
        deliveryComboBox.setItems(FXCollections.observableArrayList(DeliveryOption.values()));
        deliveryComboBox.getSelectionModel().selectFirst();
        deliveryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> recalculateTotals());

        productColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getProduct().getName()));

        priceColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("$%.2f", data.getValue().getProduct().getPrice())));

        subtotalColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("$%.2f", data.getValue().getSubtotal())));

        quantityColumn.setCellFactory(column -> new QuantityCell());
        removeColumn.setCellFactory(column -> new RemoveCell());

        cartTable.setItems(FXCollections.observableArrayList());

    }

    @FXML
    private void handleApplyDiscount(ActionEvent event) {
        String code = discountCodeField.getText();
        double percentage = OrderManager.validateDiscountCode(code);

        if (code != null && !code.isBlank() && percentage <= 0.0) {
            messageLabel.setText("That discount code is not valid.");
            appliedDiscountPercentage = 0.0;
        } else {
            appliedDiscountPercentage = percentage;
            messageLabel.setText(percentage > 0.0 ? "Discount applied." : "");
        }

        recalculateTotals();
    }

    @FXML
    private void handlePlaceOrder(ActionEvent event) {
        if (cart.isEmpty()) {
            messageLabel.setText("Your cart is empty.");
            return;
        }

        if (!hasCompleteAddress()) {
            messageLabel.setText("Please add a complete address to your profile before placing an order.");
            return;
        }

        DeliveryOption deliveryOption = deliveryComboBox.getValue();
        String discountCode = discountCodeField.getText();

        Order order = OrderManager.checkout(customer.getCustomerId(), cart, deliveryOption, discountCode);

        if (order == null) {
            messageLabel.setText("Something went wrong placing your order. Please try again.");
            return;
        }

        appliedDiscountPercentage = 0.0;
        discountCodeField.clear();
        refreshCartTable();

        messageLabel.setText("Order #" + order.getOrderId() + " placed successfully! Total: "
                + String.format("$%.2f", order.getTotal()));
    }

    private boolean hasCompleteAddress() {
        if (customer == null) {
            return false;
        }

        Address address = customer.getAddress();
        return address != null
                && isNotBlank(address.getStreet())
                && isNotBlank(address.getCity())
                && isNotBlank(address.getState())
                && isNotBlank(address.getZipCode());
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    @FXML
    private void handleBackToProducts(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource("/com/customerportal/view/product-page.fxml")
        );

        Scene scene = Launcher.createScene(loader);

        ProductController controller = loader.getController();
        controller.setCustomer(customer);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Launcher.showScene(stage, scene, "Products");
    }

    private void refreshCartTable() {
        if (cart == null) {
            return;
        }
        cartTable.getItems().setAll(cart.getItems());
        recalculateTotals();
    }

    private void recalculateTotals() {
        if (cart == null) {
            return;
        }
        double subtotal = cart.getSubtotal();
        double discountAmount = subtotal * appliedDiscountPercentage;
        double taxableAmount = subtotal - discountAmount;
        double tax = taxableAmount * TAX_RATE;

        DeliveryOption deliveryOption = deliveryComboBox.getValue();
        double deliveryFee = deliveryOption == null ? 0.0 : deliveryOption.getFee();

        double total = taxableAmount + tax + deliveryFee;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        discountLabel.setText(String.format("-$%.2f", discountAmount));
        taxLabel.setText(String.format("$%.2f", tax));
        deliveryFeeLabel.setText(String.format("$%.2f", deliveryFee));
        totalLabel.setText(String.format("$%.2f", total));
    }

    /** Allows quantity changes for items already in the cart. */
    private class QuantityCell extends TableCell<CartItem, Integer> {

        private final Button decreaseButton = new Button("-");
        private final Button increaseButton = new Button("+");
        private final Label quantityLabel = new Label();
        private final HBox container = new HBox(6, decreaseButton, quantityLabel, increaseButton);

        QuantityCell() {
            decreaseButton.setOnAction(event -> updateQuantity(-1));
            increaseButton.setOnAction(event -> updateQuantity(1));
        }

        private void updateQuantity(int change) {
            CartItem item = getTableView().getItems().get(getIndex());
            cart.updateQuantity(item.getProduct().getProductId(), item.getQuantity() + change);
            refreshCartTable();
        }

        @Override
        protected void updateItem(Integer value, boolean empty) {
            super.updateItem(value, empty);

            if (empty || getIndex() >= getTableView().getItems().size()) {
                setGraphic(null);
                return;
            }

            CartItem item = getTableView().getItems().get(getIndex());
            quantityLabel.setText(String.valueOf(item.getQuantity()));
            decreaseButton.setDisable(item.getQuantity() <= 1);
            setGraphic(container);
        }
    }

    /** Renders a "Remove" button for each cart row. */
    private class RemoveCell extends TableCell<CartItem, Void> {

        private final Button removeButton = new Button("Remove");

        RemoveCell() {
            removeButton.setOnAction(e -> {
                CartItem item = getTableView().getItems().get(getIndex());
                cart.removeItem(item.getProduct().getProductId());
                refreshCartTable();
            });
        }

        @Override
        protected void updateItem(Void value, boolean empty) {
            super.updateItem(value, empty);
            setGraphic(empty ? null : removeButton);
        }
    }
}
