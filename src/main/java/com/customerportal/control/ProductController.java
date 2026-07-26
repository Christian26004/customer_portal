package com.customerportal.control;

import com.customerportal.Launcher;
import com.customerportal.model.CartItem;
import com.customerportal.model.Customer;
import com.customerportal.model.Product;
import com.customerportal.model.ProductManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProductController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterBox;

    @FXML
    private FlowPane productGrid;

    @FXML
    private Label cartSummaryLabel;

    @FXML
    private Label messageLabel;

    private final List<Product> products = ProductManager.getAllProducts();
    private Customer customer;

    @FXML
    private void initialize() {
        filterBox.setItems(FXCollections.observableArrayList(
                "All Products", "Price: Low to High", "Price: High to Low", "Name A-Z"));
        filterBox.getSelectionModel().selectFirst();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> refreshProducts());
        filterBox.valueProperty().addListener((observable, oldValue, newValue) -> refreshProducts());
        refreshProducts();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        updateCartSummary();
    }

    private void refreshProducts() {
        if (productGrid == null) {
            return;
        }

        String search = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        List<Product> filteredProducts = new ArrayList<>(products.stream()
                .filter(product -> search.isBlank()
                        || product.getName().toLowerCase().contains(search)
                        || product.getDescription().toLowerCase().contains(search))
                .toList());

        String filter = filterBox.getValue();
        if ("Price: Low to High".equals(filter)) {
            filteredProducts.sort(Comparator.comparingDouble(Product::getPrice));
        } else if ("Price: High to Low".equals(filter)) {
            filteredProducts.sort(Comparator.comparingDouble(Product::getPrice).reversed());
        } else if ("Name A-Z".equals(filter)) {
            filteredProducts.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        }

        productGrid.getChildren().setAll(filteredProducts.stream()
                .map(this::createProductCard)
                .toList());
    }

    private VBox createProductCard(Product product) {
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label(product.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(210);

        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
        priceLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Button addButton = new Button("Add to Cart");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(event -> addToCart(product));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Region bottomSpacer = new Region();
        HBox bottomRow = new HBox(10, priceLabel, bottomSpacer, addButton);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        VBox card = new VBox(8, nameLabel, descriptionLabel, spacer, bottomRow);
        card.setPrefWidth(230);
        card.setMinHeight(190);
        card.setPrefHeight(190);
        card.setMaxHeight(190);
        card.setStyle("-fx-border-color: #d0d0d0; -fx-border-radius: 6; "
                + "-fx-background-radius: 6; -fx-padding: 14; -fx-background-color: white;");
        return card;
    }

    private void addToCart(Product product) {
        if (customer == null) {
            messageLabel.setText("Please log in before adding products to your cart.");
            return;
        }

        customer.getShoppingCart().addItem(product, 1);
        updateCartSummary();
        messageLabel.setText(product.getName() + " added to your cart.");
    }

    private void updateCartSummary() {
        if (cartSummaryLabel == null || customer == null) {
            return;
        }

        int itemCount = customer.getShoppingCart().getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        cartSummaryLabel.setText(String.format("Cart: %d item%s | Subtotal: $%.2f",
                itemCount, itemCount == 1 ? "" : "s", customer.getShoppingCart().getSubtotal()));
    }

    @FXML
    private void handleProfile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource("/com/customerportal/view/profile-view.fxml"));
        Scene scene = Launcher.createScene(loader);
        ProfileController controller = loader.getController();
        controller.setCustomer(customer);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Launcher.showScene(stage, scene, "Customer Profile");
    }

    @FXML
    private void handleProceedToCheckout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource("/com/customerportal/view/cart-view.fxml"));
        Scene scene = Launcher.createScene(loader);
        CartController controller = loader.getController();
        controller.setCustomer(customer);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Launcher.showScene(stage, scene, "Shopping Cart");
    }
}
